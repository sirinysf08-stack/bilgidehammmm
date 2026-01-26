package com.example.bilgideham

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Locale

private const val LEGACY_UL = '\u0332'
private const val MARKER_OPEN = "[["
private const val MARKER_CLOSE = "]]"
private const val HTML_UL_OPEN = "<u>"
private const val HTML_UL_CLOSE = "</u>"

/**
 * Soru metnini AnnotatedString'e çevirir
 * - [[kelime]] formatındaki metinleri altı çizili ve kalın yapar
 * - <u>kelime</u> formatındaki metinleri altı çizili ve kalın yapar
 * - Olumsuz kelimeleri (yanlıştır, değildir vb.) vurgular
 */
private fun buildQuestionAnnotatedString(rawQuestion: String): AnnotatedString {
    if (rawQuestion.isBlank()) return AnnotatedString("")

    // Legacy underline karakterlerini temizle
    var text = rawQuestion.replace(LEGACY_UL.toString(), "")

    // [[...]] formatını <u>...</u> formatına dönüştür
    text = text.replace(Regex("\\[\\[(.+?)]]")) { "<u>${it.groupValues[1]}</u>" }
    
    // **_..._** formatı
    text = text.replace(Regex("\\*\\*_(.+?)_\\*\\*")) { "<u>${it.groupValues[1]}</u>" }
    
    // _**...**_ formatı (AI bazen bu şekilde üretiyor)
    text = text.replace(Regex("_\\*\\*(.+?)\\*\\*_")) { "<u>${it.groupValues[1]}</u>" }
    
    // Basit _..._ formatı (markdown tarzı altı çizili)
    // Not: Kelimenin başında ve sonunda _ olmalı, içinde boşluk olabilir
    text = text.replace(Regex("(?<![a-zA-ZğüşöçıİĞÜŞÖÇ])_([^_]+)_(?![a-zA-ZğüşöçıİĞÜŞÖÇ])")) { "<u>${it.groupValues[1]}</u>" }


    return buildAnnotatedString {
        var currentIndex = 0

        while (currentIndex < text.length) {
            val markerStart = text.indexOf(HTML_UL_OPEN, currentIndex)

            if (markerStart < 0) {
                // Marker yok, kalan metni ekle
                append(text.substring(currentIndex))
                break
            }

            val markerEnd = text.indexOf(HTML_UL_CLOSE, markerStart + HTML_UL_OPEN.length)

            if (markerEnd < 0) {
                // Kapanış marker'ı yok, kalan metni ekle
                append(text.substring(currentIndex))
                break
            }

            // Marker öncesi metni ekle
            if (markerStart > currentIndex) {
                append(text.substring(currentIndex, markerStart))
            }

            // Marker içindeki metni altı çizili ekle (kalın değil)
            val markedText = text.substring(markerStart + HTML_UL_OPEN.length, markerEnd)
            withStyle(
                SpanStyle(
                    textDecoration = TextDecoration.Underline
                    // Kalın ve renk yok - sadece altı çizili
                )
            ) {
                append(markedText)
            }

            currentIndex = markerEnd + HTML_UL_CLOSE.length
        }
    }
}

/**
 * Düz metin için marker'ları temizler (şıklar için)
 */
private fun stripMarkers(text: String): String {
    return text
        .replace(LEGACY_UL.toString(), "")
        .replace(Regex("\\[\\[(.+?)]]"), "$1")
        .replace(Regex("<u>(.+?)</u>"), "$1")
        .replace(Regex("\\*\\*_(.+?)_\\*\\*"), "$1")
        .replace(Regex("_\\*\\*(.+?)\\*\\*_"), "$1")
        .replace(Regex("(?<![a-zA-ZğüşöçıİĞÜŞÖÇ0-9])_([^_]+)_(?![a-zA-ZğüşöçıİĞÜŞÖÇ0-9])"), "$1")
}

/**
 * Soru metninden grafik türünü otomatik tespit et
 */
private fun detectGraphicTypeFromQuestionText(text: String): String {
    val lowerText = text.lowercase(java.util.Locale("tr"))
    return when {
        // Pasta grafik önce kontrol et (öncelik önemli!)
        lowerText.contains("pasta grafik") || lowerText.contains("pasta dağ") || 
        lowerText.contains("daire grafik") || lowerText.contains("derece ile temsil") ||
        (lowerText.contains("dağılım") && lowerText.contains("grafik") && !lowerText.contains("yağış")) -> "pieChart"
        
        // Tablo - eşleştirme soruları dahil
        lowerText.contains("tabloya göre") || lowerText.contains("tabloda") ||
        lowerText.contains("aşağıdaki tablo") || lowerText.contains("eşleştirilmiştir") -> "table"
        
        // Çubuk/Sütun/Yağış grafik - geniş kapsam
        lowerText.contains("çubuk grafik") || lowerText.contains("sütun grafik") ||
        lowerText.contains("bar grafik") || lowerText.contains("yağış grafik") ||
        lowerText.contains("grafikte") && (
            lowerText.contains("yıl") || lowerText.contains("yağış") ||
            lowerText.contains("gelir") || lowerText.contains("satış") ||
            lowerText.contains("kar") || lowerText.contains("bütçe")
        ) -> "barChart"
        
        // Sayı doğrusu
        lowerText.contains("sayı doğrusu") || lowerText.contains("sayı eksen") -> "numberLine"
        
        // Koordinat
        lowerText.contains("koordinat") || lowerText.contains("grafik düzlem") -> "coordinate"
        
        // Grid
        lowerText.contains("birim kare") || lowerText.contains("ızgara") ||
        lowerText.contains("kareli") -> "grid"
        
        else -> ""
    }
}

/**
 * Grafik türü için varsayılan örnek veri üret
 * Soru metninden yıl bilgisi çıkararak daha uyumlu veri oluşturur
 */
private fun generateFallbackGraphicData(graphicType: String, questionText: String = ""): String {
    // Soru metninden yıl aralığı çıkar (örn: 2018-2022)
    val yearPattern = Regex("(\\d{4})[-–](\\d{4})")
    val yearMatch = yearPattern.find(questionText)
    val labels = if (yearMatch != null) {
        val startYear = yearMatch.groupValues[1].toIntOrNull() ?: 2018
        val endYear = yearMatch.groupValues[2].toIntOrNull() ?: 2022
        (startYear..endYear).map { "\"$it\"" }.joinToString(",")
    } else {
        "\"I\",\"II\",\"III\",\"IV\",\"V\""
    }
    
    val labelCount = if (yearMatch != null) {
        val startYear = yearMatch.groupValues[1].toIntOrNull() ?: 2018
        val endYear = yearMatch.groupValues[2].toIntOrNull() ?: 2022
        (endYear - startYear + 1).coerceIn(3, 7)
    } else 5
    
    val bars = (1..labelCount).map { (30..70).random() }.joinToString(",")
    
    return when (graphicType.lowercase()) {
        "numberline" -> """{"min":-5,"max":5,"points":{"A":-2,"B":3}}"""
        "piechart" -> """{"slices":[30,25,20,15,10],"labels":[$labels]}"""
        "table" -> {
            val rows = mutableListOf("""["Dönem","Değer"]""")
            if (yearMatch != null) {
                val startYear = yearMatch.groupValues[1].toIntOrNull() ?: 2018
                val endYear = yearMatch.groupValues[2].toIntOrNull() ?: 2022
                (startYear..endYear).forEach { year ->
                    rows.add("""["$year","${(30..70).random()}"]""")
                }
            } else {
                rows.addAll(listOf("""["I","45"]""","""["II","52"]""","""["III","38"]""","""["IV","61"]""","""["V","55"]"""))
            }
            """{"rows":[${rows.joinToString(",")}]}"""
        }
        "barchart" -> """{"bars":[$bars],"labels":[$labels]}"""
        "grid" -> """{"cols":5,"rows":5,"filled":[[0,0,"blue"],[1,1,"red"],[2,2,"green"]]}"""
        "coordinate" -> """{"minX":-5,"maxX":5,"minY":-5,"maxY":5,"points":[{"label":"A","x":2,"y":3},{"label":"B","x":-1,"y":2}]}"""
        else -> ""
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    lessonTitle: String,
    questionCount: Int = 10,
    preLoadedQuestions: List<QuestionModel> = emptyList(),
    examDurationMinutes: Int = 0,
    startQuestionIndex: Int = 0
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cloudUserId = remember { CloudUserId.getOrCreate(context) }
    val TAG = "QUIZ_DEBUG"
    val cs = MaterialTheme.colorScheme

    // Kullanıcının eğitim tercihleri
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    
    // DEBUG: Profil değerlerini logla
    LaunchedEffect(Unit) {
        DebugLog.d(TAG, "🎓 QUIZ BAŞLADI - Profil Bilgileri:")
        DebugLog.d(TAG, "   - Level: ${educationPrefs.level.name}")
        DebugLog.d(TAG, "   - SchoolType: ${educationPrefs.schoolType.name}")
        DebugLog.d(TAG, "   - Grade: ${educationPrefs.grade}")
        DebugLog.d(TAG, "   - LessonTitle: $lessonTitle")
    }

    // --- MOD KONTROLLERİ ---
    val isStructuredExam = remember(lessonTitle) {
        lessonTitle == "GENEL_DENEME" || lessonTitle == "MARATON"
    }
    val isRealExamMode = remember(examDurationMinutes, isStructuredExam) {
        examDurationMinutes > 0 || isStructuredExam || lessonTitle.contains("Deneme", ignoreCase = true)
    }
    val isParagraphMode = remember(lessonTitle) {
        lessonTitle.contains("Paragraf", ignoreCase = true)
    }
    val isInfiniteMode = remember(examDurationMinutes, preLoadedQuestions, isParagraphMode, isRealExamMode) {
        examDurationMinutes == 0 && preLoadedQuestions.isEmpty() && !isParagraphMode && !isRealExamMode
    }

    // --- STATE'LER ---
    var questions by remember { mutableStateOf<List<QuestionModel>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(startQuestionIndex) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPoolEmpty by remember { mutableStateOf(false) } // Havuz boş mu?
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var showLessonTransitionDialog by remember { mutableStateOf(false) }
    var justFinishedLesson by remember { mutableStateOf("") }
    var nextLessonName by remember { mutableStateOf("") }
    var timeLeft by remember {
        mutableLongStateOf(if (examDurationMinutes > 0) examDurationMinutes * 60L else 0L)
    }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }

    val sessionSeen = remember { linkedSetOf<String>() }
    
    // KPSS Deneme Paketi Numarasını Çıkar
    val kpssPaketNo = remember(lessonTitle) {
        if (lessonTitle.contains("kpss_deneme_", ignoreCase = true)) {
            lessonTitle.substringAfterLast("_").toIntOrNull()
        } else null
    }

    // İLERLEME KAYDETME FONKSİYONU
    fun saveProgress(status: String = "devam_ediyor") {
        if (kpssPaketNo == null) return
        
        scope.launch(Dispatchers.IO) {
            val durum = QuestionRepository.DenemeDurumu(
                paketNo = kpssPaketNo,
                durum = status,
                sonKalinanSoru = currentQuestionIndex + 1, // 1-based
                dogru = correctCount,
                yanlis = wrongCount,
                bos = 120 - (correctCount + wrongCount), // Basit hesap
                baslangicTarihi = System.currentTimeMillis() // Geçici (start zamanı state'de tutulmalı aslında)
            )
            QuestionRepository.saveDenemeDurumu(cloudUserId, kpssPaketNo, durum)
            // Log ekle
            // DebugLog.d("QUIZ_SAVE", "Progress saved: Index $currentQuestionIndex")
        }
    }

    // Her soru değişiminde kaydet
    LaunchedEffect(currentQuestionIndex) {
        if (isRealExamMode && kpssPaketNo != null) {
            saveProgress()
        }
    }
    
    // Ekrandan çıkarken kaydet
    DisposableEffect(Unit) {
        onDispose {
            if (isRealExamMode && kpssPaketNo != null) {
                saveProgress()
            }
        }
    }

    fun safeDocId(q: QuestionModel): String {
        return runCatching { QuestionRepository.computeDocIdForQuestion(q) }
            .getOrElse { (q.question ?: "").trim().lowercase(Locale.US) }
    }

    fun filterOutSeenAndSolved(list: List<QuestionModel>, solvedSet: Set<String>): List<QuestionModel> {
        val out = ArrayList<QuestionModel>(list.size)
        for (q in list) {
            val id = safeDocId(q)
            if (id.isBlank()) {
                DebugLog.d(TAG, "❌ Boş ID: ${q.question?.take(30)}")
                continue
            }
            if (id in solvedSet) {
                DebugLog.d(TAG, "❌ Çözülmüş: $id")
                continue
            }
            if (id in sessionSeen) {
                DebugLog.d(TAG, "❌ Session'da görüldü: $id")
                continue
            }
            // sessionSeen'e burada EKLEMİYORUZ - sadece gösterildikten sonra eklenecek
            out.add(q)
        }
        DebugLog.d(TAG, "✅ Filtreden geçen: ${out.size} soru")
        return out
    }

    fun shuffleOptions(q: QuestionModel): QuestionModel {
        // ŞIK KARIŞTIRMA DEVRE DIŞI
        // Sebep: AI ürettiği sorularda explanation içinde şık referansları var (örn: "A seçeneğinde...")
        // Şıkları karıştırınca explanation tutarsız oluyor.
        // Şıklar orijinal sırada kalacak.
        return q
    }

    suspend fun loadSolvedSet(): Set<String> {
        return try {
            HistoryRepository.getSolvedQuestionFps().toSet()
        } catch (e: Exception) { 
            Log.w(TAG, "loadSolvedSet hatası: ${e.message}")
            emptySet() 
        }
    }


    // --- VERİ YÜKLEME ---
    suspend fun loadInitialQuestions() {
        withContext(Dispatchers.IO) {
            try {
                withTimeout(45000L) {
                    runCatching {
                        LessonRepositoryLocal.init(context)
                        HistoryRepository.init(context)
                    }

                    // Daha önce çözülmüş soruların fingerprint'lerini al
                    val solved = loadSolvedSet()
                    DebugLog.d(TAG, "Çözülmüş soru sayısı: ${solved.size}")

                    if (preLoadedQuestions.isNotEmpty()) {
                        val filtered = filterOutSeenAndSolved(preLoadedQuestions, solved).map { shuffleOptions(it) }
                        withContext(Dispatchers.Main) {
                            questions = filtered
                            isLoading = false
                            if (filtered.isEmpty()) {
                                isPoolEmpty = true
                                errorMessage = null
                            }
                        }
                        return@withTimeout
                    }

                    val fetchLimit = if (isParagraphMode) questionCount else 50 // Daha fazla çek, filtreleme için

                    // Önce seviye bazlı soru çekmeyi dene
                    var newQs = try {
                        // Yapılandırılmış sınav modu (GENEL_DENEME/MARATON)
                        if (isStructuredExam) {
                            DebugLog.d(TAG, "Yapılandırılmış sınav modu: $lessonTitle")
                            QuestionRepository.getQuestionsForMixedExam(
                                examType = lessonTitle,
                                level = educationPrefs.level,
                                schoolType = educationPrefs.schoolType,
                                grade = educationPrefs.grade,
                                userId = cloudUserId,
                                excludeDocIds = solved + sessionSeen
                            )
                        } else {
                            // AGS ÖABT Üniteleri için özel hızlı yol - DOĞRUDAN *_unite_X formatı
                            if (educationPrefs.schoolType == SchoolType.AGS_OABT && lessonTitle.contains("_unite_")) {
                                DebugLog.d(TAG, "📚 AGS ÖABT ünite özel sorgu (doğrudan): $lessonTitle")
                                QuestionRepository.getQuestionsForAgsTarih(
                                    subjectId = lessonTitle,
                                    limit = fetchLimit,
                                    excludeDocIds = solved + sessionSeen
                                )
                            } else if (lessonTitle.startsWith("kpss_deneme_")) {
                                val paketNo = lessonTitle.removePrefix("kpss_deneme_").toIntOrNull() ?: 1
                                QuestionRepository.getKpssDenemeSorulari(paketNo)
                            } else {
                                // Normal ders modu
                                // Subject ID'yi CurriculumManager'dan bul
                                val subjects = CurriculumManager.getSubjectsFor(educationPrefs.schoolType, educationPrefs.grade)
                                val matchedSubject = subjects.find { 
                                    it.displayName.equals(lessonTitle, ignoreCase = true) ||
                                    it.displayName.contains(lessonTitle.replace(" KPSS", ""), ignoreCase = true) ||
                                    lessonTitle.contains(it.displayName, ignoreCase = true)
                                }
                            
                                val lessonId = matchedSubject?.id ?: run {
                                    // Paragraf için özel işlem - sınıf bazlı ID oluştur
                                    val normalizedTitle = lessonTitle.lowercase(Locale("tr", "TR"))
                                        .replace(" ", "_")
                                        .replace("ı", "i")
                                        .replace("ö", "o")
                                        .replace("ü", "u")
                                        .replace("ş", "s")
                                        .replace("ğ", "g")
                                        .replace("ç", "c")
                                    
                                    // Özel Ders ID Eşleştirmeleri
                                    when {
                                        // Siyer / Hz. Muhammed varyasyonları
                                        normalizedTitle.contains("hz") && normalizedTitle.contains("muhammed") -> 
                                            if (educationPrefs.grade != null) "siyer_${educationPrefs.grade}" else "siyer"
                                        normalizedTitle.contains("siyer") -> 
                                            if (educationPrefs.grade != null) "siyer_${educationPrefs.grade}" else "siyer"
                                        normalizedTitle.contains("peygamber") -> 
                                            if (educationPrefs.grade != null) "siyer_${educationPrefs.grade}" else "siyer"
                                            
                                        // Paragraf için sınıf eklentisi
                                        normalizedTitle == "paragraf" && educationPrefs.grade != null -> 
                                            "paragraf_${educationPrefs.grade}"
                                            
                                        else -> normalizedTitle
                                    }
                                }
                                
                                DebugLog.d(TAG, "LessonId: $lessonId (from ${matchedSubject?.displayName ?: "normalized"})")
                                
                                QuestionRepository.getQuestionsForLevel(
                                    level = educationPrefs.level,
                                    schoolType = educationPrefs.schoolType,
                                    grade = educationPrefs.grade,
                                    lessonId = lessonId,
                                    limit = fetchLimit,
                                    userId = cloudUserId,
                                    excludeDocIds = solved + sessionSeen
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Firestore hatası: ${e.message}")
                        emptyList()
                    }

                    // Çözülmüş soruları filtrele
                    newQs = filterOutSeenAndSolved(newQs, solved)
                    DebugLog.d(TAG, "Filtreleme sonrası soru sayısı: ${newQs.size}")

                    withContext(Dispatchers.Main) {
                        if (newQs.isNotEmpty()) {
                            questions = newQs.map { shuffleOptions(it) }
                            errorMessage = null
                            isPoolEmpty = false
                        } else {
                            // Havuz boş - tüm sorular çözülmüş
                            isPoolEmpty = true
                            errorMessage = null
                        }
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Yükleme hatası: ${e.message}")
                withContext(Dispatchers.Main) {
                    errorMessage = "Bir hata oluştu: ${e.localizedMessage}"
                    isLoading = false
                }
            }
        }
    }

    fun loadMoreQuestions() {
        if (isLoadingMore) return
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoadingMore = true }
            try {
                val solved = loadSolvedSet()
                var moreQs = try {
                    // Önce seviye bazlı soru çekmeyi dene
                    val levelQuestions = QuestionRepository.getQuestionsForLevel(
                        level = educationPrefs.level,
                        schoolType = educationPrefs.schoolType,
                        grade = educationPrefs.grade,
                        lessonId = lessonTitle.lowercase(Locale("tr", "TR"))
                            .replace(" ", "_")
                            .replace("ı", "i")
                            .replace("ö", "o")
                            .replace("ü", "u")
                            .replace("ş", "s")
                            .replace("ğ", "g")
                            .replace("ç", "c"),
                        limit = 30,
                        userId = cloudUserId,
                        excludeDocIds = solved + sessionSeen
                    )
                    levelQuestions
                } catch (e: Exception) { emptyList() }

                moreQs = filterOutSeenAndSolved(moreQs, solved)
                val uniqueQs = moreQs.filter { newQ ->
                    questions.none { safeDocId(it) == safeDocId(newQ) }
                }.map { shuffleOptions(it) }

                withContext(Dispatchers.Main) {
                    if (uniqueQs.isNotEmpty()) {
                        questions = questions + uniqueQs
                    } else {
                        // Ek soru yok - havuz tükendi
                        isPoolEmpty = true
                    }
                    isLoadingMore = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoadingMore = false }
            }
        }
    }

    // Sorular yükleme - hemen başla
    LaunchedEffect(Unit) { 
        loadInitialQuestions() 
    }

    LaunchedEffect(timeLeft, isLoading, isRealExamMode) {
        if (isRealExamMode && !isLoading && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    LaunchedEffect(currentQuestionIndex, isInfiniteMode) {
        if (isInfiniteMode && !isLoadingMore && questions.isNotEmpty()) {
            if (currentQuestionIndex >= questions.size - 2) loadMoreQuestions()
        }
    }


    // --- UI ---
    if (showLessonTransitionDialog) {
        ModernTransitionOverlay(
            finishedLesson = justFinishedLesson,
            nextLesson = nextLessonName,
            onContinue = {
                showLessonTransitionDialog = false
                currentQuestionIndex++
                selectedOption = null
                isAnswerChecked = false
            }
        )
    }

    val pageBg = cs.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
            .statusBarsPadding()
    ) {
        when {

            isLoading -> {
                ModernLoadingAnimation(
                    message = if (isStructuredExam) "Sınav Ortamı Hazırlanıyor..." else "Yapay Zeka Soruları Hazırlıyor",
                    subMessage = "Müfredat ve kazanımlar analiz ediliyor"
                )
            }

            errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = cs.error, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage ?: "Bilinmeyen bir hata oluştu", color = cs.error, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                        ) { Text("Geri Dön") }
                    }
                }
            }

            // HAVUZ BOŞ - Tüm sorular çözülmüş
            isPoolEmpty && questions.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inventory2,
                                null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Soru Havuzu Tükendi!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = cs.onSurface
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Tebrikler! 🎉\n\n$lessonTitle dersindeki tüm soruları çözdün.\n\nYeni sorular eklendiğinde tekrar gel!",
                                fontSize = 15.sp,
                                color = cs.onSurface.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                            ) {
                                Text("Ana Sayfaya Dön", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }


            questions.isNotEmpty() && currentQuestionIndex < questions.size -> {
                val currentQuestion = questions[currentQuestionIndex]
                
                // Gösterilen soruyu session'a ekle (tekrar gösterilmemesi için)
                sessionSeen.add(safeDocId(currentQuestion))
                
                // AGS Tarih üniteleri için okunabilir başlık
                val displayTitle = if (educationPrefs.schoolType == SchoolType.AGS_OABT && lessonTitle.contains("_unite_")) {
                    val subjects = AppPrefs.getCurrentSubjects(context)
                    val matched = subjects.find { it.route == lessonTitle || it.id == lessonTitle }
                    matched?.displayName ?: lessonTitle
                } else if (lessonTitle.startsWith("kpss_deneme_")) {
                    val paketNo = lessonTitle.removePrefix("kpss_deneme_").toIntOrNull() ?: 1
                    "$paketNo. Deneme"
                } else {
                    lessonTitle
                }
                val countDisplay = if (isInfiniteMode) "${currentQuestionIndex + 1}" else "${currentQuestionIndex + 1} / ${questions.size}"
                val progressVal = if (isInfiniteMode) 1f else (currentQuestionIndex + 1f) / questions.size

                val feedbackEnabled = !isRealExamMode && !isParagraphMode && isAnswerChecked && selectedOption != null
                val isCorrectInPractice = feedbackEnabled && (selectedOption == currentQuestion.correctAnswer)
                val isWrongInPractice = feedbackEnabled && (selectedOption != currentQuestion.correctAnswer)

                ModernQuizHeader(
                    title = displayTitle,
                    countDisplay = countDisplay,
                    progress = progressVal,
                    timeLeftSeconds = timeLeft,
                    showTimer = isRealExamMode,
                    onBack = { navController.popBackStack() }
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = cs.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(Modifier.padding(24.dp)) {
                                Text(
                                    text = buildQuestionAnnotatedString(currentQuestion.question ?: "..."),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cs.onSurface,
                                    lineHeight = 26.sp
                                )
                                
                                // Grafik türünü tespit et (veritabanında boşsa soru metninden çıkar)
                                val detectedGraphicType = currentQuestion.graphicType.ifBlank {
                                    detectGraphicTypeFromQuestionText(currentQuestion.question ?: "")
                                }
                                val graphicData = currentQuestion.graphicData.ifBlank {
                                    if (detectedGraphicType.isNotBlank()) 
                                        generateFallbackGraphicData(detectedGraphicType, currentQuestion.question ?: "") 
                                    else ""
                                }
                                
                                // Vega-Lite Chart gösterimi (chart_questions'dan gelen)
                                if (detectedGraphicType == "vega_chart" && graphicData.isNotBlank()) {
                                    Spacer(Modifier.height(16.dp))
                                    VegaLiteChartView(
                                        vegaSpec = graphicData,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                    )
                                }
                                // SVG grafik gösterimi (eski sistem - diğer grafik türleri)
                                else if (detectedGraphicType.isNotBlank() && detectedGraphicType != "vega_chart") {
                                    Spacer(Modifier.height(16.dp))
                                    QuestionGraphicRenderer(
                                        graphicType = detectedGraphicType,
                                        graphicData = graphicData
                                    )
                                }
                                
                                // Imagen görsel gösterimi (Base64 resimler - varsa)
                                currentQuestion.imageBase64?.takeIf { it.isNotBlank() }?.let { base64 ->
                                    Spacer(Modifier.height(16.dp))
                                    QuestionImageDisplay(
                                        base64 = base64,
                                        mimeType = currentQuestion.imageMimeType ?: "image/png"
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    // E şıkkı KPSS, AGS ve Lise için gösterilir (YKS/TYT/AYT formatı)
                    val showOptionE = educationPrefs.level in listOf(EducationLevel.KPSS, EducationLevel.AGS, EducationLevel.LISE)
                    
                    // İlkokul (1-4. sınıf) için sadece A, B, C şıkkı gösterilir
                    val isIlkokul = educationPrefs.level == EducationLevel.ILKOKUL
                    val showOptionD = !isIlkokul
                    
                    val options = listOf(
                        "A" to (currentQuestion.optionA ?: ""),
                        "B" to (currentQuestion.optionB ?: ""),
                        "C" to (currentQuestion.optionC ?: "")
                    ) + if (showOptionD) {
                        currentQuestion.optionD?.takeIf { it.isNotBlank() }?.let { 
                            listOf("D" to it)
                        } ?: emptyList()
                    } else {
                        emptyList()
                    } + if (showOptionE) {
                        currentQuestion.optionE?.takeIf { it.isNotBlank() }?.let { 
                            listOf("E" to it)
                        } ?: emptyList()
                    } else {
                        emptyList()
                    }

                    items(items = options, key = { it.first }) { opt ->
                        val isSelected = selectedOption == opt.first
                        val isCorrectOption = opt.first == currentQuestion.correctAnswer

                        val highlightCorrect = (isCorrectInPractice && isSelected) || (isWrongInPractice && isCorrectOption)
                        val highlightWrongSelected = isWrongInPractice && isSelected && !isCorrectOption

                        val correctBorder = Color(0xFF2E7D32)
                        val wrongBorder = Color(0xFFC62828)

                        val borderColor = when {
                            highlightCorrect -> correctBorder
                            highlightWrongSelected -> wrongBorder
                            isSelected -> cs.primary
                            else -> cs.outline.copy(alpha = 0.35f)
                        }

                        val bgColor = when {
                            highlightCorrect -> correctBorder.copy(alpha = 0.14f)
                            highlightWrongSelected -> wrongBorder.copy(alpha = 0.14f)
                            isSelected -> cs.primary.copy(alpha = 0.10f)
                            else -> cs.surface
                        }

                        val badgeBg = when {
                            highlightCorrect -> correctBorder
                            highlightWrongSelected -> wrongBorder
                            isSelected -> cs.primary
                            else -> cs.surfaceVariant
                        }

                        val badgeTextColor = when {
                            highlightCorrect || highlightWrongSelected || isSelected -> Color.White
                            else -> cs.onSurfaceVariant
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    if (isSelected || highlightCorrect || highlightWrongSelected) 2.dp else 1.dp,
                                    borderColor,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isAnswerChecked || isRealExamMode) {
                                    if (!isAnswerChecked || isRealExamMode) selectedOption = opt.first
                                },
                            colors = CardDefaults.cardColors(containerColor = bgColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(badgeBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(opt.first, fontWeight = FontWeight.Bold, color = badgeTextColor)
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = stripMarkers(opt.second),
                                    color = cs.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }


                    item {
                        if (isWrongInPractice) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant),
                                modifier = Modifier.padding(top = 16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Öğretmen Notu:", fontWeight = FontWeight.Bold, color = cs.secondary)
                                    Text(
                                        (currentQuestion.explanation ?: "").trim(),
                                        fontSize = 14.sp,
                                        color = cs.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Doğru Cevap: ${currentQuestion.correctAnswer}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }

                // Son soruya gelindi ve havuz tükendi uyarısı
                val isLastQuestion = currentQuestionIndex == questions.size - 1
                val showPoolEmptyWarning = isLastQuestion && isPoolEmpty && isAnswerChecked

                val buttonText = when {
                    showPoolEmptyWarning -> "Havuz Tükendi - Bitir"
                    isRealExamMode && isLastQuestion -> "Sınavı Bitir"
                    isRealExamMode -> "İşaretle ve Geç"
                    !isInfiniteMode && isLastQuestion && isAnswerChecked -> "Dersi Bitir"
                    !isAnswerChecked -> "Cevabı Kontrol Et"
                    else -> "Sonraki Soru"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.surface)
                        .padding(20.dp)
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                ) {
                    Column {
                        // Havuz tükendi uyarısı
                        if (showPoolEmptyWarning) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Inventory2,
                                        null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Bu dersteki tüm soruları çözdün! 🎉",
                                        fontSize = 14.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val isCorrect = selectedOption == currentQuestion.correctAnswer

                                if (selectedOption != null) {
                                    scope.launch(Dispatchers.IO) {
                                        runCatching {
                                            HistoryRepository.saveAnswer(
                                                currentQuestion,
                                                selectedOption!!,
                                                currentQuestion.lesson ?: "Genel",
                                                cloudUserId
                                            )
                                            StatsManager(context).addResult(
                                                lessonRaw = currentQuestion.lesson ?: "Genel",
                                                correctDelta = if (isCorrect) 1 else 0,
                                                wrongDelta = if (isCorrect) 0 else 1
                                            )
                                        }
                                    }
                                    if (isRealExamMode && isCorrect) correctCount++
                                    if (isRealExamMode && !isCorrect) wrongCount++
                                }

                                if (isRealExamMode) {
                                    if (currentQuestionIndex + 1 < questions.size) {
                                        val nextQ = questions[currentQuestionIndex + 1]
                                        if (nextQ.lesson != currentQuestion.lesson) {
                                            justFinishedLesson = currentQuestion.lesson ?: ""
                                            nextLessonName = nextQ.lesson ?: ""
                                            showLessonTransitionDialog = true
                                        } else {
                                            currentQuestionIndex++
                                            selectedOption = null
                                        }
                                    } else {
                                        val totalTime = (examDurationMinutes * 60) - timeLeft
                                        navController.navigate("exam_result/$correctCount/$wrongCount/${questions.size}/${formatTime(totalTime)}") {
                                            popUpTo("practice_exam_screen") { inclusive = false }
                                        }
                                    }
                                } else {
                                    if (!isAnswerChecked) {
                                        if (selectedOption != null) {
                                            isAnswerChecked = true
                                            if (isCorrect) correctCount++ else wrongCount++
                                        }
                                    } else {
                                        if (currentQuestionIndex < questions.size - 1) {
                                            currentQuestionIndex++
                                            selectedOption = null
                                            isAnswerChecked = false
                                        } else {
                                            // Son soru - sonuç ekranına git
                                            navController.navigate("exam_result/$correctCount/$wrongCount/${questions.size}/00:00") {
                                                popUpTo("home") { inclusive = false }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showPoolEmptyWarning) Color(0xFFFF9800) else cs.primary
                            ),
                            enabled = selectedOption != null || (isAnswerChecked && !isRealExamMode)
                        ) {
                            if (isLoadingMore && isAnswerChecked && currentQuestionIndex == questions.size - 1) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(buttonText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ModernQuizHeader(
    title: String,
    countDisplay: String,
    progress: Float,
    timeLeftSeconds: Long,
    showTimer: Boolean,
    onBack: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        Modifier
            .background(cs.surface)
            .border(0.5.dp, cs.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = cs.onSurface)
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
                if (showTimer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer, null,
                            modifier = Modifier.size(14.dp),
                            tint = cs.onSurface.copy(alpha = 0.65f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            formatTime(timeLeftSeconds),
                            fontSize = 12.sp,
                            color = cs.onSurface.copy(alpha = 0.65f)
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(color = cs.primary.copy(alpha = 0.14f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = countDisplay,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = cs.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (progress <= 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = cs.primary,
                trackColor = cs.surfaceVariant
            )
        }
    }
}

@Composable
fun ModernTransitionOverlay(
    finishedLesson: String,
    nextLesson: String,
    onContinue: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.70f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(70.dp)
                            .background(cs.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DoneAll, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "$finishedLesson Tamamlandı!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = cs.outline.copy(alpha = 0.30f))
                    Spacer(Modifier.height(16.dp))
                    Text("Sıradaki Ders:", color = cs.onSurface.copy(alpha = 0.70f), fontSize = 14.sp)
                    Text(nextLesson, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = cs.primary)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                    ) {
                        Text("Devam Et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
