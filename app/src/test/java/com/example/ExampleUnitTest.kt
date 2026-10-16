package com.example

import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ProfileEntity
import com.example.util.GamificationEngine
import com.example.util.WeeklyReportCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun gamification_levelCalculation() {
        assertEquals(1, GamificationEngine.calculateLevel(0))
        assertEquals(1, GamificationEngine.calculateLevel(30))
        // 40 XP -> sqrt(1) + 1 = Level 2
        assertEquals(2, GamificationEngine.calculateLevel(40))
        // 160 XP -> sqrt(4) + 1 = Level 3
        assertEquals(3, GamificationEngine.calculateLevel(160))
    }

    @Test
    fun gamification_heroTitle() {
        val kidTitle = GamificationEngine.getHeroTitle(level = 1, role = "KID")
        assertTrue(kidTitle.contains("Novice"))

        val parentTitle = GamificationEngine.getHeroTitle(level = 99, role = "PARENT")
        assertTrue(parentTitle.contains("Guild Master"))
    }

    @Test
    fun weeklyReport_aggregatesCorrectly() {
        val now = System.currentTimeMillis()
        val completions = listOf(
            ChoreCompletionEntity(
                id = 1,
                choreId = 1,
                kidId = 1,
                choreTitle = "Wash Dishes",
                points = 25,
                completedAt = now - 10000,
                status = "APPROVED"
            ),
            ChoreCompletionEntity(
                id = 2,
                choreId = 2,
                kidId = 1,
                choreTitle = "Make Bed",
                points = 15,
                completedAt = now - 20000,
                status = "APPROVED"
            )
        )
        val profiles = listOf(
            ProfileEntity(
                id = 1,
                name = "Leo",
                role = "KID",
                avatarEmoji = "🛡️",
                heroTitle = "Knight",
                pin = "1111"
            )
        )

        val report = WeeklyReportCalculator.calculate(completions, profiles)
        assertEquals(2, report.totalApprovedChores)
        assertEquals(40, report.totalGemsAwarded)
        assertTrue(report.topKidName.contains("Leo"))
    }

    @Test
    fun questIconMatcher_matchesKeywords() {
        // Test dish matching
        val dishMatches = com.example.util.QuestIconMatcher.getMatchingIconInfos("Empty the dishwasher")
        assertTrue(dishMatches.any { it.icon == "🍽️" })

        // Test dog matching
        val dogMatches = com.example.util.QuestIconMatcher.getMatchingIconInfos("Walk Charlie in the park")
        assertTrue(dogMatches.any { it.icon == "🐕" })

        // Test bed matching
        val bedMatches = com.example.util.QuestIconMatcher.getMatchingIconInfos("Make your bed before school")
        assertTrue(bedMatches.any { it.icon == "🛏️" })

        // Test homework matching
        val hwMatches = com.example.util.QuestIconMatcher.getMatchingIconInfos("Math homework and read 20 mins")
        assertTrue(hwMatches.any { it.icon == "📚" })

        // Test laundry matching
        val laundryMatches = com.example.util.QuestIconMatcher.getMatchingIconInfos("Fold clean clothes and laundry")
        assertTrue(laundryMatches.any { it.icon == "🧺" || it.icon == "👕" })
    }

    @Test
    fun questIconMatcher_scoredSorting() {
        val scored = com.example.util.QuestIconMatcher.getScoredMatches("Wash dishes and clear the kitchen table")
        assertTrue(scored.isNotEmpty())
        assertEquals("🍽️", scored.first().iconInfo.icon)
    }

    @Test
    fun gamification_badgesUnlockCorrectly() {
        // Novice with 0 stats
        val initialBadges = GamificationEngine.getBadges(
            totalXp = 0,
            currentPoints = 0,
            completedCount = 0,
            redemptionsCount = 0
        )
        assertTrue(initialBadges.none { it.isUnlocked })

        // Adventurer with 1 completion and 120 gems
        val earnedBadges = GamificationEngine.getBadges(
            totalXp = 160,
            currentPoints = 120,
            completedCount = 4,
            redemptionsCount = 1
        )
        val unlockedIds = earnedBadges.filter { it.isUnlocked }.map { it.id }
        assertTrue(unlockedIds.contains("first_quest"))
        assertTrue(unlockedIds.contains("clean_sweep"))
        assertTrue(unlockedIds.contains("gem_hoarder"))
        assertTrue(unlockedIds.contains("level_3"))
        assertTrue(unlockedIds.contains("shopper"))
    }

    @Test
    fun gamification_levelProgressFraction() {
        // Level 1: 0 to 40 XP
        assertEquals(0f, GamificationEngine.levelProgressFraction(0, 1), 0.01f)
        assertEquals(0.5f, GamificationEngine.levelProgressFraction(20, 1), 0.01f)
        assertEquals(1f, GamificationEngine.levelProgressFraction(40, 1), 0.01f)
    }

    @Test
    fun weeklyReport_handlesEmptyData() {
        val emptyReport = WeeklyReportCalculator.calculate(emptyList(), emptyList())
        assertEquals(0, emptyReport.totalApprovedChores)
        assertEquals(0, emptyReport.totalGemsAwarded)
        assertEquals(7, emptyReport.dailyActivities.size)
        assertEquals("None yet", emptyReport.topKidName)
    }

    @Test
    fun questIconMatcher_popularIconsFallback() {
        val popular = com.example.util.QuestIconMatcher.POPULAR_QUEST_ICONS
        assertTrue(popular.isNotEmpty())
        assertTrue(popular.any { it.icon == "🛏️" })
        assertTrue(popular.any { it.icon == "🍽️" })
        assertTrue(popular.any { it.icon == "🧹" })
    }
}

