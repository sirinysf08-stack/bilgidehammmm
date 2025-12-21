package com.example.bilgideham

import android.content.Context
import androidx.room.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

// --- 1. YARDIMCI MODEL (Sildiğimiz GameData.kt'den buraya aldık) ---
// AI Generator ve API işlemleri bu basit sınıfı kullanır.
data class GameQuestion(
    val lesson: String,
    val text: String,
    val correctIndex: Int,
    val options: List<String>
)

// --- 2. VERİTABANI TABLO YAPISI (Entity) ---
@Entity(tableName = "game_questions")
data class GameQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lesson: String,
    val text: String,
    val correctIndex: Int,
    val optionsJson: String,
    val isSolved: Boolean = false
)

// --- 3. VERİ ERİŞİM NESNESİ (DAO) ---
@Dao
interface GameQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<GameQuestionEntity>)

    // Tekil Rastgele Soru (Oyunlar için)
    @Query("SELECT * FROM game_questions WHERE lesson = :lesson AND isSolved = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomUnsolvedQuestion(lesson: String): GameQuestionEntity?

    @Query("SELECT * FROM game_questions WHERE lesson = :lesson")
    suspend fun getAllQuestionsByLesson(lesson: String): List<GameQuestionEntity>

    @Query("UPDATE game_questions SET isSolved = 1 WHERE id = :id")
    suspend fun markAsSolved(id: Int)

    @Query("SELECT COUNT(*) FROM game_questions WHERE lesson = :lesson")
    suspend fun getQuestionCount(lesson: String): Int

    @Query("SELECT COUNT(*) FROM game_questions WHERE lesson = :lesson AND isSolved = 0")
    suspend fun getUnsolvedCount(lesson: String): Int

    // Eski soruları temizlemek için
    @Query("DELETE FROM game_questions")
    suspend fun clearAll()
}

// --- 4. VERİTABANI ---
@Database(entities = [GameQuestionEntity::class], version = 2, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameQuestionDao

    companion object {
        @Volatile private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "bilgideham_game_db"
                )
                    .fallbackToDestructiveMigration() // Versiyon değişince eskileri silip yenisini kurar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- 5. REPOSITORY (YENİ SİSTEM) ---
object GameRepositoryNew {
    private lateinit var database: GameDatabase
    private val gson = Gson()

    // Firebase Firestore bağlantısı
    private val db = Firebase.firestore
    private const val COLLECTION_NAME = "global_game_pool"

    fun init(context: Context) {
        database = GameDatabase.getDatabase(context)
    }

    // --- ADMIN İÇİN: BULUTA YÜKLEME ---
    suspend fun generateAndUploadToCloud() {
        val generator = AiQuestionGenerator()
        val lessons = listOf(
            "MATH" to "Matematik", "SCIENCE" to "Fen", "SOCIAL" to "Sosyal",
            "TURKISH" to "Turkce", "ENGLISH" to "Ingilizce"
        )

        val newQuestionsBatch = mutableListOf<Map<String, Any>>()

        for ((apiTag, dbTag) in lessons) {
            try {
                // Her dersten 20 soru üret (AiQuestionGenerator buradaki GameQuestion sınıfını kullanır)
                val apiQuestions = generator.generateMiniGameBatch(apiTag, 20)

                apiQuestions.forEach { q ->
                    val map = hashMapOf(
                        "lesson" to dbTag,
                        "text" to q.text,
                        "correctIndex" to q.correctIndex,
                        "optionsJson" to gson.toJson(q.options)
                    )
                    newQuestionsBatch.add(map)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Buluta Kaydet (Toplu Yazma)
        if (newQuestionsBatch.isNotEmpty()) {
            val batch = db.batch()
            newQuestionsBatch.forEach { data ->
                val ref = db.collection(COLLECTION_NAME).document()
                batch.set(ref, data)
            }
            batch.commit().await()
        }
    }

    // --- KULLANICI İÇİN: BULUTTAN İNDİRME ---
    suspend fun syncFromCloudToDevice() {
        if (!::database.isInitialized) return

        try {
            // 1. Buluttaki tüm oyun sorularını çek
            val snapshot = db.collection(COLLECTION_NAME).get().await()

            if (!snapshot.isEmpty) {
                val entities = snapshot.documents.map { doc ->
                    GameQuestionEntity(
                        lesson = doc.getString("lesson") ?: "",
                        text = doc.getString("text") ?: "",
                        correctIndex = doc.getLong("correctIndex")?.toInt() ?: 0,
                        optionsJson = doc.getString("optionsJson") ?: "[]",
                        isSolved = false
                    )
                }

                // 2. Telefondaki eski oyun sorularını sil
                database.gameDao().clearAll()

                // 3. Yenileri kaydet
                database.gameDao().insertQuestions(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getQuestionForGame(lesson: String): TextRallyQuestion {
        if (!::database.isInitialized) return TextRallyQuestion("Yükleniyor...", 0, listOf("-", "-", "-", "-"))

        // Önce yerelden dene
        val entity = database.gameDao().getRandomUnsolvedQuestion(lesson)

        return if (entity != null) {
            val optsType = object : TypeToken<List<String>>() {}.type
            val options: List<String> = gson.fromJson(entity.optionsJson, optsType)
            TextRallyQuestion(entity.text, entity.correctIndex, options)
        } else {
            // Soru yoksa
            TextRallyQuestion("Soru Kalmadı! (Yükleniyor...)", 0, listOf("-", "-", "-", "-"))
        }
    }

    suspend fun getAllQuestionsForExam(lesson: String): List<GameQuestionEntity> {
        if (!::database.isInitialized) return emptyList()
        return database.gameDao().getAllQuestionsByLesson(lesson)
    }

    suspend fun getStats(): Map<String, Int> {
        if (!::database.isInitialized) return emptyMap()
        return mapOf(
            "Matematik" to database.gameDao().getQuestionCount("Matematik"),
            "Fen" to database.gameDao().getQuestionCount("Fen"),
            "Sosyal" to database.gameDao().getQuestionCount("Sosyal"),
            "Turkce" to database.gameDao().getQuestionCount("Turkce"),
            "Ingilizce" to database.gameDao().getQuestionCount("Ingilizce")
        )
    }

    suspend fun markQuestionSolved(text: String) {
        // İleride ID ile yapılabilir
    }
}