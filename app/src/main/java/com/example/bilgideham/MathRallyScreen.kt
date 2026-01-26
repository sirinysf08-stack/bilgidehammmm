package com.example.bilgideham

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // ✅ EKLENEN IMPORT BU
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

data class MathRallyQuestion(val text: String, val correctAnswer: Int, val options: List<Int>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathRallyScreen(navController: NavController) {
    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableFloatStateOf(6f) } // Her soru için 6 saniye
    var isGameOver by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf(generateAdvancedMathQuestion(1)) }
    var buttonColors by remember { mutableStateOf(List(4) { Color(0xFF1976D2) }) } // Mavi
    var questionKey by remember { mutableIntStateOf(0) } // Soru değiştiğinde timer'ı resetlemek için

    // Zamanlayıcı - Her soru için 6 saniye
    LaunchedEffect(key1 = isGameOver, key2 = questionKey) {
        if (!isGameOver) {
            timeLeft = 6f // Her yeni soruda süre sıfırlanır
            while (timeLeft > 0 && !isGameOver) {
                delay(100)
                timeLeft -= 0.1f
            }
            if (timeLeft <= 0 && !isGameOver) isGameOver = true
        }
    }

    fun checkAnswer(index: Int, selected: Int) {
        if (isGameOver) return

        val isCorrect = selected == question.correctAnswer
        val newColors = buttonColors.toMutableList()

        if (isCorrect) {
            newColors[index] = Color(0xFF43A047) // Yeşil
            score += (10 * level) // Seviyeye göre puan artar

            if (level < 100) {
                level++
                // 300ms gecikme ile yeni soru
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    question = generateAdvancedMathQuestion(level)
                    buttonColors = List(4) { Color(0xFF1976D2) }
                    questionKey++ // Timer'ı resetle
                }, 300)
            } else {
                isGameOver = true // Oyun bitti (Kazandı)
            }
        } else {
            newColors[index] = Color(0xFFD32F2F) // Kırmızı
            timeLeft = (timeLeft - 1.5f).coerceAtLeast(0f) // Yanlış cevap 1.5 saniye götürür
        }
        buttonColors = newColors
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ralli: Lv $level", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Default.Speed, null, tint = Color.White)
                    }
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD32F2F), titleContentColor = Color.White)
            )
        }
    ) { p ->
        Column(
            modifier = Modifier.padding(p).fillMaxSize().background(Color(0xFFECEFF1)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isGameOver) {
                // --- SONUÇ EKRANI ---
                Spacer(Modifier.height(50.dp))
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107), modifier = Modifier.size(120.dp))
                Text(if (level > 100) "ŞAMPİYON!" else "SÜRE BİTTİ!", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F))
                Text("Toplam Puan: $score", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Ulaşılan Seviye: $level", fontSize = 16.sp, color = Color.Gray)
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = { level = 1; score = 0; timeLeft = 6f; isGameOver = false; question = generateAdvancedMathQuestion(1); buttonColors = List(4) { Color(0xFF1976D2) }; questionKey++ },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Text("Tekrar Yarış")
                }
            } else {
                // --- OYUN ALANI ---

                // Süre Göstergesi (6 saniye)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.LocalGasStation, null, tint = if(timeLeft < 2f) Color.Red else Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { (timeLeft / 6f).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(10.dp)),
                        color = when {
                            timeLeft < 2f -> Color.Red
                            timeLeft < 4f -> Color(0xFFFFA000) // Turuncu
                            else -> Color(0xFF43A047) // Yeşil
                        },
                        trackColor = Color.LightGray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${timeLeft.toInt()}s",
                        fontWeight = FontWeight.Bold,
                        color = if(timeLeft < 2f) Color.Red else Color.Gray
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Soru Kartı
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = question.text,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF263238),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 45.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Şıklar
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val opts = question.options
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RallyButton(opts[0].toString(), buttonColors[0], Modifier.weight(1f)) { checkAnswer(0, opts[0]) }
                        RallyButton(opts[1].toString(), buttonColors[1], Modifier.weight(1f)) { checkAnswer(1, opts[1]) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RallyButton(opts[2].toString(), buttonColors[2], Modifier.weight(1f)) { checkAnswer(2, opts[2]) }
                        RallyButton(opts[3].toString(), buttonColors[3], Modifier.weight(1f)) { checkAnswer(3, opts[3]) }
                    }
                }
            }
        }
    }
}

@Composable
fun RallyButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// --- 5. SINIF GELİŞMİŞ MATEMATİK MOTORU ---
fun generateAdvancedMathQuestion(level: Int): MathRallyQuestion {
    val r = Random
    var qText = ""
    var ans = 0

    // Zorluk Kademeleri
    when {
        level <= 10 -> { // Isınma: Toplama/Çıkarma
            val a = r.nextInt(10, 50); val b = r.nextInt(5, 40)
            if (r.nextBoolean()) { qText = "$a + $b"; ans = a + b }
            else { val max = maxOf(a,b); val min = minOf(a,b); qText = "$max - $min"; ans = max - min }
        }
        level <= 25 -> { // Çarpım Tablosu
            val a = r.nextInt(3, 12); val b = r.nextInt(2, 10)
            qText = "$a x $b"; ans = a * b
        }
        level <= 40 -> { // Parantezli İşlemler (5. Sınıf)
            val a = r.nextInt(2, 10); val b = r.nextInt(2, 10); val c = r.nextInt(2, 5)
            qText = "($a + $b) x $c"; ans = (a + b) * c
        }
        level <= 60 -> { // Bölme ve Üslü Sayılar (Kareler)
            if (r.nextBoolean()) {
                val b = r.nextInt(3, 12); val res = r.nextInt(4, 15)
                val a = b * res
                qText = "$a ÷ $b"; ans = res
            } else {
                val base = r.nextInt(2, 10) // 2^2, 5^2 gibi
                qText = "$base²"; ans = base * base
            }
        }
        level <= 80 -> { // Bilinmeyeni Bulma (Basit Denklem)
            val x = r.nextInt(5, 20); val add = r.nextInt(10, 50)
            val res = x + add
            qText = "? + $add = $res"; ans = x
        }
        else -> { // Şampiyonlar Ligi (Karışık Zor)
            val op = r.nextInt(3)
            if (op == 0) { // Büyük Çarpma
                val a = r.nextInt(12, 25); val b = r.nextInt(3, 9)
                qText = "$a x $b"; ans = a * b
            } else if (op == 1) { // 3'lü İşlem
                val a = r.nextInt(10, 30); val b = r.nextInt(5, 15); val c = r.nextInt(2, 5)
                qText = "$a - $b x $c"; ans = a - (b * c) // İşlem önceliği!
            } else { // Küpler
                val base = r.nextInt(2, 6)
                qText = "$base³"; ans = base * base * base
            }
        }
    }

    // Şık Üretimi (Asla donmaz)
    val options = mutableSetOf<Int>()
    options.add(ans)
    while (options.size < 4) {
        val fake = ans + r.nextInt(-15, 16)
        if (fake != ans && fake >= 0) options.add(fake)
    }

    return MathRallyQuestion(qText, ans, options.toList().shuffled())
}