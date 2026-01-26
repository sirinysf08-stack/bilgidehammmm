package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Segment
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilgideham.ui.theme.InterfaceParams
import com.example.bilgideham.ui.theme.LocalInterfaceStyle
import kotlin.random.Random

/**
 * Modern Theme UI Components for HomeScreen
 * Clean and professional design
 */


// ==================== MODERN ARAYÜZ ====================

@Composable
fun ModernHeader(brandTitle: String, darkMode: Boolean, onMenuClick: () -> Unit, onToggleTheme: () -> Unit, onToggleBrightness: () -> Unit, currentBrightness: Int, onSecretTap: () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)).background(brush = resolveTopGradient(cs = cs, darkMode = darkMode))) {
        HomeStarDustEffect(color = Color.White.copy(alpha = 0.28f))
        Row(modifier = Modifier.fillMaxSize().padding(top = 40.dp, start = 12.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) { Icon(Icons.AutoMirrored.Rounded.Segment, "Menu", tint = Color.White, modifier = Modifier.size(34.dp)) }
            Spacer(Modifier.width(6.dp))
            AnimatedBrandWordmark(modifier = Modifier.weight(1f), darkMode = darkMode, compact = true, brandTitle = brandTitle, onSecretTap = onSecretTap)
            IconButton(onClick = onToggleTheme) { Crossfade(targetState = darkMode, label = "themeAnim") { isDark -> Icon(if (isDark) Icons.Rounded.Bedtime else Icons.Rounded.WbSunny, "Tema", tint = if (isDark) Color(0xFFC5CAE9) else Color(0xFFFFEE58), modifier = Modifier.size(28.dp)) } }
            IconButton(onClick = onToggleBrightness) { Icon(when (currentBrightness) { 0 -> Icons.Rounded.BrightnessLow; 1 -> Icons.Rounded.BrightnessMedium; else -> Icons.Rounded.BrightnessHigh }, "Parlaklık", tint = Color.White, modifier = Modifier.size(28.dp)) }
        }
    }
}

@Composable
fun ModernHomeContent(padding: PaddingValues, navController: NavController, context: Context, darkMode: Boolean, solvedToday: Int, dailyTarget: Int, randomMessage: String, randomPunch: String) {
    val cs = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize().background(cs.background)) {
        HomeStarDustEffect(color = cs.onBackground.copy(alpha = if (darkMode) 0.18f else 0.1f))
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            AIGlassHeroCard(message = randomMessage, solved = solvedToday, target = dailyTarget, punchWord = randomPunch, darkMode = darkMode)
            val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
            val subjects = remember(educationPrefs) { AppPrefs.getCurrentSubjects(context) }
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Normal dersler (İmam Hatip dersleri ve Paragraf hariç - Paragraf hızlı erişimde var)
                val normalSubjects = subjects.filter { subject ->
                    !subject.id.startsWith("arapca") && 
                    !subject.id.startsWith("kuran") && 
                    !subject.id.startsWith("siyer") &&
                    !subject.id.contains("paragraf", ignoreCase = true)
                }
                val imamHatipSubjects = subjects.filter { subject ->
                    subject.id.startsWith("arapca") || subject.id.startsWith("kuran") || subject.id.startsWith("siyer")
                }
                normalSubjects.forEach { subject -> DynamicLessonCard(subject = subject, onClick = { navController.navigate(subject.route) }) }
                // İmam Hatip Bölümü - Oval çerçeve içinde
                if (imamHatipSubjects.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Kartları çevreleyen ince oval border
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.8.dp,
                                    color = cs.onBackground.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(16.dp)
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            imamHatipSubjects.forEach { subject -> DynamicLessonCard(subject = subject, onClick = { navController.navigate(subject.route) }) }
                        }
                        // "İmam Hatip" etiketi - üst ortada
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-8).dp)
                                .background(cs.background)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "İmam Hatip",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = cs.onBackground.copy(alpha = 0.4f),
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
                // KPSS Geçmiş Soruları kaldırıldı - yasal olmadığı öğrenildi
                // Sınıf Düellosu - KPSS ve AGS hariç
                if (educationPrefs.level != EducationLevel.KPSS && educationPrefs.level != EducationLevel.AGS) {
                    DuelBannerModern { navController.navigate("class_duel") }
                }
                Spacer(Modifier.height(16.dp))
                val showParagraf = educationPrefs.schoolType != SchoolType.AGS_OABT || AppPrefs.getOabtField(context) == "turkce"
                
                if (showParagraf) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCardModern("Paragraf", "Hedef 20", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFF4FC3F7), Modifier.weight(1f)) { navController.navigate("paragraph_practice_screen") }
                        QuickActionCardModern("Deneme", "Sınav Provası", Icons.AutoMirrored.Rounded.Assignment, Color(0xFFFFD54F), Modifier.weight(1f)) { navController.navigate("practice_exam_screen") }
                    }
                } else {
                    // Paragraf gizliyse, geniş Deneme kartı göster
                    WideExamCard { navController.navigate("practice_exam_screen") }
                }
                // Geçmiş Sınav Soruları kartları (seviyeye göre)
                val userLevel = educationPrefs.level
                val userGrade = educationPrefs.grade
                // LGS için Geçmiş Sorular
                if (userLevel == EducationLevel.ORTAOKUL && userGrade == 8) {
                    var showLgsDialog by remember { mutableStateOf(false) }
                    if (showLgsDialog) {
                        AlertDialog(
                            onDismissRequest = { showLgsDialog = false },
                            title = { Text("Geliştirme Aşamasında 🚧") },
                            text = { Text("Geçmiş LGS soruları arşivi hazırlanıyor. Çok yakında burada!") },
                            confirmButton = {
                                TextButton(onClick = { showLgsDialog = false }) { Text("Tamam") }
                            }
                        )
                    }
                    PastExamCard(
                        title = "Geçmiş LGS Soruları", subtitle = "2018-2024 yılları arası çıkmış sorular",
                        emoji = "🎓",
                        accentColor = Color(0xFF2196F3),
                        onClick = { showLgsDialog = true }
                    )
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

// -------------------- UI BİLEŞENLERİ --------------------
@Composable
fun AIGlassHeroCard(
    message: String,
    solved: Int,
    target: Int,
    punchWord: String,
    darkMode: Boolean
) {
    val progress = (solved.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(44.dp))
            .background(
                Brush.linearGradient(
                    if (darkMode) listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                    else listOf(cs.primary, cs.tertiary, cs.primaryContainer)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(44.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = 250.dp.toPx()
                ),
                radius = 250.dp.toPx(),
                center = Offset(size.width, 0f)
            )
        }
        Row(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00E5FF)).blur(3.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "YAPAY ZEKA HER AN YANINDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 2.sp
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
                Text(
                    text = punchWord,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(0.12f),
                    modifier = Modifier.offset(y = (-8).dp)
                )
                Spacer(Modifier.height(14.dp))
                ModernTargetBar(progress, solved, target)
            }
            Box(modifier = Modifier.size(140.dp).padding(start = 16.dp)) {
                NeuralBrainAIView()
            }
        }
    }
}

@Composable
fun ModernTargetBar(progress: Float, solved: Int, target: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2200, easing = FastOutSlowInEasing),
        label = "targetProgress"
    )
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00E5FF)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "HEDEF: $target SORU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFFD600))
                        )
                    )
            )
        }
        Text(
            text = "Bugün $solved Soru Çözdün",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 10.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NeuralBrainAIView() {
    val infiniteTransition = rememberInfiniteTransition(label = "brainSystem")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "brainPulse"
    )
    val networkRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "brainRotation"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "brainGlow"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.45f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00E5FF).copy(0.35f * glowAlpha), Color.Transparent),
                center = center,
                radius = radius * 1.8f
            ),
            radius = radius * 1.8f,
            center = center
        )
        withTransform({
            rotate(networkRotation, center)
            scale(pulseScale, pulseScale, center)
        }) {
            val nodes = listOf(
                Offset(center.x - radius * 0.6f, center.y - radius * 0.4f),
                Offset(center.x + radius * 0.6f, center.y - radius * 0.3f),
                Offset(center.x - radius * 0.3f, center.y + radius * 0.6f),
                Offset(center.x + radius * 0.4f, center.y + radius * 0.5f),
                Offset(center.x, center.y - radius * 0.8f),
                Offset(center.x, center.y + radius * 0.2f),
                Offset(center.x - radius * 0.5f, center.y + radius * 0.1f)
            )
            nodes.forEachIndexed { i, start ->
                nodes.forEachIndexed { j, end ->
                    if (i < j) {
                        drawLine(
                            color = Color(0xFF00E5FF).copy(0.4f),
                            start = start,
                            end = end,
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
            }
            nodes.forEach { node ->
                drawCircle(Color.White, radius = 3.5.dp.toPx(), center = node)
                drawCircle(
                    color = Color(0xFF00E5FF).copy(glowAlpha),
                    radius = 7.dp.toPx(),
                    center = node,
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFD500F9).copy(0.8f), Color.Transparent),
                radius = 20.dp.toPx()
            ),
            radius = 22.dp.toPx() * pulseScale,
            center = center
        )
    }
}


@Composable
fun LessonCardModern(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp
    Card(
        modifier = Modifier.fillMaxWidth().height(108.dp).clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(cornerRadius * 0.8f))
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
                Text(
                    text = desc,
                    fontSize = 13.sp,
                    color = cs.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = cs.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun QuickActionCardModern(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp
    Card(
        modifier = modifier.height(135.dp).clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
                Text(text = subtitle, fontSize = 13.sp, color = cs.onSurface.copy(alpha = 0.65f))
            }
        }
    }
}

@Composable
fun DuelBannerModern(onClick: () -> Unit) {
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
            .clickable { onClick() }
            .border(1.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(cornerRadius))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🥊", fontSize = 32.sp)
            }
            Spacer(Modifier.width(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SINIF DÜELLOSU",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Bluetooth ile arkadaşınla yarış!",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            }
            Icon(
                imageVector = Icons.Rounded.Bolt,
                contentDescription = null,
                tint = Color(0xFFFFD600),
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@Composable
fun WideExamCard(
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFD54F).copy(alpha = 0.15f),
                            cs.surface
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol taraf - İkon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(cornerRadius * 0.8f))
                        .background(Color(0xFFFFD54F).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Assignment,
                        null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(42.dp)
                    )
                }
                
                Spacer(Modifier.width(24.dp))
                
                // Sağ taraf - Bilgiler
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Deneme Sınavı",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Text(
                        text = "Gerçek sınav ortamında kendinizi test edin",
                        fontSize = 14.sp,
                        color = cs.onSurface.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.Timer,
                                null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Zamanlı",
                                fontSize = 12.sp,
                                color = cs.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Detaylı Analiz",
                                fontSize = 12.sp,
                                color = cs.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Icon(
                    Icons.Rounded.ChevronRight,
                    null,
                    tint = cs.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
