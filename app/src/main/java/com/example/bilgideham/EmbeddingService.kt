package com.example.bilgideham

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * RAG Embedding Servisi
 * Gemini API kullanarak metin embedding'leri oluşturur
 */
object EmbeddingService {

    /**
     * Basit keyword-based arama (Embedding API gerektirmeden)
     * İlk aşamada keyword matching kullanıyoruz
     */
    fun searchByKeywords(
        query: String,
        kazanimlar: List<RagKazanim>,
        limit: Int = 5
    ): List<RagKazanim> {
        val queryWords = query.lowercase().split(" ", ",", "-").filter { it.length > 2 }
        
        // Kazanımları skorla
        val scored = kazanimlar.map { kazanim ->
            var score = 0
            
            // Konu eşleşmesi (en yüksek ağırlık)
            if (kazanim.konu.lowercase().contains(query.lowercase())) {
                score += 100
            }
            
            // Ünite eşleşmesi
            if (kazanim.unite.lowercase().contains(query.lowercase())) {
                score += 50
            }
            
            // Açıklama eşleşmesi
            queryWords.forEach { word ->
                if (kazanim.aciklama.lowercase().contains(word)) {
                    score += 10
                }
            }
            
            // Keyword eşleşmesi
            kazanim.keywords.forEach { keyword ->
                queryWords.forEach { word ->
                    if (keyword.lowercase().contains(word) || word.contains(keyword.lowercase())) {
                        score += 20
                    }
                }
            }
            
            // Örnek eşleşmesi
            kazanim.ornekler.forEach { ornek ->
                if (ornek.lowercase().contains(query.lowercase())) {
                    score += 15
                }
            }
            
            kazanim to score
        }
        
        // En yüksek skorluları döndür
        return scored
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    /**
     * Ders ve konuya göre kazanım ara
     */
    fun searchByDersAndKonu(
        ders: String,
        konu: String? = null,
        kazanimlar: List<RagKazanim>
    ): List<RagKazanim> {
        return kazanimlar.filter { kazanim ->
            val dersMatch = kazanim.ders.equals(ders, ignoreCase = true)
            val konuMatch = konu?.let { 
                kazanim.konu.contains(it, ignoreCase = true) ||
                kazanim.unite.contains(it, ignoreCase = true)
            } ?: true
            
            dersMatch && konuMatch
        }
    }
}
