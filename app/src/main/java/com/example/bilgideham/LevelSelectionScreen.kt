package com.example.bilgideham

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Eğitim Seviyesi Seçim Ekranı
 * HomeScreen tasarım diliyle uyumlu - glassmorphism, star dust efekti
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    navController: NavController,
    onLevelSelected: (EducationLevel) -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Tema kontrolü
    val darkMode = remember { AppPrefs.getDarkMode(context) }
    
    // Yapım aşamasında dialog state
    var showComingSoonDialog by remember { mutableStateOf(false) }
    var selectedLevelName by remember { mutableStateOf("") }

    // Arka plan gradient (HomeScreen ile aynı)
    val backgroundBrush = if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF0B1220), Color(0xFF1E293B), Color(0xFF0F172A)))
    } else {
        Brush.verticalGradient(listOf(cs.primary, cs.tertiary, cs.primaryContainer))
    }
    
    // Yapım Aşamasında Dialog
    if (showComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showComingSoonDialog = false },
            containerColor = if (darkMode) Color(0xFF1E293B) else Color.White,
            shape = RoundedCornerShape(28.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB300).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚧", fontSize = 42.sp)
                }
            },
            title = {
                Text(
                    text = "Geliştirme Aşamasında",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    color = if (darkMode) Color.White else cs.onSurface
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$selectedLevelName bölümü şu anda yapım aşamasındadır.",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = if (darkMode) Color.White.copy(alpha = 0.8f) else cs.onSurface.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00E5FF).copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "✨ Çok yakında burada olacak harika özellikler sizi bekliyor!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF00ACC1),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showComingSoonDialog = false },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300)
                    )
                ) {
                    Text(
                        text = "Anladım 👍",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding)
        ) {
            // Star Dust Efekti (HomeScreen ile aynı)
            StarDustEffect(color = Color.White.copy(alpha = 0.25f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                // Hero Card (HomeScreen AIGlassHeroCard benzeri)
                WelcomeHeroCard(darkMode = darkMode)

                Spacer(Modifier.height(32.dp))

                // Bölüm Başlığı
                Text(
                    text = "EĞİTİM SEVİYENİ SEÇ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(20.dp))

                // Seviye Kartları
                EducationLevel.entries.forEach { level ->
                    // Yapım aşamasında olan seviyeler (AGS ve LİSE artık aktif!)
                    val isComingSoon = false // KPSS artık aktif!
                    
                    LevelCardModern(
                        level = level,
                        darkMode = darkMode,
                        onClick = {
                            if (isComingSoon) {
                                // Yapım aşamasında dialog göster
                                selectedLevelName = level.displayName
                                showComingSoonDialog = true
                            } else {
                                // Tüm seviyeler (AGS dahil) SchoolTypeScreen'e yönlendirilir
                                // Oturumları/okul türlerini orada seçebilir
                                onLevelSelected(level)
                                navController.navigate("school_type_selection/${level.name}")
                            }
                        }
                    )
                    Spacer(Modifier.height(14.dp))
                }

                Spacer(Modifier.height(24.dp))

                // Alt bilgi
                Text(
                    text = "Daha sonra ayarlardan değiştirebilirsin",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun WelcomeHeroCard(darkMode: Boolean) {
    val cs = MaterialTheme.colorScheme

    // Pulse animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "welcomePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(
                Brush.linearGradient(
                    if (darkMode) listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                    else listOf(Color(0xFF667eea), Color(0xFF764ba2), Color(0xFF6B8DD6))
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(40.dp))
    ) {
        // Glow efekti
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = 200.dp.toPx()
                ),
                radius = 200.dp.toPx(),
                center = Offset(size.width, 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Üst badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                        .blur(3.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "YAPAY ZEKA DESTEKLİ EĞİTİM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Hoş Geldin! 👋",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Sana en uygun eğitim deneyimini sunmak için seviyeni öğrenmemiz gerekiyor.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun LevelCardModern(
    level: EducationLevel,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val cardColor = Color(level.colorHex)
    
    // Yapım aşamasında olan seviyeler (AGS ve LİSE artık aktif!)
    val isComingSoon = false // KPSS artık aktif!

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (darkMode)
                    Color.White.copy(alpha = 0.08f)
                else
                    Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (darkMode) 0.dp else 6.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                cardColor.copy(alpha = 0.6f),
                                cardColor.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // İkon kutusu
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.icon,
                        fontSize = 30.sp
                    )
                }

                Spacer(Modifier.width(18.dp))

                // Metin
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = level.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (darkMode) Color.White else cs.onSurface
                    )
                    Text(
                        text = level.description,
                        fontSize = 14.sp,
                        color = if (darkMode) Color.White.copy(alpha = 0.6f) else cs.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Ok ikonu
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cardColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = cardColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        // Yapım Aşamasında Badge - Sağ üst köşe
        if (isComingSoon) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-6).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚧", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Yakında",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// Star Dust Efekti (HomeScreen ile aynı)
@Composable
private fun StarDustEffect(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "starDust")
    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -250f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dustMove"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val randomGen = Random(9999)
        repeat(70) {
            val xPos = randomGen.nextFloat() * size.width
            val startY = randomGen.nextFloat() * size.height
            val currentY = (startY + moveY) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY

            drawCircle(
                color = color,
                radius = randomGen.nextFloat() * 3.5.dp.toPx() + 0.8.dp.toPx(),
                center = Offset(xPos, drawY),
                alpha = randomGen.nextFloat() * 0.9f
            )
        }
    }
}
