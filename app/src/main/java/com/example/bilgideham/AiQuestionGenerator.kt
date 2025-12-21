package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class AiQuestionGenerator {

    companion object {
        private const val TAG = "AI_QUESTION_GEN"
        private const val GEMINI_MODEL = "gemini-2.0-flash"

        // Aynı anda çoklu üretimde yarış koşullarını engelle (Admin Turbo dahil)
        private val generatorMutex = Mutex()

        // Tekrarlı soru basımını azalt
        private val seenQuestionFingerprints = LinkedHashSet<String>()

        // Scope tekrarını gün bazında kontrol et
        private val seenScopeFingerprints = LinkedHashSet<String>()
        private var lastScopeDay: String? = null

        // Şık eşitlemede padding
        private const val ZWSP = '\u200B' // zero width space

        // (Legacy) Combining underline - artık üretimde kullanılmıyor. Geriye dönük temizlik için tutuluyor.
        private const val UL = '\u0332'

        // UI katmanının parse edeceği underline markerları
        private const val UL_OPEN = "[["
        private const val UL_CLOSE = "]]"
    }

    // Firebase AI Logic entrypoint
    private val gemini: GenerativeModel by lazy {
        Firebase.vertexAI.generativeModel(modelName = GEMINI_MODEL)
    }

    data class ValidationResult(val correctAnswer: String, val explanation: String)

    data class CurriculumScope(
        val lesson: String,
        val topic: String,
        val subtopic: String,
        val code: String? = null
    ) {
        fun fingerprint(): String {
            val key = listOf(lesson, topic, code ?: subtopic).joinToString("|")
            return key.lowercase(Locale.US).replace("\\s+".toRegex(), " ").trim()
        }
    }

    /**
     * MEB Scope Router
     */
    private object Meb5Curriculum {

        val turkceScopes: List<CurriculumScope> = listOf(
            CurriculumScope("Türkçe", "Sözcükte Anlam", "Gerçek / Mecaz / Terim anlam"),
            CurriculumScope("Türkçe", "Sözcükte Anlam", "Eş anlam / Zıt anlam"),
            CurriculumScope("Türkçe", "Sözcükte Anlam", "Deyim – Atasözü – Özdeyiş"),
            CurriculumScope("Türkçe", "Cümlede Anlam", "Neden–Sonuç / Amaç–Sonuç"),
            CurriculumScope("Türkçe", "Cümlede Anlam", "Koşul–Sonuç / Karşılaştırma"),
            CurriculumScope("Türkçe", "Cümlede Anlam", "Öznel–Nesnel / Örtülü anlam"),
            CurriculumScope("Türkçe", "Parçada/Paragrafta Anlam", "Konu – Ana düşünce – Yardımcı düşünce"),
            CurriculumScope("Türkçe", "Parçada/Paragrafta Anlam", "Başlık bulma – Metnin amacı"),
            CurriculumScope("Türkçe", "Parçada/Paragrafta Anlam", "Çıkarım – Metinden sonuç"),
            CurriculumScope("Türkçe", "Yazım Kuralları", "Büyük harflerin kullanımı"),
            CurriculumScope("Türkçe", "Yazım Kuralları", "Bağlaç olan de/da – ki’nin yazımı"),
            CurriculumScope("Türkçe", "Noktalama", "Nokta / Virgül / İki nokta"),
            CurriculumScope("Türkçe", "Noktalama", "Soru işareti / Ünlem / Tırnak"),
            CurriculumScope("Türkçe", "Kelime Yapısı", "Kök – Ek (yapım/çekim)"),
            CurriculumScope("Türkçe", "Kelime Yapısı", "Basit–Türemiş–Birleşik")
        )

        val matematikScopes: List<CurriculumScope> = listOf(
            CurriculumScope("Matematik", "Doğal Sayılar", "Basamak değeri – okuma/yazma", "MAT.5.1.1"),
            CurriculumScope("Matematik", "Doğal Sayılar", "Doğal sayıları çözümleme", "MAT.5.1.2"),
            CurriculumScope("Matematik", "Doğal Sayılar", "Karşılaştırma – sıralama", "MAT.5.1.3"),
            CurriculumScope("Matematik", "Doğal Sayılar", "Toplama/çıkarma işlemleri", "MAT.5.1.4"),
            CurriculumScope("Matematik", "Doğal Sayılar", "Çarpma/bölme işlemleri", "MAT.5.1.5"),
            CurriculumScope("Matematik", "Doğal Sayılar", "Dört işlem problem çözme", "MAT.5.1.6"),
            CurriculumScope("Matematik", "Kesirler", "Kesri açıklama ve gösterme", "MAT.5.2.1"),
            CurriculumScope("Matematik", "Kesirler", "Birden çok parçanın kesri", "MAT.5.2.2"),
            CurriculumScope("Matematik", "Kesirler", "Paydası eşit kesirleri karşılaştırma", "MAT.5.2.3"),
            CurriculumScope("Matematik", "Kesirler", "Kesirlerle toplama/çıkarma", "MAT.5.2.4"),
            CurriculumScope("Matematik", "Ondalık Gösterim", "Ondalık okuma/yazma", "MAT.5.3.1"),
            CurriculumScope("Matematik", "Ondalık Gösterim", "Ondalık karşılaştırma/sıralama", "MAT.5.3.2"),
            CurriculumScope("Matematik", "Yüzdeler", "Yüzde 1/10/50 bulma", "MAT.5.4.1"),
            CurriculumScope("Matematik", "Yüzdeler", "Bir çokluğun yüzdesi – yorumlama", "MAT.5.4.2"),
            CurriculumScope("Matematik", "Geometri", "Üçgen sınıflandırma", "MAT.5.5.1"),
            CurriculumScope("Matematik", "Geometri", "Kare/dikdörtgen çevre", "MAT.5.5.2"),
            CurriculumScope("Matematik", "Veri Analizi", "Tablo/grafik – yorum", "MAT.5.6.1"),
            CurriculumScope("Matematik", "Olasılık", "İmkânsız–kesin spektrumu", "MAT.5.6.2")
        )

        val sosyalScopes: List<CurriculumScope> = listOf(
            CurriculumScope("Sosyal Bilgiler", "Birey ve Toplum", "Hak–sorumluluk – örnek olay", "SB.5.1.1"),
            CurriculumScope("Sosyal Bilgiler", "Birey ve Toplum", "Grup/kurum rollerini ayırt etme", "SB.5.1.2"),
            CurriculumScope("Sosyal Bilgiler", "Kültür ve Miras", "Kültürel ögeleri tanıma", "SB.5.2.1"),
            CurriculumScope("Sosyal Bilgiler", "Kültür ve Miras", "Tarihî mekân/nesne – çıkarım", "SB.5.2.2"),
            CurriculumScope("Sosyal Bilgiler", "İnsanlar, Yerler ve Çevreler", "Harita/yer-yön bilgisi yorumlama", "SB.5.3.1"),
            CurriculumScope("Sosyal Bilgiler", "İnsanlar, Yerler ve Çevreler", "İklim/çevre – günlük yaşama etkisi", "SB.5.3.2"),
            CurriculumScope("Sosyal Bilgiler", "Üretim, Dağıtım ve Tüketim", "İhtiyaç–istek – bilinçli tüketim", "SB.5.5.1"),
            CurriculumScope("Sosyal Bilgiler", "Üretim, Dağıtım ve Tüketim", "Meslekler ve ekonomik faaliyetler", "SB.5.5.2"),
            CurriculumScope("Sosyal Bilgiler", "Bilim, Teknoloji ve Toplum", "Teknoloji kullanımında etik/güvenlik", "SB.5.6.1"),
            CurriculumScope("Sosyal Bilgiler", "Bilim, Teknoloji ve Toplum", "Bilgi kirliliği – doğrulama", "SB.5.6.2")
        )

        val fenFallback: List<CurriculumScope> = listOf(
            CurriculumScope("Fen Bilimleri", "Fen", "5. sınıf genel kazanımlarına uygun üret (detay scope eklenebilir)")
        )

        val englishFallback: List<CurriculumScope> = listOf(
            CurriculumScope("İngilizce", "English", "A1-A2: daily life, school, hobbies, simple grammar patterns")
        )

        val dinFallback: List<CurriculumScope> = listOf(
            CurriculumScope("Din Kültürü", "Din", "5. sınıf genel kazanımlarına uygun üret (detay scope eklenebilir)")
        )

        fun scopesForLesson(lesson: String): List<CurriculumScope> {
            val l = lesson.lowercase(Locale("tr", "TR"))
            return when {
                l.contains("turk") || l.contains("türk") || l.contains("türkçe") || l.contains("paragraf") -> turkceScopes
                l.contains("mat") -> matematikScopes
                l.contains("sosyal") -> sosyalScopes
                l.contains("fen") -> fenFallback
                l.contains("din") -> dinFallback
                l.contains("ing") || l.contains("english") -> englishFallback
                else -> emptyList()
            }
        }
    }

    // ---------- Public API ----------

    suspend fun generateBatch(lesson: String, requestedSize: Int): List<QuestionModel> =
        withContext(Dispatchers.IO) {
            if (requestedSize <= 0) return@withContext emptyList()

            val collected = mutableListOf<QuestionModel>()

            generatorMutex.withLock {
                var attempt = 0
                val maxAttempts = 7

                while (collected.size < requestedSize && attempt < maxAttempts) {
                    attempt++

                    val needed = requestedSize - collected.size
                    val askCount = when {
                        needed >= 15 -> 30
                        needed >= 8 -> 20
                        else -> (needed + 10).coerceAtMost(16)
                    }

                    val batch = generateMultipleInternal(lesson, askCount)
                    if (batch.isNotEmpty()) {
                        val shuffledBatch = batch.map { shuffleOptions(it) }
                        for (q in shuffledBatch) {
                            if (collected.size >= requestedSize) break
                            collected.add(q)
                        }
                        Log.d(
                            TAG,
                            "generateBatch progress: ${collected.size}/$requestedSize (attempt=$attempt, lesson=$lesson)"
                        )
                    } else {
                        Log.w(TAG, "generateBatch empty (attempt=$attempt, lesson=$lesson)")
                    }

                    delay(350)
                }
            }

            collected.take(requestedSize)
        }

    suspend fun generateMultiple(lesson: String, count: Int): List<QuestionModel> = generateBatch(lesson, count)

    suspend fun generateMiniGameBatch(gameType: String, count: Int): List<GameQuestion> =
        withContext(Dispatchers.IO) {
            val target = count.coerceIn(1, 60)
            val type = gameType.uppercase(Locale.US).trim()

            val topic = when (type) {
                "MATH" -> "Matematik (5): işlemler, kesir, ondalık, yüzde, problem"
                "SCIENCE" -> "Fen (5): Dünya-Ay, kuvvet, madde, ışık, elektrik, canlılar"
                "SOCIAL" -> "Sosyal (5): hak-sorumluluk, kültür-miras, üretim-tüketim, teknoloji"
                "TURKISH" -> "Türkçe (5): anlam bilgisi, yazım-noktalama, paragraf"
                "ENGLISH" -> "English (A1/A2): daily life, school, hobbies"
                else -> "Genel 5. sınıf"
            }

            val languageRule = if (type == "ENGLISH") {
                "- text ve options tamamen İNGİLİZCE olacak."
            } else {
                "- text ve options tamamen TÜRKÇE olacak."
            }

            fun buildPrompt(ask: Int, id: String): String {
                return """
GÖREV: 5. sınıf için mini-oyun formatında kısa 4 şıklı sorular üret.
KONU: $topic
ADET: $ask (JSON dizisi TAM $ask eleman)
ID: $id

KURALLAR:
1) SADECE saf JSON ARRAY.
2) Her eleman: text, options, correctIndex
3) options: 4 elemanlı string dizi
4) correctIndex: 0-3 integer
5) Görsel isteme.

DİL:
$languageRule

FORMAT:
[
  { "text":"...", "options":["A","B","C","D"], "correctIndex":2 }
]
""".trimIndent()
            }

            fun fingerprint(g: GameQuestion): String {
                fun norm(s: String) = stripInvisible(s)
                    .lowercase(Locale.US)
                    .replace("\\s+".toRegex(), " ")
                    .trim()

                return buildString {
                    append(norm(g.lesson)); append("|")
                    append(norm(g.text)); append("|")
                    append(norm(g.options.joinToString("|")))
                }
            }

            val collected = mutableListOf<GameQuestion>()
            val seen = LinkedHashSet<String>()

            generatorMutex.withLock {
                var attempt = 0
                val maxAttempts = 5

                while (collected.size < target && attempt < maxAttempts) {
                    attempt++

                    val remaining = target - collected.size
                    val ask = (remaining + 10).coerceAtMost(28)
                    val id = UUID.randomUUID().toString().take(6)
                    val prompt = buildPrompt(ask, id)

                    val raw = try {
                        gemini.generateContent(prompt).text?.trim().orEmpty()
                    } catch (e: Exception) {
                        Log.e(TAG, "generateMiniGameBatch hata (attempt=$attempt): ${e.message}")
                        ""
                    }

                    if (raw.isNotBlank()) {
                        val parsed = parseMiniGameJson(raw, type)
                        var added = 0
                        for (g in parsed) {
                            val fp = fingerprint(g)
                            if (seen.add(fp)) {
                                collected.add(g)
                                added++
                                if (collected.size >= target) break
                            }
                        }
                        Log.d(TAG, "miniGame top-up: +$added, total=${collected.size}/$target (attempt=$attempt, type=$type)")
                    } else {
                        Log.w(TAG, "miniGame empty raw (attempt=$attempt, type=$type)")
                    }

                    delay(250)
                }
            }

            collected.take(target)
        }

    suspend fun validateAndExplain(lesson: String, q: QuestionModel): ValidationResult? =
        withContext(Dispatchers.IO) {
            val languageRule = getLanguageRule(lesson)

            val prompt = """
GÖREV: Aşağıdaki soru için doğru şıkkı belirle ve çok detaylı öğretmen açıklamasını üret.

KURALLAR:
- SADECE saf JSON object döndür.
- correctAnswer: "A","B","C","D"
- explanation TÜRKÇE ve EN AZ 10-15 cümle.
- Yanlış şıkları tek tek neden yanlış açıkla.
- Görsel isteme.

DİL KURALI:
$languageRule

DERS: "$lesson"
SORU: "${q.question}"

A) "${q.optionA}"
B) "${q.optionB}"
C) "${q.optionC}"
D) "${q.optionD}"

FORMAT:
{ "correctAnswer":"C", "explanation":"..." }
""".trimIndent()

            generatorMutex.withLock {
                repeat(2) { attempt ->
                    try {
                        val raw = gemini.generateContent(prompt).text?.trim().orEmpty()
                        val obj = parseJsonObject(raw) ?: run {
                            Log.w(TAG, "validateAndExplain parse fail (attempt=$attempt)")
                            return@repeat
                        }

                        val ans = obj.optString("correctAnswer").uppercase(Locale.US).trim()
                        val exp = obj.optString("explanation").trim()

                        if (ans in listOf("A", "B", "C", "D") && exp.length >= 180) {
                            return@withContext ValidationResult(ans, exp)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "validateAndExplain hata: ${e.message}")
                    }
                    delay(300)
                }
            }

            null
        }

    // ---------- Scope + Prompt ----------

    private fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun shouldPreferParagraph(lesson: String): Boolean {
        val l = lesson.lowercase(Locale("tr", "TR"))
        return l.contains("paragraf")
    }

    private fun pickCurriculumScope(lesson: String): CurriculumScope? {
        val today = todayString()
        if (lastScopeDay != today) {
            lastScopeDay = today
            seenScopeFingerprints.clear()
        }

        val scopes = Meb5Curriculum.scopesForLesson(lesson)
        if (scopes.isEmpty()) return null

        val preferParagraph = shouldPreferParagraph(lesson)

        val weighted = scopes.flatMap { s ->
            val w = when {
                (s.topic.contains("paragraf", true) || s.subtopic.contains("paragraf", true) ||
                        s.subtopic.contains("ana düşünce", true) || s.subtopic.contains("yardımcı düşünce", true) ||
                        s.subtopic.contains("başlık", true) || s.subtopic.contains("çıkarım", true)) ->
                    if (preferParagraph) 9 else 5

                lesson.lowercase(Locale("tr", "TR")).contains("mat") && s.code != null -> 4
                else -> 2
            }
            List(w) { s }
        }.shuffled(Random(System.nanoTime()))

        for (c in weighted) {
            val fp = c.fingerprint()
            if (!seenScopeFingerprints.contains(fp)) {
                seenScopeFingerprints.add(fp)
                if (seenScopeFingerprints.size > 250) seenScopeFingerprints.clear()
                return c
            }
        }

        seenScopeFingerprints.clear()
        return weighted.firstOrNull()
    }

    private fun buildCurriculumBlock(scope: CurriculumScope?): String {
        if (scope == null) {
            return """
MÜFREDAT KAPSAMI:
- Bu ders için kapsam haritası tanımlı değil; 5. sınıf MEB kazanımlarına uygun üret.
KAPSAM KURALI: Seçtiğin konu dışına çıkma.
""".trimIndent()
        }

        val codeLine = scope.code?.let { "- KAZANIM KODU: \"$it\"" } ?: "- KAZANIM KODU: \"-\""
        return """
MÜFREDAT KAPSAMI (ZORUNLU):
- KONU/BAŞLIK: "${scope.topic}"
- ALT KONU / KAZANIM: "${scope.subtopic}"
$codeLine
KAPSAM KURALI:
- Ürettiğin her soru yukarıdaki kapsamın DIŞINA ÇIKMAYACAK.
- Yakın konu/alt başlık karışmayacak.
""".trimIndent()
    }

    private data class OptionLengthPolicy(val minLen: Int, val maxLen: Int)

    private fun optionPolicy(lesson: String, isParagraphLike: Boolean): OptionLengthPolicy {
        val l = lesson.lowercase(Locale("tr", "TR"))
        return when {
            l.contains("mat") -> OptionLengthPolicy(minLen = 4, maxLen = 70)
            (l.contains("ing") || l.contains("english")) -> OptionLengthPolicy(minLen = 3, maxLen = 90)
            isParagraphLike -> OptionLengthPolicy(minLen = 8, maxLen = 220)
            else -> OptionLengthPolicy(minLen = 6, maxLen = 140)
        }
    }

    private fun stripInvisible(s: String): String {
        // ZWSP + legacy UL + underline marker bracketlarını metriklerden temizle
        var out = s.replace(ZWSP.toString(), "")
            .replace(UL.toString(), "")

        // markerların içeriğini koru, bracketları kaldır (ölçüm ve fingerprint için)
        out = out.replace(Regex("\\[\\[(.+?)]]"), "$1")
        out = out.replace(UL_OPEN, "").replace(UL_CLOSE, "")
        return out
    }

    private fun visibleLen(s: String): Int =
        stripInvisible(s).trim().length

    private fun normalizeOptionsEqualLength(
        a: String,
        b: String,
        c: String,
        d: String,
        policy: OptionLengthPolicy
    ): List<String>? {
        val raw = listOf(a.trim(), b.trim(), c.trim(), d.trim())
        if (raw.any { it.isBlank() }) return null

        val lens = raw.map { visibleLen(it) }
        val min = lens.minOrNull() ?: 0
        val max = lens.maxOrNull() ?: 0

        if (min < policy.minLen) return null
        if (max > policy.maxLen) return null

        val target = max
        return raw.map { opt ->
            val need = target - visibleLen(opt)
            if (need <= 0) opt else opt + ZWSP.toString().repeat(need)
        }
    }

    private fun questionFingerprint(q: QuestionModel): String {
        fun norm(s: String) = stripInvisible(s)
            .lowercase(Locale.US)
            .replace("\\s+".toRegex(), " ")
            .trim()

        return buildString {
            append(norm(q.question)); append("|")
            append(norm(q.optionA)); append("|")
            append(norm(q.optionB)); append("|")
            append(norm(q.optionC)); append("|")
            append(norm(q.optionD))
        }
    }

    // --- Underline Marker Utilities ---

    private val underlineMarkerRegex = Regex("\\[\\[(.+?)]]")

    private fun hasUnderlineMarker(text: String): Boolean = underlineMarkerRegex.containsMatchIn(text)

    private fun stripUnderlineMarkers(text: String): String = underlineMarkerRegex.replace(text) { mr ->
        mr.groupValues[1]
    }

    private fun needsUnderlinedWord(question: String): Boolean {
        val q = question.lowercase(Locale("tr", "TR"))
        return q.contains("altı çizili") || q.contains("altı çizilmiş") || q.contains("altı çizilen")
    }

    private fun computeMarkerRanges(text: String): List<IntRange> {
        val ranges = mutableListOf<IntRange>()
        var i = 0
        while (i < text.length) {
            val open = text.indexOf(UL_OPEN, startIndex = i)
            if (open < 0) break
            val close = text.indexOf(UL_CLOSE, startIndex = open + UL_OPEN.length)
            if (close < 0) break
            val endInclusive = (close + UL_CLOSE.length - 1)
            ranges.add(open..endInclusive)
            i = close + UL_CLOSE.length
        }
        return ranges
    }

    private fun isInsideAnyRange(idx: Int, ranges: List<IntRange>): Boolean {
        for (r in ranges) if (idx in r) return true
        return false
    }

    private fun isNegativeQuestionStem(question: String, lesson: String): Boolean {
        val l = lesson.lowercase(Locale("tr", "TR"))
        val q0 = stripInvisible(question).lowercase(Locale("tr", "TR"))

        val selectorSignals = listOf(
            "hangisi", "hangileri",
            "aşağıdakilerden", "aşağıdaki",
            "ifadelerden", "cümlelerden", "yargılardan",
            "seçeneklerden", "şık"
        )

        val trCues = listOf(
            "değildir", "degildir",
            "değil", "degil",
            "yanlıştır", "yanlistir",
            "söylenemez", "soylenemez",
            "olamaz",
            "bulunmaz",
            "yoktur",
            "hiçbir", "hicbir",
            "asla",
            "hariç", "haric",
            "istisna",
            "doğru değildir", "dogru degildir",
            "yanlış olan", "yanlis olan"
        )

        val enSelector = listOf("which", "following", "choose", "option")
        val enCues = listOf("not", "except", "incorrect", "false", "never", "none")

        val hasSelector = if (l.contains("ing") || l.contains("english")) {
            enSelector.any { q0.contains(it) }
        } else {
            selectorSignals.any { q0.contains(it) }
        }

        val hasCue = if (l.contains("ing") || l.contains("english")) {
            enCues.any { q0.contains(it) }
        } else {
            trCues.any { q0.contains(it) }
        }

        // “Sınav kuralı”: sadece seçim + olumsuzluk birlikteyse underline devreye girsin
        return hasSelector && hasCue
    }

    private fun highlightNegativeCues(question: String, lesson: String): String {
        // Gate: olumsuz soru kökü değilse asla underline uygulama
        if (!isNegativeQuestionStem(question, lesson)) return question

        var out = question

        val trCues = listOf(
            "değildir", "degildir",
            "değil", "degil",
            "yanlıştır", "yanlistir",
            "yanlış", "yanlis",
            "söylenemez", "soylenemez",
            "olamaz",
            "bulunmaz",
            "yoktur",
            "hiçbir", "hicbir",
            "asla",
            "hariç", "haric",
            "istisna"
        )

        val l = lesson.lowercase(Locale("tr", "TR"))
        val cues = if (l.contains("ing") || l.contains("english")) {
            listOf("NOT", "EXCEPT", "INCORRECT", "FALSE", "NEVER", "NONE")
        } else trCues

        for (cue in cues) {
            val protected = computeMarkerRanges(out)
            val regex = Regex("\\b${Regex.escape(cue)}\\b", RegexOption.IGNORE_CASE)

            val matches = regex.findAll(out).toList()
            if (matches.isEmpty()) continue

            val sb = StringBuilder(out.length + 16)
            var last = 0

            for (m in matches) {
                // Zaten bir [[...]] bloğunun içindeyse tekrar işaretleme
                if (isInsideAnyRange(m.range.first, protected)) continue

                sb.append(out.substring(last, m.range.first))
                sb.append(UL_OPEN)
                sb.append(m.value)
                sb.append(UL_CLOSE)
                last = m.range.last + 1
            }
            sb.append(out.substring(last))
            out = sb.toString()
        }

        return out
    }

    private fun passesParagraphGate(question: String, lesson: String, scope: CurriculumScope?): Boolean {
        val isParagraphTarget =
            shouldPreferParagraph(lesson) ||
                    (scope?.topic?.contains("paragraf", true) == true) ||
                    (scope?.subtopic?.contains("paragraf", true) == true) ||
                    (scope?.subtopic?.contains("ana düşünce", true) == true) ||
                    (scope?.subtopic?.contains("yardımcı düşünce", true) == true) ||
                    (scope?.subtopic?.contains("başlık", true) == true)

        if (!isParagraphTarget) return true

        val clean = stripInvisible(question).trim()
        if (clean.length < 90) return false

        val lower = clean.lowercase(Locale("tr", "TR"))
        val idxSoru = lower.indexOf("soru:")
        val paraCandidate = if (idxSoru >= 0) {
            clean.substring(0, idxSoru).trim()
        } else {
            clean.split(Regex("\n\\s*\n")).firstOrNull()?.trim().orEmpty()
        }

        if (paraCandidate.length < 70) return false

        fun approxSentenceCount(text: String): Int {
            val t = text.replace('…', '.')
            val parts = t.split(Regex("[.!?]+|\\n+"))
                .map { it.trim() }
                .filter { it.length >= 12 }
            return parts.size
        }

        val sentenceCount = approxSentenceCount(paraCandidate)
        return sentenceCount >= 3
    }

    private suspend fun generateMultipleInternal(lesson: String, count: Int): List<QuestionModel> =
        withContext(Dispatchers.IO) {

            val scope = pickCurriculumScope(lesson)
            val curriculumBlock = buildCurriculumBlock(scope)
            val lessonInstructions = getLessonSpecificInstruction(lesson, scope)
            val languageRule = getLanguageRule(lesson)

            val askCount = (count + 8).coerceAtMost(30)
            val uniqueId = UUID.randomUUID().toString().take(6)

            val isParagraphTarget = shouldPreferParagraph(lesson) ||
                    (scope?.topic?.contains("paragraf", true) == true) ||
                    (scope?.subtopic?.contains("paragraf", true) == true)

            val policy = optionPolicy(lesson, isParagraphTarget)

            val paragraphContract = if (isParagraphTarget) {
                """
PARAGRAF SÖZLEŞMESİ (ZORUNLU):
- "question" alanı mutlaka 6-10 cümlelik bir PARAGRAF ile BAŞLAYACAK.
- Cümlelerin büyük çoğunluğu nokta ile bitecek. (En az 3 cümle algılanabilir olsun.)
- Paragraftan sonra boş satır bırak ve "Soru:" ile soru kökünü yaz.
- Soru kökü paragrafla birebir ilişkili olacak (ana fikir/yardımcı fikir/çıkarım/başlık).
""".trimIndent()
            } else ""

            val prompt = """
GÖREV: 5. sınıf proje okulu seviyesinde (kolay değil), MEB kazanımlarına uygun 4 şıklı test soruları üret.

DERS: "$lesson"
ADET: $askCount (JSON dizisi TAM $askCount eleman olacak)
ID: $uniqueId

$curriculumBlock

GENEL KURALLAR (ZORUNLU):
1) SADECE saf JSON ARRAY döndür. Markdown/ek açıklama yok.
2) Alanlar: question, optionA, optionB, optionC, optionD, correctAnswer("A"-"D"), explanation
3) explanation EN AZ 10-15 cümle olacak. Yanlış şıkların neden yanlış olduğunu tek tek açıkla.
4) Görsel/şekil/grafik isteme.

$paragraphContract

ALT ÇİZİLİ SÖZLEŞME (ZORUNLU):
- Soru metninde “altı çizili / altı çizilmiş / altı çizilen” ifadesini kullanacaksan,
  altı çizilecek kelimeyi seçenek cümlesinde [[kelime]] formatında işaretle.
- [[...]] işaretlemesi YOKSA, soru metninde asla “altı çizili” ifadesi kullanma.
- NOT: “altı çizili” ifadesi yoksa [[...]] KULLANMA.

ŞIK STANDARDI (ZORUNLU):
- Şıklar mantıklı çeldirici olacak.
- Görünür uzunluk hedefi: her şık yaklaşık ${policy.minLen}-${policy.maxLen} karakter bandında kalsın.
- Şık uzunlukları birbirine yakın olsun.

DİL KURALI:
$languageRule

DERS ÖZEL TALİMAT:
$lessonInstructions

FORMAT:
[
  {
    "question":"...",
    "optionA":"...",
    "optionB":"...",
    "optionC":"...",
    "optionD":"...",
    "correctAnswer":"C",
    "explanation":"..."
  }
]
""".trimIndent()

            val rawText = try {
                gemini.generateContent(prompt).text?.trim().orEmpty()
            } catch (e: Exception) {
                Log.e(TAG, "generateMultipleInternal hata: ${e.message}")
                ""
            }

            if (rawText.isBlank()) return@withContext emptyList()

            val parsed = parseExamJson(rawText, lesson)
            if (parsed.isEmpty()) return@withContext emptyList()

            val out = mutableListOf<QuestionModel>()

            var dropGate = 0
            var dropNormalize = 0
            var dropDup = 0

            for (q0 in parsed) {
                // Sınav kuralı: olumsuzluk underline sadece gerçek olumsuz soru köklerinde ve sadece soru kökünde
                val qText = highlightNegativeCues(q0.question, lesson)

                if (!passesParagraphGate(qText, lesson, scope)) {
                    dropGate++
                    continue
                }

                val normalized = normalizeOptionsEqualLength(
                    q0.optionA, q0.optionB, q0.optionC, q0.optionD,
                    policy
                )
                if (normalized == null) {
                    dropNormalize++
                    continue
                }

                val q = q0.copy(
                    question = qText,
                    optionA = normalized[0],
                    optionB = normalized[1],
                    optionC = normalized[2],
                    optionD = normalized[3]
                )

                val fp = questionFingerprint(q)
                if (!seenQuestionFingerprints.add(fp)) {
                    dropDup++
                    continue
                }

                out.add(q)
                if (out.size >= count) break
            }

            if (seenQuestionFingerprints.size > 1200) seenQuestionFingerprints.clear()

            Log.d(
                TAG,
                "generateMultipleInternal: parsed=${parsed.size}, out=${out.size}, dropGate=$dropGate, dropNormalize=$dropNormalize, dropDup=$dropDup, lesson=$lesson"
            )

            out
        }

    private fun getLanguageRule(lesson: String): String {
        val l = lesson.lowercase(Locale("tr", "TR"))
        return when {
            l.contains("ing") || l.contains("english") -> {
                """
- İngilizce dersi için: question + optionA/B/C/D tamamen İNGİLİZCE olacak.
- explanation kesinlikle TÜRKÇE olacak ve ÇOK DETAYLI olacak.
""".trimIndent()
            }

            l.contains("arap") || l.contains("arabic") -> {
                """
- Arapça dersi için:
- Soru kökü (question) TÜRKÇE olabilir (örn: "Aşağıdakilerden hangisi kalemdir?").
- Şıklar (optionA/B/C/D) SADECE ARAPÇA HARFLERLE yazılacak.
- Şıklarda ASLA parantez içinde Türkçe anlam veya okunuş YAZMA. (Örn: "قلم (Kalem)" YASAK. Sadece "قلم" yaz).
- explanation TÜRKÇE olacak.
""".trimIndent()
            }

            else -> {
                """
- İngilizce ve Arapça HARİÇ tüm derslerde question + optionA/B/C/D TÜRKÇE olacak.
- explanation TÜRKÇE olacak.
""".trimIndent()
            }
        }
    }

    private fun getLessonSpecificInstruction(lesson: String, scope: CurriculumScope?): String {
        val l = lesson.lowercase(Locale("tr", "TR"))

        val common = """
Seviye: 5. Sınıf (Proje okulu).
Zorluk: Orta-Üst.
Soru Tipi: 4 Şıklı Test (tek doğru).
""".trimIndent()

        val scopeHint = scope?.let {
            "MEB SCOPE: ${it.topic} / ${it.subtopic}" + (it.code?.let { c -> " (Kodu: $c)" } ?: "")
        } ?: "MEB SCOPE: (genel)"

        return when {
            l.contains("paragraf") -> """
$common
$scopeHint
METİN: 6-10 cümle, çıkarım gerektirsin (ana düşünce/yardımcı düşünce/başlık/metnin amacı).
FORMAT: Paragraf + boş satır + "Soru:" + soru kökü. Paragraf cümleleri mümkünse nokta ile bitsin.
""".trimIndent()

            l.contains("mat") -> """
$common
$scopeHint
KURAL: En az 2 adımlı problem sorusu üret. Sonucu kesin doğrula.
""".trimIndent()

            l.contains("sosyal") -> """
$common
$scopeHint
KURAL: Günlük yaşam senaryosu ver, yorumlat. Kavramı uygulama düzeyinde ölç.
""".trimIndent()

            l.contains("turk") || l.contains("türk") -> """
$common
$scopeHint
KURAL: Ezber değil, yorum gerektirsin. Dil bilgisi + anlam bilgisi ağırlıklı.
""".trimIndent()

            l.contains("ing") || l.contains("english") -> """
$common
$scopeHint
KURAL: Soru+şıklar İngilizce, açıklama Türkçe. Çeldiriciler anlamlı olsun.
""".trimIndent()

            l.contains("arap") -> """
$common
$scopeHint
KURAL: Kelime bilgisi veya basit gramer ölç.
ÖNEMLİ: Şıklarda sadece Arapça kelime olsun. Yanına Türkçe anlamını ASLA yazma.
Örnek Yanlış Şık: "باب (Kapı)" -> Bunu yapma.
Örnek Doğru Şık: "باب" -> Sadece bunu yaz.
""".trimIndent()

            else -> """
$common
$scopeHint
KONU: 5. Sınıf genel.
""".trimIndent()
        }
    }

    // ---------- JSON ----------

    private fun cleanJsonArrayString(raw: String): String {
        var clean = raw.replace("```json", "").replace("```", "").trim()
        val first = clean.indexOf('[')
        val last = clean.lastIndexOf(']')
        if (first != -1 && last != -1 && last > first) clean = clean.substring(first, last + 1)
        return clean
    }

    private fun cleanJsonObjectString(raw: String): String {
        var clean = raw.replace("```json", "").replace("```", "").trim()
        val first = clean.indexOf('{')
        val last = clean.lastIndexOf('}')
        if (first != -1 && last != -1 && last > first) clean = clean.substring(first, last + 1)
        return clean
    }

    private fun parseJsonObject(raw: String): JSONObject? {
        return try {
            val clean = cleanJsonObjectString(raw)
            if (!clean.startsWith("{") || !clean.endsWith("}")) return null
            JSONObject(clean)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseExamJson(raw: String, lesson: String): List<QuestionModel> {
        val list = mutableListOf<QuestionModel>()
        try {
            val clean = cleanJsonArrayString(raw)
            val arr = JSONArray(clean)

            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue

                var qText = obj.optString("question", "").trim()
                var a = obj.optString("optionA", "").trim()
                var b = obj.optString("optionB", "").trim()
                var c = obj.optString("optionC", "").trim()
                var d = obj.optString("optionD", "").trim()
                val exp = obj.optString("explanation", "").trim()
                var ca = obj.optString("correctAnswer", "").trim().uppercase(Locale.US)

                if (qText.isBlank() || a.isBlank() || b.isBlank() || c.isBlank() || d.isBlank()) continue
                val minExpLen = if (lesson.lowercase(Locale("tr", "TR")).contains("paragraf")) 40 else 120
                if (exp.length < minExpLen) continue

                if (ca !in listOf("A", "B", "C", "D")) {
                    ca = when {
                        a.equals(ca, true) -> "A"
                        b.equals(ca, true) -> "B"
                        c.equals(ca, true) -> "C"
                        d.equals(ca, true) -> "D"
                        else -> "A"
                    }
                }

                // --- ALT ÇİZİLİ KURALI (Sınav standardı) ---
                val explicitUnderlineNeeded = needsUnderlinedWord(qText)

                val anyMarker = hasUnderlineMarker(qText) ||
                        hasUnderlineMarker(a) ||
                        hasUnderlineMarker(b) ||
                        hasUnderlineMarker(c) ||
                        hasUnderlineMarker(d)

                if (explicitUnderlineNeeded) {
                    // “altı çizili” diyorsa marker zorunlu, yoksa soruyu discard
                    if (!anyMarker) continue
                    // Markerlar olduğu gibi KALIR; UI underline edecektir.
                } else {
                    // “altı çizili” yoksa: model yanlışlıkla marker üretse bile temizle
                    if (anyMarker) {
                        qText = stripUnderlineMarkers(qText)
                        a = stripUnderlineMarkers(a)
                        b = stripUnderlineMarkers(b)
                        c = stripUnderlineMarkers(c)
                        d = stripUnderlineMarkers(d)
                    }
                }

                list.add(
                    QuestionModel(
                        question = qText,
                        optionA = a,
                        optionB = b,
                        optionC = c,
                        optionD = d,
                        correctAnswer = ca,
                        explanation = exp,
                        lesson = lesson,
                        needsImage = false,
                        imagePrompt = ""
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "parseExamJson hata: ${e.message}")
        }
        return list
    }

    private fun parseMiniGameJson(raw: String, type: String): List<GameQuestion> {
        val list = mutableListOf<GameQuestion>()
        try {
            val clean = cleanJsonArrayString(raw)
            val arr = JSONArray(clean)

            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue

                val text0 = obj.optString("text").ifBlank { obj.optString("question") }.trim()
                if (text0.isBlank()) continue

                val options = mutableListOf<String>()
                val optionsArr = obj.optJSONArray("options")
                if (optionsArr != null && optionsArr.length() == 4) {
                    for (k in 0 until 4) options.add(optionsArr.optString(k).trim())
                } else {
                    val a = obj.optString("optionA").trim()
                    val b = obj.optString("optionB").trim()
                    val c = obj.optString("optionC").trim()
                    val d = obj.optString("optionD").trim()
                    if (a.isBlank() || b.isBlank() || c.isBlank() || d.isBlank()) continue
                    options.addAll(listOf(a, b, c, d))
                }

                if (options.size != 4) continue
                if (options.any { it.isBlank() }) continue

                var correctIndex = obj.optInt("correctIndex", -1)
                if (correctIndex !in 0..3) {
                    val ca = obj.optString("correctAnswer").uppercase(Locale.US).trim()
                    correctIndex = when (ca) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> -1
                    }
                }
                if (correctIndex !in 0..3) continue

                list.add(
                    GameQuestion(
                        lesson = type,
                        text = text0,
                        correctIndex = correctIndex,
                        options = options
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "parseMiniGameJson hata: ${e.message}")
        }
        return list
    }

    /**
     * Şıkları karıştırır ve doğru cevabın yeni yerini bulur.
     */
    private fun shuffleOptions(q: QuestionModel): QuestionModel {
        val correctText = when (q.correctAnswer.uppercase(Locale.US)) {
            "A" -> q.optionA
            "B" -> q.optionB
            "C" -> q.optionC
            "D" -> q.optionD
            else -> q.optionA
        }

        val allOptions = listOf(q.optionA, q.optionB, q.optionC, q.optionD).shuffled()

        val newCorrectIndex = allOptions.indexOf(correctText)
        val newCorrectLetter = when (newCorrectIndex) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            else -> "A"
        }

        return q.copy(
            optionA = allOptions[0],
            optionB = allOptions[1],
            optionC = allOptions[2],
            optionD = allOptions[3],
            correctAnswer = newCorrectLetter
        )
    }
}
