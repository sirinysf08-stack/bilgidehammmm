package com.example.bilgideham

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable // ✅ EKLENEN IMPORT (Hatayı çözen satır)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// --- RENK PALETİ (OYUN) ---
val GameBg = Color(0xFFF0F4F8) // Çok açık gri-mavi (Göz yormaz)
val GameSurface = Color(0xFFFFFFFF)
val RobotColor = Color(0xFF6200EA) // Canlı Mor
val TargetColor = Color(0xFF00C853) // Yeşil
val WallColor = Color(0xFF37474F) // Koyu Gri Duvar
val PathColor = Color(0xFFE3F2FD) // Açık Mavi Yol

// --- ENUMLAR ---
enum class RoboCommand(val icon: ImageVector, val color: Color, val label: String) {
    MOVE(Icons.Default.ArrowUpward, Color(0xFF4CAF50), "İleri"),
    RIGHT(Icons.AutoMirrored.Filled.ArrowForward, Color(0xFF2196F3), "Sağ"),
    LEFT(Icons.AutoMirrored.Filled.ArrowBack, Color(0xFFFF9800), "Sol")
}

enum class GridType { EMPTY, WALL, START, TARGET, STAR }

data class LevelConfig(
    val levelNum: Int,
    val size: Int,
    val maxCommands: Int,
    val timeSeconds: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoboticCodingScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // --- PREFS KAYIT/OKUMA ---
    val prefs = remember { context.getSharedPreferences("robotic_coding_prefs", android.content.Context.MODE_PRIVATE) }
    
    // --- OYUN DURUMLARI (SharedPreferences'tan yükle) ---
    var currentLevel by remember { mutableIntStateOf(prefs.getInt("current_level", 1)) }
    var totalScore by remember { mutableIntStateOf(prefs.getInt("total_score", 0)) }
    var gameStatus by remember { mutableStateOf("IDLE") } // IDLE, RUNNING, WON, LOST
    var message by remember { mutableStateOf("Algoritmayı Kur! 🧠") }

    // Level Ayarları
    val levelConfig = remember(currentLevel) { generateLevelConfig(currentLevel) }
    val gridSize = levelConfig.size

    // Harita ve Objeler
    var gridMap by remember { mutableStateOf(generateMap(levelConfig)) }

    // Robot Durumu
    var robotPos by remember { mutableStateOf(findStartPos(gridMap, gridSize)) }
    var robotDir by remember { mutableStateOf(0) } // 0: Up, 90: Right...

    // Komutlar
    val commandList = remember { mutableStateListOf<RoboCommand>() }

    // Sayaçlar
    var timeLeft by remember { mutableStateOf(levelConfig.timeSeconds) }
    var collectedStars by remember { mutableStateOf(0) }

    // Animasyon
    val rotationAnim by animateFloatAsState(targetValue = robotDir.toFloat(), label = "rotation")

    // --- ZAMANLAYICI ---
    LaunchedEffect(gameStatus, currentLevel) {
        if (gameStatus == "IDLE") {
            timeLeft = levelConfig.timeSeconds
            while (timeLeft > 0 && gameStatus == "IDLE") {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0 && gameStatus == "IDLE") {
                gameStatus = "LOST"
                message = "Zaman Doldu! ⏰"
            }
        }
    }

    // --- KODU ÇALIŞTIRMA ---
    fun runCode() {
        if (commandList.isEmpty()) {
            message = "Komut girmedin ki! 😅"
            return
        }

        scope.launch {
            gameStatus = "RUNNING"
            message = "Robot İşleniyor..."

            // Başlangıca dön
            robotPos = findStartPos(gridMap, gridSize)
            robotDir = 0
            collectedStars = 0
            val tempMap = gridMap.toMutableList()

            for (cmd in commandList) {
                delay(500) // Hareket hızı

                when (cmd) {
                    RoboCommand.RIGHT -> robotDir = (robotDir + 90) % 360
                    RoboCommand.LEFT -> robotDir = (robotDir - 90 + 360) % 360
                    RoboCommand.MOVE -> {
                        val dx = when(robotDir) { 90 -> 1; 270 -> -1; else -> 0 }
                        val dy = when(robotDir) { 180 -> 1; 0 -> -1; else -> 0 }

                        val nx = robotPos.first + dx
                        val ny = robotPos.second + dy
                        val idx = ny * gridSize + nx

                        if (nx in 0 until gridSize && ny in 0 until gridSize) {
                            val cell = tempMap[idx]
                            if (cell == GridType.WALL) {
                                gameStatus = "LOST"
                                message = "Çarptık! 💥"
                                return@launch
                            } else {
                                robotPos = Pair(nx, ny)
                                if (cell == GridType.STAR) {
                                    collectedStars++
                                    tempMap[idx] = GridType.EMPTY
                                    gridMap = tempMap.toList()
                                }
                                if (cell == GridType.TARGET) {
                                    val timeBonus = timeLeft * 10
                                    val starBonus = collectedStars * 50
                                    totalScore += (100 + timeBonus + starBonus)
                                    // Skoru kaydet
                                    prefs.edit().putInt("total_score", totalScore).apply()
                                    gameStatus = "WON"
                                    message = "Harika İş! 🎉"
                                    return@launch
                                }
                            }
                        } else {
                            gameStatus = "LOST"
                            message = "Yoldan Çıktın! 🚧"
                            return@launch
                        }
                    }
                }
            }
            if (gameStatus == "RUNNING") {
                gameStatus = "LOST"
                message = "Enerji bitti... 🔋"
            }
        }
    }

    fun nextLevel() {
        currentLevel++
        // Seviyeyi ve skoru kaydet
        prefs.edit().putInt("current_level", currentLevel).putInt("total_score", totalScore).apply()
        
        commandList.clear()
        gridMap = generateMap(generateLevelConfig(currentLevel))
        robotPos = findStartPos(gridMap, gridSize)
        robotDir = 0
        gameStatus = "IDLE"
        message = "Hazır mısın?"
    }

    fun retryLevel() {
        commandList.clear()
        gridMap = generateMap(levelConfig)
        robotPos = findStartPos(gridMap, gridSize)
        robotDir = 0
        gameStatus = "IDLE"
        message = "Tekrar dene!"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Seviye $currentLevel", fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(Modifier.width(12.dp))
                        // Skor Rozeti
                        Surface(color = Color(0xFFFFF9C4), shape = RoundedCornerShape(12.dp)) {
                            Text("⭐ $totalScore", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GameBg)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- ÜST BİLGİ (Süre ve Mesaj) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zamanlayıcı
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if(timeLeft < 10) Color(0xFFFFEBEE) else Color.White,
                    border = BorderStroke(1.dp, if(timeLeft < 10) Color.Red else Color.LightGray)
                ) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(Icons.Default.Timer, null, modifier = Modifier.size(18.dp), tint = if(timeLeft < 10) Color.Red else Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text("$timeLeft sn", fontWeight = FontWeight.Bold, color = if(timeLeft < 10) Color.Red else Color.Black)
                    }
                }

                // Durum Mesajı
                Text(message, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }

            // --- OYUN HARİTASI ---
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.padding(8.dp).size(300.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridSize),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(gridSize * gridSize) { index ->
                            val x = index % gridSize
                            val y = index / gridSize
                            val cell = gridMap[index]

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .border(0.5.dp, Color.White)
                                    .background(
                                        when (cell) {
                                            GridType.WALL -> WallColor
                                            GridType.TARGET -> TargetColor.copy(alpha = 0.2f)
                                            GridType.START -> Color.Blue.copy(alpha = 0.1f)
                                            else -> PathColor
                                        }, RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when (cell) {
                                    GridType.TARGET -> Icon(Icons.Default.Flag, null, tint = TargetColor)
                                    GridType.STAR -> Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
                                    else -> {}
                                }

                                if (robotPos.first == x && robotPos.second == y) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = "Robot",
                                        tint = RobotColor,
                                        modifier = Modifier.fillMaxSize(0.8f).rotate(rotationAnim)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- KOMUT SIRA ÇUBUĞU (Enerji) ---
            val progress = 1f - (commandList.size.toFloat() / levelConfig.maxCommands.toFloat())
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kod Hafızası", fontSize = 12.sp, color = Color.Gray)
                    Text("${commandList.size}/${levelConfig.maxCommands}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = if(progress < 0.3f) Color(0xFFFF5252) else Color(0xFF448AFF),
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            Spacer(Modifier.height(12.dp))

            // --- KOMUT LİSTESİ (Kod Satırları) ---
            Card(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(commandList.size) { i ->
                        val cmd = commandList[i]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text("${i+1}", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(20.dp))
                            Icon(cmd.icon, null, tint = cmd.color, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(cmd.label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.weight(1f))
                            if(gameStatus == "IDLE") {
                                // ✅ DÜZELTİLEN KISIM: clickable import edildiği için artık hata vermeyecek
                                Icon(
                                    Icons.Default.Close, null,
                                    tint = Color.Red.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp).clickable { commandList.removeAt(i) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- KONTROL BUTONLARI ---
            if (gameStatus == "IDLE") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Butonlar artık daha büyük ve belirgin
                    RoboControlButton(RoboCommand.LEFT) { if (commandList.size < levelConfig.maxCommands) commandList.add(it) }
                    RoboControlButton(RoboCommand.MOVE) { if (commandList.size < levelConfig.maxCommands) commandList.add(it) }
                    RoboControlButton(RoboCommand.RIGHT) { if (commandList.size < levelConfig.maxCommands) commandList.add(it) }
                }

                Spacer(Modifier.height(12.dp))

                // Başlat Butonu
                Button(
                    onClick = { runCode() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RobotColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("KODU ÇALIŞTIR", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }

    // --- OYUN SONU DİYALOĞU (POP-UP) ---
    if (gameStatus == "WON" || gameStatus == "LOST") {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = if(gameStatus == "WON") "TEBRİKLER! 🎉" else "ÜZGÜNÜM 😔",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = if(gameStatus == "WON") Color(0xFF2E7D32) else Color(0xFFC62828),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(message, textAlign = TextAlign.Center, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    if(gameStatus == "WON") {
                        Text("Toplanan Yıldız: $collectedStars", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                if (gameStatus == "WON") {
                    Button(
                        onClick = { nextLevel() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) { Text("SONRAKİ LEVEL", color = Color.White) }
                } else {
                    Button(
                        onClick = { retryLevel() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) { Text("TEKRAR DENE", color = Color.White) }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun RoboControlButton(cmd: RoboCommand, onClick: (RoboCommand) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = { onClick(cmd) },
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = cmd.color),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Icon(cmd.icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(cmd.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

// --- LEVEL MANTIĞI ---

fun generateLevelConfig(level: Int): LevelConfig {
    val size = when {
        level <= 5 -> 5
        level <= 15 -> 6
        level <= 30 -> 7
        else -> 8
    }
    return LevelConfig(
        levelNum = level,
        size = size,
        maxCommands = 8 + (level),
        timeSeconds = 20 + (level * 2)
    )
}

fun generateMap(config: LevelConfig): List<GridType> {
    val totalCells = config.size * config.size
    val map = MutableList(totalCells) { GridType.EMPTY }

    map[0] = GridType.START

    val targetRange = (totalCells - (config.size * 2)) until totalCells
    val targetIdx = targetRange.random()
    map[targetIdx] = GridType.TARGET

    val wallCount = (config.levelNum * 1.2).toInt().coerceAtMost(totalCells / 3)
    var wallsPlaced = 0
    while (wallsPlaced < wallCount) {
        val idx = Random.nextInt(1, totalCells)
        if (map[idx] == GridType.EMPTY && idx != targetIdx) {
            map[idx] = GridType.WALL
            wallsPlaced++
        }
    }

    val starCount = (config.levelNum / 3) + 1
    var starsPlaced = 0
    while (starsPlaced < starCount) {
        val idx = Random.nextInt(1, totalCells)
        if (map[idx] == GridType.EMPTY) {
            map[idx] = GridType.STAR
            starsPlaced++
        }
    }

    return map
}

fun findStartPos(map: List<GridType>, size: Int): Pair<Int, Int> {
    val idx = map.indexOf(GridType.START)
    return if (idx != -1) Pair(idx % size, idx / size) else Pair(0, 0)
}