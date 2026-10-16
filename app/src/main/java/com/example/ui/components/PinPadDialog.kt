package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProfileEntity
import com.example.ui.theme.QuestEmeraldGreen
import com.example.ui.theme.QuestGoldAccent
import com.example.ui.theme.QuestRubyCoral

@Composable
fun PinPadDialog(
    profile: ProfileEntity,
    onDismiss: () -> Unit,
    onPinSuccess: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun handleDigit(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            isError = false
            if (newPin.length == 4) {
                if (newPin == profile.pin) {
                    onPinSuccess()
                } else {
                    isError = true
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            isError = false
        }
    }

    fun handleClear() {
        enteredPin = ""
        isError = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("pin_pad_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button & header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_pin_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar and name
                Text(
                    text = profile.avatarEmoji,
                    fontSize = 44.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (profile.role == "PARENT") "Enter Master Guild PIN" else "Enter 4-Digit Adventurer PIN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4 PIN Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        val dotColor = when {
                            isError -> QuestRubyCoral
                            isFilled -> if (profile.role == "PARENT") QuestGoldAccent else QuestEmeraldGreen
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(
                                    width = 2.dp,
                                    color = if (isError) QuestRubyCoral else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Error text or default hint
                AnimatedVisibility(
                    visible = isError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Incorrect PIN. Try again!",
                        color = QuestRubyCoral,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!isError) {
                    Text(
                        text = "Default PIN: ${profile.pin}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3x4 Keypad
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("CLEAR", "0", "BACK")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (row in keypadRows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (key in row) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (key in listOf("CLEAR", "BACK")) {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                        .clickable {
                                            when (key) {
                                                "CLEAR" -> handleClear()
                                                "BACK" -> handleBackspace()
                                                else -> handleDigit(key)
                                            }
                                        }
                                        .testTag("pin_key_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (key) {
                                        "BACK" -> {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = "Backspace",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        "CLEAR" -> {
                                            Text(
                                                text = "C",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        else -> {
                                            Text(
                                                text = key,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 22.sp,
                                                color = MaterialTheme.colorScheme.onSurface
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
    }
}
