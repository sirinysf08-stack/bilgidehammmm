package com.example.bilgideham

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Classic Theme UI Components for HomeScreen
 * Simple and professional design
 */

// ==================== KLASİK ARAYÜZ (Sade, profesyonel) ====================

@Composable
fun ClassicHeader(brandTitle: String, darkMode: Boolean, onMenuClick: () -> Unit, onToggleTheme: () -> Unit, onToggleBrightness: () -> Unit, currentBrightness: Int, onSecretTap: () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), color = cs.surface, shadowElevation = 2.dp) {
        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) { Icon(Icons.Rounded.Menu, "Menü", tint = cs.onSurface) }
            Text(brandTitle, modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
            IconButton(onClick = onToggleTheme) { Icon(if (darkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode, "Tema", tint = cs.onSurface) }
            IconButton(onClick = onToggleBrightness) { Icon(Icons.Rounded.Brightness6, "Parlaklık", tint = cs.onSurface) }
        }
    }
}

@Composable
fun ClassicHomeContent(padding: PaddingValues, navController: NavController, context: Context, darkMode: Boolean, solvedToday: Int, dailyTarget: Int, randomMessage: String, randomPunch: String) {
    val cs = MaterialTheme.colorScheme
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val subjects = remember(educationPrefs) { AppPrefs.getCurrentSubjects(context) }
    val progress = (solvedToday.toFloat() / dailyTarget).coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(padding).fillMaxSize().background(cs.background).verticalScroll(rememberScrollState())) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), elevation = CardDefaults.cardElevation(1.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.AutoMirrored.Rounded.TrendingUp, null, tint = cs.primary); Spacer(Modifier.width(8.dp)); Text("Günlük İlerleme", fontWeight = FontWeight.SemiBold, color = cs.onSurface) }
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = cs.primary, trackColor = cs.surfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text("$solvedToday / $dailyTarget soru tamamlandı", fontSize = 13.sp, color = cs.onSurface.copy(alpha = 0.6f))
            }
        }
        // Dersler (Paragraf hariç - Paragraf hızlı erişimde var)
        Column(modifier = Modifier.padding(16.dp)) {
            subjects.filter { !it.id.contains("paragraf", ignoreCase = true) }.forEach { subject -> 
                ClassicSubjectItem(subject = subject, onClick = { navController.navigate(subject.route) })
                HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.5f))
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Hızlı Erişim", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = cs.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(8.dp))
            
            val showParagraf = educationPrefs.schoolType != SchoolType.AGS_OABT || AppPrefs.getOabtField(context) == "turkce"
            val showDuel = educationPrefs.level != EducationLevel.KPSS && educationPrefs.level != EducationLevel.AGS
            
            if (showParagraf) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClassicQuickButton(title = "Paragraf", icon = Icons.AutoMirrored.Rounded.MenuBook, modifier = Modifier.weight(1f)) { navController.navigate("paragraph_practice_screen") }
                    ClassicQuickButton(title = "Deneme", icon = Icons.AutoMirrored.Rounded.Assignment, modifier = Modifier.weight(1f)) { navController.navigate("practice_exam_screen") }
                    // Düello - KPSS ve AGS hariç
                    if (showDuel) {
                        ClassicQuickButton(title = "Düello", icon = Icons.Rounded.Groups, modifier = Modifier.weight(1f)) { navController.navigate("class_duel") }
                    }
                }
            } else {
                // Paragraf gizliyse, geniş Deneme kartı göster
                WideExamCardClassic { navController.navigate("practice_exam_screen") }
                if (showDuel) {
                    Spacer(Modifier.height(12.dp))
                    ClassicQuickButton(title = "Sınıf Düellosu", icon = Icons.Rounded.Groups, modifier = Modifier.fillMaxWidth()) { navController.navigate("class_duel") }
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun ClassicSubjectItem(subject: SubjectConfig, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val subjectColor = Color(subject.colorHex)
    val fontSize = if (subject.displayName.contains("Peygamberimizin Hayatı", ignoreCase = true)) 13.sp else 15.sp
    
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = subjectColor.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(subject.icon, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                subject.displayName,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                color = cs.onSurface
            )
            Text(
                subject.description,
                fontSize = 12.sp,
                color = cs.onSurface.copy(alpha = 0.5f),
                maxLines = 1
            )
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = cs.onSurface.copy(alpha = 0.3f))
    }
}

@Composable
fun ClassicQuickButton(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    OutlinedCard(onClick = onClick, modifier = modifier.height(70.dp), shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = cs.primary, modifier = Modifier.size(22.dp)); Spacer(Modifier.height(4.dp)); Text(title, fontSize = 12.sp, color = cs.onSurface)
        }
    }
}

@Composable
fun WideExamCardClassic(
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(cs.primary.copy(alpha = 0.05f))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf - İkon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = cs.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(70.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Assignment,
                        null,
                        tint = cs.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(18.dp))
            
            // Orta - Bilgiler
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Deneme Sınavı",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )
                Text(
                    text = "Gerçek sınav ortamında test olun",
                    fontSize = 13.sp,
                    color = cs.onSurface.copy(alpha = 0.6f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        "⏱️ Zamanlı",
                        fontSize = 11.sp,
                        color = cs.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        "📊 Analiz",
                        fontSize = 11.sp,
                        color = cs.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Icon(
                Icons.Rounded.ChevronRight,
                null,
                tint = cs.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
