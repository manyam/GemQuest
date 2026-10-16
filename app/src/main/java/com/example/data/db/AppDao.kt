package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ChoreEntity
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY role DESC, id ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfileByIdFlow(id: Int): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<ProfileEntity>)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("UPDATE profiles SET currentPoints = :points, totalXp = :xp, level = :level WHERE id = :id")
    suspend fun updatePointsAndXp(id: Int, points: Int, xp: Int, level: Int)

    @Query("UPDATE profiles SET pin = :newPin WHERE id = :id")
    suspend fun updatePin(id: Int, newPin: String)

    @Query("UPDATE profiles SET avatarEmoji = :avatarEmoji WHERE id = :id")
    suspend fun updateAvatar(id: Int, avatarEmoji: String)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getCount(): Int
}

@Dao
interface ChoreDao {
    @Query("SELECT * FROM chores WHERE isArchived = 0 ORDER BY id DESC")
    fun getAllChores(): Flow<List<ChoreEntity>>

    @Query("SELECT * FROM chores WHERE id = :id")
    suspend fun getChoreById(id: Int): ChoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chore: ChoreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chores: List<ChoreEntity>)

    @Update
    suspend fun update(chore: ChoreEntity)

    @Delete
    suspend fun delete(chore: ChoreEntity)

    @Query("DELETE FROM chores WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM chores")
    suspend fun getCount(): Int
}

@Dao
interface ChoreCompletionDao {
    @Query("SELECT * FROM chore_completions ORDER BY completedAt DESC")
    fun getAllCompletions(): Flow<List<ChoreCompletionEntity>>

    @Query("SELECT * FROM chore_completions WHERE kidId = :kidId ORDER BY completedAt DESC")
    fun getCompletionsForKid(kidId: Int): Flow<List<ChoreCompletionEntity>>

    @Query("SELECT * FROM chore_completions WHERE status = 'PENDING' ORDER BY completedAt ASC")
    fun getPendingCompletions(): Flow<List<ChoreCompletionEntity>>

    @Query("SELECT * FROM chore_completions WHERE completedAt >= :sinceTimestamp ORDER BY completedAt DESC")
    fun getCompletionsSince(sinceTimestamp: Long): Flow<List<ChoreCompletionEntity>>

    @Query("SELECT * FROM chore_completions WHERE id = :id")
    suspend fun getCompletionById(id: Int): ChoreCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: ChoreCompletionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(completions: List<ChoreCompletionEntity>)

    @Update
    suspend fun update(completion: ChoreCompletionEntity)

    @Query("UPDATE chore_completions SET status = :status, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String, reviewedAt: Long)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE isAvailable = 1 ORDER BY gemCost ASC")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards WHERE id = :id")
    suspend fun getRewardById(id: Int): RewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reward: RewardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<RewardEntity>)

    @Update
    suspend fun update(reward: RewardEntity)

    @Delete
    suspend fun delete(reward: RewardEntity)

    @Query("DELETE FROM rewards WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun getCount(): Int
}

@Dao
interface RewardRedemptionDao {
    @Query("SELECT * FROM reward_redemptions ORDER BY requestedAt DESC")
    fun getAllRedemptions(): Flow<List<RewardRedemptionEntity>>

    @Query("SELECT * FROM reward_redemptions WHERE status = 'PENDING' ORDER BY requestedAt ASC")
    fun getPendingRedemptions(): Flow<List<RewardRedemptionEntity>>

    @Query("SELECT * FROM reward_redemptions WHERE kidId = :kidId ORDER BY requestedAt DESC")
    fun getRedemptionsForKid(kidId: Int): Flow<List<RewardRedemptionEntity>>

    @Query("SELECT * FROM reward_redemptions WHERE id = :id")
    suspend fun getRedemptionById(id: Int): RewardRedemptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(redemption: RewardRedemptionEntity): Long

    @Update
    suspend fun update(redemption: RewardRedemptionEntity)

    @Query("UPDATE reward_redemptions SET status = :status, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String, reviewedAt: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM point_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PointTransactionEntity>>

    @Query("SELECT * FROM point_transactions WHERE kidId = :kidId ORDER BY timestamp DESC")
    fun getTransactionsForKid(kidId: Int): Flow<List<PointTransactionEntity>>

    @Query("SELECT * FROM point_transactions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getTransactionsSince(sinceTimestamp: Long): Flow<List<PointTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: PointTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<PointTransactionEntity>)
}
