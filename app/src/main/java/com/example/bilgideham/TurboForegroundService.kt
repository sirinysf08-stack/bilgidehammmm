package com.example.bilgideham

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TurboForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "turbo_balancing_channel"
        private const val CHANNEL_NAME = "Akıllı Dengeleme (TURBO)"
        private const val NOTIF_ID = 7811

        private const val ACTION_START = "com.example.bilgideham.TURBO_START"
        private const val ACTION_STOP = "com.example.bilgideham.TURBO_STOP"
        private const val EXTRA_TARGETS = "extra_targets"

        fun start(context: Context, targetLessons: List<String>) {
            val i = Intent(context, TurboForegroundService::class.java).apply {
                action = ACTION_START
                putStringArrayListExtra(EXTRA_TARGETS, ArrayList(targetLessons))
            }
            ContextCompat.startForegroundService(context, i)
        }

        fun stop(context: Context) {
            val i = Intent(context, TurboForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            ContextCompat.startForegroundService(context, i)
        }
    }

    private val serviceJob: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + serviceJob)

    @Volatile private var running = false
    private var workerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopInternal("⛔ Kullanıcı durdurdu (bildirim/Admin Panel).")
                return START_NOT_STICKY
            }
            ACTION_START -> {
                if (running) {
                    // zaten çalışıyor; idempotent davran
                    TurboBalancingBus.updateProgress(
                        task = "Çalışıyor",
                        progress = 0.2f,
                        logLine = "ℹ️ Turbo zaten çalışıyor; yeni start isteği görmezden gelindi."
                    )
                    return START_STICKY
                }

                val targets = intent.getStringArrayListExtra(EXTRA_TARGETS)
                    ?.filter { it.isNotBlank() }
                    ?.distinct()
                    ?.toList()
                    ?: emptyList()

                startInternal(targets)
                return START_STICKY
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopInternal("⛔ Servis kapatıldı.")
        super.onDestroy()
    }

    private fun startInternal(targetLessons: List<String>) {
        running = true
        TurboBalancingBus.markStarted("🚀 Turbo (Foreground Service) başlatıldı. Ekrandan çıkabilirsiniz.")

        ensureNotificationChannel()
        try {
            startForeground(NOTIF_ID, buildNotification("Hazırlanıyor...", "Hedef seçiliyor..."))
        } catch (se: SecurityException) {
            // Android 13+ bildirim izni verilmemiş olabilir; crash yerine kontrollü kapan
            TurboBalancingBus.error("Bildirim izni yok. Android 13+ için bildirim izni verilmeli.")
            running = false
            stopSelf()
            return
        }

        workerJob = scope.launch(Dispatchers.IO) {
            val generator = AiQuestionGenerator()
            val effectiveTargets = targetLessons.filter { it != "Deneme Sınavı" }

            if (effectiveTargets.isEmpty()) {
                TurboBalancingBus.error("Hedef ders listesi boş. (Deneme hariç hedef seçilmeli)")
                withContext(Dispatchers.Main) {
                    updateNotification("Hata", "Hedef ders bulunamadı")
                }
                stopInternal("❌ Hedef ders bulunamadığı için durdu.")
                return@launch
            }

            while (running) {
                try {
                    val currentCounts = QuestionRepository.getQuestionCounts()

                    val minEntry = currentCounts
                        .filter { (k, _) -> effectiveTargets.any { t -> t == k } && k != "Deneme Sınavı" }
                        .minByOrNull { it.value }

                    if (minEntry == null) {
                        TurboBalancingBus.updateProgress(
                            task = "Beklemede",
                            progress = 0.2f,
                            logLine = "⚠️ Bulut sayımları içinde hedef ders bulunamadı; 5 sn sonra tekrar denenecek.",
                            cloudCounts = currentCounts
                        )
                        withContext(Dispatchers.Main) { updateNotification("Beklemede", "Hedef ders bulunamadı") }
                        delay(5000)
                        continue
                    }

                    val targetLessonName = minEntry.key
                    val currentCount = minEntry.value

                    TurboBalancingBus.updateProgress(
                        task = "Hedef: $targetLessonName ($currentCount)",
                        progress = 0.35f,
                        logLine = "📉 En az: $targetLessonName ($currentCount). +15 ekleme başlıyor...",
                        cloudCounts = currentCounts,
                        lastTarget = targetLessonName
                    )
                    withContext(Dispatchers.Main) { updateNotification("Hedef: $targetLessonName", "Mevcut: $currentCount") }

                    val produced = generator.generateBatch(targetLessonName, 15)

                    if (produced.isEmpty()) {
                        TurboBalancingBus.updateProgress(
                            task = "$targetLessonName Pas",
                            progress = 0.5f,
                            logLine = "⚠️ $targetLessonName için soru üretilemedi; 2 sn sonra tekrar.",
                            lastTarget = targetLessonName,
                            lastSaved = 0
                        )
                        withContext(Dispatchers.Main) { updateNotification("Pas", "$targetLessonName için üretim yok") }
                        delay(2000)
                        continue
                    }

                    val saved = QuestionRepository.saveQuestionsToFirestore(produced)

                    val afterCounts = runCatching { QuestionRepository.getQuestionCounts() }.getOrDefault(currentCounts)

                    TurboBalancingBus.updateProgress(
                        task = "$targetLessonName +$saved",
                        progress = 0.85f,
                        logLine = "✅ $targetLessonName: $saved yeni benzersiz soru eklendi.",
                        cloudCounts = afterCounts,
                        lastTarget = targetLessonName,
                        lastSaved = saved
                    )
                    withContext(Dispatchers.Main) { updateNotification("Eklendi: +$saved", targetLessonName) }

                    // sistem yükü / kota riskini azaltmak için mevcut davranışı koruyoruz
                    delay(4000)
                } catch (e: Exception) {
                    TurboBalancingBus.error(e.message ?: "Bilinmeyen hata")
                    withContext(Dispatchers.Main) { updateNotification("Hata", e.message ?: "Bilinmeyen hata") }
                    delay(5000)
                }
            }
        }
    }

    private fun stopInternal(reasonLog: String) {
        if (!running) {
            // idempotent stop
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }
        running = false
        workerJob?.cancel()
        workerJob = null

        TurboBalancingBus.markStopped(reasonLog)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val existing = nm.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Bulut sınav havuzunu otomatik dengeleme işlemleri"
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun buildNotification(title: String, content: String) : android.app.Notification {
        val stopIntent = Intent(this, TurboForegroundService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(
            this,
            1001,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("TURBO Dengeleme: $title")
            .setContentText(content)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Durdur", stopPending)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(title, content))
    }
}
