package com.example.bilgideham

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Pending
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KpssDenemListScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = remember { CloudUserId.getOrCreate(context) }
    val cs = MaterialTheme.colorScheme
    
    // State
    var paketler by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var duruMap by remember { mutableStateOf<Map<Int, QuestionRepository.DenemeDurumu>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Veri Yükleme
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            val allPaketler = QuestionRepository.getKpssDenemePaketleri()
            val durumlar = QuestionRepository.getAllDenemeDurumlari(userId)
            
            duruMap = durumlar.associateBy { it.paketNo }
            paketler = allPaketler
            isLoading = false
            isRefreshing = false
        }
    }

    // İlk yükleme
    LaunchedEffect(Unit) {
        val allPaketler = QuestionRepository.getKpssDenemePaketleri()
        val durumlar = QuestionRepository.getAllDenemeDurumlari(userId)
        
        duruMap = durumlar.associateBy { it.paketNo }
        paketler = allPaketler
        isLoading = false
    }

    // Ekrana geri dönüldüğünde yenile
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isRefreshing = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    // İstatistikler
    val totalSolved = duruMap.values.count { it.durum == "tamamlandi" }
    val avgNet = if (totalSolved > 0) {
        duruMap.values.filter { it.durum == "tamamlandi" }
            .map { it.dogru - (it.yanlis / 4.0) }
            .average()
    } else 0.0
    
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            cs.primary.copy(alpha = 0.1f),
            cs.background,
            cs.background
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("KPSS Denemeleri", fontWeight = FontWeight.Bold) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = cs.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Geri", tint = cs.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { isRefreshing = true }) {
                        Icon(Icons.Default.PlayArrow /*Refresh ico yok*/, "Yenile", modifier = Modifier.rotate(270f))
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- HEADER DASHBOARD (Premium Tasarım) ---
                    item {
                        DashboardCard(totalSolved, paketler.size, avgNet)
                    }
                    
                    item {
                        Text(
                            "Mevcut Denemeler",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onBackground,
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                        )
                    }
                    
                    // --- DENEME LİSTESİ ---
                    if (paketler.isEmpty()) {
                        item {
                            EmptyStateCard()
                        }
                    } else {
                        items(paketler) { paket ->
                            val paketNo = (paket["paketNo"] as? Long)?.toInt() ?: 0
                            val soruSayisi = (paket["toplamSoru"] as? Long)?.toInt() ?: 120
                            val seviye = paket["seviye"] as? String ?: "KPSS"
                            val durum = duruMap[paketNo]
                            
                            DenemeCard(
                                paketNo = paketNo,
                                soruSayisi = soruSayisi,
                                seviye = seviye,
                                durum = durum,
                                onClick = {
                                    if (durum?.durum == "tamamlandi") {
                                        navController.navigate("kpss_deneme_sonuc/$paketNo")
                                    } else if (durum?.durum == "devam_ediyor") {
                                        val startQ = durum.sonKalinanSoru.coerceAtLeast(1)
                                        navController.navigate("kpss_deneme_coz/$paketNo/$startQ")
                                    } else {
                                        navController.navigate("kpss_deneme_coz/$paketNo/1")
                                    }
                                }
                            )
                        }
                    }
                    
                    item {
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(solved: Int, total: Int, avgNet: Double) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = cs.primary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arka plan deseni
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = size.width * 0.4f,
                    center = center.copy(x = size.width)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.3f,
                    center = center.copy(x = 0f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // İlerleme
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { if(total > 0) solved.toFloat() / total else 0f },
                            modifier = Modifier.size(70.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeWidth = 6.dp
                        )
                        Text(
                            "$solved/$total",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Tamamlanan", color = Color.White.copy(0.8f), fontSize = 12.sp)
                }
                
                // Dikey Çizgi
                Box(Modifier.width(1.dp).height(60.dp).background(Color.White.copy(0.2f)))
                
                // Net Ortalaması
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "%.1f".format(avgNet),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                        Text(
                            " NET",
                            color = Color.White.copy(0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        "Ortalama Başarı",
                        color = Color.White.copy(0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DenemeCard(
    paketNo: Int,
    soruSayisi: Int,
    seviye: String,
    durum: QuestionRepository.DenemeDurumu?,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val isCompleted = durum?.durum == "tamamlandi"
    val isOngoing = durum?.durum == "devam_ediyor"
    
    // Duruma göre renkler
    val cardBg = if (isCompleted) cs.surfaceVariant.copy(0.5f) else cs.surface
    val accentColor = when {
        isCompleted -> Color(0xFF43A047) // Yeşil
        isOngoing -> Color(0xFFFB8C00)   // Turuncu
        else -> cs.primary
    }
    
    val scale by animateFloatAsState(if (isOngoing) 1.02f else 1f, label = "scale")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if(isOngoing) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accentColor.copy(0.2f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Numara Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accentColor,
                                accentColor.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
                } else {
                    Text(
                        "$paketNo",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            // Orta: Bilgiler
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$paketNo. Deneme",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if(isCompleted) cs.onSurface.copy(0.6f) else cs.onSurface
                    )
                    
                    if (isOngoing) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = accentColor.copy(0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "DEVAM EDİYOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DetailChip(text = seviye.replace("KPSS_",""), icon = Icons.Rounded.Code)
                    Spacer(Modifier.width(8.dp))
                    DetailChip(text = "$soruSayisi Soru", icon = Icons.Rounded.Pending)
                }
                
                // İlerleme (Varsa)
                if (durum != null && !isCompleted) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { durum.sonKalinanSoru.toFloat() / soruSayisi },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = accentColor,
                        trackColor = accentColor.copy(0.1f)
                    )
                    Text(
                        "${durum.sonKalinanSoru} / $soruSayisi",
                        fontSize = 11.sp,
                        color = cs.onSurface.copy(0.5f),
                        modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                    )
                }
                
                // Net (Tamamlandıysa)
                if (isCompleted) {
                    Spacer(Modifier.height(6.dp))
                    val net = durum.dogru - (durum.yanlis / 4.0)
                    Text(
                        "Sonuç: %.2f Net".format(net),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }
            }
            
            Spacer(Modifier.width(8.dp))
            
            // Sağ: Aksiyon
            if (!isCompleted) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isOngoing) Icons.Filled.PlayArrow else Icons.Default.PlayArrow, // Aynı ikon ama dolu olabilir
                            null,
                            tint = accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailChip(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        Spacer(Modifier.width(2.dp))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
    }
}

@Composable
fun EmptyStateCard() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.Inventory2,
            null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Henüz Deneme Yok",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Admin panelinden yeni bir 120 soruluk deneme oluşturun.",
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
        )
    }
}

// Rotate modifiers helper
fun Modifier.rotate(degrees: Float) = this.then(
    Modifier.graphicsLayer(rotationZ = degrees)
)
