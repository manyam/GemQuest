package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfileEntity
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.ui.theme.QuestIndigoPrimary
import com.example.ui.theme.QuestPurpleSecondary
import com.example.ui.theme.QuestRubyCoral
import com.example.util.Badge
import com.example.util.DailyActivity
import com.example.util.GamificationEngine
import com.example.util.WeeklyReport

@Composable
fun KidHeroHeader(
    profile: ProfileEntity,
    modifier: Modifier = Modifier,
    onAvatarClick: (() -> Unit)? = null
) {
    val levelFraction = GamificationEngine.levelProgressFraction(profile.totalXp, profile.level)
    val nextXp = GamificationEngine.nextLevelTargetXp(profile.level)
    val floorXp = GamificationEngine.currentLevelFloorXp(profile.level)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("kid_hero_header")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(profile.colorHex).copy(alpha = 0.35f),
                                        Color(profile.colorHex).copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(2.dp, Color(profile.colorHex), CircleShape)
                            .then(
                                if (onAvatarClick != null) Modifier.clickable(onClick = onAvatarClick)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.avatarEmoji,
                            fontSize = 32.sp
                        )
                        if (onAvatarClick != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "✏️", fontSize = 10.sp)
                            }
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(QuestGoldAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Lvl ${profile.level}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Text(
                            text = GamificationEngine.getHeroTitle(profile.level, profile.role),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Gem Purse
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuestGoldAccent.copy(alpha = 0.18f))
                        .border(1.dp, QuestGoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "💎", fontSize = 18.sp)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${profile.currentPoints}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gems",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "XP Progress to Lvl ${profile.level + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${profile.totalXp - floorXp} / ${nextXp - floorXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { levelFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = QuestEmeraldGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun LeaderboardCard(
    profiles: List<ProfileEntity>,
    modifier: Modifier = Modifier
) {
    val sortedKids = profiles
        .filter { it.role == "KID" }
        .sortedByDescending { it.totalXp }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("leaderboard_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = QuestGoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Family Leaderboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                sortedKids.forEachIndexed { index, kid ->
                    val medal = when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "#${index + 1}"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = medal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = kid.avatarEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = kid.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Lvl ${kid.level} • ${kid.totalXp} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "💎", fontSize = 14.sp)
                            Text(
                                text = "${kid.currentPoints}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesShowcase(
    badges: List<Badge>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("badges_showcase")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = QuestGoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Quest Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (badge.isUnlocked) QuestGoldAccent.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (badge.isUnlocked) QuestGoldAccent.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                                fontSize = 20.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (badge.isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(QuestEmeraldGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "UNLOCKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklySummaryDashboardCard(
    report: WeeklyReport,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_summary_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Guild Report",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Past 7 Days Family Productivity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${report.completionRatePercent}% Goal",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Stat Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    title = "Quests Done",
                    value = "${report.totalApprovedChores}",
                    iconEmoji = "✅",
                    color = QuestEmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    title = "Gems Awarded",
                    value = "${report.totalGemsAwarded}",
                    iconEmoji = "💎",
                    color = QuestGoldAccent,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    title = "Top Champion",
                    value = report.topKidName.split(" ").lastOrNull() ?: "-",
                    iconEmoji = "👑",
                    color = QuestPurpleSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Daily Activity Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Custom 7-day Bar Chart using DrawScope
            DailyBarChart(
                activities = report.dailyActivities,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    iconEmoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = iconEmoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DailyBarChart(
    activities: List<DailyActivity>,
    modifier: Modifier = Modifier
) {
    val maxCount = (activities.maxOfOrNull { it.count } ?: 1).coerceAtLeast(4)
    val barColor = MaterialTheme.colorScheme.primary
    val todayColor = QuestGoldAccent

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        activities.forEach { act ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${act.count}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (act.isToday) todayColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height((70.dp * (act.count.toFloat() / maxCount.toFloat())).coerceAtLeast(6.dp))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (act.isToday) todayColor else barColor)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = act.dayLabel,
                    fontSize = 10.sp,
                    fontWeight = if (act.isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (act.isToday) todayColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
