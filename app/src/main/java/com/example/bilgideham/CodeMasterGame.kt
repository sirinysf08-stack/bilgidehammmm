package com.example.bilgideham

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==================== RENK PALETİ ====================
private val CodeBg = Color(0xFF1E1E2E)        // VS Code koyu tema
private val CodeSurface = Color(0xFF2D2D3D)
private val CodeAccent = Color(0xFF7C3AED)    // Mor vurgu
private val CodeSuccess = Color(0xFF10B981)   // Yeşil
private val CodeError = Color(0xFFEF4444)     // Kırmızı
private val CodeWarning = Color(0xFFF59E0B)   // Sarı
private val CodeKeyword = Color(0xFFFF79C6)   // Pembe (keyword)
private val CodeString = Color(0xFFF1FA8C)    // Sarı (string)
private val CodeNumber = Color(0xFFBD93F9)    // Mor (number)
private val CodeComment = Color(0xFF6272A4)   // Gri (comment)
private val CodeVariable = Color(0xFF8BE9FD)  // Cyan (variable)
private val CodeFunction = Color(0xFF50FA7B)  // Yeşil (function)

// ==================== VERİ MODELLERİ ====================

enum class CodeChapter(
    val title: String,
    val emoji: String,
    val description: String,
    val color: Color,
    val levelCount: Int
) {
    VARIABLES("Değişkenler", "📦", "Veri saklama ve kullanma", Color(0xFF3B82F6), 10),
    CONDITIONS("Koşullar", "🔀", "if/else karar yapıları", Color(0xFFF59E0B), 10),
    LOOPS("Döngüler", "🔄", "for/while tekrarlama", Color(0xFF10B981), 10),
    FUNCTIONS("Fonksiyonlar", "⚡", "Kod blokları oluşturma", Color(0xFFEC4899), 10),
    ALGORITHMS("Algoritmalar", "🧩", "Problem çözme teknikleri", Color(0xFF8B5CF6), 10),
    MASTER("Usta Seviye", "🏆", "Tüm becerileri birleştir", Color(0xFFEF4444), 10)
}

data class CodeLevel(
    val chapter: CodeChapter,
    val levelNum: Int,
    val title: String,
    val story: String,           // Hikaye/senaryo
    val lesson: String,          // Öğretilecek kavram
    val codeTemplate: String,    // Başlangıç kodu
    val correctCode: String,     // Doğru cevap
    val hints: List<String>,     // İpuçları
    val explanation: String,     // Açıklama
    val options: List<CodeOption>, // Seçenekler (sürükle-bırak veya seç)
    val expectedOutput: String,  // Beklenen çıktı
    val tutorial: TutorialContent? = null // Öğretici içerik
)

// Öğretici içerik - her seviyede önce kavram öğretilir
data class TutorialContent(
    val title: String,
    val sections: List<TutorialSection>
)

data class TutorialSection(
    val subtitle: String,
    val content: String,
    val codeExample: String? = null,
    val codeOutput: String? = null
)

data class CodeOption(
    val id: String,
    val code: String,
    val isCorrect: Boolean,
    val explanation: String = ""
)

data class PlayerProgress(
    val currentChapter: Int = 0,
    val currentLevel: Int = 1,
    val totalStars: Int = 0,
    val completedLevels: Set<String> = emptySet(), // "chapter_level" formatında
    val unlockedChapters: Set<Int> = setOf(0)
)

// ==================== ANA EKRAN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeMasterScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // SharedPreferences ile ilerleme kaydetme
    val prefs = remember { context.getSharedPreferences("code_master_prefs", android.content.Context.MODE_PRIVATE) }
    
    var progress by remember {
        mutableStateOf(
            PlayerProgress(
                currentChapter = prefs.getInt("current_chapter", 0),
                currentLevel = prefs.getInt("current_level", 1),
                totalStars = prefs.getInt("total_stars", 0),
                completedLevels = prefs.getStringSet("completed_levels", emptySet())?.toSet() ?: emptySet(),
                unlockedChapters = prefs.getStringSet("unlocked_chapters", setOf("0"))?.map { it.toInt() }?.toSet() ?: setOf(0)
            )
        )
    }
    
    var selectedChapter by remember { mutableStateOf<CodeChapter?>(null) }
    var selectedLevel by remember { mutableStateOf<CodeLevel?>(null) }
    var showLevelSelect by remember { mutableStateOf(false) }
    
    // İlerlemeyi kaydet
    fun saveProgress(newProgress: PlayerProgress) {
        progress = newProgress
        prefs.edit()
            .putInt("current_chapter", newProgress.currentChapter)
            .putInt("current_level", newProgress.currentLevel)
            .putInt("total_stars", newProgress.totalStars)
            .putStringSet("completed_levels", newProgress.completedLevels)
            .putStringSet("unlocked_chapters", newProgress.unlockedChapters.map { it.toString() }.toSet())
            .apply()
    }
    
    // Seviye tamamlandığında
    fun onLevelComplete(stars: Int, goToNextLevel: Boolean) {
        val chapterIndex = selectedChapter?.ordinal ?: 0
        val currentLevelNum = selectedLevel?.levelNum ?: 1
        val levelKey = "${chapterIndex}_$currentLevelNum"
        
        val newCompletedLevels = progress.completedLevels + levelKey
        val newTotalStars = progress.totalStars + stars
        
        // Sonraki bölümü aç (10 seviye tamamlanınca)
        val chapterCompletedCount = newCompletedLevels.count { it.startsWith("${chapterIndex}_") }
        val newUnlockedChapters = if (chapterCompletedCount >= 10 && chapterIndex < CodeChapter.entries.size - 1) {
            progress.unlockedChapters + (chapterIndex + 1)
        } else {
            progress.unlockedChapters
        }
        
        // Yeni ilerlemeyi kaydet
        val newProgress = progress.copy(
            currentChapter = chapterIndex,
            currentLevel = currentLevelNum + 1,
            totalStars = newTotalStars,
            completedLevels = newCompletedLevels,
            unlockedChapters = newUnlockedChapters
        )
        saveProgress(newProgress)
        
        if (goToNextLevel) {
            // Sonraki seviyeye geç
            val nextLevelNum = currentLevelNum + 1
            if (nextLevelNum <= 10) {
                selectedLevel = getLevelForChapter(selectedChapter!!, nextLevelNum)
            } else {
                // Bölüm bitti - seviye seçimine dön
                selectedLevel = null
                showLevelSelect = true
            }
        } else {
            // Seviye seçimine dön
            selectedLevel = null
            showLevelSelect = true
        }
    }
    
    Scaffold(
        containerColor = CodeBg
    ) { padding ->
        when {
            selectedLevel != null -> {
                // Oyun ekranı
                CodeGameScreen(
                    level = selectedLevel!!,
                    progress = progress,
                    onBack = { selectedLevel = null; showLevelSelect = true },
                    onComplete = { stars -> onLevelComplete(stars, true) },
                    onBackToLevels = { stars -> onLevelComplete(stars, false) }
                )
            }
            showLevelSelect && selectedChapter != null -> {
                // Seviye seçim ekranı
                LevelSelectScreen(
                    chapter = selectedChapter!!,
                    progress = progress,
                    onBack = { showLevelSelect = false; selectedChapter = null },
                    onSelectLevel = { level ->
                        selectedLevel = level
                        showLevelSelect = false
                    }
                )
            }
            else -> {
                // Ana menü - Bölüm seçimi
                ChapterSelectScreen(
                    progress = progress,
                    onBack = { navController.popBackStack() },
                    onSelectChapter = { chapter ->
                        selectedChapter = chapter
                        showLevelSelect = true
                    }
                )
            }
        }
    }
}

// ==================== BÖLÜM SEÇİM EKRANI ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterSelectScreen(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onSelectChapter: (CodeChapter) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "bgOffset"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CodeBg, Color(0xFF0F0F1A), CodeBg)
                )
            )
    ) {
        // Header
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kod Ustası", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        color = CodeAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 16.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("${progress.totalStars}", color = CodeWarning, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        
        // Hoşgeldin mesajı
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                "Kodlama Serüvenine Hoş Geldin! 🚀",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Her bölümde yeni bir programlama kavramı öğreneceksin. Hazır mısın?",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        
        // Bölümler
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(CodeChapter.entries.toList()) { index, chapter ->
                val isUnlocked = index in progress.unlockedChapters
                val completedCount = progress.completedLevels.count { it.startsWith("${index}_") }
                
                ChapterCard(
                    chapter = chapter,
                    isUnlocked = isUnlocked,
                    completedCount = completedCount,
                    onClick = { if (isUnlocked) onSelectChapter(chapter) }
                )
            }
            
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: CodeChapter,
    isUnlocked: Boolean,
    completedCount: Int,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isUnlocked) 1f else 0.95f,
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(enabled = isUnlocked) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) CodeSurface else CodeSurface.copy(alpha = 0.5f)
        ),
        border = if (isUnlocked) BorderStroke(2.dp, chapter.color.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji/İkon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isUnlocked) chapter.color.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(chapter.emoji, fontSize = 28.sp)
                } else {
                    Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            // Bilgiler
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    chapter.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Color.White else Color.Gray
                )
                Text(
                    chapter.description,
                    fontSize = 13.sp,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.5f)
                )
                
                Spacer(Modifier.height(8.dp))
                
                // İlerleme çubuğu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { completedCount / chapter.levelCount.toFloat() },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = chapter.color,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$completedCount/${chapter.levelCount}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            
            // Ok
            if (isUnlocked) {
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = chapter.color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ==================== SEVİYE SEÇİM EKRANI ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LevelSelectScreen(
    chapter: CodeChapter,
    progress: PlayerProgress,
    onBack: () -> Unit,
    onSelectLevel: (CodeLevel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeBg)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chapter.emoji, fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(chapter.title, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = chapter.color.copy(alpha = 0.2f))
        )
        
        // Bölüm açıklaması
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CodeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Bu Bölümde Öğreneceklerin:",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    getChapterDescription(chapter),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
        
        // Seviyeler grid
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chapter.levelCount) { index ->
                val levelNum = index + 1
                val chapterIndex = chapter.ordinal
                val levelKey = "${chapterIndex}_$levelNum"
                val isCompleted = levelKey in progress.completedLevels
                val isUnlocked = levelNum == 1 || "${chapterIndex}_${levelNum - 1}" in progress.completedLevels
                
                val level = getLevelForChapter(chapter, levelNum)
                
                LevelCard(
                    level = level,
                    isCompleted = isCompleted,
                    isUnlocked = isUnlocked,
                    chapterColor = chapter.color,
                    onClick = { if (isUnlocked) onSelectLevel(level) }
                )
            }
            
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun LevelCard(
    level: CodeLevel,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    chapterColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> chapterColor.copy(alpha = 0.15f)
                isUnlocked -> CodeSurface
                else -> CodeSurface.copy(alpha = 0.5f)
            }
        ),
        border = if (isCompleted) BorderStroke(1.dp, chapterColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seviye numarası
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> chapterColor
                            isUnlocked -> chapterColor.copy(alpha = 0.3f)
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
                } else if (isUnlocked) {
                    Text(
                        "${level.levelNum}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                } else {
                    Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    level.title,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Color.White else Color.Gray
                )
                Text(
                    level.lesson,
                    fontSize = 13.sp,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f)
                )
            }
            
            if (isCompleted) {
                Row {
                    repeat(3) {
                        Text("⭐", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


// ==================== OYUN EKRANI ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodeGameScreen(
    level: CodeLevel,
    progress: PlayerProgress,
    onBack: () -> Unit,
    onComplete: (Int) -> Unit,
    onBackToLevels: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    
    var selectedOptions by remember(level) { mutableStateOf<List<CodeOption>>(emptyList()) }
    var showHint by remember { mutableStateOf(false) }
    var hintIndex by remember { mutableIntStateOf(0) }
    var gameState by remember(level) { mutableStateOf("TUTORIAL") } // TUTORIAL, PLAYING, SUCCESS, FAILED
    var showExplanation by remember { mutableStateOf(false) }
    var codeOutput by remember(level) { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    var tutorialPage by remember(level) { mutableIntStateOf(0) }
    
    // Tutorial içeriği varsa göster, yoksa direkt oyuna geç
    val hasTutorial = level.tutorial != null
    
    LaunchedEffect(level) {
        if (!hasTutorial) {
            gameState = "PLAYING"
        }
    }
    
    // Kodu çalıştır
    fun runCode() {
        scope.launch {
            isRunning = true
            codeOutput = "Kod çalıştırılıyor..."
            delay(1000)
            
            // Seçilen cevapları kontrol et
            val correctCount = selectedOptions.count { it.isCorrect }
            val totalRequired = level.options.count { it.isCorrect }
            
            if (correctCount == totalRequired && selectedOptions.size == totalRequired) {
                codeOutput = level.expectedOutput
                delay(500)
                gameState = "SUCCESS"
            } else {
                codeOutput = "❌ Hata: Beklenmeyen çıktı"
                delay(500)
                gameState = "FAILED"
            }
            isRunning = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeBg)
    ) {
        // Header
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Seviye ${level.levelNum}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        level.title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            actions = {
                // Ders/Pratik geçiş butonu
                if (hasTutorial) {
                    TextButton(onClick = { 
                        gameState = if (gameState == "TUTORIAL") "PLAYING" else "TUTORIAL"
                        tutorialPage = 0
                    }) {
                        Text(
                            if (gameState == "TUTORIAL") "Pratiğe Geç →" else "← Derse Dön",
                            color = CodeWarning,
                            fontSize = 12.sp
                        )
                    }
                }
                // İpucu butonu
                if (gameState == "PLAYING") {
                    IconButton(onClick = { showHint = true }) {
                        Icon(Icons.Default.Lightbulb, null, tint = CodeWarning)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = level.chapter.color.copy(alpha = 0.3f))
        )
        
        // TUTORIAL EKRANI
        if (gameState == "TUTORIAL" && level.tutorial != null) {
            TutorialScreen(
                tutorial = level.tutorial!!,
                chapterColor = level.chapter.color,
                currentPage = tutorialPage,
                onPageChange = { tutorialPage = it },
                onStartPractice = { gameState = "PLAYING" }
            )
        } else if (gameState == "PLAYING" || gameState == "SUCCESS" || gameState == "FAILED") {
            // OYUN EKRANI
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hikaye/Senaryo
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CodeSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📖", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("Görev", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            level.story,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            
            // Öğrenilecek kavram
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = level.chapter.color.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, level.chapter.color.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            level.lesson,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Kod editörü
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF3D3D5C))
                ) {
                    Column {
                        // Editör başlığı
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2D2D4A))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                            Spacer(Modifier.width(6.dp))
                            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                            Spacer(Modifier.width(6.dp))
                            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                            Spacer(Modifier.width(12.dp))
                            Text("main.py", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        
                        // Kod içeriği
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Şablon kodu göster
                            CodeText(level.codeTemplate, selectedOptions)
                        }
                    }
                }
            }
            
            // Seçenekler
            item {
                Text(
                    "Boşluğu dolduracak kodu seç:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    level.options.forEach { option ->
                        val isSelected = option in selectedOptions
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOptions = if (isSelected) {
                                        selectedOptions - option
                                    } else {
                                        selectedOptions + option
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSelected -> level.chapter.color.copy(alpha = 0.3f)
                                    else -> CodeSurface
                                }
                            ),
                            border = if (isSelected) BorderStroke(2.dp, level.chapter.color) else null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) level.chapter.color
                                            else Color.White.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    option.code,
                                    fontFamily = FontFamily.Monospace,
                                    color = CodeKeyword,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // Çıktı konsolu
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D1A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📟", fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Konsol Çıktısı", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            codeOutput.ifEmpty { "// Kodu çalıştır ve çıktıyı gör" },
                            fontFamily = FontFamily.Monospace,
                            color = if (codeOutput.startsWith("❌")) CodeError else CodeSuccess,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            
            // Çalıştır butonu
            item {
                Button(
                    onClick = { runCode() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedOptions.isNotEmpty() && !isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CodeSuccess,
                        disabledContainerColor = CodeSuccess.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("KODU ÇALIŞTIR", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            
            item { Spacer(Modifier.height(80.dp)) }
        }
        } // PLAYING/SUCCESS/FAILED state kapanışı
    }
    
    // İpucu dialog
    if (showHint) {
        AlertDialog(
            onDismissRequest = { showHint = false },
            containerColor = CodeSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("İpucu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    level.hints.getOrElse(hintIndex) { level.hints.lastOrNull() ?: "İpucu yok" },
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                TextButton(onClick = { showHint = false }) {
                    Text("Anladım", color = CodeAccent)
                }
            }
        )
    }
    
    // Başarı dialog
    if (gameState == "SUCCESS") {
        AlertDialog(
            onDismissRequest = {},
            containerColor = CodeSurface,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Harika!", color = CodeSuccess, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Seviyeyi başarıyla tamamladın!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Row {
                        repeat(3) {
                            Text("⭐", fontSize = 32.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    // Açıklama
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📚 Öğrendiğin:", color = CodeWarning, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(level.explanation, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Seviyelere Dön butonu
                    OutlinedButton(
                        onClick = { 
                            gameState = "PLAYING"
                            onBackToLevels(3) 
                        },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Seviyelere Dön", color = Color.White)
                    }
                    // Devam Et butonu
                    Button(
                        onClick = { 
                            gameState = "PLAYING"
                            onComplete(3) 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CodeSuccess)
                    ) {
                        Text("Devam Et", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
    
    // Başarısız dialog
    if (gameState == "FAILED") {
        AlertDialog(
            onDismissRequest = {},
            containerColor = CodeSurface,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🤔", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Tekrar Dene", color = CodeWarning, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            },
            text = {
                Text(
                    "Kod doğru çalışmadı. İpuçlarına bakarak tekrar dene!",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        gameState = "PLAYING"
                        selectedOptions = emptyList()
                        codeOutput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CodeAccent)
                ) {
                    Text("Tekrar Dene", color = Color.White)
                }
            }
        )
    }
}

// ==================== KOD GÖRÜNTÜLEME ====================

// ==================== TUTORIAL EKRANI ====================

@Composable
private fun TutorialScreen(
    tutorial: TutorialContent,
    chapterColor: Color,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onStartPractice: () -> Unit
) {
    val totalPages = tutorial.sections.size
    val currentSection = tutorial.sections.getOrNull(currentPage) ?: tutorial.sections.first()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tutorial başlığı
        Card(
            colors = CardDefaults.cardColors(containerColor = chapterColor.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, chapterColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📚", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        tutorial.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Sayfa ${currentPage + 1} / $totalPages",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // İçerik
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alt başlık
            item {
                Text(
                    currentSection.subtitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = chapterColor
                )
            }
            
            // Açıklama metni
            item {
                Text(
                    currentSection.content,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 24.sp
                )
            }
            
            // Kod örneği varsa göster
            if (currentSection.codeExample != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF3D3D5C))
                    ) {
                        Column {
                            // Editör başlığı
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2D2D4A))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                                Spacer(Modifier.width(4.dp))
                                Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                                Spacer(Modifier.width(4.dp))
                                Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                                Spacer(Modifier.width(8.dp))
                                Text("📝 Örnek Kod", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                            
                            // Kod içeriği
                            Column(modifier = Modifier.padding(12.dp)) {
                                currentSection.codeExample!!.split("\n").forEachIndexed { index, line ->
                                    Row {
                                        Text(
                                            "${index + 1}".padStart(2, ' '),
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White.copy(alpha = 0.3f),
                                            fontSize = 13.sp,
                                            modifier = Modifier.width(24.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            line,
                                            fontFamily = FontFamily.Monospace,
                                            color = getCodeColor(line),
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            }
            
            // Çıktı varsa göster
            if (currentSection.codeOutput != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D1A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📟", fontSize = 14.sp)
                                Spacer(Modifier.width(6.dp))
                                Text("Çıktı:", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                currentSection.codeOutput!!,
                                fontFamily = FontFamily.Monospace,
                                color = CodeSuccess,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Navigasyon butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Önceki buton
            if (currentPage > 0) {
                OutlinedButton(
                    onClick = { onPageChange(currentPage - 1) },
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Önceki", color = Color.White)
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }
            
            // Sonraki veya Pratiğe Geç butonu
            if (currentPage < totalPages - 1) {
                Button(
                    onClick = { onPageChange(currentPage + 1) },
                    colors = ButtonDefaults.buttonColors(containerColor = chapterColor)
                ) {
                    Text("Sonraki", color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            } else {
                Button(
                    onClick = onStartPractice,
                    colors = ButtonDefaults.buttonColors(containerColor = CodeSuccess)
                ) {
                    Text("Pratiğe Geç", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ==================== KOD GÖRÜNTÜLEME (DEVAM) ====================

@Composable
private fun CodeText(template: String, selectedOptions: List<CodeOption>) {
    val lines = template.split("\n")
    var optionIndex = 0
    
    Column {
        lines.forEachIndexed { lineIndex, line ->
            Row {
                // Satır numarası
                Text(
                    "${lineIndex + 1}".padStart(2, ' '),
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 13.sp,
                    modifier = Modifier.width(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                
                // Kod satırı
                if (line.contains("___")) {
                    // Boşluk var - seçilen cevabı göster
                    val parts = line.split("___")
                    Row {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = getCodeColor(parts[0]))) {
                                    append(parts[0])
                                }
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                        
                        // Seçilen cevap veya boşluk
                        val selectedOption = selectedOptions.getOrNull(optionIndex)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selectedOption != null) CodeAccent.copy(alpha = 0.3f)
                                    else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                selectedOption?.code ?: "???",
                                fontFamily = FontFamily.Monospace,
                                color = if (selectedOption != null) CodeKeyword else Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                        }
                        optionIndex++
                        
                        if (parts.size > 1) {
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(color = getCodeColor(parts[1]))) {
                                        append(parts[1])
                                    }
                                },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // Normal satır
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = getCodeColor(line))) {
                                append(line)
                            }
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

private fun getCodeColor(code: String): Color {
    return when {
        code.trim().startsWith("#") -> CodeComment
        code.contains("def ") || code.contains("if ") || code.contains("else") ||
        code.contains("for ") || code.contains("while ") || code.contains("return") ||
        code.contains("print") || code.contains("input") -> CodeKeyword
        code.contains("\"") || code.contains("'") -> CodeString
        code.any { it.isDigit() } && !code.any { it.isLetter() } -> CodeNumber
        else -> Color.White
    }
}


// ==================== SEVİYE VERİLERİ ====================

private fun getChapterDescription(chapter: CodeChapter): String {
    return when (chapter) {
        CodeChapter.VARIABLES -> """
• Değişken nedir ve neden kullanılır?
• Farklı veri tipleri: sayı, metin, boolean
• Değişkenlere değer atama
• Değişkenlerle işlem yapma
• Kullanıcıdan veri alma
        """.trimIndent()
        
        CodeChapter.CONDITIONS -> """
• if (eğer) koşulu nedir?
• else (değilse) kullanımı
• elif (değilse eğer) zincirleme
• Karşılaştırma operatörleri (==, !=, <, >)
• Mantıksal operatörler (and, or, not)
        """.trimIndent()
        
        CodeChapter.LOOPS -> """
• for döngüsü ile tekrarlama
• while döngüsü ile koşullu tekrar
• range() fonksiyonu
• break ve continue komutları
• İç içe döngüler
        """.trimIndent()
        
        CodeChapter.FUNCTIONS -> """
• Fonksiyon nedir ve neden kullanılır?
• def ile fonksiyon tanımlama
• Parametre ve argüman kavramı
• return ile değer döndürme
• Fonksiyonları çağırma
        """.trimIndent()
        
        CodeChapter.ALGORITHMS -> """
• Algoritma nedir?
• Sıralama algoritmaları
• Arama algoritmaları
• Problem çözme adımları
• Verimlilik kavramı
        """.trimIndent()
        
        CodeChapter.MASTER -> """
• Tüm kavramları birleştirme
• Gerçek dünya problemleri
• Mini projeler
• Kod optimizasyonu
• İleri seviye teknikler
        """.trimIndent()
    }
}

private fun getLevelForChapter(chapter: CodeChapter, levelNum: Int): CodeLevel {
    return when (chapter) {
        CodeChapter.VARIABLES -> getVariablesLevel(levelNum)
        CodeChapter.CONDITIONS -> getConditionsLevel(levelNum)
        CodeChapter.LOOPS -> getLoopsLevel(levelNum)
        CodeChapter.FUNCTIONS -> getFunctionsLevel(levelNum)
        CodeChapter.ALGORITHMS -> getAlgorithmsLevel(levelNum)
        CodeChapter.MASTER -> getMasterLevel(levelNum)
    }
}

// ==================== BÖLÜM 1: DEĞİŞKENLER ====================

private fun getVariablesLevel(levelNum: Int): CodeLevel {
    return when (levelNum) {
        1 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 1,
            title = "İlk Değişkenin",
            story = "Merhaba genç kodcu! 🎮 Bugün ilk programını yazacaksın. Bir değişken oluşturup ekrana yazdıracağız. Değişkenler, bilgisayarın hafızasında veri sakladığımız kutular gibidir.",
            lesson = "Değişken tanımlama: isim = değer",
            codeTemplate = """
# İlk programın!
mesaj = ___
print(mesaj)
            """.trimIndent(),
            correctCode = "mesaj = \"Merhaba Dünya\"",
            hints = listOf(
                "Metin (string) değerler tırnak içinde yazılır",
                "Örnek: \"Merhaba\" veya 'Merhaba'",
                "Değişkene bir metin ataman gerekiyor"
            ),
            explanation = "Değişkenler veri saklamak için kullanılır. Metin değerleri tırnak içinde yazılır. print() fonksiyonu ekrana yazdırır.",
            options = listOf(
                CodeOption("1", "\"Merhaba Dünya\"", true, "Doğru! Metin tırnak içinde yazılır"),
                CodeOption("2", "Merhaba Dünya", false, "Metin tırnak içinde olmalı"),
                CodeOption("3", "123", false, "Bu bir sayı, metin değil"),
                CodeOption("4", "True", false, "Bu bir boolean değer")
            ),
            expectedOutput = "Merhaba Dünya",
            tutorial = TutorialContent(
                title = "Değişkenler Nedir?",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🎯 Değişken Nedir?",
                        content = """Değişkenler, bilgisayarın hafızasında veri sakladığımız kutular gibidir. 

Bir değişken oluşturduğunda, bilgisayar hafızasında bir yer ayırır ve sen bu yere istediğin veriyi koyabilirsin.

Değişkenlere isim veririz ve bu isimle onlara ulaşırız. Örneğin "mesaj" adında bir değişken oluşturup içine "Merhaba" yazabiliriz."""
                    ),
                    TutorialSection(
                        subtitle = "📝 Değişken Nasıl Oluşturulur?",
                        content = """Python'da değişken oluşturmak çok kolay! Sadece bir isim seç ve = işareti ile değer ata:

değişken_adı = değer

Örneğin bir metin (string) saklamak için:""",
                        codeExample = """isim = "Ali"
sehir = "İstanbul"
mesaj = "Merhaba Dünya!" """,
                        codeOutput = null
                    ),
                    TutorialSection(
                        subtitle = "🖨️ print() Fonksiyonu",
                        content = """print() fonksiyonu ekrana bir şeyler yazdırmak için kullanılır.

Değişkenin içindeki değeri görmek için print() kullanırız:""",
                        codeExample = """mesaj = "Merhaba!"
print(mesaj)""",
                        codeOutput = "Merhaba!"
                    ),
                    TutorialSection(
                        subtitle = "⚠️ Önemli Kurallar",
                        content = """1. Metin (string) değerler tırnak içinde yazılır: "metin" veya 'metin'

2. Değişken isimleri boşluk içeremez: mesaj_metni ✓ mesaj metni ✗

3. Değişken isimleri sayı ile başlayamaz: isim1 ✓ 1isim ✗

4. Python büyük/küçük harfe duyarlıdır: Mesaj ve mesaj farklı değişkenlerdir!

Şimdi öğrendiklerini pratiğe dök! 🚀"""
                    )
                )
            )
        )
        
        2 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 2,
            title = "Sayılarla Tanış",
            story = "Harika! Şimdi sayılarla çalışacağız. 🔢 Yaşını bir değişkende saklayıp ekrana yazdıracaksın.",
            lesson = "Sayı değişkenleri tırnak kullanmaz",
            codeTemplate = """
# Yaşını kaydet
yas = ___
print("Yaşım:", yas)
            """.trimIndent(),
            correctCode = "yas = 12",
            hints = listOf(
                "Sayılar tırnak içinde yazılmaz",
                "Sadece rakamları yaz: 12",
                "Yaşın kaç? Onu yaz!"
            ),
            explanation = "Sayı (integer) değerleri tırnak kullanmadan yazılır. Python otomatik olarak veri tipini anlar.",
            options = listOf(
                CodeOption("1", "12", true, "Doğru! Sayılar tırnaksız yazılır"),
                CodeOption("2", "\"12\"", false, "Bu metin olur, sayı değil"),
                CodeOption("3", "on iki", false, "Sayıyı rakamla yaz"),
                CodeOption("4", "12.0", false, "Bu ondalıklı sayı olur")
            ),
            expectedOutput = "Yaşım: 12",
            tutorial = TutorialContent(
                title = "Sayı Değişkenleri",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🔢 Sayı Tipleri",
                        content = """Python'da iki temel sayı tipi vardır:

1. Integer (int): Tam sayılar → 5, 42, -10, 0
2. Float: Ondalıklı sayılar → 3.14, -2.5, 0.0

Sayılar tırnak içinde YAZILMAZ! Tırnak içinde yazarsan metin olur."""
                    ),
                    TutorialSection(
                        subtitle = "📊 Sayı vs Metin",
                        content = """Aradaki farkı görelim:""",
                        codeExample = """# Bu bir SAYI
yas = 12
print(yas + 5)  # Matematik yapabilirsin

# Bu bir METİN
yas_metin = "12"
print(yas_metin + "5")  # Birleştirme yapar""",
                        codeOutput = """17
125"""
                    ),
                    TutorialSection(
                        subtitle = "✨ Pratik Zamanı",
                        content = """Şimdi bir sayı değişkeni oluşturacaksın!

Unutma: Sayılar tırnak kullanmadan yazılır.

Örnek: yas = 12 ✓
Yanlış: yas = "12" ✗ (bu metin olur)"""
                    )
                )
            )
        )
        
        3 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 3,
            title = "Matematik Zamanı",
            story = "Değişkenlerle matematik yapabiliriz! ➕ İki sayıyı toplayıp sonucu gösterelim.",
            lesson = "Değişkenlerle aritmetik işlemler",
            codeTemplate = """
# Toplama işlemi
sayi1 = 5
sayi2 = 3
toplam = ___
print("Toplam:", toplam)
            """.trimIndent(),
            correctCode = "toplam = sayi1 + sayi2",
            hints = listOf(
                "İki değişkeni toplamak için + kullan",
                "sayi1 ve sayi2'yi topla",
                "Değişken isimlerini kullan, sayıları değil"
            ),
            explanation = "Değişkenler matematiksel işlemlerde kullanılabilir. +, -, *, / operatörleri ile işlem yapılır.",
            options = listOf(
                CodeOption("1", "sayi1 + sayi2", true, "Doğru! Değişkenleri topladın"),
                CodeOption("2", "5 + 3", false, "Çalışır ama değişkenleri kullanmalısın"),
                CodeOption("3", "\"sayi1 + sayi2\"", false, "Bu metin olur, işlem yapmaz"),
                CodeOption("4", "sayi1 - sayi2", false, "Bu çıkarma işlemi")
            ),
            expectedOutput = "Toplam: 8",
            tutorial = TutorialContent(
                title = "Matematiksel İşlemler",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🧮 Aritmetik Operatörler",
                        content = """Python'da temel matematik operatörleri:

+ Toplama
- Çıkarma  
* Çarpma
/ Bölme
** Üs alma
% Mod (kalan)
// Tam bölme"""
                    ),
                    TutorialSection(
                        subtitle = "📐 Örnekler",
                        content = """Değişkenlerle matematik yapalım:""",
                        codeExample = """a = 10
b = 3

print(a + b)   # Toplama
print(a - b)   # Çıkarma
print(a * b)   # Çarpma
print(a / b)   # Bölme
print(a ** 2)  # Üs (10²)
print(a % b)   # Kalan
print(a // b)  # Tam bölme""",
                        codeOutput = """13
7
30
3.333...
100
1
3"""
                    ),
                    TutorialSection(
                        subtitle = "💡 İpucu",
                        content = """Değişkenleri kullanarak işlem yapmak daha iyidir!

Neden? Çünkü değişkenin değerini değiştirdiğinde, tüm hesaplamalar otomatik güncellenir.

Şimdi iki sayıyı toplayarak pratik yap! 🎯"""
                    )
                )
            )
        )
        
        4 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 4,
            title = "Kullanıcıdan Veri Al",
            story = "Programlar kullanıcıyla etkileşim kurar! 💬 input() fonksiyonu ile kullanıcıdan isim alalım.",
            lesson = "input() fonksiyonu ile veri alma",
            codeTemplate = """
# Kullanıcıdan isim al
isim = ___
print("Merhaba", isim)
            """.trimIndent(),
            correctCode = "isim = input(\"Adın ne? \")",
            hints = listOf(
                "input() fonksiyonu kullanıcıdan veri alır",
                "Parantez içine soru yazabilirsin",
                "input(\"mesaj\") formatını kullan"
            ),
            explanation = "input() fonksiyonu programı durdurur ve kullanıcının yazmasını bekler. Girilen değer metin olarak döner.",
            options = listOf(
                CodeOption("1", "input(\"Adın ne? \")", true, "Doğru! Kullanıcıdan veri aldın"),
                CodeOption("2", "\"Adın ne?\"", false, "Bu sadece metin, veri almaz"),
                CodeOption("3", "print(\"Adın ne?\")", false, "print yazdırır, veri almaz"),
                CodeOption("4", "input()", false, "Çalışır ama soru sormaz")
            ),
            expectedOutput = "Merhaba [kullanıcı adı]"
        )
        
        5 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 5,
            title = "Veri Tipleri",
            story = "Python'da farklı veri tipleri var: metin (str), sayı (int), ondalık (float), mantıksal (bool). 📊",
            lesson = "type() ile veri tipini öğrenme",
            codeTemplate = """
# Veri tipini öğren
sayi = 42
tip = ___
print("Veri tipi:", tip)
            """.trimIndent(),
            correctCode = "tip = type(sayi)",
            hints = listOf(
                "type() fonksiyonu veri tipini döndürür",
                "Değişkeni type() içine yaz",
                "type(değişken) formatını kullan"
            ),
            explanation = "type() fonksiyonu bir değişkenin veri tipini gösterir. int=tam sayı, str=metin, float=ondalık, bool=mantıksal",
            options = listOf(
                CodeOption("1", "type(sayi)", true, "Doğru! Veri tipini öğrendin"),
                CodeOption("2", "\"int\"", false, "Bu sadece metin"),
                CodeOption("3", "sayi.type", false, "Yanlış sözdizimi"),
                CodeOption("4", "typeof(sayi)", false, "Python'da typeof yok")
            ),
            expectedOutput = "Veri tipi: <class 'int'>"
        )
        
        6 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 6,
            title = "Metin Birleştirme",
            story = "Metinleri birleştirebiliriz! 🔗 İsim ve soyismi birleştirip tam ad oluşturalım.",
            lesson = "String concatenation (+) ile metin birleştirme",
            codeTemplate = """
# Metinleri birleştir
isim = "Ali"
soyisim = "Yılmaz"
tam_ad = ___
print(tam_ad)
            """.trimIndent(),
            correctCode = "tam_ad = isim + \" \" + soyisim",
            hints = listOf(
                "Metinler + ile birleştirilir",
                "Arada boşluk için \" \" ekle",
                "isim + boşluk + soyisim"
            ),
            explanation = "Metinler + operatörü ile birleştirilir. Arada boşluk istiyorsan \" \" eklemelisin.",
            options = listOf(
                CodeOption("1", "isim + \" \" + soyisim", true, "Doğru! Metinleri birleştirdin"),
                CodeOption("2", "isim + soyisim", false, "Çalışır ama boşluk olmaz"),
                CodeOption("3", "isim, soyisim", false, "Bu birleştirme değil"),
                CodeOption("4", "isim - soyisim", false, "Metinlerde çıkarma olmaz")
            ),
            expectedOutput = "Ali Yılmaz"
        )
        
        7 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 7,
            title = "Tip Dönüşümü",
            story = "Bazen veri tipini değiştirmemiz gerekir. 🔄 Kullanıcıdan alınan sayıyı gerçek sayıya çevirelim.",
            lesson = "int(), str(), float() ile tip dönüşümü",
            codeTemplate = """
# Metni sayıya çevir
metin_sayi = "25"
gercek_sayi = ___
sonuc = gercek_sayi + 5
print("Sonuç:", sonuc)
            """.trimIndent(),
            correctCode = "gercek_sayi = int(metin_sayi)",
            hints = listOf(
                "int() metni tam sayıya çevirir",
                "Değişkeni int() içine yaz",
                "\"25\" metindir, 25 sayıdır"
            ),
            explanation = "int() metni sayıya, str() sayıyı metne, float() ondalık sayıya çevirir. input() her zaman metin döndürür!",
            options = listOf(
                CodeOption("1", "int(metin_sayi)", true, "Doğru! Metni sayıya çevirdin"),
                CodeOption("2", "str(metin_sayi)", false, "Bu zaten metin"),
                CodeOption("3", "float(metin_sayi)", false, "Çalışır ama ondalık olur"),
                CodeOption("4", "metin_sayi", false, "Dönüşüm yapmadın")
            ),
            expectedOutput = "Sonuç: 30"
        )
        
        8 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 8,
            title = "Boolean Değerler",
            story = "Boolean değerler sadece True veya False olabilir. ✅❌ Mantıksal karşılaştırmalar yapalım.",
            lesson = "True/False ve karşılaştırma operatörleri",
            codeTemplate = """
# Karşılaştırma yap
sayi = 10
buyuk_mu = ___
print("10 > 5 mi?", buyuk_mu)
            """.trimIndent(),
            correctCode = "buyuk_mu = sayi > 5",
            hints = listOf(
                "> operatörü 'büyüktür' anlamına gelir",
                "Karşılaştırma True veya False döndürür",
                "sayi > 5 ifadesini kullan"
            ),
            explanation = "Karşılaştırma operatörleri: > (büyük), < (küçük), == (eşit), != (eşit değil), >= (büyük eşit), <= (küçük eşit)",
            options = listOf(
                CodeOption("1", "sayi > 5", true, "Doğru! Karşılaştırma yaptın"),
                CodeOption("2", "True", false, "Doğru ama karşılaştırma yapmalısın"),
                CodeOption("3", "sayi = 5", false, "Bu atama, karşılaştırma değil"),
                CodeOption("4", "\"True\"", false, "Bu metin, boolean değil")
            ),
            expectedOutput = "10 > 5 mi? True"
        )
        
        9 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 9,
            title = "Çoklu Atama",
            story = "Python'da birden fazla değişkene aynı anda değer atayabilirsin! 🎯",
            lesson = "Tek satırda çoklu değişken atama",
            codeTemplate = """
# Çoklu atama
___ = 10, 20, 30
print("a:", a, "b:", b, "c:", c)
            """.trimIndent(),
            correctCode = "a, b, c = 10, 20, 30",
            hints = listOf(
                "Değişkenleri virgülle ayır",
                "a, b, c formatını kullan",
                "Sol tarafta değişkenler, sağda değerler"
            ),
            explanation = "Python'da a, b, c = 1, 2, 3 şeklinde tek satırda birden fazla değişkene değer atanabilir.",
            options = listOf(
                CodeOption("1", "a, b, c", true, "Doğru! Çoklu atama yaptın"),
                CodeOption("2", "a b c", false, "Virgül kullanmalısın"),
                CodeOption("3", "[a, b, c]", false, "Liste değil, değişkenler"),
                CodeOption("4", "a = b = c", false, "Bu farklı bir kullanım")
            ),
            expectedOutput = "a: 10 b: 20 c: 30"
        )
        
        10 -> CodeLevel(
            chapter = CodeChapter.VARIABLES,
            levelNum = 10,
            title = "Değişken Ustası",
            story = "Tebrikler! 🏆 Son test: Kullanıcıdan iki sayı al, topla ve sonucu göster.",
            lesson = "Tüm değişken bilgilerini birleştir",
            codeTemplate = """
# Hesap makinesi
sayi1 = int(input("1. sayı: "))
sayi2 = int(input("2. sayı: "))
toplam = ___
print("Toplam:", toplam)
            """.trimIndent(),
            correctCode = "toplam = sayi1 + sayi2",
            hints = listOf(
                "İki değişkeni topla",
                "sayi1 ve sayi2 zaten int tipinde",
                "+ operatörünü kullan"
            ),
            explanation = "Harika! Değişkenler bölümünü tamamladın. Artık veri saklama, tip dönüşümü ve işlem yapmayı biliyorsun!",
            options = listOf(
                CodeOption("1", "sayi1 + sayi2", true, "Mükemmel! Bölümü tamamladın!"),
                CodeOption("2", "sayi1 - sayi2", false, "Bu çıkarma işlemi"),
                CodeOption("3", "sayi1 * sayi2", false, "Bu çarpma işlemi"),
                CodeOption("4", "sayi1 / sayi2", false, "Bu bölme işlemi")
            ),
            expectedOutput = "Toplam: [sayıların toplamı]"
        )
        
        else -> getVariablesLevel(1)
    }
}


// ==================== BÖLÜM 2: KOŞULLAR ====================

private fun getConditionsLevel(levelNum: Int): CodeLevel {
    return when (levelNum) {
        1 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 1,
            title = "İlk Koşulun",
            story = "Programlar karar verebilir! 🤔 if (eğer) komutu ile bir koşul kontrol edelim.",
            lesson = "if koşulu: Eğer doğruysa çalıştır",
            codeTemplate = """
# Yaş kontrolü
yas = 15
___ yas >= 13:
    print("Gençsin!")
            """.trimIndent(),
            correctCode = "if yas >= 13:",
            hints = listOf(
                "if kelimesi ile başla",
                "Koşuldan sonra : koy",
                "if koşul: formatını kullan"
            ),
            explanation = "if koşulu, belirtilen şart doğruysa (True) altındaki kodu çalıştırır. Koşuldan sonra : konur ve alt satır girintili yazılır.",
            options = listOf(
                CodeOption("1", "if", true, "Doğru! İlk koşulunu yazdın"),
                CodeOption("2", "If", false, "Python küçük harf kullanır"),
                CodeOption("3", "IF", false, "Python küçük harf kullanır"),
                CodeOption("4", "when", false, "Python'da when yok, if kullanılır")
            ),
            expectedOutput = "Gençsin!",
            tutorial = TutorialContent(
                title = "Koşullu İfadeler",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🤔 Koşul Nedir?",
                        content = """Programlar karar verebilir! 

Gerçek hayatta da sürekli kararlar alırız:
- Hava yağmurluysa şemsiye al
- Sınav puanı 50'den büyükse geçtin

Python'da bu kararları "if" (eğer) komutu ile yazarız."""
                    ),
                    TutorialSection(
                        subtitle = "📝 if Yapısı",
                        content = """if komutu şöyle yazılır:

if koşul:
    yapılacak işlem

Önemli kurallar:
1. Koşuldan sonra : (iki nokta) koy
2. Alt satırı 4 boşluk içeri al (girinti)
3. Koşul True ise içindeki kod çalışır""",
                        codeExample = """yas = 18

if yas >= 18:
    print("Reşitsin!")
    print("Ehliyet alabilirsin")""",
                        codeOutput = """Reşitsin!
Ehliyet alabilirsin"""
                    ),
                    TutorialSection(
                        subtitle = "⚖️ Karşılaştırma Operatörleri",
                        content = """Koşullarda kullanılan operatörler:

==  Eşit mi?
!=  Eşit değil mi?
>   Büyük mü?
<   Küçük mü?
>=  Büyük veya eşit mi?
<=  Küçük veya eşit mi?

Dikkat: = atama, == karşılaştırma!"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi ilk koşulunu yazacaksın!

Yaş 13'ten büyük veya eşitse "Gençsin!" yazdıracağız.

Hangi kelimeyle başlamalısın? 🤔"""
                    )
                )
            )
        )
        
        2 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 2,
            title = "else Kullanımı",
            story = "Koşul yanlışsa ne olacak? 🔄 else (değilse) ile alternatif belirleyelim.",
            lesson = "else: Koşul yanlışsa çalışır",
            codeTemplate = """
# Geçti mi?
puan = 45
if puan >= 50:
    print("Geçtin!")
___:
    print("Kaldın!")
            """.trimIndent(),
            correctCode = "else:",
            hints = listOf(
                "else kelimesini kullan",
                "else'den sonra : koy",
                "else koşul almaz, direkt çalışır"
            ),
            explanation = "else bloğu, if koşulu False olduğunda çalışır. else koşul almaz, sadece : ile biter.",
            options = listOf(
                CodeOption("1", "else", true, "Doğru! else kullandın"),
                CodeOption("2", "otherwise", false, "Python'da otherwise yok"),
                CodeOption("3", "if not", false, "Bu farklı bir kullanım"),
                CodeOption("4", "elif", false, "elif başka koşul için")
            ),
            expectedOutput = "Kaldın!"
        )
        
        3 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 3,
            title = "elif Zinciri",
            story = "Birden fazla koşul kontrol etmek istersen elif kullan! 📊 Not sistemi yapalım.",
            lesson = "elif: Birden fazla koşul kontrolü",
            codeTemplate = """
# Not sistemi
puan = 75
if puan >= 90:
    print("A")
___ puan >= 70:
    print("B")
else:
    print("C")
            """.trimIndent(),
            correctCode = "elif puan >= 70:",
            hints = listOf(
                "elif = else if demek",
                "elif koşul: formatını kullan",
                "Yeni bir koşul belirt"
            ),
            explanation = "elif (else if) birden fazla koşulu sırayla kontrol eder. İlk doğru olan çalışır, diğerleri atlanır.",
            options = listOf(
                CodeOption("1", "elif", true, "Doğru! elif kullandın"),
                CodeOption("2", "else if", false, "Python'da elif yazılır"),
                CodeOption("3", "elseif", false, "Doğru yazım: elif"),
                CodeOption("4", "if", false, "Bu yeni bir if bloğu başlatır")
            ),
            expectedOutput = "B"
        )
        
        4 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 4,
            title = "Eşitlik Kontrolü",
            story = "İki değerin eşit olup olmadığını kontrol edelim! ⚖️ == operatörünü kullanacağız.",
            lesson = "== eşitlik, = atama operatörü",
            codeTemplate = """
# Şifre kontrolü
sifre = "1234"
giris = "1234"
if sifre ___ giris:
    print("Giriş başarılı!")
            """.trimIndent(),
            correctCode = "if sifre == giris:",
            hints = listOf(
                "Eşitlik için == kullan",
                "= atama, == karşılaştırma",
                "İki değeri karşılaştır"
            ),
            explanation = "== iki değerin eşit olup olmadığını kontrol eder. = ise değer atar. Karıştırma!",
            options = listOf(
                CodeOption("1", "==", true, "Doğru! Eşitlik kontrolü yaptın"),
                CodeOption("2", "=", false, "Bu atama operatörü"),
                CodeOption("3", "===", false, "Python'da === yok"),
                CodeOption("4", "equals", false, "Python'da equals yok")
            ),
            expectedOutput = "Giriş başarılı!"
        )
        
        5 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 5,
            title = "and Operatörü",
            story = "İki koşulun da doğru olmasını istiyorsan and kullan! 🤝",
            lesson = "and: Her iki koşul da True olmalı",
            codeTemplate = """
# Yaş ve ehliyet kontrolü
yas = 20
ehliyet = True
if yas >= 18 ___ ehliyet:
    print("Araba kullanabilirsin!")
            """.trimIndent(),
            correctCode = "if yas >= 18 and ehliyet:",
            hints = listOf(
                "and operatörünü kullan",
                "İki koşulu birleştir",
                "Her ikisi de True olmalı"
            ),
            explanation = "and operatörü her iki koşul da True olduğunda True döndürür. Biri bile False ise sonuç False olur.",
            options = listOf(
                CodeOption("1", "and", true, "Doğru! and kullandın"),
                CodeOption("2", "&&", false, "Python'da && yok, and kullanılır"),
                CodeOption("3", "or", false, "or farklı, biri yeterli"),
                CodeOption("4", "+", false, "+ matematiksel toplama")
            ),
            expectedOutput = "Araba kullanabilirsin!"
        )
        
        6 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 6,
            title = "or Operatörü",
            story = "Koşullardan biri doğru olsa yeterli mi? or kullan! 🔀",
            lesson = "or: Koşullardan biri True olmalı",
            codeTemplate = """
# Hafta sonu mu?
gun = "Cumartesi"
if gun == "Cumartesi" ___ gun == "Pazar":
    print("Hafta sonu!")
            """.trimIndent(),
            correctCode = "if gun == \"Cumartesi\" or gun == \"Pazar\":",
            hints = listOf(
                "or operatörünü kullan",
                "Biri doğru olsa yeterli",
                "İki koşulu or ile bağla"
            ),
            explanation = "or operatörü koşullardan en az biri True olduğunda True döndürür. Her ikisi False ise sonuç False olur.",
            options = listOf(
                CodeOption("1", "or", true, "Doğru! or kullandın"),
                CodeOption("2", "||", false, "Python'da || yok, or kullanılır"),
                CodeOption("3", "and", false, "and her ikisini ister"),
                CodeOption("4", "xor", false, "xor farklı bir operatör")
            ),
            expectedOutput = "Hafta sonu!"
        )
        
        7 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 7,
            title = "not Operatörü",
            story = "Bir koşulun tersini almak istersen not kullan! 🔄",
            lesson = "not: True'yu False, False'u True yapar",
            codeTemplate = """
# Giriş engeli
yasakli = False
if ___ yasakli:
    print("Hoş geldin!")
            """.trimIndent(),
            correctCode = "if not yasakli:",
            hints = listOf(
                "not operatörünü kullan",
                "not False = True olur",
                "Koşulun tersini al"
            ),
            explanation = "not operatörü boolean değerin tersini alır. not True = False, not False = True",
            options = listOf(
                CodeOption("1", "not", true, "Doğru! not kullandın"),
                CodeOption("2", "!", false, "Python'da ! yok, not kullanılır"),
                CodeOption("3", "~", false, "~ bitwise operatör"),
                CodeOption("4", "reverse", false, "reverse diye bir şey yok")
            ),
            expectedOutput = "Hoş geldin!"
        )
        
        8 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 8,
            title = "İç İçe Koşullar",
            story = "Koşulların içine koşul yazabilirsin! 🎭 Nested if yapısı.",
            lesson = "İç içe if blokları",
            codeTemplate = """
# VIP kontrolü
uye = True
vip = True
if uye:
    ___ vip:
        print("VIP üyesin!")
            """.trimIndent(),
            correctCode = "if vip:",
            hints = listOf(
                "İç içe if kullan",
                "Girintiye dikkat et",
                "if vip: yaz"
            ),
            explanation = "if blokları iç içe yazılabilir. Her iç blok bir girinti daha içeride olmalı.",
            options = listOf(
                CodeOption("1", "if", true, "Doğru! İç içe if kullandın"),
                CodeOption("2", "elif", false, "elif aynı seviyede olmalı"),
                CodeOption("3", "else", false, "else koşul almaz"),
                CodeOption("4", "and", false, "and tek satırda kullanılır")
            ),
            expectedOutput = "VIP üyesin!"
        )
        
        9 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 9,
            title = "in Operatörü",
            story = "Bir değerin listede olup olmadığını kontrol et! 📋",
            lesson = "in: Üyelik kontrolü",
            codeTemplate = """
# Meyve kontrolü
meyveler = ["elma", "armut", "muz"]
aranan = "elma"
if aranan ___ meyveler:
    print("Meyve bulundu!")
            """.trimIndent(),
            correctCode = "if aranan in meyveler:",
            hints = listOf(
                "in operatörünü kullan",
                "Listede var mı kontrol et",
                "değer in liste formatı"
            ),
            explanation = "in operatörü bir değerin liste, string veya başka bir koleksiyonda olup olmadığını kontrol eder.",
            options = listOf(
                CodeOption("1", "in", true, "Doğru! in kullandın"),
                CodeOption("2", "contains", false, "Python'da contains yok"),
                CodeOption("3", "has", false, "Python'da has yok"),
                CodeOption("4", "==", false, "== eşitlik kontrolü")
            ),
            expectedOutput = "Meyve bulundu!"
        )
        
        10 -> CodeLevel(
            chapter = CodeChapter.CONDITIONS,
            levelNum = 10,
            title = "Koşul Ustası",
            story = "Final! 🏆 Bir sayının pozitif, negatif veya sıfır olduğunu belirle.",
            lesson = "Tüm koşul bilgilerini birleştir",
            codeTemplate = """
# Sayı analizi
sayi = -5
if sayi > 0:
    print("Pozitif")
___ sayi < 0:
    print("Negatif")
else:
    print("Sıfır")
            """.trimIndent(),
            correctCode = "elif sayi < 0:",
            hints = listOf(
                "elif kullan",
                "Negatif kontrolü yap",
                "sayi < 0 koşulunu ekle"
            ),
            explanation = "Tebrikler! Koşullar bölümünü tamamladın. if, elif, else ve mantıksal operatörleri öğrendin!",
            options = listOf(
                CodeOption("1", "elif", true, "Mükemmel! Bölümü tamamladın!"),
                CodeOption("2", "else if", false, "Python'da elif yazılır"),
                CodeOption("3", "if", false, "Bu yeni blok başlatır"),
                CodeOption("4", "else", false, "else koşul almaz")
            ),
            expectedOutput = "Negatif"
        )
        
        else -> getConditionsLevel(1)
    }
}

// ==================== BÖLÜM 3: DÖNGÜLER ====================

private fun getLoopsLevel(levelNum: Int): CodeLevel {
    return when (levelNum) {
        1 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 1,
            title = "İlk Döngün",
            story = "Aynı işi tekrar tekrar yapmak yerine döngü kullan! 🔄 for döngüsü ile başlayalım.",
            lesson = "for döngüsü: Belirli sayıda tekrar",
            codeTemplate = """
# 5 kez merhaba de
___ i in range(5):
    print("Merhaba!")
            """.trimIndent(),
            correctCode = "for i in range(5):",
            hints = listOf(
                "for kelimesi ile başla",
                "for değişken in range(sayı): formatı",
                "i döngü değişkeni"
            ),
            explanation = "for döngüsü belirli sayıda tekrar yapar. range(5) 0'dan 4'e kadar 5 sayı üretir.",
            options = listOf(
                CodeOption("1", "for", true, "Doğru! for döngüsü başlattın"),
                CodeOption("2", "while", false, "while farklı bir döngü"),
                CodeOption("3", "loop", false, "Python'da loop yok"),
                CodeOption("4", "repeat", false, "Python'da repeat yok")
            ),
            expectedOutput = "Merhaba!\nMerhaba!\nMerhaba!\nMerhaba!\nMerhaba!",
            tutorial = TutorialContent(
                title = "Döngüler",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🔄 Döngü Nedir?",
                        content = """Aynı işi tekrar tekrar yapmak yerine döngü kullanırız!

Örneğin "Merhaba" kelimesini 100 kez yazdırmak istersen, 100 satır kod yazmak yerine döngü kullanırsın.

Python'da iki temel döngü var:
- for: Belirli sayıda tekrar
- while: Koşul doğru olduğu sürece tekrar"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 for Döngüsü",
                        content = """for döngüsü belirli sayıda tekrar yapar:

for değişken in range(sayı):
    yapılacak işlem

range(5) → 0, 1, 2, 3, 4 (5 kez)""",
                        codeExample = """for i in range(3):
    print("Merhaba!")
    print("Sayı:", i)""",
                        codeOutput = """Merhaba!
Sayı: 0
Merhaba!
Sayı: 1
Merhaba!
Sayı: 2"""
                    ),
                    TutorialSection(
                        subtitle = "📊 range() Fonksiyonu",
                        content = """range() sayı dizisi oluşturur:

range(5) → 0, 1, 2, 3, 4
range(1, 5) → 1, 2, 3, 4
range(0, 10, 2) → 0, 2, 4, 6, 8

Dikkat: Bitiş sayısı dahil değil!"""
                    ),
                    TutorialSection(
                        subtitle = "✨ Pratik",
                        content = """Şimdi ilk döngünü yazacaksın!

5 kez "Merhaba!" yazdıracağız.

Hangi kelimeyle başlamalısın? 🤔"""
                    )
                )
            )
        )
        
        2 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 2,
            title = "range() Fonksiyonu",
            story = "range() sayı dizisi oluşturur. 📊 1'den 5'e kadar sayıları yazdıralım.",
            lesson = "range(başlangıç, bitiş) kullanımı",
            codeTemplate = """
# 1'den 5'e kadar say
for i in range(___):
    print(i)
            """.trimIndent(),
            correctCode = "for i in range(1, 6):",
            hints = listOf(
                "range(başlangıç, bitiş) formatı",
                "bitiş dahil değil, 6 yaz",
                "1'den başla, 6'da dur"
            ),
            explanation = "range(1, 6) 1'den başlar, 6'ya kadar gider (6 dahil değil). Yani 1, 2, 3, 4, 5 üretir.",
            options = listOf(
                CodeOption("1", "1, 6", true, "Doğru! 1'den 5'e kadar"),
                CodeOption("2", "1, 5", false, "Bu 1-4 arası olur"),
                CodeOption("3", "5", false, "Bu 0-4 arası olur"),
                CodeOption("4", "0, 5", false, "Bu 0-4 arası olur")
            ),
            expectedOutput = "1\n2\n3\n4\n5"
        )
        
        3 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 3,
            title = "Liste Döngüsü",
            story = "Listedeki her elemanı tek tek işleyebilirsin! 📋",
            lesson = "for eleman in liste: kullanımı",
            codeTemplate = """
# Meyveleri listele
meyveler = ["elma", "armut", "muz"]
for ___ in meyveler:
    print(meyve)
            """.trimIndent(),
            correctCode = "for meyve in meyveler:",
            hints = listOf(
                "Döngü değişkeni adı ver",
                "meyve adını kullan",
                "Her turda bir meyve alınır"
            ),
            explanation = "for döngüsü liste elemanlarını tek tek alır. Her turda bir eleman döngü değişkenine atanır.",
            options = listOf(
                CodeOption("1", "meyve", true, "Doğru! Liste döngüsü yaptın"),
                CodeOption("2", "i", false, "Çalışır ama anlamlı isim kullan"),
                CodeOption("3", "meyveler", false, "Bu listenin kendisi"),
                CodeOption("4", "item", false, "Çalışır ama Türkçe kullan")
            ),
            expectedOutput = "elma\narmut\nmuz"
        )
        
        4 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 4,
            title = "while Döngüsü",
            story = "Koşul doğru olduğu sürece tekrarla! ♾️ while döngüsü.",
            lesson = "while koşul: Koşul True iken çalışır",
            codeTemplate = """
# 5'e kadar say
sayac = 1
___ sayac <= 5:
    print(sayac)
    sayac = sayac + 1
            """.trimIndent(),
            correctCode = "while sayac <= 5:",
            hints = listOf(
                "while kelimesi ile başla",
                "Koşul True iken devam eder",
                "while koşul: formatı"
            ),
            explanation = "while döngüsü koşul True olduğu sürece çalışır. Koşul False olunca durur. Sonsuz döngüye dikkat!",
            options = listOf(
                CodeOption("1", "while", true, "Doğru! while döngüsü başlattın"),
                CodeOption("2", "for", false, "for farklı bir döngü"),
                CodeOption("3", "if", false, "if koşul, döngü değil"),
                CodeOption("4", "until", false, "Python'da until yok")
            ),
            expectedOutput = "1\n2\n3\n4\n5"
        )
        
        5 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 5,
            title = "break Komutu",
            story = "Döngüyü erken sonlandırmak istersen break kullan! 🛑",
            lesson = "break: Döngüyü anında durdurur",
            codeTemplate = """
# 3'ü bulunca dur
for i in range(1, 10):
    if i == 3:
        ___
    print(i)
            """.trimIndent(),
            correctCode = "break",
            hints = listOf(
                "break komutu döngüyü durdurur",
                "Sadece break yaz",
                "Döngüden çıkar"
            ),
            explanation = "break komutu döngüyü anında sonlandırır. Döngüden sonraki kodla devam edilir.",
            options = listOf(
                CodeOption("1", "break", true, "Doğru! Döngüyü durdurdun"),
                CodeOption("2", "stop", false, "Python'da stop yok"),
                CodeOption("3", "exit", false, "exit programı kapatır"),
                CodeOption("4", "return", false, "return fonksiyondan çıkar")
            ),
            expectedOutput = "1\n2"
        )
        
        6 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 6,
            title = "continue Komutu",
            story = "Bir turu atlamak istersen continue kullan! ⏭️",
            lesson = "continue: Bu turu atla, sonrakine geç",
            codeTemplate = """
# 3'ü atla
for i in range(1, 6):
    if i == 3:
        ___
    print(i)
            """.trimIndent(),
            correctCode = "continue",
            hints = listOf(
                "continue komutu turu atlar",
                "Sadece continue yaz",
                "Sonraki tura geç"
            ),
            explanation = "continue komutu o anki turu atlar ve döngünün başına döner. Döngü devam eder.",
            options = listOf(
                CodeOption("1", "continue", true, "Doğru! Turu atladın"),
                CodeOption("2", "skip", false, "Python'da skip yok"),
                CodeOption("3", "next", false, "Python'da next farklı"),
                CodeOption("4", "pass", false, "pass hiçbir şey yapmaz")
            ),
            expectedOutput = "1\n2\n4\n5"
        )
        
        7 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 7,
            title = "Toplam Hesapla",
            story = "Döngü ile sayıları toplayalım! ➕ Akümülatör pattern.",
            lesson = "Döngüde değer biriktirme",
            codeTemplate = """
# 1'den 5'e kadar topla
toplam = 0
for i in range(1, 6):
    toplam = ___
print("Toplam:", toplam)
            """.trimIndent(),
            correctCode = "toplam = toplam + i",
            hints = listOf(
                "Her turda i'yi ekle",
                "toplam = toplam + i",
                "Veya toplam += i"
            ),
            explanation = "Akümülatör pattern: Bir değişkende değer biriktirme. Her turda yeni değer eklenir.",
            options = listOf(
                CodeOption("1", "toplam + i", true, "Doğru! Toplamı hesapladın"),
                CodeOption("2", "i", false, "Bu sadece son değeri atar"),
                CodeOption("3", "toplam * i", false, "Bu çarpma işlemi"),
                CodeOption("4", "toplam - i", false, "Bu çıkarma işlemi")
            ),
            expectedOutput = "Toplam: 15"
        )
        
        8 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 8,
            title = "İç İçe Döngü",
            story = "Döngü içinde döngü! 🎭 Çarpım tablosu yapalım.",
            lesson = "Nested loops: İç içe döngüler",
            codeTemplate = """
# 3x3 çarpım tablosu
for i in range(1, 4):
    ___ j in range(1, 4):
        print(i, "x", j, "=", i*j)
            """.trimIndent(),
            correctCode = "for j in range(1, 4):",
            hints = listOf(
                "İç döngü için for kullan",
                "Farklı değişken adı: j",
                "for j in range(1, 4):"
            ),
            explanation = "İç içe döngülerde dış döngünün her turu için iç döngü tamamen çalışır.",
            options = listOf(
                CodeOption("1", "for", true, "Doğru! İç içe döngü yaptın"),
                CodeOption("2", "while", false, "Çalışır ama for daha uygun"),
                CodeOption("3", "if", false, "if döngü değil"),
                CodeOption("4", "with", false, "with farklı bir yapı")
            ),
            expectedOutput = "1 x 1 = 1\n1 x 2 = 2\n..."
        )
        
        9 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 9,
            title = "enumerate()",
            story = "Hem index hem değeri almak istersen enumerate kullan! 🔢",
            lesson = "enumerate(): Index ve değer birlikte",
            codeTemplate = """
# Numaralı liste
renkler = ["kırmızı", "yeşil", "mavi"]
for index, renk in ___(renkler):
    print(index, "-", renk)
            """.trimIndent(),
            correctCode = "for index, renk in enumerate(renkler):",
            hints = listOf(
                "enumerate() fonksiyonunu kullan",
                "İki değişken al: index, renk",
                "enumerate(liste) formatı"
            ),
            explanation = "enumerate() hem index (sıra numarası) hem de değeri verir. Çok kullanışlı!",
            options = listOf(
                CodeOption("1", "enumerate", true, "Doğru! enumerate kullandın"),
                CodeOption("2", "range", false, "range sadece sayı üretir"),
                CodeOption("3", "list", false, "list dönüşüm yapar"),
                CodeOption("4", "index", false, "index diye fonksiyon yok")
            ),
            expectedOutput = "0 - kırmızı\n1 - yeşil\n2 - mavi"
        )
        
        10 -> CodeLevel(
            chapter = CodeChapter.LOOPS,
            levelNum = 10,
            title = "Döngü Ustası",
            story = "Final! 🏆 Bir sayının faktöriyelini hesapla (5! = 5×4×3×2×1 = 120)",
            lesson = "Tüm döngü bilgilerini birleştir",
            codeTemplate = """
# Faktöriyel hesapla
sayi = 5
sonuc = 1
for i in range(1, ___):
    sonuc = sonuc * i
print("5! =", sonuc)
            """.trimIndent(),
            correctCode = "for i in range(1, sayi + 1):",
            hints = listOf(
                "1'den sayıya kadar çarp",
                "sayi + 1 kullan (5 dahil)",
                "range(1, 6) veya range(1, sayi+1)"
            ),
            explanation = "Tebrikler! Döngüler bölümünü tamamladın. for, while, break, continue ve iç içe döngüleri öğrendin!",
            options = listOf(
                CodeOption("1", "sayi + 1", true, "Mükemmel! Bölümü tamamladın!"),
                CodeOption("2", "sayi", false, "Bu 4'e kadar gider"),
                CodeOption("3", "6", false, "Çalışır ama değişken kullan"),
                CodeOption("4", "5", false, "Bu 4'e kadar gider")
            ),
            expectedOutput = "5! = 120"
        )
        
        else -> getLoopsLevel(1)
    }
}


// ==================== BÖLÜM 4-6: FONKSİYONLAR, ALGORİTMALAR, USTA ====================

private fun getFunctionsLevel(levelNum: Int): CodeLevel {
    val levels = mapOf(
        1 to CodeLevel(CodeChapter.FUNCTIONS, 1, "İlk Fonksiyonun", 
            "Fonksiyonlar tekrar kullanılabilir kod bloklarıdır! 📦", "def ile fonksiyon tanımlama",
            "# Selamlama fonksiyonu\n___ selamla():\n    print(\"Merhaba!\")\n\nselamla()",
            "def selamla():", listOf("def kelimesi ile başla", "Fonksiyon adı ve parantez", "Sonunda : koy"),
            "def fonksiyon_adi(): şeklinde fonksiyon tanımlanır. Fonksiyonu çağırmak için adını yazıp () eklersin.",
            listOf(CodeOption("1", "def", true), CodeOption("2", "function", false), CodeOption("3", "func", false), CodeOption("4", "define", false)),
            "Merhaba!",
            tutorial = TutorialContent(
                title = "Fonksiyonlar",
                sections = listOf(
                    TutorialSection(
                        subtitle = "⚡ Fonksiyon Nedir?",
                        content = """Fonksiyonlar, tekrar tekrar kullanabileceğin kod bloklarıdır.

Bir işi birden fazla yerde yapman gerekiyorsa, her seferinde aynı kodu yazmak yerine fonksiyon oluşturursun.

Avantajları:
- Kod tekrarını önler
- Programı düzenli tutar
- Hataları bulmayı kolaylaştırır"""
                    ),
                    TutorialSection(
                        subtitle = "📝 Fonksiyon Tanımlama",
                        content = """Python'da fonksiyon def kelimesiyle tanımlanır:

def fonksiyon_adi():
    yapılacak işlemler

Kurallar:
1. def ile başla
2. Fonksiyon adı yaz
3. Parantez () ekle
4. İki nokta : koy
5. İçeriği girintili yaz""",
                        codeExample = """def selamla():
    print("Merhaba!")
    print("Nasılsın?")

# Fonksiyonu çağır
selamla()""",
                        codeOutput = """Merhaba!
Nasılsın?"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi ilk fonksiyonunu tanımlayacaksın!

Fonksiyon tanımlamak için hangi kelimeyle başlamalısın?

İpucu: define kelimesinin kısaltması 🤔"""
                    )
                )
            )),
        2 to CodeLevel(CodeChapter.FUNCTIONS, 2, "Parametreli Fonksiyon",
            "Fonksiyonlara değer gönderebilirsin! 📨", "Parametre kullanımı",
            "# İsimle selamla\ndef selamla(___):\n    print(\"Merhaba\", isim)\n\nselamla(\"Ali\")",
            "def selamla(isim):", listOf("Parantez içine parametre yaz", "isim parametresi ekle"),
            "Parametreler fonksiyona dışarıdan değer göndermeyi sağlar.",
            listOf(CodeOption("1", "isim", true), CodeOption("2", "name", false), CodeOption("3", "()", false), CodeOption("4", "input", false)),
            "Merhaba Ali",
            tutorial = TutorialContent(
                title = "Parametreler",
                sections = listOf(
                    TutorialSection(
                        subtitle = "📨 Parametre Nedir?",
                        content = """Parametreler, fonksiyona dışarıdan değer göndermeyi sağlar.

Örneğin bir selamlama fonksiyonu düşün:
- Parametresiz: Herkese aynı şeyi söyler
- Parametreli: Kişiye özel selamlama yapar"""
                    ),
                    TutorialSection(
                        subtitle = "📝 Parametre Kullanımı",
                        content = """Parametre parantez içine yazılır:""",
                        codeExample = """def selamla(isim):
    print("Merhaba", isim)

selamla("Ali")
selamla("Ayşe")
selamla("Mehmet")""",
                        codeOutput = """Merhaba Ali
Merhaba Ayşe
Merhaba Mehmet"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi parametreli bir fonksiyon yazacaksın!

Parantez içine hangi parametre adını yazmalısın?"""
                    )
                )
            )),
        3 to CodeLevel(CodeChapter.FUNCTIONS, 3, "return Kullanımı",
            "Fonksiyondan değer döndür! 🔙", "return ile değer döndürme",
            "# Toplama fonksiyonu\ndef topla(a, b):\n    ___ a + b\n\nsonuc = topla(3, 5)\nprint(sonuc)",
            "return a + b", listOf("return kelimesini kullan", "Sonucu döndür"),
            "return fonksiyondan değer döndürür ve fonksiyonu sonlandırır.",
            listOf(CodeOption("1", "return", true), CodeOption("2", "print", false), CodeOption("3", "give", false), CodeOption("4", "output", false)),
            "8",
            tutorial = TutorialContent(
                title = "return İfadesi",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🔙 return Nedir?",
                        content = """return, fonksiyondan değer döndürür.

print() sadece ekrana yazar, ama return değeri geri verir ve başka yerde kullanabilirsin.

Fark:
- print(): Ekrana yazar, değer döndürmez
- return: Değer döndürür, kullanabilirsin"""
                    ),
                    TutorialSection(
                        subtitle = "📝 return Kullanımı",
                        content = """return ile değer döndürme:""",
                        codeExample = """def topla(a, b):
    return a + b

sonuc = topla(3, 5)
print(sonuc)

# Direkt kullanım
print(topla(10, 20))""",
                        codeOutput = """8
30"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi bir toplama fonksiyonu yazacaksın!

Sonucu döndürmek için hangi kelimeyi kullanmalısın?"""
                    )
                )
            )),
        4 to CodeLevel(CodeChapter.FUNCTIONS, 4, "Varsayılan Değer",
            "Parametreye varsayılan değer ver! 🎁", "Default parameter",
            "# Varsayılan selamlama\ndef selamla(isim___):\n    print(\"Merhaba\", isim)\n\nselamla()",
            "def selamla(isim=\"Misafir\"):", listOf("= ile varsayılan değer", "isim=\"Misafir\" yaz"),
            "Varsayılan değer, parametre verilmezse kullanılır.",
            listOf(CodeOption("1", "=\"Misafir\"", true), CodeOption("2", ":\"Misafir\"", false), CodeOption("3", "->\"Misafir\"", false), CodeOption("4", "", false)),
            "Merhaba Misafir"),
        5 to CodeLevel(CodeChapter.FUNCTIONS, 5, "Çoklu Parametre",
            "Birden fazla parametre kullan! 📊", "Multiple parameters",
            "# Dikdörtgen alanı\ndef alan(en, ___):\n    return en * boy\n\nprint(alan(4, 5))",
            "def alan(en, boy):", listOf("İkinci parametreyi ekle", "boy parametresi"),
            "Fonksiyonlar birden fazla parametre alabilir, virgülle ayrılır.",
            listOf(CodeOption("1", "boy", true), CodeOption("2", "height", false), CodeOption("3", "y", false), CodeOption("4", "b", false)),
            "20"),
        6 to CodeLevel(CodeChapter.FUNCTIONS, 6, "Lambda Fonksiyonları",
            "Tek satırda fonksiyon yaz! ⚡", "Lambda expressions",
            "# Lambda ile kare alma\nkare = ___ x: x ** 2\nprint(kare(5))",
            "lambda x: x ** 2", listOf("lambda kelimesini kullan", "lambda parametre: işlem"),
            "Lambda, tek satırda küçük fonksiyonlar yazmak için kullanılır.",
            listOf(CodeOption("1", "lambda", true), CodeOption("2", "def", false), CodeOption("3", "func", false), CodeOption("4", "=>", false)),
            "25"),
        7 to CodeLevel(CodeChapter.FUNCTIONS, 7, "Recursive Fonksiyon",
            "Kendini çağıran fonksiyon! 🔄", "Recursion",
            "# Faktöriyel hesapla\ndef faktoriyel(n):\n    if n <= 1:\n        return 1\n    return n * ___(n - 1)\n\nprint(faktoriyel(5))",
            "faktoriyel(n - 1)", listOf("Fonksiyon kendini çağırır", "faktoriyel(n-1)"),
            "Recursive fonksiyonlar kendilerini çağırır. Base case (durma koşulu) şart!",
            listOf(CodeOption("1", "faktoriyel", true), CodeOption("2", "factorial", false), CodeOption("3", "self", false), CodeOption("4", "this", false)),
            "120"),
        8 to CodeLevel(CodeChapter.FUNCTIONS, 8, "*args Kullanımı",
            "Sınırsız parametre al! 📦", "*args",
            "# Tüm sayıları topla\ndef topla(___sayilar):\n    return sum(sayilar)\n\nprint(topla(1, 2, 3, 4, 5))",
            "*sayilar", listOf("* işareti kullan", "*args formatı"),
            "*args ile fonksiyona istediğin kadar parametre gönderebilirsin.",
            listOf(CodeOption("1", "*", true), CodeOption("2", "**", false), CodeOption("3", "&", false), CodeOption("4", "@", false)),
            "15"),
        9 to CodeLevel(CodeChapter.FUNCTIONS, 9, "Docstring",
            "Fonksiyonu belgele! 📚", "Documentation",
            "def selamla(isim):\n    ___\"\"\"İsme göre selamlama yapar\"\"\"\n    print(\"Merhaba\", isim)",
            "\"\"\"İsme göre selamlama yapar\"\"\"", listOf("Üç tırnak kullan", "\"\"\"açıklama\"\"\""),
            "Docstring, fonksiyonun ne yaptığını açıklar. Üç tırnak içinde yazılır.",
            listOf(CodeOption("1", "\"\"\"", true), CodeOption("2", "#", false), CodeOption("3", "//", false), CodeOption("4", "/*", false)),
            ""),
        10 to CodeLevel(CodeChapter.FUNCTIONS, 10, "Fonksiyon Ustası",
            "Tüm bilgilerini birleştir! 🏆", "Kapsamlı fonksiyon",
            "# Hesap makinesi\ndef hesapla(a, b, islem=\"+\"):\n    if islem == \"+\":\n        ___ a + b\n    elif islem == \"-\":\n        return a - b\n\nprint(hesapla(10, 5))",
            "return a + b", listOf("return kullan", "Sonucu döndür"),
            "Tebrikler! Fonksiyonlar bölümünü tamamladın!",
            listOf(CodeOption("1", "return", true), CodeOption("2", "print", false), CodeOption("3", "give", false), CodeOption("4", "=", false)),
            "15")
    )
    // Güvenli fallback: levels[1] yoksa ilk seviyeyi kullan
    val defaultLevel = levels[1] ?: levels.values.firstOrNull()
    return levels[levelNum] ?: defaultLevel ?: throw IllegalStateException("No level found for $levelNum and no default level available")
}

private fun getAlgorithmsLevel(levelNum: Int): CodeLevel {
    val levels = mapOf(
        1 to CodeLevel(CodeChapter.ALGORITHMS, 1, "Maksimum Bulma",
            "Listedeki en büyük sayıyı bul! 🔍", "Arama algoritması",
            "# En büyüğü bul\nsayilar = [3, 7, 2, 9, 5]\nen_buyuk = sayilar[0]\nfor sayi in sayilar:\n    if sayi ___ en_buyuk:\n        en_buyuk = sayi\nprint(en_buyuk)",
            "if sayi > en_buyuk:", listOf("> operatörünü kullan", "Büyükse güncelle"),
            "Her elemanı kontrol edip en büyüğü takip ederiz.",
            listOf(CodeOption("1", ">", true), CodeOption("2", "<", false), CodeOption("3", "==", false), CodeOption("4", ">=", false)),
            "9",
            tutorial = TutorialContent(
                title = "Algoritmalar",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🧩 Algoritma Nedir?",
                        content = """Algoritma, bir problemi çözmek için izlenen adımlar dizisidir.

Günlük hayattan örnek:
Çay yapma algoritması:
1. Su kaynat
2. Bardağa çay poşeti koy
3. Kaynar suyu dök
4. 3 dakika bekle
5. Poşeti çıkar

Programlamada da problemleri adım adım çözeriz!"""
                    ),
                    TutorialSection(
                        subtitle = "🔍 Maksimum Bulma",
                        content = """Bir listedeki en büyük sayıyı bulmak için:

1. İlk elemanı "en büyük" kabul et
2. Listeyi gez
3. Her elemanı "en büyük" ile karşılaştır
4. Daha büyükse güncelle""",
                        codeExample = """sayilar = [3, 7, 2, 9, 5]
en_buyuk = sayilar[0]  # 3

for sayi in sayilar:
    if sayi > en_buyuk:
        en_buyuk = sayi

print(en_buyuk)""",
                        codeOutput = "9"
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi maksimum bulma algoritmasını tamamlayacaksın!

Hangi karşılaştırma operatörünü kullanmalısın?"""
                    )
                )
            )),
        2 to CodeLevel(CodeChapter.ALGORITHMS, 2, "Sayı Sayma",
            "Listede kaç tane çift sayı var? 🔢", "Sayma algoritması",
            "# Çift sayıları say\nsayilar = [1, 2, 3, 4, 5, 6]\nsayac = 0\nfor sayi in sayilar:\n    if sayi ___ 2 == 0:\n        sayac += 1\nprint(sayac)",
            "if sayi % 2 == 0:", listOf("% mod operatörü", "2'ye bölümünden kalan 0 ise çift"),
            "% (mod) operatörü bölümden kalanı verir. Çift sayıların 2'ye bölümünden kalan 0'dır.",
            listOf(CodeOption("1", "%", true), CodeOption("2", "/", false), CodeOption("3", "//", false), CodeOption("4", "*", false)),
            "3"),
        3 to CodeLevel(CodeChapter.ALGORITHMS, 3, "Ters Çevirme",
            "Metni ters çevir! 🔄", "String manipulation",
            "# Metni ters çevir\nmetin = \"merhaba\"\nters = metin[___]\nprint(ters)",
            "metin[::-1]", listOf("Slicing kullan", "[::-1] ters çevirir"),
            "Python'da [::-1] slicing ile string veya liste ters çevrilir.",
            listOf(CodeOption("1", "::-1", true), CodeOption("2", "-1::", false), CodeOption("3", "reverse", false), CodeOption("4", "::", false)),
            "abahrem"),
        4 to CodeLevel(CodeChapter.ALGORITHMS, 4, "Asal Sayı Kontrolü",
            "Bir sayının asal olup olmadığını kontrol et! 🔬", "Asal sayı algoritması",
            "# Asal mı?\nsayi = 7\nasal = True\nfor i in range(2, sayi):\n    if sayi % i ___ 0:\n        asal = False\n        break\nprint(asal)",
            "if sayi % i == 0:", listOf("Tam bölünüyor mu?", "== 0 kontrolü"),
            "Asal sayı sadece 1 ve kendisine bölünür. 2'den sayıya kadar kontrol ederiz.",
            listOf(CodeOption("1", "==", true), CodeOption("2", "!=", false), CodeOption("3", ">", false), CodeOption("4", "<", false)),
            "True"),
        5 to CodeLevel(CodeChapter.ALGORITHMS, 5, "Fibonacci",
            "Fibonacci dizisi oluştur! 🐚", "Fibonacci algoritması",
            "# Fibonacci\na, b = 0, 1\nfor i in range(10):\n    print(a)\n    a, b = b, ___",
            "a, b = b, a + b", listOf("Sonraki = önceki ikisinin toplamı", "a + b yaz"),
            "Fibonacci: Her sayı önceki ikisinin toplamıdır. 0, 1, 1, 2, 3, 5, 8...",
            listOf(CodeOption("1", "a + b", true), CodeOption("2", "a * b", false), CodeOption("3", "a - b", false), CodeOption("4", "b + 1", false)),
            "0\n1\n1\n2\n3\n5\n8\n13\n21\n34"),
        6 to CodeLevel(CodeChapter.ALGORITHMS, 6, "Bubble Sort",
            "Listeyi sırala! 📊", "Sıralama algoritması",
            "# Bubble Sort\nliste = [5, 2, 8, 1]\nfor i in range(len(liste)):\n    for j in range(len(liste)-1):\n        if liste[j] ___ liste[j+1]:\n            liste[j], liste[j+1] = liste[j+1], liste[j]\nprint(liste)",
            "if liste[j] > liste[j+1]:", listOf("> operatörünü kullan", "Büyükse yer değiştir"),
            "Bubble Sort: Yan yana elemanları karşılaştırıp büyük olanı sağa taşır.",
            listOf(CodeOption("1", ">", true), CodeOption("2", "<", false), CodeOption("3", "==", false), CodeOption("4", "!=", false)),
            "[1, 2, 5, 8]"),
        7 to CodeLevel(CodeChapter.ALGORITHMS, 7, "Binary Search",
            "Sıralı listede hızlı ara! 🔎", "İkili arama",
            "# Binary Search\nliste = [1, 3, 5, 7, 9, 11]\naranan = 7\nsol, sag = 0, len(liste)-1\nwhile sol <= sag:\n    orta = (sol + sag) ___ 2\n    if liste[orta] == aranan:\n        print(\"Bulundu:\", orta)\n        break",
            "(sol + sag) // 2", listOf("// tam bölme", "Ortayı bul"),
            "Binary Search: Sıralı listede ortadan başlayarak arar. O(log n) karmaşıklık.",
            listOf(CodeOption("1", "//", true), CodeOption("2", "/", false), CodeOption("3", "%", false), CodeOption("4", "*", false)),
            "Bulundu: 3"),
        8 to CodeLevel(CodeChapter.ALGORITHMS, 8, "Palindrom Kontrolü",
            "Tersten de aynı mı? 🔄", "String algoritması",
            "# Palindrom mu?\nkelime = \"kayak\"\nters = kelime[::-1]\nif kelime ___ ters:\n    print(\"Palindrom!\")",
            "if kelime == ters:", listOf("== eşitlik kontrolü", "Aynı mı?"),
            "Palindrom: Tersten okunduğunda da aynı olan kelime. Örn: kayak, aba",
            listOf(CodeOption("1", "==", true), CodeOption("2", "!=", false), CodeOption("3", "is", false), CodeOption("4", "in", false)),
            "Palindrom!"),
        9 to CodeLevel(CodeChapter.ALGORITHMS, 9, "Ortalama Hesaplama",
            "Sayıların ortalamasını bul! 📈", "İstatistik algoritması",
            "# Ortalama\nsayilar = [10, 20, 30, 40, 50]\ntoplam = sum(sayilar)\nortalama = toplam ___ len(sayilar)\nprint(ortalama)",
            "toplam / len(sayilar)", listOf("/ bölme operatörü", "Toplam / adet"),
            "Ortalama = Toplam / Eleman sayısı",
            listOf(CodeOption("1", "/", true), CodeOption("2", "//", false), CodeOption("3", "*", false), CodeOption("4", "%", false)),
            "30.0"),
        10 to CodeLevel(CodeChapter.ALGORITHMS, 10, "Algoritma Ustası",
            "Tüm bilgilerini birleştir! 🏆", "Kapsamlı algoritma",
            "# En küçük 2 sayının toplamı\nsayilar = [5, 2, 8, 1, 9]\nsayilar.___\nprint(sayilar[0] + sayilar[1])",
            "sayilar.sort()", listOf("sort() metodu", "Listeyi sırala"),
            "Tebrikler! Algoritmalar bölümünü tamamladın!",
            listOf(CodeOption("1", "sort()", true), CodeOption("2", "sorted()", false), CodeOption("3", "order()", false), CodeOption("4", "arrange()", false)),
            "3")
    )
    // Güvenli fallback: levels[1] yoksa ilk seviyeyi kullan
    val defaultLevel = levels[1] ?: levels.values.firstOrNull()
    return levels[levelNum] ?: defaultLevel ?: throw IllegalStateException("No level found for $levelNum and no default level available")
}

private fun getMasterLevel(levelNum: Int): CodeLevel {
    val levels = mapOf(
        1 to CodeLevel(CodeChapter.MASTER, 1, "Mini Hesap Makinesi",
            "Tüm bilgilerini birleştir! 🧮", "Fonksiyon + Koşul + Döngü",
            "# Hesap makinesi\ndef hesapla(a, b, islem):\n    if islem == \"+\":\n        return a + b\n    ___ islem == \"-\":\n        return a - b\n\nprint(hesapla(10, 5, \"+\"))",
            "elif islem == \"-\":", listOf("elif kullan", "Çıkarma kontrolü"),
            "Fonksiyonlar, koşullar ve döngüler birlikte güçlü programlar oluşturur!",
            listOf(CodeOption("1", "elif", true), CodeOption("2", "else if", false), CodeOption("3", "if", false), CodeOption("4", "else", false)),
            "15",
            tutorial = TutorialContent(
                title = "Usta Seviye",
                sections = listOf(
                    TutorialSection(
                        subtitle = "🏆 Usta Seviyeye Hoş Geldin!",
                        content = """Tebrikler! Buraya kadar geldin demek ki Python'un temellerini öğrendin.

Bu bölümde öğrendiğin her şeyi birleştireceğiz:
- Değişkenler
- Koşullar (if/elif/else)
- Döngüler (for/while)
- Fonksiyonlar
- Algoritmalar

Gerçek dünya problemlerini çözeceğiz!"""
                    ),
                    TutorialSection(
                        subtitle = "🧮 Mini Hesap Makinesi",
                        content = """İlk projemiz: Basit bir hesap makinesi!

Fonksiyon + Koşul birleşimi:""",
                        codeExample = """def hesapla(a, b, islem):
    if islem == "+":
        return a + b
    elif islem == "-":
        return a - b
    elif islem == "*":
        return a * b
    elif islem == "/":
        return a / b

print(hesapla(10, 5, "+"))
print(hesapla(10, 5, "-"))""",
                        codeOutput = """15
5"""
                    ),
                    TutorialSection(
                        subtitle = "🎯 Pratik",
                        content = """Şimdi hesap makinesini tamamlayacaksın!

Çıkarma işlemi için hangi koşul yapısını kullanmalısın?"""
                    )
                )
            )),
        2 to CodeLevel(CodeChapter.MASTER, 2, "Kelime Sayacı",
            "Metindeki kelime sayısını bul! 📝", "String + Liste + Döngü",
            "# Kelime say\nmetin = \"Merhaba dünya nasılsın\"\nkelimeler = metin.___(\" \")\nprint(len(kelimeler))",
            "metin.split(\" \")", listOf("split() metodu", "Boşluktan böl"),
            "split() metodu metni parçalara ayırır ve liste döndürür.",
            listOf(CodeOption("1", "split", true), CodeOption("2", "divide", false), CodeOption("3", "cut", false), CodeOption("4", "separate", false)),
            "3"),
        3 to CodeLevel(CodeChapter.MASTER, 3, "Tahmin Oyunu",
            "Basit bir tahmin oyunu yap! 🎯", "While + Koşul + Input",
            "# Tahmin oyunu\ngizli = 7\ntahmin = 0\nwhile tahmin ___ gizli:\n    tahmin = int(input(\"Tahmin: \"))\nprint(\"Bildin!\")",
            "while tahmin != gizli:", listOf("!= eşit değil", "Eşit olana kadar devam"),
            "while döngüsü koşul sağlanana kadar devam eder.",
            listOf(CodeOption("1", "!=", true), CodeOption("2", "==", false), CodeOption("3", "<", false), CodeOption("4", ">", false)),
            "Bildin!"),
        4 to CodeLevel(CodeChapter.MASTER, 4, "Liste Filtreleme",
            "Listeden belirli elemanları filtrele! 🔍", "Liste + Döngü + Koşul",
            "# Çift sayıları filtrele\nsayilar = [1, 2, 3, 4, 5, 6]\nciftler = []\nfor s in sayilar:\n    if s % 2 == 0:\n        ciftler.___(s)\nprint(ciftler)",
            "ciftler.append(s)", listOf("append() listeye ekler", "Elemanı ekle"),
            "append() metodu listenin sonuna eleman ekler.",
            listOf(CodeOption("1", "append", true), CodeOption("2", "add", false), CodeOption("3", "insert", false), CodeOption("4", "push", false)),
            "[2, 4, 6]"),
        5 to CodeLevel(CodeChapter.MASTER, 5, "Sözlük Kullanımı",
            "Dictionary ile veri sakla! 📚", "Dictionary",
            "# Öğrenci bilgileri\nogrenci = {\"isim\": \"Ali\", \"yas\": 15}\nprint(ogrenci[___])",
            "ogrenci[\"isim\"]", listOf("Anahtar ile eriş", "\"isim\" anahtarı"),
            "Dictionary key-value çiftleri saklar. dict[key] ile değere erişilir.",
            listOf(CodeOption("1", "\"isim\"", true), CodeOption("2", "isim", false), CodeOption("3", "0", false), CodeOption("4", "name", false)),
            "Ali"),
        6 to CodeLevel(CodeChapter.MASTER, 6, "List Comprehension",
            "Tek satırda liste oluştur! ⚡", "List Comprehension",
            "# Kareleri al\nsayilar = [1, 2, 3, 4, 5]\nkareler = [x**2 ___ x in sayilar]\nprint(kareler)",
            "[x**2 for x in sayilar]", listOf("for kullan", "x**2 for x in liste"),
            "List comprehension ile tek satırda liste oluşturabilirsin.",
            listOf(CodeOption("1", "for", true), CodeOption("2", "in", false), CodeOption("3", "while", false), CodeOption("4", "if", false)),
            "[1, 4, 9, 16, 25]"),
        7 to CodeLevel(CodeChapter.MASTER, 7, "Dosya İşlemleri",
            "Dosyaya yaz ve oku! 📄", "File I/O",
            "# Dosyaya yaz\nwith ___(\"test.txt\", \"w\") as f:\n    f.write(\"Merhaba!\")",
            "open(\"test.txt\", \"w\")", listOf("open() fonksiyonu", "w = write modu"),
            "open() ile dosya açılır. w=yaz, r=oku, a=ekle modları var.",
            listOf(CodeOption("1", "open", true), CodeOption("2", "file", false), CodeOption("3", "read", false), CodeOption("4", "write", false)),
            ""),
        8 to CodeLevel(CodeChapter.MASTER, 8, "Hata Yakalama",
            "Hataları yakala! 🛡️", "Try/Except",
            "# Hata yakalama\ntry:\n    sayi = int(\"abc\")\n___ ValueError:\n    print(\"Geçersiz sayı!\")",
            "except ValueError:", listOf("except kullan", "Hata tipini belirt"),
            "try/except ile hatalar yakalanır ve program çökmez.",
            listOf(CodeOption("1", "except", true), CodeOption("2", "catch", false), CodeOption("3", "error", false), CodeOption("4", "handle", false)),
            "Geçersiz sayı!"),
        9 to CodeLevel(CodeChapter.MASTER, 9, "Class Temelleri",
            "Kendi sınıfını oluştur! 🏗️", "OOP Basics",
            "# Araba sınıfı\n___ Araba:\n    def __init__(self, marka):\n        self.marka = marka\n\narabam = Araba(\"Toyota\")\nprint(arabam.marka)",
            "class Araba:", listOf("class kelimesi", "Sınıf tanımla"),
            "class ile kendi veri tiplerini oluşturabilirsin. OOP'nin temeli!",
            listOf(CodeOption("1", "class", true), CodeOption("2", "def", false), CodeOption("3", "struct", false), CodeOption("4", "type", false)),
            "Toyota"),
        10 to CodeLevel(CodeChapter.MASTER, 10, "Kod Ustası Sertifikası",
            "Tebrikler! Tüm bölümleri tamamladın! 🏆🎉", "Tüm kavramlar",
            "# Son görev: Mükemmel bir mesaj yaz\nmesaj = \"Artık bir Kod Ustasıyım!\"\nprint(___)",
            "print(mesaj)", listOf("mesaj değişkenini yazdır", "print(mesaj)"),
            "🎊 TEBRİKLER! Python'un temellerini öğrendin. Değişkenler, koşullar, döngüler, fonksiyonlar ve algoritmalar artık senin için sır değil!",
            listOf(CodeOption("1", "mesaj", true), CodeOption("2", "\"mesaj\"", false), CodeOption("3", "Mesaj", false), CodeOption("4", "MESAJ", false)),
            "Artık bir Kod Ustasıyım!")
    )
    // Güvenli fallback: levels[1] yoksa ilk seviyeyi kullan
    val defaultLevel = levels[1] ?: levels.values.firstOrNull()
    return levels[levelNum] ?: defaultLevel ?: throw IllegalStateException("No level found for $levelNum and no default level available")
}
