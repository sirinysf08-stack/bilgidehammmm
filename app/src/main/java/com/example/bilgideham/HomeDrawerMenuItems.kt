package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Home Screen Drawer Menu Items and Drawer Contents
 * Shared drawer logic for all themes
 */


// ==================== DRAWER İÇERİKLERİ ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDrawerContent(
    cs: ColorScheme, darkMode: Boolean, brandTitle: String,
    userLevel: EducationLevel, userGrade: Int?, educationPrefs: UserEducationPrefs,
    context: Context, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState,
    onNavigate: (String) -> Unit, onAction: (() -> Unit) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = cs.surface,
        drawerShape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxHeight().width(320.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(resolveTopGradient(cs = cs, darkMode = darkMode))
                ) {
                    HomeStarDustEffect(color = Color.White.copy(alpha = 0.35f))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        AnimatedBrandWordmark(darkMode = darkMode, compact = false, brandTitle = brandTitle)
                        Text(
                            "${userLevel.icon} ${educationPrefs.schoolType.displayName}" + (userGrade?.let { " • $it. Sınıf" } ?: ""),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(20.dp).size(80.dp)) { NeuralBrainAIView() }
                }
                Spacer(Modifier.height(20.dp))
            }
            // Drawer içeriği - ortak fonksiyon
            drawerMenuItems(userLevel, userGrade, educationPrefs, context, scope, drawerState, onNavigate, onAction, isPlayful = false, isClassic = false)
        }
    }
}

 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun NeuralLuxDrawerContent(
     cs: ColorScheme, darkMode: Boolean, brandTitle: String,
     userLevel: EducationLevel, userGrade: Int?, educationPrefs: UserEducationPrefs,
     context: Context, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState,
     onNavigate: (String) -> Unit, onAction: (() -> Unit) -> Unit
 ) {
     val headerBrush = Brush.linearGradient(
         colors = if (darkMode) {
             listOf(Color(0xFF0B1020), Color(0xFF0A1B2E), Color(0xFF071826))
         } else {
             listOf(cs.primary, cs.secondary, cs.primary.copy(alpha = 0.9f))
         },
         start = Offset(0f, 0f),
         end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
     )

     ModalDrawerSheet(
         drawerContainerColor = if (darkMode) Color(0xFF0B1220) else cs.surface,
         drawerShape = RoundedCornerShape(topEnd = 42.dp, bottomEnd = 42.dp),
         modifier = Modifier.fillMaxHeight().width(320.dp)
     ) {
         LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
             item {
                 Box(
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(220.dp)
                         .background(headerBrush)
                 ) {
                     HomeStarDustEffect(color = Color.White.copy(alpha = 0.22f))
                     Box(
                         modifier = Modifier
                             .fillMaxSize()
                             .background(
                                 Brush.verticalGradient(
                                     colors = listOf(
                                         Color.White.copy(alpha = if (darkMode) 0.06f else 0.10f),
                                         Color.Transparent,
                                         Color.Black.copy(alpha = if (darkMode) 0.18f else 0.06f)
                                     )
                                 )
                             )
                     )

                     Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                         AnimatedBrandWordmark(darkMode = darkMode, compact = false, brandTitle = brandTitle)
                         Text(
                             "${userLevel.icon} ${educationPrefs.schoolType.displayName}" + (userGrade?.let { " • $it. Sınıf" } ?: ""),
                             fontSize = 12.sp,
                             color = Color.White.copy(alpha = 0.72f),
                             modifier = Modifier.padding(top = 6.dp)
                         )
                         Spacer(Modifier.height(10.dp))
                         Surface(
                             shape = RoundedCornerShape(999.dp),
                             color = Color.White.copy(alpha = 0.14f)
                         ) {
                             Row(
                                 modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                 verticalAlignment = Alignment.CenterVertically
                             ) {
                                 Box(
                                     modifier = Modifier
                                         .size(8.dp)
                                         .clip(CircleShape)
                                         .background(Color(0xFF00E5FF))
                                 )
                                 Spacer(Modifier.width(10.dp))
                                 Text(
                                     text = if (darkMode) "NEURAL LUX • GECE" else "NEURAL LUX • PREMIUM",
                                     fontSize = 11.sp,
                                     fontWeight = FontWeight.Black,
                                     color = Color.White.copy(alpha = 0.92f),
                                     letterSpacing = 1.2.sp
                                 )
                             }
                         }
                     }

                     Box(
                         modifier = Modifier
                             .align(Alignment.TopEnd)
                             .padding(18.dp)
                             .size(82.dp)
                     ) {
                         NeuralBrainAIView()
                     }
                 }

                 Spacer(Modifier.height(18.dp))
             }

             drawerMenuItems(
                 userLevel,
                 userGrade,
                 educationPrefs,
                 context,
                 scope,
                 drawerState,
                 onNavigate,
                 onAction,
                 isPlayful = false,
                 isClassic = false
             )
         }
     }
 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayfulDrawerContent(
    cs: ColorScheme, darkMode: Boolean, brandTitle: String,
    userLevel: EducationLevel, userGrade: Int?, educationPrefs: UserEducationPrefs,
    context: Context, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState,
    onNavigate: (String) -> Unit, onAction: (() -> Unit) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "drawer")
    // Modern Gradient Header
    val hueShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "hueDrawer"
    )
    val headerBrush = Brush.linearGradient(
        colors = listOf(
            Color.hsv(hueShift, 0.65f, 0.9f),
            Color.hsv((hueShift + 40f) % 360f, 0.7f, 0.95f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    ModalDrawerSheet(
        drawerContainerColor = if (darkMode) Color(0xFF1E293B) else Color(0xFFFFFDF5),
        drawerShape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxHeight().width(320.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(headerBrush)
                ) {
                    // Dekoratif Daireler
                    Box(Modifier.offset(x = (-30).dp, y = (-30).dp).size(150.dp).clip(CircleShape).background(Color.White.copy(0.1f)))
                    Box(Modifier.align(Alignment.BottomEnd).offset(x = 40.dp, y = 40.dp).size(200.dp).clip(CircleShape).background(Color.White.copy(0.1f)))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(60.dp),
                            shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🦄", fontSize = 32.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = brandTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "${userLevel.icon} ${educationPrefs.schoolType.displayName}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            // Drawer içeriği - eğlenceli stil
            drawerMenuItems(userLevel, userGrade, educationPrefs, context, scope, drawerState, onNavigate, onAction, isPlayful = true, isClassic = false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicDrawerContent(
    cs: ColorScheme, darkMode: Boolean, brandTitle: String,
    userLevel: EducationLevel, userGrade: Int?, educationPrefs: UserEducationPrefs,
    context: Context, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState,
    onNavigate: (String) -> Unit, onAction: (() -> Unit) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = cs.surface,
        drawerShape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
        modifier = Modifier.fillMaxHeight().width(280.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                Surface(modifier = Modifier.fillMaxWidth(), color = cs.surfaceVariant) {
                    Column(modifier = Modifier.padding(20.dp).statusBarsPadding()) {
                        Text(brandTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${educationPrefs.schoolType.displayName}" + (userGrade?.let { " - $it. Sınıf" } ?: ""),
                            fontSize = 13.sp,
                            color = cs.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
            }
            // Drawer içeriği - klasik stil
            drawerMenuItems(userLevel, userGrade, educationPrefs, context, scope, drawerState, onNavigate, onAction, isPlayful = false, isClassic = true)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.drawerMenuItems(
    userLevel: EducationLevel, userGrade: Int?, educationPrefs: UserEducationPrefs,
    context: Context, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState,
    onNavigate: (String) -> Unit, onAction: (() -> Unit) -> Unit,
    isPlayful: Boolean, isClassic: Boolean
) {
    // Ana Sayfa
    item {
        if (isPlayful) PlayfulDrawerItem("🏠", "Ana Sayfa", Color(0xFF4ECDC4)) { onAction { } }
        else if (isClassic) ClassicDrawerItem("Ana Sayfa", Icons.Rounded.Home) { onAction { } }
        else ColorfulDrawerItem("Ana Sayfa", Icons.Rounded.Home, Color(0xFF1976D2)) { onAction { } }
    }
    // YAPAY ZEKA
    item {
        if (isPlayful) {
            PlayfulSectionTitle("🤖 Yapay Zeka")
            PlayfulDrawerItem("🔍", "Yol Gösterici", Color(0xFFAB47BC)) { onNavigate("scan_solve") }
            PlayfulDrawerItem("🎤", "Yapay Zeka Sözlüsü", Color(0xFFEC407A)) { onNavigate("ai_oral_exam") }
            PlayfulDrawerItem("📜", when { userLevel == EducationLevel.ILKOKUL -> "Tarihle Öğrenelim"; userGrade == 5 -> "Tarih Hikayeleri"; else -> "Tarihle Sohbet" }, Color(0xFF7E57C2)) { onNavigate("history_chat") }
            PlayfulDrawerItem("👨‍🏫", if (userLevel == EducationLevel.ILKOKUL) "Öğretmenim Ol!" else "Öğretmen Sensin!", Color(0xFF26A69A)) { onNavigate("be_the_teacher") }
            PlayfulDrawerItem("✏️", when { userLevel == EducationLevel.ILKOKUL -> "Yazı Yazalım!"; userGrade == 5 -> "Paragraf Yazma"; else -> "Kompozisyon" }, Color(0xFFEF5350)) { onNavigate("composition_fixer") }
        } else if (isClassic) {
            ClassicSectionTitle("Yapay Zeka")
            ClassicDrawerItem("Yol Gösterici", Icons.Rounded.Search) { onNavigate("scan_solve") }
            ClassicDrawerItem("Yapay Zeka Sözlüsü", Icons.Rounded.Mic) { onNavigate("ai_oral_exam") }
            ClassicDrawerItem(when { userLevel == EducationLevel.ILKOKUL -> "Tarihle Öğrenelim"; userGrade == 5 -> "Tarih Hikayeleri"; else -> "Tarihle Sohbet" }, Icons.Rounded.HistoryEdu) { onNavigate("history_chat") }
            ClassicDrawerItem(if (userLevel == EducationLevel.ILKOKUL) "Öğretmenim Ol!" else "Öğretmen Sensin!", Icons.Rounded.CastForEducation) { onNavigate("be_the_teacher") }
            ClassicDrawerItem(when { userLevel == EducationLevel.ILKOKUL -> "Yazı Yazalım!"; userGrade == 5 -> "Paragraf Yazma"; else -> "Kompozisyon Düzeltici" }, Icons.Rounded.Edit) { onNavigate("composition_fixer") }
        } else {
            DrawerSectionTitle(when (userLevel) { EducationLevel.ILKOKUL -> "Yapay Zeka Yardımcım 🤖"; EducationLevel.KPSS -> "KPSS AI Asistanı 🎯"; else -> "Yapay Zeka Laboratuvarı ✨" }, Color(0xFF9C27B0))
            ColorfulDrawerItem("Yol Gösterici (AI)", Icons.Rounded.Search, Color(0xFFAB47BC)) { onNavigate("scan_solve") }
            ColorfulDrawerItem("Yapay Zeka Sözlüsü", Icons.Rounded.Mic, Color(0xFFEC407A)) { onNavigate("ai_oral_exam") }
            ColorfulDrawerItem(when { userLevel == EducationLevel.ILKOKUL -> "Tarihle Öğrenelim"; userGrade == 5 -> "Tarih Hikayeleri"; else -> "Tarihle Sohbet" }, Icons.Rounded.HistoryEdu, Color(0xFF7E57C2)) { onNavigate("history_chat") }
            ColorfulDrawerItem(if (userLevel == EducationLevel.ILKOKUL) "Öğretmenim Ol!" else "Öğretmen Sensin!", Icons.Rounded.CastForEducation, Color(0xFF26A69A)) { onNavigate("be_the_teacher") }
            ColorfulDrawerItem(when { userLevel == EducationLevel.ILKOKUL -> "Yazı Yazalım!"; userGrade == 5 -> "Paragraf Yazma"; else -> "Kompozisyon Düzeltici" }, Icons.Rounded.Edit, Color(0xFFEF5350)) { onNavigate("composition_fixer") }
            HorizontalDivider(Modifier.padding(vertical = 12.dp, horizontal = 20.dp).alpha(0.1f))
        }
    }
    // DİL DÜNYASI
    item {
        if (isPlayful) {
            PlayfulSectionTitle("🌍 Dil Dünyası")
            PlayfulDrawerItem("💬", if (userLevel == EducationLevel.ILKOKUL) "İngilizce Arkadaşım" else "English Buddy", Color(0xFF00ACC1)) { onNavigate("english_chat_buddy") }
            PlayfulDrawerItem("🗣️", "Aksan Koçu", Color(0xFF00BCD4)) { onNavigate("accent_coach") }
            if (educationPrefs.schoolType.name.contains("IMAM_HATIP")) PlayfulDrawerItem("🕌", "Arapça Hafız", Color(0xFF2E7D32)) { onNavigate("arabic_coach") }
            PlayfulDrawerItem("🎯", "Kelime Avı", Color(0xFF00897B)) { onNavigate("word_hunt") }
        } else if (isClassic) {
            ClassicSectionTitle("Dil Becerileri")
            ClassicDrawerItem(if (userLevel == EducationLevel.ILKOKUL) "İngilizce Arkadaşım" else "English Chat Buddy", Icons.Rounded.ChatBubble) { onNavigate("english_chat_buddy") }
            ClassicDrawerItem("Aksan Koçu", Icons.Rounded.RecordVoiceOver) { onNavigate("accent_coach") }
            if (educationPrefs.schoolType.name.contains("IMAM_HATIP")) ClassicDrawerItem("Arapça Hafız", Icons.Rounded.Translate) { onNavigate("arabic_coach") }
            ClassicDrawerItem("Kelime Avı", Icons.Rounded.Extension) { onNavigate("word_hunt") }
        } else {
            DrawerSectionTitle(when (userLevel) { EducationLevel.ILKOKUL -> "İngilizce Köşesi 🇬🇧"; EducationLevel.KPSS -> "Dil Becerileri 📚"; else -> "Dil Dünyası 🌍" }, Color(0xFF0097A7))
            ColorfulDrawerItem(if (userLevel == EducationLevel.ILKOKUL) "İngilizce Arkadaşım" else "English Chat Buddy", Icons.Rounded.ChatBubble, Color(0xFF00ACC1)) { onNavigate("english_chat_buddy") }
            ColorfulDrawerItem("İngilizce Aksan Koçu", Icons.Rounded.RecordVoiceOver, Color(0xFF00BCD4)) { onNavigate("accent_coach") }
            if (educationPrefs.schoolType.name.contains("IMAM_HATIP")) ColorfulDrawerItem("Arapça Hafız", Icons.Rounded.Translate, Color(0xFF2E7D32)) { onNavigate("arabic_coach") }
            ColorfulDrawerItem("Kelime Avı", Icons.Rounded.Extension, Color(0xFF00897B)) { onNavigate("word_hunt") }
            HorizontalDivider(Modifier.padding(vertical = 12.dp, horizontal = 20.dp).alpha(0.1f))
        }
    }
    // ÖĞRENCİ ÇANTASI
    item {
        if (isPlayful) {
            PlayfulSectionTitle("🎒 Okul Çantam")
            PlayfulDrawerItem("⏰", when (userLevel) { EducationLevel.ORTAOKUL -> if (userGrade == 8) "LGS Sayacı" else "Sınav Sayacı"; EducationLevel.LISE -> "YKS Sayacı"; EducationLevel.KPSS -> "KPSS Sayacı"; else -> "Sınav Sayacı" }, Color(0xFFFF7043)) { onNavigate("exam_countdown") }
            PlayfulDrawerItem("📅", "Ders Programım", Color(0xFFFFA726)) { onNavigate("timetable") }
            PlayfulDrawerItem("📚", "Kitap Kurdu", Color(0xFFFFCA28)) { onNavigate("book_worm") }
            PlayfulDrawerItem("📖", "Akıllı Sözlük", Color(0xFF8D6E63)) { onNavigate("ai_dictionary") }
            if (userLevel != EducationLevel.ILKOKUL) PlayfulDrawerItem("🗺️", "Coğrafya Atlası", Color(0xFF66BB6A)) { onNavigate("atlas") }
            PlayfulDrawerItem("💡", "Günün Bilimi", Color(0xFFFDD835)) { onNavigate("science_fact") }
        } else if (isClassic) {
            ClassicSectionTitle(when (userLevel) { EducationLevel.KPSS -> "KPSS Hazırlık"; EducationLevel.LISE -> "YKS Hazırlık"; else -> "Öğrenci Araçları" })
            ClassicDrawerItem(when (userLevel) { EducationLevel.ORTAOKUL -> if (userGrade == 8) "LGS Geri Sayım" else "Sınav Geri Sayım"; EducationLevel.LISE -> "YKS Geri Sayım"; EducationLevel.KPSS -> "KPSS Geri Sayım"; else -> "Sınav Geri Sayım" }, Icons.Rounded.Timer) { onNavigate("exam_countdown") }
            ClassicDrawerItem("Ders Programı", Icons.Rounded.DateRange) { onNavigate("timetable") }
            ClassicDrawerItem("Kitap Takibi", Icons.AutoMirrored.Rounded.MenuBook) { onNavigate("book_worm") }
            ClassicDrawerItem("Sözlük", Icons.Rounded.Translate) { onNavigate("ai_dictionary") }
            if (userLevel != EducationLevel.ILKOKUL) ClassicDrawerItem("Coğrafya Atlası", Icons.Rounded.Map) { onNavigate("atlas") }
            ClassicDrawerItem("Günün Bilimi", Icons.Rounded.Lightbulb) { onNavigate("science_fact") }
        } else {
            DrawerSectionTitle(when (userLevel) { EducationLevel.ILKOKUL -> "Okul Çantam 🎒"; EducationLevel.KPSS -> "KPSS Hazırlık 📋"; EducationLevel.LISE -> "YKS Hazırlık 🎯"; else -> "Öğrenci Çantası 🎒" }, Color(0xFFFF6F00))
            ColorfulDrawerItem(when (userLevel) { EducationLevel.ORTAOKUL -> if (userGrade == 8) "LGS Geri Sayım" else "Sınav Geri Sayım"; EducationLevel.LISE -> "YKS Geri Sayım"; EducationLevel.KPSS -> "KPSS Geri Sayım"; else -> "Sınav Geri Sayım" }, Icons.Rounded.Timer, Color(0xFFFF7043)) { onNavigate("exam_countdown") }
            ColorfulDrawerItem("Ders Programım", Icons.Rounded.DateRange, Color(0xFFFFA726)) { onNavigate("timetable") }
            ColorfulDrawerItem("Kitap Kurdu", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFFFFCA28)) { onNavigate("book_worm") }
            ColorfulDrawerItem("Akıllı Sözlük", Icons.Rounded.Translate, Color(0xFF8D6E63)) { onNavigate("ai_dictionary") }
            if (userLevel != EducationLevel.ILKOKUL) ColorfulDrawerItem("Coğrafya Atlası", Icons.Rounded.Map, Color(0xFF66BB6A)) { onNavigate("atlas") }
            ColorfulDrawerItem("Günün Bilimi", Icons.Rounded.Lightbulb, Color(0xFFFDD835)) { onNavigate("science_fact") }
            HorizontalDivider(Modifier.padding(vertical = 12.dp, horizontal = 20.dp).alpha(0.1f))
        }
    }
    // OYUNLAR - Sadece ilkokul ve ortaokul
    if (userLevel == EducationLevel.ILKOKUL || userLevel == EducationLevel.ORTAOKUL) {
        item {
            if (isPlayful) {
                PlayfulSectionTitle("🎮 Oyun Zamanı!")
                PlayfulDrawerItem("🎮", "Oyunlar & Kodlama", Color(0xFF43A047)) { onNavigate("games") }
            } else if (isClassic) {
                ClassicSectionTitle("Eğitici Oyunlar")
                ClassicDrawerItem("Oyunlar", Icons.Rounded.SportsEsports) { onNavigate("games") }
            } else {
                DrawerSectionTitle("Teneffüs Zamanı 🎮", Color(0xFF2E7D32))
                ColorfulDrawerItem("Oyunlar & Robo-Kodlama", Icons.Rounded.SportsEsports, Color(0xFF43A047)) { onNavigate("games") }
                HorizontalDivider(Modifier.padding(vertical = 12.dp, horizontal = 20.dp).alpha(0.1f))
            }
        }
    }
    // ANALİZ & RAPOR
    item {
        if (isPlayful) {
            PlayfulSectionTitle("📊 Raporlarım")
            PlayfulDrawerItem("📊", "Öğrenci Analizi", Color(0xFF5E35B1)) { onNavigate("detailed_analytics") }
            PlayfulDrawerItem("📈", "Hata Analizi", Color(0xFF1976D2)) { onNavigate("progress") }
            PlayfulDrawerItem("📋", "Geçmiş Sorular", Color(0xFF283593)) { onNavigate("history") }
        } else if (isClassic) {
            ClassicSectionTitle("Analiz & Rapor")
            ClassicDrawerItem("Öğrenci Analizi", Icons.Rounded.Insights) { onNavigate("detailed_analytics") }
            ClassicDrawerItem("Hata Analizi", Icons.Rounded.Analytics) { onNavigate("progress") }
            ClassicDrawerItem("Geçmiş Sorular", Icons.Rounded.History) { onNavigate("history") }
        } else {
            DrawerSectionTitle("Analiz & Rapor 📈", Color(0xFF1565C0))
            ColorfulDrawerItem("Öğrenci Analizi", Icons.Rounded.Insights, Color(0xFF5E35B1)) { onNavigate("detailed_analytics") }
            ColorfulDrawerItem("Hata Analizi", Icons.Rounded.Analytics, Color(0xFF1976D2)) { onNavigate("progress") }
            ColorfulDrawerItem("Geçmiş Sorular", Icons.Rounded.History, Color(0xFF283593)) { onNavigate("history") }
            HorizontalDivider(Modifier.padding(vertical = 12.dp, horizontal = 20.dp).alpha(0.1f))
        }
    }
    // --- VELİ GİRİŞİ (Sadece İlkokul, Ortaokul, Lise - KPSS/AGS hariç) ---
    if (userLevel != EducationLevel.KPSS && userLevel != EducationLevel.AGS) {
        item {
            if (isPlayful) BetaDrawerItemPlayful("🔒", "Veli Girişi", Color(0xFFD32F2F)) { onNavigate("internal://parental_login") }
            else if (isClassic) BetaDrawerItemClassic("Veli Girişi", Icons.Rounded.Lock) { onNavigate("internal://parental_login") }
            else BetaDrawerItemColorful("Veli Girişi", Icons.Rounded.Lock, Color(0xFFD32F2F)) { onNavigate("internal://parental_login") }
        }
    }
    // BOŞLUK
    item { Spacer(Modifier.height(32.dp)) }
    // AYARLAR
    item {
        if (isPlayful) {
            PlayfulSectionTitle("⚙️ Ayarlar")
            PlayfulDrawerItem("⚙️", "Ayarlar", Color(0xFF78909C)) { onNavigate("settings") }
            DrawerThemeSelector(context = context, onThemeChanged = { scope.launch { drawerState.close() } })
            PlayfulDrawerItem("🔄", "Seviye Değiştir", Color(0xFF78909C)) { onNavigate("level_selection") }
            PlayfulDrawerItem("🐛", "Hata Bildir", Color(0xFF78909C)) { onAction { openBugReportEmail(context, "2.0.0") } }
        } else if (isClassic) {
            ClassicSectionTitle("Sistem")
            DrawerThemeSelector(context = context, onThemeChanged = { scope.launch { drawerState.close() } })
            ClassicDrawerItem("Seviye Değiştir", Icons.Rounded.SwapHoriz) { onNavigate("level_selection") }
            ClassicDrawerItem("Ayarlar", Icons.Rounded.Settings) { onNavigate("settings") }
            ClassicDrawerItem("Hata Bildir", Icons.Rounded.BugReport) { onAction { openBugReportEmail(context, "2.0.0") } }
        } else {
            DrawerSectionTitle("Sistem & Yapılandırma", Color(0xFF546E7A))
            DrawerThemeSelector(context = context, onThemeChanged = { scope.launch { drawerState.close() } })
            ColorfulDrawerItem("Seviye Değiştir", Icons.Rounded.SwapHoriz, Color(0xFF78909C)) { onNavigate("level_selection") }
            ColorfulDrawerItem("Ayarlar", Icons.Rounded.Settings, Color(0xFF78909C)) { onNavigate("settings") }
            ColorfulDrawerItem("Hata Bildir", Icons.Rounded.BugReport, Color(0xFF78909C)) { onAction { openBugReportEmail(context, "2.0.0") } }
        }
        Spacer(Modifier.height(20.dp))
    }
    item { Spacer(Modifier.height(40.dp)) }
}


// Klasik drawer bileşenleri
@Composable
fun ClassicSectionTitle(title: String) {
    val cs = MaterialTheme.colorScheme
    Text(title, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = cs.onSurface.copy(alpha = 0.5f))
}

@Composable
fun ClassicDrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = cs.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(22.dp)); Spacer(Modifier.width(16.dp)); Text(title, fontSize = 14.sp, color = cs.onSurface)
    }
}

