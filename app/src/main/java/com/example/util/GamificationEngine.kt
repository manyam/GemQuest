package com.example.util

import kotlin.math.sqrt

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean
)

object GamificationEngine {
    fun calculateLevel(totalXp: Int): Int {
        if (totalXp <= 0) return 1
        return (sqrt(totalXp.toDouble() / 40.0) + 1).toInt().coerceAtLeast(1)
    }

    fun currentLevelFloorXp(level: Int): Int {
        if (level <= 1) return 0
        val prev = level - 1
        return prev * prev * 40
    }

    fun nextLevelTargetXp(level: Int): Int {
        return level * level * 40
    }

    fun levelProgressFraction(totalXp: Int, currentLevel: Int): Float {
        val floor = currentLevelFloorXp(currentLevel)
        val target = nextLevelTargetXp(currentLevel)
        val span = target - floor
        if (span <= 0) return 1f
        val currentInLevel = (totalXp - floor).coerceAtLeast(0)
        return (currentInLevel.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    }

    fun getHeroTitle(level: Int, role: String): String {
        if (role == "PARENT") return "👑 Grand Guild Master"
        return when (level) {
            1 -> "🌱 Novice Recruit"
            2 -> "⚔️ Quest Apprentice"
            3 -> "🛡️ Keeper of Order"
            4 -> "✨ Champion of Tidiness"
            5 -> "🌟 Knight of Sparkle"
            6 -> "🐉 Dragon Slayer of Dust"
            7 -> "🏆 Mythic Paladin"
            else -> "👑 Legendary Master of Quests"
        }
    }

    fun getBadges(totalXp: Int, currentPoints: Int, completedCount: Int, redemptionsCount: Int): List<Badge> {
        return listOf(
            Badge(
                id = "first_quest",
                title = "First Step",
                description = "Complete your very first household quest.",
                iconEmoji = "🎯",
                isUnlocked = completedCount >= 1
            ),
            Badge(
                id = "clean_sweep",
                title = "Sweep Champion",
                description = "Complete 3 or more approved chores.",
                iconEmoji = "🧹",
                isUnlocked = completedCount >= 3
            ),
            Badge(
                id = "gem_hoarder",
                title = "Gem Hoarder",
                description = "Reach a balance of 100 or more shiny gems.",
                iconEmoji = "💎",
                isUnlocked = currentPoints >= 100
            ),
            Badge(
                id = "level_3",
                title = "Order Knight",
                description = "Level up to Level 3 or higher.",
                iconEmoji = "🛡️",
                isUnlocked = calculateLevel(totalXp) >= 3
            ),
            Badge(
                id = "shopper",
                title = "Treasure Claimer",
                description = "Redeem at least 1 custom reward in the Bazaar.",
                iconEmoji = "🎁",
                isUnlocked = redemptionsCount >= 1
            ),
            Badge(
                id = "quest_master",
                title = "Master of Deeds",
                description = "Earn over 300 total lifetime XP.",
                iconEmoji = "👑",
                isUnlocked = totalXp >= 300
            )
        )
    }
}
