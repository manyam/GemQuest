package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = QuestPurpleRadiant, // Bright, vivid purple that pops on black
    onPrimary = Color(0xFF1E0A40),
    primaryContainer = Color(0xFF4A2A85),
    onPrimaryContainer = Color(0xFFEDE3FF),
    secondary = QuestGoldAccent,
    onSecondary = Color(0xFF261900),
    secondaryContainer = Color(0xFF473300),
    onSecondaryContainer = Color(0xFFFFDE9F),
    tertiary = QuestEmeraldGreen,
    onTertiary = Color(0xFF003826),
    background = QuestBackgroundDark,
    onBackground = TextPrimaryLight,
    surface = QuestSurfaceDark,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFF2C2740),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFF5D5478)
)

private val LightColorScheme = lightColorScheme(
    primary = QuestIndigoPrimary, // Deep bold purple for light theme
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE5FF),
    onPrimaryContainer = Color(0xFF2A0868),
    secondary = QuestGoldAccent,
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFFFFF3D4),
    onSecondaryContainer = Color(0xFF573B00),
    tertiary = QuestEmeraldGreen,
    onTertiary = Color.White,
    background = QuestBackgroundLight,
    onBackground = TextPrimaryDark,
    surface = QuestSurfaceLight,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFEDEBF5),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFFCAC5DC)
)

@Composable
fun GemQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
