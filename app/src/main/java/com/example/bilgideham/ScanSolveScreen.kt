package com.example.bilgideham

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow // [NEW]
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanSolveScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val darkMode = remember { AppPrefs.getDarkMode(context) }

    // Kullanıcı seviyesini al - remember ile sarmalayalım ki değişmez olsun
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val userLevel = remember { prefs.getString("education_level", "ORTAOKUL") ?: "ORTAOKUL" }
    val userGrade = remember { prefs.getInt("grade", 5) }
    
    // Seviye açıklaması
    val levelDescription = remember(userLevel, userGrade) {
        when (userLevel) {
            "ILKOKUL" -> "4. sınıf"
            "ORTAOKUL" -> "${userGrade}. sınıf"
            "LISE" -> "${userGrade}. sınıf lise"
            "KPSS" -> "KPSS adayı"
            "AGS" -> "Üniversite öğrencisi"
            else -> "5. sınıf"
        }
    }

    var showCamera by remember { mutableStateOf(false) }
    var ocrLoading by remember { mutableStateOf(false) }
    var aiLoading by remember { mutableStateOf(false) }

    var scannedText by remember { mutableStateOf("") }
    var solvedText by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportContentDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { _, _ -> showReportDialog = false }
        )
    }

    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    var camGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        camGranted = granted
        if (!granted) {
            scope.launch { snackbarHostState.showSnackbar("Kamera izni verilmedi.") }
            showCamera = false
        } else {
            showCamera = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            ocrLoading = true
            aiLoading = false
            solvedText = ""
            scannedText = ""
            try {
                val img = InputImage.fromFilePath(context, uri)
                val result = recognizer.process(img).await()
                scannedText = result.text.trim()
                autoSolveFromOcr(snackbarHostState, scope, scannedText, userLevel, userGrade, levelDescription) { l, s -> aiLoading = l; solvedText = s }
            } catch (_: Exception) {
                snackbarHostState.showSnackbar("Görüntü işlenemedi.")
            } finally {
                ocrLoading = false
            }
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    fun captureAndSolve() {
        val cap = imageCapture ?: return
        scope.launch {
            ocrLoading = true; aiLoading = false; solvedText = ""; scannedText = ""
            try {
                val input = takePictureAsInputImage(context, cap)
                val result = recognizer.process(input).await()
                scannedText = result.text.trim()
                showCamera = false
                autoSolveFromOcr(snackbarHostState, scope, scannedText, userLevel, userGrade, levelDescription) { l, s -> aiLoading = l; solvedText = s }
            } catch (_: Exception) {
                snackbarHostState.showSnackbar("Çekim hatası.")
            } finally {
                ocrLoading = false
            }
        }
    }

    Scaffold(
        containerColor = if (darkMode) Color(0xFF0F172A) else Color(0xFFF0F4F8), // Header köşeleri için arka plan
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            // ... (Header kodu aynı kalacak, burası değişmiyor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            if (darkMode)
                                listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
                            else
                                listOf(Color(0xFF42A5F5), Color(0xFF2196F3))
                        )
                    )
            ) {
                // Dekoratif Arka Plan Efektleri
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                ) {
                    // Sağ taraftaki büyük silik ikon
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 20.dp, y = 10.dp)
                            .size(140.dp)
                            .rotate(-15f)
                    )
                    
                    // Rastgele noktalar
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        repeat(5) {
                            val radiusVal = (10..30).random().toFloat()
                            val minX = size.width * 0.2f
                            val rangeX = size.width - minX
                            val xVal = minX + kotlin.random.Random.nextFloat() * rangeX
                            val minY = size.height * 0.1f
                            val rangeY = size.height - minY
                            val yVal = minY + kotlin.random.Random.nextFloat() * rangeY

                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = radiusVal,
                                center = androidx.compose.ui.geometry.Offset(xVal, yVal)
                            )
                        }
                    }
                }
                
                // İçerik Alanı
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Geri Butonu
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // 2. Başlık ve Alt Başlık
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Yol Gösterici AI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 30.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Matematik ve Fen Rehberin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(poolBackgroundBrush(darkMode))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. AÇIKLAMA VE SLOGAN KARTI (Mavi Tonlara Çevrildi) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp), // Daha yuvarlak
                    colors = CardDefaults.cardColors(
                        containerColor = if (darkMode) Color(0xFF1E293B) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                    border = borderStroke(darkMode) // Fonksiyonla border
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // İkon kutusu
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎣", fontSize = 20.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Balık Tutmayı Öğreten AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (darkMode) Color.White else Color(0xFF1565C0)
                            )
                        }
                        Spacer(Modifier.height(16.dp))

                        // Örnek Kutusu (Modernize)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (darkMode) Color(0xFF0F172A) else Color(0xFFF5F9FF))
                                .border(1.dp, Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                             Column {
                                // Yanlış Yaklaşım
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("❌", fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Diğer AI'lar:",
                                            fontSize = 12.sp, 
                                            fontWeight = FontWeight.Bold,
                                            color = if (darkMode) Color(0xFFEF5350) else Color(0xFFD32F2F)
                                        )
                                        Text(
                                            text = "\"Cevap: 42\"",
                                            fontSize = 13.sp, 
                                            fontStyle = FontStyle.Italic,
                                            color = if (darkMode) Color.Gray else Color(0xFF757575)
                                        )
                                    }
                                }
                                
                                Spacer(Modifier.height(12.dp))
                                
                                // Doğru Yaklaşım
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("✅", fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Akıl Küpü AI:",
                                            fontSize = 12.sp, 
                                            fontWeight = FontWeight.Bold,
                                            color = if (darkMode) Color(0xFF66BB6A) else Color(0xFF2E7D32)
                                        )
                                        Text(
                                            text = "\"Önce parantez içini çöz. Sonra çarpma işlemini yap. Son olarak toplama...\"",
                                            fontSize = 13.sp, 
                                            fontStyle = FontStyle.Italic,
                                            color = if (darkMode) Color(0xFF81C784) else Color(0xFF388E3C),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Seni tembelleştirmez, geliştirir! 💪",
                                fontSize = 13.sp,
                                color = if (darkMode) Color.Gray else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- 2. AKSİYON ALANI (KAMERA / GALERİ) ---
                if (!showCamera) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Kamera Butonu (Gradient)
                        GradientActionButton(
                            text = "Fotoğraf Çek",
                            icon = Icons.Default.CameraAlt,
                            gradient = Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF1976D2))),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (camGranted) showCamera = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                        }

                        // Galeri Butonu (Gradient)
                        GradientActionButton(
                            text = "Galeriden Seç",
                            icon = Icons.Default.PhotoLibrary,
                            // Galeri Butonu (Gradient - Turkuaz/Yeşil)
                            gradient = Brush.linearGradient(listOf(Color(0xFF26C6DA), Color(0xFF00ACC1))),
                            modifier = Modifier.weight(1f)
                        ) {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                } else {
                    // Kamera Önizleme Modu (Mevcut kod korunabilir veya iyileştirilebilir)
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(420.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box {
                            CameraPreview(
                                onReady = { imageCapture = it },
                                modifier = Modifier.fillMaxSize()
                            )
                            // Kapat Butonu
                            IconButton(
                                onClick = { showCamera = false },
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                                    .background(Color.Black.copy(0.4f), CircleShape)
                                    .border(1.dp, Color.White.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White)
                            }
                            // Çek Butonu
                             Button(
                                onClick = { captureAndSolve() },
                                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Camera, null)
                                Spacer(Modifier.width(8.dp))
                                Text("TARA VE İPUCU AL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // ... (Yükleniyor ve Sonuç alanları için mevcut kodu koru veya benzer stili uygula)

            // Yükleniyor Göstergesi (Modern & Minik)
            if (ocrLoading || aiLoading) {
                Spacer(Modifier.height(40.dp))
                 ModernLoadingAnimation(
                    message = if (ocrLoading) "Görüntü İşleniyor..." else "Çözüm Üretiliyor...",
                    subMessage = "Yapay zeka analiz yapıyor",
                    modifier = Modifier.height(200.dp) // Yüksekliği sınırla
                )
            }

            // --- 3. SONUÇ ALANI (ÖĞRETMEN NOTU - MODERN) ---
            if (solvedText.isNotBlank()) {
                Spacer(Modifier.height(80.dp)) // Daha aşağıda olması için boşluk artırıldı

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = if (darkMode) Color(0xFF000000) else Color(0xFFE3F2FD)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                if (darkMode) 
                                    listOf(Color(0xFF263238), Color(0xFF1E293B)) 
                                else 
                                    listOf(Color(0xFFFFF9C4), Color(0xFFFFF176).copy(0.3f))
                            )
                        )
                        .border(
                            1.dp, 
                            if (darkMode) Color.White.copy(0.1f) else Color(0xFFFBC02D).copy(0.3f), 
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (darkMode) Color(0xFF37474F) else Color(0xFFFFF59D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡", fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "ÖĞRETMENİN NOTU",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (darkMode) Color(0xFFFFCA28) else Color(0xFFF57F17),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "Yol Gösterici İpuçları",
                                    fontSize = 11.sp,
                                    color = if (darkMode) Color.White.copy(0.5f) else Color.Black.copy(0.4f)
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            ReportIconButton(onClick = { showReportDialog = true })
                        }

                        Spacer(Modifier.height(16.dp))
                        
                        // İçerik
                        Text(
                            text = solvedText,
                            fontSize = 15.sp,
                            color = if (darkMode) Color(0xFFECEFF1) else Color(0xFF4E342E),
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(20.dp))

                        // Footer Notu
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (darkMode) Color(0xFF37474F) else Color(0xFFFFFDE7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    null,
                                    tint = if (darkMode) Color(0xFF81C784) else Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Çözümü kendin bulman için ipucu verildi!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (darkMode) Color.White.copy(0.7f) else Color(0xFF5D4037).copy(0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Eğer metin manuel düzeltilecekse
            if (!showCamera && scannedText.isNotBlank() && solvedText.isBlank() && !aiLoading) {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = scannedText,
                    onValueChange = { scannedText = it },
                    label = { Text("Okunan Metin (Hata varsa düzelt)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Button(
                    onClick = {
                        autoSolveFromOcr(snackbarHostState, scope, scannedText, userLevel, userGrade, levelDescription) { l, s -> aiLoading = l; solvedText = s }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Tekrar Analiz Et")
                }
            }
            Spacer(Modifier.height(24.dp))
            AiDisclaimerFooter(isDarkMode = darkMode)
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(text, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

// Helper: Arka Plan Gradienti
fun poolBackgroundBrush(isDark: Boolean): Brush {
    return if (isDark) {
        Brush.verticalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E293B)), // Koyu Lacivert -> Biraz daha açık
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFF0F4F8), Color(0xFFE3F2FD)) // Çok açık gri/mavi -> Açık Mavi
        )
    }
}

// Helper: Border
@Composable
fun borderStroke(isDark: Boolean): androidx.compose.foundation.BorderStroke? {
    return if (isDark) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f)) else null
}

@Composable
private fun CameraPreview(onReady: (ImageCapture) -> Unit, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(modifier = modifier, factory = { ctx ->
        val previewView = PreviewView(ctx)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            onReady(imageCapture)
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    })
}

// GÜNCELLENMİŞ PEDAGOJİK YAPAY ZEKA İSTEMİ (BALIK TUTMAYI ÖĞRETEN)
private fun autoSolveFromOcr(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    ocrText: String,
    userLevel: String,
    userGrade: Int,
    levelDescription: String,
    setState: (loading: Boolean, solved: String) -> Unit
) {
    scope.launch {
        setState(true, "")
        try {
            val text = ocrText.trim()
            
            // Seviyeye göre ton ve yaklaşım
            val (teacherRole, approachStyle) = when (userLevel) {
                "ILKOKUL" -> "4. sınıf öğretmeni" to "Çok tatlı, sevecen ve basit bir dil kullan. Kısa cümleler, bol emoji 🌟"
                "ORTAOKUL" -> when (userGrade) {
                    5, 6 -> "${userGrade}. sınıf öğretmeni" to "Arkadaşça ve cesaretlendirici bir dil kullan. Anlaşılır ipuçları ver."
                    7, 8 -> "${userGrade}. sınıf öğretmeni" to "Daha olgun bir dil kullan. Mantıksal düşünmeyi teşvik et."
                    else -> "5. sınıf öğretmeni" to "Arkadaşça ve cesaretlendirici bir dil kullan."
                }
                "LISE" -> "${userGrade}. sınıf lise öğretmeni" to "Akademik ama anlaşılır bir dil kullan. Derin kavramsal bağlantılar kur."
                "KPSS" -> "KPSS koçu" to "Profesyonel ve stratejik bir yaklaşım kullan. Sınav tekniklerini de öğret."
                "AGS" -> "Akademik danışman" to "Üniversite seviyesinde akademik bir yaklaşım kullan. Kapsamlı analizler sun."
                else -> "5. sınıf öğretmeni" to "Arkadaşça ve cesaretlendirici bir dil kullan."
            }
            
            val promptText = """
Sen $teacherRole'sin. Görevin bu soruyu ÇÖZMEK DEĞİL, öğrenciye BALIK TUTMAYI ÖĞRETMEK.
Aşağıdaki soruyla ilgili öğrenciye koçluk yap:
"$text"

SEVİYE: $levelDescription
YAKLAŞIM: $approachStyle

KURALLAR:
1. Asla şıkkı (A, B, C...) veya cevabı (5, 10, X...) söyleme.
2. Adım adım düşünmesini sağla.
3. Örnekteki gibi konuş: "Önce parantez içini yapmalısın, sonra çarpma işlemini dene. İpucu: 5x4 kaç eder?"
4. Eğer soru metni anlaşılmazsa nazikçe tekrar çekmesini söyle.
5. Seviyeye uygun bir dil ve ton kullan.
""".trimIndent()

            val ans = solveQuestionText(promptText) // AiCompat.kt içindeki fonksiyonu kullanır
            setState(false, ans.ifBlank { "Metni tam okuyamadım, tekrar dener misin? 🤔" })
        } catch (_: Exception) {
            setState(false, "Bağlantı hatası oluştu. İnternetini kontrol et.")
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private suspend fun takePictureAsInputImage(context: Context, capture: ImageCapture): InputImage = suspendCancellableCoroutine { cont ->
    val executor = ContextCompat.getMainExecutor(context)
    capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            try {
                val mediaImage = image.image
                if (mediaImage != null) {
                    val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                    if (!cont.isCompleted) cont.resume(input)
                }
            } finally { image.close() }
        }
        override fun onError(exception: ImageCaptureException) { if (!cont.isCompleted) cont.resumeWithException(exception) }
    })
}