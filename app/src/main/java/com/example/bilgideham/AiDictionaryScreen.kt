package com.example.bilgideham

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Kelime Sonuç Modeli
data class WordDefinition(
    val definition: String,
    val synonym: String,
    val exampleSentence: String,
    val lessonType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDictionaryScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf<WordDefinition?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Kritik: "Tanım bulunamadı" mesajını sadece ARAMA yapıldıktan sonra göstermek için
    var hasSearched by remember { mutableStateOf(false) }

    // Eski/yarışan istekleri ekranda göstermemek için basit token
    var lastRequestToken by remember { mutableStateOf(0L) }

    fun normalizeRaw(raw: String): String {
        return raw
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trim()
    }

    fun extractLabeledBlock(raw: String, labels: List<String>): String? {
        // Çok satırlı blok yakalama:
        // ^(label)\s*:\s*(content...)(?=^\s*anotherLabel\s*:|\z)
        val allLabels = listOf(
            "Tanım", "Tanim", "Anlam", "Açıklama", "Aciklama",
            "Eş Anlamlısı", "Es Anlamlisi", "Eş Anlamlı", "Es Anlamli", "Eşanlamlı", "Esanlamli",
            "Örnek Cümle", "Ornek Cumle", "Örnek", "Ornek", "Cümle", "Cumle",
            "Ders Tipi", "Ders", "Alan", "Kategori"
        ).distinct()

        val labelAlternation = allLabels.joinToString("|") { Regex.escape(it) }

        for (label in labels) {
            val pattern = Regex(
                pattern = "(?ims)^\\s*(?:[-•*]\\s*)?(?:${Regex.escape(label)})\\s*:\\s*(.*?)\\s*(?=^\\s*(?:[-•*]\\s*)?(?:$labelAlternation)\\s*:\\s*|\\z)",
                options = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
            )
            val m = pattern.find(raw) ?: continue
            val v = m.groupValues.getOrNull(1)?.trim()?.trim('"', '“', '”', '’', '‘')
            if (!v.isNullOrBlank()) return v
        }
        return null
    }

    fun parseWordDefinition(rawInput: String): WordDefinition? {
        val raw = normalizeRaw(rawInput)
        if (raw.isBlank()) return null

        val def = extractLabeledBlock(raw, listOf("Tanım", "Tanim", "Anlam", "Açıklama", "Aciklama"))
        val syn = extractLabeledBlock(raw, listOf("Eş Anlamlısı", "Es Anlamlisi", "Eş Anlamlı", "Es Anlamli", "Eşanlamlı", "Esanlamli"))
        val ex = extractLabeledBlock(raw, listOf("Örnek Cümle", "Ornek Cumle", "Örnek", "Ornek", "Cümle", "Cumle"))
        val lesson = extractLabeledBlock(raw, listOf("Ders Tipi", "Ders", "Alan", "Kategori"))

        // En azından tanım veya örnek gelmeden "başarılı" saymayalım
        val hasMeaningful = !def.isNullOrBlank() || !ex.isNullOrBlank()
        if (!hasMeaningful) return null

        return WordDefinition(
            definition = def?.takeIf { it.isNotBlank() } ?: "Tanım bulunamadı.",
            synonym = syn?.takeIf { it.isNotBlank() } ?: "Yok.",
            exampleSentence = ex?.takeIf { it.isNotBlank() } ?: "Örnek bulunamadı.",
            lessonType = lesson?.takeIf { it.isNotBlank() } ?: "Genel"
        )
    }

    fun searchWord() {
        val word = searchText.trim()
        if (word.isBlank()) return

        hasSearched = true
        isLoading = true
        definition = null

        val token = System.currentTimeMillis()
        lastRequestToken = token

        scope.launch {
            try {
                val prompt = """
                    Görev: Öğrencinin aradığı "$word" kelimesi için 5. Sınıf seviyesine uygun sözlük çıktısı üret.

                    ZORUNLU FORMAT (yalnızca bu 4 satır):
                    Tanım: ...
                    Eş Anlamlısı: ...
                    Örnek Cümle: ...
                    Ders Tipi: ...
                """.trimIndent()

                val rawResponse = aiGenerateText(prompt)
                val parsed = parseWordDefinition(rawResponse)

                // Stale response guard
                if (lastRequestToken != token) return@launch

                definition = parsed
            } catch (_: Throwable) {
                // Hata durumunda definition null kalır; UI hasSearched ile doğru mesajı gösterecek
                if (lastRequestToken == token) definition = null
            } finally {
                if (lastRequestToken == token) isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Akıllı Sözlük 🧠", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F4C3))
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFF0F4C3)) // Açık Sarı/Limon Rengi Arkaplan
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. SLOGAN VE AMAÇ KARTI ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC5E1A5)) // Açık Yeşil
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        null,
                        tint = Color(0xFF388E3C),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Slogan: Kelime Avcısı Panosu 🔎",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF388E3C)
                        )
                        Text(
                            "Amacı: Sadece kelime anlamını değil, cümle içinde nasıl kullanıldığını ve hangi derse ait olduğunu gösterir.",
                            fontSize = 12.sp,
                            color = Color(0xFF388E3C).copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // --- 2. ARAMA ÇUBUĞU ---
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    // Kullanıcı input değiştirince önceki sonuç ve hata state'i temizlensin
                    definition = null
                    hasSearched = false
                },
                label = { Text("Kelimeyi Yazınız...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    AnimatedVisibility(visible = searchText.isNotBlank() || isLoading) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp
                            )
                        } else {
                            IconButton(onClick = { searchWord() }) {
                                Icon(Icons.Default.ChevronRight, null, tint = cs.primary)
                            }
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // --- 3. SONUÇ EKRANI ---
            if (definition != null && !isLoading) {
                val def = definition!!

                // Ders Tipi Rozeti
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Chip(
                        label = { Text("Ders: ${def.lessonType}", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp)) },
                        color = cs.primary
                    )
                }

                // Tanım Kartı
                ResultCard(title = "Tanım", content = def.definition, icon = Icons.Default.Info, color = cs.primary)

                // Eş Anlamlı Kartı
                ResultCard(title = "Eş Anlamlısı", content = def.synonym, icon = Icons.Default.CompareArrows, color = cs.secondary)

                // Örnek Cümle Kartı
                ResultCard(
                    title = "Kullanım Örneği",
                    content = def.exampleSentence,
                    icon = Icons.Default.ChatBubble,
                    color = cs.tertiary,
                    isExample = true
                )

                Spacer(Modifier.height(30.dp))
            } else if (!isLoading && hasSearched) {
                // Sadece arama yapıldıktan sonra hata mesajı
                Text(
                    "Tanım bulunamadı. Lütfen kelimeyi kontrol ediniz.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else if (!isLoading && searchText.isBlank()) {
                Text(
                    "Yukarıdaki kutuya bir kelime yazıp arama ikonuna bas.",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun Chip(label: @Composable () -> Unit, icon: @Composable () -> Unit, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(4.dp))
            CompositionLocalProvider(LocalContentColor provides color) {
                label()
            }
        }
    }
}

@Composable
fun ResultCard(title: String, content: String, icon: ImageVector, color: Color, isExample: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            }
            Spacer(Modifier.height(8.dp))

            Text(
                text = content,
                fontSize = 16.sp,
                color = if (isExample) Color(0xFF388E3C) else Color.Black,
                fontStyle = if (isExample) FontStyle.Italic else FontStyle.Normal,
                lineHeight = 22.sp
            )
        }
    }
}
