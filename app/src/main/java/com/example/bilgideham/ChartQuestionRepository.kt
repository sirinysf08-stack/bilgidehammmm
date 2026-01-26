package com.example.bilgideham

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Grafikli sorular için Firebase repository
 */
object ChartQuestionRepository {
    
    private const val TAG = "ChartQuestionRepo"
    private const val COLLECTION = "chart_questions"
    
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    
    /**
     * Grafik sorusu kaydet
     */
    suspend fun saveQuestion(question: ChartQuestionModel): Boolean {
        return try {
            val docRef = db.collection(COLLECTION).document()
            val questionWithId = question.copy(id = docRef.id)
            docRef.set(questionWithId).await()
            Log.d(TAG, "✅ Grafik soru kaydedildi: ${docRef.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Kayıt hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Sınıf ve derse göre grafik soruları getir
     */
    suspend fun getQuestionsByGradeAndSubject(
        grade: Int,
        subject: String,
        limit: Int = 10
    ): List<ChartQuestionModel> {
        return try {
            val snapshot = db.collection(COLLECTION)
                .whereEqualTo("grade", grade)
                .whereEqualTo("subject", subject)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            snapshot.toObjects(ChartQuestionModel::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Sorgu hatası: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Grafik tipine göre sorular getir
     */
    suspend fun getQuestionsByChartType(
        chartType: String,
        limit: Int = 10
    ): List<ChartQuestionModel> {
        return try {
            val snapshot = db.collection(COLLECTION)
                .whereEqualTo("chartType", chartType)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            snapshot.toObjects(ChartQuestionModel::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Sorgu hatası: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Tüm grafik sorularını say
     */
    suspend fun getTotalCount(): Int {
        return try {
            val snapshot = db.collection(COLLECTION).get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Grafik tiplerine göre istatistik
     */
    suspend fun getStatsByChartType(): Map<String, Int> {
        return try {
            val snapshot = db.collection(COLLECTION).get().await()
            val questions = snapshot.toObjects(ChartQuestionModel::class.java)
            questions.groupingBy { it.chartType }.eachCount()
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    /**
     * Soru sil
     */
    suspend fun deleteQuestion(questionId: String): Boolean {
        return try {
            db.collection(COLLECTION).document(questionId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Silme hatası: ${e.message}")
            false
        }
    }
}
