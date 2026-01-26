package com.example.bilgideham.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Segment
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.statusBarsPadding

/**
 * HomeScreen Header Bileşenleri
 * Modern, Playful ve Classic arayüz stilleri için header composable'ları
 */

// ==================== MODERN HEADER ====================

@Composable
fun ModernHeader(
    brandTitle: String,
    darkMode: Boolean,
    onMenuClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleBrightness: () -> Unit,
    currentBrightness: Int,
    onSecretTap: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(brush = resolveTopGradient(cs = cs, darkMode = darkMode))
    ) {
        HomeStarDustEffect(color = Color.White.copy(alpha = 0.28f))
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.AutoMirrored.Rounded.Segment, "Menu", tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(6.dp))
            AnimatedBrandWordmark(
                modifier = Modifier.weight(1f),
                darkMode = darkMode,
                compact = true,
                brandTitle = brandTitle,
                onSecretTap = onSecretTap
            )
            IconButton(onClick = onToggleTheme) {
                Crossfade(targetState = darkMode, label = "themeAnim") { isDark ->
                    Icon(
                        if (isDark) Icons.Rounded.Bedtime else Icons.Rounded.WbSunny,
                        "Tema",
                        tint = if (isDark) Color(0xFFC5CAE9) else Color(0xFFFFEE58),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            IconButton(onClick = onToggleBrightness) {
                Icon(
                    when (currentBrightness) {
                        0 -> Icons.Rounded.BrightnessLow
                        1 -> Icons.Rounded.BrightnessMedium
                        else -> Icons.Rounded.BrightnessHigh
                    },
                    "Parlaklık",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ==================== PLAYFUL HEADER ====================

@Composable
fun PlayfulHeader(
    brandTitle: String,
    darkMode: Boolean,
    onMenuClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleBrightness: () -> Unit,
    currentBrightness: Int,
    onSecretTap: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val auroraGradient = Brush.linearGradient(
        colors = listOf(
            Color.hsv((phase + 200f) % 360f, 0.65f, 0.85f),
            Color.hsv((phase + 260f) % 360f, 0.55f, 0.90f),
            Color.hsv((phase + 320f) % 360f, 0.50f, 0.95f),
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 500f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
            .background(auroraGradient)
    ) {
        PremiumFloatingOrbs(glowAlpha)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PremiumGlassButton(icon = Icons.Rounded.Menu, onClick = onMenuClick)
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PremiumGlassButton(
                        icon = if (darkMode) Icons.Rounded.DarkMode else Icons.Rounded.WbSunny,
                        onClick = onToggleTheme
                    )
                    PremiumGlassButton(
                        icon = when (currentBrightness) {
                            0 -> Icons.Rounded.BrightnessLow
                            1 -> Icons.Rounded.BrightnessMedium
                            else -> Icons.Rounded.BrightnessHigh
                        },
                        onClick = onToggleBrightness
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column {
                Text(
                    text = "Merhaba,",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = brandTitle,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "👋",
                        fontSize = 28.sp,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Bugün harika şeyler öğrenmeye hazır mısın?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ==================== CLASSIC HEADER ====================

@Composable
fun ClassicHeader(
    brandTitle: String,
    darkMode: Boolean,
    onMenuClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleBrightness: () -> Unit,
    currentBrightness: Int,
    onSecretTap: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cs.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Rounded.Menu, "Menü", tint = cs.onSurface)
            }
            Text(
                brandTitle,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = cs.onSurface
            )
            IconButton(onClick = onToggleTheme) {
                Icon(
                    if (darkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    "Tema",
                    tint = cs.onSurface
                )
            }
            IconButton(onClick = onToggleBrightness) {
                Icon(Icons.Rounded.Brightness6, "Parlaklık", tint = cs.onSurface)
            }
        }
    }
}

// ==================== ANIMATED BRAND WORDMARK ====================

@Composable
fun AnimatedBrandWordmark(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    compact: Boolean,
    brandTitle: String,
    onSecretTap: () -> Unit = {}
) {
    val titleSize = if (compact) 30.sp else 38.sp
    val aiSize = if (compact) 20.sp else 26.sp

    val infiniteTransition = rememberInfiniteTransition(label = "shimmerSystem")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(0.7f), Color.White, Color.White.copy(0.7f)),
        start = Offset(shimmerOffset * 1800f - 900f, 0f),
        end = Offset(shimmerOffset * 1800f, 400f)
    )

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = brandTitle,
                fontSize = titleSize,
                fontWeight = FontWeight.Black,
                style = TextStyle(brush = shimmerBrush),
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSecretTap() }
                    .background(Color.White.copy(0.18f))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
                    .border(1.2.dp, Color.White.copy(0.3f), CircleShape)
            ) {
                Text(
                    text = "AI",
                    fontSize = aiSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.5.sp
                )
            }
        }
        if (!compact) {
            Text(
                text = "Cebindeki Bilge Yapay Zeka.",
                fontSize = 17.sp,
                color = Color.White.copy(0.85f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp, top = 6.dp)
            )
        }
    }
}
