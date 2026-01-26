package com.example.bilgideham

/**
 * 11. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 * Lise 11. Sınıf - Genel Lise Müfredatı
 */
object Lise11Kazanimlari {

    // ==================== MATEMATİK ====================
    
    val matematik = listOf(
        RagKazanim(
            kod = "M.11.1.1",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Trigonometrik Fonksiyonlar",
            aciklama = "Trigonometrik fonksiyonları ve özelliklerini bilir.",
            ornekler = listOf("sin x, cos x, tan x grafikleri", "Periyot", "Genlik"),
            keywords = listOf("trigonometri", "fonksiyon", "periyot", "grafik")
        ),
        RagKazanim(
            kod = "M.11.1.2",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Logaritma",
            aciklama = "Logaritma kavramını ve özelliklerini kullanır.",
            ornekler = listOf("log_a(b)", "Logaritma özellikleri", "Üstel denklemler"),
            keywords = listOf("logaritma", "üs", "denklem", "özellik")
        ),
        RagKazanim(
            kod = "M.11.2.1",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Diziler",
            aciklama = "Aritmetik ve geometrik dizileri inceler.",
            ornekler = listOf("Aritmetik dizi", "Geometrik dizi", "Genel terim"),
            keywords = listOf("dizi", "aritmetik", "geometrik", "terim")
        ),
        RagKazanim(
            kod = "M.11.3.1",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Çember ve Daire",
            aciklama = "Çember ve daire ile ilgili teoremleri bilir.",
            ornekler = listOf("Teğet", "Kiriş", "Merkez açı", "Çevre açı"),
            keywords = listOf("çember", "daire", "teğet", "kiriş", "açı")
        ),
        RagKazanim(
            kod = "M.11.4.1",
            ders = "Matematik",
            unite = "İstatistiksel Araştırma",
            konu = "Merkezi Eğilim Ölçüleri",
            aciklama = "Ortalama, ortanca ve tepe değeri hesaplar.",
            ornekler = listOf("Aritmetik ortalama", "Medyan", "Mod"),
            keywords = listOf("ortalama", "medyan", "mod", "istatistik")
        )
    )

    // ==================== FİZİK ====================
    
    val fizik = listOf(
        RagKazanim(
            kod = "F.11.1.1",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Çembersel Hareket",
            aciklama = "Düzgün çembersel hareketi ve merkezcil kuvveti anlar.",
            ornekler = listOf("Açısal hız", "Merkezcil ivme", "Merkezcil kuvvet"),
            keywords = listOf("çembersel", "merkezcil", "açısal hız", "ivme")
        ),
        RagKazanim(
            kod = "F.11.1.2",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Basit Harmonik Hareket",
            aciklama = "Basit harmonik hareketi ve özelliklerini açıklar.",
            ornekler = listOf("Yay hareketi", "Sarkaç", "Periyot", "Frekans"),
            keywords = listOf("harmonik", "yay", "sarkaç", "periyot")
        ),
        RagKazanim(
            kod = "F.11.2.1",
            ders = "Fizik",
            unite = "Elektrik ve Manyetizma",
            konu = "Elektrik Alan",
            aciklama = "Elektrik alan kavramını ve özelliklerini bilir.",
            ornekler = listOf("Elektrik alan şiddeti", "Potansiyel fark", "Kondansatör"),
            keywords = listOf("elektrik alan", "potansiyel", "kondansatör", "yük")
        ),
        RagKazanim(
            kod = "F.11.2.2",
            ders = "Fizik",
            unite = "Elektrik ve Manyetizma",
            konu = "Manyetik Alan",
            aciklama = "Manyetik alan ve akım taşıyan iletkenlerin etkileşimini açıklar.",
            ornekler = listOf("Manyetik alan çizgileri", "Lorentz kuvveti", "Elektromıknatıs"),
            keywords = listOf("manyetik alan", "lorentz", "elektromıknatıs", "akım")
        ),
        RagKazanim(
            kod = "F.11.3.1",
            ders = "Fizik",
            unite = "Madde ve Doğası",
            konu = "Atom Modelleri",
            aciklama = "Atom modellerinin gelişimini ve modern atom teorisini bilir.",
            ornekler = listOf("Dalton", "Thomson", "Rutherford", "Bohr"),
            keywords = listOf("atom", "model", "elektron", "çekirdek")
        ),
        RagKazanim(
            kod = "F.11.4.1",
            ders = "Fizik",
            unite = "Optik",
            konu = "Aynalar ve Mercekler",
            aciklama = "Düzlem ve küresel aynalar ile merceklerin özelliklerini bilir.",
            ornekler = listOf("Düzlem ayna", "Çukur ayna", "Tümsek ayna", "İnce kenarlı mercek"),
            keywords = listOf("ayna", "mercek", "görüntü", "odak")
        )
    )

    // ==================== KİMYA ====================
    
    val kimya = listOf(
        RagKazanim(
            kod = "K.11.1.1",
            ders = "Kimya",
            unite = "Enerji",
            konu = "Termodinamik",
            aciklama = "Termodinamik yasalarını ve enerji değişimlerini anlar.",
            ornekler = listOf("Entalpi", "Entropi", "Ekzotermik", "Endotermik"),
            keywords = listOf("termodinamik", "entalpi", "entropi", "enerji")
        ),
        RagKazanim(
            kod = "K.11.2.1",
            ders = "Kimya",
            unite = "Kimyasal Hız",
            konu = "Tepkime Hızı",
            aciklama = "Kimyasal tepkime hızını etkileyen faktörleri bilir.",
            ornekler = listOf("Derişim", "Sıcaklık", "Katalizör", "Yüzey alanı"),
            keywords = listOf("hız", "tepkime", "katalizör", "derişim")
        ),
        RagKazanim(
            kod = "K.11.3.1",
            ders = "Kimya",
            unite = "Denge",
            konu = "Kimyasal Denge",
            aciklama = "Kimyasal denge kavramını ve Le Chatelier prensibini anlar.",
            ornekler = listOf("Denge sabiti", "Le Chatelier", "Geri dönüşümlü tepkime"),
            keywords = listOf("denge", "le chatelier", "geri dönüşümlü", "sabit")
        ),
        RagKazanim(
            kod = "K.11.4.1",
            ders = "Kimya",
            unite = "Asit-Baz Dengesi",
            konu = "Asit-Baz Tepkimeleri",
            aciklama = "Asit-baz tepkimelerini ve tampon çözeltileri açıklar.",
            ornekler = listOf("Kuvvetli asit", "Zayıf baz", "Tampon çözelti", "pH hesabı"),
            keywords = listOf("asit", "baz", "tampon", "pH", "denge")
        ),
        RagKazanim(
            kod = "K.11.5.1",
            ders = "Kimya",
            unite = "Çözünürlük Dengesi",
            konu = "Çözünürlük Çarpımı",
            aciklama = "Çözünürlük dengesi ve çözünürlük çarpımını hesaplar.",
            ornekler = listOf("Ksp", "Çökelme", "İyon çarpımı"),
            keywords = listOf("çözünürlük", "ksp", "çökelme", "denge")
        )
    )

    // ==================== BİYOLOJİ ====================
    
    val biyoloji = listOf(
        RagKazanim(
            kod = "B.11.1.1",
            ders = "Biyoloji",
            unite = "Tepki",
            konu = "Sinir Sistemi",
            aciklama = "Sinir sisteminin yapısını ve çalışma prensibini açıklar.",
            ornekler = listOf("Nöron", "Sinaps", "Uyarı iletimi", "Refleks"),
            keywords = listOf("sinir", "nöron", "sinaps", "uyarı")
        ),
        RagKazanim(
            kod = "B.11.1.2",
            ders = "Biyoloji",
            unite = "Tepki",
            konu = "Endokrin Sistem",
            aciklama = "Hormonların üretimini ve etkilerini bilir.",
            ornekler = listOf("Hipofiz", "Tiroid", "İnsülin", "Adrenalin"),
            keywords = listOf("hormon", "endokrin", "bez", "salgı")
        ),
        RagKazanim(
            kod = "B.11.2.1",
            ders = "Biyoloji",
            unite = "Homeostazi",
            konu = "İç Ortam Dengesi",
            aciklama = "Vücudun iç ortam dengesini nasıl koruduğunu açıklar.",
            ornekler = listOf("Kan şekeri dengesi", "Su dengesi", "Vücut ısısı"),
            keywords = listOf("homeostazi", "denge", "regülasyon", "kontrol")
        ),
        RagKazanim(
            kod = "B.11.2.2",
            ders = "Biyoloji",
            unite = "Homeostazi",
            konu = "Boşaltım Sistemi",
            aciklama = "Boşaltım sisteminin yapısını ve görevlerini bilir.",
            ornekler = listOf("Böbrek", "Nefron", "İdrar oluşumu", "Filtrasyon"),
            keywords = listOf("boşaltım", "böbrek", "nefron", "idrar")
        ),
        RagKazanim(
            kod = "B.11.2.3",
            ders = "Biyoloji",
            unite = "Homeostazi",
            konu = "Bağışıklık Sistemi",
            aciklama = "Bağışıklık sisteminin çalışmasını ve savunma mekanizmalarını açıklar.",
            ornekler = listOf("Antijen", "Antikor", "Aşı", "Bağışıklık türleri"),
            keywords = listOf("bağışıklık", "antijen", "antikor", "aşı")
        )
    )

    // ==================== TARİH ====================
    
    val tarih = listOf(
        RagKazanim(
            kod = "T.11.1.1",
            ders = "Tarih",
            unite = "Osmanlı Tarihi",
            konu = "Osmanlı'da Duraklama Dönemi",
            aciklama = "Osmanlı'nın duraklama dönemini ve nedenlerini açıklar.",
            ornekler = listOf("Karlofça Antlaşması", "Askeri gerileme", "Ekonomik sorunlar"),
            keywords = listOf("duraklama", "gerileme", "karlofça", "sorun")
        ),
        RagKazanim(
            kod = "T.11.1.2",
            ders = "Tarih",
            unite = "Osmanlı Tarihi",
            konu = "Islahat Hareketleri",
            aciklama = "Osmanlı'daki ıslahat hareketlerini ve etkilerini değerlendirir.",
            ornekler = listOf("Tanzimat", "Islahat Fermanı", "I. Meşrutiyet"),
            keywords = listOf("ıslahat", "tanzimat", "reform", "meşrutiyet")
        ),
        RagKazanim(
            kod = "T.11.2.1",
            ders = "Tarih",
            unite = "XIX. Yüzyıl",
            konu = "Osmanlı'da Modernleşme",
            aciklama = "XIX. yüzyılda Osmanlı'nın modernleşme çabalarını bilir.",
            ornekler = listOf("Eğitim reformları", "Askeri yenilikler", "Batılılaşma"),
            keywords = listOf("modernleşme", "reform", "batılılaşma", "yenilik")
        ),
        RagKazanim(
            kod = "T.11.3.1",
            ders = "Tarih",
            unite = "XX. Yüzyıl Başları",
            konu = "II. Meşrutiyet ve Balkan Savaşları",
            aciklama = "II. Meşrutiyet dönemini ve Balkan Savaşları'nı açıklar.",
            ornekler = listOf("1908 İhtilali", "İttihat ve Terakki", "Balkan Savaşları"),
            keywords = listOf("meşrutiyet", "balkan", "savaş", "ittihat terakki")
        )
    )

    // ==================== COĞRAFYA ====================
    
    val cografya = listOf(
        RagKazanim(
            kod = "C.11.1.1",
            ders = "Coğrafya",
            unite = "Türkiye Coğrafyası",
            konu = "Türkiye'nin Bölgeleri",
            aciklama = "Türkiye'nin coğrafi bölgelerini ve özelliklerini bilir.",
            ornekler = listOf("Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu"),
            keywords = listOf("bölge", "coğrafi", "türkiye", "özellik")
        ),
        RagKazanim(
            kod = "C.11.2.1",
            ders = "Coğrafya",
            unite = "Ekonomik Coğrafya",
            konu = "Türkiye'nin Ekonomik Kaynakları",
            aciklama = "Türkiye'nin doğal kaynakları ve ekonomik potansiyelini değerlendirir.",
            ornekler = listOf("Maden kaynakları", "Enerji kaynakları", "Tarım alanları"),
            keywords = listOf("kaynak", "ekonomi", "maden", "enerji")
        ),
        RagKazanim(
            kod = "C.11.3.1",
            ders = "Coğrafya",
            unite = "Çevre ve Toplum",
            konu = "Çevre Sorunları ve Çözümleri",
            aciklama = "Türkiye'nin çevre sorunlarını ve çözüm önerilerini tartışır.",
            ornekler = listOf("Erozyon", "Çölleşme", "Su kirliliği", "Hava kirliliği"),
            keywords = listOf("çevre", "sorun", "kirlilik", "çözüm")
        )
    )

    // ==================== TÜRK DİLİ VE EDEBİYATI ====================
    
    val turk_dili = listOf(
        RagKazanim(
            kod = "TDE.11.1.1",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Dil ve Anlatım",
            konu = "Anlatım Teknikleri",
            aciklama = "Farklı anlatım tekniklerini tanır ve kullanır.",
            ornekler = listOf("Öyküleme", "Betimleme", "Tartışma", "Açıklama"),
            keywords = listOf("anlatım", "teknik", "öyküleme", "betimleme")
        ),
        RagKazanim(
            kod = "TDE.11.1.2",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Tanzimat Edebiyatı",
            aciklama = "Tanzimat edebiyatının özelliklerini ve temsilcilerini bilir.",
            ornekler = listOf("Şinasi", "Namık Kemal", "Ziya Paşa", "Yeni türler"),
            keywords = listOf("tanzimat", "edebiyat", "yenilik", "batılılaşma")
        ),
        RagKazanim(
            kod = "TDE.11.1.3",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Servet-i Fünun Edebiyatı",
            aciklama = "Servet-i Fünun edebiyatının özelliklerini ve sanatçılarını bilir.",
            ornekler = listOf("Tevfik Fikret", "Cenap Şahabettin", "Sanat için sanat"),
            keywords = listOf("servet-i fünun", "edebiyat", "sanat", "şiir")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "İ.11.1.1",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Formal and Informal Language",
            aciklama = "Resmi ve gayri resmi dil kullanımını ayırt eder.",
            ornekler = listOf("Formal letters", "Informal emails", "Register"),
            keywords = listOf("formal", "informal", "register", "style")
        ),
        RagKazanim(
            kod = "İ.11.1.2",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Expressing Cause and Effect",
            aciklama = "Neden-sonuç ilişkilerini İngilizce ifade eder.",
            ornekler = listOf("Because", "Therefore", "As a result", "Due to"),
            keywords = listOf("cause", "effect", "because", "result")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val din_kulturu = listOf(
        RagKazanim(
            kod = "DK.11.1.1",
            ders = "Din Kültürü",
            unite = "İslam Düşüncesi",
            konu = "İtikadi Yorumlar",
            aciklama = "İslam düşüncesindeki farklı itikadi yorumları bilir.",
            ornekler = listOf("Ehl-i Sünnet", "Mutezile", "Eşarilik", "Maturidilik"),
            keywords = listOf("itikat", "yorum", "mezhep", "düşünce")
        ),
        RagKazanim(
            kod = "DK.11.1.2",
            ders = "Din Kültürü",
            unite = "İslam Düşüncesi",
            konu = "Fıkhi Yorumlar",
            aciklama = "İslam hukukundaki farklı mezhepleri ve yorumları tanır.",
            ornekler = listOf("Hanefi", "Şafii", "Maliki", "Hanbeli"),
            keywords = listOf("fıkıh", "mezhep", "hukuk", "yorum")
        )
    )

    // Tüm dersleri birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fizik + kimya + biyoloji + tarih + cografya + 
               turk_dili + ingilizce + din_kulturu
    }
}
