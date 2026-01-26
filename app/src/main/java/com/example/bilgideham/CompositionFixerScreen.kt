package com.example.bilgideham

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
// Eğer "Sync Now" yaptıysan buradaki kırmızılık gidecektir:
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private const val TAG = "CompositionFixer"

// Operasyonel stabilite: primary + fallback (Multimodal modeller)
private const val MODEL_PRIMARY = "gemini-2.0-flash"
private const val MODEL_FALLBACK = "gemini-1.5-flash"

private enum class FailType { NETWORK, TIMEOUT, AUTH, QUOTA, UNKNOWN }

@Composable
fun CompositionFixerScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    // Renk Paleti
    // Renk Paleti - Dinamik
    val pageBg = MaterialTheme.colorScheme.background
    val fieldBg = MaterialTheme.colorScheme.surfaceContainerHighest
    val borderUnfocused = MaterialTheme.colorScheme.outline
    val borderFocused = MaterialTheme.colorScheme.primary
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    // Modern Gradientler
    val topBarGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
    )
    val btnGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    )

    // Firebase Vertex AI
    val vertexAi = remember { Firebase.vertexAI }
    val modelPrimary = remember { vertexAi.generativeModel(modelName = MODEL_PRIMARY) }
    val modelFallback = remember { vertexAi.generativeModel(modelName = MODEL_FALLBACK) }

    // States
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // Görsel State'i
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportContentDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { _, _ -> showReportDialog = false }
        )
    }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // --- Görsel İşlemleri için Yardımcılar ---

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> selectedImageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) selectedImageUri = tempCameraUri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ComposeFileProvider.getImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Kamera izni gerekli.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- AI Yardımcı Fonksiyonları ---

    fun classifyFail(t: Throwable?): FailType {
        if (t == null) return FailType.UNKNOWN
        val msg = (t.message ?: "").lowercase()

        if (t is IOException || msg.contains("network")) return FailType.NETWORK
        if (msg.contains("timeout")) return FailType.TIMEOUT
        if (msg.contains("auth") || msg.contains("appcheck")) return FailType.AUTH
        if (msg.contains("quota") || msg.contains("429")) return FailType.QUOTA
        return FailType.UNKNOWN
    }

    fun userMessage(type: FailType): String {
        return when (type) {
            FailType.NETWORK -> "İnternet bağlantını kontrol et."
            FailType.TIMEOUT -> "İstek zaman aşımına uğradı. Tekrar dene."
            FailType.AUTH -> "AI yetkilendirme hatası."
            FailType.QUOTA -> "Günlük AI limitine ulaşıldı."
            FailType.UNKNOWN -> "AI şu an yanıt veremedi. Tekrar dene."
        }
    }

    fun buildPrompt(text: String, hasImage: Boolean): String {
        val t = text.trim()
        val taskDescription = if (hasImage) {
            "Görev: Bu görseldeki el yazısı veya basılı metni oku. Ardından okuduğun bu metni (ve varsa kullanıcının eklediği aşağıdaki notları) yazım, noktalama ve dil bilgisi açısından düzelt."
        } else {
            "Görev: Aşağıdaki metni yazım, noktalama ve dil bilgisi açısından düzelt."
        }

        val userTextSection = if (t.isNotBlank()) "\n\nKullanıcı Notu/Metni:\n$t" else ""

        return """
Sen 5. sınıf seviyesine uygun, tatlı ve sabırlı bir Türkçe öğretmenisin.
$taskDescription

KRİTİK KURALLAR:
- Metnin orijinal anlamını KORU.
- Sadece verilen metni düzelt, yeni cümleler ekleme.
- Önce DÜZELTİLMİŞ metnin TAM halini yaz.
- Altına hataları kısa maddelerle not et.

ÇIKTI FORMATI:
Düzeltilmiş Metin:
<buraya düzeltilmiş metin>

Notlar:
- <hata notu 1>
- <hata notu 2>
(Hata yoksa: "- Metin hatasız görünüyor.")
$userTextSection
""".trimIndent()
    }

    suspend fun uriToBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Görsel dönüştürme hatası: ${e.message}")
            null
        }
    }

    suspend fun callModelMultimodal(model: GenerativeModel, prompt: String, imageUri: Uri?): Pair<String, Throwable?> = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = if (imageUri != null) uriToBitmap(imageUri) else null
            val resp = model.generateContent(content {
                if (bitmap != null) image(bitmap)
                text(prompt)
            })
            (resp.text ?: "").trim()
        }.fold(
            onSuccess = { it to null },
            onFailure = { "" to it }
        )
    }

    suspend fun fixContent(text: String, imageUri: Uri?): Pair<String, Throwable?> = withContext(Dispatchers.IO) {
        val prompt = buildPrompt(text, imageUri != null)
        val (pText, pErr) = callModelMultimodal(modelPrimary, prompt, imageUri)
        if (pText.isNotBlank()) return@withContext pText to null
        val (fText, fErr) = callModelMultimodal(modelFallback, prompt, imageUri)
        if (fText.isNotBlank()) return@withContext fText to null
        "" to (fErr ?: pErr)
    }

    Scaffold(
        containerColor = pageBg,
        topBar = {
            // --- DÜZELTİLEN MODERN ÜST BAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Yüksekliği artırdım, çakışmayı önler
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(topBarGradient)
            ) {
                // Geri Tuşu - En sol üstte
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 48.dp, start = 16.dp) // Status bar payı
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                // Yazılar - En sol altta
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = "Kompozisyon",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Düzeltici & Asistan",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Dekoratif İkon - Sağ altta
                Icon(
                    Icons.Rounded.AutoFixHigh,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .size(100.dp) // İkonu biraz büyüttüm
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Metnini yükle, AI öğretmen düzeltsin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(16.dp))

                    // --- Görsel Seçim Butonları ---
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OptionButton(
                            icon = Icons.Rounded.CameraAlt,
                            text = "Foto Çek",
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                        )
                        OptionButton(
                            icon = Icons.Rounded.PhotoLibrary,
                            text = "Galeriden Seç",
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- Görsel Önizleme ---
                    AnimatedVisibility(visible = selectedImageUri != null, enter = fadeIn(), exit = fadeOut()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, borderFocused, RoundedCornerShape(16.dp))
                                .background(fieldBg)
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Seçilen Görsel",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Kaldır", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // --- Metin Giriş ---
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = if (selectedImageUri != null) 3 else 6,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text(
                                if (selectedImageUri != null) "Görselle ilgili eklemek istediğin bir not var mı?" else "Metnini buraya yaz veya yapıştır...",
                                color = placeholderColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedBorderColor = borderFocused,
                            unfocusedBorderColor = borderUnfocused,
                            cursorColor = borderFocused
                        ),
                    )

                    Spacer(Modifier.height(20.dp))

                    // --- Gönder Butonu ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50), spotColor = borderFocused)
                            .clip(RoundedCornerShape(50))
                            .background(btnGradient)
                            .clickable(enabled = !loading) {
                                val t = input.trim()
                                if (t.isBlank() && selectedImageUri == null) {
                                    output = "Lütfen bir metin yaz veya bir fotoğraf ekle."
                                    return@clickable
                                }
                                loading = true
                                output = null
                                scope.launch {
                                    try {
                                        val (r, err) = fixContent(t, selectedImageUri)
                                        output = if (r.isBlank()) userMessage(classifyFail(err)) else r
                                    } finally {
                                        loading = false
                                    }
                                }
                            }
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("İnceleniyor...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(10.dp))
                                Text("Düzelt", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Sonuç Alanı ---
            output?.let { out ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).background(Color(0xFF22C55E), CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Öğretmenin Düzeltmesi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cs.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            Row {
                                ReportIconButton(onClick = { showReportDialog = true })
                                IconButton(onClick = { clipboard.setText(AnnotatedString(out)) }) {
                                    Icon(Icons.Default.Share, contentDescription = "Kopyala", tint = borderFocused)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(out, fontSize = 15.sp, color = cs.onSurface, lineHeight = 22.sp)
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
            AiDisclaimerFooter(isDarkMode = false)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun OptionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

object ComposeFileProvider {
    fun getImageUri(context: Context): Uri {
        val directory = File(context.cacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile("selected_image_", ".jpg", directory)
        val authority = context.packageName + ".fileprovider"
        return try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            Log.e(TAG, "FileProvider hatası: ${e.message}")
            Uri.fromFile(file)
        }
    }
}