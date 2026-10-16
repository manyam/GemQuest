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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import com.example.ui.components.PointHistorySection
import com.example.ui.components.QuestIconPicker
import com.example.ui.components.WeeklySummaryDashboardCard
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.ui.theme.QuestRubyCoral
import com.example.util.WeeklyReport

@Composable
fun ParentCommandScreen(
    parent: ProfileEntity,
    profiles: List<ProfileEntity>,
    chores: List<ChoreEntity>,
    pendingCompletions: List<ChoreCompletionEntity>,
    rewards: List<RewardEntity>,
    pendingRedemptions: List<RewardRedemptionEntity>,
    transactions: List<PointTransactionEntity>,
    weeklyReport: WeeklyReport,
    onApproveChore: (Int) -> Unit,
    onRejectChore: (Int) -> Unit,
    onApproveRedemption: (Int) -> Unit,
    onRejectRedemption: (Int) -> Unit,
    onCreateChore: (title: String, desc: String, points: Int, icon: String, kidId: Int?, freq: String, cat: String) -> Unit,
    onUpdateChore: (id: Int, title: String, desc: String, points: Int, icon: String, kidId: Int?, freq: String, cat: String) -> Unit,
    onDeleteChore: (Int) -> Unit,
    onCreateReward: (title: String, desc: String, cost: Int, icon: String) -> Unit,
    onDeleteReward: (Int) -> Unit,
    onUpdatePin: (profileId: Int, newPin: String) -> Unit,
    onAddKidProfile: (name: String, emoji: String, title: String, pin: String) -> Unit,
    onDeleteProfile: (profileId: Int) -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    // 0: Approvals, 1: Quests, 2: Rewards, 3: History & Reports, 4: Family PINs

    var showAddChoreDialog by remember { mutableStateOf(false) }
    var choreToEdit by remember { mutableStateOf<ChoreEntity?>(null) }
    var showAddRewardDialog by remember { mutableStateOf(false) }
    var showAddKidDialog by remember { mutableStateOf(false) }

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
                    icon = { Icon(Icons.Default.PendingActions, contentDescription = "Approvals") },
                    label = {
                        val totalPending = pendingCompletions.size + pendingRedemptions.size
                        Text(if (totalPending > 0) "Approvals ($totalPending)" else "Approvals")
                    },
                    modifier = Modifier.testTag("parent_tab_approvals")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Quests") },
                    label = { Text("Quests") },
                    modifier = Modifier.testTag("parent_tab_quests")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Redeem, contentDescription = "Rewards") },
                    label = { Text("Rewards") },
                    modifier = Modifier.testTag("parent_tab_rewards")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                    label = { Text("Reports") },
                    modifier = Modifier.testTag("parent_tab_reports")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "PINs") },
                    label = { Text("PINs") },
                    modifier = Modifier.testTag("parent_tab_pins")
                )
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                1 -> {
                    FloatingActionButton(
                        onClick = { showAddChoreDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("fab_add_chore")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Quest")
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { showAddRewardDialog = true },
                        containerColor = QuestGoldAccent,
                        contentColor = Color(0xFF332000),
                        modifier = Modifier.testTag("fab_add_reward")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reward")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Parent Header Bar with Finish Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "👑", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "Parent Command Center",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Guild Master Access",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                    modifier = Modifier.testTag("parent_save_btn")
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
                        Text("Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> ApprovalsTabContent(
                    profiles = profiles,
                    pendingCompletions = pendingCompletions,
                    pendingRedemptions = pendingRedemptions,
                    onApproveChore = onApproveChore,
                    onRejectChore = onRejectChore,
                    onApproveRedemption = onApproveRedemption,
                    onRejectRedemption = onRejectRedemption
                )
                1 -> QuestsManagerTabContent(
                    chores = chores,
                    profiles = profiles,
                    onEditChore = { choreToEdit = it },
                    onDeleteChore = onDeleteChore
                )
                2 -> RewardsManagerTabContent(
                    rewards = rewards,
                    onDeleteReward = onDeleteReward
                )
                3 -> ReportsAndBalancesTabContent(
                    profiles = profiles,
                    weeklyReport = weeklyReport,
                    transactions = transactions
                )
                4 -> FamilySettingsTabContent(
                    profiles = profiles,
                    onUpdatePin = onUpdatePin,
                    onDeleteProfile = onDeleteProfile,
                    onOpenAddKid = { showAddKidDialog = true }
                )
            }
        }
    }

    if (showAddChoreDialog) {
        AddChoreDialog(
            kids = profiles.filter { it.role == "KID" },
            onDismiss = { showAddChoreDialog = false },
            onConfirm = { title, desc, points, icon, kidId, freq, cat ->
                onCreateChore(title, desc, points, icon, kidId, freq, cat)
                showAddChoreDialog = false
            }
        )
    }

    choreToEdit?.let { chore ->
        EditChoreDialog(
            chore = chore,
            kids = profiles.filter { it.role == "KID" },
            onDismiss = { choreToEdit = null },
            onConfirm = { id, title, desc, points, icon, kidId, freq, cat ->
                onUpdateChore(id, title, desc, points, icon, kidId, freq, cat)
                choreToEdit = null
            }
        )
    }

    if (showAddRewardDialog) {
        AddRewardDialog(
            onDismiss = { showAddRewardDialog = false },
            onConfirm = { title, desc, cost, icon ->
                onCreateReward(title, desc, cost, icon)
                showAddRewardDialog = false
            }
        )
    }

    if (showAddKidDialog) {
        AddKidDialog(
            onDismiss = { showAddKidDialog = false },
            onConfirm = { name, emoji, title, pin ->
                onAddKidProfile(name, emoji, title, pin)
                showAddKidDialog = false
            }
        )
    }
}

@Composable
fun ApprovalsTabContent(
    profiles: List<ProfileEntity>,
    pendingCompletions: List<ChoreCompletionEntity>,
    pendingRedemptions: List<RewardRedemptionEntity>,
    onApproveChore: (Int) -> Unit,
    onRejectChore: (Int) -> Unit,
    onApproveRedemption: (Int) -> Unit,
    onRejectRedemption: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Pending Approvals Queue",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Verify completed chores and grant rewards",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section: Completed Chores waiting for approval
        item {
            Text(
                text = "Turned In Quests (${pendingCompletions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (pendingCompletions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "✨ No pending quests waiting for approval!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingCompletions, key = { "comp_${it.id}" }) { comp ->
                val kid = profiles.find { it.id == comp.kidId }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_chore_${comp.id}")
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = kid?.avatarEmoji ?: "🛡️", fontSize = 28.sp)
                            Column {
                                Text(
                                    text = comp.choreTitle,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Completed by ${kid?.name ?: "Adventurer"} • +${comp.points} Gems",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = QuestGoldAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { onRejectChore(comp.id) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(QuestRubyCoral.copy(alpha = 0.15f))
                                    .testTag("reject_chore_${comp.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Reject", tint = QuestRubyCoral)
                            }

                            Button(
                                onClick = { onApproveChore(comp.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = QuestEmeraldGreen,
                                    contentColor = Color(0xFF003826)
                                ),
                                modifier = Modifier.testTag("approve_chore_${comp.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: Reward Redemptions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reward Claims (${pendingRedemptions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (pendingRedemptions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "🎁 No reward claims waiting for approval.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingRedemptions, key = { "red_${it.id}" }) { red ->
                val kid = profiles.find { it.id == red.kidId }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_redemption_${red.id}")
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎁", fontSize = 28.sp)
                            Column {
                                Text(
                                    text = red.rewardTitle,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Claimed by ${kid?.name ?: "Adventurer"} • Cost: ${red.gemCost} Gems",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = QuestGoldAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { onRejectRedemption(red.id) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(QuestRubyCoral.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Decline", tint = QuestRubyCoral)
                            }

                            Button(
                                onClick = { onApproveRedemption(red.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = QuestGoldAccent,
                                    contentColor = Color(0xFF332000)
                                ),
                                modifier = Modifier.testTag("approve_redemption_${red.id}")
                            ) {
                                Text("Fulfill", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestsManagerTabContent(
    chores: List<ChoreEntity>,
    profiles: List<ProfileEntity>,
    onEditChore: (ChoreEntity) -> Unit,
    onDeleteChore: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Quest Board Manager (${chores.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap the edit icon to modify quest details or points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(chores, key = { it.id }) { chore ->
            val assignedKid = profiles.find { it.id == chore.assignedKidId }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manage_chore_${chore.id}")
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = chore.iconEmoji, fontSize = 28.sp)
                        Column {
                            Text(
                                text = chore.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${chore.points} Gems • ${chore.frequency} • ${assignedKid?.name ?: "All Kids"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onEditChore(chore) },
                            modifier = Modifier.testTag("edit_chore_${chore.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Quest",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { onDeleteChore(chore.id) },
                            modifier = Modifier.testTag("delete_chore_${chore.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Quest",
                                tint = QuestRubyCoral
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardsManagerTabContent(
    rewards: List<RewardEntity>,
    onDeleteReward: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Reward Bazaar Catalog (${rewards.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure custom rewards that kids can buy with gems",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(rewards, key = { it.id }) { reward ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = reward.iconEmoji, fontSize = 28.sp)
                        Column {
                            Text(
                                text = reward.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${reward.gemCost} Gems • ${reward.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = QuestGoldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = { onDeleteReward(reward.id) },
                        modifier = Modifier.testTag("delete_reward_${reward.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = QuestRubyCoral
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsAndBalancesTabContent(
    profiles: List<ProfileEntity>,
    weeklyReport: WeeklyReport,
    transactions: List<PointTransactionEntity>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            WeeklySummaryDashboardCard(report = weeklyReport)
        }

        item {
            PointHistorySection(
                profiles = profiles,
                transactions = transactions
            )
        }
    }
}

@Composable
fun FamilySettingsTabContent(
    profiles: List<ProfileEntity>,
    onUpdatePin: (profileId: Int, newPin: String) -> Unit,
    onDeleteProfile: (profileId: Int) -> Unit,
    onOpenAddKid: () -> Unit
) {
    var editingProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    var profileToDelete by remember { mutableStateOf<ProfileEntity?>(null) }
    var newPinInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Clean Header (without Add Kid button)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Family Profiles & PIN Security",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage individual 4-digit PIN access codes and members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // List of Profiles
        items(profiles, key = { it.id }) { profile ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
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
                        Text(text = profile.avatarEmoji, fontSize = 30.sp)
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (profile.role == "PARENT") {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(QuestGoldAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Admin",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Current PIN: ${profile.pin}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                editingProfile = profile
                                newPinInput = profile.pin
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("change_pin_${profile.id}")
                        ) {
                            Text(
                                text = "Change PIN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Allow deleting kid profiles
                        if (profile.role == "KID") {
                            IconButton(
                                onClick = { profileToDelete = profile },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(QuestRubyCoral.copy(alpha = 0.12f))
                                    .testTag("delete_profile_btn_${profile.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Profile",
                                    tint = QuestRubyCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add a Kid button placed BELOW the profile list
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onOpenAddKid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_kid_below_list_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add a Kid Adventurer 🛡️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Change PIN Dialog
    editingProfile?.let { prof ->
        AlertDialog(
            onDismissRequest = { editingProfile = null },
            title = { Text("Change PIN for ${prof.name}") },
            text = {
                Column {
                    Text(
                        text = "Enter a 4-digit numeric code:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                newPinInput = it
                            }
                        },
                        label = { Text("4-Digit PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 4) {
                            onUpdatePin(prof.id, newPinInput)
                            editingProfile = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = newPinInput.length == 4,
                    modifier = Modifier.testTag("save_pin_btn")
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProfile = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Profile Confirmation Dialog
    profileToDelete?.let { prof ->
        AlertDialog(
            onDismissRequest = { profileToDelete = null },
            title = { Text("Delete ${prof.name}'s Profile?") },
            text = {
                Text(
                    text = "Are you sure you want to remove ${prof.name}? This will delete their profile from the family Guild.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProfile(prof.id)
                        profileToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuestRubyCoral,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_delete_profile_btn")
                ) {
                    Text("Delete Profile", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { profileToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddChoreDialog(
    kids: List<ProfileEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, points: Int, icon: String, kidId: Int?, freq: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var pointsText by remember { mutableStateOf("25") }
    var selectedEmoji by remember { mutableStateOf("🧹") }
    var selectedFrequency by remember { mutableStateOf("DAILY") }
    var selectedCategory by remember { mutableStateOf("CLEANING") }
    var selectedKidId by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Household Quest ⚔️") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Quest Title") },
                        placeholder = { Text("e.g. Wash dishes, Clean bedroom, Walk dog") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_quest_title_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Instructions") },
                        placeholder = { Text("e.g. Put blocks in the toy bin") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = pointsText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) pointsText = it },
                        label = { Text("Gem Points Reward") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    QuestIconPicker(
                        selectedIcon = selectedEmoji,
                        questQuery = "$title $desc",
                        onIconSelected = { selectedEmoji = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(
                        text = "Recurrence",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("DAILY", "WEEKLY", "ONE_OFF").forEach { freq ->
                            Button(
                                onClick = { selectedFrequency = freq },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedFrequency == freq) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedFrequency == freq) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                Text(freq, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pts = pointsText.toIntOrNull() ?: 20
                    if (title.isNotBlank()) {
                        onConfirm(title, desc, pts, selectedEmoji, selectedKidId, selectedFrequency, selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = title.isNotBlank()
            ) {
                Text("Create Quest")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditChoreDialog(
    chore: ChoreEntity,
    kids: List<ProfileEntity>,
    onDismiss: () -> Unit,
    onConfirm: (id: Int, title: String, desc: String, points: Int, icon: String, kidId: Int?, freq: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf(chore.title) }
    var desc by remember { mutableStateOf(chore.description) }
    var pointsText by remember { mutableStateOf(chore.points.toString()) }
    var selectedEmoji by remember { mutableStateOf(chore.iconEmoji) }
    var selectedFrequency by remember { mutableStateOf(chore.frequency) }
    var selectedCategory by remember { mutableStateOf(chore.category) }
    var selectedKidId by remember { mutableStateOf(chore.assignedKidId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Quest ⚔️") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Quest Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_chore_title_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Instructions") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = pointsText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) pointsText = it },
                        label = { Text("Gem Points Reward") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_chore_points_input")
                    )
                }
                item {
                    QuestIconPicker(
                        selectedIcon = selectedEmoji,
                        questQuery = "$title $desc",
                        onIconSelected = { selectedEmoji = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(text = "Recurrence", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("DAILY", "WEEKLY", "ONE_OFF").forEach { freq ->
                            Button(
                                onClick = { selectedFrequency = freq },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedFrequency == freq) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedFrequency == freq) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                Text(freq, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pts = pointsText.toIntOrNull() ?: chore.points
                    if (title.isNotBlank()) {
                        onConfirm(chore.id, title, desc, pts, selectedEmoji, selectedKidId, selectedFrequency, selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("save_edited_chore_btn")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddRewardDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, cost: Int, icon: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("50") }
    var selectedEmoji by remember { mutableStateOf("🎁") }

    val emojis = listOf("🎁", "🎮", "🍦", "🍿", "🌙", "🍕", "🚴", "🎨")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Reward 🎁") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reward Name") },
                    placeholder = { Text("e.g. 1 Hour Video Games") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Extra gaming time on weekend") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = costText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) costText = it },
                    label = { Text("Cost in Gems") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Choose Icon", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojis.take(5).forEach { emo ->
                        Button(
                            onClick = { selectedEmoji = emo },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedEmoji == emo) QuestGoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedEmoji == emo) Color(0xFF332000) else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Text(text = emo, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costText.toIntOrNull() ?: 50
                    if (title.isNotBlank()) {
                        onConfirm(title, desc, cost, selectedEmoji)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuestGoldAccent,
                    contentColor = Color(0xFF332000)
                ),
                enabled = title.isNotBlank()
            ) {
                Text("Add Reward")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddKidDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, emoji: String, title: String, pin: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🏹") }
    var heroTitle by remember { mutableStateOf("Scout of the Realm") }
    var pin by remember { mutableStateOf("3333") }

    val emojis = listOf("🏹", "🦄", "🚀", "⚡", "🦁", "🐉", "🎨")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Child Adventurer 🛡️") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Child Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = heroTitle,
                    onValueChange = { heroTitle = it },
                    label = { Text("Hero Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) pin = it },
                    label = { Text("4-Digit PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Choose Avatar", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojis.take(5).forEach { emo ->
                        Button(
                            onClick = { selectedEmoji = emo },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedEmoji == emo) QuestGoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedEmoji == emo) Color(0xFF332000) else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Text(text = emo, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && pin.length == 4) {
                        onConfirm(name, selectedEmoji, heroTitle, pin)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = name.isNotBlank() && pin.length == 4
            ) {
                Text("Add Adventurer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
