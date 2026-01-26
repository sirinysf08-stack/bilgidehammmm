@file:OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)

package com.example.bilgideham

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

// ===================== MODELLER =====================
data class MathQuestion(val q: String, val options: List<String>, val correct: String)

enum class StoryItemType { SAHNE, KARAKTER }
data class StorySticker(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val type: StoryItemType,
    val tint: Color = Color(0xFF2D1B0D)
)

data class StoryPanelState(
    val scene: StorySticker? = null,
    val characters: List<StorySticker> = emptyList()
)

data class StoryLevel(
    val id: Int,
    val title: String,
    val panelCount: Int,
    val availableStickers: List<StorySticker>,
    val winningSolution: (List<StoryPanelState>) -> Boolean
)

// ===================== ANA EKRAN =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val cs = MaterialTheme.colorScheme
    val headerBrush = Brush.verticalGradient(listOf(cs.primary, cs.tertiary, cs.secondary))

    // --- DURUMLAR ---
    var state by remember { mutableStateOf("LOBBY") }
    var myNick by rememberSaveable { mutableStateOf("Takmaİsim-${Random.nextInt(100, 999)}") }
    var opponentNick by remember { mutableStateOf("Bekleniyor...") }
    var amIHost by remember { mutableStateOf(false) }

    // Yakında uyarısı için state
    var showComingSoonDialog by remember { mutableStateOf(false) }

    // --- RALLY STATE (MATEMATİK) ---
    var rallyCode by remember { mutableStateOf("") }
    var rallyCurrentIdx by remember { mutableIntStateOf(0) }
    var rallyScoreSelf by remember { mutableIntStateOf(0) }
    var rallyScoreOpponent by remember { mutableIntStateOf(0) }
    var rallyTimeLeft by remember { mutableIntStateOf(10) }
    var rallyQuestions by remember { mutableStateOf<List<MathQuestion>>(emptyList()) }
    var myAnsweredValue by remember { mutableStateOf<String?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    // Anonim giriş
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) auth.signInAnonymously()
    }

    // Firestore sync (Rally)
    LaunchedEffect(rallyCode) {
        if (rallyCode.isNotEmpty()) {
            db.collection("math_rally_rooms").document(rallyCode)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        val status = snapshot.getString("status")
                        val currentIdx = snapshot.getLong("currentIdx")?.toInt() ?: 0
                        val qListRaw = snapshot.get("questions") as? List<Map<String, Any>>

                        if (qListRaw != null && rallyQuestions.isEmpty()) {
                            // 🛡️ P0: Unsafe cast → Safe cast (NPE prevention)
                            rallyQuestions = qListRaw.mapNotNull { item ->
                                val q = item["q"] as? String ?: return@mapNotNull null
                                val options = item["options"] as? List<*> ?: return@mapNotNull null
                                val correct = item["correct"] as? String ?: return@mapNotNull null
                                
                                // Type-safe list conversion
                                val safeOptions = options.filterIsInstance<String>()
                                if (safeOptions.size != options.size) return@mapNotNull null
                                
                                MathQuestion(q, safeOptions, correct)
                            }
                        }

                        if (currentIdx != rallyCurrentIdx) {
                            rallyCurrentIdx = currentIdx
                            myAnsweredValue = null
                            rallyTimeLeft = 10
                        }

                        if (amIHost) {
                            opponentNick = snapshot.getString("joineeNick") ?: "Bekleniyor..."
                            rallyScoreOpponent = snapshot.getLong("joineeScore")?.toInt() ?: 0
                        } else {
                            opponentNick = snapshot.getString("hostNick") ?: "Bekleniyor..."
                            rallyScoreOpponent = snapshot.getLong("hostScore")?.toInt() ?: 0
                        }

                        if (status == "playing" && state != "RALLY_GAME") state = "RALLY_GAME"
                        if (status == "finished" && state != "RALLY_RESULT") {
                            if (rallyScoreSelf >= rallyScoreOpponent) showConfetti = true
                            state = "RALLY_RESULT"
                        }

                        if (amIHost && status == "playing") {
                            val hostDone = snapshot.getString("hostAnswer") != null
                            val joinerDone = snapshot.getString("joineeAnswer") != null
                            if (hostDone && joinerDone) {
                                scope.launch {
                                    delay(1500)
                                    if (rallyCurrentIdx < rallyQuestions.size - 1) {
                                        db.collection("math_rally_rooms").document(rallyCode).update(
                                            "currentIdx", rallyCurrentIdx + 1,
                                            "hostAnswer", null,
                                            "joineeAnswer", null
                                        )
                                    } else {
                                        db.collection("math_rally_rooms").document(rallyCode).update("status", "finished")
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    // Zamanlayıcı
    LaunchedEffect(state, rallyCurrentIdx, rallyTimeLeft, myAnsweredValue) {
        if (state == "RALLY_GAME" && myAnsweredValue == null && rallyTimeLeft > 0) {
            delay(1000L)
            rallyTimeLeft--
            if (rallyTimeLeft == 0) {
                val fieldAnswer = if (amIHost) "hostAnswer" else "joineeAnswer"
                db.collection("math_rally_rooms").document(rallyCode).update(fieldAnswer, "TIMEOUT")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(cs.background)) {
        DuelStarDustEffect(color = cs.onBackground.copy(alpha = 0.10f))

        AnimatedContent(targetState = state, label = "duel_state") { target ->
            when (target) {
                "LOBBY" -> DuelLobby(
                    title = "Sınıf Düellosu",
                    subtitle = "Rekabet modu",
                    headerBrush = headerBrush,
                    onStoryDuel = { showComingSoonDialog = true }, // Uyarı tetikleyici
                    onMathRally = { state = "RALLY_SETUP" },
                    onBack = { navController.popBackStack() }
                )

                "RALLY_SETUP" -> RallySetupScreenModern(
                    headerBrush = headerBrush,
                    nick = myNick,
                    onNickChange = { myNick = it },
                    onHost = { count ->
                        val code = (1000..9999).random().toString()
                        val qList = generateHardMathQuestions(count)
                        amIHost = true
                        rallyCode = code
                        scope.launch {
                            db.collection("math_rally_rooms").document(code).set(
                                hashMapOf(
                                    "hostNick" to myNick,
                                    "status" to "waiting",
                                    "qCount" to count,
                                    "questions" to qList,
                                    "currentIdx" to 0,
                                    "hostScore" to 0,
                                    "joineeScore" to 0,
                                    "hostAnswer" to null,
                                    "joineeAnswer" to null
                                )
                            )
                            state = "RALLY_WAITING"
                        }
                    },
                    onJoin = { state = "RALLY_JOIN_INPUT" },
                    onBack = { state = "LOBBY" }
                )

                "RALLY_WAITING" -> RallyWaitingRoomModern(headerBrush = headerBrush, code = rallyCode, onBack = { rallyCode = ""; state = "RALLY_SETUP" })

                "RALLY_JOIN_INPUT" -> RallyJoinInputModern(
                    headerBrush = headerBrush,
                    onJoin = { code ->
                        scope.launch {
                            val doc = db.collection("math_rally_rooms").document(code).get().await()
                            if (doc.exists() && doc.getString("status") == "waiting") {
                                amIHost = false
                                rallyCode = code
                                db.collection("math_rally_rooms").document(code).update("joineeNick", myNick, "status", "playing")
                            }
                        }
                    },
                    onBack = { state = "RALLY_SETUP" }
                )

                "RALLY_GAME" -> RallyGameScreenModern(
                    headerBrush = headerBrush,
                    questions = rallyQuestions,
                    currentIndex = rallyCurrentIdx,
                    scoreSelf = rallyScoreSelf,
                    scoreOpponent = rallyScoreOpponent,
                    timeLeft = rallyTimeLeft,
                    answered = myAnsweredValue,
                    selfNick = myNick,
                    opponentNick = opponentNick,
                    onAnswer = { ans ->
                        if (myAnsweredValue == null && rallyQuestions.isNotEmpty()) {
                            myAnsweredValue = ans
                            if (ans == rallyQuestions[rallyCurrentIdx].correct) rallyScoreSelf += 10
                            val fieldScore = if (amIHost) "hostScore" else "joineeScore"
                            val fieldAnswer = if (amIHost) "hostAnswer" else "joineeAnswer"
                            db.collection("math_rally_rooms").document(rallyCode).update(fieldScore, rallyScoreSelf, fieldAnswer, ans)
                        }
                    },
                    onBack = { state = "RALLY_SETUP" }
                )

                "RALLY_RESULT" -> RallyResultScreenModern(
                    headerBrush = headerBrush,
                    win = rallyScoreSelf >= rallyScoreOpponent,
                    scoreSelf = rallyScoreSelf,
                    scoreOpponent = rallyScoreOpponent,
                    onBack = {
                        rallyCode = ""; rallyQuestions = emptyList(); rallyCurrentIdx = 0; rallyScoreSelf = 0
                        rallyScoreOpponent = 0; rallyTimeLeft = 10; myAnsweredValue = null; state = "LOBBY"; showConfetti = false
                    }
                )
            }
        }

        // HİKAYE DÜELLOSU - YAKINDA DİALOGU
        if (showComingSoonDialog) {
            AlertDialog(
                onDismissRequest = { showComingSoonDialog = false },
                icon = { Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = cs.primary, modifier = Modifier.size(40.dp)) },
                title = { Text("Çok Yakında!", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Hikâye Düellosu şu an hazırlık aşamasında. Çok yakında arkadaşlarınla en yaratıcı hikayeleri burada kurgulayabileceksin!",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showComingSoonDialog = false }) {
                        Text("Bekliyorum 🚀", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )
        }

        if (showConfetti) ConfettiEffectModern()
    }
}

// ===================== MODERN HEADER =====================
@Composable
private fun DuelHeader(title: String, subtitle: String, headerBrush: Brush, onBack: (() -> Unit)?, trailing: (@Composable RowScope.() -> Unit)? = null) {
    Box(modifier = Modifier.fillMaxWidth().height(116.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(headerBrush)) {
        DuelStarDustEffect(color = Color.White.copy(alpha = 0.15f))
        Row(modifier = Modifier.fillMaxSize().padding(top = 30.dp, start = 12.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            else Spacer(Modifier.width(48.dp))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, maxLines = 1)
                Text(subtitle, color = Color.White.copy(alpha = 0.88f), fontSize = 12.sp, maxLines = 1)
            }
            Row(modifier = Modifier.widthIn(min = 48.dp), horizontalArrangement = Arrangement.End) { trailing?.invoke(this) ?: Spacer(Modifier.width(8.dp)) }
        }
    }
}

// ===================== LOBBY =====================
@Composable
private fun DuelLobby(title: String, subtitle: String, headerBrush: Brush, onStoryDuel: () -> Unit, onMathRally: () -> Unit, onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize()) {
        DuelHeader(title, subtitle, headerBrush, onBack)
        Column(Modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState())) {
            DuelShowcaseCard("Matematik Rallisi", "Online hız ve zeka yarışı.", "⚡", "Canlı", Brush.linearGradient(listOf(Color(0xFF00BCD4), Color(0xFF3F51B5))), onMathRally)
            Spacer(Modifier.height(14.dp))
            DuelShowcaseCard("Hikâye Düellosu", "Antika kitapta kurgu savaşı.", "📖", "Yakında", Brush.linearGradient(listOf(Color(0xFFFF8A65), Color(0xFF8D6E63))), onStoryDuel)
        }
    }
}

@Composable
private fun DuelShowcaseCard(title: String, desc: String, emoji: String, badge: String, gradient: Brush, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(140.dp).shadow(10.dp, RoundedCornerShape(28.dp)).clickable { onClick() }, shape = RoundedCornerShape(28.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(gradient).padding(18.dp)) {
            Column {
                Surface(color = Color.White.copy(0.22f), shape = RoundedCornerShape(10.dp)) {
                    Text(badge, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(desc, color = Color.White.copy(0.9f), fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = Color.White)
                }
            }
        }
    }
}

// ===================== SETUP & WAITING =====================
@Composable
private fun RallySetupScreenModern(headerBrush: Brush, nick: String, onNickChange: (String) -> Unit, onHost: (Int) -> Unit, onJoin: () -> Unit, onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var selectedCount by rememberSaveable { mutableIntStateOf(10) }
    Column(Modifier.fillMaxSize()) {
        DuelHeader("Ayarlar", "Matematik Rallisi", headerBrush, onBack)
        Column(Modifier.padding(18.dp)) {
            OutlinedTextField(value = nick, onValueChange = onNickChange, label = { Text("Takma Ad") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("Soru Sayısı", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 15, 25).forEach { c ->
                    FilterChip(selected = selectedCount == c, onClick = { selectedCount = c }, label = { Text("$c Soru") })
                }
            }
            Spacer(Modifier.height(24.dp))
            DuelPrimaryButton("Oda Kur", Brush.horizontalGradient(listOf(cs.primary, cs.tertiary))) { onHost(selectedCount) }
            Spacer(Modifier.height(12.dp))
            DuelSecondaryButton("Koda Katıl", onJoin)
        }
    }
}

@Composable
private fun RallyWaitingRoomModern(headerBrush: Brush, code: String, onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val qr = remember(code) { buildQrBitmap(code) }
    Column(Modifier.fillMaxSize()) {
        DuelHeader("Oda Bekleme", code, headerBrush, onBack)
        Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Kod: $code", fontSize = 44.sp, fontWeight = FontWeight.Black, color = cs.primary)
            Spacer(Modifier.height(20.dp))
            qr?.let { Image(it.asImageBitmap(), null, Modifier.size(240.dp)) }
            Spacer(Modifier.height(20.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Rakip bekleniyor...", modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
private fun RallyJoinInputModern(headerBrush: Brush, onJoin: (String) -> Unit, onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var code by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        DuelHeader("Koda Katıl", "Oda Kodunu Gir", headerBrush, onBack)
        Column(Modifier.padding(18.dp)) {
            OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("4 Haneli Kod") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            DuelPrimaryButton("Katıl", Brush.horizontalGradient(listOf(cs.tertiary, cs.primary)), code.length == 4) { onJoin(code) }
        }
    }
}

// ===================== GAME & RESULT =====================
@Composable
private fun RallyGameScreenModern(headerBrush: Brush, questions: List<MathQuestion>, currentIndex: Int, scoreSelf: Int, scoreOpponent: Int, timeLeft: Int, answered: String?, selfNick: String, opponentNick: String, onAnswer: (String) -> Unit, onBack: () -> Unit) {
    if (questions.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    val q = questions[currentIndex]
    Column(Modifier.fillMaxSize()) {
        DuelHeader("Soru ${currentIndex + 1}", "${timeLeft}s", headerBrush, onBack)
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ScorePill(selfNick, scoreSelf, cs.primary)
                ScorePill(opponentNick, scoreOpponent, cs.tertiary)
            }
            Spacer(Modifier.height(20.dp))
            Card(Modifier.fillMaxWidth().height(120.dp), colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(q.q, fontSize = 34.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(20.dp))
            q.options.forEach { opt ->
                Button(onClick = { onAnswer(opt) }, enabled = answered == null, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text(opt) }
            }
        }
    }
}

@Composable
private fun RallyResultScreenModern(headerBrush: Brush, win: Boolean, scoreSelf: Int, scoreOpponent: Int, onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize()) {
        DuelHeader("Sonuç", if (win) "Zafer!" else "Mücadele!", headerBrush, null)
        Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(120.dp), tint = Color(0xFFFFC107))
            Text("Puanın: $scoreSelf", fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Rakip: $scoreOpponent", fontSize = 18.sp)
            Spacer(Modifier.height(20.dp))
            DuelPrimaryButton("Geri Dön", Brush.horizontalGradient(listOf(cs.primary, cs.tertiary))) { onBack() }
        }
    }
}

// ===================== BUTONLAR & EFEKTLER =====================

@Composable
private fun ScorePill(label: String, score: Int, tint: Color) {
    Surface(color = tint.copy(0.12f), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label.take(8), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tint)
            Text("$score", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun DuelPrimaryButton(text: String, gradient: Brush, enabled: Boolean = true, onClick: () -> Unit) {
    // HATA BURADAYDI: Color.Gray -> SolidColor(Color.Gray) olarak düzeltildi
    val backgroundBrush = if (enabled) gradient else SolidColor(Color.Gray)

    Box(modifier = Modifier.fillMaxWidth().height(58.dp).clip(RoundedCornerShape(18.dp))
        .background(backgroundBrush).clickable(enabled) { onClick() }, contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun DuelSecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp)) { Text(text) }
}

@Composable
private fun DuelStarDustEffect(color: Color) {
    val transition = rememberInfiniteTransition(label = "")
    val moveY by transition.animateFloat(0f, -55f, infiniteRepeatable(tween(5200), RepeatMode.Restart), label = "")
    Canvas(Modifier.fillMaxSize()) {
        val r = Random(123)
        repeat(20) {
            drawCircle(color, radius = 4f, center = Offset(r.nextFloat() * size.width, (r.nextFloat() * size.height + moveY) % size.height), alpha = 0.2f)
        }
    }
}

@Composable
private fun ConfettiEffectModern() {
    val transition = rememberInfiniteTransition(label = "")
    val y by transition.animateFloat(0f, 2000f, infiniteRepeatable(tween(3000), RepeatMode.Restart), label = "")
    Canvas(Modifier.fillMaxSize()) {
        repeat(50) { i -> drawCircle(Color.Red, radius = 6f, center = Offset((i * 150f) % size.width, (y + i * 100) % size.height)) }
    }
}

// ===================== YARDIMCI =====================
fun generateHardMathQuestions(count: Int): List<Map<String, Any>> {
    return List(count) {
        val a = Random.nextInt(20, 100); val b = Random.nextInt(10, 50)
        mapOf("q" to "$a + $b = ?", "options" to listOf("${a+b}", "${a+b+2}", "${a+b-3}", "${a+b+5}").shuffled(), "correct" to "${a+b}")
    }
}

fun buildQrBitmap(text: String): Bitmap? {
    return try {
        val m = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val b = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) for (y in 0 until 512) b.setPixel(x, y, if (m.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        b
    } catch (_: Exception) { null }
}