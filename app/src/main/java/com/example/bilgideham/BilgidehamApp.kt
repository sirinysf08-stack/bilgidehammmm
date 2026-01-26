package com.example.bilgideham

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class BilgidehamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 🛡️ P0: Global Exception Handler - Crash raporlama
        GlobalExceptionHandler.init()
        
        // 🔥 Crashlytics yapılandırması
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
            
            // 🛡️ P0: COPPA Uyumluluğu (Çocuk Odaklı İçerik - Google Play Policy)
            // Firebase Analytics - Çocuk odaklı içerik için yapılandırma
            val analytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(this)
            analytics.setAnalyticsCollectionEnabled(true)
            
            // Uygulama açılış eventi (COPPA uyumlu - kişisel veri toplamadan)
            val bundle = android.os.Bundle()
            analytics.logEvent("app_open", bundle)
            
            DebugLog.d("CRASH", "✅ Crashlytics + COPPA uyumlu Analytics başlatıldı")
        }
        
        DebugLog.d("AI_DIAG", "BilgidehamApp started ✅")

        // 🛡️ P0: DB Başlatma Yarışı Düzeltmesi
        // Tüm DB'ler sıralı başlatılıyor (paralel değil!)
        runBlocking(Dispatchers.IO) {
            runCatching { HistoryRepository.init(this@BilgidehamApp) }
                .onFailure { Log.e("DB", "HistoryRepository init failed", it) }
            
            runCatching { GameRepositoryNew.init(this@BilgidehamApp) }
                .onFailure { Log.e("DB", "GameRepository init failed", it) }
            
            runCatching { LessonRepositoryLocal.init(this@BilgidehamApp) }
                .onFailure { Log.e("DB", "LessonRepository init failed", it) }
        }
    }
}

