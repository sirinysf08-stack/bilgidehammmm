package com.example.bilgideham

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha // ✅ EKLENEN IMPORT BU
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordHuntScreen(navController: NavController) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var content by remember { mutableStateOf("") }
    var retryTick by remember { mutableStateOf(0) }

    // Veri Çekme
    LaunchedEffect(retryTick) {
        loading = true
        error = null
        content = ""
        try {
            val result = fetchDailyEnglishWordsText().trim()
            if (result.isNotBlank()) {
                content = result
            } else {
                error = "Kelimeler alınamadı. İnternetini kontrol et."
            }
        } catch (_: Exception) {
            error = "Bağlantı hatası oluştu."
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("English Word Hunt 🎯", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE1F5FE) // Çok açık mavi
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE1F5FE)) // Arkaplan
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- YÜKLENİYOR ---
            if (loading) {
                Spacer(Modifier.height(100.dp))
                CircularProgressIndicator(color = Color(0xFF0288D1), modifier = Modifier.size(50.dp))
                Spacer(Modifier.height(24.dp))
                Text("Kelime hazinesi taranıyor...", color = Color(0xFF0277BD))
                return@Column
            }

            // --- HATA ---
            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hata: $error", color = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { retryTick++ }) { Text("Tekrar Dene") }
                    }
                }
                return@Column
            }

            // --- İÇERİK ---

            // 1. Motivasyon Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.linearGradient(listOf(Color(0xFF0288D1), Color(0xFF29B6F6))))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Günün Kelimeleri", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Her gün 5 yeni kelime öğren!", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 2. Kelime Listesi (AI Çıktısı)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFA000))
                        Spacer(Modifier.width(8.dp))
                        Text("Yapay Zeka Seçti:", fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                    }
                    // Buradaki .alpha() artık hata vermeyecek
                    Divider(Modifier.padding(vertical = 12.dp).alpha(0.2f))

                    // Metni seçilebilir yapıyoruz ki öğrenci kopyalayabilsin
                    SelectionContainer {
                        Text(
                            text = content,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF37474F)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 3. Alt Butonlar
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Yenile Butonu
                Button(
                    onClick = { retryTick++ },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Yeni Getir")
                }

                // Paylaş Butonu
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Bugünün İngilizce Kelimeleri:\n\n$content")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Paylaş")
                }
            }
        }
    }
}

// Backend Fonksiyonu
private suspend fun fetchDailyEnglishWordsText(): String = withContext(Dispatchers.IO) {
    val today = LocalDate.now().toString()
    val model = Firebase.vertexAI.generativeModel("gemini-2.0-flash")

    val prompt = """
Sen 5. sınıf öğrencileri için eğlenceli bir İngilizce öğretmenisin.
TARİH: $today

GÖREV:
- Günlük hayattan 5 tane basit İngilizce kelime seç (A1-A2 seviyesi).
- Her kelimeyi şu formatta yaz:
  🔹 **WORD** (Okunuşu) - Türkçe Anlamı
     *Örnek:* Sentence in English. (Türkçesi)

- En alta "🧠 MİNİ QUIZ" başlığıyla bu kelimelerden biriyle ilgili 1 tane çoktan seçmeli soru sor.

KURALLAR:
- Emoji kullan.
- Markdown kullanma (Bold yapma), düz metin olsun.
- Samimi ve öğretici ol.
""".trimIndent()

    try {
        (model.generateContent(prompt).text ?: "").trim()
    } catch (_: Exception) {
        ""
    }
}