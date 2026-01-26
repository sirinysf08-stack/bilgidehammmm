package com.example.bilgideham

/**
 * 12. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 * Lise 12. Sınıf - Genel Lise Müfredatı (YKS Hazırlık)
 */
object Lise12Kazanimlari {

    // ==================== MATEMATİK ====================
    
    val matematik = listOf(
        RagKazanim(
            kod = "M.12.1.1",
            ders = "Matematik",
            unite = "Değişimin Matematiği",
            konu = "Limit",
            aciklama = "Limit kavramını anlar ve limit hesaplamalarını yapar.",
            ornekler = listOf("lim(x→a) f(x)", "Belirsizlik durumları", "Limit özellikleri"),
            keywords = listOf("limit", "yaklaşma", "belirsizlik", "süreklilik")
        ),
        RagKazanim(
            kod = "M.12.1.2",
            ders = "Matematik",
            unite = "Değişimin Matematiği",
            konu = "Türev",
            aciklama = "Türev kavramını ve türev alma kurallarını bilir.",
            ornekler = listOf("f'(x)", "Türev kuralları", "Anlık değişim hızı"),
            keywords = listOf("türev", "değişim hızı", "eğim", "anlık")
        ),
        RagKazanim(
            kod = "M.12.1.3",
            ders = "Matematik",
            unite = "Değişimin Matematiği",
            konu = "İntegral (Opsiyonel)",
            aciklama = "[ESKİ MÜFREDAT - 2026'da kaldırıldı] Belirsiz ve belirli integral hesaplamalarını yapar. AYT için ekstra çalışma.",
            ornekler = listOf("∫f(x)dx", "Belirli integral", "Alan hesabı", "Ters türev"),
            keywords = listOf("integral", "alan", "toplam", "ters türev", "opsiyonel"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "M.12.2.1",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Katı Cisimler",
            aciklama = "Katı cisimlerin hacim ve yüzey alanlarını hesaplar.",
            ornekler = listOf("Küre", "Koni", "Silindir", "Prizma", "Piramit"),
            keywords = listOf("hacim", "yüzey alanı", "katı cisim", "geometri")
        ),
        RagKazanim(
            kod = "M.12.3.1",
            ders = "Matematik",
            unite = "Hazır Veriler",
            konu = "İstatistiksel Analiz",
            aciklama = "Veri setlerini analiz eder ve yorumlar.",
            ornekler = listOf("Standart sapma", "Varyans", "Korelasyon"),
            keywords = listOf("istatistik", "analiz", "sapma", "varyans")
        )
    )

    // ==================== FİZİK ====================
    
    val fizik = listOf(
        RagKazanim(
            kod = "F.12.1.1",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Momentum ve İtme",
            aciklama = "Momentum ve itme kavramlarını anlar, momentum korunumunu uygular.",
            ornekler = listOf("p = mv", "İtme = Δp", "Çarpışmalar", "Momentum korunumu"),
            keywords = listOf("momentum", "itme", "çarpışma", "korunum")
        ),
        RagKazanim(
            kod = "F.12.1.2",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Açısal Momentum",
            aciklama = "Açısal momentum ve dönme hareketini açıklar.",
            ornekler = listOf("L = Iω", "Dönme hareketi", "Tork", "Açısal momentum korunumu"),
            keywords = listOf("açısal momentum", "dönme", "tork", "atalet momenti")
        ),
        RagKazanim(
            kod = "F.12.2.1",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Termodinamik",
            aciklama = "Termodinamik yasalarını ve ısı makinelerini anlar.",
            ornekler = listOf("İç enerji", "İş ve ısı", "Carnot çevrimi", "Verim"),
            keywords = listOf("termodinamik", "ısı", "iş", "verim", "entropi")
        ),
        RagKazanim(
            kod = "F.12.3.1",
            ders = "Fizik",
            unite = "Dalgalar",
            konu = "Elektromanyetik Dalgalar",
            aciklama = "Elektromanyetik dalgaları ve spektrumu bilir.",
            ornekler = listOf("Radyo dalgaları", "Mikrodalgalar", "Kızılötesi", "Görünür ışık", "X-ışınları"),
            keywords = listOf("elektromanyetik", "dalga", "spektrum", "frekans")
        ),
        RagKazanim(
            kod = "F.12.4.1",
            ders = "Fizik",
            unite = "Madde ve Doğası",
            konu = "Modern Fizik",
            aciklama = "Modern fizik kavramlarını ve kuantum fiziğinin temellerini bilir.",
            ornekler = listOf("Fotoelektrik olay", "Kuantum", "Atom modelleri", "Radyoaktivite"),
            keywords = listOf("modern fizik", "kuantum", "fotoelektrik", "radyoaktivite")
        )
    )

    // ==================== KİMYA ====================
    
    val kimya = listOf(
        RagKazanim(
            kod = "K.12.1.1",
            ders = "Kimya",
            unite = "Redoks Tepkimeleri",
            konu = "Yükseltgenme-İndirgenme",
            aciklama = "Redoks tepkimelerini tanır ve denklemlerini dengeler.",
            ornekler = listOf("Yükseltgenme sayısı", "Elektron transferi", "Redoks denklemi"),
            keywords = listOf("redoks", "yükseltgenme", "indirgenme", "elektron")
        ),
        RagKazanim(
            kod = "K.12.2.1",
            ders = "Kimya",
            unite = "Elektrokimya",
            konu = "Elektrokimyasal Hücreler",
            aciklama = "Galvanik ve elektrolitik hücreleri açıklar.",
            ornekler = listOf("Pil", "Elektroliz", "Anot", "Katot"),
            keywords = listOf("elektrokimya", "pil", "elektroliz", "hücre")
        ),
        RagKazanim(
            kod = "K.12.3.1",
            ders = "Kimya",
            unite = "Organik Kimya",
            konu = "Hidrokarbonlar",
            aciklama = "Hidrokarbonların sınıflandırmasını ve özelliklerini bilir.",
            ornekler = listOf("Alkanlar", "Alkenler", "Alkinler", "Aromatik bileşikler"),
            keywords = listOf("hidrokarbon", "alkan", "alken", "alkin", "organik")
        ),
        RagKazanim(
            kod = "K.12.3.2",
            ders = "Kimya",
            unite = "Organik Kimya",
            konu = "Fonksiyonel Gruplar",
            aciklama = "Organik bileşiklerdeki fonksiyonel grupları tanır.",
            ornekler = listOf("Alkol", "Aldehit", "Keton", "Karboksilik asit", "Ester"),
            keywords = listOf("fonksiyonel grup", "alkol", "asit", "ester", "organik")
        ),
        RagKazanim(
            kod = "K.12.4.1",
            ders = "Kimya",
            unite = "Yeşil Kimya",
            konu = "Sürdürülebilir Kimya",
            aciklama = "Yeşil kimya prensiplerini ve sürdürülebilir uygulamaları bilir.",
            ornekler = listOf("Atık azaltma", "Yenilenebilir kaynaklar", "Enerji verimliliği"),
            keywords = listOf("yeşil kimya", "sürdürülebilirlik", "çevre", "atık")
        )
    )

    // ==================== BİYOLOJİ ====================
    
    val biyoloji = listOf(
        RagKazanim(
            kod = "B.12.1.1",
            ders = "Biyoloji",
            unite = "Genetik",
            konu = "DNA ve Genetik Şifre",
            aciklama = "DNA yapısını ve genetik şifrenin özelliklerini bilir.",
            ornekler = listOf("Çift sarmal", "Baz çiftleri", "Genetik kod", "Kodon"),
            keywords = listOf("DNA", "genetik", "baz", "kodon", "şifre")
        ),
        RagKazanim(
            kod = "B.12.1.2",
            ders = "Biyoloji",
            unite = "Genetik",
            konu = "Protein Sentezi",
            aciklama = "Protein sentezi sürecini ve aşamalarını açıklar.",
            ornekler = listOf("Transkripsiyon", "Translasyon", "mRNA", "tRNA", "Ribozom"),
            keywords = listOf("protein sentezi", "transkripsiyon", "translasyon", "RNA")
        ),
        RagKazanim(
            kod = "B.12.2.1",
            ders = "Biyoloji",
            unite = "Kalıtım",
            konu = "Mendel Genetiği",
            aciklama = "Mendel'in kalıtım yasalarını ve uygulamalarını bilir.",
            ornekler = listOf("Baskın-çekinik", "Genotip-fenotip", "Monohibrit çaprazlama"),
            keywords = listOf("mendel", "kalıtım", "gen", "baskın", "çekinik")
        ),
        RagKazanim(
            kod = "B.12.2.2",
            ders = "Biyoloji",
            unite = "Kalıtım",
            konu = "Modern Genetik",
            aciklama = "Modern genetik kavramlarını ve biyoteknoloji uygulamalarını bilir.",
            ornekler = listOf("Gen mühendisliği", "Klonlama", "GMO", "DNA parmak izi"),
            keywords = listOf("biyoteknoloji", "gen mühendisliği", "klonlama", "GMO")
        ),
        RagKazanim(
            kod = "B.12.3.1",
            ders = "Biyoloji",
            unite = "Evrim",
            konu = "Evrim Teorisi",
            aciklama = "Evrim teorisini ve kanıtlarını açıklar.",
            ornekler = listOf("Doğal seçilim", "Adaptasyon", "Fosiller", "Homolog organlar"),
            keywords = listOf("evrim", "doğal seçilim", "darwin", "adaptasyon")
        )
    )

    // ==================== TARİH ====================
    
    val tarih = listOf(
        RagKazanim(
            kod = "T.12.1.1",
            ders = "Tarih",
            unite = "I. Dünya Savaşı",
            konu = "Savaşın Nedenleri ve Gelişimi",
            aciklama = "I. Dünya Savaşı'nın nedenlerini ve gelişimini açıklar.",
            ornekler = listOf("İttifaklar", "Cepheler", "Osmanlı'nın katılımı"),
            keywords = listOf("dünya savaşı", "ittifak", "cephe", "savaş")
        ),
        RagKazanim(
            kod = "T.12.2.1",
            ders = "Tarih",
            unite = "Milli Mücadele",
            konu = "Kurtuluş Savaşı",
            aciklama = "Milli Mücadele dönemini ve Kurtuluş Savaşı'nı bilir.",
            ornekler = listOf("Amasya Genelgesi", "TBMM", "Sakarya", "Büyük Taarruz"),
            keywords = listOf("milli mücadele", "kurtuluş savaşı", "atatürk", "tbmm")
        ),
        RagKazanim(
            kod = "T.12.3.1",
            ders = "Tarih",
            unite = "Atatürk İlkeleri",
            konu = "İnkılaplar ve İlkeler",
            aciklama = "Atatürk'ün inkılaplarını ve ilkelerini bilir.",
            ornekler = listOf("Cumhuriyetçilik", "Laiklik", "Halkçılık", "Devrimcilik"),
            keywords = listOf("atatürk", "inkılap", "ilke", "cumhuriyet")
        ),
        RagKazanim(
            kod = "T.12.4.1",
            ders = "Tarih",
            unite = "Çağdaş Türkiye",
            konu = "Çok Partili Dönem",
            aciklama = "Türkiye'nin çok partili döneme geçişini ve gelişmelerini bilir.",
            ornekler = listOf("Demokrat Parti", "1950 seçimleri", "Siyasi gelişmeler"),
            keywords = listOf("çok partili", "demokrasi", "seçim", "parti")
        )
    )

    // ==================== COĞRAFYA ====================
    
    val cografya = listOf(
        RagKazanim(
            kod = "C.12.1.1",
            ders = "Coğrafya",
            unite = "Dünya Coğrafyası",
            konu = "Kıtalar ve Okyanuslar",
            aciklama = "Dünya'nın kıtalarını ve okyanuslarını bilir.",
            ornekler = listOf("Asya", "Afrika", "Avrupa", "Amerika", "Pasifik", "Atlas"),
            keywords = listOf("kıta", "okyanus", "dünya", "coğrafya")
        ),
        RagKazanim(
            kod = "C.12.2.1",
            ders = "Coğrafya",
            unite = "Küresel Sorunlar",
            konu = "İklim Değişikliği",
            aciklama = "Küresel iklim değişikliğini ve etkilerini açıklar.",
            ornekler = listOf("Sera etkisi", "Küresel ısınma", "Buzulların erimesi"),
            keywords = listOf("iklim değişikliği", "küresel ısınma", "sera etkisi")
        ),
        RagKazanim(
            kod = "C.12.3.1",
            ders = "Coğrafya",
            unite = "Uluslararası İlişkiler",
            konu = "Ekonomik ve Siyasi Birlikler",
            aciklama = "Dünya'daki önemli ekonomik ve siyasi birlikleri bilir.",
            ornekler = listOf("AB", "NATO", "BM", "G20", "BRICS"),
            keywords = listOf("birlik", "uluslararası", "ekonomi", "siyaset")
        )
    )

    // ==================== TÜRK DİLİ VE EDEBİYATI ====================
    
    val turk_dili = listOf(
        RagKazanim(
            kod = "TDE.12.1.1",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Dil ve Anlatım",
            konu = "Metin Çözümleme",
            aciklama = "Farklı türdeki metinleri çözümler ve yorumlar.",
            ornekler = listOf("Şiir çözümleme", "Hikaye analizi", "Makale inceleme"),
            keywords = listOf("metin", "çözümleme", "analiz", "yorum")
        ),
        RagKazanim(
            kod = "TDE.12.1.2",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Cumhuriyet Dönemi Edebiyatı",
            aciklama = "Cumhuriyet dönemi Türk edebiyatını ve temsilcilerini bilir.",
            ornekler = listOf("Nazım Hikmet", "Orhan Veli", "Sait Faik", "Yaşar Kemal"),
            keywords = listOf("cumhuriyet", "edebiyat", "şiir", "roman", "modern")
        ),
        RagKazanim(
            kod = "TDE.12.1.3",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Çağdaş Türk Edebiyatı",
            aciklama = "Çağdaş Türk edebiyatının özelliklerini ve yazarlarını tanır.",
            ornekler = listOf("Postmodern edebiyat", "Güncel yazarlar", "Yeni akımlar"),
            keywords = listOf("çağdaş", "modern", "edebiyat", "yazar")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "İ.12.1.1",
            ders = "İngilizce",
            unite = "Advanced Communication",
            konu = "Academic Writing",
            aciklama = "Akademik yazma becerilerini geliştirir.",
            ornekler = listOf("Essay writing", "Research papers", "Citations"),
            keywords = listOf("academic", "writing", "essay", "research")
        ),
        RagKazanim(
            kod = "İ.12.1.2",
            ders = "İngilizce",
            unite = "Advanced Communication",
            konu = "Critical Reading",
            aciklama = "Eleştirel okuma ve analiz becerilerini kullanır.",
            ornekler = listOf("Text analysis", "Inference", "Critical thinking"),
            keywords = listOf("critical", "reading", "analysis", "inference")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val din_kulturu = listOf(
        RagKazanim(
            kod = "DK.12.1.1",
            ders = "Din Kültürü",
            unite = "Kur'an-ı Kerim",
            konu = "Kur'an'ın Temel Mesajları",
            aciklama = "Kur'an'ın temel mesajlarını ve evrensel değerlerini bilir.",
            ornekler = listOf("Tevhid", "Adalet", "Merhamet", "Sorumluluk"),
            keywords = listOf("kuran", "mesaj", "değer", "evrensel")
        ),
        RagKazanim(
            kod = "DK.12.2.1",
            ders = "Din Kültürü",
            unite = "Güncel Dinî Meseleler",
            konu = "Din ve Çağdaş Sorunlar",
            aciklama = "Dinin çağdaş sorunlara yaklaşımını tartışır.",
            ornekler = listOf("Aile", "Teknoloji", "Çevre", "Etik"),
            keywords = listOf("güncel", "çağdaş", "sorun", "din", "etik")
        ),
        RagKazanim(
            kod = "DK.12.3.1",
            ders = "Din Kültürü",
            unite = "Dünya Dinleri",
            konu = "Hint ve Çin Dinleri",
            aciklama = "Hinduizm, Budizm ve diğer Doğu dinlerini tanır.",
            ornekler = listOf("Hinduizm", "Budizm", "Konfüçyanizm", "Taoizm"),
            keywords = listOf("dünya dinleri", "hinduizm", "budizm", "doğu")
        )
    )

    // Tüm dersleri birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fizik + kimya + biyoloji + tarih + cografya + 
               turk_dili + ingilizce + din_kulturu
    }
}
