package com.example.bilgideham

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Veri sınıfı (Eğer başka dosyada yoksa burada tanımlı kalsın)
data class TextRallyQuestion(
    val text: String,
    val correctOptionIndex: Int,
    val options: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FenBilimleriRallyScreen(navController: NavController) {
    // TEMA: DERİN MAVİ
    val primaryColor = Color(0xFF303F9F)
    val secondaryColor = Color(0xFF7986CB)
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var fuel by remember { mutableFloatStateOf(1.0f) }
    var isGameOver by remember { mutableStateOf(false) }

    // Başlangıçta soru yok (Loading durumu)
    var question by remember { mutableStateOf<TextRallyQuestion?>(null) }

    // Veritabanından soru çekme durumu
    var isPoolEmpty by remember { mutableStateOf(false) }

    // SAYFA AÇILINCA İLK SORUYU ÇEK
    LaunchedEffect(Unit) {
        val q = GameRepositoryNew.getQuestionForGame("Fen")
        // Eğer gelen soru "Soru Kalmadı" veya "Yükle" içeriyorsa havuz boştur
        if (q.text.contains("Yükle") || q.text.contains("Boş")) {
            isPoolEmpty = true
        } else {
            question = q
        }
    }

    var buttonColors: List<Color> by remember {
        mutableStateOf(List(4) { secondaryColor })
    }

    // --- SÜRE AYARI (OKUMA DOSTU) ---
    LaunchedEffect(key1 = isGameOver, key2 = level, key3 = isPoolEmpty) {
        if (!isGameOver && !isPoolEmpty && question != null) {
            while (fuel > 0) {
                // Yavaşlatılmış süre
                val baseDrain = 0.0015f
                val levelDrain = level * 0.00005f
                fuel -= (baseDrain + levelDrain)
                delay(100)
            }
            if (fuel <= 0) isGameOver = true
        }
    }

    fun loadNextQuestion() {
        scope.launch {
            val nextQ = GameRepositoryNew.getQuestionForGame("Fen")
            if (nextQ.text.contains("Yükle") || nextQ.text.contains("Boş")) {
                isPoolEmpty = true
            } else {
                question = nextQ
                buttonColors = List(4) { secondaryColor }
            }
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
            fuel = (fuel + 0.20f).coerceAtMost(1.0f)

            // Soruyu çözüldü işaretle
            scope.launch { GameRepositoryNew.markQuestionSolved(currentQ.text) }

            if (level < 100) {
                level++
                scope.launch {
                    delay(300)
                    loadNextQuestion()
                }
            } else { isGameOver = true }
        } else {
            newColors[selectedIndex] = Color(0xFFD32F2F) // Kırmızı
            fuel = (fuel - 0.10f).coerceAtLeast(0f)
        }
        buttonColors = newColors
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fen Laboratuvarı: Lv $level", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Default.Science, null, tint = Color.White)
                    }
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor, titleContentColor = Color.White)
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFE8EAF6))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. DURUM: HAVUZ BOŞ İSE ÖZEL EKRAN GÖSTER
            if (isPoolEmpty) {
                Spacer(Modifier.height(60.dp))
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFF5C6BC0),
                    modifier = Modifier.size(100.dp).background(Color(0xFFC5CAE9), RoundedCornerShape(50)).padding(20.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Soru Havuzu Boş!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF283593)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Oyun oynamak için önce Admin panelinden soru yüklemeniz gerekiyor.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
                ) {
                    Text("Geri Dön")
                }
            }

            // 2. DURUM: OYUN BİTTİ İSE SKOR GÖSTER
            else if (isGameOver) {
                Spacer(Modifier.height(50.dp))
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107), modifier = Modifier.size(120.dp))
                Text("Oyun Bitti!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Puan: $score", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = {
                        level = 1; score = 0; fuel = 1.0f; isGameOver = false; isPoolEmpty = false
                        loadNextQuestion()
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) { Text("Yeniden Dene") }
            }

            // 3. DURUM: SORU YÜKLENİYORSA
            else if (question == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }

            // 4. DURUM: NORMAL OYUN EKRANI
            else {
                LinearProgressIndicator(progress = { fuel }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(10.dp)), color = if(fuel < 0.3f) Color.Red else Color(0xFF4CAF50))
                Spacer(Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = question!!.text,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF283593),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val opts = question!!.options
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FenOptionButton(opts.getOrElse(0) { "-" }, buttonColors[0], Modifier.weight(1f)) { checkAnswer(0) }
                        FenOptionButton(opts.getOrElse(1) { "-" }, buttonColors[1], Modifier.weight(1f)) { checkAnswer(1) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FenOptionButton(opts.getOrElse(2) { "-" }, buttonColors[2], Modifier.weight(1f)) { checkAnswer(2) }
                        FenOptionButton(opts.getOrElse(3) { "-" }, buttonColors[3], Modifier.weight(1f)) { checkAnswer(3) }
                    }
                }
            }
        }
    }
}

// Çakışmayı önlemek için ismini özelleştirdik
@Composable
private fun FenOptionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(
            text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            textAlign = TextAlign.Center
        )
    }
}