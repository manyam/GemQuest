package com.example.util

import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ProfileEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyActivity(
    val dayLabel: String, // e.g. "Mon", "Tue"
    val count: Int,
    val gems: Int,
    val isToday: Boolean
)

data class WeeklyReport(
    val totalApprovedChores: Int,
    val totalGemsAwarded: Int,
    val topKidName: String,
    val topKidGems: Int,
    val completionRatePercent: Int,
    val dailyActivities: List<DailyActivity>,
    val categoryCounts: Map<String, Int>
)

object WeeklyReportCalculator {
    fun calculate(
        completions: List<ChoreCompletionEntity>,
        profiles: List<ProfileEntity>,
        daysLookback: Int = 7
    ): WeeklyReport {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val startTime = now - (daysLookback * 86_400_000L)

        val approvedThisWeek = completions.filter {
            it.status == "APPROVED" && it.completedAt >= startTime
        }

        val totalGems = approvedThisWeek.sumOf { it.points }

        // Find top kid
        val kidGemsMap = mutableMapOf<Int, Int>()
        for (comp in approvedThisWeek) {
            kidGemsMap[comp.kidId] = (kidGemsMap[comp.kidId] ?: 0) + comp.points
        }

        var topKidName = "None yet"
        var topKidGems = 0
        val topKidId = kidGemsMap.maxByOrNull { it.value }?.key
        if (topKidId != null) {
            val kidProfile = profiles.find { it.id == topKidId }
            if (kidProfile != null) {
                topKidName = "${kidProfile.avatarEmoji} ${kidProfile.name}"
                topKidGems = kidGemsMap[topKidId] ?: 0
            }
        }

        // Daily activity array for the past 7 days
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayKeyFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayKey = dayKeyFormat.format(Date(now))

        val dailyList = mutableListOf<DailyActivity>()
        for (i in (daysLookback - 1) downTo 0) {
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = now - (i * 86_400_000L)
            }
            val key = dayKeyFormat.format(dayCal.time)
            val label = dayFormat.format(dayCal.time)

            val count = approvedThisWeek.count { comp ->
                dayKeyFormat.format(Date(comp.completedAt)) == key
            }
            val gems = approvedThisWeek.filter { comp ->
                dayKeyFormat.format(Date(comp.completedAt)) == key
            }.sumOf { it.points }

            dailyList.add(
                DailyActivity(
                    dayLabel = label,
                    count = count,
                    gems = gems,
                    isToday = key == todayKey
                )
            )
        }

        // Target completion rate (e.g. expected 2 chores per kid per day)
        val kidCount = profiles.count { it.role == "KID" }.coerceAtLeast(1)
        val expectedWeeklyChores = (kidCount * 2 * daysLookback).coerceAtLeast(1)
        val completionRate = ((approvedThisWeek.size.toFloat() / expectedWeeklyChores.toFloat()) * 100)
            .toInt().coerceIn(0, 100)

        // Category counts (dummy grouping from title if needed)
        val categories = mutableMapOf<String, Int>()
        for (comp in approvedThisWeek) {
            val cat = when {
                comp.choreTitle.contains("Bed", ignoreCase = true) || comp.choreTitle.contains("Room", ignoreCase = true) -> "Bedroom"
                comp.choreTitle.contains("Dish", ignoreCase = true) || comp.choreTitle.contains("Kitchen", ignoreCase = true) -> "Kitchen"
                comp.choreTitle.contains("Dog", ignoreCase = true) || comp.choreTitle.contains("Pet", ignoreCase = true) -> "Pets"
                comp.choreTitle.contains("Math", ignoreCase = true) || comp.choreTitle.contains("Reading", ignoreCase = true) -> "Homework"
                else -> "General"
            }
            categories[cat] = (categories[cat] ?: 0) + 1
        }

        return WeeklyReport(
            totalApprovedChores = approvedThisWeek.size,
            totalGemsAwarded = totalGems,
            topKidName = topKidName,
            topKidGems = topKidGems,
            completionRatePercent = completionRate,
            dailyActivities = dailyList,
            categoryCounts = categories
        )
    }
}
