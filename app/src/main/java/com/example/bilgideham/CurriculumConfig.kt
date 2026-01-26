package com.example.bilgideham

/**
 * MEB 2025 TYMM (Türkiye Yüzyılı Maarif Modeli) Müfredat Yapılandırması
 *
 * Desteklenen Seviyeler:
 * - İlkokul (4. sınıf)
 * - Ortaokul (5-8. sınıf) - Standard & İmam Hatip
 * - Lise (9-12. sınıf) - Anadolu, Fen, Sosyal Bilimler, İmam Hatip, MTAL
 * - KPSS (Ortaöğretim, Önlisans, Lisans)
 */

// ==================== EĞİTİM SEVİYELERİ ====================

enum class EducationLevel(
    val displayName: String,
    val description: String,
    val icon: String,
    val colorHex: Long
) {
    ILKOKUL("İlkokul", "3-4. Sınıf", "📚", 0xFF4CAF50),
    ORTAOKUL("Ortaokul", "5-8. Sınıf", "🎓", 0xFF2196F3),
    LISE("Lise", "9-12. Sınıf", "🏫", 0xFF9C27B0),
    KPSS("KPSS", "Kamu Personeli Seçme Sınavı", "📋", 0xFFFF5722),
    AGS("AGS", "Adalet Görevde Yükselme Sınavı", "⚖️", 0xFF5E35B1)
}

// ==================== OKUL TÜRLERİ ====================

enum class SchoolType(
    val level: EducationLevel,
    val displayName: String,
    val description: String,
    val grades: List<Int>
) {
    // İlkokul
    ILKOKUL_STANDARD(EducationLevel.ILKOKUL, "İlkokul", "3-4. Sınıf Müfredatı", listOf(3, 4)),

    // Ortaokul (tek tip - müfredat birleştirildi)
    ORTAOKUL_STANDARD(EducationLevel.ORTAOKUL, "Ortaokul", "Genel Ortaokul Müfredatı", listOf(5, 6, 7, 8)),

    // Lise
    LISE_GENEL(EducationLevel.LISE, "Lise", "Genel Lise Müfredatı", listOf(9, 10, 11, 12)),

    // KPSS
    KPSS_ORTAOGRETIM(EducationLevel.KPSS, "KPSS Ortaöğretim", "Lise Mezunları İçin", emptyList()),
    KPSS_ONLISANS(EducationLevel.KPSS, "KPSS Önlisans", "Önlisans Mezunları İçin", emptyList()),
    KPSS_LISANS(EducationLevel.KPSS, "KPSS Lisans", "Lisans Mezunları İçin", emptyList()),

    // AGS
    AGS_MEB(EducationLevel.AGS, "MEB AGS", "1. Oturum - Genel Kültür & Mevzuat", emptyList()),
    AGS_OABT(EducationLevel.AGS, "2. Oturum (ÖABT)", "Öğretmenlik Alan Bilgisi Testleri", emptyList())
}

// ==================== ÜNİTE TANIMLARI ====================

data class UnitConfig(
    val id: String,
    val name: String,
    val topics: List<String> = emptyList()
)

// ==================== DERS TANIMLARI ====================

data class SubjectConfig(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val colorHex: Long,
    val route: String,
    val units: List<UnitConfig> = emptyList(),
    val isActive: Boolean = true  // Soru havuzu hazır mı? false = "Yakında" gösterilir
)

// ==================== MÜFREDAT YÖNETİCİSİ ====================

object CurriculumManager {

    fun getSubjectsFor(schoolType: SchoolType, grade: Int? = null): List<SubjectConfig> {
        return when (schoolType.level) {
            EducationLevel.ILKOKUL -> getIlkokulSubjects(grade ?: 4)
            EducationLevel.ORTAOKUL -> getOrtaokulSubjects(schoolType, grade ?: 5)
            EducationLevel.LISE -> getLiseSubjects(schoolType, grade ?: 9)
            EducationLevel.KPSS -> getKpssSubjects(schoolType)
            EducationLevel.AGS -> getAgsSubjects(schoolType)
        }
    }
    
    // ==================== AGS DERSLERİ ====================
    
    private fun getAgsSubjects(schoolType: SchoolType): List<SubjectConfig> {
        return when (schoolType) {
            SchoolType.AGS_MEB -> listOf(
                SubjectConfig("ags_sozel", "Sözel Yetenek", "Türkçe ve Dil Bilgisi", "📖", 0xFF1976D2, "ags_sozel"),
                SubjectConfig("ags_paragraf", "Paragraf", "Paragraf Soruları", "📚", 0xFF9C27B0, "ags_paragraf"),
                SubjectConfig("ags_sayisal", "Sayısal Yetenek", "Matematik ve Mantık", "🔢", 0xFFFF5722, "ags_sayisal"),
                SubjectConfig("ags_tarih", "Tarih", "Atatürk İlkeleri ve İnkılap Tarihi", "🏛️", 0xFF795548, "ags_tarih"),
                SubjectConfig("ags_cografya", "Türkiye Coğrafyası", "Türkiye Fiziki ve Beşeri Coğrafyası", "🗺️", 0xFF4CAF50, "ags_cografya"),
                SubjectConfig("ags_egitim", "Eğitimin Temelleri", "Eğitimin Temelleri ve Türk Millî Eğitim Sistemi", "🎓", 0xFF9C27B0, "ags_egitim"),
                SubjectConfig("ags_mevzuat", "Mevzuat", "657 ve İdare Hukuku", "📜", 0xFF5E35B1, "ags_mevzuat")
            )
            SchoolType.AGS_OABT -> listOf(
                SubjectConfig("oabt_turkce", "Türkçe", "Türkçe Öğretmenliği", "📖", 0xFF1976D2, "oabt_turkce", isActive = true),
                SubjectConfig("oabt_ilkmat", "İlköğretim Matematik", "İlköğretim Matematik Öğretmenliği", "🔢", 0xFFFF5722, "oabt_ilkmat", isActive = true),
                SubjectConfig("oabt_fen", "Fen Bilimleri", "Fen Bilimleri Öğretmenliği", "🔬", 0xFF4CAF50, "oabt_fen", isActive = true),
                SubjectConfig("oabt_sosyal", "Sosyal Bilgiler", "Sosyal Bilgiler Öğretmenliği", "🌍", 0xFF9C27B0, "oabt_sosyal", isActive = true),
                SubjectConfig("oabt_edebiyat", "Türk Dili ve Edebiyatı", "Edebiyat Öğretmenliği", "📚", 0xFF673AB7, "oabt_edebiyat", isActive = true),
                SubjectConfig("oabt_tarih", "Tarih", "Tarih Öğretmenliği", "🏛️", 0xFF795548, "oabt_tarih", isActive = true),
                SubjectConfig("oabt_cografya", "Coğrafya", "Coğrafya Öğretmenliği", "🗺️", 0xFF00BCD4, "oabt_cografya", isActive = true),
                SubjectConfig("oabt_matematik", "Matematik", "Matematik Öğretmenliği", "📐", 0xFFE91E63, "oabt_matematik", isActive = true),
                SubjectConfig("oabt_fizik", "Fizik", "Fizik Öğretmenliği", "⚡", 0xFF2196F3, "oabt_fizik", isActive = true),
                SubjectConfig("oabt_kimya", "Kimya", "Kimya Öğretmenliği", "🧪", 0xFFFF9800, "oabt_kimya", isActive = true),
                SubjectConfig("oabt_biyoloji", "Biyoloji", "Biyoloji Öğretmenliği", "🧬", 0xFF8BC34A, "oabt_biyoloji", isActive = true),
                SubjectConfig("oabt_din", "Din Kültürü", "Din Kültürü Öğretmenliği", "☪️", 0xFF607D8B, "oabt_din", isActive = true),
                SubjectConfig("oabt_rehberlik", "Rehberlik", "Rehberlik Öğretmenliği", "🧠", 0xFF3F51B5, "oabt_rehberlik", isActive = true),
                SubjectConfig("oabt_sinif", "Sınıf Öğretmenliği", "Sınıf Öğretmenliği", "👨‍🏫", 0xFFCDDC39, "oabt_sinif", isActive = true),
                SubjectConfig("oabt_okoncesi", "Okul Öncesi", "Okul Öncesi Öğretmenliği", "🎨", 0xFFFF4081, "oabt_okoncesi", isActive = true),
                SubjectConfig("oabt_beden", "Beden Eğitimi", "Beden Eğitimi Öğretmenliği", "🏃", 0xFF4CAF50, "oabt_beden", isActive = true)
            )
            else -> emptyList()
        }
    }

    // ==================== İLKOKUL DERSLERİ (3-4. SINIF) ====================

    private fun getIlkokulSubjects(grade: Int): List<SubjectConfig> {
        return when (grade) {
            3 -> listOf(
                SubjectConfig("turkce_3", "Türkçe", "Okuma ve Yazma", "📖", 0xFF64B5F6, "turkce_3"),
                SubjectConfig("matematik_3", "Matematik", "Sayılar ve İşlemler", "🔢", 0xFFFF8A65, "matematik_3"),
                SubjectConfig("hayat_bilgisi_3", "Hayat Bilgisi", "Çevremizi Tanıyalım", "🌱", 0xFF81C784, "hayat_bilgisi_3"),
                SubjectConfig("fen_3", "Fen Bilimleri", "Doğa ve Bilim", "🔬", 0xFF66BB6A, "fen_3"),
                SubjectConfig("ingilizce_3", "İngilizce", "Temel İngilizce", "🇬🇧", 0xFF4FC3F7, "ingilizce_3")
            )
            4 -> listOf(
                SubjectConfig("turkce_4", "Türkçe", "Okuma ve Yazma", "📖", 0xFF64B5F6, "turkce_4",
                    listOf(
                        UnitConfig("t4_1", "Okuma-Anlama"),
                        UnitConfig("t4_2", "Yazma"),
                        UnitConfig("t4_3", "Dinleme-Konuşma"),
                        UnitConfig("t4_4", "Söz Varlığı"),
                        UnitConfig("t4_5", "Yazım-Noktalama")
                    )),
                SubjectConfig("matematik_4", "Matematik", "Sayılar ve İşlemler", "🔢", 0xFFFF8A65, "matematik_4",
                    listOf(
                        UnitConfig("m4_1", "Sayılar"),
                        UnitConfig("m4_2", "Dört İşlem"),
                        UnitConfig("m4_3", "Kesirler"),
                        UnitConfig("m4_4", "Geometri"),
                        UnitConfig("m4_5", "Ölçme"),
                        UnitConfig("m4_6", "Veri")
                    )),
                SubjectConfig("fen_4", "Fen Bilimleri", "Doğa ve Bilim", "🔬", 0xFF81C784, "fen_4"),
                SubjectConfig("sosyal_4", "Sosyal Bilgiler", "Toplum ve Tarih", "🏛️", 0xFFBA68C8, "sosyal_4"),
                SubjectConfig("ingilizce_4", "İngilizce", "Temel İngilizce", "🇬🇧", 0xFF4FC3F7, "ingilizce_4"),
                SubjectConfig("din_4", "Din Kültürü", "Ahlak ve Değerler", "☪️", 0xFFA1887F, "din_4")
            )
            else -> getIlkokulSubjects(4) // Varsayılan olarak 4. sınıf
        }
    }

    // ==================== ORTAOKUL DERSLERİ (5-8. SINIF) ====================

    private fun getOrtaokulSubjects(schoolType: SchoolType, grade: Int): List<SubjectConfig> {
        val baseSubjects = mutableListOf(
            SubjectConfig("turkce_$grade", "Türkçe", "Dil Bilgisi ve Anlam", "📖", 0xFF64B5F6, "turkce_$grade",
                getTurkceUnits(grade)),
            SubjectConfig("paragraf_$grade", "Paragraf", "Paragraf Soruları", "📚", 0xFF9C27B0, "paragraf_$grade"),
            SubjectConfig("matematik_$grade", "Matematik", "Sayılar ve Problemler", "🔢", 0xFFFF8A65, "matematik_$grade",
                getMatematikOrtaokulUnits(grade)),
            SubjectConfig("fen_$grade", "Fen Bilimleri", "Doğa ve Deney", "🔬", 0xFF81C784, "fen_$grade",
                getFenOrtaokulUnits(grade)),
            SubjectConfig("sosyal_$grade", "Sosyal Bilgiler", "Tarih ve Toplum", "🏛️", 0xFF4DB6AC, "sosyal_$grade",
                getSosyalOrtaokulUnits(grade)),
            SubjectConfig("ingilizce_$grade", "İngilizce", "Grammar and Vocabulary", "🇬🇧", 0xFFBA68C8, "ingilizce_$grade",
                getIngilizceOrtaokulUnits(grade)),
            SubjectConfig("din_$grade", "Din Kültürü", "İnanç ve Ahlak", "☪️", 0xFFA1887F, "din_$grade",
                getDkabOrtaokulUnits(grade))
        )

        // 8. sınıfta T.C. İnkılap Tarihi eklenir, Sosyal Bilgiler kalkar
        if (grade == 8) {
            baseSubjects.removeAll { it.id.startsWith("sosyal_") }
            baseSubjects.add(
                SubjectConfig("inkilap_8", "T.C. İnkılap Tarihi", "Atatürk ve Cumhuriyet", "🇹🇷", 0xFFE91E63, "inkilap_8")
            )
        }

        // İmam Hatip derslerini de (Seçmeli/Ek olarak) herkese ekle (Admin panelinde görünsün diye)
        // Kullanıcının isteği üzerine havuzları birleştiriyoruz.
        baseSubjects.addAll(listOf(
            SubjectConfig("arapca_$grade", "Arapça", "Temel Arapça", "🕌", 0xFF9575CD, "arapca_$grade",
                getArapcaOrtaokulUnits(grade)),
            SubjectConfig("kuran_$grade", "Kur'an-ı Kerim", "Kur'an Okuma", "📿", 0xFF7E57C2, "kuran_$grade"),
            SubjectConfig("siyer_$grade", "Peygamberimizin Hayatı", "Siyer", "📜", 0xFF5C6BC0, "siyer_$grade")
        ))

        return baseSubjects
    }

    // ==================== LİSE DERSLERİ (9-12. SINIF) ====================

    private fun getLiseSubjects(schoolType: SchoolType, grade: Int): List<SubjectConfig> {
        // Çekirdek zorunlu dersler (tüm lise türleri)
        val coreSubjects = mutableListOf(
            SubjectConfig("turk_dili_$grade", "Türk Dili ve Edebiyatı", "Edebiyat ve Dil Bilgisi", "📚", 0xFF64B5F6, "turk_dili_$grade"),
            SubjectConfig("paragraf_lise_$grade", "Paragraf", "Paragraf Soruları", "📖", 0xFF9C27B0, "paragraf_lise_$grade"),
            SubjectConfig("tarih_$grade", "Tarih", "Türk ve Dünya Tarihi", "🏛️", 0xFF4DB6AC, "tarih_$grade",
                getTarihLiseUnits(grade)),
            SubjectConfig("cografya_$grade", "Coğrafya", "Fiziki ve Beşeri Coğrafya", "🌍", 0xFF81C784, "cografya_$grade",
                getCografyaLiseUnits(grade)),
            SubjectConfig("ingilizce_lise_$grade", "İngilizce", "Advanced English", "🇬🇧", 0xFFBA68C8, "ingilizce_lise_$grade"),
            SubjectConfig("din_lise_$grade", "Din Kültürü", "Din ve Ahlak", "☪️", 0xFFA1887F, "din_lise_$grade",
                getDkabLiseUnits(grade))
        )

        // Matematik - tüm liselerde
        coreSubjects.add(SubjectConfig("matematik_lise_$grade", "Matematik", "Matematik", "📐", 0xFFFF8A65, "matematik_lise_$grade",
            getMatematikLiseUnits(grade)))

        // Fen dersleri
        coreSubjects.addAll(listOf(
            SubjectConfig("fizik_$grade", "Fizik", "Fizik", "⚡", 0xFFFFEB3B, "fizik_$grade",
                getFizikLiseUnits(grade)),
            SubjectConfig("kimya_$grade", "Kimya", "Kimya", "🧪", 0xFF00BCD4, "kimya_$grade",
                getKimyaLiseUnits(grade)),
            SubjectConfig("biyoloji_$grade", "Biyoloji", "Biyoloji", "🧬", 0xFF8BC34A, "biyoloji_$grade",
                getBiyolojiLiseUnits(grade)),
            SubjectConfig("felsefe_$grade", "Felsefe", "Felsefe", "🤔", 0xFF9C27B0, "felsefe_$grade",
                getFelsefeLiseUnits(grade))
        ))

        return coreSubjects
    }

    // ==================== KPSS DERSLERİ ====================

    private fun getKpssSubjects(schoolType: SchoolType): List<SubjectConfig> {
        val gyGkSubjects = listOf(
            SubjectConfig("turkce_kpss", "Türkçe", "Dil Bilgisi ve Anlam", "📖", 0xFF64B5F6, "turkce_kpss"),
            SubjectConfig("paragraf_kpss", "Paragraf", "Paragraf Soruları", "📚", 0xFF9C27B0, "paragraf_kpss"),
            SubjectConfig("matematik_kpss", "Matematik", "Temel Matematik", "🔢", 0xFFFF8A65, "matematik_kpss"),
            SubjectConfig("tarih_kpss", "Tarih", "Atatürk İlkeleri ve İnkılap Tarihi", "🏛️", 0xFF4DB6AC, "tarih_kpss"),
            SubjectConfig("cografya_kpss", "Coğrafya", "Türkiye Coğrafyası", "🌍", 0xFF81C784, "cografya_kpss"),
            SubjectConfig("vatandaslik_kpss", "Vatandaşlık", "Anayasa ve Temel Haklar", "🇹🇷", 0xFFE91E63, "vatandaslik_kpss"),
            SubjectConfig("guncel_kpss", "Güncel Bilgiler", "Gündem ve Aktüalite", "📰", 0xFF9C27B0, "guncel_kpss")
        )

        val denemeSubjects = listOf(
            SubjectConfig("kpss_gy_deneme", "GY Deneme", "Genel Yetenek Denemesi", "📝", 0xFFE91E63, "kpss_gy_deneme")
        )

        return gyGkSubjects + denemeSubjects
    }

    // ==================== ÜNİTE BAŞLIKLARI (TYMM 2025) ====================

    // Türkçe Üniteleri (Ortaokul)
    private fun getTurkceUnits(grade: Int): List<UnitConfig> = listOf(
        UnitConfig("okuma_anlama", "Okuma-Anlama"),
        UnitConfig("metin_turleri", "Metin Türleri"),
        UnitConfig("soz_varligi", "Söz Varlığı"),
        UnitConfig("yazim_noktalama", "Yazım-Noktalama"),
        UnitConfig("yazma", "Yazma"),
        UnitConfig("dinleme_konusma", "Dinleme-Konuşma")
    )

    // Matematik Üniteleri (Ortaokul - TYMM)
    private fun getMatematikOrtaokulUnits(grade: Int): List<UnitConfig> = when (grade) {
        5 -> listOf(
            UnitConfig("sayilar_nicelikler", "Sayılar ve Nicelikler"),
            UnitConfig("islemler_cebirsel", "İşlemlerle Cebirsel Düşünme"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("olcme", "Ölçme"),
            UnitConfig("veri_istatistik", "Veri ve İstatistik")
        )
        6 -> listOf(
            UnitConfig("sayilar_nicelikler_1", "Sayılar ve Nicelikler (1)"),
            UnitConfig("sayilar_nicelikler_2", "Sayılar ve Nicelikler (2)"),
            UnitConfig("islemler_cebirsel", "İşlemlerle Cebirsel Düşünme ve Değişimler"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("geometrik_nicelikler", "Geometrik Nicelikler"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci"),
            UnitConfig("veriden_olasiliga", "Veriden Olasılığa")
        )
        7 -> listOf(
            UnitConfig("sayilar_nicelikler_1", "Sayılar ve Nicelikler (1)"),
            UnitConfig("sayilar_nicelikler_2", "Sayılar ve Nicelikler (2)"),
            UnitConfig("islemler_cebirsel", "İşlemlerle Cebirsel Düşünme ve Değişimler"),
            UnitConfig("donusum", "Dönüşüm"),
            UnitConfig("geometrik_nicelikler_1", "Geometrik Nicelikler (1)"),
            UnitConfig("geometrik_nicelikler_2", "Geometrik Nicelikler (2)"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci"),
            UnitConfig("veriden_olasiliga", "Veriden Olasılığa")
        )
        8 -> listOf(
            UnitConfig("sayilar_nicelikler", "Sayılar ve Nicelikler"),
            UnitConfig("cebirsel_degisimler", "Cebirsel Düşünme ve Değişimler"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("geometrik_nicelikler", "Geometrik Nicelikler"),
            UnitConfig("donusum", "Dönüşüm"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci"),
            UnitConfig("veriden_olasiliga", "Veriden Olasılığa")
        )
        else -> emptyList()
    }

    // Fen Bilimleri Üniteleri (Ortaokul - TYMM)
    private fun getFenOrtaokulUnits(grade: Int): List<UnitConfig> = when (grade) {
        5 -> listOf(
            UnitConfig("fen_5_1", "Dünya ve Evren"),
            UnitConfig("fen_5_2", "Canlılar Dünyası"),
            UnitConfig("fen_5_3", "Fiziksel Olaylar"),
            UnitConfig("fen_5_4", "Madde ve Değişim")
        )
        6 -> listOf(
            UnitConfig("gunes_sistemi", "Güneş Sistemi ve Tutulmalar"),
            UnitConfig("kuvvet_hareket", "Kuvvetin Etkisinde Hareket"),
            UnitConfig("canli_sistemler", "Canlılarda Sistemler"),
            UnitConfig("isik_renkler", "Işığın Yansıması ve Renkler"),
            UnitConfig("madde_ozellikleri", "Maddenin Ayırt Edici Özellikleri"),
            UnitConfig("elektrik_direnc", "Elektriğin İletimi ve Direnç"),
            UnitConfig("surdurulebilir_yasam", "Sürdürülebilir Yaşam ve Etkileşim")
        )
        7 -> listOf(
            UnitConfig("uzay_cagi", "Uzay Çağı"),
            UnitConfig("kuvvet_enerji", "Kuvvet ve Enerjiyi Keşfedelim"),
            UnitConfig("vucut_sistemleri", "Vücudumuzdaki Sistemler"),
            UnitConfig("isik_kirilmasi", "Işığın Kırılması ve Mercekler"),
            UnitConfig("madde_dogasi", "Maddenin Doğasına Yolculuk"),
            UnitConfig("elektriklenme", "Elektriklenme"),
            UnitConfig("geri_donusum", "Sürdürülebilir Yaşam ve Geri Dönüşüm")
        )
        8 -> listOf(
            UnitConfig("mevsimler_iklim", "Mevsimler ve İklim"),
            UnitConfig("yasami_kolaylastiran", "Yaşamı Kolaylaştıran Kuvvet"),
            UnitConfig("yasamin_gizemi", "Yaşamın Gizemi"),
            UnitConfig("sesin_dunyasi", "Sesin Dünyası"),
            UnitConfig("periyodik_tablo", "Periyodik Tablo ve Maddenin Etkileşimi"),
            UnitConfig("elektrik_yolculugu", "Elektriğin Yolculuğu"),
            UnitConfig("madde_dongusu", "Sürdürülebilir Yaşam ve Madde Döngüleri")
        )
        else -> emptyList()
    }

    // Sosyal Bilgiler Üniteleri (Ortaokul - TYMM)
    private fun getSosyalOrtaokulUnits(grade: Int): List<UnitConfig> = listOf(
        UnitConfig("birlikte_yasamak", "Birlikte Yaşamak"),
        UnitConfig("evimiz_dunya", "Evimiz Dünya"),
        UnitConfig("ortak_mirasimiz", "Ortak Mirasımız"),
        UnitConfig("yasayan_demokrasi", "Yaşayan Demokrasimiz"),
        UnitConfig("ekonomi", "Hayatımızdaki Ekonomi"),
        UnitConfig("teknoloji_sosyal", "Teknoloji ve Sosyal Bilimler")
    )

    // İngilizce Üniteleri (Ortaokul)
    private fun getIngilizceOrtaokulUnits(grade: Int): List<UnitConfig> = listOf(
        UnitConfig("classroom_life", "Classroom Life"),
        UnitConfig("family_life", "Family Life"),
        UnitConfig("life_nature", "Life in Nature & Global Problems"),
        UnitConfig("neighbourhood", "Life in the Neighbourhood & City"),
        UnitConfig("universe_future", "Life in the Universe & Future"),
        UnitConfig("world_culture", "Life in the World & Culture"),
        UnitConfig("personal_life", "Personal Life"),
        UnitConfig("school_life", "School Life")
    )

    // Din Kültürü Üniteleri (Ortaokul - TYMM)
    private fun getDkabOrtaokulUnits(grade: Int): List<UnitConfig> = when (grade) {
        5 -> listOf(
            UnitConfig("dkab5_1", "Allah İnancı"),
            UnitConfig("dkab5_2", "İbadet"),
            UnitConfig("dkab5_3", "Ahlaki Davranışlar"),
            UnitConfig("dkab5_4", "Kur'an ve Özellikleri"),
            UnitConfig("dkab5_5", "Hz. Muhammed'i Tanıyalım")
        )
        6 -> listOf(
            UnitConfig("peygamber_inanc", "Peygamber ve İlahi Kitap İnancı"),
            UnitConfig("ramazan_oruc", "Ramazan ve Oruç"),
            UnitConfig("ahlaki_davranislar", "Ahlaki Davranışlar"),
            UnitConfig("hz_muhammed_oncesi", "Peygamberliğinden Önce Hz. Muhammed"),
            UnitConfig("kulturel_motifler", "Kültürümüzdeki Dinî Motifler")
        )
        7 -> listOf(
            UnitConfig("melek_ahiret", "Melek ve Ahiret İnancı"),
            UnitConfig("hac_umre_kurban", "Hac, Umre ve Kurban"),
            UnitConfig("islam_yorumlar", "İslam Düşüncesinde Yorumlar"),
            UnitConfig("hz_muhammed_peygamber", "Peygamber Olarak Hz. Muhammed"),
            UnitConfig("dunya_dinleri", "Yaşayan Dünya Dinleri")
        )
        8 -> listOf(
            UnitConfig("kader_inanc", "Kader İnancı"),
            UnitConfig("zekat_sadaka", "Zekât ve Sadaka"),
            UnitConfig("din_sosyal_hayat", "Din ve Sosyal Hayat"),
            UnitConfig("kuran_insan", "Kur'an ve İnsan"),
            UnitConfig("bilim_kultur_katki", "Müslümanların Bilim ve Kültüre Katkısı")
        )
        else -> emptyList()
    }

    // Arapça Üniteleri (İmam Hatip Ortaokul)
    private fun getArapcaOrtaokulUnits(grade: Int): List<UnitConfig> = when (grade) {
        5 -> emptyList()
        6 -> listOf(
            UnitConfig("ar6_1", "Akrabalarım"),
            UnitConfig("ar6_2", "Haydi Okula!"),
            UnitConfig("ar6_3", "Vücudum"),
            UnitConfig("ar6_4", "Bu Hafta Hava Nasıl?")
        )
        7 -> listOf(
            UnitConfig("ar7_1", "Bu Gün Ne Yaptım?"),
            UnitConfig("ar7_2", "Alışveriş Zamanı"),
            UnitConfig("ar7_3", "Nereye Seyahat Edelim?"),
            UnitConfig("ar7_4", "Şehrim ve Ülkem")
        )
        8 -> listOf(
            UnitConfig("ar8_1", "Güzel Bir Günüm"),
            UnitConfig("ar8_2", "Sağlıklı Hayatım"),
            UnitConfig("ar8_3", "İletişim Günlüğüm"),
            UnitConfig("ar8_4", "Mezun Oluyorum")
        )
        else -> emptyList()
    }

    // Matematik Üniteleri (Lise - TYMM)
    private fun getMatematikLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("sayilar", "Sayılar"),
            UnitConfig("nicelikler_degisimler", "Nicelikler ve Değişimler"),
            UnitConfig("sayma_algoritma", "Sayma, Algoritma ve Bilişim"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("analitik_inceleme", "Analitik İnceleme"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci"),
            UnitConfig("veriden_olasiliga", "Veriden Olasılığa")
        )
        10 -> listOf(
            UnitConfig("sayilar", "Sayılar"),
            UnitConfig("nicelikler_degisimler", "Nicelikler ve Değişimler"),
            UnitConfig("sayma_algoritma", "Sayma, Algoritma ve Bilişim"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("analitik_inceleme", "Analitik İnceleme"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci"),
            UnitConfig("veriden_olasiliga", "Veriden Olasılığa")
        )
        11 -> listOf(
            UnitConfig("nicelikler_1", "Nicelikler ve Değişimler (1)"),
            UnitConfig("nicelikler_2", "Nicelikler ve Değişimler (2)"),
            UnitConfig("nicelikler_3", "Nicelikler ve Değişimler (3)"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("istatistik_surec", "İstatistiksel Araştırma Süreci")
        )
        12 -> listOf(
            UnitConfig("nicelikler_1", "Nicelikler ve Değişimler (1)"),
            UnitConfig("nicelikler_2", "Nicelikler ve Değişimler (2)"),
            UnitConfig("degisim_mat_1", "Değişimin Matematiği (1)"),
            UnitConfig("degisim_mat_2", "Değişimin Matematiği (2)"),
            UnitConfig("degisim_mat_3", "Değişimin Matematiği (3)"),
            UnitConfig("geometrik_sekiller", "Geometrik Şekiller"),
            UnitConfig("geometrik_cisimler", "Geometrik Cisimler"),
            UnitConfig("hazir_veriler", "Hazır Veriler Üzerinde Çalışma")
        )
        else -> emptyList()
    }

    // Fizik Üniteleri (Lise - TYMM)
    private fun getFizikLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("fizik_kariyer", "Fizik Bilimi ve Kariyer Keşfi"),
            UnitConfig("kuvvet_hareket", "Kuvvet ve Hareket"),
            UnitConfig("akiskanlar", "Akışkanlar"),
            UnitConfig("enerji", "Enerji")
        )
        10 -> listOf(
            UnitConfig("kuvvet_hareket", "Kuvvet ve Hareket"),
            UnitConfig("enerji", "Enerji"),
            UnitConfig("elektrik", "Elektrik"),
            UnitConfig("dalgalar", "Dalgalar")
        )
        11 -> listOf(
            UnitConfig("kuvvet_hareket", "Kuvvet ve Hareket"),
            UnitConfig("elektrik_manyetizma", "Elektrik ve Manyetizma"),
            UnitConfig("madde_dogasi", "Madde ve Doğası"),
            UnitConfig("optik", "Optik")
        )
        12 -> listOf(
            UnitConfig("kuvvet_hareket", "Kuvvet ve Hareket"),
            UnitConfig("enerji", "Enerji"),
            UnitConfig("dalgalar", "Dalgalar"),
            UnitConfig("madde_dogasi", "Madde ve Doğası")
        )
        else -> emptyList()
    }

    // Kimya Üniteleri (Lise - TYMM)
    private fun getKimyaLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9, 10 -> listOf(
            UnitConfig("etkilesim", "Etkileşim"),
            UnitConfig("cesitlilik", "Çeşitlilik"),
            UnitConfig("surdurulebilirlik", "Sürdürülebilirlik")
        )
        11 -> listOf(
            UnitConfig("enerji", "Enerji"),
            UnitConfig("kimyasal_hiz", "Kimyasal Tepkimelerde Hız"),
            UnitConfig("denge", "Denge"),
            UnitConfig("asit_baz", "Asit-Baz Çözeltilerinde Denge"),
            UnitConfig("cozunurluk", "Çözünürlük Dengesi"),
            UnitConfig("nanoteknoloji", "Nanoteknoloji ve Sürdürülebilirlik")
        )
        12 -> listOf(
            UnitConfig("redoks", "İndirgenme-Yükseltgenme Tepkimeleri"),
            UnitConfig("elektrokimya", "Elektrokimyasal Hücreler"),
            UnitConfig("organik_giris", "Organik Kimyaya Giriş"),
            UnitConfig("organik_bilesikler", "Organik Bileşikler"),
            UnitConfig("nanobilim", "Nanobilim"),
            UnitConfig("yesil_kimya", "Yeşil Kimya"),
            UnitConfig("surdurulebilirlik", "Sürdürülebilirlik")
        )
        else -> emptyList()
    }

    // Biyoloji Üniteleri (Lise - TYMM)
    private fun getBiyolojiLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("yasam", "Yaşam"),
            UnitConfig("organizasyon", "Organizasyon")
        )
        10 -> listOf(
            UnitConfig("enerji", "Enerji"),
            UnitConfig("ekoloji", "Ekoloji")
        )
        11 -> listOf(
            UnitConfig("tepki", "Tepki"),
            UnitConfig("homeostazi", "Homeostazi")
        )
        12 -> listOf(
            UnitConfig("biyo12_1", "TYMM_BAZ_AL"),
            UnitConfig("biyo12_2", "TYMM_BAZ_AL")
        )
        else -> emptyList()
    }

    // Tarih Üniteleri (Lise - TYMM)
    private fun getTarihLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("gecmisin_insasi", "Geçmişin İnşa Sürecinde Tarih"),
            UnitConfig("eski_cag", "Eski Çağ Medeniyetleri"),
            UnitConfig("orta_cag", "Orta Çağ Medeniyetleri")
        )
        10 -> listOf(
            UnitConfig("turkistan_turkiye", "Türkistan'dan Türkiye'ye (1040–1299)"),
            UnitConfig("beylikten_devlete", "Beylikten Devlete Osmanlı (1299–1453)"),
            UnitConfig("cihan_devleti", "Cihan Devleti Osmanlı (1453–1683)")
        )
        else -> listOf(
            UnitConfig("tarih_tymm", "TYMM_BAZ_AL")
        )
    }

    // Coğrafya Üniteleri (Lise - TYMM)
    private fun getCografyaLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("cografya_dogasi", "Coğrafyanın Doğası"),
            UnitConfig("mekansal_bilgi", "Mekânsal Bilgi Teknolojileri"),
            UnitConfig("dogal_sistemler", "Doğal Sistemler ve Süreçler"),
            UnitConfig("beseri_sistemler", "Beşerî Sistemler ve Süreçler"),
            UnitConfig("ekonomik_faaliyetler", "Ekonomik Faaliyetler ve Etkileri"),
            UnitConfig("afetler_cevre", "Afetler ve Sürdürülebilir Çevre"),
            UnitConfig("bolgeler_ulkeler", "Bölgeler, Ülkeler ve Küresel Bağlantılar")
        )
        else -> listOf(
            UnitConfig("cografya_tymm", "TYMM_BAZ_AL")
        )
    }

    // Felsefe Üniteleri (Lise - TYMM)
    private fun getFelsefeLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        10 -> listOf(
            UnitConfig("felsefe_dogasi", "Felsefenin Doğası"),
            UnitConfig("mantik_argumantasyon", "Felsefe, Mantık ve Argümantasyon"),
            UnitConfig("varlik_felsefesi", "Varlık Felsefesi"),
            UnitConfig("bilgi_felsefesi", "Bilgi Felsefesi"),
            UnitConfig("ahlak_felsefesi", "Ahlak Felsefesi"),
            UnitConfig("estetik_sanat", "Estetik ve Sanat Felsefesi"),
            UnitConfig("siyaset_felsefesi", "Siyaset Felsefesi"),
            UnitConfig("din_felsefesi", "Din Felsefesi"),
            UnitConfig("bilim_felsefesi", "Bilim Felsefesi")
        )
        else -> listOf(
            UnitConfig("felsefe_tymm", "OKULA_GORE_DEGISIR")
        )
    }

    // Din Kültürü Üniteleri (Lise - TYMM)
    private fun getDkabLiseUnits(grade: Int): List<UnitConfig> = when (grade) {
        9 -> listOf(
            UnitConfig("allah_insan", "Allah-İnsan İlişkisi"),
            UnitConfig("inanc_esaslari", "İslam'da İnanç Esasları"),
            UnitConfig("ibadetler", "İslam'da İbadetler"),
            UnitConfig("ahlak_ilkeleri", "İslam'da Ahlak İlkeleri"),
            UnitConfig("hz_muhammed", "Kur'an'a Göre Hz. Muhammed")
        )
        10 -> listOf(
            UnitConfig("varlik_bilgi", "İslam'da Varlık ve Bilgi"),
            UnitConfig("allah_tanimak", "Allah'ı Tanımak"),
            UnitConfig("evrensel_mesajlar", "İslam'ın Evrensel Mesajları"),
            UnitConfig("din_cevre_teknoloji", "Din, Çevre ve Teknoloji"),
            UnitConfig("itikadi_yorumlar", "İslam Düşüncesinde İtikadi-Siyasi ve Fıkhi Yorumlar")
        )
        12 -> listOf(
            UnitConfig("kuran_kerim", "Kur'an-ı Kerim"),
            UnitConfig("din_aile", "Din ve Aile"),
            UnitConfig("guncel_meseleler", "Güncel Dinî Meseleler"),
            UnitConfig("tasavvufi_yorumlar", "İslam Düşüncesinde Tasavvufi Yorumlar"),
            UnitConfig("hint_cin_dinleri", "Hint ve Çin Dinleri")
        )
        else -> listOf(
            UnitConfig("dkab_tymm", "TYMM_BAZ_AL")
        )
    }

    // ==================== YARDIMCI FONKSİYONLAR ====================

    fun getSchoolTypesFor(level: EducationLevel): List<SchoolType> {
        return SchoolType.entries.filter { it.level == level }
    }

    fun getGradesFor(schoolType: SchoolType): List<Int> {
        return schoolType.grades
    }

    fun getSubjectById(subjectId: String): SubjectConfig? {
        for (schoolType in SchoolType.entries) {
            val grades = if (schoolType.grades.isEmpty()) listOf(0) else schoolType.grades
            for (grade in grades) {
                val subjects = getSubjectsFor(schoolType, grade)
                subjects.find { it.id == subjectId }?.let { return it }
            }
        }
        return null
    }

    // Ders için ünite başlıklarını döndürür
    fun getUnitsForSubject(subjectId: String): List<UnitConfig> {
        return getSubjectById(subjectId)?.units ?: emptyList()
    }
}

// ==================== KULLANICI TERCİHLERİ ====================

data class UserEducationPrefs(
    val level: EducationLevel,
    val schoolType: SchoolType,
    val grade: Int?
) {
    companion object {
        val DEFAULT = UserEducationPrefs(
            level = EducationLevel.ORTAOKUL,
            schoolType = SchoolType.ORTAOKUL_STANDARD,
            grade = 5
        )
    }
}
