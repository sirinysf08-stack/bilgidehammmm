package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random

/**
 * Okul Türü ve Sınıf Seçim Ekranı
 * HomeScreen tasarım diliyle uyumlu - glassmorphism, modern kartlar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTypeScreen(
    navController: NavController,
    educationLevel: EducationLevel,
    onSelectionComplete: (SchoolType, Int?) -> Unit = { _, _ -> }
) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Tema kontrolü
    val darkMode = remember { AppPrefs.getDarkMode(context) }

    var selectedSchoolType by remember { mutableStateOf<SchoolType?>(null) }
    var selectedGrade by remember { mutableStateOf<Int?>(null) }

    val schoolTypes = remember(educationLevel) {
        CurriculumManager.getSchoolTypesFor(educationLevel)
    }

    val levelColor = Color(educationLevel.colorHex)

    // Arka plan gradient
    val backgroundBrush = if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF0B1220), Color(0xFF1E293B), Color(0xFF0F172A)))
    } else {
        Brush.verticalGradient(listOf(cs.primary, cs.tertiary, cs.primaryContainer))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Star Dust Efekti
        SchoolTypeStarDustEffect(color = Color.White.copy(alpha = 0.2f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "${educationLevel.icon} ${educationLevel.displayName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Hero Card
                SchoolTypeHeroCard(
                    level = educationLevel,
                    darkMode = darkMode
                )

                Spacer(Modifier.height(28.dp))

                // Bölüm Başlığı
                Text(
                    text = "OKUL TÜRÜNÜ SEÇ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(16.dp))

                // Okul Türü Kartları
                schoolTypes.forEach { schoolType ->
                    SchoolTypeCardModern(
                        schoolType = schoolType,
                        isSelected = selectedSchoolType == schoolType,
                        levelColor = levelColor,
                        darkMode = darkMode,
                        onClick = {
                            selectedSchoolType = schoolType
                            selectedGrade = null
                        }
                    )

                    // Sınıf seçimi - Her zaman görünür (tıklamaya gerek yok)
                    if (schoolType.grades.isNotEmpty()) {
                        GradeSelectorModern(
                            grades = schoolType.grades,
                            selectedGrade = if (selectedSchoolType == schoolType) selectedGrade else null,
                            levelColor = levelColor,
                            darkMode = darkMode,
                            onGradeSelected = { grade ->
                                selectedSchoolType = schoolType
                                selectedGrade = grade
                            }
                        )
                    }
                    
                    // AGS ÖABT Alan Seçimi (sınıf seçimi benzeri)
                    if (schoolType == SchoolType.AGS_OABT) {
                        var selectedField by remember { mutableStateOf<String?>(null) }
                        
                        OabtFieldSelectorModern(
                            selectedField = if (selectedSchoolType == schoolType) selectedField else null,
                            levelColor = levelColor,
                            darkMode = darkMode,
                            onFieldSelected = { field ->
                                selectedSchoolType = schoolType
                                selectedField = field
                                selectedGrade = 1 // Dummy grade to enable proceed button
                                // Alan'ı kaydet
                                AppPrefs.setOabtField(context, field)
                            }
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(28.dp))

                // Devam Et Butonu
                val canProceed = selectedSchoolType != null &&
                        (selectedSchoolType!!.grades.isEmpty() || selectedGrade != null)

                Button(
                    onClick = {
                        selectedSchoolType?.let { schoolType ->
                            AppPrefs.saveEducationPrefs(context, educationLevel, schoolType, selectedGrade)
                            onSelectionComplete(schoolType, selectedGrade)

                            navController.navigate("home") {
                                popUpTo("level_selection") { inclusive = true }
                            }
                        }
                    },
                    enabled = canProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canProceed) levelColor else Color.White.copy(alpha = 0.2f),
                        disabledContainerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Başlayalım! 🚀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Seçimini daha sonra ayarlardan değiştirebilirsin",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SchoolTypeHeroCard(
    level: EducationLevel,
    darkMode: Boolean
) {
    val levelColor = Color(level.colorHex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    if (darkMode) listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    else listOf(levelColor.copy(alpha = 0.9f), levelColor.copy(alpha = 0.7f))
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
    ) {
        // Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(levelColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.2f),
                    radius = 150.dp.toPx()
                ),
                radius = 150.dp.toPx(),
                center = Offset(size.width, 0f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                            .blur(2.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ADIM 2/2",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Okul Türü & Sınıf",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "Müfredatını belirleyelim",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(level.icon, fontSize = 36.sp)
            }
        }
    }
}

@Composable
private fun SchoolTypeCardModern(
    schoolType: SchoolType,
    isSelected: Boolean,
    levelColor: Color,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkMode) {
                if (isSelected) levelColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
            } else {
                if (isSelected) levelColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.95f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (darkMode) 0.dp else if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSelected) Modifier.border(
                        width = 2.dp,
                        color = levelColor,
                        shape = RoundedCornerShape(22.dp)
                    ) else Modifier
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) levelColor.copy(alpha = 0.2f)
                        else if (darkMode) Color.White.copy(alpha = 0.1f)
                        else cs.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = if (isSelected) levelColor
                    else if (darkMode) Color.White.copy(alpha = 0.7f)
                    else cs.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Metin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schoolType.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) levelColor
                    else if (darkMode) Color.White
                    else cs.onSurface
                )
                Text(
                    text = schoolType.description,
                    fontSize = 13.sp,
                    color = if (darkMode) Color.White.copy(alpha = 0.5f) else cs.onSurface.copy(alpha = 0.6f)
                )
            }

            // Seçim göstergesi
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(levelColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = if (darkMode) Color.White.copy(alpha = 0.3f) else cs.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun GradeSelectorModern(
    grades: List<Int>,
    selectedGrade: Int?,
    levelColor: Color,
    darkMode: Boolean,
    onGradeSelected: (Int) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkMode) Color.White.copy(alpha = 0.05f) else cs.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SINIF SEÇ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (darkMode) Color.White.copy(alpha = 0.5f) else cs.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                grades.forEach { grade ->
                    val isSelected = selectedGrade == grade

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onGradeSelected(grade) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) levelColor
                        else if (darkMode) Color.White.copy(alpha = 0.1f)
                        else cs.surface,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$grade",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White
                                else if (darkMode) Color.White.copy(alpha = 0.8f)
                                else cs.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// Star Dust Efekti
@Composable
private fun SchoolTypeStarDustEffect(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "schoolStarDust")
    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -250f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dustMove"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val randomGen = Random(8888)
        repeat(60) {
            val xPos = randomGen.nextFloat() * size.width
            val startY = randomGen.nextFloat() * size.height
            val currentY = (startY + moveY) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY

            drawCircle(
                color = color,
                radius = randomGen.nextFloat() * 3.dp.toPx() + 0.8.dp.toPx(),
                center = Offset(xPos, drawY),
                alpha = randomGen.nextFloat() * 0.85f
            )
        }
    }
}

// ÖABT Alan Seçici - Tarih, Türkçe vb.
@Composable
private fun OabtFieldSelectorModern(
    selectedField: String?,
    levelColor: Color,
    darkMode: Boolean,
    onFieldSelected: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    
    // Mevcut alanlar - CurriculumConfig'deki tüm ÖABT alanları
    val fields = listOf(
        Triple("turkce", "Türkçe", "📖"),
        Triple("ilkmat", "İlk. Mat.", "🔢"),
        Triple("fen", "Fen Bil.", "🔬"),
        Triple("sosyal", "Sosyal Bil.", "🌍"),
        Triple("edebiyat", "Edebiyat", "📚"),
        Triple("tarih", "Tarih", "🏛️"),
        Triple("cografya", "Coğrafya", "🗺️"),
        Triple("matematik", "Matematik", "📐"),
        Triple("fizik", "Fizik", "⚡"),
        Triple("kimya", "Kimya", "🧪"),
        Triple("biyoloji", "Biyoloji", "🧬"),
        Triple("din", "Din Kültürü", "☪️"),
        Triple("rehberlik", "Rehberlik", "🧠"),
        Triple("sinif", "Sınıf Öğrt.", "👨‍🏫"),
        Triple("okoncesi", "Okul Öncesi", "🎨"),
        Triple("beden", "Beden Eğt.", "🏃")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkMode) Color.White.copy(alpha = 0.05f) else cs.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ALAN SEÇ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (darkMode) Color.White.copy(alpha = 0.5f) else cs.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // İlk satır - 4 alan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fields.take(4).forEach { (id, name, icon) ->
                    val isSelected = selectedField == id
                    val isActive = true // TÜM ALANLAR AKTİF

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isActive) { onFieldSelected(id) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> levelColor
                            !isActive -> if (darkMode) Color.White.copy(alpha = 0.03f) else cs.surface.copy(alpha = 0.5f)
                            darkMode -> Color.White.copy(alpha = 0.1f)
                            else -> cs.surface
                        },
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isSelected -> Color.White
                                    !isActive -> if (darkMode) Color.White.copy(alpha = 0.2f) else cs.onSurface.copy(alpha = 0.3f)
                                    darkMode -> Color.White.copy(alpha = 0.8f)
                                    else -> cs.onSurface
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(10.dp))
            
            // İkinci satır - 4 alan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fields.drop(4).take(4).forEach { (id, name, icon) ->
                    val isSelected = selectedField == id
                    val isActive = true // TÜM ALANLAR AKTİF

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isActive) { onFieldSelected(id) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> levelColor
                            !isActive -> if (darkMode) Color.White.copy(alpha = 0.03f) else cs.surface.copy(alpha = 0.5f)
                            darkMode -> Color.White.copy(alpha = 0.1f)
                            else -> cs.surface
                        },
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isSelected -> Color.White
                                    !isActive -> if (darkMode) Color.White.copy(alpha = 0.2f) else cs.onSurface.copy(alpha = 0.3f)
                                    darkMode -> Color.White.copy(alpha = 0.8f)
                                    else -> cs.onSurface
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(10.dp))
            
            // Üçüncü satır - 4 alan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fields.drop(8).take(4).forEach { (id, name, icon) ->
                    val isSelected = selectedField == id
                    val isActive = true // TÜM ALANLAR AKTİF

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isActive) { onFieldSelected(id) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> levelColor
                            !isActive -> if (darkMode) Color.White.copy(alpha = 0.03f) else cs.surface.copy(alpha = 0.5f)
                            darkMode -> Color.White.copy(alpha = 0.1f)
                            else -> cs.surface
                        },
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isSelected -> Color.White
                                    !isActive -> if (darkMode) Color.White.copy(alpha = 0.2f) else cs.onSurface.copy(alpha = 0.3f)
                                    darkMode -> Color.White.copy(alpha = 0.8f)
                                    else -> cs.onSurface
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(10.dp))
            
            // Dördüncü satır - 4 alan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fields.drop(12).forEach { (id, name, icon) ->
                    val isSelected = selectedField == id
                    val isActive = true // TÜM ALANLAR AKTİF

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isActive) { onFieldSelected(id) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> levelColor
                            !isActive -> if (darkMode) Color.White.copy(alpha = 0.03f) else cs.surface.copy(alpha = 0.5f)
                            darkMode -> Color.White.copy(alpha = 0.1f)
                            else -> cs.surface
                        },
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isSelected -> Color.White
                                    !isActive -> if (darkMode) Color.White.copy(alpha = 0.2f) else cs.onSurface.copy(alpha = 0.3f)
                                    darkMode -> Color.White.copy(alpha = 0.8f)
                                    else -> cs.onSurface
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
