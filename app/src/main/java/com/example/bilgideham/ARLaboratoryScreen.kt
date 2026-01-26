package com.example.bilgideham

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.sin

/**
 * AR Laboratuvarı Ekranı
 * 20 hazır 3D model ile interaktif görüntüleme + Kamera AR Modu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARLaboratoryScreen(navController: NavController) {
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme
    
    var selectedCategory by remember { mutableStateOf(ARModelCategory.FEN) }
    var selectedModel by remember { mutableStateOf(ARModels.allModels.first()) }
    var showAIDialog by remember { mutableStateOf(false) }
    var isARMode by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Kamera izni launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            isARMode = true
            Toast.makeText(context, "Kamera açıldı! Modeli görmek için etrafınıza bakın.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Kamera izni verilmedi. 3D görünüm kullanılıyor.", Toast.LENGTH_SHORT).show()
        }
    }
    
    // AR modundan çıkış için BackHandler yerine buton kullanacağız
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isARMode) "📸" else "🔬", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isARMode) "AR Kamera" else "AR Laboratuvarı", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isARMode) {
                            isARMode = false
                        } else {
                            navController.popBackStack() 
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isARMode) Color(0xFF1A237E) else Color(0xFF00BFA5),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        
        if (isARMode && hasCameraPermission) {
            // AR KAMERA MODU
            ARCameraView(
                model = selectedModel,
                onExitAR = { isARMode = false },
                onModelSelected = { selectedModel = it },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        } else {
            // NORMAL 3D GÖRÜNÜM
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(cs.background)
            ) {
                // Kategori Seçici
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { 
                        selectedCategory = it
                        selectedModel = ARModels.getByCategory(it).firstOrNull() ?: selectedModel
                    }
                )
                
                // 3D Model Görüntüleyici
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Interactive3DViewer(
                        model = selectedModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Model Bilgi Kartı
                ModelInfoCard(model = selectedModel)
                
                // Model Seçim Grid
                Text(
                    text = "Model Seç",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = cs.onBackground
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ARModels.getByCategory(selectedCategory)) { model ->
                        ModelGridItem(
                            model = model,
                            isSelected = model.id == selectedModel.id,
                            onClick = { selectedModel = model }
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Aksiyon Butonları
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AR Modu Butonu
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                isARMode = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BFA5)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.CameraAlt, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("📸 AR Modu", fontWeight = FontWeight.Bold)
                    }
                    
                    // AI Üretim Butonu
                    OutlinedButton(
                        onClick = { showAIDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, Color(0xFFFF9800))
                    ) {
                        Text("🤖 AI Üret", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    }
                }
                
                Spacer(Modifier.height(16.dp))
            }
        }
    }
    
    // AI Yapım Aşamasında Dialog
    if (showAIDialog) {
        AlertDialog(
            onDismissRequest = { showAIDialog = false },
            icon = { Text("🚧", fontSize = 48.sp) },
            title = { 
                Text(
                    "Yapım Aşamasında",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "AI ile 3D Model Üretimi yakında aktif olacak!",
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "İstediğiniz herhangi bir nesneyi yazarak 3D model oluşturabileceksiniz.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAIDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Anladım")
                }
            }
        )
    }
}

/**
 * AR Kamera Görünümü - Kamera üzerine 3D model overlay
 */
@Composable
private fun ARCameraView(
    model: ARModel,
    onExitAR: () -> Unit,
    onModelSelected: (ARModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Model transform states
    var modelScale by remember { mutableFloatStateOf(1f) }
    var modelRotationY by remember { mutableFloatStateOf(0f) }
    var modelOffsetX by remember { mutableFloatStateOf(0f) }
    var modelOffsetY by remember { mutableFloatStateOf(0f) }
    
    // Auto rotation
    val infiniteTransition = rememberInfiniteTransition(label = "arRotate")
    val autoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(modifier = modifier) {
        // Kamera önizleme
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            Log.e("ARLaboratory", "Kamera başlatılamadı", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // AR Model Overlay - Kamera üzerinde 3D model
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        modelOffsetX += pan.x
                        modelOffsetY += pan.y
                        modelRotationY += pan.x * 0.3f
                        modelScale = (modelScale * zoom).coerceIn(0.3f, 4f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // 3D Model - Kamera görüntüsü üzerinde
            Box(
                modifier = Modifier
                    .offset(x = modelOffsetX.dp / 3, y = modelOffsetY.dp / 3)
                    .scale(modelScale)
                    .graphicsLayer {
                        rotationY = modelRotationY + autoRotation
                        shadowElevation = 20f
                    },
                contentAlignment = Alignment.Center
            ) {
                AR3DModelOverlay(model = model)
            }
        }
        
        // Üst bilgi paneli
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "📍 ${model.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Modeli hareket ettirmek için sürükleyin",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
        
        // Alt kontrol paneli
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Model seçim butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ARModels.allModels.take(6).forEach { m ->
                    Surface(
                        onClick = { onModelSelected(m) },
                        shape = CircleShape,
                        color = if (m.id == model.id) Color(0xFF00BFA5) else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(m.emoji, fontSize = 24.sp)
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // AR'dan çık butonu
            Button(
                onClick = onExitAR,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Close, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("3D Görünüme Dön", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * AR modunda gösterilen 3D model overlay
 */
@Composable
private fun AR3DModelOverlay(model: ARModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "arModel")
    
    // Glow effect
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(contentAlignment = Alignment.Center) {
        // Glow background
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(glowScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(model.color).copy(alpha = 0.5f),
                            Color(model.color).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Model
        when (model.id) {
            "heart" -> ARAnimatedHeart()
            "solar_system" -> ARAnimatedSolarSystem()
            "volcano" -> ARAnimatedVolcano()
            "dna" -> ARAnimatedDNA()
            "atom" -> ARAnimatedAtom()
            else -> Text(model.emoji, fontSize = 120.sp)
        }
    }
}

@Composable
private fun ARAnimatedHeart() {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )
    
    Text("❤️", fontSize = 140.sp, modifier = Modifier.scale(scale))
}

@Composable
private fun ARAnimatedSolarSystem() {
    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )
    
    Box(contentAlignment = Alignment.Center) {
        Text("☀️", fontSize = 60.sp)
        listOf("🌍" to 50f, "🪐" to 75f, "🔴" to 95f).forEachIndexed { i, (emoji, r) ->
            val angle = rotation + (i * 120f)
            Text(
                emoji, fontSize = 28.sp,
                modifier = Modifier.offset(
                    x = (cos(Math.toRadians(angle.toDouble())).toFloat() * r).dp,
                    y = (sin(Math.toRadians(angle.toDouble())).toFloat() * r * 0.5f).dp
                )
            )
        }
    }
}

@Composable
private fun ARAnimatedVolcano() {
    val infiniteTransition = rememberInfiniteTransition(label = "volcano")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lava"
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("💥", fontSize = 50.sp, modifier = Modifier.offset(y = bounce.dp))
        Text("🌋", fontSize = 120.sp)
    }
}

@Composable
private fun ARAnimatedDNA() {
    val infiniteTransition = rememberInfiniteTransition(label = "dna")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twist"
    )
    
    Text("🧬", fontSize = 140.sp, modifier = Modifier.rotate(rotation))
}

@Composable
private fun ARAnimatedAtom() {
    val infiniteTransition = rememberInfiniteTransition(label = "atom")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "electron"
    )
    
    Box(contentAlignment = Alignment.Center) {
        Text("⚛️", fontSize = 120.sp)
        repeat(3) { i ->
            val angle = rotation + (i * 120f)
            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(Math.toRadians(angle.toDouble())).toFloat() * 80).dp,
                        y = (sin(Math.toRadians(angle.toDouble())).toFloat() * 35).dp
                    )
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

// ========== MEVCUT FONKSİYONLAR (Normal mod için) ==========

@Composable
private fun CategorySelector(
    selectedCategory: ARModelCategory,
    onCategorySelected: (ARModelCategory) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ARModelCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF00BFA5) else cs.surfaceVariant,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(category.icon, fontSize = 24.sp)
                    Text(
                        category.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else cs.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun Interactive3DViewer(
    model: ARModel,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "autoRotate")
    val autoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(model.color).copy(alpha = 0.2f),
                        cs.surfaceVariant.copy(alpha = 0.5f),
                        cs.surface
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(model.color).copy(alpha = glowAlpha),
                        Color(model.color).copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    rotationY += pan.x * 0.5f
                    rotationX -= pan.y * 0.5f
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .graphicsLayer {
                    this.rotationX = rotationX
                    this.rotationY = rotationY + autoRotation
                },
            contentAlignment = Alignment.Center
        ) {
            Model3DPlaceholder(model = model)
        }
        
        Text(
            text = "👆 Döndürmek için kaydır\n🔍 Yakınlaştır için sıkıştır",
            fontSize = 12.sp,
            color = cs.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun Model3DPlaceholder(model: ARModel) {
    when (model.id) {
        "heart" -> AnimatedHeart()
        "solar_system" -> AnimatedSolarSystem()
        "volcano" -> AnimatedVolcano()
        "dna" -> AnimatedDNA()
        "atom" -> AnimatedAtom()
        else -> DefaultModelPlaceholder(model)
    }
}

@Composable
private fun AnimatedHeart() {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )
    Text("❤️", fontSize = 120.sp, modifier = Modifier.scale(scale))
}

@Composable
private fun AnimatedSolarSystem() {
    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )
    
    Box(contentAlignment = Alignment.Center) {
        Text("☀️", fontSize = 50.sp)
        listOf("🪐" to 60f, "🌍" to 80f, "🔴" to 100f).forEachIndexed { i, (emoji, r) ->
            val angle = rotation + (i * 120f)
            Text(
                emoji, fontSize = 24.sp,
                modifier = Modifier.offset(
                    x = (cos(Math.toRadians(angle.toDouble())).toFloat() * r).dp,
                    y = (sin(Math.toRadians(angle.toDouble())).toFloat() * r * 0.5f).dp
                )
            )
        }
    }
}

@Composable
private fun AnimatedVolcano() {
    val infiniteTransition = rememberInfiniteTransition(label = "volcano")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lava"
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("💥", fontSize = 40.sp, modifier = Modifier.offset(y = bounce.dp))
        Text("🌋", fontSize = 100.sp)
    }
}

@Composable
private fun AnimatedDNA() {
    val infiniteTransition = rememberInfiniteTransition(label = "dna")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twist"
    )
    Text("🧬", fontSize = 120.sp, modifier = Modifier.rotate(rotation))
}

@Composable
private fun AnimatedAtom() {
    val infiniteTransition = rememberInfiniteTransition(label = "atom")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "electron"
    )
    
    Box(contentAlignment = Alignment.Center) {
        Text("⚛️", fontSize = 100.sp)
        repeat(3) { i ->
            val angle = rotation + (i * 120f)
            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(Math.toRadians(angle.toDouble())).toFloat() * 70).dp,
                        y = (sin(Math.toRadians(angle.toDouble())).toFloat() * 30).dp
                    )
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@Composable
private fun DefaultModelPlaceholder(model: ARModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(model.emoji, fontSize = 100.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            model.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ModelInfoCard(model: ARModel) {
    val cs = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(model.color).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(model.emoji, fontSize = 40.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(model.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                Text(model.description, fontSize = 14.sp, color = cs.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun ModelGridItem(
    model: ARModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(model.color).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) BorderStroke(2.dp, Color(model.color)) else null
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(model.emoji, fontSize = 28.sp)
        }
    }
}
