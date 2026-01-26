package com.example.bilgideham

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtlasScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Kullanıcı seviyesini al
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userLevel = prefs.getString("education_level", "ORTAOKUL") ?: "ORTAOKUL"
    val userGrade = prefs.getInt("grade", 5)
    
    // Seviye açıklaması
    val levelDescription = when (userLevel) {
        "ILKOKUL" -> "4. sınıf"
        "ORTAOKUL" -> "${userGrade}. sınıf"
        "LISE" -> "${userGrade}. sınıf lise"
        "KPSS" -> "KPSS"
        "AGS" -> "Üniversite"
        else -> "5. sınıf"
    }

    // Durumlar
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    // Scaffold: Geri tuşunu ve başlığı sayfanın tepesine sabitler
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // --- MODERN HEADER ---
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)))
            ) {
                // Yıldız Tozu Efekti
                Canvas(modifier = Modifier.fillMaxSize()) {
                    repeat(20) {
                        drawCircle(
                            color = Color.White,
                            radius = (1..3).random().dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset((0..size.width.toInt()).random().toFloat(), (0..size.height.toInt()).random().toFloat()),
                            alpha = 0.2f
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxSize().padding(top = 24.dp, start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White) }
                    Spacer(Modifier.width(8.dp))
                    Text("Coğrafya Atlası 🌍", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Dinamik Arka Plan
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. SLOGAN KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dünyayı Keşfet! 🗺️",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Merak ettiğin ülke veya şehri yaz, senin için rehber olayım.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- 2. MODERN ARAMA ALANI ---
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nereyi merak ediyorsun?") },
                placeholder = { Text("Örn: Japonya, Paris, Nil Nehri...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                // Varsayılan theme renkleri kullanılıyor, dark mode uyumlu

            )

            Spacer(Modifier.height(16.dp))

            // --- 3. AKSİYON BUTONU ---
            Button(
                onClick = {
                    if (loading) return@Button
                    val q = input.trim()
                    if (q.isBlank()) {
                        result = "Lütfen geçerli bir yer ismi yazınız."
                        return@Button
                    }
                    focusManager.clearFocus() // Klavyeyi kapat
                    loading = true
                    result = null

                    scope.launch {
                        // Mevcut yapındaki fonksiyonu çağırıyoruz
                        val text = try {
                            atlasLookupText(q, levelDescription)
                        } catch (e: Exception) {
                            "Bağlantı hatası oluştu. Lütfen tekrar dene."
                        }
                        result = text
                        loading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Haritalar Açılıyor...")
                } else {
                    Icon(Icons.Default.Public, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Bilgileri Getir", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // --- 4. SONUÇ ALANI ---
            if (result != null) {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Keşif Raporu:",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            text = result!!,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                        
                        // Kopyalama ve Paylaşma Butonları
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Kopyala Butonu
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Keşif Raporu", result)
                                    clipboard.setPrimaryClip(clip)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Rapor kopyalandı! 📋")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Kopyala",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Kopyala", fontSize = 14.sp)
                            }
                            
                            // Paylaş Butonu
                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Coğrafya Atlası - $input")
                                        putExtra(Intent.EXTRA_TEXT, "🌍 Coğrafya Atlası - $input\n\n$result\n\n📱 Akıl Küpü AI ile keşfedildi")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Raporu Paylaş"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Paylaş",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Paylaş", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}