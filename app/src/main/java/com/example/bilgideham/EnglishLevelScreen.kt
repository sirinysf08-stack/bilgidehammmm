package com.example.bilgideham

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random

// Seviye bilgilerini tutan özel veri sınıfı
data class EnglishLevel(
    val code: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishLevelScreen(navController: NavController) {

    // RENKLER
    val levels = listOf(
        EnglishLevel("A1", "Başlangıç", "Temel kelimeler ve tanışma.", Color(0xFF4CAF50), Icons.Default.Star),
        EnglishLevel("A2", "Temel", "Günlük rutinler ve basit cümleler.", Color(0xFF00BCD4), Icons.Default.Hiking),
        EnglishLevel("B1", "Orta Öncesi", "Gelecek planları ve deneyimler.", Color(0xFFFFA726), Icons.Default.FlightTakeoff),
        EnglishLevel("B2", "Orta Üstü", "Karmaşık metinler ve detaylar.", Color(0xFFEF5350), Icons.Default.School),
        EnglishLevel("C1", "İleri", "Akıcı konuşma ve zor kelimeler.", Color(0xFFAB47BC), Icons.Default.AutoAwesome),
        EnglishLevel("C2", "Uzman", "Ana dil gibi İngilizce!", Color(0xFF5C6BC0), Icons.Default.EmojiEvents)
    )

    Scaffold(
        // ✨ DÜZELTME: statusBarsPadding() buraya eklendi.
        // Sayfa içeriği artık bildirim çubuğunun altından başlayacak ve çakışma olmayacak.
        modifier = Modifier.statusBarsPadding(),
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            // --- MODERN BAŞLIK ALANI ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                        )
                    )
            ) {
                // Yıldız Tozu Efekti
                EnglishLevelStarDust(color = Color.White.copy(alpha = 0.2f))

                // Dekoratif Arka Plan İkonu
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 20.dp, y = 10.dp)
                        .rotate(-15f)
                ) {
                    Icon(
                        Icons.Rounded.Public,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(140.dp)
                    )
                }

                // İçerik (Geri Butonu ve Başlıklar)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        // statusBarsPadding buradan kaldırıldı (Scaffold'a eklendiği için)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
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

                    // Başlık Yazıları
                    Text(
                        "Seviye Seçimi",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "İngilizce Yolculuğun Başlıyor 🌍",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(levels) { level ->
                    LevelCard(level) {
                        // Kilit kontrolü
                        if (level.code == "B2" || level.code == "C1" || level.code == "C2") {
                            navController.navigate("level_locked")
                        } else {
                            navController.navigate("quiz_screen/İngilizce (${level.code})/20?duration=0")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: EnglishLevel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SOL: Renkli Kutu (Rozet)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(level.accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.code,
                    color = level.accentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ORTA: Yazılar
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = level.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = level.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    lineHeight = 16.sp
                )
            }

            // SAĞ: Kilit veya Ok İkonu
            val isLocked = level.code == "B2" || level.code == "C1" || level.code == "C2"

            Icon(
                imageVector = if (isLocked) Icons.Default.EmojiEvents else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isLocked) Color.LightGray else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// --- YILDIZ TOZU EFEKTİ ---
@Composable
private fun EnglishLevelStarDust(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_movement")

    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "moveY"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse), label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = Random(33)
        repeat(20) {
            val startX = r.nextFloat() * size.width
            val startY = r.nextFloat() * size.height
            val radius = r.nextFloat() * 2.2.dp.toPx() + 1.dp.toPx()
            val speedFactor = (it % 3) + 1
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