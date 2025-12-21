package com.example.bilgideham

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

// ✅ Grafik için model (ProgressScreen ve StatsManager ortak kullanır)
data class DayPoint(
    val label: String,
    val solved: Int,
    val correct: Int,
    val wrong: Int
)

/**
 * ✅ EK DEPENDENCY YOK: SharedPreferences ile tutar (DataStore yok → hata çıkarmaz).
 *
 * Kayıt Yapısı:
 * - Toplam ders bazlı: total_<LESSON>_c / total_<LESSON>_w
 * - Günlük toplam: day_<yyyy-MM-dd>_c / day_<yyyy-MM-dd>_w
 * - Günlük ders bazlı: day_<yyyy-MM-dd>_<LESSON>_c / day_<yyyy-MM-dd>_<LESSON>_w
 * - Gün listesi: days (StringSet)
 */
class StatsManager(context: Context) {

    private val prefs = context.getSharedPreferences("bd_stats", Context.MODE_PRIVATE)
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun todayKey(): String = df.format(System.currentTimeMillis())

    private fun keyTotalC(lesson: String) = "total_${lesson}_c"
    private fun keyTotalW(lesson: String) = "total_${lesson}_w"

    private fun keyDayC(day: String) = "day_${day}_c"
    private fun keyDayW(day: String) = "day_${day}_w"

    private fun keyDayLessonC(day: String, lesson: String) = "day_${day}_${lesson}_c"
    private fun keyDayLessonW(day: String, lesson: String) = "day_${day}_${lesson}_w"

    private fun getInt(key: String): Int = prefs.getInt(key, 0)
    private fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    private fun addInt(key: String, delta: Int) {
        val cur = getInt(key)
        putInt(key, (cur + delta).coerceAtLeast(0))
    }

    private fun addDayToSet(day: String) {
        val set = (prefs.getStringSet("days", emptySet()) ?: emptySet()).toMutableSet()
        if (!set.contains(day)) {
            set.add(day)
            prefs.edit().putStringSet("days", set).apply()
        }
    }

    /** ✅ Quiz bitince çağır: ders bazlı + günlük + günlük ders bazlı hepsini günceller */
    fun addResult(lessonRaw: String, correctDelta: Int, wrongDelta: Int) {
        val lesson = lessonRaw.uppercase().ifBlank { "GENEL" }
        val day = todayKey()

        addDayToSet(day)

        // Toplam ders
        addInt(keyTotalC(lesson), correctDelta)
        addInt(keyTotalW(lesson), wrongDelta)

        // Günlük toplam
        addInt(keyDayC(day), correctDelta)
        addInt(keyDayW(day), wrongDelta)

        // Günlük ders
        addInt(keyDayLessonC(day, lesson), correctDelta)
        addInt(keyDayLessonW(day, lesson), wrongDelta)
    }

    /** ✅ Ders bazlı toplam doğru/yanlış */
    fun getStats(): Map<String, Pair<Int, Int>> {
        val out = linkedMapOf<String, Pair<Int, Int>>()
        for (lesson in KNOWN_LESSONS) {
            val c = getInt(keyTotalC(lesson))
            val w = getInt(keyTotalW(lesson))
            if (c != 0 || w != 0) out[lesson] = Pair(c, w)
        }
        return out
    }

    /** ✅ Bugünün ders bazlı doğru/yanlış listesi (drawer için) */
    fun getTodayLessonStats(): Map<String, Pair<Int, Int>> {
        val day = todayKey()
        val out = linkedMapOf<String, Pair<Int, Int>>()
        for (lesson in KNOWN_LESSONS) {
            val c = getInt(keyDayLessonC(day, lesson))
            val w = getInt(keyDayLessonW(day, lesson))
            if (c != 0 || w != 0) out[lesson] = Pair(c, w)
        }
        return out
    }

    /** ✅ Bugünün toplam doğru/yanlış */
    fun getTodayTotals(): Pair<Int, Int> {
        val day = todayKey()
        val c = getInt(keyDayC(day))
        val w = getInt(keyDayW(day))
        return Pair(c, w)
    }

    /** ✅ Son N gün seri (grafik/haftalık analiz için). */
    fun getDailySeries(days: Int = 7): List<DayPoint> {
        val safeDays = max(1, days)
        val cal = Calendar.getInstance()
        val list = ArrayList<DayPoint>()

        // eski → yeni
        cal.add(Calendar.DAY_OF_YEAR, -(safeDays - 1))

        for (i in 0 until safeDays) {
            val dayKey = df.format(cal.timeInMillis)

            val c = getInt(keyDayC(dayKey))
            val w = getInt(keyDayW(dayKey))
            val solved = c + w

            list.add(
                DayPoint(
                    label = shortLabel(cal),
                    solved = solved,
                    correct = c,
                    wrong = w
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    private fun shortLabel(cal: Calendar): String {
        val tr = Locale("tr", "TR")
        val dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, tr) ?: ""
        return dayName.replace(".", "")
    }

    companion object {
        // Uygulamadaki ders tipleri (NavGraph/LoadingAIScreen ile uyumlu)
        val KNOWN_LESSONS = listOf(
            "MATH",
            "TURKCE",
            "FEN",
            "SOSYAL",
            "ENGLISH",
            "ARAPCA",
            "PARAGRAF",
            "DENEME_KARMA",
            "GENEL"
        )
    }
}
