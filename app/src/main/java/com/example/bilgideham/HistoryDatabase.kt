package com.example.bilgideham

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Tablo
@Entity(tableName = "solved_questions")
data class SolvedQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lesson: String,
    val questionText: String,
    val questionFp: String, // ✅ tekrar engelleme / deduplikasyon için fingerprint (docId)
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val explanation: String,
    val dateParams: String, // Format: "dd MMM HH:mm"
    val examType: String
)

// 2. DAO
@Dao
interface HistoryDao {
    @Insert
    suspend fun insertQuestion(question: SolvedQuestionEntity)

    @Query("SELECT * FROM solved_questions ORDER BY id DESC")
    fun getAllHistory(): Flow<List<SolvedQuestionEntity>>

    // --- RAPOR İÇİN ---
    @Query("SELECT * FROM solved_questions")
    suspend fun getAllHistoryList(): List<SolvedQuestionEntity>

    @Query("SELECT questionText FROM solved_questions")
    suspend fun getSolvedQuestionTexts(): List<String>

    @Query("SELECT questionFp FROM solved_questions")
    suspend fun getSolvedQuestionFps(): List<String>

    @Query("DELETE FROM solved_questions")
    suspend fun clearHistory()
}

// 3. Database
@Database(entities = [SolvedQuestionEntity::class], version = 6, exportSchema = false)
abstract class MainHistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MainHistoryDatabase? = null

        fun getDatabase(context: Context): MainHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainHistoryDatabase::class.java,
                    "bilgideham_final_history_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Repository
object HistoryRepository {
    private var database: MainHistoryDatabase? = null

    fun init(context: Context) {
        if (database == null) {
            database = MainHistoryDatabase.getDatabase(context)
        }
    }

    suspend fun saveAnswer(q: QuestionModel, userSelected: String, examTitle: String?) {
        val db = database ?: return
        try {
            val isCorrect = (userSelected == q.correctAnswer)
            val dateStr = SimpleDateFormat("dd MMM HH:mm", Locale("tr", "TR")).format(Date())

            val fp = try {
                QuestionRepository.computeDocIdForQuestion(q)
            } catch (_: Exception) {
                // En kötü senaryo: soru metni bazlı (daha zayıf) fp
                (q.question.trim() + "|" + q.optionA + "|" + q.optionB + "|" + q.optionC + "|" + q.optionD).hashCode().toString()
            }

            val entity = SolvedQuestionEntity(
                lesson = q.lesson ?: "Genel",
                questionText = q.question,
                questionFp = fp,
                userAnswer = userSelected,
                correctAnswer = q.correctAnswer ?: "",
                isCorrect = isCorrect,
                explanation = q.explanation ?: "",
                dateParams = dateStr,
                examType = examTitle ?: "Pratik"
            )
            db.historyDao().insertQuestion(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- RAPOR EKRANI İÇİN VERİ ÇEKME ---
    suspend fun getStatsData(): List<SolvedQuestionEntity> {
        return database?.historyDao()?.getAllHistoryList() ?: emptyList()
    }

    suspend fun getSolvedQuestionTexts(): List<String> {
        return try {
            database?.historyDao()?.getSolvedQuestionTexts() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /** ✅ Öğrenci tekrar görmesin diye: çözülmüş soru fingerprint listesi */
    suspend fun getSolvedQuestionFps(): List<String> {
        return try {
            database?.historyDao()?.getSolvedQuestionFps()?.distinct() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun clearAll() {
        database?.historyDao()?.clearHistory()
    }

    fun getAll(): Flow<List<SolvedQuestionEntity>>? {
        return database?.historyDao()?.getAllHistory()
    }
}
