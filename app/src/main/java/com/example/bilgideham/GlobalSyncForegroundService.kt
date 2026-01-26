package com.example.bilgideham

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 24/7 Global Soru Eşitleme Servisi
 * 
 * Özellikler:
 * - Uygulama kapansa bile çalışır
 * - Telefon uyusa bile çalışır (WakeLock)
 * - Crash olursa otomatik devam eder
 * - Notification ile ilerleme gösterir
 * - Battery optimization bypass
 */
class GlobalSyncForegroundService : Service() {

    companion object {
        private const val TAG = "GlobalSyncService"
        private const val NOTIFICATION_ID = 9001
        private const val CHANNEL_ID = "global_sync_channel"
        
        private val isRunning = AtomicBoolean(false)
        private var serviceJob: Job? = null
        private var wakeLock: PowerManager.WakeLock? = null
        
        // İstatistikler
        val totalQuestionsAdded = AtomicInteger(0)
        val currentRound = AtomicInteger(0)
        var currentStatus = "Hazırlanıyor..."
        
        // Servis kontrolü
        fun start(context: Context, selectedLevel: EducationLevel? = null) {
            val intent = Intent(context, GlobalSyncForegroundService::class.java).apply {
                putExtra("SELECTED_LEVEL", selectedLevel?.name)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            isRunning.set(false)
            serviceJob?.cancel()
            context.stopService(Intent(context, GlobalSyncForegroundService::class.java))
        }
        
        fun isServiceRunning(): Boolean = isRunning.get()
    }

    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🚀 GlobalSyncForegroundService onCreate")
        
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        
        // WakeLock al (telefon uyusa bile çalışsın)
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "BilgiDeham::GlobalSyncWakeLock"
        ).apply {
            acquire(24 * 60 * 60 * 1000L) // 24 saat
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "📱 onStartCommand called")
        
        // STOP action kontrolü
        if (intent?.action == "STOP") {
            Log.d(TAG, "🛑 STOP action alındı")
            isRunning.set(false)
            stopSelf()
            return START_NOT_STICKY
        }
        
        val selectedLevelName = intent?.getStringExtra("SELECTED_LEVEL")
        val selectedLevel = selectedLevelName?.let { 
            EducationLevel.entries.find { it.name == selectedLevelName }
        }
        
        // Foreground notification başlat
        startForeground(NOTIFICATION_ID, createNotification("Başlatılıyor...", 0, 0))
        
        // Eğer zaten çalışıyorsa tekrar başlatma
        if (isRunning.get()) {
            Log.d(TAG, "⚠️ Servis zaten çalışıyor")
            return START_STICKY
        }
        
        isRunning.set(true)
        
        // Ana eşitleme işini başlat
        serviceJob = serviceScope.launch {
            try {
                startGlobalSync(selectedLevel)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Servis hatası: ${e.message}", e)
                // Hata olursa 10 saniye bekle ve tekrar başlat
                delay(10000)
                if (isRunning.get()) {
                    startGlobalSync(selectedLevel)
                }
            }
        }
        
        // START_STICKY: Sistem servisi kapatırsa otomatik yeniden başlat
        return START_STICKY
    }

    private suspend fun startGlobalSync(selectedLevel: EducationLevel?) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "🌍 Global Eşitleme başlıyor...")
            updateNotification("API key'ler yükleniyor...", 0, 0)
            
            // API key'leri yükle
            try {
                GeminiApiProvider.loadKeysFromAssets(applicationContext)
                val keyCount = GeminiApiProvider.getLoadedKeyCount()
                
                if (keyCount == 0) {
                    Log.e(TAG, "❌ API key bulunamadı!")
                    updateNotification("HATA: API key yok", 0, 0)
                    delay(5000)
                    stopSelf()
                    return@withContext
                }
                
                Log.d(TAG, "✅ $keyCount API key yüklendi")
                updateNotification("$keyCount API key yüklendi", 0, 0)
                delay(1000)
            } catch (e: Exception) {
                Log.e(TAG, "❌ API key yükleme hatası: ${e.message}", e)
                updateNotification("HATA: ${e.message?.take(30)}", 0, 0)
                delay(5000)
                stopSelf()
                return@withContext
            }
            
            val keyCount = GeminiApiProvider.getLoadedKeyCount()
            
            // ADIM 1: TÜM DERSLERİ TOPLA
            updateNotification("Dersler taranıyor...", 0, 0)
            
            data class GlobalTarget(
                val level: EducationLevel,
                val schoolType: SchoolType,
                val grade: Int?,
                val subject: SubjectConfig,
                var count: Int
            )
            
            val targetLevels = selectedLevel?.let { listOf(it) } 
                ?: EducationLevel.entries.toList()
            
            val allGlobalTargets = mutableListOf<GlobalTarget>()
            
            try {
                for (level in targetLevels) {
                    if (!isRunning.get()) break
                    
                    Log.d(TAG, "📚 ${level.displayName} taranıyor...")
                    updateNotification("${level.displayName} taranıyor...", 0, 0)
                    
                    val schoolTypes = CurriculumManager.getSchoolTypesFor(level)
                    
                    for (schoolType in schoolTypes) {
                        if (!isRunning.get()) break
                        
                        val grades = if (schoolType.grades.isEmpty()) {
                            listOf<Int?>(null)
                        } else {
                            schoolType.grades.map { it as Int? }
                        }
                        
                        for (grade in grades) {
                            if (!isRunning.get()) break
                            
                            try {
                                val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                                val counts = QuestionRepository.getQuestionCountsForLevel(level, schoolType, grade)
                                
                                for (subj in subjects) {
                                    val count = counts[subj.id] ?: 0
                                    allGlobalTargets.add(
                                        GlobalTarget(level, schoolType, grade, subj, count)
                                    )
                                }
                                
                                Log.d(TAG, "   ✓ ${schoolType.displayName}/${grade ?: "G"}: ${subjects.size} ders")
                            } catch (e: Exception) {
                                Log.e(TAG, "   ✗ ${schoolType.displayName}/${grade ?: "G"} hatası: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Ders tarama hatası: ${e.message}", e)
                updateNotification("HATA: Ders tarama başarısız", 0, 0)
                delay(5000)
                stopSelf()
                return@withContext
            }
            
            if (!isRunning.get()) return@withContext
            
            if (allGlobalTargets.isEmpty()) {
                Log.e(TAG, "❌ Hiç ders bulunamadı!")
                updateNotification("HATA: Ders bulunamadı", 0, 0)
                delay(5000)
                stopSelf()
                return@withContext
            }
            
            Log.d(TAG, "✅ Toplam ${allGlobalTargets.size} ders tarandı")
            updateNotification("${allGlobalTargets.size} ders bulundu", 0, 0)
            delay(1000)
            
            // ADIM 2: SONSUZ DÖNGÜ - SÜREKLI EŞİTLE
            var roundCount = 0
            var consecutiveErrors = 0
            val maxConsecutiveErrors = 10
            
            Log.d(TAG, "🔄 Sonsuz döngü başlıyor...")
            updateNotification("Eşitleme başlıyor...", 0, 0)
            
            // İLK SIRALAMA (sadece 1 kez)
            var currentTargets = allGlobalTargets.sortedBy { it.count }.toMutableList()
            
            while (isRunning.get()) {
                roundCount++
                currentRound.set(roundCount)
                
                Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                Log.d(TAG, "🔄 TUR $roundCount BAŞLIYOR")
                
                try {
                    // En düşük N dersi al (zaten sıralı)
                    val targets = currentTargets.take(keyCount)
                    
                    if (targets.isEmpty()) {
                        Log.w(TAG, "⚠️ Hedef ders bulunamadı")
                        updateNotification("Tur $roundCount: Hedef yok, bekleniyor...", roundCount, totalQuestionsAdded.get())
                        delay(30000) // 30 saniye bekle
                        continue
                    }
                    
                    val targetStr = targets.mapIndexed { i, t -> 
                        val emoji = listOf("🔵", "🟢", "🟣", "🟡")[i % 4]
                        "$emoji[${t.level.displayName}]${t.subject.displayName}(${t.count})"
                    }.joinToString(" ")
                    
                    Log.d(TAG, "🎯 HEDEFLER: $targetStr")
                    currentStatus = "Tur $roundCount: ${targets.first().subject.displayName}"
                    updateNotification(currentStatus, roundCount, totalQuestionsAdded.get())
                    
                    // PARALEL ÜRETIM - STAGGERED START
                    val jobs = mutableListOf<Job>()
                    val emojis = listOf("🔵", "🟢", "🟣", "🟡")
                    val updatedCounts = mutableMapOf<Int, Int>() // Index -> Yeni sayı
                    
                    Log.d(TAG, "🚀 Paralel üretim başlıyor...")
                    
                    targets.forEachIndexed { index, target ->
                        jobs += CoroutineScope(Dispatchers.IO).launch {
                            // Staggered start: 0s, 1.5s, 3s, 4.5s
                            val delayMs = index * 1500L
                            if (delayMs > 0) {
                                delay(delayMs)
                            }
                            
                            val emoji = emojis[index % 4]
                            Log.d(TAG, "🎬 $emoji [${target.level.displayName}] ${target.subject.displayName} başlıyor...")
                            
                            // Retry mekanizması (3 deneme)
                            var attempts = 0
                            var success = false
                            var addedCount = 0
                            
                            while (attempts < 3 && !success && isRunning.get()) {
                                attempts++
                                
                                try {
                                    val result = GeminiApiProvider.generateWithKey(
                                        index, 
                                        target.subject.displayName, 
                                        15, 
                                        target.level, 
                                        target.schoolType, 
                                        target.grade
                                    )
                                    
                                    if (result.first.isNotEmpty()) {
                                        val saved = QuestionRepository.saveQuestionsForLevel(
                                            result.first, 
                                            target.level, 
                                            target.schoolType, 
                                            target.grade, 
                                            target.subject.id
                                        )
                                        
                                        addedCount = saved
                                        totalQuestionsAdded.addAndGet(saved)
                                        
                                        // Güncellenen sayıyı kaydet
                                        synchronized(updatedCounts) {
                                            val targetIndex = currentTargets.indexOfFirst { 
                                                it.level == target.level && 
                                                it.schoolType == target.schoolType && 
                                                it.grade == target.grade && 
                                                it.subject.id == target.subject.id 
                                            }
                                            if (targetIndex >= 0) {
                                                updatedCounts[targetIndex] = target.count + saved
                                            }
                                        }
                                        
                                        Log.d(TAG, "✅ $emoji [${target.level.displayName}] ${target.subject.displayName}: +$saved → ${target.count + saved}")
                                        
                                        // Notification güncelle
                                        updateNotification(
                                            "${target.subject.displayName}: +$saved",
                                            roundCount,
                                            totalQuestionsAdded.get()
                                        )
                                        
                                        success = true
                                        consecutiveErrors = 0
                                    } else {
                                        Log.w(TAG, "⚠️ $emoji ${result.second}: ${target.subject.displayName} - 0 soru")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ $emoji ${target.subject.displayName} (deneme $attempts): ${e.message?.take(60)}")
                                    
                                    if (attempts < 3) {
                                        val backoffMs = 3000L * attempts
                                        delay(backoffMs)
                                    }
                                }
                            }
                            
                            if (!success) {
                                consecutiveErrors++
                                Log.e(TAG, "💥 $emoji ${target.subject.displayName} BAŞARISIZ (3/3 deneme)")
                            }
                        }
                    }
                    
                    Log.d(TAG, "⏳ Tüm job'ların bitmesi bekleniyor...")
                    jobs.forEach { it.join() }
                    Log.d(TAG, "✅ Tüm job'lar tamamlandı")
                    
                    // Sadece güncellenen derslerin sayılarını güncelle
                    synchronized(updatedCounts) {
                        updatedCounts.forEach { (index, newCount) ->
                            currentTargets[index] = currentTargets[index].copy(count = newCount)
                        }
                    }
                    
                    // Yeniden sırala (sadece güncellenen dersler için)
                    currentTargets.sortBy { it.count }
                    Log.d(TAG, "🔄 Liste yeniden sıralandı")
                    
                    // Çok fazla ardışık hata varsa uzun bekleme
                    if (consecutiveErrors >= maxConsecutiveErrors) {
                        Log.w(TAG, "⚠️ ÇOK FAZLA HATA: $consecutiveErrors ardışık hata, 5 dakika bekleniyor...")
                        updateNotification("Çok fazla hata, 5dk bekleniyor...", roundCount, totalQuestionsAdded.get())
                        delay(300000) // 5 dakika
                        consecutiveErrors = 0
                        Log.d(TAG, "✅ Bekleme tamamlandı, devam ediliyor...")
                    } else {
                        // Normal bekleme (rate limit için)
                        Log.d(TAG, "⏳ Rate limit için 2sn bekleniyor...")
                        delay(2000)
                    }
                    
                    Log.d(TAG, "🏁 TUR $roundCount TAMAMLANDI (Toplam: ${totalQuestionsAdded.get()} soru)")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ TUR $roundCount HATASI: ${e.message}", e)
                    consecutiveErrors++
                    updateNotification("Tur $roundCount hata: ${e.message?.take(20)}", roundCount, totalQuestionsAdded.get())
                    
                    // Hata durumunda kısa bekleme
                    Log.d(TAG, "⏳ Hata sonrası 10sn bekleniyor...")
                    delay(10000)
                }
            }
            
            Log.d(TAG, "🏁 GLOBAL EŞİTLEME DURDURULDU")
            Log.d(TAG, "📊 TOPLAM İSTATİSTİKLER:")
            Log.d(TAG, "   - Tur Sayısı: $roundCount")
            Log.d(TAG, "   - Eklenen Soru: ${totalQuestionsAdded.get()}")
            updateNotification("Durduruldu: ${totalQuestionsAdded.get()} soru eklendi", roundCount, totalQuestionsAdded.get())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Global Soru Eşitleme",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "24/7 arka plan soru üretimi"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(status: String, round: Int, totalQuestions: Int): Notification {
        // MainActivity'ye dönüş intent'i
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Durdur butonu
        val stopIntent = Intent(this, GlobalSyncForegroundService::class.java).apply {
            action = "STOP"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🌍 Global Eşitleme Aktif")
            .setContentText("Tur $round | +$totalQuestions soru | $status")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_delete,
                "Durdur",
                stopPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(status: String, round: Int, totalQuestions: Int) {
        try {
            val notification = createNotification(status, round, totalQuestions)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Notification güncellenemedi: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🛑 GlobalSyncForegroundService onDestroy")
        
        isRunning.set(false)
        serviceJob?.cancel()
        serviceScope.cancel()
        
        // WakeLock'u serbest bırak
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        
        // Notification'ı kaldır
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
