package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.ui.theme.QuestRubyCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PointHistorySection(
    profiles: List<ProfileEntity>,
    transactions: List<PointTransactionEntity>,
    modifier: Modifier = Modifier
) {
    val kids = profiles.filter { it.role == "KID" }
    var selectedKidIndex by remember { mutableIntStateOf(0) }

    if (kids.isEmpty()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = "No kid profiles found.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val safeIndex = selectedKidIndex.coerceIn(0, kids.lastIndex)
    val currentKid = kids[safeIndex]
    val kidTransactions = transactions
        .filter { it.kidId == currentKid.id }
        .sortedByDescending { it.timestamp }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("point_history_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Point Balance Ledger & Trends",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Monitor point accumulation and spending over time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Kid Selector Tabs
            ScrollableTabRow(
                selectedTabIndex = safeIndex,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                kids.forEachIndexed { idx, kid ->
                    Tab(
                        selected = idx == safeIndex,
                        onClick = { selectedKidIndex = idx },
                        text = {
                            Text(
                                text = "${kid.avatarEmoji} ${kid.name}",
                                fontWeight = if (idx == safeIndex) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Stats Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Spendable Gems",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "💎 ${currentKid.currentPoints} Gems",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = QuestGoldAccent
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Lifetime XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "⭐ ${currentKid.totalXp} XP (Lvl ${currentKid.level})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Point Trend Line Chart
            Text(
                text = "Balance Trajectory",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            PointTrendLineChart(
                transactions = kidTransactions.take(10).reversed(),
                currentBalance = currentKid.currentPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Transactions List
            Text(
                text = "Recent Transactions (${kidTransactions.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (kidTransactions.isEmpty()) {
                Text(
                    text = "No point transactions recorded yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    kidTransactions.take(5).forEach { tx ->
                        val isPositive = tx.amount >= 0
                        val pillColor = if (isPositive) QuestEmeraldGreen else QuestRubyCoral

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.description,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = dateFormat.format(Date(tx.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pillColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isPositive) "+${tx.amount} 💎" else "${tx.amount} 💎",
                                    color = pillColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
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
fun PointTrendLineChart(
    transactions: List<PointTransactionEntity>,
    currentBalance: Int,
    modifier: Modifier = Modifier
) {
    // Generate simulated points curve from transactions
    val pointsData = remember(transactions, currentBalance) {
        if (transactions.isEmpty()) {
            listOf(currentBalance.coerceAtLeast(10), currentBalance)
        } else {
            var running = 0
            val list = mutableListOf<Int>()
            for (tx in transactions) {
                running += tx.amount
                list.add(running.coerceAtLeast(0))
            }
            if (list.size == 1) list.add(list.first())
            list
        }
    }

    val maxVal = (pointsData.maxOrNull() ?: 100).coerceAtLeast(50)
    val minVal = (pointsData.minOrNull() ?: 0).coerceAtLeast(0)
    val strokeColor = QuestEmeraldGreen

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (pointsData.size - 1).coerceAtLeast(1)
        val range = (maxVal - minVal).coerceAtLeast(1)

        val path = Path()
        pointsData.forEachIndexed { i, pt ->
            val x = i * stepX
            val normalizedY = (pt - minVal).toFloat() / range.toFloat()
            val y = height - (normalizedY * height * 0.8f) - (height * 0.1f)

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

            // Draw data point circle
            drawCircle(
                color = strokeColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
