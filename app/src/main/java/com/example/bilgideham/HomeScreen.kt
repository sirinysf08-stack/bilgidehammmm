package com.example.bilgideham

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Segment
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.bilgideham.ui.theme.InterfaceParams
import com.example.bilgideham.ui.theme.LocalInterfaceStyle
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer

/**
 * HOME SCREEN - Stabilize edilmiş Drawer Navigasyon + Pure Helper Layer
 * - Drawer tıklamalarında "ilk açılışta çalışmıyor" senaryosu için:
 *   1) Drawer kapanışı
 *   2) Main thread deferred navigate (graph hazır olana kadar kontrollü retry)
 * - resolveTopGradient: PURE helper (Composable API çağırmaz)
 */

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

    // Drawer açık mı - rememberSaveable ile sakla
    var isDrawerOpen by rememberSaveable { mutableStateOf(false) }
    
    // Current route'u takip et
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    
    // Home screen'den ayrıldığımızda drawer'ı geçici olarak kapat
    LaunchedEffect(currentRoute) {
        if (currentRoute != null && currentRoute != "home") {
            // Başka bir sayfadayız, drawer'ı kapat
            if (drawerState.isOpen) {
                drawerState.close()
            }
        } else if (currentRoute == "home" && isDrawerOpen) {
            // Home screen'e döndük ve drawer açıktı, tekrar aç
            if (!drawerState.isOpen) {
                drawerState.open()
            }
        }
    }
    
    // Drawer state ile senkronize et
    LaunchedEffect(isDrawerOpen) {
        if (isDrawerOpen && !drawerState.isOpen && currentRoute == "home") {
            drawerState.open()
        } else if (!isDrawerOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }
    
    // Drawer state değişikliklerini takip et
    LaunchedEffect(drawerState.currentValue) {
        isDrawerOpen = drawerState.isOpen
    }

    // Veri ve Marka Yönetimi (SharedPreferences & Stats)
    val statsManager = remember { StatsManager(context) }
    var solvedToday by remember { mutableIntStateOf(0) }
    var brandTitle by remember { mutableStateOf(readBrandTitle(context)) }

    // Kademeli Hedef Mantığı (30 -> 50 -> 100)
    val dailyTarget = when {
        solvedToday >= 50 -> 100
        solvedToday >= 30 -> 50
        else -> 30
    }

    // GİZLİ ADMİN GİRİŞİ - AI badge'ine 5 kez tıkla
    var secretTapCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(secretTapCount) {
        if (secretTapCount >= 5) {
            navController.navigate("admin_panel")
            secretTapCount = 0
        } else if (secretTapCount > 0) {
            kotlinx.coroutines.delay(3000)
            secretTapCount = 0
        }
    }

    // Vurucu Motivasyon Kelimeleri
    val punchyWords = remember {
        listOf("HADİ!", "BAŞAR!", "ZİRVEYE!", "ODAKLAN!", "ŞİMDİ!", "YÜRÜ!", "IŞILDA!", "KAZAN!")
    }
    val randomPunch = remember { punchyWords.random() }

    // Motivasyon Cümleleri
    val motivationMessages = remember {
        listOf(
            "Zirveye Adım Adım! 🚀",
            "Bilgi En Büyük Güçtür! 🧠",
            "Pes Etmek Yok. 💪",
        )
    }
    val randomMessage = remember { motivationMessages.random() }

    fun refreshHomeData() {
        val (correct, wrong) = statsManager.getTodayTotals()
        solvedToday = (correct + wrong).coerceAtLeast(0)
        brandTitle = readBrandTitle(context)
    }

    LaunchedEffect(Unit) { refreshHomeData() }

    // OnResume veri tazeleme
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshHomeData()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // State for Parental Control
    var showParentalLogin by remember { mutableStateOf(false) }
    
    // Rating Popup State
    var showRatingPopup by remember { mutableStateOf(false) }
    
    // Rating popup gösterilmeli mi kontrol et
    LaunchedEffect(Unit) {
        if (AppPrefs.shouldShowRatingPopup(context)) {
            kotlinx.coroutines.delay(60000) // 1 dakika bekle (Kullanıcı biraz zaman geçirsin)
            showRatingPopup = true
        }
    }

    if (showParentalLogin) {
        ParentalLoginDialog(
            onDismiss = { showParentalLogin = false },
            onLoginSuccess = {
                showParentalLogin = false
                navController.navigate("parental_control")
            }
        )
    }
    
    // Rating Popup Dialog
    if (showRatingPopup) {
        RatingPopupDialog(
            onDismiss = { 
                showRatingPopup = false
                AppPrefs.markRatingShown(context)
            },
            onRate = {
                showRatingPopup = false
                AppPrefs.markRatingShown(context)
                // Google Play sayfasına yönlendir
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                }
                context.startActivity(intent)
            }
        )
    }

    fun drawerNavigate(route: String) {
        scope.launch {
            // Drawer'ı kapatmadan navigasyon yap - state korunur
            if (route == "internal://parental_login") {
                if (ParentalPrefs.hasPin(context)) {
                    showParentalLogin = true
                } else {
                    navController.safeNavigateDeferred("parental_control", context)
                }
            } else {
                navController.safeNavigateDeferred(route, context)
            }
        }
    }

    fun drawerAction(action: () -> Unit) {
        scope.launch {
            // Drawer'ı kapatmadan action'ı çalıştır
            Handler(Looper.getMainLooper()).post { action() }
        }
    }

    // Kullanıcının eğitim seviyesi
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val userLevel = educationPrefs.level
    val userGrade = educationPrefs.grade

    // Arayüz stili
    val interfaceStyle = LocalInterfaceStyle.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            // Arayüz stiline göre farklı drawer
            when (interfaceStyle) {
                com.example.bilgideham.ui.theme.InterfaceStyle.MODERN -> ModernDrawerContent(
                    cs = cs, darkMode = darkMode, brandTitle = brandTitle,
                    userLevel = userLevel, userGrade = userGrade, educationPrefs = educationPrefs,
                    context = context, scope = scope, drawerState = drawerState,
                    onNavigate = { drawerNavigate(it) }, onAction = { drawerAction(it) }
                )
                com.example.bilgideham.ui.theme.InterfaceStyle.PLAYFUL -> PlayfulDrawerContent(
                    cs = cs, darkMode = darkMode, brandTitle = brandTitle,
                    userLevel = userLevel, userGrade = userGrade, educationPrefs = educationPrefs,
                    context = context, scope = scope, drawerState = drawerState,
                    onNavigate = { drawerNavigate(it) }, onAction = { drawerAction(it) }
                )
                com.example.bilgideham.ui.theme.InterfaceStyle.CLASSIC -> ClassicDrawerContent(
                    cs = cs, darkMode = darkMode, brandTitle = brandTitle,
                    userLevel = userLevel, userGrade = userGrade, educationPrefs = educationPrefs,
                    context = context, scope = scope, drawerState = drawerState,
                    onNavigate = { drawerNavigate(it) }, onAction = { drawerAction(it) }
                )
                com.example.bilgideham.ui.theme.InterfaceStyle.NEURAL_LUX -> NeuralLuxDrawerContent(
                    cs = cs, darkMode = darkMode, brandTitle = brandTitle,
                    userLevel = userLevel, userGrade = userGrade, educationPrefs = educationPrefs,
                    context = context, scope = scope, drawerState = drawerState,
                    onNavigate = { drawerNavigate(it) }, onAction = { drawerAction(it) }
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                // Arayüz stiline göre farklı header
                val interfaceStyle = LocalInterfaceStyle.current
                when (interfaceStyle) {
                    com.example.bilgideham.ui.theme.InterfaceStyle.MODERN -> ModernHeader(brandTitle, darkMode, { scope.launch { runCatching { drawerState.open() } } }, onToggleTheme, onToggleBrightness, currentBrightness, onSecretTap = { secretTapCount++ })
                    com.example.bilgideham.ui.theme.InterfaceStyle.PLAYFUL -> PlayfulHeader(brandTitle, darkMode, { scope.launch { runCatching { drawerState.open() } } }, onToggleTheme, onToggleBrightness, currentBrightness, onSecretTap = { secretTapCount++ })
                    com.example.bilgideham.ui.theme.InterfaceStyle.CLASSIC -> ClassicHeader(brandTitle, darkMode, { scope.launch { runCatching { drawerState.open() } } }, onToggleTheme, onToggleBrightness, currentBrightness, onSecretTap = { secretTapCount++ })
                    com.example.bilgideham.ui.theme.InterfaceStyle.NEURAL_LUX -> NeuralLuxHeader(brandTitle, darkMode, { scope.launch { runCatching { drawerState.open() } } }, onToggleTheme, onToggleBrightness, currentBrightness, onSecretTap = { secretTapCount++ })
                }
            }
        ) { padding ->
            val interfaceStyle = LocalInterfaceStyle.current
            when (interfaceStyle) {
                com.example.bilgideham.ui.theme.InterfaceStyle.MODERN -> ModernHomeContent(padding, navController, context, darkMode, solvedToday, dailyTarget, randomMessage, randomPunch)
                com.example.bilgideham.ui.theme.InterfaceStyle.PLAYFUL -> PlayfulHomeContent(padding, navController, context, darkMode, solvedToday, dailyTarget, randomMessage, randomPunch)
                com.example.bilgideham.ui.theme.InterfaceStyle.CLASSIC -> ClassicHomeContent(padding, navController, context, darkMode, solvedToday, dailyTarget, randomMessage, randomPunch)
                com.example.bilgideham.ui.theme.InterfaceStyle.NEURAL_LUX -> NeuralLuxHomeContent(padding, navController, context, darkMode, solvedToday, dailyTarget, randomMessage, randomPunch)
            }
        }
    }
}

// Tema bileşenleri ayri dosyalara taşındı:
// - ModernThemeHome.kt
// - PlayfulThemeHome.kt
// - ClassicThemeHome.kt
// - HomeDrawerMenuItems.kt





// -------------------- DİNAMİK DERS KARTLARI --------------------

@Composable
fun EducationLevelHeader(
    title: String,
    level: EducationLevel,
    onChangeLevel: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val levelColor = Color(level.colorHex)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "EĞİTİM MODÜLLERİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = cs.onBackground.copy(0.5f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = level.icon, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = levelColor)
            }
        }
        Surface(
            onClick = onChangeLevel,
            shape = RoundedCornerShape(12.dp),
            color = levelColor.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.SwapHoriz, contentDescription = "Seviye Değiştir", tint = levelColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = "Değiştir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = levelColor)
            }
        }
    }
}

@Composable
fun DynamicLessonCard(
    subject: SubjectConfig,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val subjectColor = Color(subject.colorHex)
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp
    // İkon eşleştirme
    val icon = getIconForSubject(subject.id)
    
    // Aktiflik durumu
    val isActive = subject.isActive

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .let { m ->
                if (isActive) m.clickable { onClick() } else m
            },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) cs.surface else cs.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) elevation else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .alpha(if (isActive) 1f else 0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(cornerRadius * 0.8f))
                    .background(subjectColor.copy(alpha = if (isActive) 0.16f else 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                // Emoji veya Icon
                if (subject.icon.length <= 2) {
                    Text(subject.icon, fontSize = 28.sp)
                } else {
                    Icon(icon, null, tint = subjectColor, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.width(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                val fontSize = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 16.sp else 21.sp
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subject.displayName,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    
                    if (!isActive) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "YAKINDA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
                
                Text(
                    text = if (isActive) subject.description else "Soru havuzu hazırlanıyor...",
                    fontSize = 13.sp,
                    color = cs.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (isActive) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = cs.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Kilitli",
                    tint = cs.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun getIconForSubject(subjectId: String): ImageVector {
    val id = subjectId.lowercase()
    return when {
        id.contains("turkce") || id.contains("turk_dili") -> Icons.Rounded.AutoStories
        id.contains("matematik") || id.contains("math") -> Icons.Rounded.Functions
        id.contains("fen") || id.contains("fizik") || id.contains("kimya") || id.contains("biyoloji") -> Icons.Rounded.Science
        id.contains("sosyal") || id.contains("tarih") || id.contains("cografya") -> Icons.Rounded.Public
        id.contains("ingilizce") || id.contains("english") -> Icons.Rounded.Language
        id.contains("arapca") || id.contains("kuran") -> Icons.Rounded.Translate
        id.contains("din") || id.contains("siyer") || id.contains("hadis") || id.contains("fikih") -> Icons.Rounded.HistoryEdu
        id.contains("felsefe") || id.contains("mantik") -> Icons.Rounded.Psychology
        id.contains("sosyoloji") || id.contains("psikoloji") -> Icons.Rounded.Groups
        id.contains("paragraf") -> Icons.AutoMirrored.Rounded.MenuBook
        id.contains("deneme") || id.contains("tyt") || id.contains("ayt") || id.contains("lgs") || id.contains("kpss") -> Icons.AutoMirrored.Rounded.Assignment
        id.contains("hayat") -> Icons.Rounded.Explore
        id.contains("meslek") || id.contains("atolye") -> Icons.Rounded.Build
        id.contains("egitim") || id.contains("rehberlik") -> Icons.Rounded.School
        id.contains("vatandaslik") || id.contains("guncel") -> Icons.Rounded.Newspaper
        else -> Icons.Rounded.Book
    }
}


// Geçmiş Sınav Kartı
@Composable
fun PastExamCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            cs.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji container
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurface.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- BETA DRAWER ITEMS (CUSTOM BADGE) ---

@Composable
fun BetaDrawerItemPlayful(emoji: String, title: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                ModernBetaBadge()
            }
        }
    }
}

@Composable
fun BetaDrawerItemClassic(title: String, icon: ImageVector, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = cs.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 14.sp, color = cs.onSurface)
            Spacer(Modifier.width(8.dp))
            ModernBetaBadge()
        }
    }
}

@Composable
fun BetaDrawerItemColorful(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
                Spacer(Modifier.width(8.dp))
                ModernBetaBadge()
            }
        }
    }
}

@Composable
fun ModernBetaBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "betaBadge")
    
    // Pulsing glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    // Gradient shift
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient"
    )
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF4444),
                        Color(0xFFFF6B6B),
                        Color(0xFFFF4444)
                    ),
                    startX = gradientShift - 50f,
                    endX = gradientShift + 50f
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF6B6B).copy(alpha = glowAlpha),
                        Color(0xFFFFFFFF).copy(alpha = glowAlpha * 0.5f),
                        Color(0xFFFF6B6B).copy(alpha = glowAlpha)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "BETA",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(0f, 1f),
                    blurRadius = 2f
                )
            )
        )
    }
}

@Composable
private fun BetaBadge(color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = "BETA",
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun RatingPopupDialog(
    onDismiss: () -> Unit,
    onRate: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("⭐", fontSize = 48.sp)
        },
        title = {
            Text(
                "Uygulamayı Beğendiniz mi?",
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Text(
                "Görüşleriniz bizim için çok değerli! Play Store'da bizi değerlendirerek diğer öğrencilere yardımcı olabilirsiniz.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onRate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Rounded.Star, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Değerlendir", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Daha Sonra", color = cs.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}
