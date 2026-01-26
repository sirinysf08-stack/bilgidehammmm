package com.example.bilgideham

/**
 * 4. Sınıf (İlkokul) MEB 2025-2026 Müfredatı
 * Türkiye Yüzyılı Maarif Modeli (TYMM) uyumlu kazanım veritabanı
 * Türkçe, Matematik, Fen Bilimleri, Sosyal Bilgiler, İngilizce, Din Kültürü
 */
object Ilkokul4Kazanimlari {

    // ==================== MATEMATİK (MEB 2025-2026) ====================
    val matematik = listOf(
        // ÜNİTE 1: DOĞAL SAYILAR (Bölük 1)
        RagKazanim("M.4.1.1", "Matematik", "Doğal Sayılar", "Çok Basamaklı Sayılar",
            "4, 5 ve 6 basamaklı doğal sayıları okur ve yazar.",
            listOf("yüzer sayma", "biner sayma", "6 basamaklı", "basamak değeri")),
        RagKazanim("M.4.1.2", "Matematik", "Doğal Sayılar", "Bölük ve Basamak Değeri",
            "Doğal sayılarda bölük, basamak değeri ve çözümleme yapar.",
            listOf("birler bölüğü", "binler bölüğü", "çözümleme", "sayı değeri")),
        RagKazanim("M.4.1.3", "Matematik", "Doğal Sayılar", "Yuvarlama",
            "Doğal sayıları en yakın onluğa ve yüzlüğe yuvarlar.",
            listOf("yuvarlama", "onluğa yuvarlama", "yüzlüğe yuvarlama", "tahmin")),
        RagKazanim("M.4.1.4", "Matematik", "Doğal Sayılar", "Sayı Örüntüleri",
            "Sayı örüntülerindeki kuralı bulur ve uygular.",
            listOf("örüntü", "kural", "genelleme", "dizi")),
        // ÜNİTE 2: TOPLAMA VE ÇIKARMA
        RagKazanim("M.4.2.1", "Matematik", "Toplama İşlemi", "Toplama ve Tahmin",
            "Toplamı tahmin eder ve zihinden toplama işlemi yapar.",
            listOf("toplama", "tahmin", "zihinden toplama", "problem")),
        RagKazanim("M.4.2.2", "Matematik", "Çıkarma İşlemi", "Çıkarma ve Tahmin",
            "Çıkarma işleminin sonucunu tahmin eder.",
            listOf("çıkarma", "tahmin", "zihinden çıkarma", "fark")),
        // ÜNİTE 3: ÇARPMA VE BÖLME
        RagKazanim("M.4.3.1", "Matematik", "Çarpma İşlemi", "Çarpma ve Çarpan",
            "Çarpma işleminde çarpan sırasının değişmesini anlar.",
            listOf("çarpma", "çarpan", "değişme özelliği", "zihinden çarpma")),
        RagKazanim("M.4.3.2", "Matematik", "Bölme İşlemi", "Dört Basamaklı Bölme",
            "Dört basamaklı doğal sayılarla bölme işlemi yapar.",
            listOf("bölme", "bölünen", "bölen", "kalan", "zihinden bölme")),
        // ÜNİTE 4: KESİRLER VE ZAMAN
        RagKazanim("M.4.4.1", "Matematik", "Kesirler", "Kesir Kavramı",
            "Bir çokluğun belirtilen basit kesir kadarını bulur.",
            listOf("kesir", "pay", "payda", "basit kesir")),
        RagKazanim("M.4.4.2", "Matematik", "Kesirler", "Kesir Karşılaştırma",
            "Kesirleri karşılaştırır ve sıralar.",
            listOf("kesir karşılaştırma", "büyüklük", "sıralama")),
        RagKazanim("M.4.4.3", "Matematik", "Zaman Ölçme", "Zaman Birimleri",
            "Zaman ölçme birimleri arasındaki ilişkileri açıklar.",
            listOf("saat", "dakika", "saniye", "gün", "hafta", "ay", "yıl")),
        // ÜNİTE 5: GEOMETRİ
        RagKazanim("M.4.5.1", "Matematik", "Geometri", "Üçgen ve Dörtgenler",
            "Üçgen, kare ve dikdörtgeni isimlendirir ve kenar özelliklerini açıklar.",
            listOf("üçgen", "kare", "dikdörtgen", "kenar")),
        RagKazanim("M.4.5.2", "Matematik", "Geometri", "Açılar",
            "Açıları isimlendirir ve standart açı ölçme birimlerini kullanır.",
            listOf("açı", "derece", "dar açı", "geniş açı", "dik açı")),
        RagKazanim("M.4.5.3", "Matematik", "Geometri", "Küp ve Yapılar",
            "Açınımı verilen küpü oluşturur ve eş küplerle yapılar kurar.",
            listOf("küp", "açınım", "geometrik cisim", "yapı")),
        // ÜNİTE 6: SIVI ÖLÇME
        RagKazanim("M.4.6.1", "Matematik", "Sıvı Ölçme", "Litre ve Mililitre",
            "Litre ve mililitre birimleri ile sıvı miktarını ölçer.",
            listOf("litre", "mililitre", "sıvı ölçme", "tahmin")),
        // VERİ
        RagKazanim("M.4.7.1", "Matematik", "Veri", "Tablo ve Grafik",
            "Verileri tablo ve grafikle gösterir, yorumlar.",
            listOf("veri", "tablo", "grafik", "sütun grafik"))
    )

    // ==================== FEN BİLİMLERİ ====================
    val fenBilimleri = listOf(
        RagKazanim(
            kod = "F.4.1.1",
            ders = "Fen Bilimleri",
            unite = "Yer Kabuğu ve Dünya'mızın Hareketleri",
            konu = "Yer Kabuğu",
            aciklama = "Yer kabuğunu oluşturan kayaç ve mineralleri tanır.",
            keywords = listOf("yer kabuğu", "kayaç", "mineral", "taş çeşitleri")
        ),
        RagKazanim(
            kod = "F.4.1.2",
            ders = "Fen Bilimleri",
            unite = "Yer Kabuğu ve Dünya'mızın Hareketleri",
            konu = "Dünya'nın Hareketleri",
            aciklama = "Dünya'nın kendi ekseni etrafında ve Güneş etrafında hareketlerini açıklar.",
            keywords = listOf("dünya", "eksen", "güneş", "gece gündüz", "mevsimler")
        ),
        RagKazanim(
            kod = "F.4.2.1",
            ders = "Fen Bilimleri",
            unite = "Besinlerimiz",
            konu = "Besin Grupları",
            aciklama = "Besinleri gruplandırır ve dengeli beslenmenin önemini açıklar.",
            keywords = listOf("besin", "protein", "karbonhidrat", "vitamin", "dengeli beslenme")
        ),
        RagKazanim(
            kod = "F.4.3.1",
            ders = "Fen Bilimleri",
            unite = "Kuvvetin Etkileri",
            konu = "Kuvvet ve Hareket",
            aciklama = "Kuvvetin cisimlerin hareket ve şekline etkisini açıklar.",
            keywords = listOf("kuvvet", "hareket", "itme", "çekme", "sürtünme")
        ),
        RagKazanim(
            kod = "F.4.4.1",
            ders = "Fen Bilimleri",
            unite = "Maddenin Özellikleri",
            konu = "Madde ve Halleri",
            aciklama = "Maddenin katı, sıvı ve gaz hallerini ayırt eder.",
            keywords = listOf("madde", "katı", "sıvı", "gaz", "hal değişimi")
        ),
        RagKazanim(
            kod = "F.4.5.1",
            ders = "Fen Bilimleri",
            unite = "Aydınlatma ve Ses Teknolojileri",
            konu = "Işık ve Ses",
            aciklama = "Işık ve ses kaynaklarını tanır, özelliklerini açıklar.",
            keywords = listOf("ışık", "ses", "yansıma", "titreşim", "ışık kaynağı")
        ),
        RagKazanim(
            kod = "F.4.6.1",
            ders = "Fen Bilimleri",
            unite = "İnsan ve Çevre",
            konu = "Çevre Bilinci",
            aciklama = "Çevre sorunlarını ve korunma yollarını açıklar.",
            keywords = listOf("çevre", "kirlilik", "geri dönüşüm", "doğa koruma")
        ),
        RagKazanim(
            kod = "F.4.7.1",
            ders = "Fen Bilimleri",
            unite = "Basit Elektrik Devreleri",
            konu = "Elektrik Devresi",
            aciklama = "Basit elektrik devreleri kurar ve açıklar.",
            keywords = listOf("elektrik", "devre", "pil", "ampul", "anahtar", "iletken")
        )
    )

    // ==================== SOSYAL BİLGİLER ====================
    val sosyalBilgiler = listOf(
        RagKazanim(
            kod = "SB.4.1.1",
            ders = "Sosyal Bilgiler",
            unite = "Birey ve Toplum",
            konu = "Kimlik ve Aidiyet",
            aciklama = "Bireysel ve toplumsal kimlik özelliklerini ayırt eder.",
            keywords = listOf("kimlik", "aile", "toplum", "empati", "farklılık")
        ),
        RagKazanim(
            kod = "SB.4.2.1",
            ders = "Sosyal Bilgiler",
            unite = "Kültür ve Miras",
            konu = "Kültürel Değerler",
            aciklama = "Millî kültür ögelerini ve gelenekleri tanır.",
            keywords = listOf("kültür", "gelenek", "bayram", "millî mücadele", "tarih")
        ),
        RagKazanim(
            kod = "SB.4.3.1",
            ders = "Sosyal Bilgiler",
            unite = "İnsanlar, Yerler ve Çevreler",
            konu = "Coğrafi Farkındalık",
            aciklama = "Çevresindeki doğal ve beşeri unsurları tanır.",
            keywords = listOf("harita", "yön", "coğrafya", "afet", "doğa")
        ),
        RagKazanim(
            kod = "SB.4.4.1",
            ders = "Sosyal Bilgiler",
            unite = "Bilim, Teknoloji ve Toplum",
            konu = "Teknoloji Kullanımı",
            aciklama = "Teknolojiyi güvenli ve bilinçli kullanır.",
            keywords = listOf("teknoloji", "internet", "güvenlik", "değişim")
        ),
        RagKazanim(
            kod = "SB.4.5.1",
            ders = "Sosyal Bilgiler",
            unite = "Üretim, Dağıtım ve Tüketim",
            konu = "Ekonomi ve Tüketim",
            aciklama = "İhtiyaç ve istek ayrımını yapar, bilinçli tüketir.",
            keywords = listOf("ihtiyaç", "istek", "tüketim", "tasarruf", "kaynak")
        ),
        RagKazanim(
            kod = "SB.4.6.1",
            ders = "Sosyal Bilgiler",
            unite = "Etkin Vatandaşlık",
            konu = "Hak ve Sorumluluklar",
            aciklama = "Hak ve sorumluluklarını bilir, kurallara uyar.",
            keywords = listOf("hak", "sorumluluk", "kural", "vatandaşlık", "katılım")
        ),
        RagKazanim(
            kod = "SB.4.7.1",
            ders = "Sosyal Bilgiler",
            unite = "Küresel Bağlantılar",
            konu = "Farklı Kültürler",
            aciklama = "Farklı kültürleri tanır ve saygı gösterir.",
            keywords = listOf("kültür", "iletişim", "etkileşim", "farklılık")
        )
    )

    // ==================== TÜRKÇE ====================
    val turkce = listOf(
        RagKazanim(
            kod = "T.4.1.1",
            ders = "Türkçe",
            unite = "Okuma-Anlama",
            konu = "Ana Fikir ve Yardımcı Fikir",
            aciklama = "Okuduğu metnin ana fikrini ve yardımcı fikirlerini belirler.",
            keywords = listOf("ana fikir", "yardımcı fikir", "paragraf", "anlama")
        ),
        RagKazanim(
            kod = "T.4.1.2",
            ders = "Türkçe",
            unite = "Okuma-Anlama",
            konu = "Paragraf Yapısı",
            aciklama = "Paragrafın yapısını (giriş-gelişme-sonuç) analiz eder.",
            keywords = listOf("paragraf", "giriş", "gelişme", "sonuç", "yapı")
        ),
        RagKazanim(
            kod = "T.4.1.3",
            ders = "Türkçe",
            unite = "Okuma-Anlama",
            konu = "Çıkarım Yapma",
            aciklama = "Metinden çıkarımlar yapar, neden-sonuç ilişkisi kurar.",
            keywords = listOf("çıkarım", "neden", "sonuç", "yorumlama", "akıl yürütme")
        ),
        RagKazanim(
            kod = "T.4.1.4",
            ders = "Türkçe",
            unite = "Okuma-Anlama",
            konu = "Metin Türleri",
            aciklama = "Hikâyeleyici, bilgilendirici ve şiir türlerini ayırt eder.",
            keywords = listOf("metin türü", "hikâye", "bilgilendirici", "şiir")
        ),
        RagKazanim(
            kod = "T.4.2.1",
            ders = "Türkçe",
            unite = "Sözcük Bilgisi",
            konu = "Gerçek ve Mecaz Anlam",
            aciklama = "Sözcüklerin gerçek ve mecaz anlamlarını ayırt eder.",
            keywords = listOf("gerçek anlam", "mecaz anlam", "sözcük", "deyim")
        ),
        RagKazanim(
            kod = "T.4.2.2",
            ders = "Türkçe",
            unite = "Sözcük Bilgisi",
            konu = "Eş-Zıt-Eş Sesli",
            aciklama = "Eş anlamlı, zıt anlamlı ve eş sesli sözcükleri tanır.",
            keywords = listOf("eş anlamlı", "zıt anlamlı", "eş sesli", "sözcük")
        ),
        RagKazanim(
            kod = "T.4.2.3",
            ders = "Türkçe",
            unite = "Sözcük Bilgisi",
            konu = "Deyim ve Atasözü",
            aciklama = "Deyim ve atasözlerinin anlamlarını kavrar.",
            keywords = listOf("deyim", "atasözü", "söz varlığı", "anlam")
        ),
        RagKazanim(
            kod = "T.4.3.1",
            ders = "Türkçe",
            unite = "Yazma",
            konu = "Paragraf Yazma",
            aciklama = "Kurallara uygun paragraf yazar (giriş-gelişme-sonuç).",
            keywords = listOf("yazma", "paragraf", "planlama", "bütünlük")
        ),
        RagKazanim(
            kod = "T.4.4.1",
            ders = "Türkçe",
            unite = "İmla ve Noktalama",
            konu = "Yazım Kuralları",
            aciklama = "Büyük harf ve yazım kurallarını uygular.",
            keywords = listOf("büyük harf", "yazım", "imla", "doğru yazma")
        ),
        RagKazanim(
            kod = "T.4.4.2",
            ders = "Türkçe",
            unite = "İmla ve Noktalama",
            konu = "Noktalama İşaretleri",
            aciklama = "Noktalama işaretlerini yerinde kullanır.",
            keywords = listOf("nokta", "virgül", "soru işareti", "ünlem", "noktalama")
        )
    )

    // ==================== İNGİLİZCE ====================
    val ingilizce = listOf(
        RagKazanim(
            kod = "E.4.1.1",
            ders = "İngilizce",
            unite = "Greetings",
            konu = "Selamlaşma",
            aciklama = "Selamlaşma ve vedalaşma ifadelerini kullanır.",
            keywords = listOf("hello", "goodbye", "greeting", "selamlaşma")
        ),
        RagKazanim(
            kod = "E.4.2.1",
            ders = "İngilizce",
            unite = "Numbers and Colors",
            konu = "Sayılar ve Renkler",
            aciklama = "1-100 arası sayıları ve renkleri tanır.",
            keywords = listOf("numbers", "colors", "sayılar", "renkler")
        ),
        RagKazanim(
            kod = "E.4.3.1",
            ders = "İngilizce",
            unite = "Daily Routines",
            konu = "Günlük Rutin",
            aciklama = "Günlük rutinleri anlatır ve sorar.",
            keywords = listOf("daily routine", "wake up", "go to school", "günlük aktivite")
        ),
        RagKazanim(
            kod = "E.4.4.1",
            ders = "İngilizce",
            unite = "My Family",
            konu = "Aile",
            aciklama = "Aile bireylerini tanıtır.",
            keywords = listOf("family", "mother", "father", "aile", "akraba")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    val dinKulturu = listOf(
        RagKazanim(
            kod = "DK.4.1.1",
            ders = "Din Kültürü",
            unite = "Allah İnancı",
            konu = "Allah'ın Varlığı",
            aciklama = "Allah'ın varlığını ve birliğini kavrar.",
            keywords = listOf("Allah", "inanç", "yaratıcı", "tek")
        ),
        RagKazanim(
            kod = "DK.4.2.1",
            ders = "Din Kültürü",
            unite = "Peygamberimizin Hayatı",
            konu = "Peygamberimizin Hayatı",
            aciklama = "Peygamberimizin hayatından kesitler öğrenir.",
            keywords = listOf("Hz. Muhammed", "peygamber", "yaşam", "örnek")
        ),
        RagKazanim(
            kod = "DK.4.3.1",
            ders = "Din Kültürü",
            unite = "Değerler ve Ahlak",
            konu = "Temel Değerler",
            aciklama = "Dürüstlük, güvenilirlik gibi temel değerleri benimser.",
            keywords = listOf("değer", "ahlak", "dürüstlük", "güvenilirlik")
        )
    )

    // ==================== HAYAT BİLGİSİ ====================
    val hayatBilgisi = listOf(
        RagKazanim(
            kod = "HB.4.1.1",
            ders = "Hayat Bilgisi",
            unite = "Birey ve Toplum",
            konu = "Kendini Tanıma",
            aciklama = "Kendi özelliklerini ve yeteneklerini tanır.",
            keywords = listOf("kendini tanıma", "yetenek", "özellik", "kişilik")
        ),
        RagKazanim(
            kod = "HB.4.2.1",
            ders = "Hayat Bilgisi",
            unite = "Sağlık ve Güvenlik",
            konu = "Sağlıklı Yaşam",
            aciklama = "Sağlıklı yaşam alışkanlıklarını benimser.",
            keywords = listOf("sağlık", "beslenme", "temizlik", "egzersiz")
        ),
        RagKazanim(
            kod = "HB.4.3.1",
            ders = "Hayat Bilgisi",
            unite = "Doğa ve Çevre",
            konu = "Çevre Koruma",
            aciklama = "Çevresini koruma bilinciyle davranır.",
            keywords = listOf("çevre", "doğa", "koruma", "temizlik")
        )
    )

    /**
     * Tüm 4. sınıf kazanımlarını döndür
     */
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fenBilimleri + sosyalBilgiler + turkce + 
               ingilizce + dinKulturu + hayatBilgisi
    }

    /**
     * Ders adına göre kazanımları getir
     */
    fun getByDers(ders: String): List<RagKazanim> {
        return tumKazanimlar().filter { 
            it.ders.equals(ders, ignoreCase = true) ||
            it.ders.contains(ders, ignoreCase = true) ||
            ders.contains(it.ders, ignoreCase = true)
        }
    }
}
