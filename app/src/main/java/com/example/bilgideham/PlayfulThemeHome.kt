package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random
import androidx.compose.foundation.layout.statusBarsPadding

/**
 * Playful Theme UI Components for HomeScreen
 * Fun and colorful design for kids
 */


// ==================== EĞLENCELİ ARAYÜZ (Çocuklar için) ====================

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
    // Premium Aurora Gradient Animation
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    // Breathing glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    // Premium gradient colors - aurora borealis style
    val auroraGradient = Brush.linearGradient(
        colors = listOf(
            Color.hsv((phase + 200f) % 360f, 0.65f, 0.85f),      // Deep blue-purple
            Color.hsv((phase + 260f) % 360f, 0.55f, 0.90f),      // Purple-pink
            Color.hsv((phase + 320f) % 360f, 0.50f, 0.95f),      // Pink-coral
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
        // Floating orbs background
        PremiumFloatingOrbs(glowAlpha)
        // Glass overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent, Color.Black.copy(alpha = 0.05f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            // Top action bar
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
            // Welcome section with premium typography
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
                    // Animated wave emoji
                    val waveRotation by infiniteTransition.animateFloat(
                        initialValue = -10f,
                        targetValue = 10f,
                        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                        label = "wave"
                    )
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
        modifier = Modifier.size(48.dp).scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.blur(if (isPressed) 0.dp else 0.dp)
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun PremiumFloatingOrbs(glowAlpha: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    // Multiple floating orbs with different speeds
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
            animationSpec = infiniteRepeatable(tween(duration, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "orbX$i"
        )
        val offsetY by infiniteTransition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(tween((duration * 0.8).toInt(), easing = FastOutSlowInEasing), RepeatMode.Reverse),
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

// Keep the old function for compatibility but it's now unused
@Composable
fun GlassyIconButton(icon: ImageVector, onClick: () -> Unit) {
    PremiumGlassButton(icon = icon, onClick = onClick)
}

@Composable
fun PlayfulBackgroundBubbles() {
    // Simplified - now using PremiumFloatingOrbs instead
    PremiumFloatingOrbs(0.4f)
}


@Composable
fun PlayfulHomeContent(
    padding: PaddingValues,
    navController: NavController,
    context: Context,
    darkMode: Boolean,
    solvedToday: Int,
    dailyTarget: Int,
    randomMessage: String,
    randomPunch: String
) {
    val cs = MaterialTheme.colorScheme
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val subjects = remember(educationPrefs) { AppPrefs.getCurrentSubjects(context) }

    // Modern, temiz arka plan
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // İlerleme Alanı
            PlayfulProgressCard(solved = solvedToday, target = dailyTarget, darkMode = darkMode)

            // Ders Kartları Grid - Ana Dersler
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Normal dersler (İmam Hatip dersleri ve Paragraf hariç - Paragraf hızlı erişimde var)
                val normalSubjects = subjects.filter { subject ->
                    !subject.id.startsWith("arapca") && 
                    !subject.id.startsWith("kuran") && 
                    !subject.id.startsWith("siyer") &&
                    !subject.id.contains("paragraf", ignoreCase = true)
                }
                val imamHatipSubjects = subjects.filter { subject ->
                    subject.id.startsWith("arapca") || subject.id.startsWith("kuran") || subject.id.startsWith("siyer")
                }
                normalSubjects.chunked(2).forEach { rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSubjects.forEach { subject ->
                            PlayfulSubjectCard(
                                subject = subject,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(subject.route) }
                            )
                        }
                        if (rowSubjects.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                // İmam Hatip Bölümü - Oval çerçeve içinde
                if (imamHatipSubjects.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Kartları çevreleyen ince oval border
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.8.dp,
                                    color = cs.onBackground.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(12.dp)
                                .padding(top = 8.dp)
                        ) {
                            imamHatipSubjects.chunked(2).forEach { rowSubjects ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowSubjects.forEach { subject ->
                                        PlayfulSubjectCard(
                                            subject = subject,
                                            modifier = Modifier.weight(1f),
                                            onClick = { navController.navigate(subject.route) }
                                        )
                                    }
                                    if (rowSubjects.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                        // "İmam Hatip" etiketi - üst ortada
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-8).dp)
                                .background(if (darkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "İmam Hatip",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = cs.onBackground.copy(alpha = 0.4f),
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                // Hızlı Aksiyonlar (Renkli Butonlar)
                Text(
                    "Hızlı Görevler",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onBackground.copy(0.8f),
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
                
                val showParagraf = educationPrefs.schoolType != SchoolType.AGS_OABT || AppPrefs.getOabtField(context) == "turkce"
                
                if (showParagraf) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlayfulActionButton(
                            emoji = "📖",
                            title = "Paragraf",
                            subtitle = "Kitap Kurdu",
                            color = Color(0xFF26C6DA), // Cyan
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("paragraph_practice_screen") }
                        PlayfulActionButton(
                            emoji = "📝",
                            title = "Deneme",
                            subtitle = "Sınav Provası",
                            color = Color(0xFFFFA726), // Orange
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("practice_exam_screen") }
                    }
                } else {
                    // Paragraf gizliyse, geniş Deneme kartı göster
                    WideExamCardPlayful { navController.navigate("practice_exam_screen") }
                }
                Spacer(Modifier.height(12.dp))
                // Düello Banner (Geniş) - KPSS ve AGS hariç
                if (educationPrefs.level != EducationLevel.KPSS && educationPrefs.level != EducationLevel.AGS) {
                    PlayfulDuelBanner { navController.navigate("class_duel") }
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}


@Composable
fun PlayfulProgressCard(solved: Int, target: Int, darkMode: Boolean) {
    val progress = (solved.toFloat() / target).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "circularProgress")
    // Pulsing glow for the ring
    val infiniteTransition = rememberInfiniteTransition(label = "progressGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    // Premium gradient for progress ring
    val ringGradient = listOf(
        Color(0xFFFF6B6B),  // Coral red
        Color(0xFFFFE66D),  // Yellow
        Color(0xFF4ECDC4),  // Teal
        Color(0xFF45B7D1),  // Sky blue
        Color(0xFFFF6B6B)   // Back to coral
    )
    Card(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = if (darkMode) Color(0xFF1E293B) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (darkMode) 0.dp else 8.dp),
        border = if (darkMode) BorderStroke(1.dp, Color.White.copy(0.08f)) else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Circular Progress
            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                // Background glow
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Background ring
                Canvas(modifier = Modifier.size(85.dp)) {
                    drawArc(
                        color = if (darkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
                // Progress ring with gradient
                Canvas(modifier = Modifier.size(85.dp)) {
                    val sweepAngle = animatedProgress * 360f
                    drawArc(
                        brush = Brush.sweepGradient(ringGradient),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
                // Center content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (darkMode) Color.White else Color(0xFF2D3436)
                    )
                }
            }
            Spacer(Modifier.width(24.dp))
            // Right Side: Stats & Motivation
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GÜNLÜK HEDEF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = (if (darkMode) Color.White else Color.Black).copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$solved",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (darkMode) Color.White else Color(0xFF2D3436)
                    )
                    Text(
                        text = "/$target",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = (if (darkMode) Color.White else Color.Black).copy(alpha = 0.35f),
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Motivation badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        progress >= 1f -> Color(0xFF4ECDC4).copy(alpha = 0.15f)
                        progress >= 0.5f -> Color(0xFFFFE66D).copy(alpha = 0.15f)
                        else -> Color(0xFFFF6B6B).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when {
                            progress >= 1f -> "🏆 Harika! Tamamlandı!"
                            progress >= 0.7f -> "🔥 Az kaldı!"
                            progress >= 0.3f -> "💪 Devam et!"
                            else -> "🚀 Haydi başla!"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            progress >= 1f -> Color(0xFF4ECDC4)
                            progress >= 0.5f -> Color(0xFFE6A23C)
                            else -> Color(0xFFFF6B6B)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun PlayfulSubjectCard(subject: SubjectConfig, modifier: Modifier, onClick: () -> Unit) {
    val subjectColor = Color(subject.colorHex)
    val darkSubjectColor = subjectColor.copy(alpha = 0.9f)
    // Press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, tween(100), label = "cardScale")
    Box(
        modifier = modifier
            .height(160.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        subjectColor.copy(alpha = 0.15f),
                        subjectColor.copy(alpha = 0.08f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(200f, 200f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        subjectColor.copy(alpha = 0.3f),
                        subjectColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        // Subtle glow effect at top-left
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = (-20).dp, y = (-20).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            subjectColor.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon container with glass effect
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(subject.icon, fontSize = 26.sp)
            }
            // Text section
            Column {
                val fontSize = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 13.sp else 17.sp
                val lineHeight = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 16.sp else 21.sp
                
                Text(
                    text = subject.displayName,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    color = darkSubjectColor,
                    maxLines = 2,
                    lineHeight = lineHeight
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Çalışmaya Başla",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = subjectColor.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        null,
                        tint = subjectColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayfulActionButton(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, tween(100), label = "btnScale")
    Box(
        modifier = modifier
            .height(95.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = 0.18f),
                        color.copy(alpha = 0.08f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(200f, 100f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.1f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji container with subtle background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color.copy(alpha = 0.65f)
                )
            }
        }
    }
}


@Composable
fun PlayfulDuelBanner(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "duelPulse")
    // Pulse animation for glow
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    // Gradient shift animation
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "gradient"
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (isPressed) 0.97f else 1f, tween(100), label = "duelPress")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(pressScale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667EEA),  // Indigo
                        Color(0xFF764BA2),  // Purple
                        Color(0xFFF953C6)   // Pink
                    ),
                    start = Offset(gradientOffset, 0f),
                    end = Offset(gradientOffset + 400f, 200f)
                )
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        // Glow overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(100f, 50f),
                        radius = 200f
                    )
                )
        )
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji with glow
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⚔️", fontSize = 28.sp)
            }
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "DÜELLO ZAMANI!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    "Arkadaşınla yarış, kazanan sen ol!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WideExamCardPlayful(
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .scale(scale)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFA726).copy(alpha = 0.2f),
                        Color(0xFFFFD54F).copy(alpha = 0.15f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 300f)
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFA726).copy(alpha = 0.4f),
                        Color(0xFFFFD54F).copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-30).dp, y = (-30).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFA726).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf - Emoji ve İkon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.4f)
                            )
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📝", fontSize = 48.sp)
            }
            
            Spacer(Modifier.width(20.dp))
            
            // Sağ taraf - Bilgiler
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Deneme Sınavı",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = cs.onSurface,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Gerçek sınav deneyimi",
                    fontSize = 14.sp,
                    color = cs.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFA726).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "⏱️ Zamanlı",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD54F).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "📊 Analiz",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            // Sağ ok
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFA726).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.ArrowForward,
                    null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
