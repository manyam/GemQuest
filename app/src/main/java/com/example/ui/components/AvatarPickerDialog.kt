package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.QuestGoldAccent

@Composable
fun AvatarPickerDialog(
    currentAvatar: String,
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit
) {
    val avatars = listOf(
        "🛡️", "🧙‍♀️", "🏹", "🦄", "🚀", "⚡",
        "🦁", "🐉", "🎨", "⚔️", "👑", "🥷",
        "👾", "🦸", "🧜‍♀️", "🦊", "🐯", "🦅",
        "🤖", "🐶", "🐱", "🌟", "🌈", "🏎️"
    )

    var selected by remember { mutableStateOf(currentAvatar) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("avatar_picker_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Choose Your Hero ✨",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pick an avatar that represents your quest persona",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Preview
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(QuestGoldAccent.copy(alpha = 0.2f))
                        .border(2.dp, QuestGoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selected, fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4-column Grid of Avatars
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(avatars) { emoji ->
                        val isCurrent = emoji == selected
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 0.dp,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selected = emoji }
                                .testTag("avatar_option_$emoji"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 26.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Avatar Button
                Button(
                    onClick = {
                        onAvatarSelected(selected)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_avatar_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Save Avatar ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
