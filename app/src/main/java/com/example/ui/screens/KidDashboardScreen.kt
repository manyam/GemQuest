package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ChoreEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import com.example.ui.components.AvatarPickerDialog
import com.example.ui.components.BadgesShowcase
import com.example.ui.components.KidHeroHeader
import com.example.ui.components.LeaderboardCard
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.util.GamificationEngine

@Composable
fun KidDashboardScreen(
    kid: ProfileEntity,
    allProfiles: List<ProfileEntity>,
    chores: List<ChoreEntity>,
    completions: List<ChoreCompletionEntity>,
    rewards: List<RewardEntity>,
    redemptions: List<RewardRedemptionEntity>,
    onMarkChoreDone: (choreId: Int) -> Unit,
    onRequestRedeem: (rewardId: Int) -> Unit,
    onUpdateAvatar: (newAvatar: String) -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Quests, 1: Bazaar, 2: Leaderboard
    var showAvatarPicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Quests") },
                    label = { Text("Quests") },
                    modifier = Modifier.testTag("kid_tab_quests")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Redeem, contentDescription = "Bazaar") },
                    label = { Text("Bazaar") },
                    modifier = Modifier.testTag("kid_tab_bazaar")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Stars, contentDescription = "Ranks") },
                    label = { Text("Ranks & Badges") },
                    modifier = Modifier.testTag("kid_tab_ranks")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar with Save Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⚔️", fontSize = 24.sp)
                    Text(
                        text = "Adventurer Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Save / Return to Entry Wall Button (renamed from Finish to Save)
                Button(
                    onClick = onLockScreen,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("kid_save_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Kid Hero Status Header with Avatar Customization
            KidHeroHeader(
                profile = kid,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                onAvatarClick = { showAvatarPicker = true }
            )

            if (showAvatarPicker) {
                AvatarPickerDialog(
                    currentAvatar = kid.avatarEmoji,
                    onDismiss = { showAvatarPicker = false },
                    onAvatarSelected = { newEmoji ->
                        onUpdateAvatar(newEmoji)
                        showAvatarPicker = false
                    }
                )
            }

            // Content based on tab
            when (selectedTab) {
                0 -> QuestsTabContent(
                    kid = kid,
                    chores = chores,
                    completions = completions,
                    onMarkDone = onMarkChoreDone
                )
                1 -> RewardBazaarTabContent(
                    kid = kid,
                    rewards = rewards,
                    redemptions = redemptions,
                    onRedeem = onRequestRedeem
                )
                2 -> LeaderboardTabContent(
                    kid = kid,
                    allProfiles = allProfiles,
                    completions = completions,
                    redemptions = redemptions
                )
            }
        }
    }
}

@Composable
fun QuestsTabContent(
    kid: ProfileEntity,
    chores: List<ChoreEntity>,
    completions: List<ChoreCompletionEntity>,
    onMarkDone: (Int) -> Unit
) {
    var filterStatus by remember { mutableStateOf("AVAILABLE") } // "AVAILABLE", "PENDING", "COMPLETED"

    val kidCompletions = completions.filter { it.kidId == kid.id }
    val pendingChoreIds = kidCompletions.filter { it.status == "PENDING" }.map { it.choreId }.toSet()
    val approvedChoreIds = kidCompletions.filter { it.status == "APPROVED" }.map { it.choreId }.toSet()

    // Chores assigned to this kid or open to all
    val eligibleChores = chores.filter { it.assignedKidId == null || it.assignedKidId == kid.id }

    val displayedChores = when (filterStatus) {
        "AVAILABLE" -> eligibleChores.filter { it.id !in pendingChoreIds }
        "PENDING" -> eligibleChores.filter { it.id in pendingChoreIds }
        else -> eligibleChores.filter { it.id in approvedChoreIds }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Chips Row with high contrast in light & dark modes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "AVAILABLE",
                    onClick = { filterStatus = "AVAILABLE" },
                    label = { Text("Available (${eligibleChores.count { it.id !in pendingChoreIds }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("filter_available")
                )
                FilterChip(
                    selected = filterStatus == "PENDING",
                    onClick = { filterStatus = "PENDING" },
                    label = { Text("Pending (${pendingChoreIds.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = QuestGoldAccent,
                        selectedLabelColor = Color(0xFF332000),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("filter_pending")
                )
                FilterChip(
                    selected = filterStatus == "COMPLETED",
                    onClick = { filterStatus = "COMPLETED" },
                    label = { Text("Done (${approvedChoreIds.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = QuestEmeraldGreen,
                        selectedLabelColor = Color(0xFF003826),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("filter_completed")
                )
            }
        }

        if (displayedChores.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (filterStatus == "AVAILABLE") "All caught up! No quests waiting."
                            else if (filterStatus == "PENDING") "No quests waiting for approval."
                            else "No completed quests yet. Go embark!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Check the Bazaar or ask parents for more quests!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(displayedChores, key = { it.id }) { chore ->
                val isPending = chore.id in pendingChoreIds
                val isApproved = chore.id in approvedChoreIds

                ChoreQuestCard(
                    chore = chore,
                    isPending = isPending,
                    isApproved = isApproved,
                    onMarkDone = { onMarkDone(chore.id) }
                )
            }
        }
    }
}

@Composable
fun ChoreQuestCard(
    chore: ChoreEntity,
    isPending: Boolean,
    isApproved: Boolean,
    onMarkDone: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chore_card_${chore.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = chore.iconEmoji, fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = chore.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = chore.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = chore.frequency,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = chore.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Gem Reward Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(QuestGoldAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+${chore.points} 💎",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action / Status Button
            when {
                isPending -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuestGoldAccent.copy(alpha = 0.18f))
                            .border(1.dp, QuestGoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = QuestGoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Waiting for Parent Approval",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = QuestGoldAccent
                        )
                    }
                }
                isApproved -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuestEmeraldGreen.copy(alpha = 0.18f))
                            .border(1.dp, QuestEmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = QuestEmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Completed & Gems Awarded!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = QuestEmeraldGreen
                        )
                    }
                }
                else -> {
                    // Turn In Quest button with theme-adaptive standout colors
                    Button(
                        onClick = onMarkDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("complete_quest_${chore.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Turn In Quest ⚔️",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RewardBazaarTabContent(
    kid: ProfileEntity,
    rewards: List<RewardEntity>,
    redemptions: List<RewardRedemptionEntity>,
    onRedeem: (Int) -> Unit
) {
    val pendingRedemptionIds = redemptions
        .filter { it.kidId == kid.id && it.status == "PENDING" }
        .map { it.rewardId }
        .toSet()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Reward Bazaar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Spend your hard-earned gems on epic real-world treasures!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(rewards, key = { it.id }) { reward ->
            val canAfford = kid.currentPoints >= reward.gemCost
            val isPending = reward.id in pendingRedemptionIds

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reward_card_${reward.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(QuestGoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = reward.iconEmoji, fontSize = 26.sp)
                        }

                        Column {
                            Text(
                                text = reward.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = reward.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${reward.gemCost} Gems",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = QuestGoldAccent
                            )
                        }
                    }

                    if (isPending) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(QuestGoldAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Requested",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = QuestGoldAccent
                            )
                        }
                    } else {
                        Button(
                            onClick = { onRedeem(reward.id) },
                            enabled = canAfford,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = QuestEmeraldGreen,
                                contentColor = Color(0xFF003826),
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("claim_reward_${reward.id}")
                        ) {
                            Text(
                                text = if (canAfford) "Claim" else "Need ${reward.gemCost - kid.currentPoints} 💎",
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

@Composable
fun LeaderboardTabContent(
    kid: ProfileEntity,
    allProfiles: List<ProfileEntity>,
    completions: List<ChoreCompletionEntity>,
    redemptions: List<RewardRedemptionEntity>
) {
    val kidCompletions = completions.filter { it.kidId == kid.id && it.status == "APPROVED" }
    val kidRedemptions = redemptions.filter { it.kidId == kid.id && it.status == "APPROVED" }
    val badges = GamificationEngine.getBadges(
        totalXp = kid.totalXp,
        currentPoints = kid.currentPoints,
        completedCount = kidCompletions.size,
        redemptionsCount = kidRedemptions.size
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LeaderboardCard(profiles = allProfiles)
        }

        item {
            BadgesShowcase(badges = badges)
        }
    }
}
