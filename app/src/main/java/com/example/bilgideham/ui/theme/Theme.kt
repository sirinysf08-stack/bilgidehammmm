package com.example.bilgideham.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeId {
    ROYAL_ACADEMY,
    CYBER_FUTURE,
    HIDDEN_FOREST,
    SUNSET_LOFI,
    HIGH_ENERGY,
    FAIRY_TALE
}

/**
 * Kurumsal hedef:
 * - Dark mode "soft dark" ve okunabilir olmalı (arka plan kömür ton, kartlar ayrışır).
 * - Tema kimliği (primary/secondary) korunmalı; karanlık modda sadece zemin ve metin dengeleri iyileştirilir.
 */
@Composable
fun BilgidehamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeId: AppThemeId = AppThemeId.ROYAL_ACADEMY,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val lightScheme = when (themeId) {
        AppThemeId.ROYAL_ACADEMY -> lightColorScheme(
            primary = RoyalPrimary,
            onPrimary = Color.White,
            secondary = RoyalAccent,
            onSecondary = Color.Black,
            background = RoyalBg,
            onBackground = Color(0xFF101418),
            surface = RoyalCard,
            onSurface = Color(0xFF101418),
            surfaceVariant = Color(0xFFF1F3F6),
            onSurfaceVariant = Color(0xFF2A3138),
            outline = Color(0xFFCED6E0)
        )

        AppThemeId.CYBER_FUTURE -> lightColorScheme(
            primary = SpacePrimary,
            onPrimary = Color.Black,
            secondary = SpaceAccent,
            onSecondary = Color.Black,
            background = Color(0xFFF7F7FB),
            onBackground = Color(0xFF111318),
            surface = Color.White,
            onSurface = Color(0xFF111318),
            surfaceVariant = Color(0xFFF1F1F7),
            onSurfaceVariant = Color(0xFF2A2A33),
            outline = Color(0xFFCFCFDD)
        )

        AppThemeId.HIDDEN_FOREST -> lightColorScheme(
            primary = ForestPrimary,
            onPrimary = Color.White,
            secondary = ForestAccent,
            onSecondary = Color.White,
            background = ForestBg,
            onBackground = Color(0xFF101418),
            surface = ForestCard,
            onSurface = Color(0xFF101418),
            surfaceVariant = Color(0xFFEFF5EE),
            onSurfaceVariant = Color(0xFF2A3130),
            outline = Color(0xFFCFE0D0)
        )

        AppThemeId.SUNSET_LOFI -> lightColorScheme(
            primary = SunsetPrimary,
            onPrimary = Color.White,
            secondary = SunsetAccent,
            onSecondary = Color.Black,
            background = SunsetBg,
            onBackground = Color(0xFF101418),
            surface = SunsetCard,
            onSurface = Color(0xFF101418),
            surfaceVariant = Color(0xFFFFF1EA),
            onSurfaceVariant = Color(0xFF2A3138),
            outline = Color(0xFFE5D1C8)
        )

        AppThemeId.HIGH_ENERGY -> lightColorScheme(
            primary = EnergyPrimary,
            onPrimary = Color.White,
            secondary = EnergyAccent,
            onSecondary = Color.Black,
            background = EnergyBg,
            onBackground = Color(0xFF101418),
            surface = EnergyCard,
            onSurface = Color(0xFF101418),
            surfaceVariant = Color(0xFFE9F0FF),
            onSurfaceVariant = Color(0xFF2A3138),
            outline = Color(0xFFCDD8FF)
        )

        AppThemeId.FAIRY_TALE -> lightColorScheme(
            primary = PrincessPrimary,
            onPrimary = Color.White,
            secondary = PrincessAccent,
            onSecondary = Color.Black,
            background = PrincessBg,
            onBackground = Color(0xFF101418),
            surface = PrincessCard,
            onSurface = Color(0xFF101418),
            surfaceVariant = Color(0xFFFFEDF3),
            onSurfaceVariant = Color(0xFF2A3138),
            outline = Color(0xFFE8C8D6)
        )
    }

    // Soft dark dönüşümü: primer/sekonder kimliği koru, zemin/kart/metin kontrastını normalize et.
    val colorScheme = if (darkTheme) {
        lightScheme.toSoftDark(themeId)
    } else {
        // Cyber Future isterse aydınlık da kullanılabilir; kullanıcı dark mode açınca karanlığa geçer.
        lightScheme
    }

    // Status bar: darkTheme açıkken ikonlar açık (light status bar false), aydınlıkken koyu ikonlar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun ColorScheme.toSoftDark(themeId: AppThemeId): ColorScheme {
    // Base soft-dark palette (çok siyah değil, kömür ton).
    val bg = Color(0xFF0F1218)
    val surface = Color(0xFF141A23)
    val surface2 = Color(0xFF1A2230)
    val surface3 = Color(0xFF202A3A)

    val on = Color(0xFFECEFF1)
    val on2 = Color(0xFFBAC3CF)

    // Tema kimliği: primary/secondary korunur, ama koyu zeminde göz yormasın diye az yumuşatılır.
    val p = primary.slightlySoftOnDark(themeId)
    val s = secondary.slightlySoftSecondaryOnDark(themeId)

    return darkColorScheme(
        primary = p,
        onPrimary = Color(0xFF081018),
        secondary = s,
        onSecondary = Color(0xFF081018),

        background = bg,
        onBackground = on,

        surface = surface,
        onSurface = on,

        surfaceVariant = surface2,
        onSurfaceVariant = on2,

        outline = Color(0xFF334155),
        outlineVariant = Color(0xFF263244),

        tertiary = p,
        onTertiary = Color(0xFF081018),

        error = Color(0xFFFF6B6B),
        onError = Color(0xFF2B0B0B)
    ).copy(
        // Ek yüzey katmanları (özellikle Card/Chip) için daha stabil görünüm
        inverseSurface = Color(0xFFE9EEF5),
        inverseOnSurface = Color(0xFF101418),
        surfaceTint = p
    )
}

private fun Color.slightlySoftOnDark(themeId: AppThemeId): Color {
    // Koyu modda primeri hafif yumuşat (özellikle çok parlak mavi/sarı patlamasın).
    // Bu fonksiyon "renk bilimi" iddiası taşımaz; pratik UI dengesi amaçlar.
    return when (themeId) {
        AppThemeId.HIGH_ENERGY -> this.copy(alpha = 0.92f)
        AppThemeId.SUNSET_LOFI -> this.copy(alpha = 0.95f)
        AppThemeId.ROYAL_ACADEMY -> this.copy(alpha = 0.96f)
        AppThemeId.HIDDEN_FOREST -> this.copy(alpha = 0.96f)
        AppThemeId.FAIRY_TALE -> this.copy(alpha = 0.95f)
        AppThemeId.CYBER_FUTURE -> this.copy(alpha = 0.98f)
    }
}

private fun Color.slightlySoftSecondaryOnDark(themeId: AppThemeId): Color {
    return when (themeId) {
        AppThemeId.HIGH_ENERGY -> this.copy(alpha = 0.90f)
        AppThemeId.SUNSET_LOFI -> this.copy(alpha = 0.92f)
        AppThemeId.ROYAL_ACADEMY -> this.copy(alpha = 0.92f)
        AppThemeId.HIDDEN_FOREST -> this.copy(alpha = 0.92f)
        AppThemeId.FAIRY_TALE -> this.copy(alpha = 0.92f)
        AppThemeId.CYBER_FUTURE -> this.copy(alpha = 0.96f)
    }
}
