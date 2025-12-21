package com.example.bilgideham

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    darkMode: Boolean,
    onToggleTheme: () -> Unit,
    onToggleBrightness: () -> Unit,
    currentBrightness: Int
) {
    val cs = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val dailyTarget = 30

    val statsManager = remember { StatsManager(context) }
    var solvedToday by remember { mutableIntStateOf(0) }

    // MOTİVASYON MESAJLARI LİSTESİ
    val motivationMessages = remember {
        listOf(
            "Zirveye Adım Adım! 🚀",
            "Bilgi En Büyük Güçtür! 🧠",
            "Harika İşler Çıkaralım! ✨",
            "Pes Etmek Yok. 💪",
            "Bugün Yeni Bir Macera! 🗺️",
            "Öğrenmek En Büyük Eğlence! 🎢",
            "Rekoru Kırmaya Hazırlan!🔥"
        )
    }

    // Her ekran yenilendiğinde rastgele bir mesaj seçer
    val randomMessage = remember { motivationMessages.random() }

    fun reloadDailySolved() {
        val (c, w) = statsManager.getTodayTotals()
        solvedToday = (c + w).coerceAtLeast(0)
    }

    LaunchedEffect(Unit) { reloadDailySolved() }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) reloadDailySolved()
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = cs.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(resolveTopGradient(cs, darkMode))
                    ) {
                        HomeStarDustEffect(color = Color.White.copy(alpha = 0.25f))

                        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                AnimatedBrandWordmark(
                                    darkMode = darkMode,
                                    compact = false
                                )
                            }
                            Box(modifier = Modifier.align(Alignment.TopEnd).size(70.dp)) {
                                HomeRobotHead(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    ColorfulDrawerItem("Ana Sayfa", Icons.Rounded.Home, Color(0xFF1976D2)) {
                        scope.launch { drawerState.close() }
                    }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Yapay Zeka Laboratuvarı ✨", Color(0xFF9C27B0))
                    ColorfulDrawerItem("Yol Gösterici (AI)", Icons.Rounded.Search, Color(0xFFAB47BC)) { scope.launch { drawerState.close() }; navController.navigate("scan_solve") }
                    ColorfulDrawerItem("Yapay Zeka Sözlüsü", Icons.Rounded.Mic, Color(0xFFEC407A)) { scope.launch { drawerState.close() }; navController.navigate("ai_oral_exam") }
                    ColorfulDrawerItem("Tarihle Sohbet", Icons.Rounded.HistoryEdu, Color(0xFF7E57C2)) { scope.launch { drawerState.close() }; navController.navigate("history_chat") }
                    ColorfulDrawerItem("Öğretmen Sensin!", Icons.Rounded.CastForEducation, Color(0xFF26A69A)) { scope.launch { drawerState.close() }; navController.navigate("be_the_teacher") }
                    ColorfulDrawerItem("Kompozisyon Düzeltici", Icons.Rounded.Edit, Color(0xFFEF5350)) { scope.launch { drawerState.close() }; navController.navigate("composition_fixer") }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Dil Dünyası 🌍", Color(0xFF0097A7))
                    ColorfulDrawerItem("English Chat Buddy", Icons.Rounded.ChatBubble, Color(0xFF00ACC1)) { scope.launch { drawerState.close() }; navController.navigate("english_chat_buddy") }
                    ColorfulDrawerItem("İngilizce Aksan Koçu", Icons.Rounded.RecordVoiceOver, Color(0xFF00BCD4)) { scope.launch { drawerState.close() }; navController.navigate("accent_coach") }
                    ColorfulDrawerItem("Arapça Hafız", Icons.Rounded.Translate, Color(0xFF2E7D32)) { scope.launch { drawerState.close() }; navController.navigate("arabic_coach") }
                    ColorfulDrawerItem("Kelime Avı", Icons.Rounded.Extension, Color(0xFF00897B)) { scope.launch { drawerState.close() }; navController.navigate("word_hunt") }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Öğrenci Çantası 🎒", Color(0xFFFF6F00))
                    ColorfulDrawerItem("Sınav Geri Sayım", Icons.Rounded.Timer, Color(0xFFFF7043)) { scope.launch { drawerState.close() }; navController.navigate("exam_countdown") }
                    ColorfulDrawerItem("Ders Programım", Icons.Rounded.DateRange, Color(0xFFFFA726)) { scope.launch { drawerState.close() }; navController.navigate("timetable") }
                    ColorfulDrawerItem("Kitap Kurdu", Icons.Rounded.MenuBook, Color(0xFFFFCA28)) { scope.launch { drawerState.close() }; navController.navigate("book_worm") }
                    ColorfulDrawerItem("Akıllı Sözlük", Icons.Rounded.Translate, Color(0xFF8D6E63)) { scope.launch { drawerState.close() }; navController.navigate("ai_dictionary") }
                    ColorfulDrawerItem("Coğrafya Atlası", Icons.Rounded.Map, Color(0xFF66BB6A)) { scope.launch { drawerState.close() }; navController.navigate("atlas") }
                    ColorfulDrawerItem("Günün Bilimi", Icons.Rounded.Lightbulb, Color(0xFFFDD835)) { scope.launch { drawerState.close() }; navController.navigate("science_fact") }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Teneffüs Zamanı 🎮", Color(0xFF2E7D32))
                    ColorfulDrawerItem("Oyunlar & Robo-Kodlama", Icons.Rounded.SportsEsports, Color(0xFF43A047)) { scope.launch { drawerState.close() }; navController.navigate("games") }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Analiz & Rapor 📈", Color(0xFF1565C0))
                    ColorfulDrawerItem("Hata Analiz Raporu", Icons.Rounded.Analytics, Color(0xFF1976D2)) { scope.launch { drawerState.close() }; navController.navigate("progress") }
                    ColorfulDrawerItem("Geçmiş Sorular", Icons.Rounded.History, Color(0xFF283593)) { scope.launch { drawerState.close() }; navController.navigate("history") }
                    Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).alpha(0.1f))

                    DrawerSectionTitle("Sistem", Color(0xFF546E7A))
                    ColorfulDrawerItem("Tema Değiştir", Icons.Rounded.Palette, Color(0xFF78909C)) { scope.launch { drawerState.close() }; navController.navigate("theme_picker") }
                    ColorfulDrawerItem("Hata Bildir", Icons.Rounded.BugReport, Color(0xFF78909C)) { scope.launch { drawerState.close() }; openBugReportEmail(context, "2.0.0") }

                    Spacer(Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { scope.launch { drawerState.close() }; navController.navigate("admin_panel") },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.Security, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Yönetici Paneli", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(brush = resolveTopGradient(cs, darkMode))
                ) {
                    HomeStarDustEffect(color = Color.White.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp, start = 12.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        Spacer(Modifier.width(4.dp))

                        AnimatedBrandWordmark(
                            modifier = Modifier.weight(1f),
                            darkMode = darkMode,
                            compact = true
                        )

                        IconButton(onClick = onToggleTheme) {
                            Crossfade(targetState = darkMode, label = "themeAnim") { isDark ->
                                if (isDark) {
                                    Icon(Icons.Rounded.Bedtime, "Karanlık Mod", tint = Color(0xFFC5CAE9), modifier = Modifier.size(26.dp))
                                } else {
                                    Icon(Icons.Rounded.WbSunny, "Aydınlık Mod", tint = Color(0xFFFFEE58), modifier = Modifier.size(26.dp))
                                }
                            }
                        }

                        IconButton(onClick = onToggleBrightness) {
                            val (brightnessIcon, iconTint) = when (currentBrightness) {
                                0 -> Pair(Icons.Rounded.BrightnessLow, Color.White.copy(alpha = 0.5f))
                                1 -> Pair(Icons.Rounded.BrightnessMedium, Color.White.copy(alpha = 0.8f))
                                2 -> Pair(Icons.Rounded.BrightnessHigh, Color.White)
                                else -> Pair(Icons.Rounded.BrightnessAuto, Color(0xFF64FFDA))
                            }
                            Icon(brightnessIcon, "Parlaklık", tint = iconTint, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cs.background)
            ) {
                HomeStarDustEffect(color = cs.onBackground.copy(alpha = if (darkMode) 0.2f else 0.1f))

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // HERO CARD
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(resolveHeroGradient(cs, darkMode))
                    ) {
                        HomeStarDustEffect(color = Color.White.copy(alpha = 0.4f))

                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    randomMessage,
                                    fontSize = 22.sp,
                                    color = if (darkMode) cs.onSurface else cs.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))

                                CuteDailyGoalBar(
                                    solved = solvedToday,
                                    target = dailyTarget,
                                    barColor = if (darkMode) cs.onSurface else cs.onPrimary
                                )
                            }

                            Box(modifier = Modifier.size(100.dp)) {
                                HomeRobotView(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }

                    Text(
                        "Dersler",
                        modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 12.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        LessonCard("Türkçe", "Dil bilgisi ve Anlam", Icons.Rounded.Create) { navController.navigate("turkce") }
                        LessonCard("Matematik", "Zor seviye testler", Icons.Rounded.Add) { navController.navigate("math") }
                        LessonCard("Sosyal Bilgiler", "Tarih + Coğrafya", Icons.Rounded.Place) { navController.navigate("sosyal") }
                        LessonCard("Fen Bilimleri", "Kritik konu tarama", Icons.Rounded.Star) { navController.navigate("fen") }
                        LessonCard("İngilizce", "Kelime + Grammar", Icons.Rounded.Info) { navController.navigate("ingilizce") }
                        LessonCard("Arapça", "Temel Arapça", Icons.Rounded.Star) { navController.navigate("arapca") }
                        LessonCard("Din Kültürü", "İnanç ve Ahlak", Icons.Rounded.Face) { navController.navigate("din_kulturu") }

                        // SADECE: Paragraf + Deneme Sınavları yan yana (Sıkışıklık giderildi)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MiniHalfCard(
                                title = "Paragraf",
                                subtitle = "Hergün\n20",
                                icon = Icons.Rounded.MenuBook,
                                iconBg = cs.primary.copy(alpha = 0.12f),
                                iconTint = cs.primary,
                                modifier = Modifier.weight(1f)
                            ) { navController.navigate("paragraph_practice_screen") }

                            MiniHalfCard(
                                title = "Deneme\nSınavları",
                                subtitle = "Genel\ndeneme ve",
                                icon = Icons.Rounded.Assignment,
                                iconBg = Color(0xFFFFF176).copy(alpha = 0.30f),
                                iconTint = Color(0xFFFBC02D),
                                modifier = Modifier.weight(1f)
                            ) { navController.navigate("practice_exam_screen") }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { navController.navigate("class_duel") },
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🥊", fontSize = 28.sp)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SINIF DÜELLOSU", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("Arkadaşınla Bluetooth yarış!", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                }
                                Icon(Icons.Rounded.ArrowForward, null, tint = Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
}

/**
 * ANIMATED BRAND WORDMARK (MODERN SYNCED ENERGY VERSION)
 */
@Composable
private fun AnimatedBrandWordmark(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    compact: Boolean
) {
    val sloganText = "Cebindeki Öğretmen."
    val titleSize = if (compact) 30.sp else 38.sp
    val aiSize = if (compact) 20.sp else 26.sp
    val subtitleSize = if (compact) 14.sp else 16.sp

    val shimmerTransition = rememberInfiniteTransition(label = "shimmer_sync")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                0f at 0 using LinearEasing
                1f at 2800 using LinearEasing
                1f at 3500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val gradientWidth = 500f
    val startX = (shimmerOffset * (gradientWidth * 2)) - gradientWidth

    val whiteBase = Color.White.copy(alpha = 0.7f)
    val whiteShine = Color.White
    val whiteShimmerBrush = Brush.linearGradient(
        colors = listOf(whiteBase, whiteShine, whiteBase),
        start = Offset(startX, 0f),
        end = Offset(startX + gradientWidth, 0f),
        tileMode = TileMode.Clamp
    )

    val vibrantEnergyColors = listOf(
        Color(0xFF00E5FF),
        Color(0xFFD500F9),
        Color(0xFFFFD600),
        Color(0xFF00E5FF)
    )
    val aiEnergyBrush = Brush.linearGradient(
        colors = vibrantEnergyColors,
        start = Offset(startX, 0f),
        end = Offset(startX + gradientWidth, 0f),
        tileMode = TileMode.Clamp
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Akıl Küpü",
                fontSize = titleSize,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                style = TextStyle(brush = whiteShimmerBrush),
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.width(3.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    fontSize = aiSize,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(brush = aiEnergyBrush),
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = sloganText,
            fontSize = subtitleSize,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            style = TextStyle(brush = whiteShimmerBrush),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

@Composable
fun DrawerSectionTitle(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun ColorfulDrawerItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = false,
        icon = { Icon(icon, null, tint = color) },
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = color,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun CuteDailyGoalBar(solved: Int, target: Int, barColor: Color) {
    val safeSolved = solved.coerceAtLeast(0)
    val safeTarget = target.coerceAtLeast(1)
    val progress = (safeSolved.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
    val percent = (progress * 100f).toInt().coerceIn(0, 100)

    val infiniteTransition = rememberInfiniteTransition(label = "hamster_run")
    val legAnim by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse),
        label = "legs"
    )
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -3f,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing), RepeatMode.Reverse),
        label = "bounce"
    )

    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Bugün $target soru", fontSize = 12.sp, color = barColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("%$percent", fontSize = 12.sp, color = barColor, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth().height(12.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(color = barColor.copy(alpha = 0.3f), cornerRadius = CornerRadius(100f))
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val filledWidth = maxWidth * progress
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = barColor,
                        size = Size(filledWidth.toPx(), size.height),
                        cornerRadius = CornerRadius(100f)
                    )
                }
                val rabbitSize = 34.dp
                val offsetX = filledWidth - (rabbitSize / 2)
                Canvas(
                    modifier = Modifier
                        .offset(x = offsetX, y = -rabbitSize + 8.dp + bounceY.dp)
                        .size(rabbitSize)
                ) {
                    val colorOrange = Color(0xFFFF9800)
                    val colorBelly = Color(0xFFFFF3E0)
                    drawOval(colorOrange, topLeft = Offset(0f, size.height * 0.2f), size = Size(size.width, size.height * 0.7f))
                    drawOval(colorBelly, topLeft = Offset(size.width * 0.3f, size.height * 0.4f), size = Size(size.width * 0.5f, size.height * 0.4f))
                    withTransform({ rotate(-10f) }) {
                        drawOval(colorOrange, topLeft = Offset(size.width * 0.6f, -size.height * 0.1f), size = Size(size.width * 0.25f, size.height * 0.5f))
                    }
                    withTransform({ rotate(-30f) }) {
                        drawOval(colorOrange, topLeft = Offset(size.width * 0.3f, -size.height * 0.1f), size = Size(size.width * 0.25f, size.height * 0.5f))
                    }
                    drawCircle(Color.Black, radius = 2.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.4f))
                    withTransform({ rotate(legAnim, center) }) {
                        drawOval(colorOrange, topLeft = Offset(size.width * 0.4f, size.height * 0.8f), size = Size(8.dp.toPx(), 6.dp.toPx()))
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("$safeSolved / $safeTarget tamamlandı", fontSize = 11.sp, color = barColor.copy(alpha = 0.8f))
    }
}

@Composable
private fun LessonCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth().height(88.dp).clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(20.dp), color = cs.primary.copy(alpha = 0.12f)) {
                Icon(icon, null, modifier = Modifier.padding(14.dp).size(28.dp), tint = cs.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Text(subtitle, fontSize = 13.sp, color = cs.onSurface.copy(alpha = 0.65f))
            }
            Icon(Icons.Rounded.ArrowForward, null, tint = cs.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun MiniHalfCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cs.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = cs.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Rounded.ArrowForward, null, tint = cs.onSurface.copy(alpha = 0.35f))
        }
    }
}

@Composable
private fun HomeStarDustEffect(color: Color = Color.White) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_movement")
    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "moveY"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = Random(123)
        repeat(30) {
            val startX = r.nextFloat() * size.width
            val startY = r.nextFloat() * size.height
            val radius = r.nextFloat() * 2.5.dp.toPx() + 1.dp.toPx()
            val speedFactor = (it % 3) + 1
            val currentY = (startY + moveY * speedFactor) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY
            drawCircle(color = color, radius = radius, center = Offset(startX, drawY), alpha = alpha * r.nextFloat())
        }
    }
}

@Composable
private fun HomeRobotView(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_anim")
    val hoverY by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hover"
    )
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    Canvas(modifier = modifier.graphicsLayer { translationY = hoverY; scaleX = breatheScale; scaleY = breatheScale }) {
        val cx = size.width / 2
        val cy = size.height / 2
        val robotColor = Color.White
        val faceColor = Color(0xFF1A237E)
        drawLine(Color(0xFFB0BEC5), Offset(cx, cy - 40.dp.toPx()), Offset(cx, cy - 50.dp.toPx()), 4.dp.toPx())
        drawCircle(Color(0xFF90CAF9), 6.dp.toPx(), Offset(cx, cy - 54.dp.toPx()))
        drawRoundRect(robotColor, topLeft = Offset(cx - 35.dp.toPx(), cy - 40.dp.toPx()), size = Size(70.dp.toPx(), 60.dp.toPx()), cornerRadius = CornerRadius(20.dp.toPx()))
        drawRoundRect(faceColor, topLeft = Offset(cx - 28.dp.toPx(), cy - 32.dp.toPx()), size = Size(56.dp.toPx(), 36.dp.toPx()), cornerRadius = CornerRadius(15.dp.toPx()))
        drawOval(Color(0xFF00E5FF), topLeft = Offset(cx - 18.dp.toPx(), cy - 25.dp.toPx()), size = Size(12.dp.toPx(), 16.dp.toPx()))
        drawOval(Color(0xFF00E5FF), topLeft = Offset(cx + 6.dp.toPx(), cy - 25.dp.toPx()), size = Size(12.dp.toPx(), 16.dp.toPx()))
        drawPath(
            path = Path().apply {
                moveTo(cx - 20.dp.toPx(), cy + 22.dp.toPx())
                cubicTo(cx - 20.dp.toPx(), cy + 22.dp.toPx(), cx, cy + 50.dp.toPx(), cx + 20.dp.toPx(), cy + 22.dp.toPx())
                close()
            },
            color = robotColor
        )
    }
}

@Composable
private fun HomeRobotHead(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        drawRoundRect(color = Color.White, topLeft = Offset(cx - 20.dp.toPx(), cy - 15.dp.toPx()), size = Size(40.dp.toPx(), 30.dp.toPx()), cornerRadius = CornerRadius(10.dp.toPx()))
        drawRoundRect(color = Color(0xFF1A237E), topLeft = Offset(cx - 16.dp.toPx(), cy - 12.dp.toPx()), size = Size(32.dp.toPx(), 20.dp.toPx()), cornerRadius = CornerRadius(8.dp.toPx()))
        drawCircle(Color(0xFF00E5FF), 3.dp.toPx(), Offset(cx - 8.dp.toPx(), cy - 2.dp.toPx()))
        drawCircle(Color(0xFF00E5FF), 3.dp.toPx(), Offset(cx + 8.dp.toPx(), cy - 2.dp.toPx()))
        drawLine(Color(0xFFB0BEC5), Offset(cx, cy - 15.dp.toPx()), Offset(cx, cy - 22.dp.toPx()), 2.dp.toPx())
        drawCircle(Color(0xFF90CAF9), 3.dp.toPx(), Offset(cx, cy - 24.dp.toPx()))
    }
}

private fun resolveTopGradient(cs: androidx.compose.material3.ColorScheme, darkMode: Boolean): Brush {
    return if (darkMode) {
        Brush.verticalGradient(colors = listOf(Color(0xFF0B1220), Color(0xFF0F172A), cs.primary.copy(alpha = 0.35f)))
    } else {
        Brush.verticalGradient(colors = listOf(cs.primary, cs.tertiary))
    }
}

private fun resolveHeroGradient(cs: androidx.compose.material3.ColorScheme, darkMode: Boolean): Brush {
    return if (darkMode) {
        Brush.linearGradient(colors = listOf(cs.surfaceVariant, cs.surface, cs.primary.copy(alpha = 0.28f)))
    } else {
        Brush.linearGradient(listOf(cs.primary, cs.tertiary, cs.secondary))
    }
}

private fun openBugReportEmail(context: Context, appVersionName: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("destek@bilgideham.app"))
        putExtra(Intent.EXTRA_SUBJECT, "Hata Bildirimi (v$appVersionName)")
    }
    runCatching { context.startActivity(Intent.createChooser(intent, "Hata Bildir")) }
}
