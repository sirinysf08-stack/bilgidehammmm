package com.example.bilgideham

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Arka Plan Soru Senkronizasyon Servisi - V2
 *
 * ÖZELLİKLER:
 * - Sadece İlkokul ve Ortaokul için soru üretir
 * - Her sınıfa 15'er soru ekler ve eşitler
 * - En düşük soru sayısına öncelik verir
 * - Eşit olunca hedefi yükselterek devam eder
 * - Detaylı bildirimlerle çalışır
 */
class QuestionSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SYNC_WORKER"
        private const val WORK_NAME = "question_sync_worker"
        private const val CHANNEL_ID = "question_sync_channel"
        private const val NOTIFICATION_ID = 1001

        // Her ekleme turunda eklenecek soru sayısı
        const val QUESTIONS_PER_BATCH = 15

        // Sadece bu seviyeler için soru üret
        val ALLOWED_LEVELS = listOf(EducationLevel.ILKOKUL, EducationLevel.ORTAOKUL)

        // Toplam eklenen soru sayısı (bildirim için)
        @Volatile
        var totalQuestionsAdded = 0
            private set

        /**
         * Periyodik senkronizasyonu başlat (15 dakikada bir)
         */
        fun startPeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<QuestionSyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            totalQuestionsAdded = 0
            DebugLog.d(TAG, "✅ Periyodik senkronizasyon başlatıldı (Sadece İlkokul + Ortaokul)")
        }

        /**
         * Sürekli senkronizasyon (zincirleme çalışır)
         */
        fun startContinuousSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<QuestionSyncWorker>()
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("continuous_sync")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "continuous_question_sync",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            totalQuestionsAdded = 0
            DebugLog.d(TAG, "🚀 Sürekli senkronizasyon başlatıldı")
        }

        /**
         * Tek seferlik senkronizasyon (hemen çalıştır)
         */
        fun runOnce(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<QuestionSyncWorker>()
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            DebugLog.d(TAG, "🚀 Tek seferlik senkronizasyon başlatıldı")
        }

        /**
         * Senkronizasyonu durdur
         */
        fun stopSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork("continuous_question_sync")
            WorkManager.getInstance(context).cancelAllWorkByTag("continuous_sync")
            totalQuestionsAdded = 0
            DebugLog.d(TAG, "⛔ Senkronizasyon durduruldu")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        DebugLog.d(TAG, "🔄 SONSUZ DÖNGÜ: Senkronizasyon başladı...")

        // GECE SAATLERİNDE ÇALIŞMA (22:00 - 07:00 arası)
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (currentHour >= 22 || currentHour < 7) {
            DebugLog.d(TAG, "🌙 Gece saati ($currentHour:00), senkronizasyon ertelendi")
            // Sabah 7'de tekrar dene
            val calendar = java.util.Calendar.getInstance().apply {
                if (currentHour >= 22) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
                set(java.util.Calendar.HOUR_OF_DAY, 7)
                set(java.util.Calendar.MINUTE, 0)
            }
            val delay = calendar.timeInMillis - System.currentTimeMillis()
            
            val workRequest = OneTimeWorkRequestBuilder<QuestionSyncWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("continuous_sync")
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "continuous_question_sync", ExistingWorkPolicy.REPLACE, workRequest
            )
            return@withContext Result.success()
        }

        try {
            createNotificationChannel()
            setForeground(createForegroundInfo("Sorular hazırlanıyor...", totalQuestionsAdded))

            // Tüm sınıfların soru sayılarını topla
            val allClassCounts = getAllClassCounts()
            
            if (allClassCounts.isEmpty()) {
                DebugLog.d(TAG, "⚠️ Hiç sınıf bulunamadı, 30 saniye sonra tekrar denenecek")
                scheduleContinuation()
                return@withContext Result.success()
            }

            DebugLog.d(TAG, "📊 ${allClassCounts.size} ders bulundu")
            
            val generator = AiQuestionGenerator()
            var addedThisRound = 0

            // EN DÜŞÜK SORU SAYISINA SAHİP DERSLERE ÖNCELİK VER
            val sortedByCount = allClassCounts.entries.sortedBy { it.value }
            
            // En düşük soru sayısını bul
            val minCount = sortedByCount.firstOrNull()?.value ?: 0
            
            // En düşük soru sayısına sahip SADECE 1 dersi al (Timeout önlemek için tek tek ilerle)
            val lowestCountClasses = sortedByCount.filter { it.value == minCount }.take(1)
            
            DebugLog.d(TAG, "📉 En düşük soru sayısı: $minCount (${lowestCountClasses.size} ders)")

            // SADECE EN DÜŞÜK SORU SAYISINA SAHİP DERSLERE SORU EKLE
            for ((classKey, currentCount) in lowestCountClasses) {
                try {
                    val gradeText = classKey.grade?.let { "$it. Sınıf" } ?: "Genel"
                    val displayText = "$gradeText ${classKey.subjectName}"
                    
                    DebugLog.d(TAG, "📝 [$gradeText] ${classKey.subjectName}: Soru üretiliyor... (mevcut: $currentCount)")
                    setForeground(createForegroundInfo("$displayText: Soru üretiliyor...", totalQuestionsAdded))

                    val questions = generator.generateFastBatch(
                        lesson = classKey.subjectName,
                        count = QUESTIONS_PER_BATCH,
                        level = classKey.level,
                        schoolType = classKey.schoolType,
                        grade = classKey.grade
                    )

                    // ============ ÇİFT KONTROL SİSTEMİ ============
                    val validatedQuestions = if (classKey.level in ALLOWED_LEVELS) {
                        DebugLog.d(TAG, "🔍 Çift kontrol başlıyor: ${questions.size} soru")
                        setForeground(createForegroundInfo("🔍 Doğrulama: $displayText", totalQuestionsAdded))
                        
                        val validated = mutableListOf<QuestionModel>()
                        for (q in questions) {
                            val isValid = try {
                                generator.validateQuestionWithAI(q)
                            } catch (e: Exception) {
                                Log.w(TAG, "AI doğrulama hatası, soru geçerli sayılıyor: ${e.message}")
                                true
                            }
                            
                            if (isValid) validated.add(q)
                            delay(200)
                        }
                        
                        DebugLog.d(TAG, "🔍 Çift kontrol tamamlandı: ${validated.size}/${questions.size} soru geçerli")
                        validated
                    } else {
                        questions
                    }

                    if (validatedQuestions.isNotEmpty()) {
                        val saved = QuestionRepository.saveQuestionsForLevel(
                            questions = validatedQuestions,
                            level = classKey.level,
                            schoolType = classKey.schoolType,
                            grade = classKey.grade,
                            subjectId = classKey.subjectId
                        )
                        addedThisRound += saved
                        totalQuestionsAdded += saved
                        
                        DebugLog.d(TAG, "✅ [$gradeText] ${classKey.subjectName}: +$saved soru")
                        setForeground(createForegroundInfo("✅ $displayText: +$saved soru", totalQuestionsAdded))
                    }

                    delay(2000) // Rate limiting - API aşırı yüklenmesini önle
                } catch (e: Exception) {
                    Log.e(TAG, "❌ ${classKey.subjectName}: ${e.message}")
                }
            }

            DebugLog.d(TAG, "🏁 Tur tamamlandı: +$addedThisRound soru (Toplam: $totalQuestionsAdded)")
            setForeground(createForegroundInfo("🔄 Tur tamamlandı! +$addedThisRound soru", totalQuestionsAdded))
            
            // BİR SONRAKİ TURU PLANLA (SONSUZ DÖNGÜ)
            scheduleContinuation()
            
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "❌ Senkronizasyon hatası: ${e.message}")
            // Hata olsa bile devam et
            scheduleContinuation()
            Result.retry()
        }
    }

    private fun scheduleContinuation() {
        // 10 saniye sonra bir sonraki turu başlat
        val workRequest = OneTimeWorkRequestBuilder<QuestionSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag("continuous_sync")
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "continuous_question_sync",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private suspend fun getAllClassCounts(): Map<ClassKey, Int> {
        val result = mutableMapOf<ClassKey, Int>()

        // TÜM SEVİYELER İÇİN SORU SAYILARINI TOPLA
        for (level in ALLOWED_LEVELS) {
            val schoolTypes = CurriculumManager.getSchoolTypesFor(level)

            for (schoolType in schoolTypes) {
                val grades = schoolType.grades.ifEmpty { listOf(null) }

                for (grade in grades) {
                    val subjects = CurriculumManager.getSubjectsFor(schoolType, grade as? Int)
                    val counts = try {
                        QuestionRepository.getQuestionCountsForLevel(level, schoolType, grade as? Int)
                    } catch (e: Exception) { emptyMap() }

                    for (subject in subjects) {
                        val currentCount = counts[subject.id] ?: 0
                        val key = ClassKey(level, schoolType, grade as? Int, subject.id, subject.displayName)
                        result[key] = currentCount
                    }
                }
            }
        }
        
        return result
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, 
                "Soru Senkronizasyonu", 
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Arka planda soru ekleme bildirimleri"
                setShowBadge(true)
            }
            applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(message: String, totalAdded: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("📚 Bilgi Deham - Soru Ekleniyor")
            .setContentText(message)
            .setSubText("Toplam: $totalAdded soru eklendi")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        // Android 14+ (API 34+) için foreground service type gerekli
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(
                NOTIFICATION_ID, 
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    data class ClassKey(
        val level: EducationLevel,
        val schoolType: SchoolType,
        val grade: Int?,
        val subjectId: String,
        val subjectName: String
    )
}
