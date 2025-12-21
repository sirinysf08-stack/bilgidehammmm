package com.example.bilgideham

import android.content.Context
import androidx.room.*
import java.security.MessageDigest

@Entity(tableName = "lesson_questions")
data class LessonQuestionEntity(
    @PrimaryKey val id: String,
    val lessonTag: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // A/B/C/D
    val explanation: String
)

@Dao
interface LessonQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<LessonQuestionEntity>)

    @Query("SELECT * FROM lesson_questions WHERE lessonTag = :lessonTag")
    suspend fun getAllByLesson(lessonTag: String): List<LessonQuestionEntity>

    @Query("DELETE FROM lesson_questions WHERE lessonTag = :lessonTag")
    suspend fun deleteByLesson(lessonTag: String)
}

@Database(entities = [LessonQuestionEntity::class], version = 1, exportSchema = false)
abstract class LessonDatabase : RoomDatabase() {
    abstract fun dao(): LessonQuestionDao

    companion object {
        @Volatile private var INSTANCE: LessonDatabase? = null

        fun getDatabase(context: Context): LessonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LessonDatabase::class.java,
                    "bilgideham_lesson_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object LessonRepositoryLocal {
    @Volatile private var db: LessonDatabase? = null

    fun init(context: Context) {
        if (db == null) {
            db = LessonDatabase.getDatabase(context)
        }
    }

    private fun sha256Short(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.take(12).joinToString("") { "%02x".format(it) }
    }

    private fun normalizeCorrect(q: QuestionModel): String {
        val ans = q.correctAnswer.trim().uppercase()
        if (ans in listOf("A", "B", "C", "D")) return ans

        // Eğer "1000" gibi metin geldiyse şıklara eşle
        return when {
            q.optionA.equals(ans, true) -> "A"
            q.optionB.equals(ans, true) -> "B"
            q.optionC.equals(ans, true) -> "C"
            q.optionD.equals(ans, true) -> "D"
            else -> "A"
        }
    }

    suspend fun saveQuestions(lessonTag: String, questions: List<QuestionModel>) {
        val database = db ?: return
        if (questions.isEmpty()) return

        val entities = questions.map { q ->
            val stableKey = (lessonTag + "|" + q.question.trim() + "|" + q.optionA + "|" + q.optionB + "|" + q.optionC + "|" + q.optionD).trim()
            LessonQuestionEntity(
                id = sha256Short(stableKey),
                lessonTag = lessonTag,
                question = q.question,
                optionA = q.optionA,
                optionB = q.optionB,
                optionC = q.optionC,
                optionD = q.optionD,
                correctAnswer = normalizeCorrect(q),
                explanation = q.explanation
            )
        }
        database.dao().upsertAll(entities)
    }

    suspend fun getAllQuestions(lessonTag: String): List<QuestionModel> {
        val database = db ?: return emptyList()
        return database.dao().getAllByLesson(lessonTag).map {
            QuestionModel(
                question = it.question,
                optionA = it.optionA,
                optionB = it.optionB,
                optionC = it.optionC,
                optionD = it.optionD,
                correctAnswer = it.correctAnswer,
                explanation = it.explanation,
                lesson = lessonTag,
                needsImage = false,
                imagePrompt = ""
            )
        }
    }

    suspend fun clearLesson(lessonTag: String) {
        val database = db ?: return
        database.dao().deleteByLesson(lessonTag)
    }
}
