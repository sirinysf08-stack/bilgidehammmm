package com.example.bilgideham

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Veri Modeli
data class ScienceFactData(
    val topic: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScienceFactScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Kullanıcı seviyesini al
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val userLevel = educationPrefs.level
    val userGrade = educationPrefs.grade

    // --- DURUMLAR ---
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var factData by remember { mutableStateOf<ScienceFactData?>(null) }
    var retryTick by remember { mutableStateOf(0) }

    // Bilgi Çekme
    LaunchedEffect(retryTick) {
        loading = true
        error = null
        try {
            val result = fetchDailyScienceFact(userLevel, userGrade)
            factData = result
        } catch (e: Exception) {
            error = "Bağlantı hatası oluştu. Lütfen tekrar dene."
        } finally {
            loading = false
        }
    }

    // Yardımcı Fonksiyonlar
    fun shareFact(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$text\n\n- BilgiDeham Uygulamasından Gönderildi 🚀")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bilgiyi Paylaş")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            // --- MODERN HEADER ---
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)))
            ) {
                // Bilimsel Efekt (Moleküller)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    repeat(15) {
                        drawCircle(
                            color = Color.White,
                            radius = (2..5).random().dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset((0..size.width.toInt()).random().toFloat(), (0..size.height.toInt()).random().toFloat()),
                            alpha = 0.3f
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxSize().padding(top = 24.dp, start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White) }
                    Spacer(Modifier.width(8.dp))
                    Text("Günün Bilimi 🧬", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- YÜKLENİYOR ---
            if (loading) {
                Spacer(Modifier.height(80.dp))
                CircularProgressIndicator(
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 6.dp
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Laboratuvardan veriler geliyor... 🧪",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                return@Column
            }

            // --- HATA ---
            if (error != null) {
                ErrorCard(error = error!!, onRetry = { retryTick++ })
                return@Column
            }

            // --- İÇERİK ---
            if (factData != null) {
                val data = factData!!

                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // 1. Konu Başlığı (Chip)
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = "KONU: ${data.topic.uppercase()}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // 2. Ana Kart
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        null,
                                        tint = Color(0xFFFF6F00), // Amber Rengi
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Biliyor muydun?",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Divider(Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(0.3f))

                                Text(
                                    text = data.content,
                                    fontSize = 18.sp,
                                    lineHeight = 28.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // 3. Aksiyon Butonları (Paylaş ve Kopyala)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center // Ortala
                        ) {
                            // Paylaş
                            ActionButton(
                                icon = Icons.Default.Share,
                                label = "Paylaş",
                                color = MaterialTheme.colorScheme.tertiary,
                                onClick = { shareFact(data.content) }
                            )

                            Spacer(Modifier.width(40.dp)) // Butonlar arası boşluk

                            // Kopyala
                            ActionButton(
                                icon = Icons.Default.ContentCopy,
                                label = "Kopyala",
                                color = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Bilim Bilgisi", data.content)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Kopyalandı!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        // 4. Yeni Bilgi Butonu
                        Button(
                            onClick = { retryTick++ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Yeni Bir Bilgi Getir", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp) // Butonları biraz büyüttüm
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ErrorCard(error: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Hata Oluştu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.height(8.dp))
            Text(error, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Tekrar Dene") }
        }
    }
}

// Backend Fonksiyonu (Aynı Kalıyor)
private suspend fun fetchDailyScienceFact(level: EducationLevel, grade: Int?): ScienceFactData = withContext(Dispatchers.IO) {
    // Seviyeye göre konular
    val topics = when {
        level == EducationLevel.ILKOKUL && grade == 3 -> listOf(
            "Hayvanlar", "Bitkiler", "Mevsimler", "Su", "Hava", "Toprak",
            "Güneş", "Ay", "Yıldızlar", "Gece ve Gündüz", "Vücudumuz", "Duyularımız"
        )
        level == EducationLevel.ILKOKUL && grade == 4 -> listOf(
            "Yer Kabuğu", "Kayaçlar", "Mineraller", "Dünya'nın Hareketleri",
            "Besinler", "Kuvvet", "Madde", "Işık", "Ses", "Elektrik"
        )
        level == EducationLevel.ILKOKUL || grade == 5 -> listOf(
            "Ay'ın Evreleri", "Sürtünme Kuvveti", "Genleşme", "Mikroskobik Canlılar",
            "Dinamometre", "Işığın Yayılması", "Ses Yalıtımı", "Elektrik", "Biyoçeşitlilik",
            "Güneş Sistemi", "Fosiller", "Maddenin Halleri"
        )
        grade in 6..8 -> listOf(
            "DNA", "Hücre Bölünmesi", "Fotosentez", "Ekosistem", "Kimyasal Tepkimeler",
            "Asit-Baz", "Periyodik Tablo", "Atom Yapısı", "Kuvvet ve Hareket",
            "Enerji Dönüşümü", "Elektrik Devresi", "Işık Kırılması", "Ses Dalgaları"
        )
        level == EducationLevel.LISE -> listOf(
            "Kuantum Fiziği", "Genetik Mühendisliği", "Nöronlar", "Karadelikler",
            "Görelilik Teorisi", "Organik Kimya", "Termodinamik", "Elektromanyetizma",
            "Evrim", "Biyoteknoloji", "Nanoteknoloji", "Yapay Zeka"
        )
        else -> listOf(
            "Bilimsel Yöntem", "Araştırma Teknikleri", "İstatistik", "Veri Analizi",
            "Bilim Tarihi", "Bilim Felsefesi", "Teknoloji ve Toplum"
        )
    }
    val randomTopic = topics.random()

    // Seviyeye göre dil ve içerik
    val (targetAudience, complexity, sentenceCount) = when {
        level == EducationLevel.ILKOKUL && grade == 3 -> Triple("3. Sınıf öğrencilerine", "çok basit ve anlaşılır", "2-3")
        level == EducationLevel.ILKOKUL && grade == 4 -> Triple("4. Sınıf öğrencilerine", "basit ve anlaşılır", "3-4")
        level == EducationLevel.ILKOKUL || grade == 5 -> Triple("5. Sınıf öğrencilerine", "eğlenceli", "3-4")
        grade in 6..8 -> Triple("Ortaokul öğrencilerine", "bilimsel ama anlaşılır", "4-5")
        level == EducationLevel.LISE -> Triple("Lise öğrencilerine", "akademik ve detaylı", "5-6")
        else -> Triple("Yetişkinlere", "profesyonel ve kapsamlı", "5-7")
    }

    val model = Firebase.vertexAI.generativeModel("gemini-2.0-flash")

    val prompt = """
Rol: Sen $targetAudience hitap eden eğlenceli bir "Bilim Koçu"sun.
Konu: $randomTopic

GÖREV:
Bu konu hakkında okul kitabında her zaman yazmayan, "Vay be!" diyecekleri İLGİNÇ bir bilimsel gerçek yaz.

KURALLAR:
1. Sadece gerçeği yaz. Merhaba vb. deme.
2. $sentenceCount cümleyi geçmesin.
3. Dil seviyesi: $complexity
4. Sonunda mutlaka "Peki sence..." diye başlayan düşündürücü bir soru olsun.
5. Emoji kullan.
""".trimIndent()

    val content = try {
        (model.generateContent(prompt).text ?: "").trim()
    } catch (e: Exception) {
        "Şu an bilimsel veri tabanına ulaşamıyorum. Birazdan tekrar dener misin? 🧪"
    }

    ScienceFactData(randomTopic, content)
}