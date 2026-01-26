package com.example.bilgideham.ui.home

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilgideham.SubjectConfig
import com.example.bilgideham.ui.theme.InterfaceParams
import com.example.bilgideham.ui.theme.LocalInterfaceStyle

/**
 * HomeScreen Kart Bileşenleri
 * AI Hero Card, Progress Card, Subject Card, Action Card vb.
 */

// ==================== AI GLASS HERO CARD ====================

@Composable
fun AIGlassHeroCard(
    message: String,
    solved: Int,
    target: Int,
    punchWord: String,
    darkMode: Boolean
) {
    val progress = (solved.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(44.dp))
            .background(
                Brush.linearGradient(
                    if (darkMode) listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                    else listOf(cs.primary, cs.tertiary, cs.primaryContainer)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(44.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = 250.dp.toPx()
                ),
                radius = 250.dp.toPx(),
                center = Offset(size.width, 0f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00E5FF)).blur(3.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "YAPAY ZEKA HER AN YANINDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = message,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                Text(
                    text = punchWord,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(0.12f),
                    modifier = Modifier.offset(y = (-8).dp)
                )

                Spacer(Modifier.height(14.dp))
                ModernTargetBar(progress, solved, target)
            }

            Box(modifier = Modifier.size(140.dp).padding(start = 16.dp)) {
                NeuralBrainAIView()
            }
        }
    }
}

// ==================== MODERN TARGET BAR ====================

@Composable
fun ModernTargetBar(progress: Float, solved: Int, target: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2200, easing = FastOutSlowInEasing),
        label = "targetProgress"
    )

    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00E5FF)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "HEDEF: $target SORU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFFD600))
                        )
                    )
            )
        }

        Text(
            text = "Bugün $solved Soru Çözdün",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 10.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== NEURAL BRAIN AI VIEW ====================

@Composable
fun NeuralBrainAIView() {
    val infiniteTransition = rememberInfiniteTransition(label = "brainSystem")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "brainPulse"
    )

    val networkRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "brainRotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "brainGlow"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.45f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00E5FF).copy(0.35f * glowAlpha), Color.Transparent),
                center = center,
                radius = radius * 1.8f
            ),
            radius = radius * 1.8f,
            center = center
        )

        withTransform({
            rotate(networkRotation, center)
            scale(pulseScale, pulseScale, center)
        }) {
            val nodes = listOf(
                Offset(center.x - radius * 0.6f, center.y - radius * 0.4f),
                Offset(center.x + radius * 0.6f, center.y - radius * 0.3f),
                Offset(center.x - radius * 0.3f, center.y + radius * 0.6f),
                Offset(center.x + radius * 0.4f, center.y + radius * 0.5f),
                Offset(center.x, center.y - radius * 0.8f),
                Offset(center.x, center.y + radius * 0.2f),
                Offset(center.x - radius * 0.5f, center.y + radius * 0.1f)
            )

            nodes.forEachIndexed { i, start ->
                nodes.forEachIndexed { j, end ->
                    if (i < j) {
                        drawLine(
                            color = Color(0xFF00E5FF).copy(0.4f),
                            start = start,
                            end = end,
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
            }

            nodes.forEach { node ->
                drawCircle(Color.White, radius = 3.5.dp.toPx(), center = node)
                drawCircle(
                    color = Color(0xFF00E5FF).copy(glowAlpha),
                    radius = 7.dp.toPx(),
                    center = node,
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFD500F9).copy(0.8f), Color.Transparent),
                radius = 20.dp.toPx()
            ),
            radius = 22.dp.toPx() * pulseScale,
            center = center
        )
    }
}


// ==================== DYNAMIC LESSON CARD ====================

@Composable
fun DynamicLessonCard(
    subject: SubjectConfig,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val subjectColor = Color(subject.colorHex)
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp

    val icon = getIconForSubject(subject.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(cornerRadius * 0.8f))
                    .background(subjectColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                if (subject.icon.length <= 2) {
                    Text(subject.icon, fontSize = 28.sp)
                } else {
                    Icon(icon, null, tint = subjectColor, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.width(22.dp))

            Column(modifier = Modifier.weight(1f)) {
                val fontSize = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 16.sp else 21.sp
                
                Text(
                    text = subject.displayName,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )
                Text(
                    text = subject.description,
                    fontSize = 13.sp,
                    color = cs.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = cs.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ==================== PLAYFUL PROGRESS CARD ====================

@Composable
fun PlayfulProgressCard(solved: Int, target: Int, darkMode: Boolean) {
    val progress = (solved.toFloat() / target).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress, 
        animationSpec = tween(1500, easing = FastOutSlowInEasing), 
        label = "circularProgress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "progressGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val ringGradient = listOf(
        Color(0xFFFF6B6B),
        Color(0xFFFFE66D),
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFFFF6B6B)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkMode) Color(0xFF1E293B) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (darkMode) 0.dp else 8.dp),
        border = if (darkMode) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.08f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
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
                
                Canvas(modifier = Modifier.size(85.dp)) {
                    drawArc(
                        color = if (darkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Canvas(modifier = Modifier.size(85.dp)) {
                    val sweepAngle = animatedProgress * 360f
                    drawArc(
                        brush = Brush.sweepGradient(ringGradient),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
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


// ==================== PLAYFUL SUBJECT CARD ====================

@Composable
fun PlayfulSubjectCard(subject: SubjectConfig, modifier: Modifier, onClick: () -> Unit) {
    val subjectColor = Color(subject.colorHex)
    val darkSubjectColor = subjectColor.copy(alpha = 0.9f)
    
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

            Column {
                Text(
                    text = subject.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = darkSubjectColor,
                    maxLines = 2,
                    lineHeight = 16.sp
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

// ==================== PLAYFUL ACTION BUTTON ====================

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

// ==================== PLAYFUL DUEL BANNER ====================

@Composable
fun PlayfulDuelBanner(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "duelPulse")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    
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
                        Color(0xFF667EEA),
                        Color(0xFF764BA2),
                        Color(0xFFF953C6)
                    ),
                    start = Offset(gradientOffset, 0f),
                    end = Offset(gradientOffset + 400f, 200f)
                )
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
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


// ==================== CLASSIC SUBJECT ITEM ====================

@Composable
fun ClassicSubjectItem(subject: SubjectConfig, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val subjectColor = Color(subject.colorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = subjectColor.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(subject.icon, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            val fontSize = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 13.sp else 15.sp
            
            Text(subject.displayName, fontSize = fontSize, fontWeight = FontWeight.Medium, color = cs.onSurface)
            Text(subject.description, fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.5f), maxLines = 1)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = cs.onSurface.copy(alpha = 0.3f))
    }
}

// ==================== CLASSIC QUICK BUTTON ====================

@Composable
fun ClassicQuickButton(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    OutlinedCard(onClick = onClick, modifier = modifier.height(70.dp), shape = RoundedCornerShape(8.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = cs.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, color = cs.onSurface)
        }
    }
}

// ==================== PAST EXAM CARD ====================

@Composable
fun PastExamCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            cs.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 28.sp)
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurface.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==================== HELPER FUNCTIONS ====================

fun getIconForSubject(subjectId: String): ImageVector {
    val id = subjectId.lowercase()
    return when {
        id.contains("turkce") || id.contains("turk_dili") -> Icons.Rounded.AutoStories
        id.contains("matematik") || id.contains("math") -> Icons.Rounded.Functions
        id.contains("fen") || id.contains("fizik") || id.contains("kimya") || id.contains("biyoloji") -> Icons.Rounded.Science
        id.contains("sosyal") || id.contains("tarih") || id.contains("cografya") -> Icons.Rounded.Public
        id.contains("ingilizce") || id.contains("english") -> Icons.Rounded.Language
        id.contains("arapca") || id.contains("kuran") -> Icons.Rounded.Translate
        id.contains("din") || id.contains("siyer") || id.contains("hadis") || id.contains("fikih") -> Icons.Rounded.HistoryEdu
        id.contains("felsefe") || id.contains("mantik") -> Icons.Rounded.Psychology
        id.contains("sosyoloji") || id.contains("psikoloji") -> Icons.Rounded.Groups
        id.contains("paragraf") -> Icons.AutoMirrored.Rounded.MenuBook
        id.contains("deneme") || id.contains("tyt") || id.contains("ayt") || id.contains("lgs") || id.contains("kpss") -> Icons.AutoMirrored.Rounded.Assignment
        id.contains("hayat") -> Icons.Rounded.Explore
        id.contains("meslek") || id.contains("atolye") -> Icons.Rounded.Build
        id.contains("egitim") || id.contains("rehberlik") -> Icons.Rounded.School
        id.contains("vatandaslik") || id.contains("guncel") -> Icons.Rounded.Newspaper
        else -> Icons.Rounded.Book
    }
}
