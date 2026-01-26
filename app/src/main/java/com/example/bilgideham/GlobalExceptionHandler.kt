package com.example.bilgideham

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Global Uncaught Exception Handler
 * 
 * Yakalanmamış tüm exception'ları loglar ve Crashlytics'e gönderir.
 * Uygulamanın crash olmasını ENGELLEMEZ, sadece crash bilgisini toplar.
 */
object GlobalExceptionHandler : Thread.UncaughtExceptionHandler {
    
    private const val TAG = "GlobalExceptionHandler"
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var isInitialized = false
    
    /**
     * Uygulama başlangıcında çağrılmalı (Application.onCreate)
     */
    fun init() {
        if (isInitialized) return
        
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        isInitialized = true
        
        DebugLog.d(TAG, "✅ Global Exception Handler başlatıldı")
    }
    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // Hata detaylarını logla
            Log.e(TAG, "❌ UNCAUGHT EXCEPTION on thread: ${thread.name}", throwable)
            Log.e(TAG, "❌ Exception type: ${throwable.javaClass.simpleName}")
            Log.e(TAG, "❌ Message: ${throwable.message}")
            
            // Crashlytics'e gönder (Firebase yapılandırılmışsa)
            runCatching {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.log("Thread: ${thread.name}")
                crashlytics.log("Exception: ${throwable.javaClass.simpleName}")
                crashlytics.recordException(throwable)
            }.onFailure { e ->
                Log.w(TAG, "Crashlytics gönderilemedi: ${e.message}")
            }
            
            // Breadcrumb - son 5 stack frame'i logla
            throwable.stackTrace.take(5).forEachIndexed { index, frame ->
                Log.e(TAG, "  [$index] ${frame.className}.${frame.methodName}:${frame.lineNumber}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception handler hatası: ${e.message}")
        } finally {
            // Varsayılan handler'a devret (crash oluşsun)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
    
    /**
     * Custom exception'ları Crashlytics'e gönder (crash olmadan)
     */
    fun logException(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
        
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("$tag: $message")
            crashlytics.recordException(throwable)
        }
    }
    
    /**
     * Custom breadcrumb ekle (crash analizinde görünür)
     */
    fun logBreadcrumb(message: String) {
        runCatching {
            FirebaseCrashlytics.getInstance().log(message)
        }
    }
    
    /**
     * Kullanıcı kimliğini Crashlytics'e ata
     */
    fun setUserId(userId: String) {
        runCatching {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        }
    }
}
