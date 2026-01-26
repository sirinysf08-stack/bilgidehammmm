package com.example.bilgideham

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ModernLoadingAnimation(
    message: String = "Yapay Zeka Hazırlanıyor...",
    subMessage: String = "Müfredat analiz ediliyor",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_loading")
    
    // Dönen halkalar
    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "rot1"
    )
    
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rot2"
    )

    // Pulse efekti
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // Yanıp sönen noktalar
    var dotCount by remember { mutableStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = if (dotCount >= 3) 1 else dotCount + 1
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            // Dış Halka 1 (Turkuaz)
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(rotation1) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, Color(0xFF00E5FF), Color.Transparent)
                        ),
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            
            // Dış Halka 2 (Mor) - Ters Yön
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                rotate(rotation2) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, Color(0xFFD500F9), Color.Transparent)
                        ),
                        startAngle = 90f,
                        sweepAngle = 220f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            
            // Merkezdeki Beyin/Sinyal Noktası
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = size.minDimension / 2 * pulse,
                    alpha = 0.8f
                )
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension / 4,
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subMessage + ".".repeat(dotCount),
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
