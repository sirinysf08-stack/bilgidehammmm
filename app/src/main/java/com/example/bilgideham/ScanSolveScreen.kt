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
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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

    var showCamera by remember { mutableStateOf(false) }
    var ocrLoading by remember { mutableStateOf(false) }
    var aiLoading by remember { mutableStateOf(false) }

    var scannedText by remember { mutableStateOf("") }
    var solvedText by remember { mutableStateOf("") }

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
                autoSolveFromOcr(snackbarHostState, scope, scannedText) { l, s -> aiLoading = l; solvedText = s }
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
                autoSolveFromOcr(snackbarHostState, scope, scannedText) { l, s -> aiLoading = l; solvedText = s }
            } catch (_: Exception) {
                snackbarHostState.showSnackbar("Çekim hatası.")
            } finally {
                ocrLoading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Yol Gösterici AI 🧭", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.surface,
                    titleContentColor = cs.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(cs.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. AÇIKLAMA VE SLOGAN KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cs.primaryContainer),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "🎣 Balık Tutmayı Öğreten AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = cs.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    // Örnek Kutusu
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Text(
                                text = "Yapay zeka cevabı hemen vermez. Şöyle der:",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = cs.onPrimaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "\"Soruyu net cevap vermez ! Örnek; Önce parantez içini yapmalısın, sonra çarpma işlemini dene. İpucu: 5x4 kaç eder? gibi.\"",
                                fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Seni tembelleştirmez, adım adım düşünmeni sağlar! 💪",
                        fontSize = 13.sp,
                        color = cs.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- 2. AKSİYON ALANI (KAMERA / GALERİ) ---
            if (!showCamera) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Kamera Butonu
                    ActionButton(
                        text = "Fotoğraf Çek",
                        icon = Icons.Default.CameraAlt,
                        color = cs.primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (camGranted) showCamera = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                    }

                    // Galeri Butonu
                    ActionButton(
                        text = "Galeriden Seç",
                        icon = Icons.Default.PhotoLibrary,
                        color = cs.secondary,
                        modifier = Modifier.weight(1f)
                    ) {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            } else {
                // Kamera Önizleme Modu
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(400.dp)
                ) {
                    Box {
                        CameraPreview(
                            onReady = { imageCapture = it },
                            modifier = Modifier.fillMaxSize()
                        )
                        // Kapat Butonu
                        IconButton(
                            onClick = { showCamera = false },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                        // Çek Butonu
                        Button(
                            onClick = { captureAndSolve() },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                        ) {
                            Icon(Icons.Default.Camera, null)
                            Spacer(Modifier.width(8.dp))
                            Text("TARA VE İPUCU AL")
                        }
                    }
                }
            }

            // Yükleniyor Göstergesi
            if (ocrLoading || aiLoading) {
                Spacer(Modifier.height(20.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(6.dp), color = cs.primary, trackColor = cs.surfaceVariant)
                Text(
                    if (ocrLoading) "Soru okunuyor..." else "Öğretmen düşünüyor...",
                    modifier = Modifier.padding(top = 8.dp),
                    color = cs.primary,
                    fontSize = 14.sp
                )
            }

            // --- 3. SONUÇ ALANI (ÖĞRETMEN NOTU) ---
            if (solvedText.isNotBlank()) {
                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)) // Post-it Sarısı
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFBC02D), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("ÖĞRETMENİN NOTU 📝", fontWeight = FontWeight.Black, color = Color(0xFFE65100), fontSize = 16.sp)
                        }

                        Divider(Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.1f))

                        Text(
                            text = solvedText,
                            fontSize = 16.sp,
                            color = Color(0xFF3E2723),
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "💡 Unutma: Çözümü kendin bulursan asla unutmazsın!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f),
                            fontStyle = FontStyle.Italic
                        )
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
                        autoSolveFromOcr(snackbarHostState, scope, scannedText) { l, s -> aiLoading = l; solvedText = s }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Tekrar Analiz Et")
                }
            }
        }
    }
}

// Şık Aksiyon Butonu Bileşeni
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(text, fontWeight = FontWeight.Bold, color = color)
        }
    }
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
    setState: (loading: Boolean, solved: String) -> Unit
) {
    scope.launch {
        setState(true, "")
        try {
            val text = ocrText.trim()
            val promptText = """
Sen 5.sınıf'ın özel öğretmenisin. Görevin bu soruyu ÇÖZMEK DEĞİL, ona BALIK TUTMAYI ÖĞRETMEK.
Aşağıdaki soruyla ilgili öğrenciye koçluk yap:
"$text"

KURALLAR:
1. Asla şıkkı (A, B, C...) veya cevabı (5, 10, X...) söyleme.
2. Adım adım düşünmesini sağla.
3. Örnekteki gibi konuş: "Önce parantez içini yapmalısın, sonra çarpma işlemini dene. İpucu: 5x4 kaç eder?"
4. Eğer soru metni anlaşılmazsa nazikçe tekrar çekmesini söyle.
5. Çocuk dostu, cesaretlendirici ve emoji dolu bir dil kullan.
""".trimIndent()

            val ans = solveQuestionText(promptText) // AiCompat.kt içindeki fonksiyonu kullanır
            setState(false, ans.ifBlank { "Metni tam okuyamadım, tekrar dener misin? 🤔" })
        } catch (_: Exception) {
            setState(false, "Bağlantı hatası oluştu. İnternetini kontrol et.")
        }
    }
}

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