package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val role: String, // "KID" or "PARENT"
    val avatarEmoji: String, // e.g., "🛡️", "🧙‍♀️", "👑", "🏹"
    val heroTitle: String, // e.g. "Knight of Cleanliness", "Sorceress of Tidiness", "Guild Master"
    val pin: String, // 4-digit PIN
    val currentPoints: Int = 0, // Redeemable Gems
    val totalXp: Int = 0, // Lifetime XP
    val level: Int = 1,
    val colorHex: Long = 0xFF4361EE
)

@Entity(tableName = "chores")
data class ChoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val points: Int,
    val iconEmoji: String = "✨",
    val assignedKidId: Int? = null, // null = available to any kid
    val frequency: String = "DAILY", // "DAILY", "WEEKLY", "ONE_OFF"
    val category: String = "CLEANING", // "CLEANING", "HOMEWORK", "PETS", "BEDROOM", "KITCHEN"
    val isArchived: Boolean = false
)

@Entity(tableName = "chore_completions")
data class ChoreCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val choreId: Int,
    val kidId: Int,
    val choreTitle: String,
    val points: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val reviewedAt: Long? = null,
    val note: String = ""
)

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val gemCost: Int,
    val iconEmoji: String = "🎁",
    val isAvailable: Boolean = true
)

@Entity(tableName = "reward_redemptions")
data class RewardRedemptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rewardId: Int,
    val kidId: Int,
    val rewardTitle: String,
    val gemCost: Int,
    val status: String = "PENDING", // "PENDING", "APPROVED", "FULFILLED", "REJECTED"
    val requestedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "point_transactions")
data class PointTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kidId: Int,
    val amount: Int, // positive for earn, negative for spend
    val type: String, // "EARNED", "SPENT", "BONUS"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
