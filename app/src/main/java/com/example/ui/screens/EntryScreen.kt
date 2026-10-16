package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfileEntity
import com.example.ui.components.PinPadDialog
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.ui.theme.QuestIndigoPrimary
import com.example.ui.theme.QuestPurpleSecondary

@Composable
fun EntryScreen(
    profiles: List<ProfileEntity>,
    onProfileAuthenticated: (ProfileEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProfileForPin by remember { mutableStateOf<ProfileEntity?>(null) }

    val kids = profiles.filter { it.role == "KID" }
    val parent = profiles.find { it.role == "PARENT" }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Title Banner
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, QuestPurpleSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💎", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "GemQuest",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Who is questing today? Tap your profile & enter PIN",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // KIDS SECTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Adventurers (Kids)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Kid Cards Grid / List
                    kids.forEach { kid ->
                        KidFaceEntryCard(
                            kid = kid,
                            onClick = { selectedProfileForPin = kid }
                        )
                    }
                }
            }

            // PARENT / ADMIN SECTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = QuestGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Guild Master (Parents)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (parent != null) {
                        ParentFaceEntryCard(
                            parent = parent,
                            onClick = { selectedProfileForPin = parent }
                        )
                    }
                }
            }

            // Helpful PIN reminder tip
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔒 Quick PIN Guide: Leo (1111) • Maya (2222) • Parents (1234)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Active PIN Pad Dialog
        selectedProfileForPin?.let { profile ->
            PinPadDialog(
                profile = profile,
                onDismiss = { selectedProfileForPin = null },
                onPinSuccess = {
                    val authProfile = profile
                    selectedProfileForPin = null
                    onProfileAuthenticated(authProfile)
                }
            )
        }
    }
}

@Composable
fun KidFaceEntryCard(
    kid: ProfileEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("kid_profile_${kid.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large Avatar Face
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(kid.colorHex).copy(alpha = 0.35f),
                                    Color(kid.colorHex).copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(2.5.dp, Color(kid.colorHex), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = kid.avatarEmoji, fontSize = 34.sp)
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = kid.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Lvl ${kid.level}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = kid.heroTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "💎 ${kid.currentPoints} Gems • ⭐ ${kid.totalXp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = QuestGoldAccent
                    )
                }
            }

            // Lock Indicator Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN Protected",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "PIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ParentFaceEntryCard(
    parent: ProfileEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.5.dp, QuestGoldAccent.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("parent_profile_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Parent Face
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    QuestGoldAccent.copy(alpha = 0.4f),
                                    QuestGoldAccent.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(2.5.dp, QuestGoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = parent.avatarEmoji, fontSize = 34.sp)
                }

                Column {
                    Text(
                        text = parent.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Approve Quests, Rewards & Reports",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "👑 Parent Command Center",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = QuestGoldAccent
                    )
                }
            }

            // Lock Indicator Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(QuestGoldAccent.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN Protected",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "PIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
