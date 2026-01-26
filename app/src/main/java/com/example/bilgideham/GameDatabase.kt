package com.example.bilgideham

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

// --- 1. YARDIMCI MODEL ---
data class GameQuestion(
    val lesson: String,
    val text: String,
    val correctIndex: Int,
    val options: List<String>
)

// --- 2. ROOM ENTITY ---
@Entity(tableName = "game_questions")
data class GameQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lesson: String,
    val text: String,
    val correctIndex: Int,
    val optionsJson: String,
    val isSolved: Boolean = false
)

// --- 3. DAO ---
@Dao
interface GameQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<GameQuestionEntity>)

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

    @Query("DELETE FROM game_questions")
    suspend fun clearAll()
}

// --- 4. DATABASE ---
@Database(entities = [GameQuestionEntity::class], version = 2, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameQuestionDao

    companion object {
        @Volatile private var INSTANCE: GameDatabase? = null
        
        // Migration: v1 -> v2 (veri koruma)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // v2'de şema değişikliği yoksa boş bırak
                // Gelecekte sütun eklenirse burada ALTER TABLE kullan
            }
        }

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "bilgideham_game_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigrationFrom(1) // Sadece v1'den kayıp kabul edilir
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- 5. REPOSITORY ---
object GameRepositoryNew {
    private lateinit var database: GameDatabase
    private val gson = Gson()

    private val db = Firebase.firestore
    private const val COLLECTION_NAME = "global_game_pool"

    fun init(context: Context) {
        database = GameDatabase.getDatabase(context)
    }

    private fun ensureDbReady() = ::database.isInitialized

    // --- ADMIN: BULUTA YÜKLEME ---
    suspend fun generateAndUploadToCloud() {
        val generator = AiQuestionGenerator()
        val lessons = listOf(
            "MATH" to "Matematik",
            "SCIENCE" to "Fen",
            "SOCIAL" to "Sosyal",
            "TURKISH" to "Turkce",
            "ENGLISH" to "Ingilizce"
        )

        val newQuestionsBatch = mutableListOf<Map<String, Any>>()

        for ((apiTag, dbTag) in lessons) {
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (newQuestionsBatch.isNotEmpty()) {
            val batch = db.batch()
            newQuestionsBatch.forEach { data ->
                val ref = db.collection(COLLECTION_NAME).document()
                batch.set(ref, data)
            }
            batch.commit().await()
        }
    }

    // --- KULLANICI: BULUTTAN İNDİRME ---
    // Kurumsal düzeltme: Bulut boşsa da local'i temizler.
    suspend fun syncFromCloudToDevice() {
        if (!ensureDbReady()) return

        try {
            val snapshot = db.collection(COLLECTION_NAME).get().await()

            // 1) Önce local'i her koşulda sıfırla (bulut boş da olabilir)
            database.gameDao().clearAll()

            // 2) Bulutta veri varsa yükle
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
                database.gameDao().insertQuestions(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- ADMIN: LOCAL OYUN HAVUZU SIFIRLAMA ---
    suspend fun clearLocalAll() {
        if (!ensureDbReady()) return
        database.gameDao().clearAll()
    }

    // --- ADMIN: BULUT OYUN HAVUZU SIFIRLAMA (Batch Delete / Pagination) ---
    suspend fun clearCloudAll() {
        // Firestore batch limit nedeniyle parçalı silme
        while (true) {
            val snap = db.collection(COLLECTION_NAME).limit(450).get().await()
            if (snap.isEmpty) break

            val batch = db.batch()
            for (doc in snap.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
    }

    suspend fun getQuestionForGame(lesson: String): TextRallyQuestion {
        if (!ensureDbReady()) return TextRallyQuestion("Yükleniyor...", 0, listOf("-", "-", "-", "-"))

        val entity = database.gameDao().getRandomUnsolvedQuestion(lesson)
        return if (entity != null) {
            val optsType = object : TypeToken<List<String>>() {}.type
            val options: List<String> = gson.fromJson(entity.optionsJson, optsType)
            TextRallyQuestion(entity.text, entity.correctIndex, options)
        } else {
            TextRallyQuestion("Soru Kalmadı! (Yükleniyor...)", 0, listOf("-", "-", "-", "-"))
        }
    }

    suspend fun getAllQuestionsForExam(lesson: String): List<GameQuestionEntity> {
        if (!ensureDbReady()) return emptyList()
        return database.gameDao().getAllQuestionsByLesson(lesson)
    }

    suspend fun getStats(): Map<String, Int> {
        if (!ensureDbReady()) return emptyMap()
        return mapOf(
            "Matematik" to database.gameDao().getQuestionCount("Matematik"),
            "Fen" to database.gameDao().getQuestionCount("Fen"),
            "Sosyal" to database.gameDao().getQuestionCount("Sosyal"),
            "Turkce" to database.gameDao().getQuestionCount("Turkce"),
            "Ingilizce" to database.gameDao().getQuestionCount("Ingilizce")
        )
    }

    suspend fun markQuestionSolved(text: String) {
        // Not: Şu an text ile çözme işaretleme yok.
        // Kurumsal öneri: entity.id üzerinden markAsSolved bağlanmalı.
    }
}
