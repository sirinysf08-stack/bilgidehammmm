package com.example.bilgideham

import android.app.Application
import android.util.Log

class BilgidehamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("AI_DIAG", "BilgidehamApp started ✅")

        // ❌ QuestionStock.init(this) -> SİLİNDİ (Artık yok)

        // ✅ Veritabanını uygulama açılır açılmaz başlat
        // (Bunu buraya koyarsan MainActivity'deki init satırını silebilirsin,
        // ama orada kalsa da sorun olmaz, iki kere başlatmak zarar vermez.)
        HistoryRepository.init(this)
    }
}