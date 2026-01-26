package com.example.bilgideham

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data Classes
data class DailyWord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val english: String,
    val turkish: String,
    val pronunciation: String,
    val exampleSentence: String,
    var isLearned: Boolean = false,
    var seenDate: Long = System.currentTimeMillis()
)

data class WordHuntState(
    val currentDay: Int = 1,
    val lastFetchDate: String = "",
    val allWords: MutableList<DailyWord> = mutableListOf(),
    val learnedCount: Int = 0
)

object WordHuntManager {
    private const val PREFS_NAME = "word_hunt_prefs"
    private const val KEY_STATE = "state_json"
    private val gson = Gson()

    // State
    private var _state = WordHuntState()
    val state: WordHuntState get() = _state

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_STATE, null)
        if (json != null) {
            try {
                _state = gson.fromJson(json, WordHuntState::class.java)

                // MIGRATION: 1970 hatasını düzelt (0 olanları bugüne çek)
                var changed = false
                val now = System.currentTimeMillis()
                _state.allWords.forEach {
                    if (it.seenDate == 0L) {
                        it.seenDate = now
                        changed = true
                    }
                }
                if(changed) saveState(context)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(_state)
        prefs.edit().putString(KEY_STATE, json).apply()
    }

    // Bugünü kontrol et, gerekirse yeni kelime çek
    suspend fun checkAndFetchDailyWords(context: Context): List<DailyWord> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Eğer bugün zaten kelime aldıysak, son eklenenleri döndür (en sondan 5)
        if (_state.lastFetchDate == today && _state.allWords.isNotEmpty()) {
            return _state.allWords.takeLast(5)
        }

        // Yeni gün, yeni kelimeler
        val newWords = fetchFromAI(_state.currentDay, _state.allWords.map { it.english })
        
        if (newWords.isNotEmpty()) {
            // Fix: Tarihi manuel olarak şu anki zaman yap (Gson 0 yapmasın)
            val now = System.currentTimeMillis()
            newWords.forEach { it.seenDate = now }

            _state.allWords.addAll(newWords)
            _state = _state.copy(
                currentDay = _state.currentDay + 1,
                lastFetchDate = today,
                allWords = _state.allWords
            )
            saveState(context)
        }
        return newWords
    }

    // Kullanıcı manuel olarak +5 kelime isterse
    suspend fun fetchMoreWords(context: Context): List<DailyWord> {
        val nextDay = _state.currentDay 
        val newWords = fetchFromAI(_state.currentDay + 1, _state.allWords.map { it.english })
        
        if (newWords.isNotEmpty()) {
            // Fix: Tarihi ekle
            val now = System.currentTimeMillis()
            newWords.forEach { it.seenDate = now }

            _state.allWords.addAll(newWords)
            _state = _state.copy(
                currentDay = _state.currentDay + 1, 
                allWords = _state.allWords
            )
            saveState(context)
        }
        return newWords
    }

    suspend fun markAsLearned(wordId: String, context: Context) {
        val word = _state.allWords.find { it.id == wordId }
        if (word != null && !word.isLearned) {
            word.isLearned = true
            _state = _state.copy(learnedCount = _state.learnedCount + 1)
            saveState(context)
        }
    }

    fun getHistory(): Map<String, List<DailyWord>> {
        // Tarihe göre grupla
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
        return _state.allWords.sortedByDescending { it.seenDate }.groupBy { sdf.format(Date(it.seenDate)) }
    }
    
    fun getQuizWords(count: Int = 4): List<DailyWord> {
         // Henüz öğrenilmemişlerden rastgele seç
         val pool = _state.allWords.filter { !it.isLearned }
         return pool.shuffled().take(count)
    }

    private suspend fun fetchFromAI(day: Int, existingWords: List<String>): List<DailyWord> = withContext(Dispatchers.IO) {
        try {
            val model = Firebase.vertexAI.generativeModel("gemini-2.0-flash")
            val unwanted = existingWords.takeLast(50).joinToString(", ") 
            
            val prompt = """
                Sen uzman bir İngilizce öğretmenisin. Öğrenci için 'Sıfırdan Zirveye' programının $day. günündeyiz.
                
                GÖREV:
                - Bana tam olarak 5 tane İngilizce kelime üret.
                - Seviye: Gün $day olduğuna göre seviyeyi buna göre ayarla (Gün 1 -> A1, Gün 100 -> B1 gibi yavaşça artır).
                - HARİÇ TUTULACAKLAR: $unwanted
                - JSON formatında ver.
                
                ÖNEMLİ: 'pronunciation' kısmına IPA alfabesi YAZMA. Türklerin okuyabileceği basit okunuşu yaz.
                Örnek: "Thank you" -> "Tenk yu" veya "Water" -> "Wotır"
                
                JSON Formatı:
                [
                  {
                    "english": "Book",
                    "turkish": "Kitap",
                    "pronunciation": "Buk",
                    "exampleSentence": "I read a book."
                  }
                ]
                
                Sadece saf JSON ver, markdown veya backtick kullanma.
            """.trimIndent()

            val response = model.generateContent(prompt).text?.replace("```json", "")?.replace("```", "")?.trim() ?: "[]"
            val type = object : TypeToken<List<DailyWord>>() {}.type
            gson.fromJson(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
