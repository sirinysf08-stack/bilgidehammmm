package com.example.bilgideham

import android.util.Log

/**
 * Debug Log Wrapper
 * Üretim modunda logları devre dışı bırakır
 */
object DebugLog {
    
    // Release modunda false, debug modunda true
    // BuildConfig yerine basit kontrol
    private val ENABLE_LOGS: Boolean
        get() = try {
            // Reflection ile BuildConfig.DEBUG'ı kontrol et
            val buildConfigClass = Class.forName("com.example.bilgideham.BuildConfig")
            val debugField = buildConfigClass.getField("DEBUG")
            debugField.getBoolean(null)
        } catch (e: Exception) {
            false // Hata durumunda false döndür (üretim modu)
        }
    
    fun d(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.d(tag, message)
        }
    }
    
    fun i(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.i(tag, message)
        }
    }
    
    fun w(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.w(tag, message)
        }
    }
    
    fun e(tag: String, message: String) {
        // Error logları her zaman göster (crash analizi için)
        Log.e(tag, message)
    }
    
    fun v(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.v(tag, message)
        }
    }
}
