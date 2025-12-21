package com.example.bilgideham

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ExamResultScreen(
    navController: NavController,
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    durationText: String
) {
    // 1. PUAN HESAPLAMA (100 ÜZERİNDEN)
    val score = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val emptyCount = totalQuestions - (correctCount + wrongCount)

    // 2. MOTİVASYON MESAJLARI
    val (title, message, iconColor) = when {
        score == 100 -> Triple("Efsane! 🏆", "Mükemmelsin! Hata yapmadın.", Color(0xFFFFD700)) // Altın
        score >= 90 -> Triple("Harika! 🌟", "Çok başarılısın, tebrikler!", Color(0xFF4CAF50)) // Yeşil
        score >= 80 -> Triple("Çok İyi! 👏", "Gayet iyi gidiyorsun.", Color(0xFF81C784)) // Açık Yeşil
        score >= 70 -> Triple("İyi İş! 👍", "Biraz daha tekrarla zirvedesin.", Color(0xFF2196F3)) // Mavi
        score >= 50 -> Triple("Gelişiyorsun 💪", "Çalışmaya devam etmelisin.", Color(0xFFFF9800)) // Turuncu
        else -> Triple("Pes Etme! 🌱", "Hatalarından ders çıkararak başaracaksın.", Color(0xFFE57373)) // Kırmızı
    }

    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1500),
        label = "scoreAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFFAFAFA))))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(50.dp))

        // --- KUPA / İKON ALANI ---
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(12.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if(score >= 80) Icons.Default.Star else if(score >= 50) Icons.Default.ThumbUp else Icons.Default.TrendingUp,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- BAŞLIK VE MESAJ ---
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF37474F)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(30.dp))

        // --- PUAN KARTI ---
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SINAV PUANI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                Text(
                    text = "${animatedScore.toInt()}",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = iconColor
                )

                Divider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFF5F5F5))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStatItem(Icons.Default.Check, Color(0xFF4CAF50), "$correctCount", "Doğru")
                    ResultStatItem(Icons.Default.Close, Color(0xFFE57373), "$wrongCount", "Yanlış")
                    ResultStatItem(Icons.Default.Refresh, Color.Gray, "$emptyCount", "Boş")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- SÜRE KARTI ---
        Surface(
            color = Color(0xFFE0F7FA),
            shape = RoundedCornerShape(50)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️ Tamamlama Süresi: $durationText",
                    color = Color(0xFF006064),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // --- BUTONLAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.popBackStack("home", inclusive = false) },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1), contentColor = Color(0xFF455A64))
            ) {
                Icon(Icons.Default.Home, null)
                Spacer(Modifier.width(8.dp))
                Text("Ana Sayfa", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { navController.popBackStack() }, // Geri dön = Yeni sınav
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Tekrar Çöz", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}

@Composable
fun ResultStatItem(icon: ImageVector, color: Color, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}