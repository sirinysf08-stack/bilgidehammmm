package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphPracticeScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val pageBg = Color(0xFFFFF3E0) // Hafif turuncu/krem arka plan (Okuma modu hissi)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paragraf Antrenmanı", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF9800)) // Turuncu Tema
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(pageBg)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Okuma Hızını Arttır! 📚",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037) // Dark brown for visibility on cream background
            )
            Text(
                text = "Her gün düzenli paragraf çözmek başarıyı getirir.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(30.dp))

            // --- GÜNLÜK 20 SORU ---
            ParagraphOptionCard(
                title = "Günlük Doz",
                subtitle = "20 Soru • İdeal Pratik",
                badgeText = "Her Gün",
                icon = Icons.Default.Timer,
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF43A047)), // Yeşil tonları
                onClick = {
                    navController.navigate("turkce_paragraf_gunluk")
                }
            )

            Spacer(Modifier.height(20.dp))

            // --- HAFTA SONU 30 SORU ---
            ParagraphOptionCard(
                title = "Hafta Sonu Kampı",
                subtitle = "30 Soru • Derinlemesine Analiz",
                badgeText = "Hafta Sonu",
                icon = Icons.Default.Book,
                gradientColors = listOf(Color(0xFFEF5350), Color(0xFFD32F2F)), // Kırmızı tonları
                onClick = {
                    navController.navigate("turkce_paragraf_haftasonu")
                }
            )
        }
    }
}

@Composable
fun ParagraphOptionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(48.dp))
            }
        }
    }
}