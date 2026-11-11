package com.example.app_1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Existing pages use white surfaces. Keep controls legible under both device settings.
private val AppColorScheme = lightColorScheme(
    primary = LostFoundBlue,
    onPrimary = Color.White,
    background = LostFoundPageBackground,
    onBackground = LostFoundNavy,
    surface = Color.White,
    onSurface = LostFoundNavy,
    onSurfaceVariant = Color(0xFF475569),
    outline = LostFoundBorder,
)

@Composable
fun App_1Theme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColorScheme, typography = Typography, content = content)
}
