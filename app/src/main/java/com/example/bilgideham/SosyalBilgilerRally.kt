package com.example.bilgideham

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TextRallyQuestion sınıfı FenBilimleriRally.kt içinde tanımlı, oradan görüyor.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosyalBilgilerRallyScreen(navController: NavController) {
    val primaryColor = Color(0xFFE91E63)
    val secondaryColor = Color(0xFFFF6090)
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var fuel by remember { mutableFloatStateOf(1.0f) }
    var isGameOver by remember { mutableStateOf(false) }

    // Veritabanından gelen soru (Başlangıçta null)
    var question by remember { mutableStateOf<TextRallyQuestion?>(null) }

    // Ekran açılınca soru çek
    LaunchedEffect(Unit) {
        question = GameRepositoryNew.getQuestionForGame("Sosyal")
    }

    var buttonColors: List<Color> by remember {
        mutableStateOf(List(4) { secondaryColor })
    }

    // --- YAVAŞ SÜRE AKIŞI ---
    LaunchedEffect(key1 = isGameOver, key2 = level) {
        if (!isGameOver) {
            while (fuel > 0) {
                // Okuma dostu hız:
                val baseDrain = 0.0015f
                val levelDrain = level * 0.00005f
                fuel -= (baseDrain + levelDrain)
                delay(100) // Saniyede 10 tetik
            }
            if (fuel <= 0) isGameOver = true
        }
    }

    fun loadNextQuestion() {
        scope.launch {
            val nextQ = GameRepositoryNew.getQuestionForGame("Sosyal")
            question = nextQ
            buttonColors = List(4) { secondaryColor }
        }
    }

    fun checkAnswer(selectedIndex: Int) {
        if (isGameOver || question == null) return
        val currentQ = question!!
        val isCorrect = selectedIndex == currentQ.correctOptionIndex
        val newColors = buttonColors.toMutableList()

        if (isCorrect) {
            newColors[selectedIndex] = Color(0xFF43A047) // Yeşil
            score += (10 * level)
            fuel = (fuel + 0.20f).coerceAtMost(1.0f) // Ödül %20

            // Soruyu çözüldü işaretle (Basit text bazlı)
            scope.launch { GameRepositoryNew.markQuestionSolved(currentQ.text) }

            if (level < 100) {
                level++
                // Gecikmeli yeni soru
                scope.launch {
                    delay(300)
                    loadNextQuestion()
                }
            } else { isGameOver = true }
        } else {
            newColors[selectedIndex] = Color(0xFFD32F2F) // Kırmızı
            fuel = (fuel - 0.10f).coerceAtLeast(0f) // Ceza %10
        }
        buttonColors = newColors
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Row(verticalAlignment = Alignment.CenterVertically) { Text("Sosyal Bilgiler: Lv $level", fontWeight = FontWeight.Bold); Spacer(Modifier.width(10.dp)); Icon(Icons.Default.Public, null, tint = Color.White) } },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor, titleContentColor = Color.White)
            )
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).fillMaxSize().background(Color(0xFFFCE4EC)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isGameOver) {
                Spacer(Modifier.height(50.dp))
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107), modifier = Modifier.size(120.dp))
                Text("Oyun Bitti!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Puan: $score", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = {
                        level = 1; score = 0; fuel = 1.0f; isGameOver = false
                        loadNextQuestion()
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) { Icon(Icons.Default.Refresh, null); Text("Yeniden Dene") }
            } else {
                LinearProgressIndicator(progress = { fuel }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(10.dp)), color = if(fuel < 0.3f) Color.Red else Color(0xFF4CAF50))
                Spacer(Modifier.height(24.dp))

                if (question == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    Card(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = question!!.text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val opts = question!!.options
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SosyalOptionButton(opts.getOrElse(0){"-"}, buttonColors[0], Modifier.weight(1f)) { checkAnswer(0) }
                            SosyalOptionButton(opts.getOrElse(1){"-"}, buttonColors[1], Modifier.weight(1f)) { checkAnswer(1) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SosyalOptionButton(opts.getOrElse(2){"-"}, buttonColors[2], Modifier.weight(1f)) { checkAnswer(2) }
                            SosyalOptionButton(opts.getOrElse(3){"-"}, buttonColors[3], Modifier.weight(1f)) { checkAnswer(3) }
                        }
                    }
                }
            }
        }
    }
}

// İSİM DEĞİŞTİRİLDİ: Çakışmayı önlemek için SosyalOptionButton yaptık
@Composable
private fun SosyalOptionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(80.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = color), elevation = ButtonDefaults.buttonElevation(6.dp)) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 3, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}