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
    var fuel by remember { mutableFloatStateOf(1.0f) } // Benzin (Süre)
    var isGameOver by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf(generateAdvancedMathQuestion(1)) }
    var buttonColors by remember { mutableStateOf(List(4) { Color(0xFF1976D2) }) } // Mavi

    // Zamanlayıcı (Benzin Tüketimi)
    LaunchedEffect(key1 = isGameOver, key2 = level) {
        if (!isGameOver) {
            while (fuel > 0) {
                // Seviye arttıkça benzin daha hızlı tükenir
                val drainRate = 0.005f + (level * 0.0001f)
                fuel -= drainRate
                delay(50)
            }
            if (fuel <= 0) isGameOver = true
        }
    }

    fun checkAnswer(index: Int, selected: Int) {
        if (isGameOver) return

        val isCorrect = selected == question.correctAnswer
        val newColors = buttonColors.toMutableList()

        if (isCorrect) {
            newColors[index] = Color(0xFF43A047) // Yeşil
            score += (10 * level) // Seviyeye göre puan artar
            fuel = (fuel + 0.2f).coerceAtMost(1.0f) // Doğru cevap benzin verir

            if (level < 100) {
                level++
                // 300ms gecikme ile yeni soru
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    question = generateAdvancedMathQuestion(level)
                    buttonColors = List(4) { Color(0xFF1976D2) }
                }, 300)
            } else {
                isGameOver = true // Oyun bitti (Kazandı)
            }
        } else {
            newColors[index] = Color(0xFFD32F2F) // Kırmızı
            fuel = (fuel - 0.2f).coerceAtLeast(0f) // Yanlış cevap benzin götürür
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
                Text(if (fuel > 0) "ŞAMPİYON!" else "BENZİN BİTTİ!", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F))
                Text("Toplam Puan: $score", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = { level = 1; score = 0; fuel = 1.0f; isGameOver = false; question = generateAdvancedMathQuestion(1); buttonColors = List(4) { Color(0xFF1976D2) } },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Text("Tekrar Yarış")
                }
            } else {
                // --- OYUN ALANI ---

                // Benzin Göstergesi
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.LocalGasStation, null, tint = if(fuel < 0.3f) Color.Red else Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { fuel },
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(10.dp)), // clip artık çalışacak
                        color = if(fuel < 0.3f) Color.Red else Color(0xFF43A047),
                        trackColor = Color.LightGray
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