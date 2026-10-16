package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ChoreEntity
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import com.example.util.GamificationEngine
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GemQuestRepository(private val context: Context, private val db: AppDatabase) {

    val allProfiles: Flow<List<ProfileEntity>> = db.profileDao().getAllProfiles()
    val allChores: Flow<List<ChoreEntity>> = db.choreDao().getAllChores()
    val allCompletions: Flow<List<ChoreCompletionEntity>> = db.choreCompletionDao().getAllCompletions()
    val pendingCompletions: Flow<List<ChoreCompletionEntity>> = db.choreCompletionDao().getPendingCompletions()
    val allRewards: Flow<List<RewardEntity>> = db.rewardDao().getAllRewards()
    val allRedemptions: Flow<List<RewardRedemptionEntity>> = db.rewardRedemptionDao().getAllRedemptions()
    val pendingRedemptions: Flow<List<RewardRedemptionEntity>> = db.rewardRedemptionDao().getPendingRedemptions()
    val allTransactions: Flow<List<PointTransactionEntity>> = db.transactionDao().getAllTransactions()

    fun getProfileByIdFlow(id: Int): Flow<ProfileEntity?> = db.profileDao().getProfileByIdFlow(id)
    fun getCompletionsForKid(kidId: Int): Flow<List<ChoreCompletionEntity>> =
        db.choreCompletionDao().getCompletionsForKid(kidId)
    fun getTransactionsForKid(kidId: Int): Flow<List<PointTransactionEntity>> =
        db.transactionDao().getTransactionsForKid(kidId)

    suspend fun markChoreDone(kidId: Int, choreId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val chore = db.choreDao().getChoreById(choreId) ?: return@withContext Result.failure(
            IllegalArgumentException("Chore not found")
        )
        val kid = db.profileDao().getProfileById(kidId) ?: return@withContext Result.failure(
            IllegalArgumentException("Kid profile not found")
        )

        val completion = ChoreCompletionEntity(
            choreId = chore.id,
            kidId = kid.id,
            choreTitle = chore.title,
            points = chore.points,
            completedAt = System.currentTimeMillis(),
            status = "PENDING"
        )
        db.choreCompletionDao().insert(completion)

        // Trigger push notification to inform parent
        NotificationHelper.notifyQuestTurnedIn(
            context = context,
            kidName = kid.name,
            choreTitle = chore.title,
            gemPoints = chore.points
        )

        Result.success(Unit)
    }

    suspend fun approveChore(completionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val completion = db.choreCompletionDao().getCompletionById(completionId)
            ?: return@withContext Result.failure(IllegalArgumentException("Completion not found"))

        if (completion.status == "APPROVED") {
            return@withContext Result.success(Unit)
        }

        val kid = db.profileDao().getProfileById(completion.kidId)
            ?: return@withContext Result.failure(IllegalArgumentException("Kid not found"))

        val newPoints = kid.currentPoints + completion.points
        val newXp = kid.totalXp + completion.points
        val newLevel = GamificationEngine.calculateLevel(newXp)

        db.profileDao().updatePointsAndXp(
            id = kid.id,
            points = newPoints,
            xp = newXp,
            level = newLevel
        )

        db.choreCompletionDao().updateStatus(
            id = completionId,
            status = "APPROVED",
            reviewedAt = System.currentTimeMillis()
        )

        db.transactionDao().insert(
            PointTransactionEntity(
                kidId = kid.id,
                amount = completion.points,
                type = "EARNED",
                description = "Quest Approved: ${completion.choreTitle}",
                timestamp = System.currentTimeMillis()
            )
        )

        NotificationHelper.notifyQuestApproved(
            context = context,
            kidName = kid.name,
            choreTitle = completion.choreTitle,
            gemPoints = completion.points
        )

        Result.success(Unit)
    }

    suspend fun rejectChore(completionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        db.choreCompletionDao().updateStatus(
            id = completionId,
            status = "REJECTED",
            reviewedAt = System.currentTimeMillis()
        )
        Result.success(Unit)
    }

    suspend fun requestRedemption(kidId: Int, rewardId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val reward = db.rewardDao().getRewardById(rewardId)
            ?: return@withContext Result.failure(IllegalArgumentException("Reward not found"))
        val kid = db.profileDao().getProfileById(kidId)
            ?: return@withContext Result.failure(IllegalArgumentException("Kid not found"))

        if (kid.currentPoints < reward.gemCost) {
            return@withContext Result.failure(IllegalStateException("Not enough gems! Needs ${reward.gemCost}, has ${kid.currentPoints}."))
        }

        val redemption = RewardRedemptionEntity(
            rewardId = reward.id,
            kidId = kid.id,
            rewardTitle = reward.title,
            gemCost = reward.gemCost,
            status = "PENDING",
            requestedAt = System.currentTimeMillis()
        )
        db.rewardRedemptionDao().insert(redemption)
        Result.success(Unit)
    }

    suspend fun approveRedemption(redemptionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val redemption = db.rewardRedemptionDao().getRedemptionById(redemptionId)
            ?: return@withContext Result.failure(IllegalArgumentException("Redemption not found"))

        val kid = db.profileDao().getProfileById(redemption.kidId)
            ?: return@withContext Result.failure(IllegalArgumentException("Kid not found"))

        if (kid.currentPoints < redemption.gemCost) {
            return@withContext Result.failure(IllegalStateException("Kid does not have enough gems left!"))
        }

        val newPoints = kid.currentPoints - redemption.gemCost
        db.profileDao().updatePointsAndXp(
            id = kid.id,
            points = newPoints,
            xp = kid.totalXp,
            level = kid.level
        )

        db.rewardRedemptionDao().updateStatus(
            id = redemptionId,
            status = "APPROVED",
            reviewedAt = System.currentTimeMillis()
        )

        db.transactionDao().insert(
            PointTransactionEntity(
                kidId = kid.id,
                amount = -redemption.gemCost,
                type = "SPENT",
                description = "Redeemed: ${redemption.rewardTitle}",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(Unit)
    }

    suspend fun rejectRedemption(redemptionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        db.rewardRedemptionDao().updateStatus(
            id = redemptionId,
            status = "REJECTED",
            reviewedAt = System.currentTimeMillis()
        )
        Result.success(Unit)
    }

    suspend fun addChore(
        title: String,
        description: String,
        points: Int,
        iconEmoji: String,
        assignedKidId: Int?,
        frequency: String,
        category: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        db.choreDao().insert(
            ChoreEntity(
                title = title.trim(),
                description = description.trim(),
                points = points,
                iconEmoji = iconEmoji,
                assignedKidId = assignedKidId,
                frequency = frequency,
                category = category
            )
        )
        Result.success(Unit)
    }

    suspend fun updateChore(
        choreId: Int,
        title: String,
        description: String,
        points: Int,
        iconEmoji: String,
        assignedKidId: Int?,
        frequency: String,
        category: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = db.choreDao().getChoreById(choreId) ?: return@withContext Result.failure(
            IllegalArgumentException("Chore not found")
        )
        db.choreDao().update(
            existing.copy(
                title = title.trim(),
                description = description.trim(),
                points = points,
                iconEmoji = iconEmoji,
                assignedKidId = assignedKidId,
                frequency = frequency,
                category = category
            )
        )
        Result.success(Unit)
    }

    suspend fun deleteChore(choreId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        db.choreDao().deleteById(choreId)
        Result.success(Unit)
    }

    suspend fun addReward(
        title: String,
        description: String,
        gemCost: Int,
        iconEmoji: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        db.rewardDao().insert(
            RewardEntity(
                title = title.trim(),
                description = description.trim(),
                gemCost = gemCost,
                iconEmoji = iconEmoji
            )
        )
        Result.success(Unit)
    }

    suspend fun deleteReward(rewardId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        db.rewardDao().deleteById(rewardId)
        Result.success(Unit)
    }

    suspend fun updateProfilePin(profileId: Int, newPin: String): Result<Unit> = withContext(Dispatchers.IO) {
        db.profileDao().updatePin(profileId, newPin)
        Result.success(Unit)
    }

    suspend fun updateAvatar(profileId: Int, newAvatarEmoji: String): Result<Unit> = withContext(Dispatchers.IO) {
        db.profileDao().updateAvatar(profileId, newAvatarEmoji)
        Result.success(Unit)
    }

    suspend fun deleteProfile(profileId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        db.profileDao().deleteProfileById(profileId)
        Result.success(Unit)
    }

    suspend fun addKidProfile(
        name: String,
        avatarEmoji: String,
        heroTitle: String,
        pin: String,
        colorHex: Long
    ): Result<Unit> = withContext(Dispatchers.IO) {
        db.profileDao().insert(
            ProfileEntity(
                name = name.trim(),
                role = "KID",
                avatarEmoji = avatarEmoji,
                heroTitle = heroTitle.trim(),
                pin = pin,
                currentPoints = 0,
                totalXp = 0,
                level = 1,
                colorHex = colorHex
            )
        )
        Result.success(Unit)
    }
}
