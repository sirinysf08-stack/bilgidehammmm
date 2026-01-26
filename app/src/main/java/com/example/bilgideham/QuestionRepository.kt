package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object QuestionRepository {

    private const val TAG = "QuestionRepository"
    private val db: FirebaseFirestore get() = Firebase.firestore

    private fun candidateItemCollections(lessonTitle: String) = listOf(
        db.collection("question_pools").document(lessonTitle).collection("items"),
        db.collection("questions").document(lessonTitle).collection("items"),
        db.collection("questionPool").document(lessonTitle).collection("items"),
        db.collection("question_pool").document(lessonTitle).collection("items")
    )

    fun computeDocIdForQuestion(q: QuestionModel): String {
        val raw = buildString {
            append((q.lesson).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.question).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.optionA).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.optionB).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.optionC).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.optionD).trim().lowercase(Locale("tr", "TR")))
            append("|")
            append((q.correctAnswer).trim().uppercase(Locale.US))
        }
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(raw.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }.take(40)
    }

    suspend fun markSeenAllBestEffort(
        userId: String,
        lessonTitle: String,
        questions: List<QuestionModel>
    ) {
        if (userId.isBlank() || questions.isEmpty()) return
        runCatching {
            val seenCol = db.collection("users").document(userId).collection("seen")
            var batch = db.batch()
            var ops = 0
            for (q in questions) {
                val docId = computeDocIdForQuestion(q)
                val ref = seenCol.document(docId)
                val payload = hashMapOf(
                    "lessonTitle" to lessonTitle,
                    "lesson" to q.lesson,
                    "seenAt" to FieldValue.serverTimestamp()
                )
                batch.set(ref, payload, SetOptions.merge())
                ops++
                if (ops >= 450) {
                    batch.commit().await()
                    batch = db.batch()
                    ops = 0
                }
            }
            if (ops > 0) batch.commit().await()
        }.onFailure {
            Log.w(TAG, "markSeenAllBestEffort failed: ${it.message}")
        }
    }


    /**
     * Buluttan kullanıcının daha önce gördüğü soruların ID'lerini çeker.
     */
    suspend fun getSeenQuestionIdsFromCloud(userId: String): Set<String> {
        if (userId.isBlank()) return emptySet()
        return try {
            val seenCol = db.collection("users").document(userId).collection("seen")
            val snap = seenCol.get().await()
            snap.documents.map { it.id }.toSet()
        } catch (e: Exception) {
            Log.w(TAG, "getSeenQuestionIdsFromCloud failed: ${e.message}")
            emptySet()
        }
    }

    /**
     * AGS Tarih için HIZLI soru çekme fonksiyonu.
     * Cloud seen kontrolü yapmaz - sadece local excludeDocIds kullanır.
     * Bu sayede 30 saniye yerine 1-2 saniyede sorular yüklenir.
     */
    suspend fun getQuestionsForAgsTarih(
        subjectId: String,
        limit: Int,
        excludeDocIds: Set<String> = emptySet()
    ): List<QuestionModel> {
        DebugLog.d(TAG, "🏛️ AGS Tarih HIZLI sorgu: $subjectId, limit=$limit, exclude=${excludeDocIds.size}")
        
        // Doğrudan koleksiyona git: question_pools/AGS/AGS_OABT/general/{subjectId}
        val col = db.collection("question_pools")
            .document("AGS")
            .collection("AGS_OABT")
            .document("general")
            .collection(subjectId)
        
        val fetchSize = (limit * 3).coerceAtLeast(50).coerceAtMost(200)
        
        val rawList = runCatching {
            col.limit(fetchSize.toLong()).get().await()
                .toObjects(QuestionModel::class.java)
        }.getOrElse { e ->
            Log.e(TAG, "❌ AGS Tarih sorgu hatası: ${e.message}")
            emptyList()
        }
        
        DebugLog.d(TAG, "   - rawList: ${rawList.size} soru bulundu")
        
        if (rawList.isEmpty()) return emptyList()
        
        // Sadece local exclude ile filtrele (cloud seen yok - hız için)
        val filtered = rawList.filter { q ->
            val docId = computeDocIdForQuestion(q)
            docId !in excludeDocIds && q.graphicData.isNullOrBlank() && q.graphicType.isNullOrBlank()
        }.take(limit)
        
        DebugLog.d(TAG, "   - filtered: ${filtered.size} soru döndürülüyor")
        
        return filtered
    }

    /**
     * AGS Tarih toplam soru sayısını döndürür (14 ünite)
     */
    suspend fun getAgsTarihQuestionCount(): Int {
        var total = 0
        for (i in 1..14) {
            val col = db.collection("question_pools")
                .document("AGS")
                .collection("AGS_OABT")
                .document("general")
                .collection("tarih_unite_$i")
            
            val count = runCatching {
                col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
            }.getOrDefault(0)
            total += count
        }
        return total
    }

    /**
     * AGS Tarih ünite bazlı soru sayılarını döndürür
     * Map<UniteId, Count> formatında
     */
    suspend fun getAgsTarihUniteCounts(): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        for (i in 1..14) {
            val col = db.collection("question_pools")
                .document("AGS")
                .collection("AGS_OABT")
                .document("general")
                .collection("tarih_unite_$i")
            
            val count = runCatching {
                col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
            }.getOrDefault(0)
            counts[i] = count
        }
        return counts
    }

    suspend fun getAgsMebLessonCounts(): Map<String, Int> {
        val subjects = CurriculumManager.getSubjectsFor(SchoolType.AGS_MEB, null)
        val raw = getQuestionCountsForLevel(EducationLevel.AGS, SchoolType.AGS_MEB, null)
        return subjects.associate { it.displayName to (raw[it.id] ?: 0) }
    }

    suspend fun getAgsOabtUnitCountsByField(): Map<String, List<Pair<String, Int>>> = withContext(Dispatchers.IO) {
        val fields = listOf(
            "turkce",
            "ilkmat",
            "fen",
            "sosyal",
            "edebiyat",
            "tarih",
            "cografya",
            "matematik",
            "fizik",
            "kimya",
            "biyoloji",
            "rehberlik",
            "sinif",
            "okoncesi",
            "beden",
            "din"
        )

        val result = mutableMapOf<String, List<Pair<String, Int>>>()
        for (field in fields) {
            if (field == "tarih") {
                val uniteNames = listOf(
                    "Tarih Bilimi", "Osmanlı Türkçesi", "Uygarlığın Doğuşu",
                    "İlk Türk Devletleri", "İslam Tarihi", "Türk İslam Devletleri",
                    "Türk Dünyası", "Osmanlı Tarihi", "En Uzun Yüzyıl",
                    "XX. Yüzyıl Başları", "Milli Mücadele", "Atatürk Dönemi",
                    "Dünya Tarihi", "Çağdaş Tarih"
                )
                val uniteCounts = getAgsTarihUniteCounts()
                result[field] = uniteNames.mapIndexed { index, name ->
                    name to (uniteCounts[index + 1] ?: 0)
                }
                continue
            }

            val subjects = AppPrefs.getAgsOabtUnitSubjects(field)
            if (subjects.isEmpty()) {
                result[field] = emptyList()
                continue
            }

            val counts = mutableListOf<Pair<String, Int>>()
            for (subj in subjects) {
                val col = db.collection("question_pools")
                    .document("AGS")
                    .collection("AGS_OABT")
                    .document("general")
                    .collection(subj.id)
                val count = runCatching {
                    col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
                }.getOrDefault(0)
                counts.add(subj.displayName to count)
            }
            result[field] = counts
        }
        result
    }

    /**
     * AGS Tarih tüm sorularını siler (14 ünite)
     */
    suspend fun deleteAgsTarihQuestions(): Int {
        var totalDeleted = 0
        for (i in 1..14) {
            val col = db.collection("question_pools")
                .document("AGS")
                .collection("AGS_OABT")
                .document("general")
                .collection("tarih_unite_$i")
            
            val deleted = runCatching {
                var count = 0
                while (true) {
                    val snap = col.limit(400).get().await()
                    if (snap.isEmpty) break
                    
                    val batch = db.batch()
                    for (doc in snap.documents) {
                        batch.delete(doc.reference)
                    }
                    batch.commit().await()
                    count += snap.size()
                }
                count
            }.getOrDefault(0)
            totalDeleted += deleted
            DebugLog.d(TAG, "🗑️ tarih_unite_$i: $deleted soru silindi")
        }
        DebugLog.d(TAG, "🏁 AGS Tarih toplam $totalDeleted soru silindi")
        return totalDeleted
    }

    /**
     * Kullanıcının daha önce çözdüğü soruları hariç tutarak Firestore'dan soru çeker.
     * Hem local hem bulut seen kayıtlarını kontrol eder.
     */
    suspend fun getUnseenQuestionsForUser(
        lessonTitle: String,
        limit: Int,
        userId: String,
        excludeDocIds: Set<String> = emptySet()
    ): List<QuestionModel> {
        if (lessonTitle.isBlank() || limit <= 0) return emptyList()

        // 1. Buluttan kullanıcının gördüğü soruları al
        val cloudSeenIds = getSeenQuestionIdsFromCloud(userId)
        val allExcluded = excludeDocIds + cloudSeenIds

        DebugLog.d(TAG, "Fetching for $lessonTitle: limit=$limit, localExclude=${excludeDocIds.size}, cloudSeen=${cloudSeenIds.size}")

        // 2. Daha fazla çek, client-side filtrele
        val fetchSize = (limit * 5).coerceAtLeast(100).coerceAtMost(300)

        var rawList: List<QuestionModel> = emptyList()

        for (col in candidateItemCollections(lessonTitle)) {
            val snap = runCatching { col.limit(fetchSize.toLong()).get().await() }.getOrNull()
            val list = snap?.toObjects(QuestionModel::class.java).orEmpty()
            if (list.isNotEmpty()) {
                rawList = list
                DebugLog.d(TAG, "Found ${list.size} questions in Firestore for $lessonTitle")
                break
            }
        }

        if (rawList.isEmpty()) {
            Log.w(TAG, "No Firestore stock found for $lessonTitle")
            return emptyList()
        }

        // 3. Daha önce çözülmüş soruları filtrele
        val filtered = ArrayList<QuestionModel>(limit)
        var skippedCount = 0

        for (q in rawList) {
            val docId = computeDocIdForQuestion(q)
            if (docId.isBlank()) continue

            if (docId in allExcluded) {
                skippedCount++
                continue
            }

            filtered.add(q)
            if (filtered.size >= limit) break
        }

        DebugLog.d(TAG, "Result: ${filtered.size} unseen, $skippedCount skipped (total pool: ${rawList.size})")

        // 4. Yeni soruları bulutta işaretle
        if (filtered.isNotEmpty()) {
            markSeenAllBestEffort(userId = userId, lessonTitle = lessonTitle, questions = filtered)
        }

        return filtered
    }

    suspend fun getQuestionCounts(): Map<String, Int> {
        val lessons = listOf(
            "Matematik", "Türkçe", "Fen Bilimleri", "Sosyal Bilgiler",
            "İngilizce (A1)", "İngilizce (A2)", "İngilizce (B1)",
            "Din Kültürü", "Arapça", "Paragraf", "Deneme Sınavı"
        )
        val result = mutableMapOf<String, Int>()
        for (lesson in lessons) {
            var count = 0
            for (col in candidateItemCollections(lesson)) {
                val snap = runCatching { col.get().await() }.getOrNull()
                val size = snap?.size() ?: 0
                if (size > 0) {
                    count = size
                    break
                }
            }
            result[lesson] = count
        }
        return result
    }

    suspend fun saveQuestionsToFirestore(questions: List<QuestionModel>): Int {
        if (questions.isEmpty()) return 0
        var savedCount = 0
        for (q in questions) {
            val lessonTitle = q.lesson.ifBlank { "Genel" }
            val docId = computeDocIdForQuestion(q)
            val col = db.collection("question_pools").document(lessonTitle).collection("items")
            runCatching {
                col.document(docId).set(q).await()
                savedCount++
            }.onFailure {
                Log.w(TAG, "saveQuestionsToFirestore failed: ${it.message}")
            }
        }
        return savedCount
    }

    suspend fun deleteAllQuestionsFromFirestore() {
        DebugLog.d(TAG, "🗑️ Tüm sorular siliniyor...")
        
        val deleteJobs = mutableListOf<Deferred<Int>>()
        
        coroutineScope {
            // 1. ESKİ YAPI: question_pools/Matematik/items gibi
            val lessons = listOf(
                "Matematik", "Türkçe", "Fen Bilimleri", "Sosyal Bilgiler",
                "İngilizce (A1)", "İngilizce (A2)", "İngilizce (B1)",
                "Din Kültürü", "Arapça", "Paragraf", "Deneme Sınavı",
                // Alternatif yazımlar (Türkçe karakter olmadan)
                "turkce", "matematik", "fen_bilimleri", "sosyal_bilgiler",
                "turkce_4", "turkce_5", "matematik_4", "matematik_5",
                "fen_4", "fen_5", "sosyal_4", "sosyal_5",
                "Turkce", "Fen", "Sosyal"
            )
            
            for (lesson in lessons) {
                for (col in candidateItemCollections(lesson)) {
                    deleteJobs.add(async(Dispatchers.IO) {
                        deleteCollectionBatch(col, "Eski: $lesson")
                    })
                }
            }
            
            // 1.5 DOĞRUDAN questions/ KOLEKSİYONU (tüm alt belgeler)
            val directQuestions = db.collection("questions")
            deleteJobs.add(async(Dispatchers.IO) {
                try {
                    val snap = directQuestions.get().await()
                    var deleted = 0
                    for (doc in snap.documents) {
                        // Her belgenin items alt koleksiyonunu sil
                        val itemsCol = doc.reference.collection("items")
                        deleted += deleteCollectionBatch(itemsCol, "questions/${doc.id}/items")
                    }
                    deleted
                } catch (e: Exception) {
                    Log.w(TAG, "questions/ silme hatası: ${e.message}")
                    0
                }
            })
            
            // 2. YENİ YAPI: question_pools/ORTAOKUL/ORTAOKUL_STANDARD/5/matematik_5/
            for (level in EducationLevel.entries) {
                val schoolTypes = CurriculumManager.getSchoolTypesFor(level)
                for (schoolType in schoolTypes) {
                    val grades = if (schoolType.grades.isEmpty()) listOf<Int?>(null) else schoolType.grades.map { it as Int? }
                    for (grade in grades) {
                        val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                        for (subject in subjects) {
                            val col = db.collection("question_pools")
                                .document(level.name)
                                .collection(schoolType.name)
                                .document(grade?.toString() ?: "general")
                                .collection(subject.id)
                            
                            deleteJobs.add(async(Dispatchers.IO) {
                                deleteCollectionBatch(col, "${level.name}/${grade ?: "G"}/${subject.id}")
                            })
                        }
                    }
                }
            }
        }
        
        val totalDeleted = deleteJobs.sumOf { runCatching { it.await() }.getOrDefault(0) }
        DebugLog.d(TAG, "🏁 Toplam $totalDeleted soru silindi!")
    }

    /**
     * BELİRLİ EĞİTİM SEVİYESİ İÇİN TÜM SORULARI SİLER
     * İlkokul, Ortaokul veya Lise soruları ayrı ayrı silinebilir
     */
    suspend fun deleteQuestionsByLevel(level: EducationLevel): Int = withContext(Dispatchers.IO) {
        DebugLog.d(TAG, "🗑️ ${level.displayName} soruları siliniyor...")
        
        val deleteJobs = mutableListOf<Deferred<Int>>()
        
        coroutineScope {
            val validSchoolTypes = CurriculumManager.getSchoolTypesFor(level)
            
            for (schoolType in validSchoolTypes) {
                val grades = if (schoolType.grades.isEmpty()) listOf<Int?>(null) else schoolType.grades.map { it }
                
                for (grade in grades) {
                    val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                    
                    for (subject in subjects) {
                        val col = db.collection("question_pools")
                            .document(level.name)
                            .collection(schoolType.name)
                            .document(grade?.toString() ?: "general")
                            .collection(subject.id)
                        
                        deleteJobs.add(async(Dispatchers.IO) {
                            deleteCollectionBatch(col, "${level.name}/${grade ?: "G"}/${subject.id}")
                        })
                    }
                }
            }
        }
        
        val totalDeleted = deleteJobs.sumOf { runCatching { it.await() }.getOrDefault(0) }
        DebugLog.d(TAG, "🏁 ${level.displayName}: Toplam $totalDeleted soru silindi!")
        totalDeleted
    }

    /**
     * TÜM SEVİYELERDEKİ PARAGRAF SORULARINI SİLER
     * Ortaokul (5-8), Lise (9-12), KPSS ve AGS paragraf soruları
     */
    suspend fun deleteAllParagrafQuestions(): Int = withContext(Dispatchers.IO) {
        DebugLog.d(TAG, "🗑️ Tüm paragraf soruları siliniyor...")
        
        val deleteJobs = mutableListOf<Deferred<Int>>()
        
        coroutineScope {
            // 1. ORTAOKUL PARAGRAF (5-8. sınıflar)
            for (grade in 5..8) {
                val col = db.collection("question_pools")
                    .document("ORTAOKUL")
                    .collection("ORTAOKUL_STANDARD")
                    .document(grade.toString())
                    .collection("paragraf_$grade")
                
                deleteJobs.add(async(Dispatchers.IO) {
                    deleteCollectionBatch(col, "ORTAOKUL/$grade/paragraf_$grade")
                })
            }
            
            // 2. LİSE PARAGRAF (9-12. sınıflar)
            for (grade in 9..12) {
                val col = db.collection("question_pools")
                    .document("LISE")
                    .collection("LISE_GENEL")
                    .document(grade.toString())
                    .collection("paragraf_lise_$grade")
                
                deleteJobs.add(async(Dispatchers.IO) {
                    deleteCollectionBatch(col, "LISE/$grade/paragraf_lise_$grade")
                })
            }
            
            // 3. KPSS PARAGRAF (Ortaöğretim, Önlisans, Lisans)
            for (schoolType in listOf("KPSS_ORTAOGRETIM", "KPSS_ONLISANS", "KPSS_LISANS")) {
                val col = db.collection("question_pools")
                    .document("KPSS")
                    .collection(schoolType)
                    .document("general")
                    .collection("paragraf_kpss")
                
                deleteJobs.add(async(Dispatchers.IO) {
                    deleteCollectionBatch(col, "KPSS/$schoolType/paragraf_kpss")
                })
            }
            
            // 4. AGS PARAGRAF (MEB 1. Oturum)
            val col = db.collection("question_pools")
                .document("AGS")
                .collection("AGS_MEB")
                .document("general")
                .collection("ags_paragraf")
            
            deleteJobs.add(async(Dispatchers.IO) {
                deleteCollectionBatch(col, "AGS/MEB/ags_paragraf")
            })
        }
        
        val totalDeleted = deleteJobs.sumOf { runCatching { it.await() }.getOrDefault(0) }
        DebugLog.d(TAG, "🏁 Toplam $totalDeleted paragraf sorusu silindi!")
        totalDeleted
    }
    
    private suspend fun deleteCollectionBatch(
        collection: com.google.firebase.firestore.CollectionReference,
        label: String
    ): Int {
        return runCatching {
            var totalDeleted = 0
            
            // Firestore batch limit: 500, güvenli olarak 400 kullanıyoruz
            while (true) {
                val snap = collection.limit(400).get().await()
                if (snap.isEmpty) break
                
                val batch = db.batch()
                for (doc in snap.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().await()
                totalDeleted += snap.size()
                
                DebugLog.d(TAG, "✅ $label: ${snap.size()} soru silindi")
            }
            
            totalDeleted
        }.getOrElse { e ->
            Log.w(TAG, "❌ $label silme hatası: ${e.message}")
            0
        }
    }

    // ==================== ÇOK SEVİYELİ SORU YÖNETİMİ ====================

    /**
     * Seviye bazlı koleksiyon yolu oluşturur
     * Örnek: question_pools/ORTAOKUL/ORTAOKUL_STANDARD/5/Matematik/items
     */
    private fun getLevelBasedCollection(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        lessonId: String
    ): com.google.firebase.firestore.CollectionReference {
        return db.collection("question_pools")
            .document(level.name)
            .collection(schoolType.name)
            .document(grade?.toString() ?: "general")
            .collection(lessonId)
    }

    private fun isReligiousSubject(lessonId: String): Boolean {
        val id = lessonId.lowercase()
        return id.startsWith("arapca") || 
               id.startsWith("kuran") || 
               id.startsWith("siyer") || 
               id.startsWith("hadis") || 
               id.startsWith("fikih") || 
               id.startsWith("kelam") ||
               id.startsWith("temel_dini") ||
               id.startsWith("peygamber")
    }

    // ==================== SİSTEM İSTATİSTİKLERİ ====================

    data class ClassStats(
        val grade: Int,
        val lessonCounts: Map<String, Int>
    )

    data class SchoolTypeStats(
        val type: SchoolType,
        val classStats: List<ClassStats>,
        val totalQuestions: Int
    )

    data class SystemStats(
        val detailedStats: Map<EducationLevel, List<SchoolTypeStats>>,
        val totalQuestions: Int
    )

    /**
     * Tüm sistemdeki soru sayılarını hiyerarşik olarak çeker (PARALEL/HIZLANDIRILMIŞ)
     */
    suspend fun getAllSystemStatistics(): SystemStats = withContext(Dispatchers.IO) {
        val jobs = mutableListOf<Deferred<Unit>>()
        val detailedStats = ConcurrentHashMap<EducationLevel, MutableList<SchoolTypeStats>>()
        val grandTotal = AtomicInteger(0)

        // Veri yapısını thread-safe doldurabilmek için senkronizasyon gerekecek veya
        // sonuçları toplayıp sonra map'leyeceğiz. En temizi: hiyerarşiyi koruyarak her seviye için async başlatmak.

        val levelDeffereds = EducationLevel.entries.map { level ->
            async {
                val validSchoolTypes = CurriculumManager.getSchoolTypesFor(level)
                val schoolTypeStatsList = mutableListOf<SchoolTypeStats>()

                for (schoolType in validSchoolTypes) {
                    // Bu okul türü için tüm sınıfları ve dersleri topla
                    val grades = if (schoolType.grades.isEmpty()) listOf<Int?>(null) else schoolType.grades.map { it }
                    
                    // Grade'leri de paralel yapabiliriz ama schoolType bazında paralellik yeterli olabilir. 
                    // Daha da hızlandırmak için en alt seviyeye (ders) kadar inelim.
                    
                    val schoolTypeTotal = AtomicInteger(0)
                    
                    val classStatsDeferreds = grades.map { grade ->
                        async {
                            val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                            val lessonCounts = ConcurrentHashMap<String, Int>()
                            
                            val subjectJobs = subjects.map { subject ->
                                async {
                                    val col = getLevelBasedCollection(level, schoolType, grade, subject.id)
                                    val count = runCatching {
                                         // FAZ 1.1: AGGREGATION QUERY - Sadece sayıyı sor (paralel + hızlı)
                                         col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
                                    }.getOrDefault(0)

                                    if (count > 0) {
                                        lessonCounts[subject.displayName] = count
                                        schoolTypeTotal.addAndGet(count)
                                        grandTotal.addAndGet(count)
                                    }
                                }
                            }
                            subjectJobs.awaitAll()

                            if (lessonCounts.isNotEmpty()) {
                                // Sıralı olması için map'i sorted map'e çevirebiliriz veya listeye
                                ClassStats(grade ?: 0, lessonCounts.toSortedMap())
                            } else {
                                null
                            }
                        }
                    }
                    
                    val classStatsList = classStatsDeferreds.awaitAll().filterNotNull().sortedBy { it.grade }

                    if (schoolTypeTotal.get() > 0) {
                        schoolTypeStatsList.add(SchoolTypeStats(schoolType, classStatsList, schoolTypeTotal.get()))
                    }
                }
                
                if (schoolTypeStatsList.isNotEmpty()) {
                    detailedStats[level] = schoolTypeStatsList
                }
            }
        }
        
        levelDeffereds.awaitAll()

        // ConcurrentHashMap'i normal Map'e ve sıralı hale çevir
        val sortedDetailedStats = detailedStats.keys.sortedBy { it.ordinal }.associateWith { level ->
             detailedStats[level]!!.sortedBy { it.type.ordinal }.toList()
        }

        SystemStats(sortedDetailedStats, grandTotal.get())
    }

    /**
     * Sadece UI iskeletini oluşturmak için boş istatistik döner (Firestore'a gitmez, ANINDA CEVAP)
     */
    fun getEmptySystemStatistics(): SystemStats {
        val detailedStats = mutableMapOf<EducationLevel, MutableList<SchoolTypeStats>>()
        
        for (level in EducationLevel.entries) {
            val validSchoolTypes = CurriculumManager.getSchoolTypesFor(level)
            val schoolTypeStatsList = mutableListOf<SchoolTypeStats>()

            for (schoolType in validSchoolTypes) {
                val grades = if (schoolType.grades.isEmpty()) listOf<Int?>(null) else schoolType.grades.map { it }
                val classStatsList = mutableListOf<ClassStats>()

                for (grade in grades) {
                    val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                    val lessonCounts = subjects.associate { it.displayName to 0 }.toSortedMap()
                    
                    if (lessonCounts.isNotEmpty()) {
                        classStatsList.add(ClassStats(grade ?: 0, lessonCounts))
                    }
                }
                schoolTypeStatsList.add(SchoolTypeStats(schoolType, classStatsList, 0))
            }
            if (schoolTypeStatsList.isNotEmpty()) {
                detailedStats[level] = schoolTypeStatsList
            }
        }
        return SystemStats(detailedStats, 0)
    }

    /**
     * Belirli bir seviye için soru sayılarını döndürür
     */
    suspend fun getQuestionCountsForLevel(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): Map<String, Int> {
        val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
        val result = mutableMapOf<String, Int>()

        for (subject in subjects) {
            val col = getLevelBasedCollection(level, schoolType, grade, subject.id)
            val count = runCatching {
                // FAZ 1.1: AGGREGATION QUERY - Sadece sayıyı sor (10MB → 8 byte)
                col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
            }.getOrDefault(0)
            result[subject.id] = count
        }

        return result
    }

    /**
     * Belirli bir seviye için soruları kaydeder
     * subjectId parametresi ile doğru koleksiyona kaydedilir
     * DUPLIKAT KONTROLU: Varolan sorular tekrar eklenmez
     */
    data class SaveResult(val added: Int, val skipped: Int)
    
    /**
     * FAZ 1.2: BATCH WRITE - Soruları toplu olarak kaydeder
     * 60 soru: 120 network call → 4 network call (%97 hızlanma)
     */
    private suspend fun saveQuestionsForLevelBatch(
        questions: List<QuestionModel>,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        finalSubjectId: String
    ): SaveResult = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext SaveResult(0, 0)
        
        val col = getLevelBasedCollection(level, schoolType, grade, finalSubjectId)
        var addedCount = 0
        var skippedCount = 0
        
        // Firestore whereIn limiti: 30 item
        val chunks = questions.chunked(30)
        
        for (chunk in chunks) {
            // 1. BATCH DUPLICATE CHECK - Tek network call ile 30 soruyu kontrol et
            val docIds = chunk.map { computeDocIdForQuestion(it) }
            val existingDocs = runCatching {
                col.whereIn(com.google.firebase.firestore.FieldPath.documentId(), docIds).get().await()
            }.getOrNull()
            val existingIds = existingDocs?.documents?.map { it.id }?.toSet() ?: emptySet()
            
            // 2. BATCH WRITE - Sadece yeni soruları toplu kaydet
            var batch = db.batch()
            var ops = 0
            
            chunk.forEachIndexed { index, q ->
                val docId = docIds[index]
                
                if (docId in existingIds) {
                    skippedCount++
                    DebugLog.d(TAG, "⏭️ Zaten var, atlandı: ${q.question.take(50)}...")
                } else {
                    val data = hashMapOf(
                        "question" to q.question,
                        "optionA" to q.optionA,
                        "optionB" to q.optionB,
                        "optionC" to q.optionC,
                        "optionD" to q.optionD,
                        "optionE" to q.optionE,
                        "correctAnswer" to q.correctAnswer,
                        "explanation" to q.explanation,
                        "lesson" to q.lesson,
                        "subjectId" to finalSubjectId,
                        "level" to level.name,
                        "schoolType" to schoolType.name,
                        "grade" to grade,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    
                    batch.set(col.document(docId), data)
                    ops++
                    addedCount++
                    
                    // Firestore batch limiti: 500
                    if (ops >= 450) {
                        batch.commit().await()
                        DebugLog.d(TAG, "✅ Batch commit: $ops soru kaydedildi")
                        batch = db.batch()
                        ops = 0
                    }
                }
            }
            
            // Kalan soruları commit et
            if (ops > 0) {
                batch.commit().await()
                DebugLog.d(TAG, "✅ Batch commit: $ops soru kaydedildi")
            }
        }
        
        if (skippedCount > 0) {
            DebugLog.d(TAG, "⚠️ $skippedCount soru zaten vardı, $addedCount yeni eklendi")
        }
        
        SaveResult(addedCount, skippedCount)
    }
    
    /**
     * FAZ 2: PARALEL BATCH WRITE - Chunk'ları paralel olarak kaydeder
     * 60 soru: 2sn → 0.5sn (%75 hızlanma)
     */
    private suspend fun saveQuestionsForLevelParallel(
        questions: List<QuestionModel>,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        finalSubjectId: String
    ): SaveResult = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext SaveResult(0, 0)
        
        // 1. Deduplicate - Aynı soru birden fazla chunk'ta olmasın
        val uniqueQuestions = questions.distinctBy { computeDocIdForQuestion(it) }
        
        // 2. 15'lik chunk'lara böl (4 paralel için optimal)
        val chunks = uniqueQuestions.chunked(15)
        
        // 3. Rate limit için semaphore (max 4 paralel)
        val semaphore = kotlinx.coroutines.sync.Semaphore(4)
        
        // 4. Paralel kaydet
        val results = coroutineScope {
            chunks.map { chunk ->
                async(Dispatchers.IO) {
                    semaphore.withPermit {
                        saveQuestionsForLevelBatch(chunk, level, schoolType, grade, finalSubjectId)
                    }
                }
            }.awaitAll()
        }
        
        // 5. Sonuçları topla
        val totalAdded = results.sumOf { it.added }
        val totalSkipped = results.sumOf { it.skipped }
        
        SaveResult(totalAdded, totalSkipped)
    }
    
    suspend fun saveQuestionsForLevel(
        questions: List<QuestionModel>,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        subjectId: String? = null
    ): Int {
        if (questions.isEmpty()) return 0

        // SubjectId'yi belirle
        val finalSubjectId = subjectId ?: run {
            val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
            val matchedSubject = subjects.find {
                it.displayName.equals(questions.first().lesson, ignoreCase = true) ||
                        it.displayName.contains(questions.first().lesson, ignoreCase = true) ||
                        questions.first().lesson.contains(it.displayName, ignoreCase = true)
            }
            matchedSubject?.id ?: questions.first().lesson.lowercase(Locale("tr", "TR"))
                .replace(" ", "_")
                .replace("ı", "i")
                .replace("ö", "o")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ğ", "g")
                .replace("ç", "c")
        }
        
        // FAZ 2: Paralel batch write kullan (60+ soru için)
        val result = if (questions.size >= 30) {
            DebugLog.d(TAG, "🚀 Paralel batch write başlatılıyor: ${questions.size} soru")
            saveQuestionsForLevelParallel(questions, level, schoolType, grade, finalSubjectId)
        } else {
            // Az sayıda soru için normal batch yeterli
            DebugLog.d(TAG, "📦 Batch write başlatılıyor: ${questions.size} soru")
            saveQuestionsForLevelBatch(questions, level, schoolType, grade, finalSubjectId)
        }
        
        val addedCount = result.added

        // Ayrıca eski yapıya da kaydet (geriye uyumluluk) - sadece yeni olanları
        if (addedCount > 0) {
            saveQuestionsToFirestore(questions.take(addedCount))
        }

        return addedCount
    }

    /**
     * Belirli bir seviye için soruları çeker
     */
    suspend fun getQuestionsForLevel(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        lessonId: String,
        limit: Int,
        userId: String,
        excludeDocIds: Set<String> = emptySet()
    ): List<QuestionModel> {
        DebugLog.d(TAG, "🔍 getQuestionsForLevel çağrıldı:")
        DebugLog.d(TAG, "   - level: ${level.name}")
        DebugLog.d(TAG, "   - schoolType: ${schoolType.name}")
        DebugLog.d(TAG, "   - grade: $grade")
        DebugLog.d(TAG, "   - lessonId: $lessonId")

        // DERS ID ÇÖZÜMLEME:
        // Gelen lessonId ("paragraf" veya "matematik" gibi) tam bir subjectId ("paragraf_5") olmayabilir.
        // CurriculumManager'dan doğru subjectId'yi bulmaya çalışıyoruz.
        // Bu mantık saveQuestionsForLevel ile aynı olmalı.
        val finalSubjectId = run {
            val validSchoolTypes = if (level == EducationLevel.ORTAOKUL) {
                // İmam Hatip ise de Standart müfredattan bak (çünkü oraya yönlendiriyoruz)
                 listOf(SchoolType.ORTAOKUL_STANDARD)
            } else {
                 listOf(schoolType)
            }
            
            var matchedId: String? = null
            
            for (type in validSchoolTypes) {
                 val subjects = CurriculumManager.getSubjectsFor(type, grade)
                 val matched = subjects.find {
                     it.displayName.equals(lessonId, ignoreCase = true) ||
                     it.displayName.contains(lessonId, ignoreCase = true) ||
                     lessonId.contains(it.displayName, ignoreCase = true) ||
                     it.id.equals(lessonId, ignoreCase = true)
                 }
                 if (matched != null) {
                     matchedId = matched.id
                     break
                 }
            }
            
            matchedId ?: lessonId // Bulamazsa gelen ID'yi kullan
        }
        
        DebugLog.d(TAG, "   - resolved subjectId: $finalSubjectId")
        
        val col = getLevelBasedCollection(level, schoolType, grade, finalSubjectId)
        DebugLog.d(TAG, "   - collection path: question_pools/${level.name}/${schoolType.name}/${grade ?: "general"}/$finalSubjectId")

        // Kullanıcının gördüğü soruları al
        val cloudSeenIds = getSeenQuestionIdsFromCloud(userId)
        val allExcluded = excludeDocIds + cloudSeenIds
        DebugLog.d(TAG, "   - excluded count: ${allExcluded.size}")

        val fetchSize = (limit * 3).coerceAtLeast(50).coerceAtMost(200)

        val rawList = runCatching {
            col.limit(fetchSize.toLong()).get().await()
                .toObjects(QuestionModel::class.java)
        }.getOrDefault(emptyList())
        
        DebugLog.d(TAG, "   - rawList count: ${rawList.size}")

        if (rawList.isEmpty()) {
            DebugLog.d(TAG, "❌ No level-based questions for $lessonId")
            // Normal sorular boşsa chart_questions'a bak
        }

        // Filtreleme (exclude listesine göre)
        val filteredRegular = rawList.filter { q ->
            val docId = computeDocIdForQuestion(q)
            docId !in allExcluded
        }
        
        // ==================== CHART QUESTIONS ENTEGRASYONU ====================
        // chart_questions koleksiyonundan da soru çek ve birleştir
        val chartQuestions = if (grade != null) {
            try {
                // Önce grade ile tüm chart soruları çek
                Log.d(TAG, "🔍 Chart questions query: grade=$grade, lessonId=$lessonId")
                
                val chartSnapshot = db.collection("chart_questions")
                    .whereEqualTo("grade", grade)
                    .limit(50)
                    .get()
                    .await()
                
                Log.d(TAG, "📊 chart_questions raw count for grade $grade: ${chartSnapshot.size()}")
                
                // Subject eşleştirmesi (çok esnek)
                val normalizedLessonId = lessonId.lowercase().replace("_", " ").replace("ı","i")
                    .replace("ö","o").replace("ü","u").replace("ş","s").replace("ğ","g").replace("ç","c")
                val normalizedSubjectId = finalSubjectId.lowercase().replace("_", " ").replace("ı","i")
                    .replace("ö","o").replace("ü","u").replace("ş","s").replace("ğ","g").replace("ç","c")
                
                Log.d(TAG, "📊 Matching: normalizedLessonId=$normalizedLessonId, normalizedSubjectId=$normalizedSubjectId")
                
                chartSnapshot.toObjects(ChartQuestionModel::class.java)
                    .filter { chart ->
                        val chartSubject = chart.subject.lowercase()
                        val normalizedChartSubject = chartSubject.replace("ı","i").replace("ö","o")
                            .replace("ü","u").replace("ş","s").replace("ğ","g").replace("ç","c")
                        
                        Log.d(TAG, "📊 Checking chart subject: $chartSubject -> normalized: $normalizedChartSubject")
                        
                        // Çoklu eşleştirme stratejisi
                        normalizedChartSubject == normalizedLessonId ||
                        normalizedChartSubject == normalizedSubjectId ||
                        normalizedChartSubject.contains(normalizedLessonId.take(5)) ||
                        normalizedLessonId.contains(normalizedChartSubject.take(5)) ||
                        // "matematik" her ikisinde de var mı?
                        normalizedChartSubject.startsWith(normalizedLessonId.take(3))
                    }
                    .take(10)
                    .map { chart ->
                        // ChartQuestionModel -> QuestionModel dönüşümü
                        QuestionModel(
                            question = chart.question,
                            optionA = chart.optionA,
                            optionB = chart.optionB,
                            optionC = chart.optionC,
                            optionD = chart.optionD,
                            optionE = chart.optionE,
                            correctAnswer = chart.correctAnswer,
                            explanation = chart.explanation,
                            graphicData = chart.vegaSpec,  // Vega-Lite spec
                            graphicType = "vega_chart"      // Grafik türü
                        )
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Chart questions fetch error: ${e.message}")
                emptyList()
            }
        } else {
            Log.d(TAG, "📊 Grade is null, skipping chart questions")
            emptyList()
        }
        
        Log.d(TAG, "📊 Chart questions matched: ${chartQuestions.size}")
        
        // Birleştir ve shuffle et
        val combined = (filteredRegular + chartQuestions).shuffled().take(limit)

        return combined
    }

    // ==================== KARMA DENEME SINAVI SORU ÇEKİMİ ====================
    
    /**
     * Deneme sınavları için birden fazla dersten soru çeker ve birleştirir.
     * GENEL_DENEME (70 soru): Tr:18, Mat:18, Fen:12, Sos:12, İng:5, Din:5
     * MARATON (120 soru): Tr:32, Mat:32, Fen:22, Sos:22, İng:6, Din:6
     */
    suspend fun getQuestionsForMixedExam(
        examType: String,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        userId: String,
        excludeDocIds: Set<String>
    ): List<QuestionModel> {
        
        // Soru dağılımı tanımları
        // Soru dağılımını Sınav Tipine göre belirle
        val distribution = when (examType) {
            "GENEL_DENEME" -> {
                // Standart İlkokul/Ortaokul (70 Soru)
                mapOf(
                    "turkce" to 20,
                    "matematik" to 20,
                    "fen" to 12,
                    "sosyal" to 8,
                    "ingilizce" to 6,
                    "din_kulturu" to 4
                )
            }
            "MARATON" -> {
                // Standart İlkokul/Ortaokul (120 Soru)
                mapOf(
                    "turkce" to 34,
                    "matematik" to 34,
                    "fen" to 22,
                    "sosyal" to 14,
                    "ingilizce" to 10,
                    "din_kulturu" to 6
                )
            }
            else -> return emptyList()
        }
        
        val allQuestions = mutableListOf<QuestionModel>()
        
        for ((baseId, count) in distribution) {
            // Ders ID'sini seviyeye ve sınıfa göre uyarla (örn: turkce -> turkce_4)
            val realLessonId = if (grade != null) {
                when (baseId) {
                    "din_kulturu" -> "din_$grade" // Config'de 'din_4' vs geçiyor
                    else -> "${baseId}_$grade"
                }
            } else baseId

            try {
                // Deneme sınavlarında görülen sorular filtrelenmez - öğrenciler tekrar çözebilmeli
                val questions = getQuestionsForLevel(
                    level = level,
                    schoolType = schoolType,
                    grade = grade,
                    lessonId = realLessonId,
                    limit = count + 20, // Filtreleme payı
                    userId = "", // Boş userId = görülen sorular filtrelenmez
                    excludeDocIds = allQuestions.map { computeDocIdForQuestion(it) }.toSet() // Sadece bu oturumdaki tekrarları engelle
                )
                
                // GRAFİK FİLTRESİ KALDIRILDI - Grafikli sorular da değerli, gösterilmeli
                // Sadece çok büyük/karmaşık grafikleri filtrele (opsiyonel)
                val cleanQuestions = questions.filter { 
                    val graphicData = it.graphicData ?: ""
                    val graphicType = it.graphicType ?: ""
                    // Grafik yoksa veya basit grafikse kabul et
                    graphicData.isBlank() || graphicData.length < 5000 // 5KB'dan küçük grafikler OK
                }
                
                DebugLog.d(TAG, "MixedExam: $realLessonId -> ${questions.size} toplam, ${cleanQuestions.size} kullanılabilir, hedef: $count")
                
                // DERS ADI NORMALİZASYONU - Ders geçiş uyarısı için tutarlılık
                val displayName = when (baseId) {
                    "turkce" -> "Türkçe"
                    "matematik" -> "Matematik"
                    "fen" -> "Fen Bilimleri"
                    "sosyal" -> "Sosyal Bilgiler"
                    "ingilizce" -> "İngilizce"
                    "din_kulturu" -> "Din Kültürü"
                    "arapca" -> "Arapça"
                    else -> baseId.replaceFirstChar { it.uppercase() }
                }
                
                // İstenen sayı kadar al, lesson alanını düzelt (Sıralamayı bozmadan kendi içinde karıştır)
                val normalizedQuestions = cleanQuestions.take(count).map { it.copy(lesson = displayName) }.shuffled()
                allQuestions.addAll(normalizedQuestions)
                
                // Eksik soru uyarısı
                if (normalizedQuestions.size < count) {
                    Log.w(TAG, "⚠️ MixedExam: $realLessonId -> Hedef: $count, Bulunan: ${normalizedQuestions.size} (${count - normalizedQuestions.size} eksik!)")
                } else {
                    DebugLog.d(TAG, "✅ MixedExam: $realLessonId -> ${normalizedQuestions.size}/$count soru eklendi ($displayName)")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "MixedExam hata ($realLessonId): ${e.message}")
            }
        }
        
        DebugLog.d(TAG, "MixedExam toplam: ${allQuestions.size} soru")
        
        // Hedef soru sayısını kontrol et
        val expectedTotal = distribution.values.sum()
        if (allQuestions.size < expectedTotal) {
            Log.w(TAG, "⚠️ MixedExam EKSIK SORU: Hedef: $expectedTotal, Bulunan: ${allQuestions.size} (${expectedTotal - allQuestions.size} eksik!)")
        } else {
            DebugLog.d(TAG, "✅ MixedExam TAMAMLANDI: ${allQuestions.size}/$expectedTotal soru")
        }
        
        return allQuestions // Ders sırasını koru (Türkçe -> Matematik -> ...), sadece ders içi karışık
    }

    /**
     * Belirli bir seviyenin sorularını siler
     */
    suspend fun deleteQuestionsForLevel(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ) {
        val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)

        for (subject in subjects) {
            val col = getLevelBasedCollection(level, schoolType, grade, subject.id)
            runCatching {
                val snap = col.get().await()
                for (doc in snap.documents) {
                    doc.reference.delete().await()
                }
            }.onFailure {
                Log.w(TAG, "deleteQuestionsForLevel failed for ${subject.id}: ${it.message}")
            }
        }
    }

    // ==================== GEÇMİŞ SINAV SORULARI ====================

    /**
     * Geçmiş KPSS sorularını Firestore'dan çeker
     * Collection yapısı: past_exams/KPSS/{yıl}/{ders}/items
     */
    suspend fun getPastKpssQuestions(
        year: Int? = null,
        subject: String? = null,
        limit: Int = 50
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val results = mutableListOf<QuestionModel>()

        try {
            val baseRef = db.collection("past_exams").document("KPSS")

            // Yıl belirtilmişse sadece o yılı çek
            val yearsToQuery = if (year != null) {
                listOf(year.toString())
            } else {
                // Tüm yılları çek (2020-2024)
                (2020..2024).map { it.toString() }
            }

            for (yearStr in yearsToQuery) {
                val yearRef = baseRef.collection(yearStr)

                // Ders belirtilmişse sadece o dersi çek
                val subjectsToQuery = if (subject != null) {
                    listOf(subject)
                } else {
                    // Tüm KPSS derslerini çek
                    listOf("turkce", "matematik", "tarih", "cografya", "vatandaslik", "guncel")
                }

                for (subj in subjectsToQuery) {
                    val itemsRef = yearRef.document(subj).collection("items")
                    val snapshot = itemsRef.limit(limit.toLong()).get().await()

                    for (doc in snapshot.documents) {
                        val q = doc.toObject(QuestionModel::class.java)
                        if (q != null) {
                            results.add(q.copy(
                                lesson = "${subj.replaceFirstChar { it.uppercase() }} (KPSS $yearStr)"
                            ))
                        }
                    }
                }
            }

            DebugLog.d(TAG, "📚 Geçmiş KPSS soruları: ${results.size} adet")
        } catch (e: Exception) {
            Log.e(TAG, "getPastKpssQuestions error: ${e.message}")
        }

        results.shuffled().take(limit)
    }

    /**
     * Geçmiş LGS sorularını Firestore'dan çeker
     * Collection yapısı: past_exams/LGS/{yıl}/{ders}/items
     */
    suspend fun getPastLgsQuestions(
        year: Int? = null,
        subject: String? = null,
        limit: Int = 50
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val results = mutableListOf<QuestionModel>()

        try {
            val baseRef = db.collection("past_exams").document("LGS")

            val yearsToQuery = if (year != null) {
                listOf(year.toString())
            } else {
                (2018..2024).map { it.toString() }
            }

            for (yearStr in yearsToQuery) {
                val yearRef = baseRef.collection(yearStr)

                val subjectsToQuery = if (subject != null) {
                    listOf(subject)
                } else {
                    listOf("turkce", "matematik", "fen", "sosyal", "ingilizce", "dkab")
                }

                for (subj in subjectsToQuery) {
                    val itemsRef = yearRef.document(subj).collection("items")
                    val snapshot = itemsRef.limit(limit.toLong()).get().await()

                    for (doc in snapshot.documents) {
                        val q = doc.toObject(QuestionModel::class.java)
                        if (q != null) {
                            results.add(q.copy(
                                lesson = "${subj.replaceFirstChar { it.uppercase() }} (LGS $yearStr)"
                            ))
                        }
                    }
                }
            }

            DebugLog.d(TAG, "📚 Geçmiş LGS soruları: ${results.size} adet")
        } catch (e: Exception) {
            Log.e(TAG, "getPastLgsQuestions error: ${e.message}")
        }

        results.shuffled().take(limit)
    }

    /**
     * Mevcut geçmiş sınav yıllarını döndürür
     */
    suspend fun getAvailableExamYears(examType: String): List<Int> = withContext(Dispatchers.IO) {
        val years = mutableListOf<Int>()
        try {
            val baseRef = db.collection("past_exams").document(examType)
            val collections = baseRef.collection("years_index").get().await()
            
            // Alternatif: Bilinen yılları döndür
            years.addAll(
                if (examType == "LGS") (2018..2024).toList()
                else (2020..2024).toList()
            )
        } catch (e: Exception) {
            Log.w(TAG, "getAvailableExamYears error: ${e.message}")
            // Fallback
            years.addAll(
                if (examType == "LGS") (2018..2024).toList()
                else (2020..2024).toList()
            )
        }
        years
    }

    /**
     * Geçmiş AGS sorularını Firestore'dan çeker
     * Collection yapısı: past_exams/AGS/{session}/{yıl}/{branch?}/items
     */
    suspend fun getPastAgsQuestions(
        session: String, // "oturum1" veya "oturum2_oatb"
        branch: String? = null,
        year: Int? = null,
        limit: Int = 50
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val results = mutableListOf<QuestionModel>()

        try {
            val baseRef = db.collection("past_exams").document("AGS").collection(session)

            val yearsToQuery = if (year != null) {
                listOf(year.toString())
            } else {
                (2020..2024).map { it.toString() }
            }

            for (yearStr in yearsToQuery) {
                val yearRef = baseRef.document(yearStr)
                
                if (session == "oturum2_oatb" && branch != null) {
                    // ÖATB: branş bazlı
                    val itemsRef = yearRef.collection(branch).document("items").collection("questions")
                    val snapshot = itemsRef.limit(limit.toLong()).get().await()
                    
                    for (doc in snapshot.documents) {
                        val q = doc.toObject(QuestionModel::class.java)
                        if (q != null) {
                            results.add(q.copy(
                                lesson = "AGS ÖATB - $branch ($yearStr)"
                            ))
                        }
                    }
                } else {
                    // MEB AGS (1. Oturum): tek tip
                    val itemsRef = yearRef.collection("items")
                    val snapshot = itemsRef.limit(limit.toLong()).get().await()
                    
                    for (doc in snapshot.documents) {
                        val q = doc.toObject(QuestionModel::class.java)
                        if (q != null) {
                            results.add(q.copy(
                                lesson = "AGS MEB ($yearStr)"
                            ))
                        }
                    }
                }
            }

            DebugLog.d(TAG, "📚 Geçmiş AGS soruları: ${results.size} adet")
        } catch (e: Exception) {
            Log.e(TAG, "getPastAgsQuestions error: ${e.message}")
        }

        results.shuffled().take(limit)
    }

    suspend fun getTotalSystemQuestionCount(): Int = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("system_stats").document("general").get().await()
            val count = doc.getLong("total_questions") ?: 0L
            count.toInt()
        } catch (e: Exception) {
            Log.e(TAG, "getTotalSystemQuestionCount error: ${e.message}")
            0
        }
    }

    private fun incrementGlobalCounter(amount: Int) {
        val ref = db.collection("system_stats").document("general")
        ref.set(mapOf("total_questions" to FieldValue.increment(amount.toLong())), SetOptions.merge())
    }

    suspend fun recalculateGlobalCounts(): Int = withContext(Dispatchers.IO) {
        var total = 0
        try {
            // Basit bir yaklaşımla bilinen koleksiyonları topla
            // Not: Bu çok maliyetli olabilir, sadece admin manuel tetiklemeli
            // Şimdilik sadece "system_stats" varsa onu döndür, yoksa 0.
            // Gerçek sayım için tüm koleksiyonları gezmek gerekir.
            // Bu örnekte sadece sayaç düzeltme mantığı ekliyoruz.
            // Gelecekte buraya detaylı sayım eklenebilir.
            val current = getTotalSystemQuestionCount()
            if (current == 0) {
                 // Belki bir başlangıç değeri atanabilir veya manuel set edilebilir
            }
            total = current
        } catch (e: Exception) {
            Log.e(TAG, "recalculateGlobalCounts error: ${e.message}")
        }
        total
    }

    // ==================== KPSS DENEME PAKETLERİ ====================
    
    /**
     * KPSS Deneme paketini Firestore'a kaydeder
     * Her paket 120 soru içerir ve ayrı bir koleksiyonda saklanır
     * 
     * Yapı: kpss_deneme_paketleri/{paketNo}/sorular/{soruNo}
     */
    suspend fun saveKpssDenemePaketi(
        paketNo: Int,
        questions: List<QuestionModel>,
        seviye: SchoolType
    ): Int = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext 0
        
        val paketId = "paket_$paketNo"
        val paketRef = db.collection("kpss_deneme_paketleri").document(paketId)
        
        var savedCount = 0
        
        try {
            // Paket metadata'sını kaydet
            val metadata = hashMapOf(
                "paketNo" to paketNo,
                "toplamSoru" to questions.size,
                "olusturmaTarihi" to FieldValue.serverTimestamp(),
                "durum" to "aktif",
                "seviye" to seviye.name, // KPSS_LISANS vb.
                "dersDagilimi" to mapOf(
                    "turkce" to questions.count { it.lesson?.contains("turkce", true) == true },
                    "matematik" to questions.count { it.lesson?.contains("matematik", true) == true },
                    "tarih" to questions.count { it.lesson?.contains("tarih", true) == true },
                    "cografya" to questions.count { it.lesson?.contains("cografya", true) == true },
                    "vatandaslik" to questions.count { it.lesson?.contains("vatandaslik", true) == true },
                    "guncel" to questions.count { it.lesson?.contains("guncel", true) == true }
                )
            )
            paketRef.set(metadata, SetOptions.merge()).await()
            
            // Her soruyu kaydet
            val sorularRef = paketRef.collection("sorular")
            
            for ((index, q) in questions.withIndex()) {
                val soruNo = index + 1
                val docId = "soru_${soruNo.toString().padStart(3, '0')}" // soru_001, soru_002...
                
                val data = hashMapOf(
                    "soruNo" to soruNo,
                    "question" to q.question,
                    "optionA" to q.optionA,
                    "optionB" to q.optionB,
                    "optionC" to q.optionC,
                    "optionD" to q.optionD,
                    "optionE" to q.optionE,
                    "correctAnswer" to q.correctAnswer,
                    "explanation" to q.explanation,
                    "lesson" to q.lesson,
                    "topic" to q.topic,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                
                runCatching {
                    sorularRef.document(docId).set(data).await()
                    savedCount++
                }.onFailure {
                    Log.e(TAG, "KPSS Deneme soru kayıt hatası [$soruNo]: ${it.message}")
                }
            }
            
            // Global sayacı güncelle
            incrementGlobalCounter(savedCount)
            
            Log.d(TAG, "✅ KPSS Deneme #$paketNo kaydedildi: $savedCount soru")
            
        } catch (e: Exception) {
            Log.e(TAG, "saveKpssDenemePaketi error: ${e.message}")
        }
        
        savedCount
    }
    
    /**
     * KPSS Deneme paketlerini listeler
     * @return Map<paketNo, metadata>
     */
    suspend fun getKpssDenemePaketleri(): List<Map<String, Any?>> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Map<String, Any?>>()
        
        try {
            val snapshot = db.collection("kpss_deneme_paketleri")
                .orderBy("paketNo")
                .get()
                .await()
            
            for (doc in snapshot.documents) {
                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                data["id"] = doc.id
                result.add(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getKpssDenemePaketleri error: ${e.message}")
        }
        
        result
    }
    
    /**
     * Belirli bir KPSS deneme paketinin sorularını çeker
     * @param paketNo Deneme paket numarası
     * @return Sıralı soru listesi (120 soru)
     */
    suspend fun getKpssDenemeSorulari(paketNo: Int): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        try {
            val paketId = "paket_$paketNo"
            val snapshot = db.collection("kpss_deneme_paketleri")
                .document(paketId)
                .collection("sorular")
                .orderBy("soruNo")
                .get()
                .await()
            
            for (doc in snapshot.documents) {
                val q = QuestionModel(
                    id = doc.id,
                    questionNumber = doc.getLong("soruNo")?.toInt() ?: 0,
                    question = doc.getString("question") ?: "",
                    optionA = doc.getString("optionA") ?: "",
                    optionB = doc.getString("optionB") ?: "",
                    optionC = doc.getString("optionC") ?: "",
                    optionD = doc.getString("optionD") ?: "",
                    optionE = doc.getString("optionE") ?: "",
                    correctAnswer = doc.getString("correctAnswer") ?: "",
                    explanation = doc.getString("explanation") ?: "",
                    lesson = doc.getString("lesson") ?: "",
                    topic = doc.getString("topic") ?: "",
                    level = EducationLevel.KPSS
                )
                result.add(q)
            }
            
            Log.d(TAG, "📚 KPSS Deneme #$paketNo: ${result.size} soru yüklendi")
        } catch (e: Exception) {
            Log.e(TAG, "getKpssDenemeSorulari error: ${e.message}")
        }
        
        result
    }
    
    /**
     * KPSS Deneme paketi sayısını döndürür
     */
    suspend fun getKpssDenemePaketiSayisi(): Int = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("kpss_deneme_paketleri").get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.e(TAG, "getKpssDenemePaketiSayisi error: ${e.message}")
            0
        }
    }
    
    // ==================== KPSS DENEME DURUM TAKİBİ ====================
    
    data class DenemeDurumu(
        val paketNo: Int = 0,
        val seviye: String = "KPSS_LISANS",
        val durum: String = "cozulmedi", // cozulmedi, devam_ediyor, tamamlandi
        val sonKalinanSoru: Int = 0,
        val dogru: Int = 0,
        val yanlis: Int = 0,
        val bos: Int = 0,
        val baslangicTarihi: Long = 0,
        val bitisTarihi: Long? = null,
        val cevaplar: Map<String, String> = emptyMap()
    )
    
    suspend fun saveDenemeDurumu(userId: String, paketNo: Int, durumu: DenemeDurumu): Boolean = withContext(Dispatchers.IO) {
        try {
            db.collection("users").document(userId)
                .collection("kpss_deneme_durumu").document("paket_$paketNo")
                .set(mapOf(
                    "paketNo" to durumu.paketNo,
                    "seviye" to durumu.seviye,
                    "durum" to durumu.durum,
                    "sonKalinanSoru" to durumu.sonKalinanSoru,
                    "dogru" to durumu.dogru,
                    "yanlis" to durumu.yanlis,
                    "bos" to durumu.bos,
                    "baslangicTarihi" to durumu.baslangicTarihi,
                    "bitisTarihi" to durumu.bitisTarihi,
                    "cevaplar" to durumu.cevaplar
                ), SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "saveDenemeDurumu error: ${e.message}")
            false
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    suspend fun getDenemeDurumu(userId: String, paketNo: Int): DenemeDurumu? = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("users").document(userId)
                .collection("kpss_deneme_durumu").document("paket_$paketNo").get().await()
            if (!doc.exists()) return@withContext null
            DenemeDurumu(
                paketNo = doc.getLong("paketNo")?.toInt() ?: paketNo,
                seviye = doc.getString("seviye") ?: "KPSS_LISANS",
                durum = doc.getString("durum") ?: "cozulmedi",
                sonKalinanSoru = doc.getLong("sonKalinanSoru")?.toInt() ?: 0,
                dogru = doc.getLong("dogru")?.toInt() ?: 0,
                yanlis = doc.getLong("yanlis")?.toInt() ?: 0,
                bos = doc.getLong("bos")?.toInt() ?: 0,
                baslangicTarihi = doc.getLong("baslangicTarihi") ?: 0,
                bitisTarihi = doc.getLong("bitisTarihi"),
                cevaplar = (doc.get("cevaplar") as? Map<String, String>) ?: emptyMap()
            )
        } catch (e: Exception) {
            Log.e(TAG, "getDenemeDurumu error: ${e.message}")
            null
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    suspend fun getAllDenemeDurumlari(userId: String): List<DenemeDurumu> = withContext(Dispatchers.IO) {
        try {
            db.collection("users").document(userId).collection("kpss_deneme_durumu").get().await()
                .documents.mapNotNull { doc ->
                    DenemeDurumu(
                        paketNo = doc.getLong("paketNo")?.toInt() ?: 0,
                        seviye = doc.getString("seviye") ?: "KPSS_LISANS",
                        durum = doc.getString("durum") ?: "cozulmedi",
                        sonKalinanSoru = doc.getLong("sonKalinanSoru")?.toInt() ?: 0,
                        dogru = doc.getLong("dogru")?.toInt() ?: 0,
                        yanlis = doc.getLong("yanlis")?.toInt() ?: 0,
                        bos = doc.getLong("bos")?.toInt() ?: 0,
                        baslangicTarihi = doc.getLong("baslangicTarihi") ?: 0,
                        bitisTarihi = doc.getLong("bitisTarihi"),
                        cevaplar = (doc.get("cevaplar") as? Map<String, String>) ?: emptyMap()
                    )
                }.sortedBy { it.paketNo }
        } catch (e: Exception) {
            Log.e(TAG, "getAllDenemeDurumlari error: ${e.message}")
            emptyList()
        }
    }
    // ==================== KPSS DENEME SİLME ====================
    
    /**
     * Tüm KPSS Deneme paketlerini siler
     */
    suspend fun deleteAllKpssDenemePackages(): Int = withContext(Dispatchers.IO) {
        var deletedCount = 0
        try {
            val snapshot = db.collection("kpss_deneme_paketleri").get().await()
            val batchSize = 500 // Firestore batch limit
            var batch = db.batch()
            var counter = 0
            
            for (doc in snapshot.documents) {
                // Önce alt koleksiyonu (sorular) sil
                val sorularSnap = doc.reference.collection("sorular").get().await()
                for (soruDoc in sorularSnap.documents) {
                    batch.delete(soruDoc.reference)
                    counter++
                    if (counter >= batchSize) {
                        batch.commit().await()
                        batch = db.batch()
                        counter = 0
                    }
                }
                
                // Ana paket dökümanını sil
                batch.delete(doc.reference)
                deletedCount++
                counter++
                
                if (counter >= batchSize) {
                    batch.commit().await()
                    batch = db.batch()
                    counter = 0
                }
            }
            
            // Kalanları commit et
            if (counter > 0) {
                batch.commit().await()
            }
            
            Log.d(TAG, "🗑️ Tüm KPSS Deneme Paketleri silindi: $deletedCount paket")
            
        } catch (e: Exception) {
            Log.e(TAG, "deleteAllKpssDenemePackages error: ${e.message}")
        }
        deletedCount
    }
}

