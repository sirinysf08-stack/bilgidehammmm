package com.example.bilgideham

/**
 * RAG Kazanım Modeli
 * MEB müfredat kazanımlarını temsil eden veri sınıfı
 */
data class RagKazanim(
    val kod: String,
    val ders: String,
    val unite: String,
    val konu: String,
    val aciklama: String,
    val ornekler: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val zorlukSeviyesi: String = "Orta" // Kolay, Orta, Zor
) {
    /**
     * AI prompt için bağlam metni oluştur
     */
    fun toContextText(): String {
        return buildString {
            appendLine("📚 $ders - $unite")
            appendLine("📌 Konu: $konu")
            appendLine("💡 Kazanım: $aciklama")
            if (ornekler.isNotEmpty()) {
                appendLine("📝 Örnekler: ${ornekler.joinToString(", ")}")
            }
            if (keywords.isNotEmpty()) {
                appendLine("🔑 Anahtar: ${keywords.joinToString(", ")}")
            }
        }
    }
}
