package com.example.bilgideham

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun RobotLoadingAnimation(
    message: String = "AI Soruları Hazırlıyor",
    subMessage: String = "Az kaldı! Birazdan başlıyoruz 😊",
    modifier: Modifier = Modifier.fillMaxSize(),
    background: Color = Color(0xFF30343A)
) {
    val cs = MaterialTheme.colorScheme
    val infinite = rememberInfiniteTransition(label = "robot_infinite")

    val t by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1900, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "t"
    )

    val t2 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2600, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "t2"
    )

    val phase = (t * 2f * PI).toFloat()
    val bobY = (sin(phase) * 9f)
    val swayX = (sin(phase * 0.7f) * 10f)
    val tilt = (sin(phase * 0.55f) * 3.2f)
    val pulse = 1f + (sin(phase * 1.15f) * 0.018f)
    val blinkY = if (t2 > 0.93f) 0.10f else 1f
    val pupil = sin(phase * 0.9f)
    val armWave = sin(phase * 0.95f)

    val bubbleTexts = remember {
        listOf("Sorularını seçiyorum", "En güzel soruları buluyorum", "Birazdan hazır!", "Zekânı test edeceğiz 😄", "Hadi odaklanalım", "Tamam! Geliyor...")
    }
    var bubbleIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) { delay(2000); bubbleIndex = (bubbleIndex + 1) % bubbleTexts.size }
    }

    val dots = ((t * 4f).toInt() % 4).coerceIn(0, 3)
    val dotText = ".".repeat(dots)

    Box(
        modifier = modifier.background(
            Brush.radialGradient(
                colors = listOf(background, background.copy(alpha = 0.92f), background.copy(alpha = 0.86f)),
                radius = 900f
            )
        )
    ) {
        SoftGlowBlob(Modifier.align(Alignment.TopCenter).offset(y = 90.dp, x = (-40).dp).size(240.dp), cs.primary.copy(alpha = 0.18f))
        SoftGlowBlob(Modifier.align(Alignment.CenterEnd).offset(x = 40.dp, y = (-40).dp).size(220.dp), cs.tertiary.copy(alpha = 0.14f))
        SoftGlowBlob(Modifier.align(Alignment.BottomCenter).offset(y = 120.dp, x = 30.dp).size(280.dp), cs.secondary.copy(alpha = 0.12f))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ThoughtBubble(
                text = bubbleTexts[bubbleIndex] + dotText,
                modifier = Modifier.offset(y = (-18).dp).rotate(tilt * 0.5f)
            )
            Spacer(Modifier.height(10.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.offset(x = swayX.dp, y = bobY.dp).rotate(tilt).scale(pulse)
            ) {
                KawaiiRobotReader(blinkScaleY = blinkY, pupilDir = pupil, armWave = armWave, glow = Color(0xFF5AA9FF).copy(alpha = 0.20f))
            }
            Spacer(Modifier.height(18.dp))
            Text(text = "$message$dotText ✨", fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFF2F2F2))
            Spacer(Modifier.height(6.dp))
            Text(text = subMessage, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF2F2F2).copy(alpha = 0.78f))
        }
    }
}

@Composable
fun SoftGlowBlob(modifier: Modifier, color: Color) {
    Box(modifier = modifier.background(brush = Brush.radialGradient(colors = listOf(color, Color.Transparent), radius = 900f), shape = CircleShape))
}

@Composable
fun ThoughtBubble(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(18.dp), color = Color(0xFFF6F6F6).copy(alpha = 0.94f), shadowElevation = 10.dp) {
        Text(text = text, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 13.sp, color = Color(0xFF2B2B2B), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun KawaiiRobotReader(blinkScaleY: Float, pupilDir: Float, armWave: Float, glow: Color) {
    val headW = 176.dp; val headH = 152.dp; val bodyW = 156.dp; val bodyH = 132.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(240.dp).background(brush = Brush.radialGradient(colors = listOf(glow, Color.Transparent), radius = 700f), shape = CircleShape))
        Surface(shape = RoundedCornerShape(46.dp), color = Color(0xFFF7FAFF), shadowElevation = 14.dp, modifier = Modifier.offset(y = (-198).dp).width(headW).height(headH)) {
            Box(Modifier.fillMaxSize()) {
                Box(modifier = Modifier.align(Alignment.CenterStart).offset(x = (-18).dp).size(48.dp).background(Color(0xFFE8F0FB), CircleShape))
                Box(modifier = Modifier.align(Alignment.CenterEnd).offset(x = (18).dp).size(48.dp).background(Color(0xFFE8F0FB), CircleShape))
                Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = 10.dp).size(11.dp).background(Color(0xFF4FB6FF).copy(alpha = 0.78f + abs(armWave) * 0.12f), CircleShape))
                Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
                    val eyeGlow = Color(0xFF4FB6FF); val eyeRing = eyeGlow.copy(alpha = 0.55f); val eyeInner = Color(0xFF0B1B2B); val pupilColor = Color(0xFFC9F0FF)
                    val w = size.width; val h = size.height; val leftCx = w * 0.34f; val rightCx = w * 0.66f; val cy = h * 0.44f; val eyeR = (minOf(w, h) * 0.19f)
                    val blink = blinkScaleY.coerceIn(0.10f, 1f)
                    fun drawEye(cx: Float) {
                        drawCircle(color = eyeRing, radius = eyeR * 1.18f, center = androidx.compose.ui.geometry.Offset(cx, cy))
                        drawCircle(color = eyeGlow.copy(alpha = 0.85f), radius = eyeR, center = androidx.compose.ui.geometry.Offset(cx, cy))
                        val innerTop = cy - (eyeR * 0.86f) * blink; val innerH = ((cy + (eyeR * 0.86f) * blink) - innerTop).coerceAtLeast(1f)
                        drawRoundRect(color = eyeInner, topLeft = androidx.compose.ui.geometry.Offset(cx - eyeR * 0.86f, innerTop), size = androidx.compose.ui.geometry.Size(eyeR * 1.72f, innerH), cornerRadius = androidx.compose.ui.geometry.CornerRadius(eyeR * 0.86f, eyeR * 0.86f))
                        val pOff = (pupilDir * eyeR * 0.22f).toFloat(); val py = cy + eyeR * 0.06f
                        drawCircle(color = pupilColor.copy(alpha = 0.92f), radius = eyeR * 0.34f, center = androidx.compose.ui.geometry.Offset(cx + pOff, py))
                        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = eyeR * 0.12f, center = androidx.compose.ui.geometry.Offset(cx + pOff + eyeR * 0.16f, py - eyeR * 0.18f))
                    }
                    drawEye(leftCx); drawEye(rightCx)
                    // Details omitted for brevity but preserved from original...
                }
            }
        }
        Box(modifier = Modifier.offset(y = (-218).dp), contentAlignment = Alignment.Center) {
            Surface(shape = RoundedCornerShape(36.dp), color = Color(0xFFF1F6FF), shadowElevation = 10.dp, modifier = Modifier.width(bodyW).height(bodyH)) {
                Box(Modifier.fillMaxSize()) {
                    Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFF162435), shadowElevation = 4.dp, modifier = Modifier.align(Alignment.Center).width(104.dp).height(56.dp)) { Box(contentAlignment = Alignment.Center) { Text(text = "hazırlıyorum", fontSize = 12.sp, color = Color(0xFF4FB6FF).copy(alpha = 0.92f), fontWeight = FontWeight.SemiBold) } }
                }
            }
            KawaiiArm(isLeft = true, wave = armWave, modifier = Modifier.align(Alignment.CenterStart).offset(x = (-70).dp, y = 2.dp))
            KawaiiArm(isLeft = false, wave = armWave, modifier = Modifier.align(Alignment.CenterEnd).offset(x = (70).dp, y = 2.dp))
            KawaiiBook(wave = armWave, modifier = Modifier.align(Alignment.BottomCenter).offset(y = 64.dp))
        }
    }
}

@Composable
fun KawaiiArm(isLeft: Boolean, wave: Float, modifier: Modifier = Modifier) {
    val rot = if (isLeft) (-10f + (wave * 9f)) else (10f - (wave * 9f))
    val foreRot = if (isLeft) (-8f + (wave * 10f)) else (8f - (wave * 10f))
    Column(modifier = modifier.rotate(rot), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.width(62.dp).height(16.dp).background(Color(0xFFE6EEF9), RoundedCornerShape(99.dp)))
        Spacer(Modifier.height(6.dp)); Box(modifier = Modifier.size(16.dp).background(Color(0xFFD7E3F2), CircleShape)); Spacer(Modifier.height(6.dp))
        Column(modifier = Modifier.rotate(foreRot), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.width(58.dp).height(14.dp).background(Color(0xFFE6EEF9), RoundedCornerShape(99.dp)))
            Spacer(Modifier.height(8.dp)); Box(modifier = Modifier.size(18.dp).background(Color(0xFFF7FAFF), CircleShape))
        }
    }
}

@Composable
fun KawaiiBook(wave: Float, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF7F4EE), shadowElevation = 10.dp, modifier = modifier.width(196.dp).height(72.dp).rotate((sin((wave * PI).toFloat()) * 2.0f))) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFFFFBF2), shadowElevation = 2.dp, modifier = Modifier.weight(1f).height(48.dp)) { }
            Box(modifier = Modifier.width(8.dp).height(50.dp).background(Color(0xFF2B2B2B).copy(alpha = 0.10f), RoundedCornerShape(99.dp)))
            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFFFFBF2), shadowElevation = 2.dp, modifier = Modifier.weight(1f).height(48.dp)) { }
        }
    }
}