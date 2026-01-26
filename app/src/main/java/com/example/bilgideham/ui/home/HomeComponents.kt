package com.example.bilgideham.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random

/**
 * HomeScreen için ortak UI bileşenleri
 * Tüm arayüz stilleri (Modern, Playful, Classic) tarafından kullanılır
 */

// ==================== YILDIZ TOZU EFEKTİ ====================

@Composable
fun HomeStarDustEffect(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "starDust")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val starCount = 30
        for (i in 0 until starCount) {
            val seed = i * 137
            val x = ((seed * 7) % size.width.toInt()).toFloat()
            val y = ((seed * 13 + phase.toInt()) % size.height.toInt()).toFloat()
            val radius = (2f + (seed % 3))
            val alpha = 0.3f + (seed % 5) * 0.1f
            drawCircle(color.copy(alpha = alpha), radius, Offset(x, y))
        }
    }
}

// ==================== PREMIUM FLOATING ORBS ====================

@Composable
fun PremiumFloatingOrbs(glowAlpha: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    
    val orbConfigs = remember {
        listOf(
            Triple(Alignment.TopStart, 120.dp, 4000),
            Triple(Alignment.TopEnd, 80.dp, 5000),
            Triple(Alignment.BottomStart, 60.dp, 3500),
            Triple(Alignment.BottomEnd, 100.dp, 4500),
            Triple(Alignment.Center, 140.dp, 6000)
        )
    }

    orbConfigs.forEachIndexed { i, (alignment, size, duration) ->
        val offsetX by infiniteTransition.animateFloat(
            initialValue = -20f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                tween(duration, easing = FastOutSlowInEasing), RepeatMode.Reverse
            ),
            label = "orbX$i"
        )
        val offsetY by infiniteTransition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                tween((duration * 0.8).toInt(), easing = FastOutSlowInEasing), RepeatMode.Reverse
            ),
            label = "orbY$i"
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = alignment) {
            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = glowAlpha * 0.4f),
                                Color.White.copy(alpha = glowAlpha * 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

// ==================== PREMIUM GLASS BUTTON ====================

@Composable
fun PremiumGlassButton(icon: ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, tween(100), label = "scale")

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

// ==================== DÜELLO BANNER ====================

@Composable
fun DuelBannerModern(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "duel")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(pulse)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF7C4DFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚔️", fontSize = 36.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Sınıf Düellosu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Arkadaşlarınla yarış!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ==================== QUICK ACTION CARD ====================

@Composable
fun QuickActionCardModern(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
                Text(subtitle, fontSize = 11.sp, color = cs.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

// ==================== GRADIENT HELPER ====================

fun resolveTopGradient(cs: ColorScheme, darkMode: Boolean): Brush {
    return if (darkMode) {
        Brush.linearGradient(
            colors = listOf(
                cs.primary.copy(alpha = 0.9f),
                cs.primaryContainer.copy(alpha = 0.8f),
                cs.secondary.copy(alpha = 0.7f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                cs.primary,
                cs.primaryContainer,
                cs.secondary
            )
        )
    }
}

// ==================== NAVIGATION HELPER ====================

fun NavController.safeNavigateDeferred(route: String, context: Context, maxRetry: Int = 3) {
    try {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    } catch (e: Exception) {
        Log.e("NAV_SAFE", "Navigate başarısız. route=$route err=${e.message}", e)
        Toast.makeText(context, "Sayfa yüklenemedi, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
    }
}

// ==================== BRAND TITLE HELPER ====================

fun readBrandTitle(context: Context): String {
    val prefs = context.getSharedPreferences("bilgideham_prefs", Context.MODE_PRIVATE)
    val name = (prefs.getString("student_name", "") ?: "").trim()
    val style = prefs.getString("brand_style", "Küpü") ?: "Küpü"

    if (name.isEmpty()) return "Akıl Küpü"

    val vowels = setOf('a', 'e', 'ı', 'i', 'o', 'ö', 'u', 'ü', 'A', 'E', 'I', 'İ', 'O', 'Ö', 'U', 'Ü')
    val suffix = if (vowels.contains(name.last())) "'nın" else "'in"

    return "$name$suffix $style"
}
