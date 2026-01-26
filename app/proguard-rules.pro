# Add project specific ProGuard rules here.
# Bilgideham - Google Play Release Kuralları

# ==================== GENEL AYARLAR ====================
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# ==================== KOTLIN ====================
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# ==================== FIREBASE ====================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# ==================== GOOGLE PLAY BILLING ====================
-keep class com.android.vending.billing.** { *; }
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# ==================== GSON ====================
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== ROOM DATABASE ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== DATA CLASSES ====================
# Uygulama model sınıfları (Firestore için) - DOĞRU PAKET ADI: com.example.bilgideham
-keep class com.example.bilgideham.QuestionModel { *; }
-keep class com.example.bilgideham.QuestionDoc { *; }
-keep class com.example.bilgideham.QuizConfig { *; }
-keep class com.example.bilgideham.ExamItem { *; }
-keep class com.example.bilgideham.UserEducationPrefs { *; }
-keep class com.example.bilgideham.SubjectConfig { *; }
-keep class com.example.bilgideham.UnitConfig { *; }
-keep class com.example.bilgideham.RagKazanim { *; }

# Firestore'un data class alanlarını okuması için tüm alanları koru
-keepclassmembers class com.example.bilgideham.QuestionModel { *; }
-keepclassmembers class com.example.bilgideham.QuestionDoc { *; }
-keepclassmembers class com.example.bilgideham.QuizConfig { *; }
-keepclassmembers class com.example.bilgideham.ExamItem { *; }
-keepclassmembers class com.example.bilgideham.UserEducationPrefs { *; }
-keepclassmembers class com.example.bilgideham.SubjectConfig { *; }
-keepclassmembers class com.example.bilgideham.UnitConfig { *; }
-keepclassmembers class com.example.bilgideham.RagKazanim { *; }

# 🛡️ P1: Kiro Önerisi - Eksik Model Sınıfları
-keep class com.example.bilgideham.ChartQuestionModel { *; }
-keep class com.example.bilgideham.QuestionRepository$DenemeDurumu { *; }
-keep class com.example.bilgideham.QuestionRepository$SystemStats { *; }
-keep class com.example.bilgideham.QuestionRepository$ClassStats { *; }
-keep class com.example.bilgideham.QuestionRepository$SchoolTypeStats { *; }
-keep class com.example.bilgideham.GameQuestionEntity { *; }

# Tüm data class alanlarını koru (Firestore + Gson)
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.Exclude <fields>;
}

# ==================== GSON SERİALİZASYON (SharedPreferences) ====================
# Timetable (Ders Programı)
-keep class com.example.bilgideham.TimetableScreenKt$TimeCell { *; }
-keepclassmembers class com.example.bilgideham.TimetableScreenKt$TimeCell { *; }

# WordHunt (Kelime Avı)
-keep class com.example.bilgideham.DailyWord { *; }
-keep class com.example.bilgideham.WordHuntState { *; }
-keepclassmembers class com.example.bilgideham.DailyWord { *; }
-keepclassmembers class com.example.bilgideham.WordHuntState { *; }

# LevelConfig (Robotic Coding)
-keep class com.example.bilgideham.LevelConfig { *; }
-keepclassmembers class com.example.bilgideham.LevelConfig { *; }

# Generic Gson Type Tokens - ÖNEMLI
-keep class * extends com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keepattributes Signature

# ==================== COMPOSE ====================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ==================== ML KIT ====================
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ==================== CAMERAX ====================
-keep class androidx.camera.** { *; }

# ==================== NEARBY CONNECTIONS ====================
-keep class com.google.android.gms.nearby.** { *; }

# ==================== COROUTINES ====================
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# ==================== COIL ====================
-dontwarn coil.**
-keep class coil.** { *; }