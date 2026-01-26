package com.example.bilgideham.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Color.kt'den renk importları (aynı pakette olsa da açık import)
// RoyalPrimary, RoyalAccent, RoyalBg, RoyalCard vb. Color.kt'de tanımlı

enum class AppThemeId {
    ROYAL_ACADEMY,
    CYBER_FUTURE,
    HIDDEN_FOREST,
    SUNSET_LOFI,
    HIGH_ENERGY,
    FAIRY_TALE
}

/**
 * Arayüz stili için CompositionLocal
 * Tüm uygulama genelinde erişilebilir
 */
val LocalInterfaceStyle = staticCompositionLocalOf { InterfaceStyle.MODERN }

/**
 * Arayüz stiline göre dinamik şekiller
 */
@Composable
fun getInterfaceShapes(style: InterfaceStyle): Shapes {
    val cornerRadius = InterfaceParams.getCornerRadius(style).dp
    return Shapes(
        extraSmall = RoundedCornerShape(cornerRadius / 3),
        small = RoundedCornerShape(cornerRadius / 2),
        medium = RoundedCornerShape(cornerRadius),
        large = RoundedCornerShape(cornerRadius * 1.25f),
        extraLarge = RoundedCornerShape(cornerRadius * 1.5f)
    )
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
    themeColor: ThemeColor? = null, // Yeni tema sistemi için
    interfaceStyle: InterfaceStyle? = null, // Yeni arayüz sistemi için
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Yeni tema sistemi kullanılıyorsa
    val colorScheme = if (themeColor != null) {
        buildColorSchemeFromThemeColor(themeColor, darkTheme)
    } else {
        // Eski tema sistemi
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

        if (darkTheme) {
            lightScheme.toSoftDark(themeId)
        } else {
            lightScheme
        }
    }

    // Status bar ikon rengi: darkTheme açıkken açık ikonlar, aydınlıkken koyu ikonlar
    // Not: statusBarColor artık deprecated - enableEdgeToEdge() ile yönetiliyor
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = if (interfaceStyle != null) getInterfaceShapes(interfaceStyle) else MaterialTheme.shapes,
        content = {
            CompositionLocalProvider(
                LocalInterfaceStyle provides (interfaceStyle ?: InterfaceStyle.MODERN)
            ) {
                content()
            }
        }
    )
}

/**
 * Yeni tema sistemi için ColorScheme oluştur
 */
private fun buildColorSchemeFromThemeColor(themeColor: ThemeColor, isDark: Boolean): ColorScheme {
    val primary = themeColor.getPrimaryColor(isDark)
    val secondary = themeColor.getSecondaryColor(isDark)
    val background = themeColor.getBackgroundColor(isDark)

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF081018),
            secondary = secondary,
            onSecondary = Color(0xFF081018),
            background = background,
            onBackground = Color(0xFFECEFF1),
            surface = Color(0xFF141A23),
            onSurface = Color(0xFFECEFF1),
            surfaceVariant = Color(0xFF1A2230),
            onSurfaceVariant = Color(0xFFBAC3CF),
            outline = Color(0xFF334155),
            tertiary = primary,
            onTertiary = Color(0xFF081018),
            error = Color(0xFFFF6B6B),
            onError = Color(0xFF2B0B0B)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            secondary = secondary,
            onSecondary = Color.Black,
            background = background,
            onBackground = Color(0xFF101418),
            surface = Color.White,
            onSurface = Color(0xFF101418),
            surfaceVariant = background.copy(alpha = 0.7f),
            onSurfaceVariant = Color(0xFF2A3138),
            outline = Color(0xFFCED6E0),
            tertiary = primary,
            onTertiary = Color.White,
            error = Color(0xFFD32F2F),
            onError = Color.White
        )
    }
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
