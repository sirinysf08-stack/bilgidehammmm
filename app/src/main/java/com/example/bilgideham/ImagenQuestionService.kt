package com.example.bilgideham

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Firebase Vertex AI Imagen ile karmaşık görsel üretimi
 * 
 * Desteklenen görsel türleri:
 * - Biyoloji: Hücre, organ, sistem şemaları
 * - Coğrafya: Haritalar, iklim diyagramları
 * - Fizik/Kimya: Deney düzenekleri, molekül yapıları
 * - Geometri: Karmaşık şekiller, 3D cisimler
 */
object ImagenQuestionService {
    
    private const val TAG = "ImagenService"
    private const val IMAGEN_MODEL = "imagegeneration@006"
    
    // Gemini Vision (resim analizi ve açıklama için)
    private val geminiVision by lazy {
        Firebase.vertexAI.generativeModel("gemini-2.0-flash")
    }
    
    /**
     * Soru için görsel üret
     * @param imagePrompt Görsel açıklaması (türkçe)
     * @param lesson Ders adı (prompt optimizasyonu için)
     * @return Base64 encoded image ve mime type
     */
    suspend fun generateQuestionImage(
        imagePrompt: String,
        lesson: String
    ): ImageResult = withContext(Dispatchers.IO) {
        
        if (imagePrompt.isBlank()) {
            return@withContext ImageResult.Error("Boş görsel promptu")
        }
        
        try {
            DebugLog.d(TAG, "🎨 Görsel üretiliyor: $imagePrompt")
            
            // Eğitim odaklı, temiz prompt oluştur
            val optimizedPrompt = buildEducationalImagePrompt(imagePrompt, lesson)
            
            // Gemini ile görsel üret (text-to-image henüz desteklenmiyorsa alternatif yol)
            // Not: Firebase Vertex AI'da doğrudan Imagen API'si farklı çağrılabilir
            // Şimdilik placeholder olarak bırakıyoruz
            
            // Alternatif: Gemini'den SVG/ASCII art iste
            val svgResult = generateSvgFallback(optimizedPrompt, lesson)
            
            if (svgResult != null) {
                return@withContext ImageResult.Success(
                    base64 = svgResult,
                    mimeType = "image/svg+xml"
                )
            }
            
            ImageResult.Error("Görsel üretilemedi")
            
        } catch (e: Exception) {
            Log.e(TAG, "Imagen error: ${e.message}")
            ImageResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
    
    /**
     * Eğitim odaklı görsel promptu oluştur
     */
    private fun buildEducationalImagePrompt(prompt: String, lesson: String): String {
        val lessonContext = when {
            lesson.contains("Biyoloji", ignoreCase = true) -> 
                "scientific biology diagram, labeled, educational, clean white background"
            lesson.contains("Coğrafya", ignoreCase = true) -> 
                "educational map or geography diagram, labeled, simple colors"
            lesson.contains("Fizik", ignoreCase = true) -> 
                "physics diagram, scientific illustration, labeled arrows and forces"
            lesson.contains("Kimya", ignoreCase = true) -> 
                "chemistry molecular structure, clean diagram, labeled atoms"
            lesson.contains("Matematik", ignoreCase = true) || lesson.contains("Geometri", ignoreCase = true) -> 
                "geometry diagram, clean lines, labeled points and angles"
            else -> "educational diagram, simple, labeled, clean background"
        }
        
        return """
            Create a simple, clean educational diagram for Turkish exam:
            Subject: $prompt
            Style: $lessonContext
            Requirements:
            - Simple, clear illustration
            - White or light background
            - Black labels in Turkish where needed
            - No text watermarks
            - Professional textbook style
        """.trimIndent()
    }
    
    /**
     * Gemini ile SVG fallback üretimi
     * Imagen yoksa Gemini'den SVG kodu iste
     */
    private suspend fun generateSvgFallback(prompt: String, lesson: String): String? {
        return try {
            val svgPrompt = """
                Sen bir eğitim materyali tasarımcısısın.
                Aşağıdaki konu için basit, temiz bir SVG kodu üret:
                
                Konu: $prompt
                Ders: $lesson
                
                Kurallar:
                1. Sadece SVG kodu döndür, başka hiçbir şey yazma
                2. viewBox="0 0 300 200" kullan
                3. Temiz, basit çizgiler
                4. Etiketler Türkçe olsun
                5. Profesyonel ders kitabı tarzı
                
                Sadece <svg>...</svg> döndür.
            """.trimIndent()
            
            val response = geminiVision.generateContent(svgPrompt)
            val svgCode = response.text?.trim()
            
            if (svgCode != null && svgCode.startsWith("<svg") && svgCode.endsWith("</svg>")) {
                // SVG'yi Base64'e çevir
                Base64.encodeToString(svgCode.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "SVG fallback failed: ${e.message}")
            null
        }
    }
    
    /**
     * Mevcut soruya görsel ekle
     */
    suspend fun addImageToQuestion(question: QuestionModel): QuestionModel {
        if (!question.needsImage || question.imagePrompt.isBlank()) {
            return question
        }
        
        // Zaten resim varsa atla
        if (!question.imageBase64.isNullOrBlank()) {
            return question
        }
        
        val result = generateQuestionImage(question.imagePrompt, question.lesson)
        
        return when (result) {
            is ImageResult.Success -> question.copy(
                imageBase64 = result.base64,
                imageMimeType = result.mimeType
            )
            is ImageResult.Error -> {
                Log.w(TAG, "Image generation failed: ${result.message}")
                question
            }
        }
    }
    
    /**
     * Toplu soru listesine görsel ekle
     */
    suspend fun addImagesToQuestions(questions: List<QuestionModel>): List<QuestionModel> {
        return questions.map { addImageToQuestion(it) }
    }
    
    /**
     * Base64 string'i Bitmap'e çevir
     */
    fun decodeBase64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "Bitmap decode error: ${e.message}")
            null
        }
    }
    
    /**
     * Bitmap'i Base64'e çevir
     */
    fun encodeBitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    sealed class ImageResult {
        data class Success(val base64: String, val mimeType: String) : ImageResult()
        data class Error(val message: String) : ImageResult()
    }
}
