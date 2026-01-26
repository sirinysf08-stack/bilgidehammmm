package com.example.bilgideham

import java.util.Locale

/**
 * MEB Müfredat Veritabanı
 *
 * Tüm sınıf seviyeleri için detaylı kazanım ve konu tanımları.
 * Halüsinasyon önleme için kesin sınırlar belirler.
 */
object MebCurriculumDatabase {

    data class Kazanim(
        val kod: String,
        val konu: String,
        val altKonu: String,
        val aciklama: String,
        val yasak: List<String> = emptyList(), // Bu konuda ASLA sorulmaması gerekenler
        val ornekSoruTipleri: List<String> = emptyList()
    )

    // ==================== 4. SINIF İLKOKUL ====================

    val ilkokul4Turkce = listOf(
        Kazanim("T.4.1.1", "Okuma", "Akıcı Okuma", "Noktalama işaretlerine dikkat ederek okur",
            yasak = listOf("Edebiyat akımları", "Şiir türleri analizi")),
        Kazanim("T.4.1.2", "Okuma", "Söz Varlığı", "Okuduğu metindeki bilmediği kelimelerin anlamını tahmin eder"),
        Kazanim("T.4.2.1", "Anlama", "Ana Fikir", "Metnin ana fikrini belirler"),
        Kazanim("T.4.2.2", "Anlama", "Yardımcı Fikir", "Metindeki yardımcı fikirleri belirler"),
        Kazanim("T.4.3.1", "Yazım", "Büyük Harf", "Büyük harflerin kullanıldığı yerleri bilir"),
        Kazanim("T.4.3.2", "Yazım", "Noktalama", "Temel noktalama işaretlerini doğru kullanır")
    )

    val ilkokul4Matematik = listOf(
        Kazanim("M.4.1.1", "Doğal Sayılar", "Basamak Değeri", "6 basamaklı doğal sayıları okur ve yazar",
            yasak = listOf("Negatif sayılar", "Üslü sayılar")),
        Kazanim("M.4.1.2", "Doğal Sayılar", "Karşılaştırma", "Doğal sayıları karşılaştırır ve sıralar"),
        Kazanim("M.4.1.3", "İşlemler", "Toplama-Çıkarma", "Doğal sayılarla toplama ve çıkarma yapar"),
        Kazanim("M.4.1.4", "İşlemler", "Çarpma-Bölme", "En çok 3 basamaklı sayılarla çarpma ve bölme yapar"),
        Kazanim("M.4.2.1", "Kesirler", "Kesir Kavramı", "Bir bütünün eş parçalarını kesir olarak ifade eder",
            yasak = listOf("Rasyonel sayılar", "Kesirlerle çarpma/bölme")),
        Kazanim("M.4.3.1", "Geometri", "Temel Şekiller", "Kare, dikdörtgen, üçgen özelliklerini bilir")
    )

    // ==================== 5. SINIF ORTAOKUL ====================

    val ortaokul5Turkce = listOf(
        Kazanim("T.5.1.1", "Sözcükte Anlam", "Gerçek-Mecaz", "Sözcüğün gerçek ve mecaz anlamını ayırt eder",
            ornekSoruTipleri = listOf("Cümlede altı çizili sözcüğün anlamı", "Mecaz anlam kullanılan cümle")),
        Kazanim("T.5.1.2", "Sözcükte Anlam", "Eş-Zıt Anlam", "Eş ve zıt anlamlı sözcükleri bulur"),
        Kazanim("T.5.1.3", "Sözcükte Anlam", "Deyim-Atasözü", "Deyim ve atasözlerini anlamına uygun kullanır",
            yasak = listOf("Argo ifadeler", "Bölgesel deyimler")),
        Kazanim("T.5.2.1", "Cümlede Anlam", "Neden-Sonuç", "Neden-sonuç ilişkisi kurar"),
        Kazanim("T.5.2.2", "Cümlede Anlam", "Amaç-Sonuç", "Amaç-sonuç ilişkisi kurar"),
        Kazanim("T.5.2.3", "Cümlede Anlam", "Öznel-Nesnel", "Öznel ve nesnel yargıları ayırt eder"),
        Kazanim("T.5.3.1", "Paragraf", "Ana Düşünce", "Paragrafın ana düşüncesini belirler"),
        Kazanim("T.5.3.2", "Paragraf", "Yardımcı Düşünce", "Yardımcı düşünceleri belirler"),
        Kazanim("T.5.3.3", "Paragraf", "Başlık", "Paragrafa uygun başlık belirler"),
        Kazanim("T.5.4.1", "Yazım", "Büyük Harf", "Büyük harflerin kullanıldığı yerleri bilir"),
        Kazanim("T.5.4.2", "Yazım", "de/da - ki", "de/da ve ki'nin yazımını bilir"),
        Kazanim("T.5.5.1", "Noktalama", "Temel İşaretler", "Nokta, virgül, soru işareti kullanır")
    )

    val ortaokul5Matematik = listOf(
        Kazanim("M.5.1.1", "Doğal Sayılar", "Basamak Değeri", "9 basamaklı doğal sayıları okur ve yazar",
            yasak = listOf("Negatif sayılar", "Rasyonel sayılar", "Üslü ifadeler")),
        Kazanim("M.5.1.2", "Doğal Sayılar", "Çözümleme", "Doğal sayıları çözümler"),
        Kazanim("M.5.1.3", "Doğal Sayılar", "Karşılaştırma", "Doğal sayıları karşılaştırır"),
        Kazanim("M.5.1.4", "İşlemler", "Dört İşlem", "Doğal sayılarla dört işlem yapar"),
        Kazanim("M.5.1.5", "İşlemler", "Problem", "Dört işlem gerektiren problemleri çözer",
            ornekSoruTipleri = listOf("Sözel problem", "Çok adımlı problem")),
        Kazanim("M.5.2.1", "Kesirler", "Kesir Gösterimi", "Kesirleri modelle gösterir"),
        Kazanim("M.5.2.2", "Kesirler", "Karşılaştırma", "Paydaları eşit kesirleri karşılaştırır"),
        Kazanim("M.5.2.3", "Kesirler", "Toplama-Çıkarma", "Paydaları eşit kesirlerle işlem yapar",
            yasak = listOf("Kesirlerle çarpma", "Kesirlerle bölme")),
        Kazanim("M.5.3.1", "Ondalık", "Okuma-Yazma", "Ondalık gösterimi okur ve yazar"),
        Kazanim("M.5.3.2", "Ondalık", "Karşılaştırma", "Ondalık gösterimleri karşılaştırır"),
        Kazanim("M.5.4.1", "Yüzde", "Yüzde Hesaplama", "Bir çokluğun belirli bir yüzdesini bulur"),
        Kazanim("M.5.5.1", "Geometri", "Üçgen", "Üçgenleri kenarlarına göre sınıflandırır"),
        Kazanim("M.5.5.2", "Geometri", "Çevre", "Kare ve dikdörtgenin çevresini hesaplar"),
        Kazanim("M.5.6.1", "Veri", "Tablo-Grafik", "Verileri tablo ve grafikle gösterir")
    )

    val ortaokul5Fen = listOf(
        Kazanim("F.5.1.1", "Canlılar", "Besin Zinciri", "Besin zinciri ve besin ağını açıklar",
            yasak = listOf("Hücre yapısı detayları", "DNA/RNA")),
        Kazanim("F.5.1.2", "Canlılar", "Çevre Sorunları", "İnsan faaliyetlerinin çevreye etkisini tartışır"),
        Kazanim("F.5.2.1", "Kuvvet", "Sürtünme", "Sürtünme kuvvetini açıklar"),
        Kazanim("F.5.2.2", "Kuvvet", "Hareket", "Kuvvetin cisimlerin hareketine etkisini açıklar"),
        Kazanim("F.5.3.1", "Madde", "Hal Değişimi", "Maddenin hal değişimlerini açıklar"),
        Kazanim("F.5.3.2", "Madde", "Isı-Sıcaklık", "Isı ve sıcaklık kavramlarını ayırt eder"),
        Kazanim("F.5.4.1", "Işık", "Işığın Yayılması", "Işığın doğrusal yayıldığını gösterir"),
        Kazanim("F.5.4.2", "Işık", "Gölge", "Gölge oluşumunu açıklar"),
        Kazanim("F.5.5.1", "Elektrik", "Basit Devre", "Basit elektrik devresi kurar"),
        Kazanim("F.5.6.1", "Dünya-Uzay", "Güneş Sistemi", "Güneş sistemindeki gezegenleri tanır")
    )

    val ortaokul5Sosyal = listOf(
        Kazanim("SB.5.1.1", "Birey-Toplum", "Hak-Sorumluluk", "Temel hak ve sorumluluklarını bilir",
            yasak = listOf("Siyasi partiler", "Seçim sistemleri")),
        Kazanim("SB.5.1.2", "Birey-Toplum", "Kurum Rolleri", "Aile, okul gibi kurumların rollerini açıklar"),
        Kazanim("SB.5.2.1", "Kültür-Miras", "Kültürel Ögeler", "Kültürel ögeleri tanır ve korur"),
        Kazanim("SB.5.2.2", "Kültür-Miras", "Tarihî Mekânlar", "Tarihî mekânların önemini kavrar"),
        Kazanim("SB.5.3.1", "Coğrafya", "Harita Okuma", "Harita ve krokiyi okur"),
        Kazanim("SB.5.3.2", "Coğrafya", "İklim-Yaşam", "İklimin günlük yaşama etkisini açıklar"),
        Kazanim("SB.5.4.1", "Ekonomi", "İhtiyaç-İstek", "İhtiyaç ve istekleri ayırt eder"),
        Kazanim("SB.5.4.2", "Ekonomi", "Bilinçli Tüketim", "Bilinçli tüketici davranışlarını sergiler"),
        Kazanim("SB.5.5.1", "Teknoloji", "Etik Kullanım", "Teknolojiyi etik kurallara uygun kullanır")
    )

    // ==================== 6. SINIF ORTAOKUL ====================

    val ortaokul6Turkce = listOf(
        Kazanim("T.6.1.1", "Sözcükte Anlam", "Çok Anlamlılık", "Sözcüğün farklı anlamlarını ayırt eder"),
        Kazanim("T.6.1.2", "Sözcükte Anlam", "Terim Anlam", "Terim anlamlı sözcükleri tanır"),
        Kazanim("T.6.2.1", "Cümle", "Cümle Türleri", "Cümle türlerini tanır"),
        Kazanim("T.6.2.2", "Cümle", "Anlatım Bozuklukları", "Basit anlatım bozukluklarını bulur"),
        Kazanim("T.6.3.1", "Paragraf", "Paragraf Yapısı", "Paragrafın yapısını çözümler"),
        Kazanim("T.6.4.1", "Dil Bilgisi", "Fiil", "Fiilleri tanır ve kullanır"),
        Kazanim("T.6.4.2", "Dil Bilgisi", "İsim", "İsimleri tanır ve sınıflandırır")
    )

    val ortaokul6Matematik = listOf(
        Kazanim("M.6.1.1", "Tam Sayılar", "Tam Sayı Kavramı", "Tam sayıları tanır ve sıralar",
            yasak = listOf("Rasyonel sayılar", "İrrasyonel sayılar")),
        Kazanim("M.6.1.2", "Tam Sayılar", "İşlemler", "Tam sayılarla toplama ve çıkarma yapar"),
        Kazanim("M.6.2.1", "Kesirler", "Çarpma-Bölme", "Kesirlerle çarpma ve bölme yapar"),
        Kazanim("M.6.3.1", "Oran-Orantı", "Oran", "İki çokluğun oranını belirler"),
        Kazanim("M.6.4.1", "Cebir", "Cebirsel İfade", "Basit cebirsel ifadeleri yazar"),
        Kazanim("M.6.5.1", "Geometri", "Alan", "Kare ve dikdörtgenin alanını hesaplar"),
        Kazanim("M.6.6.1", "Veri", "Ortalama", "Aritmetik ortalamayı hesaplar")
    )

    // ==================== 7. SINIF ORTAOKUL ====================

    val ortaokul7Turkce = listOf(
        Kazanim("T.7.1.1", "Sözcük", "Sözcük Türleri", "Sözcük türlerini ayırt eder"),
        Kazanim("T.7.2.1", "Cümle", "Cümle Ögeleri", "Cümlenin ögelerini bulur"),
        Kazanim("T.7.2.2", "Cümle", "Fiilimsi", "Fiilimsileri tanır"),
        Kazanim("T.7.3.1", "Paragraf", "Düşünceyi Geliştirme", "Düşünceyi geliştirme yollarını tanır"),
        Kazanim("T.7.4.1", "Yazım", "Birleşik Sözcükler", "Birleşik sözcüklerin yazımını bilir")
    )

    val ortaokul7Matematik = listOf(
        Kazanim("M.7.1.1", "Rasyonel Sayılar", "Rasyonel Sayı", "Rasyonel sayıları tanır"),
        Kazanim("M.7.1.2", "Rasyonel Sayılar", "İşlemler", "Rasyonel sayılarla işlem yapar"),
        Kazanim("M.7.2.1", "Cebir", "Denklem", "Birinci dereceden denklemleri çözer"),
        Kazanim("M.7.3.1", "Oran-Orantı", "Doğru Orantı", "Doğru orantıyı kullanır"),
        Kazanim("M.7.3.2", "Oran-Orantı", "Ters Orantı", "Ters orantıyı kullanır"),
        Kazanim("M.7.4.1", "Geometri", "Çember", "Çemberin özelliklerini bilir"),
        Kazanim("M.7.5.1", "Veri", "Olasılık", "Basit olasılık hesaplar")
    )

    // ==================== 8. SINIF ORTAOKUL (LGS) ====================

    val ortaokul8Turkce = listOf(
        Kazanim("T.8.1.1", "Sözcük", "Anlam İlişkileri", "Sözcükler arası anlam ilişkilerini çözümler"),
        Kazanim("T.8.2.1", "Cümle", "Cümle Vurgusu", "Cümle vurgusunu belirler"),
        Kazanim("T.8.2.2", "Cümle", "Anlatım Biçimleri", "Anlatım biçimlerini tanır"),
        Kazanim("T.8.3.1", "Paragraf", "Paragraf Türleri", "Paragraf türlerini ayırt eder"),
        Kazanim("T.8.3.2", "Paragraf", "Çıkarım", "Metinden çıkarım yapar"),
        Kazanim("T.8.4.1", "Dil Bilgisi", "Cümle Türleri", "Yapısına göre cümle türlerini bilir"),
        Kazanim("T.8.4.2", "Dil Bilgisi", "Fiil Çatısı", "Fiil çatısını belirler")
    )

    val ortaokul8Matematik = listOf(
        Kazanim("M.8.1.1", "Üslü İfadeler", "Üslü Sayılar", "Üslü ifadelerle işlem yapar"),
        Kazanim("M.8.1.2", "Kareköklü İfadeler", "Karekök", "Kareköklü ifadelerle işlem yapar"),
        Kazanim("M.8.2.1", "Cebir", "Özdeşlikler", "Özdeşlikleri kullanır"),
        Kazanim("M.8.2.2", "Cebir", "Çarpanlara Ayırma", "İfadeleri çarpanlarına ayırır"),
        Kazanim("M.8.3.1", "Denklemler", "Doğrusal Denklem", "Doğrusal denklemleri çözer"),
        Kazanim("M.8.3.2", "Eşitsizlikler", "Eşitsizlik", "Birinci dereceden eşitsizlikleri çözer"),
        Kazanim("M.8.4.1", "Geometri", "Üçgenler", "Üçgenlerde açı-kenar ilişkilerini bilir"),
        Kazanim("M.8.4.2", "Geometri", "Eşlik-Benzerlik", "Eşlik ve benzerlik kavramlarını kullanır"),
        Kazanim("M.8.5.1", "Veri", "Olasılık", "Bağımlı-bağımsız olayların olasılığını hesaplar")
    )

    // ==================== LİSE 9. SINIF ====================

    val lise9Matematik = listOf(
        Kazanim("M.9.1.1", "Kümeler", "Küme Kavramı", "Kümeleri tanır ve işlem yapar"),
        Kazanim("M.9.1.2", "Kümeler", "Alt Küme", "Alt küme kavramını kullanır"),
        Kazanim("M.9.2.1", "Denklemler", "İkinci Derece", "İkinci dereceden denklemleri çözer"),
        Kazanim("M.9.3.1", "Fonksiyonlar", "Fonksiyon Kavramı", "Fonksiyon kavramını açıklar"),
        Kazanim("M.9.4.1", "Geometri", "Doğruda Açılar", "Doğruda açı ilişkilerini bilir"),
        Kazanim("M.9.4.2", "Geometri", "Üçgenler", "Üçgenlerin özelliklerini kullanır")
    )

    // ==================== KPSS ====================

    val kpssTurkce = listOf(
        Kazanim("KPSS.T.1", "Sözcük", "Anlam Bilgisi", "Sözcükte anlam konularını çözer"),
        Kazanim("KPSS.T.2", "Cümle", "Cümle Bilgisi", "Cümle yapısı ve anlamı konularını çözer"),
        Kazanim("KPSS.T.3", "Paragraf", "Paragraf Bilgisi", "Paragraf sorularını çözer"),
        Kazanim("KPSS.T.4", "Dil Bilgisi", "Yazım-Noktalama", "Yazım ve noktalama kurallarını uygular")
    )

    val kpssMatematik = listOf(
        Kazanim("KPSS.M.1", "Sayılar", "Temel Kavramlar", "Sayı sistemlerini kullanır"),
        Kazanim("KPSS.M.2", "Problem", "Sözel Problemler", "Sözel problemleri çözer"),
        Kazanim("KPSS.M.3", "Geometri", "Temel Geometri", "Temel geometri sorularını çözer")
    )

    // ==================== KAZANIM SORGULAMA ====================

    fun getKazanimlar(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        lesson: String
    ): List<Kazanim> {
        val l = lesson.lowercase(Locale("tr", "TR"))

        return when (level) {
            EducationLevel.ILKOKUL -> when {
                l.contains("türk") || l.contains("turk") -> ilkokul4Turkce
                l.contains("mat") -> ilkokul4Matematik
                else -> emptyList()
            }

            EducationLevel.ORTAOKUL -> when (grade) {
                5 -> when {
                    l.contains("türk") || l.contains("turk") || l.contains("paragraf") -> ortaokul5Turkce
                    l.contains("mat") -> ortaokul5Matematik
                    l.contains("fen") -> ortaokul5Fen
                    l.contains("sosyal") -> ortaokul5Sosyal
                    else -> emptyList()
                }
                6 -> when {
                    l.contains("türk") || l.contains("turk") -> ortaokul6Turkce
                    l.contains("mat") -> ortaokul6Matematik
                    else -> emptyList()
                }
                7 -> when {
                    l.contains("türk") || l.contains("turk") -> ortaokul7Turkce
                    l.contains("mat") -> ortaokul7Matematik
                    else -> emptyList()
                }
                8 -> when {
                    l.contains("türk") || l.contains("turk") || l.contains("paragraf") -> ortaokul8Turkce
                    l.contains("mat") -> ortaokul8Matematik
                    else -> emptyList()
                }
                else -> emptyList()
            }

            EducationLevel.LISE -> when {
                l.contains("mat") && grade == 9 -> lise9Matematik
                else -> emptyList()
            }

            EducationLevel.KPSS -> when {
                l.contains("türk") || l.contains("turk") -> kpssTurkce
                l.contains("mat") -> kpssMatematik
                else -> emptyList()
            }

            EducationLevel.AGS -> when {
                l.contains("türk") || l.contains("turk") -> kpssTurkce
                l.contains("mat") -> kpssMatematik
                l.contains("mevzuat") || l.contains("anayasa") -> kpssTurkce
                else -> emptyList()
            }
        }
    }

    /**
     * Rastgele bir kazanım seçer (tekrar önleme ile)
     */
    private val usedKazanimlar = mutableSetOf<String>()

    fun pickRandomKazanim(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        lesson: String
    ): Kazanim? {
        val kazanimlar = getKazanimlar(level, schoolType, grade, lesson)
        if (kazanimlar.isEmpty()) return null

        // Kullanılmamış kazanımları bul
        val unused = kazanimlar.filter { it.kod !in usedKazanimlar }

        // Hepsi kullanıldıysa sıfırla
        if (unused.isEmpty()) {
            usedKazanimlar.clear()
            return kazanimlar.random()
        }

        val selected = unused.random()
        usedKazanimlar.add(selected.kod)

        // Çok fazla birikirse temizle
        if (usedKazanimlar.size > 100) {
            usedKazanimlar.clear()
        }

        return selected
    }
}
