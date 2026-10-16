package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.QuestGoldAccent
import com.example.util.QuestIconInfo
import com.example.util.QuestIconMatcher

@Composable
fun QuestIconPicker(
    selectedIcon: String,
    questQuery: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAllIcons by remember { mutableStateOf(false) }
    var manuallyChosenIcon by remember { mutableStateOf<String?>(null) }

    val scoredMatches = remember(questQuery) {
        QuestIconMatcher.getScoredMatches(questQuery)
    }

    val matchingIcons = remember(scoredMatches) {
        scoredMatches.map { it.iconInfo }
    }

    val hasMatches = matchingIcons.isNotEmpty()

    // Auto-select the top matched icon when typing if user hasn't explicitly picked a different icon
    LaunchedEffect(matchingIcons) {
        if (hasMatches) {
            val topMatch = matchingIcons.first().icon
            if (manuallyChosenIcon == null && selectedIcon != topMatch) {
                onIconSelected(topMatch)
            }
        }
    }

    // Find label for the currently selected icon
    val currentIconInfo = remember(selectedIcon) {
        QuestIconMatcher.ALL_QUEST_ICONS.firstOrNull { it.icon == selectedIcon }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        // Top Header: Label, Current Selection Badge, and Browse All Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Quest Icon",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Selected Icon pill
                Surface(
                    color = QuestGoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QuestGoldAccent.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = selectedIcon, fontSize = 16.sp)
                        Text(
                            text = currentIconInfo?.label ?: "Selected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            TextButton(
                onClick = { showAllIcons = !showAllIcons },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.testTag("toggle_all_icons_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (showAllIcons) "Hide Library" else "All Icons",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (showAllIcons) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECTION 1: Matching Icons based on keywords (if found)
        if (hasMatches) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = QuestGoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Matching Icons (${matchingIcons.size} found):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = QuestGoldAccent
                )
            }

            // Horizontal scrolling row of matching icon cards
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .testTag("matching_icons_list"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scoredMatches.forEach { scoredItem ->
                    val iconInfo = scoredItem.iconInfo
                    val isSelected = iconInfo.icon == selectedIcon

                    MatchingIconCard(
                        iconInfo = iconInfo,
                        matchedKeyword = scoredItem.matchedKeyword,
                        isSelected = isSelected,
                        onClick = {
                            manuallyChosenIcon = iconInfo.icon
                            onIconSelected(iconInfo.icon)
                        }
                    )
                }
            }
        } else {
            // SECTION 2: No keywords matched yet -> Show Popular Icons + helpful hint
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "Suggested Popular Icons:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .testTag("popular_icons_list"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuestIconMatcher.POPULAR_QUEST_ICONS.forEach { iconInfo ->
                    val isSelected = iconInfo.icon == selectedIcon

                    MatchingIconCard(
                        iconInfo = iconInfo,
                        matchedKeyword = null,
                        isSelected = isSelected,
                        onClick = {
                            manuallyChosenIcon = iconInfo.icon
                            onIconSelected(iconInfo.icon)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "💡 Type words in title (e.g. dishes, bed, dog, homework, sweep, laundry) to auto-match icons.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // SECTION 3: Expanded Full Icon Library (Animated visibility)
        AnimatedVisibility(
            visible = showAllIcons,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Complete Icon Library (${QuestIconMatcher.ALL_QUEST_ICONS.size} icons):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(QuestIconMatcher.ALL_QUEST_ICONS) { iconInfo ->
                        val isSelected = iconInfo.icon == selectedIcon

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) QuestGoldAccent.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) QuestGoldAccent else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    manuallyChosenIcon = iconInfo.icon
                                    onIconSelected(iconInfo.icon)
                                }
                                .testTag("library_icon_${iconInfo.label}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(text = iconInfo.icon, fontSize = 20.sp)
                                Text(
                                    text = iconInfo.label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(12.dp)
                                        .background(QuestGoldAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchingIconCard(
    iconInfo: QuestIconInfo,
    matchedKeyword: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(76.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) QuestGoldAccent.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) QuestGoldAccent else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(4.dp)
            .testTag("matching_icon_${iconInfo.label}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = iconInfo.icon, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = iconInfo.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (matchedKeyword != null) {
                Text(
                    text = matchedKeyword,
                    fontSize = 9.sp,
                    color = QuestGoldAccent,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(16.dp)
                    .background(QuestGoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}
