package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Segment
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.BrightnessLow
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.bilgideham.ui.theme.LocalInterfaceStyle
import com.example.bilgideham.ui.theme.InterfaceParams
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun NeuralLuxHeader(
    brandTitle: String,
    darkMode: Boolean,
    onMenuClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleBrightness: () -> Unit,
    currentBrightness: Int,
    onSecretTap: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(label = "neuralLuxHeader")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing)),
        label = "glow"
    )
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5200, easing = LinearEasing)),
        label = "sweep"
    )

    val headerBrush = Brush.linearGradient(
        colors = if (darkMode) {
            listOf(Color(0xFF0B1020), Color(0xFF0A1B2E), Color(0xFF071826))
        } else {
            listOf(cs.primary, cs.secondary, cs.primary.copy(alpha = 0.9f))
        },
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(bottomStart = 42.dp, bottomEnd = 42.dp))
            .background(headerBrush)
    ) {
        NeuralLuxNeuralMesh(
            modifier = Modifier.fillMaxSize(),
            nodeColor = Color(0xFF00E5FF).copy(alpha = glow),
            lineColor = Color.White.copy(alpha = if (darkMode) 0.10f else 0.12f),
            sweep = sweep
        )

        // Subtle top highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (darkMode) 0.06f else 0.10f),
                            Color.Transparent,
                            Color.Black.copy(alpha = if (darkMode) 0.16f else 0.06f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Segment,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                AnimatedBrandWordmark(
                    modifier = Modifier.weight(1f),
                    darkMode = darkMode,
                    compact = true,
                    brandTitle = brandTitle,
                    onSecretTap = onSecretTap
                )

                IconButton(onClick = onToggleTheme) {
                    Icon(
                        if (darkMode) Icons.Rounded.Bedtime else Icons.Rounded.WbSunny,
                        contentDescription = "Tema",
                        tint = if (darkMode) Color(0xFFB3C7FF) else Color(0xFFFFF176),
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = onToggleBrightness) {
                    Icon(
                        when (currentBrightness) {
                            0 -> Icons.Rounded.BrightnessLow
                            1 -> Icons.Rounded.BrightnessMedium
                            else -> Icons.Rounded.BrightnessHigh
                        },
                        contentDescription = "Parlaklık",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            NeuralLuxSignalPill(
                text = if (darkMode) "NEURAL LUX • GECE MODU" else "NEURAL LUX • PREMIUM",
                accent = Color(0xFF00E5FF)
            )
        }
    }
}

@Composable
fun NeuralLuxHomeContent(
    padding: PaddingValues,
    navController: NavController,
    context: Context,
    darkMode: Boolean,
    solvedToday: Int,
    dailyTarget: Int,
    randomMessage: String,
    randomPunch: String
) {
    val cs = MaterialTheme.colorScheme

    val bg = if (darkMode) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF060B14), Color(0xFF08111D), Color(0xFF060B14))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(cs.background, cs.background.copy(alpha = 0.92f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        NeuralLuxHoloGrid(
            modifier = Modifier.fillMaxSize(),
            tint = if (darkMode) Color.White.copy(alpha = 0.05f) else cs.primary.copy(alpha = 0.05f)
        )

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            NeuralLuxHeroCard(
                title = randomPunch,
                message = randomMessage,
                solved = solvedToday,
                target = dailyTarget,
                darkMode = darkMode
            )

            val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
            val subjects = remember(educationPrefs) { AppPrefs.getCurrentSubjects(context) }

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                // Paragraf hariç dersler (Paragraf hızlı erişimde var)
                subjects.filter { !it.id.contains("paragraf", ignoreCase = true) }.forEach { subject ->
                    DynamicLessonCard(subject = subject, onClick = { navController.navigate(subject.route) })
                    Spacer(Modifier.height(14.dp))
                }

                if (educationPrefs.level != EducationLevel.KPSS && educationPrefs.level != EducationLevel.AGS) {
                    DuelBannerModern { navController.navigate("class_duel") }
                    Spacer(Modifier.height(14.dp))
                }

                val showParagraf = educationPrefs.schoolType != SchoolType.AGS_OABT || AppPrefs.getOabtField(context) == "turkce"
                
                if (showParagraf) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        QuickActionCardModern(
                            title = "Paragraf",
                            subtitle = "Neural okuma modu",
                            icon = Icons.AutoMirrored.Rounded.MenuBook,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("paragraph_practice_screen") }
                        
                        Spacer(Modifier.width(14.dp))

                        QuickActionCardModern(
                            title = "Deneme",
                            subtitle = "Odaklan & çöz",
                            icon = Icons.AutoMirrored.Rounded.Assignment,
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("practice_exam_screen") }
                    }
                } else {
                    // Paragraf gizliyse, geniş Deneme kartı göster
                    WideExamCardNeuralLux { navController.navigate("practice_exam_screen") }
                }

                Spacer(Modifier.height(90.dp))
            }
        }
    }
}

@Composable
private fun NeuralLuxHeroCard(
    title: String,
    message: String,
    solved: Int,
    target: Int,
    darkMode: Boolean
) {
    val cs = MaterialTheme.colorScheme
    val progress = (solved.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "neuralHero")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
        label = "sweep"
    )

    val gradient = Brush.linearGradient(
        colors = if (darkMode) {
            listOf(Color(0xFF111827), Color(0xFF0B1220), Color(0xFF020617))
        } else {
            listOf(cs.primary, cs.secondary, cs.tertiary)
        },
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(36.dp))
                .background(gradient)
                .border(1.2.dp, Color.White.copy(alpha = if (darkMode) 0.14f else 0.16f), RoundedCornerShape(36.dp))
        ) {
            NeuralLuxNeuralMesh(
                modifier = Modifier.fillMaxSize(),
                nodeColor = Color(0xFF00E5FF).copy(alpha = 0.22f),
                lineColor = Color.White.copy(alpha = 0.10f),
                sweep = sweep
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                Text(
                    text = "YAPAY ZEKA DESTEKLİ ÖĞRENME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = message,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = title,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(
                        color = Color.White,
                        shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 12f)
                    ),
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.weight(1f))

                NeuralLuxProgressPill(progress = progress, solved = solved, target = target)
            }
        }
    }
}

@Composable
private fun NeuralLuxProgressPill(progress: Float, solved: Int, target: Int) {
    val pct = (progress * 100).roundToInt().coerceIn(0, 100)
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))))
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "$pct% • $solved/$target",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun NeuralLuxSignalPill(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.14f),
        modifier = Modifier
            .padding(start = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.92f),
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
private fun NeuralLuxHoloGrid(modifier: Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val step = 42.dp.toPx()
        val stroke = 1.dp.toPx()
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = tint,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = stroke
            )
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = tint,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = stroke
            )
            y += step
        }
    }
}

@Composable
private fun NeuralLuxNeuralMesh(
    modifier: Modifier,
    nodeColor: Color,
    lineColor: Color,
    sweep: Float
) {
    Canvas(modifier = modifier) {
        val rand = Random(1337)

        val nodes = List(18) {
            Offset(rand.nextFloat() * size.width, rand.nextFloat() * size.height)
        }

        // Connections
        for (i in 0 until nodes.size) {
            val a = nodes[i]
            for (j in i + 1 until nodes.size) {
                if (rand.nextFloat() < 0.12f) {
                    val b = nodes[j]
                    drawLine(
                        color = lineColor,
                        start = a,
                        end = b,
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }

        // Sweep highlight
        val sweepX = size.width * sweep
        val sweepPath = Path().apply {
            moveTo(sweepX - 140.dp.toPx(), 0f)
            lineTo(sweepX, 0f)
            lineTo(sweepX + 140.dp.toPx(), size.height)
            lineTo(sweepX - 140.dp.toPx(), size.height)
            close()
        }
        drawPath(
            path = sweepPath,
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, nodeColor.copy(alpha = 0.55f), Color.Transparent),
                start = Offset(sweepX - 200.dp.toPx(), 0f),
                end = Offset(sweepX + 200.dp.toPx(), size.height)
            ),
            alpha = 0.8f
        )

        // Nodes
        nodes.forEach { p ->
            drawCircle(color = nodeColor, radius = 2.4.dp.toPx(), center = p)
            drawCircle(
                color = nodeColor.copy(alpha = 0.20f),
                radius = 10.dp.toPx(),
                center = p,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}


@Composable
fun WideExamCardNeuralLux(
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val interfaceStyle = LocalInterfaceStyle.current
    val cornerRadius = InterfaceParams.getCornerRadius(interfaceStyle).dp
    val elevation = InterfaceParams.getCardElevation(interfaceStyle).dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
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
                            Color(0xFFFFD54F).copy(alpha = 0.12f),
                            cs.surface,
                            Color(0xFF00E5FF).copy(alpha = 0.08f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(26.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol taraf - İkon
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(cornerRadius * 0.8f))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.25f),
                                    Color(0xFFFFA726).copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color(0xFFFFD54F).copy(alpha = 0.3f),
                            RoundedCornerShape(cornerRadius * 0.8f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Assignment,
                        null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(44.dp)
                    )
                }
                
                Spacer(Modifier.width(22.dp))
                
                // Sağ taraf - Bilgiler
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Deneme Sınavı",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface,
                        letterSpacing = 0.3.sp
                    )
                    Text(
                        text = "Gerçek sınav ortamında performansınızı test edin",
                        fontSize = 13.sp,
                        color = cs.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD54F))
                            )
                            Text(
                                "Zamanlı Sınav",
                                fontSize = 11.sp,
                                color = cs.onSurface.copy(alpha = 0.65f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF))
                            )
                            Text(
                                "Detaylı Analiz",
                                fontSize = 11.sp,
                                color = cs.onSurface.copy(alpha = 0.65f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Icon(
                    Icons.Rounded.ChevronRight,
                    null,
                    tint = cs.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
