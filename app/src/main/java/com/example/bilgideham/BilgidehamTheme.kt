package com.example.bilgideham

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),      // canlı indigo
    secondary = Color(0xFFEC4899),    // canlı pembe
    tertiary = Color(0xFF22C55E),     // canlı yeşil
    background = Color(0xFFF6F7FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF818CF8),
    secondary = Color(0xFFF472B6),
    tertiary = Color(0xFF4ADE80),
    background = Color(0xFF0B1220),
    surface = Color(0xFF101A2E),
    onPrimary = Color(0xFF0B1220),
    onSecondary = Color(0xFF0B1220),
    onTertiary = Color(0xFF0B1220),
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB)
)

@Composable
fun BilgidehamTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
