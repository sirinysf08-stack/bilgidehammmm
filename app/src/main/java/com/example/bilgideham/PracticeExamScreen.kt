package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeExamScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme

    // MaterialTheme'a bağlı: dark mode aktifken otomatik karanlık, açık modda açık zemin.
    val pageBg = cs.background

    val isDark = cs.background.luminance() < 0.30f

    val appBarGradient = if (isDark) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF0B1220),
                Color(0xFF0F172A),
                cs.primary.copy(alpha = 0.30f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                cs.primary,
                cs.primary.copy(alpha = 0.90f)
            )
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                    .background(appBarGradient)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "Deneme Sınavları",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = "Gerçek sınav provası.",
                                color = Color.White.copy(alpha = 0.90f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
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
                "Kendini Test Et! 🚀",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onBackground
            )
            Text(
                "Gerçek sınav provası yapmak için bir mod seç.",
                fontSize = 14.sp,
                color = cs.onBackground.copy(alpha = 0.70f)
            )
            Spacer(Modifier.height(30.dp))

            // --- 70 SORULUK SINAV ---
            ExamOptionCard(
                title = "Genel Deneme",
                subtitle = "70 Soru • 105 Dakika\nSıralı: Tr-Mat-Fen-Sos-İng-Din",
                badgeText = "Orta Seviye",
                icon = Icons.Default.Timer,
                gradientColors = if (isDark)
                    listOf(Color(0xFF1E293B), cs.primary.copy(alpha = 0.55f))
                else
                    listOf(Color(0xFF42A5F5), Color(0xFF1976D2)),
                onClick = {
                    navController.navigate("quiz_screen/GENEL_DENEME/70?duration=105")
                }
            )

            Spacer(Modifier.height(20.dp))

            // --- 120 SORULUK SINAV ---
            ExamOptionCard(
                title = "Büyük Maraton",
                subtitle = "120 Soru • 120 Dakika\nTam Kapsamlı Müfredat",
                badgeText = "Zorlu Mod",
                icon = Icons.Default.WorkspacePremium,
                gradientColors = if (isDark)
                    listOf(Color(0xFF2A1F10), cs.secondary.copy(alpha = 0.55f))
                else
                    listOf(Color(0xFFFFA726), Color(0xFFF57C00)),
                onClick = {
                    navController.navigate("quiz_screen/MARATON/120?duration=120")
                }
            )
        }
    }
}

@Composable
fun ExamOptionCard(
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
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(gradientColors))) {
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = Color.White.copy(alpha = 0.20f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            badgeText,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.90f),
                        lineHeight = 16.sp
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.90f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
