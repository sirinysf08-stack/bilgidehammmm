package com.example.bilgideham

/**
 * RAG Repository
 * Kazanım verilerine erişim ve arama işlemleri
 */
object RagRepository {

     private val agsOabtUnitNamesByField: Map<String, List<String>> = mapOf(
         "turkce" to listOf(
             "Anlama ve Anlatma Teknikleri",
             "Dil Bilgisi ve Dil Bilimi",
             "Çocuk Edebiyatı",
             "Türk Halk Edebiyatı",
             "Eski Türk Edebiyatı",
             "Yeni Türk Edebiyatı",
             "Edebiyat Bilgi ve Kuramları"
         ),
         "ilkmat" to listOf(
             "Analiz",
             "Cebir",
             "Geometri",
             "Uygulamalı Matematik"
         ),
         "fen" to listOf(
             "Fizik",
             "Kimya",
             "Biyoloji",
             "Jeoloji (Yer Bilimi)",
             "Astronomi",
             "Çevre Bilimi"
         ),
         "rehberlik" to listOf(
             "Temel Psikolojik Kavramlar",
             "Psikolojik Danışma Kuram ve Teknikleri",
             "Davranış ve Uyum Problemleri",
             "Bireyi Tanıma Teknikleri",
             "Bireyle ve Grupla Psikolojik Danışma",
             "Mesleki Rehberlik ve Kariyer Danışmanlığı",
             "Araştırma ve Program Geliştirme",
             "Özel Eğitim ve Yasal Konular"
         ),
         "okoncesi" to listOf(
             "Erken Çocukluk Eğitimine Giriş",
             "Erken Çocukluk Döneminde Gelişim",
             "Çocuk Sağlığı ve İlk Yardım",
             "Erken Çocuklukta Sanat",
             "Erken Çocukluk Dönemi Edebiyatı",
             "Program, Yöntem ve Yaklaşımlar",
             "Anne-Baba Eğitimi",
             "Çocuk Hakları"
         ),
         "beden" to listOf(
             "Beden Eğitimi ve Sporun Temelleri",
             "İnsan Anatomisi ve Kinesiyoloji",
             "Egzersiz Fizyolojisi",
             "Antrenman Bilgisi",
             "Sağlık Bilgisi ve İlk Yardım"
         ),
         "din" to listOf(
             "Kur'an-ı Kerim ve Tecvid",
             "Tefsir",
             "Hadis",
             "Fıkıh",
             "Akaid ve Kelam",
             "İslam Mezhepleri ve Akımlar",
             "Siyer",
             "İslam Tarihi, Kültür ve Medeniyeti",
             "İslam Felsefesi ve Din Bilimleri",
             "Din Eğitimi"
         ),
         "kimya" to listOf(
             "Temel Kimya",
             "Analitik Kimya",
             "Anorganik Kimya",
             "Organik Kimya",
             "Fizikokimya"
         ),
         "biyoloji" to listOf(
             "Hücre ve Metabolizma",
             "Bitki Biyolojisi",
             "İnsan ve Hayvan Biyolojisi",
             "Ekoloji",
             "Canlıların Sınıflandırılması",
             "Genetik"
         ),
         "cografya" to listOf(
             "Fiziki Coğrafya",
             "Beşerî ve Ekonomik Coğrafya",
             "Kıtalar ve Ülkeler Coğrafyası"
         ),
         "edebiyat" to listOf(
             "Eski Türk Dili ve Yeni Türk Dili",
             "Türk Halk Edebiyatı",
             "Eski Türk Edebiyatı",
             "Yeni Türk Edebiyatı"
         ),
         "sinif" to listOf(
             "İlkokulda Temel Matematik",
             "İlkokulda Temel Fen Bilimleri",
             "Türk Dili",
             "Türk Tarihi ve Kültürü",
             "Türkiye Coğrafyası ve Jeopolitiği",
             "Çocuk Edebiyatı",
             "Alan Eğitimi"
         )
     )

     private fun tryResolveAgsOabtUnitFromSubjectId(subjectId: String): Pair<String, String>? {
         // Expected: <field>_unite_<index>
         if (!subjectId.contains("_unite_")) return null
         val parts = subjectId.split("_unite_")
         if (parts.size != 2) return null
         val field = parts[0]
         val index = parts[1].toIntOrNull() ?: return null
         val units = agsOabtUnitNamesByField[field] ?: return null
         val unitName = units.getOrNull(index - 1) ?: return null
         val dersName = when (field) {
             "turkce" -> "Türkçe"
             "ilkmat" -> "İlköğretim Matematik"
             "fen" -> "Fen Bilimleri"
             "rehberlik" -> "Rehberlik"
             "okoncesi" -> "Okul Öncesi"
             "beden" -> "Beden Eğitimi"
             "din" -> "Din Kültürü"
             "kimya" -> "Kimya"
             "biyoloji" -> "Biyoloji"
             "cografya" -> "Coğrafya"
             "edebiyat" -> "Türk Dili ve Edebiyatı"
             "sinif" -> "Sınıf Öğretmenliği"
             else -> return null
         }
         return dersName to unitName
     }

     private fun buildAgsOabtUnitContext(ders: String): String {
         val raw = ders.trim()

         // 1) Unit subjectId route: turkce_unite_1
         val token = Regex("[a-zçğıöşü]+_unite_\\d+", RegexOption.IGNORE_CASE)
             .find(raw)
             ?.value
         val byId = tryResolveAgsOabtUnitFromSubjectId(token ?: raw)
         if (byId != null) {
             val (dersName, unitName) = byId
             val kazanimlar = (
                 AgsOabtTarihHaricKazanimlar.tumKazanimlar() +
                     AgsOabtBransKazanimlar.tumKazanimlar() +
                     AgsOabtEksikKazanimlar.tumKazanimlar()
                 )
                 .filter { it.ders.equals(dersName, ignoreCase = true) && it.unite.equals(unitName, ignoreCase = true) }

             if (kazanimlar.isEmpty()) return ""
             return buildString {
                 appendLine("=== AGS ÖABT MÜFREDAT BAĞLAMI ===")
                 appendLine("📚 Ders: $dersName")
                 appendLine("📌 Ünite: $unitName")
                 appendLine()
                 kazanimlar.take(5).forEach { kazanim ->
                     appendLine(kazanim.toContextText())
                     appendLine("---")
                 }
                 appendLine()
                 appendLine("⚠️ ÖNEMLİ: Sorular ÖABT (Öğretmenlik Alan Bilgisi Testi) seviyesinde olmalı.")
                 appendLine("- 5 şık (A, B, C, D, E) kullanılmalı")
                 appendLine("- Akademik düzeyde, derinlemesine bilgi ölçen sorular")
             }
         }

         // 2) Admin prompt friendly format: "AGS <ders> - <ünite>" (or similar)
         val normalized = raw.replace("AGS", "", ignoreCase = true).trim()
         val split = normalized.split("-").map { it.trim() }.filter { it.isNotBlank() }
         if (split.size >= 2) {
             val maybeDers = split.first()
             val maybeUnit = split.last()
             val kazanimlar = (
                 AgsOabtTarihHaricKazanimlar.tumKazanimlar() +
                     AgsOabtBransKazanimlar.tumKazanimlar() +
                     AgsOabtEksikKazanimlar.tumKazanimlar()
                 ).filter {
                 it.ders.equals(maybeDers, ignoreCase = true) && it.unite.equals(maybeUnit, ignoreCase = true)
             }
             if (kazanimlar.isNotEmpty()) {
                 return buildString {
                     appendLine("=== AGS ÖABT MÜFREDAT BAĞLAMI ===")
                     appendLine("📚 Ders: $maybeDers")
                     appendLine("📌 Ünite: $maybeUnit")
                     appendLine()
                     kazanimlar.take(5).forEach { kazanim ->
                         appendLine(kazanim.toContextText())
                         appendLine("---")
                     }
                 }
             }
         }

         // 3) Fallback keyword search inside the non-tarih DB
         val searched = EmbeddingService.searchByKeywords(
             raw,
             AgsOabtTarihHaricKazanimlar.tumKazanimlar() +
                 AgsOabtBransKazanimlar.tumKazanimlar() +
                 AgsOabtEksikKazanimlar.tumKazanimlar(),
             5
         )
         if (searched.isEmpty()) return ""
         return buildString {
             appendLine("=== AGS ÖABT MÜFREDAT BAĞLAMI ===")
             appendLine()
             searched.forEach { kazanim ->
                 appendLine(kazanim.toContextText())
                 appendLine("---")
             }
         }
     }

     private fun normalizeDersForKazanims(ders: String): Pair<String, String?> {
        val trimmed = ders.trim()
        return when {
            trimmed.contains("sözel yetenek", ignoreCase = true) -> "Sözel Yetenek" to null
            trimmed.contains("sayısal yetenek", ignoreCase = true) -> "Sayısal Yetenek" to null
            trimmed.contains("eğitimin temelleri", ignoreCase = true) -> "Eğitimin Temelleri" to null
            trimmed.contains("mevzuat", ignoreCase = true) -> "Mevzuat" to null
            trimmed.contains("türkiye coğrafyası", ignoreCase = true) -> "Türkiye Coğrafyası" to null
            trimmed.contains("tarih", ignoreCase = true) -> "Tarih" to null
            trimmed.contains("paragraf", ignoreCase = true) -> "Türkçe" to "Paragraf"
            else -> trimmed to null
        }
    }

    /**
     * Seviye, okul türü ve sınıfa göre kazanımları getir
     */
    fun getKazanimlar(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): List<RagKazanim> {
        return when {
            // İlkokul 3. sınıf
            level == EducationLevel.ILKOKUL && grade == 3 -> Ilkokul3Kazanimlari.tumKazanimlar()
            
            // İlkokul 4. sınıf
            level == EducationLevel.ILKOKUL && grade == 4 -> Ilkokul4Kazanimlari.tumKazanimlar()
            
            // 5. sınıf - Tüm ortaokullar için
            (level == EducationLevel.ORTAOKUL || level == EducationLevel.ILKOKUL) && grade == 5 -> Sinif5Kazanimlari.tumKazanimlar()
            
            // 6. sınıf
            level == EducationLevel.ORTAOKUL && grade == 6 -> Sinif6Kazanimlari.tumKazanimlar()
            
            // 7. sınıf
            level == EducationLevel.ORTAOKUL && grade == 7 -> Sinif7Kazanimlari.tumKazanimlar()
            
            // 8. sınıf (LGS)
            level == EducationLevel.ORTAOKUL && grade == 8 -> Sinif8Kazanimlari.tumKazanimlar()
            
            // Lise 9. sınıf
            level == EducationLevel.LISE && grade == 9 -> Lise9Kazanimlari.tumKazanimlar()
            
            // Lise 10. sınıf
            level == EducationLevel.LISE && grade == 10 -> Lise10Kazanimlari.tumKazanimlar()
            
            // Lise 11. sınıf
            level == EducationLevel.LISE && grade == 11 -> Lise11Kazanimlari.tumKazanimlar()
            
            // Lise 12. sınıf
            level == EducationLevel.LISE && grade == 12 -> Lise12Kazanimlari.tumKazanimlar()
            
            // AGS Tarih Öğretmenliği
            level == EducationLevel.AGS && schoolType == SchoolType.AGS_OABT ->
                AgsTarihKazanimlari.tumKazanimlar() +
                    AgsOabtTarihHaricKazanimlar.tumKazanimlar() +
                    AgsOabtBransKazanimlar.tumKazanimlar() +
                    AgsOabtEksikKazanimlar.tumKazanimlar()

            // AGS MEB (1. Oturum)
            level == EducationLevel.AGS && schoolType == SchoolType.AGS_MEB ->
                AgsMebKazanimlar.tumKazanimlar()
            
            // Diğer seviyeler için boş liste
            else -> emptyList()
        }
    }

    /**
     * Ders adına göre kazanımları getir
     */
    fun getKazanimlarByDers(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        ders: String
    ): List<RagKazanim> {
        val tumKazanimlar = getKazanimlar(level, schoolType, grade)
        return tumKazanimlar.filter { it.ders.equals(ders, ignoreCase = true) }
    }

    /**
     * Konu araması yap
     */
    fun searchKazanimlar(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        query: String,
        limit: Int = 5
    ): List<RagKazanim> {
        val kazanimlar = getKazanimlar(level, schoolType, grade)
        return EmbeddingService.searchByKeywords(query, kazanimlar, limit)
    }

    /**
     * AI prompt için bağlam oluştur
     */
    fun buildContext(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        ders: String,
        konu: String? = null
    ): String {
        // AGS Tarih için özel işlem - ünite bazlı arama
        if (level == EducationLevel.AGS && schoolType == SchoolType.AGS_OABT) {
            return if (
                ders.startsWith("tarih_unite_", ignoreCase = true) ||
                    ders.startsWith("AGS Tarih -", ignoreCase = true)
            ) {
                buildAgsTarihContext(ders)
            } else {
                buildAgsOabtUnitContext(ders)
            }
        }

         val (normalizedDers, defaultKonu) = normalizeDersForKazanims(ders)
         val effectiveKonu = konu ?: defaultKonu
        
        // Önce derse göre filtrele
        var kazanimlar = getKazanimlarByDers(level, schoolType, grade, normalizedDers)
        
        // Konu varsa arama yap
        if (!effectiveKonu.isNullOrBlank() && kazanimlar.isNotEmpty()) {
            val searchResults = EmbeddingService.searchByKeywords(effectiveKonu, kazanimlar, 3)
            if (searchResults.isNotEmpty()) {
                kazanimlar = searchResults
            }
        }
        
        if (kazanimlar.isEmpty()) {
            return ""
        }
        
        return buildString {
            appendLine("=== MEB MÜFREDAT BAĞLAMI ===")
            appendLine()
            kazanimlar.take(3).forEach { kazanim ->
                appendLine(kazanim.toContextText())
                appendLine("---")
            }
        }
    }

    /**
     * AGS Tarih için özel bağlam oluşturma
     * Ders adından ünite ID'sini çıkarır ve ilgili kazanımları getirir
     */
    private fun buildAgsTarihContext(ders: String): String {
        // "AGS Tarih - Tarih Bilimi" formatından ünite adını çıkar
        val uniteName = ders.replace("AGS Tarih - ", "").trim()
        
        // Ünite ID'sini bul
        val uniteId = when {
            uniteName.contains("Tarih Bilimi", ignoreCase = true) -> 1
            uniteName.contains("Osmanlı Türkçesi", ignoreCase = true) || uniteName.contains("Osmanlıca", ignoreCase = true) -> 2
            uniteName.contains("Uygarlığın Doğuşu", ignoreCase = true) || uniteName.contains("İlk Çağ", ignoreCase = true) -> 3
            uniteName.contains("İlk Türk", ignoreCase = true) -> 4
            uniteName.contains("İslam Tarihi", ignoreCase = true) -> 5
            uniteName.contains("Türk İslam", ignoreCase = true) -> 6
            uniteName.contains("Türk Dünyası", ignoreCase = true) -> 7
            uniteName.contains("Osmanlı Tarihi", ignoreCase = true) || uniteName.contains("Osmanlı", ignoreCase = true) -> 8
            uniteName.contains("En Uzun Yüzyıl", ignoreCase = true) || uniteName.contains("1800", ignoreCase = true) -> 9
            uniteName.contains("XX. Yüzyıl", ignoreCase = true) || uniteName.contains("Dağılma", ignoreCase = true) -> 10
            uniteName.contains("Milli Mücadele", ignoreCase = true) || uniteName.contains("Kurtuluş", ignoreCase = true) -> 11
            uniteName.contains("Atatürk", ignoreCase = true) || uniteName.contains("Cumhuriyet", ignoreCase = true) -> 12
            uniteName.contains("Dünya Tarihi", ignoreCase = true) -> 13
            uniteName.contains("Çağdaş", ignoreCase = true) -> 14
            else -> 0
        }
        
        val kazanimlar = if (uniteId > 0) {
            AgsTarihKazanimlari.getKazanimlarByUnite(uniteId)
        } else {
            // Anahtar kelime araması yap
            EmbeddingService.searchByKeywords(uniteName, AgsTarihKazanimlari.tumKazanimlar(), 5)
        }
        
        if (kazanimlar.isEmpty()) {
            return ""
        }
        
        return buildString {
            appendLine("=== AGS TARİH ÖĞRETMENLİĞİ MÜFREDAT BAĞLAMI ===")
            appendLine("📚 Ünite: $uniteName")
            appendLine()
            kazanimlar.forEach { kazanim ->
                appendLine(kazanim.toContextText())
                appendLine("---")
            }
            appendLine()
            appendLine("⚠️ ÖNEMLİ: Sorular ÖABT (Öğretmenlik Alan Bilgisi Testi) seviyesinde olmalı.")
            appendLine("- 5 şık (A, B, C, D, E) kullanılmalı")
            appendLine("- Akademik düzeyde, derinlemesine bilgi ölçen sorular")
            appendLine("- Tarih öğretmeni adaylarına yönelik")
        }
    }

    /**
     * Mevcut kazanım sayısını döndür (istatistik için)
     */
    fun getStats(): Map<String, Int> {
        return mapOf(
            "3. Sınıf - Türkçe" to Ilkokul3Kazanimlari.turkce.size,
            "3. Sınıf - Matematik" to Ilkokul3Kazanimlari.matematik.size,
            "3. Sınıf - Fen Bilimleri" to Ilkokul3Kazanimlari.fenBilimleri.size,
            "3. Sınıf - Hayat Bilgisi" to Ilkokul3Kazanimlari.hayatBilgisi.size,
            "3. Sınıf - İngilizce" to Ilkokul3Kazanimlari.ingilizce.size,
            "4. Sınıf - Türkçe" to Ilkokul4Kazanimlari.turkce.size,
            "4. Sınıf - Matematik" to Ilkokul4Kazanimlari.matematik.size,
            "4. Sınıf - Fen Bilimleri" to Ilkokul4Kazanimlari.fenBilimleri.size,
            "4. Sınıf - Sosyal Bilgiler" to Ilkokul4Kazanimlari.sosyalBilgiler.size,
            "5. Sınıf - Türkçe" to Sinif5Kazanimlari.turkce.size,
            "5. Sınıf - Matematik" to Sinif5Kazanimlari.matematik.size,
            "5. Sınıf - Fen Bilimleri" to Sinif5Kazanimlari.fenBilimleri.size,
            "5. Sınıf - Sosyal Bilgiler" to Sinif5Kazanimlari.sosyalBilgiler.size,
            "5. Sınıf - İngilizce" to Sinif5Kazanimlari.ingilizce.size,
            "5. Sınıf - Din Kültürü" to Sinif5Kazanimlari.dinKulturu.size,
            "Toplam 3. Sınıf" to Ilkokul3Kazanimlari.tumKazanimlar().size,
            "Toplam 4. Sınıf" to Ilkokul4Kazanimlari.tumKazanimlar().size,
            "Toplam 5. Sınıf" to Sinif5Kazanimlari.tumKazanimlar().size,
            "Lise 9. Sınıf - Matematik" to Lise9Kazanimlari.matematik.size,
            "Lise 9. Sınıf - Fizik" to Lise9Kazanimlari.fizik.size,
            "Lise 9. Sınıf - Kimya" to Lise9Kazanimlari.kimya.size,
            "Lise 9. Sınıf - Biyoloji" to Lise9Kazanimlari.biyoloji.size,
            "Lise 9. Sınıf - Tarih" to Lise9Kazanimlari.tarih.size,
            "Lise 9. Sınıf - Coğrafya" to Lise9Kazanimlari.cografya.size,
            "Toplam Lise 9. Sınıf" to Lise9Kazanimlari.tumKazanimlar().size,
            "Toplam Lise 10. Sınıf" to Lise10Kazanimlari.tumKazanimlar().size,
            "Toplam Lise 11. Sınıf" to Lise11Kazanimlari.tumKazanimlar().size,
            "Toplam Lise 12. Sınıf" to Lise12Kazanimlari.tumKazanimlar().size,
            "AGS Tarih - Toplam" to AgsTarihKazanimlari.tumKazanimlar().size
        )
    }
}
