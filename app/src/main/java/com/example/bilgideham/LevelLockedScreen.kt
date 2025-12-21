package com.example.bilgideham

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius // EKLENEN IMPORT
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random

@Composable
fun LevelLockedScreen(navController: NavController) {
    // Gövde için çok hafif pastel yeşil arka plan
    val bodyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8))
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            // --- MODERN BAŞLIK ALANI (YEŞİL TEMA) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2E7D32), Color(0xFF66BB6A)) // Koyu Yeşilden Açığa
                        )
                    )
            ) {
                // Yıldız Tozu Efekti
                LockedLevelStarDust(color = Color.White.copy(alpha = 0.3f))

                // Dekoratif Arka Plan İkonu (Yaprak)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp, y = 20.dp)
                        .rotate(-20f)
                ) {
                    Icon(
                        Icons.Rounded.Spa,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(130.dp)
                    )
                }

                // İçerik (Geri Butonu ve Başlık)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding() // Bildirim çubuğu güvenli alan
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(12.dp))

                    // Geri Butonu
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        "Gelişim Süreci",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bodyGradient)
                .padding(padding) // Scaffold padding'i
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(20.dp))

            // --- TATLI FİDAN ANİMASYONU ---
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Arka plandaki parıltı
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                )
                CuteSproutAnimation()
            }

            Spacer(Modifier.height(32.dp))

            // --- MOTİVE EDİCİ MESAJLAR ---
            Text(
                "Henüz Olgunlaşma Aşamasında 🌱",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Bu seviye şu an gelişim sürecinde. Sen temellerini sağlamlaştırırken biz de burayı senin için hazırlıyoruz!",
                fontSize = 15.sp,
                color = Color(0xFF558B2F),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(24.dp))

            // Küçük ipucu kutusu
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "İpucu: A1 ve A2 seviyelerini tamamlayarak buraya daha güçlü gelebilirsin!",
                        fontSize = 13.sp,
                        color = Color(0xFF33691E),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // --- BUTON ---
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Çalışmaya Devam Et 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- FİDAN ANİMASYONU ÇİZİMİ ---
@Composable
fun CuteSproutAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "plant")
    val leafRotation by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "leaf"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2 + 60.dp.toPx()

        // Saksı
        drawArc(
            color = Color(0xFF795548),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - 60.dp.toPx(), cy - 20.dp.toPx()),
            size = Size(120.dp.toPx(), 80.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF5D4037),
            topLeft = Offset(cx - 70.dp.toPx(), cy - 20.dp.toPx()),
            size = Size(140.dp.toPx(), 20.dp.toPx()),
            cornerRadius = CornerRadius(10f) // ARTIK HATA VERMEYECEK
        )

        // Gövde
        val stemPath = Path().apply {
            moveTo(cx, cy - 20.dp.toPx())
            quadraticBezierTo(cx + 10.dp.toPx(), cy - 80.dp.toPx(), cx, cy - 120.dp.toPx())
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )

        // Yapraklar
        rotate(leafRotation, pivot = Offset(cx, cy - 90.dp.toPx())) {
            drawOval(
                color = Color(0xFF66BB6A),
                topLeft = Offset(cx - 40.dp.toPx(), cy - 110.dp.toPx()),
                size = Size(40.dp.toPx(), 25.dp.toPx())
            )
        }
        rotate(-leafRotation, pivot = Offset(cx, cy - 70.dp.toPx())) {
            drawOval(
                color = Color(0xFF66BB6A),
                topLeft = Offset(cx, cy - 90.dp.toPx()),
                size = Size(35.dp.toPx(), 20.dp.toPx())
            )
        }

        // Yüz
        val faceY = cy + 20.dp.toPx()
        drawCircle(Color.White, 6.dp.toPx(), Offset(cx - 15.dp.toPx(), faceY))
        drawCircle(Color.Black, 2.dp.toPx(), Offset(cx - 15.dp.toPx(), faceY))
        drawCircle(Color.White, 6.dp.toPx(), Offset(cx + 15.dp.toPx(), faceY))
        drawCircle(Color.Black, 2.dp.toPx(), Offset(cx + 15.dp.toPx(), faceY))

        // Yanaklar
        drawOval(Color(0xFFFFCDD2), topLeft = Offset(cx - 28.dp.toPx(), faceY + 8.dp.toPx()), size = Size(10.dp.toPx(), 6.dp.toPx()))
        drawOval(Color(0xFFFFCDD2), topLeft = Offset(cx + 18.dp.toPx(), faceY + 8.dp.toPx()), size = Size(10.dp.toPx(), 6.dp.toPx()))

        // Ağız (Gülümseme)
        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - 8.dp.toPx(), faceY + 5.dp.toPx()),
            size = Size(16.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

// --- YILDIZ TOZU EFEKTİ (Bu dosya için özel) ---
@Composable
private fun LockedLevelStarDust(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_movement")

    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "moveY"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse), label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = Random(99)
        repeat(15) {
            val startX = r.nextFloat() * size.width
            val startY = r.nextFloat() * size.height
            val radius = r.nextFloat() * 2.dp.toPx() + 1.dp.toPx()
            val speedFactor = (it % 2) + 1
            val currentY = (startY + moveY * speedFactor) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY

            drawCircle(
                color = color,
                radius = radius,
                center = Offset(startX, drawY),
                alpha = alpha * r.nextFloat()
            )
        }
    }
}