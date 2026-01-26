package com.example.bilgideham

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

object AppNotificationManager {
    
    private const val CHANNEL_MOTIVATION = "motivation_channel"
    private const val CHANNEL_UPDATE = "update_channel"
    
    private const val NOTIFICATION_MOTIVATION = 1001
    private const val NOTIFICATION_UPDATE = 1002
    
    /**
     * Bildirim kanallarını oluştur (Android 8+)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Motivasyon Kanalı
            val motivationChannel = NotificationChannel(
                CHANNEL_MOTIVATION,
                "Günlük Motivasyon",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Günlük hedef hatırlatmaları ve motivasyon mesajları"
            }
            
            // Güncelleme Kanalı
            val updateChannel = NotificationChannel(
                CHANNEL_UPDATE,
                "Uygulama Güncellemeleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yeni sürüm bildirimleri"
            }
            
            notificationManager.createNotificationChannels(listOf(motivationChannel, updateChannel))
        }
    }
    
    /**
     * Motivasyon bildirimi gönder
     */
    fun sendMotivationNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_MOTIVATION)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_MOTIVATION, notification)
        } catch (e: SecurityException) {
            // İzin yoksa sessizce geç
        }
    }
    
    /**
     * Güncelleme bildirimi gönder
     */
    fun sendUpdateNotification(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_UPDATE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Yeni Güncelleme Mevcut!")
            .setContentText("Akıl Küpü'nün yeni sürümü hazır. Hemen güncelle!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_UPDATE, notification)
        } catch (e: SecurityException) {
            // İzin yoksa sessizce geç
        }
    }
    
    /**
     * Günlük motivasyon worker'ını başlat
     * Her gün akşam 17:30'da bildirim gönderir
     */
    fun scheduleDailyMotivation(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        // Her gün saat 17:30'da çalışacak şekilde ayarla
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 17)
            set(java.util.Calendar.MINUTE, 30)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        
        var delay = calendar.timeInMillis - currentTime
        if (delay < 0) {
            delay += TimeUnit.DAYS.toMillis(1) // Yarın için ayarla
        }
        
        val workRequest = PeriodicWorkRequestBuilder<DailyMotivationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        
        // REPLACE ile eski zamanlamayı güncelle
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_motivation",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}

/**
 * Günlük motivasyon bildirimi gönderen Worker
 */
class DailyMotivationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val context = applicationContext
        val statsManager = StatsManager(context)
        val (correct, wrong) = statsManager.getTodayTotals()
        val totalSolved = correct + wrong
        
        // Hedef kontrolü
        val dailyTarget = when {
            totalSolved >= 50 -> 100
            totalSolved >= 30 -> 50
            else -> 30
        }
        
        val (title, message) = when {
            totalSolved == 0 -> "📚 Bugün hiç soru çözmedin!" to "Hadi birkaç soru çözelim, zirveye adım adım!"
            totalSolved < dailyTarget / 2 -> "💪 Yarı yoldasın!" to "Bugün $totalSolved soru çözdün. Hedefe $dailyTarget soru, devam et!"
            totalSolved < dailyTarget -> "🔥 Az kaldı!" to "Bugün $totalSolved soru çözdün. Hedefe sadece ${dailyTarget - totalSolved} soru kaldı!"
            else -> "🏆 Harikasın!" to "Bugün $totalSolved soru çözdün ve hedefini aştın! Yarın da böyle devam!"
        }
        
        AppNotificationManager.sendMotivationNotification(context, title, message)
        
        return Result.success()
    }
}
