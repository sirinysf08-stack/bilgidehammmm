package com.example.bilgideham

/**
 * 7. Sınıf MEB 2025-2026 Müfredatı
 * RAG sistemi için kazanım veritabanı
 */
object Sinif7Kazanimlari {

    // ==================== TÜRKÇE ====================
    
    val turkce = listOf(
        RagKazanim(
            kod = "T.7.1.1",
            ders = "Türkçe",
            unite = "Sözcükte ve Cümlede Anlam",
            konu = "Anlam İlişkileri ve Yorumlama",
            aciklama = "Sözcük ve cümle düzeyinde anlam ilişkilerini çözümler.",
            ornekler = listOf("Anlam ilişkileri", "Yorum yapma", "Çıkarım"),
            keywords = listOf("anlam", "ilişki", "yorum", "çıkarım", "sözcük", "cümle")
        ),
        RagKazanim(
            kod = "T.7.2.1",
            ders = "Türkçe",
            unite = "Fiiller",
            konu = "Fiillerde Anlam ve Kip",
            aciklama = "Fiillerin anlam özelliklerini (iş, oluş, durum) ve kiplerini tanır.",
            ornekler = listOf("Okumak (iş)", "Solmak (oluş)", "Uyumak (durum)", "Haber/Dilek kipleri"),
            keywords = listOf("fiil", "eylem", "kip", "haber", "dilek", "iş", "oluş", "durum")
        ),
        RagKazanim(
            kod = "T.7.2.2",
            ders = "Türkçe",
            unite = "Fiiller",
            konu = "Fiillerde Zaman Kayması",
            aciklama = "Fiillerde anlam (zaman) kaymasını açıklar.",
            ornekler = listOf("Yarın geliyorum (şimdiki zaman, gelecek anlamında)"),
            keywords = listOf("zaman", "kayma", "fiil", "anlam kayması")
        ),
        RagKazanim(
            kod = "T.7.3.1",
            ders = "Türkçe",
            unite = "Fiilde Yapı",
            konu = "Basit, Türemiş ve Birleşik Fiiller",
            aciklama = "Fiilleri yapılarına göre (basit, türemiş, birleşik) sınıflandırır.",
            ornekler = listOf("Gel (basit)", "Gözle (türemiş)", "Yardım et (birleşik)"),
            keywords = listOf("basit fiil", "türemiş fiil", "birleşik fiil", "yapı")
        ),
        RagKazanim(
            kod = "T.7.4.1",
            ders = "Türkçe",
            unite = "Zarflar",
            konu = "Zarf Türleri",
            aciklama = "Durum, zaman, miktar, yer-yön ve soru zarflarını tanır.",
            ornekler = listOf("Hızlı (durum)", "Bugün (zaman)", "Çok (miktar)", "İleri (yer-yön)"),
            keywords = listOf("zarf", "belirteç", "durum", "zaman", "miktar", "yer")
        ),
        RagKazanim(
            kod = "T.7.5.1",
            ders = "Türkçe",
            unite = "Ek Fiil",
            konu = "Ek Fiil Kullanımı",
            aciklama = "Ek fiilin işlevini ve kullanımını açıklar.",
            ornekler = listOf("Öğrenciyim (ek fiil)", "Güzeldi", "Hastaymiş"),
            keywords = listOf("ek fiil", "ek eylem", "idi", "imiş")
        ),
        RagKazanim(
            kod = "T.7.6.1",
            ders = "Türkçe",
            unite = "Metin Türleri",
            konu = "Biyografi ve Otobiyografi",
            aciklama = "Sohbet, biyografi, otobiyografi, gezi yazısı türlerini tanır.",
            ornekler = listOf("Biyografi: başkası anlatır", "Otobiyografi: kendi anlatır"),
            keywords = listOf("biyografi", "otobiyografi", "sohbet", "gezi yazısı", "metin türü")
        ),
        RagKazanim(
            kod = "T.7.8.1",
            ders = "Türkçe",
            unite = "Anlatım Bozuklukları",
            konu = "Anlamsal Bozukluklar",
            aciklama = "Cümlelerdeki anlamsal bozuklukları tespit eder.",
            ornekler = listOf("Gereksiz sözcük", "Anlam belirsizliği", "Çelişki"),
            keywords = listOf("anlatım bozukluğu", "anlamsal", "gereksiz", "belirsizlik")
        )
    )

    // ==================== MATEMATİK ====================
    
    val matematik = listOf(
        RagKazanim(
            kod = "MAT.7.1.1",
            ders = "Matematik",
            unite = "Tam Sayılarla İşlemler",
            konu = "Tam Sayılarla Dört İşlem",
            aciklama = "Tam sayılarla toplama, çıkarma, çarpma, bölme ve üslü işlemler yapar.",
            ornekler = listOf("(-3) + (+5) = 2", "(-2) × (-4) = 8", "(-2)³ = -8"),
            keywords = listOf("tam sayı", "toplama", "çıkarma", "çarpma", "bölme", "üs")
        ),
        RagKazanim(
            kod = "MAT.7.2.1",
            ders = "Matematik",
            unite = "Rasyonel Sayılar",
            konu = "Rasyonel Sayılar ve İşlemler",
            aciklama = "Rasyonel sayıları tanır, sıralar ve dört işlem yapar.",
            ornekler = listOf("-2/3", "0,5 = 1/2", "Rasyonel sayılarla işlem"),
            keywords = listOf("rasyonel", "kesir", "ondalık", "sayı doğrusu")
        ),
        RagKazanim(
            kod = "MAT.7.3.1",
            ders = "Matematik",
            unite = "Cebirsel İfadeler",
            konu = "Cebirsel İfadelerle İşlemler",
            aciklama = "Cebirsel ifadelerle toplama, çıkarma ve çarpma yapar.",
            ornekler = listOf("2x + 3x = 5x", "3(x+2) = 3x + 6"),
            keywords = listOf("cebir", "ifade", "toplama", "çıkarma", "çarpma", "değişken")
        ),
        RagKazanim(
            kod = "MAT.7.4.1",
            ders = "Matematik",
            unite = "Denklemler",
            konu = "Birinci Dereceden Denklemler",
            aciklama = "Birinci dereceden bir bilinmeyenli denklemleri çözer.",
            ornekler = listOf("2x + 5 = 11", "x = 3", "Denklem problemi"),
            keywords = listOf("denklem", "bilinmeyen", "çözüm", "eşitlik")
        ),
        RagKazanim(
            kod = "MAT.7.5.1",
            ders = "Matematik",
            unite = "Oran ve Orantı",
            konu = "Doğru ve Ters Orantı",
            aciklama = "Doğru ve ters orantı kavramlarını kullanır, problemler çözer.",
            ornekler = listOf("Doğru orantı: x↑ y↑", "Ters orantı: x↑ y↓"),
            keywords = listOf("oran", "orantı", "doğru orantı", "ters orantı")
        ),
        RagKazanim(
            kod = "MAT.7.6.1",
            ders = "Matematik",
            unite = "Yüzdeler",
            konu = "Yüzde Hesaplamaları",
            aciklama = "Bir çokluğun yüzdesini bulur, yüzde problemlerini çözer.",
            ornekler = listOf("200'ün %25'i = 50", "İndirim/artış hesabı"),
            keywords = listOf("yüzde", "oran", "hesaplama", "indirim", "artış")
        ),
        RagKazanim(
            kod = "MAT.7.7.1",
            ders = "Matematik",
            unite = "Açılar",
            konu = "Açıortay ve Paralel Doğrular",
            aciklama = "Açıortayı çizer, paralel doğruların kesenle yaptığı açıları hesaplar.",
            ornekler = listOf("Açıortay", "İç ters açılar", "Yöndeş açılar"),
            keywords = listOf("açıortay", "paralel", "kesen", "iç ters", "yöndeş")
        ),
        RagKazanim(
            kod = "MAT.7.8.1",
            ders = "Matematik",
            unite = "Çokgenler",
            konu = "Düzgün Çokgenler ve Dörtgenler",
            aciklama = "Düzgün çokgenleri tanır, dörtgenlerin alanını hesaplar.",
            ornekler = listOf("Düzgün altıgen", "Yamuk alanı", "Eşkenar dörtgen"),
            keywords = listOf("çokgen", "düzgün", "dörtgen", "alan", "yamuk")
        ),
        RagKazanim(
            kod = "MAT.7.9.1",
            ders = "Matematik",
            unite = "Çember ve Daire",
            konu = "Çember ve Daire Hesaplamaları",
            aciklama = "Çemberin çevresini ve dairenin alanını hesaplar.",
            ornekler = listOf("Çevre = 2πr", "Alan = πr²", "Pi sayısı"),
            keywords = listOf("çember", "daire", "çevre", "alan", "pi", "yarıçap")
        ),
        RagKazanim(
            kod = "MAT.7.10.1",
            ders = "Matematik",
            unite = "Veri Analizi",
            konu = "Çizgi ve Daire Grafiği",
            aciklama = "Çizgi grafiği ve daire grafiği oluşturur ve yorumlar.",
            ornekler = listOf("Zaman serisi grafiği", "Yüzdelik daire dilimi"),
            keywords = listOf("grafik", "çizgi grafik", "daire grafik", "veri", "yorumlama")
        )
    )

    // ==================== FEN BİLİMLERİ ====================
    
    val fenBilimleri = listOf(
        RagKazanim(
            kod = "F.7.1.1",
            ders = "Fen Bilimleri",
            unite = "Güneş Sistemi ve Ötesi",
            konu = "Uzay Araştırmaları ve Gök Cisimleri",
            aciklama = "Yıldızlar, galaksiler ve evren hakkında bilgi edinir.",
            ornekler = listOf("Samanyolu Galaksisi", "Yıldız türleri", "Evrenin genişlemesi"),
            keywords = listOf("uzay", "galaksi", "yıldız", "evren", "araştırma")
        ),
        RagKazanim(
            kod = "F.7.2.1",
            ders = "Fen Bilimleri",
            unite = "Hücre ve Bölünmeler",
            konu = "Mitoz ve Mayoz Bölünme",
            aciklama = "Hücre bölünme türlerini (mitoz, mayoz) açıklar.",
            ornekler = listOf("Mitoz: büyüme, onarım", "Mayoz: üreme hücresi"),
            keywords = listOf("hücre", "mitoz", "mayoz", "bölünme", "kromozom")
        ),
        RagKazanim(
            kod = "F.7.3.1",
            ders = "Fen Bilimleri",
            unite = "Kuvvet ve Enerji",
            konu = "Kuvvet, İş ve Enerji",
            aciklama = "Kuvvet-iş-enerji ilişkisini ve enerji dönüşümlerini açıklar.",
            ornekler = listOf("İş = Kuvvet × Yol", "Potansiyel-kinetik dönüşüm"),
            keywords = listOf("kuvvet", "iş", "enerji", "joule", "dönüşüm")
        ),
        RagKazanim(
            kod = "F.7.4.1",
            ders = "Fen Bilimleri",
            unite = "Saf Madde ve Karışımlar",
            konu = "Element, Bileşik ve Karışımlar",
            aciklama = "Atom, element, bileşik ve karışım kavramlarını açıklar.",
            ornekler = listOf("Oksijen (element)", "Su (bileşik)", "Tuzlu su (karışım)"),
            keywords = listOf("atom", "element", "bileşik", "karışım", "molekül")
        ),
        RagKazanim(
            kod = "F.7.5.1",
            ders = "Fen Bilimleri",
            unite = "Işık",
            konu = "Aynalar ve Mercekler",
            aciklama = "Düz, çukur, tümsek aynalar ve mercekleri açıklar.",
            ornekler = listOf("Düz ayna: sanal görüntü", "Çukur ayna: gerçek/sanal", "İnce kenarlı mercek"),
            keywords = listOf("ayna", "mercek", "yansıma", "kırılma", "görüntü")
        ),
        RagKazanim(
            kod = "F.7.6.1",
            ders = "Fen Bilimleri",
            unite = "Canlılarda Üreme",
            konu = "İnsan, Bitki ve Hayvanlarda Üreme",
            aciklama = "İnsan, bitki ve hayvanlardaki üreme süreçlerini açıklar.",
            ornekler = listOf("Eşeyli-eşeysiz üreme", "Döllenme", "Gelişim"),
            keywords = listOf("üreme", "eşeyli", "eşeysiz", "döllenme", "gelişim")
        ),
        RagKazanim(
            kod = "F.7.7.1",
            ders = "Fen Bilimleri",
            unite = "Elektrik Devreleri",
            konu = "Seri ve Paralel Bağlama",
            aciklama = "Ampullerin seri ve paralel bağlanma şekillerini açıklar.",
            ornekler = listOf("Seri: akım aynı", "Paralel: gerilim aynı"),
            keywords = listOf("seri", "paralel", "devre", "ampul", "bağlama")
        )
    )

    // ==================== SOSYAL BİLGİLER ====================
    
    val sosyalBilgiler = listOf(
        RagKazanim(
            kod = "SB.7.1.1",
            ders = "Sosyal Bilgiler",
            unite = "Birey ve Toplum",
            konu = "İletişim ve Kitle İletişim Araçları",
            aciklama = "İletişimin önemini ve kitle iletişim araçlarını değerlendirir.",
            ornekler = listOf("Sözlü/yazılı iletişim", "Televizyon", "İnternet"),
            keywords = listOf("iletişim", "kitle", "medya", "televizyon", "internet")
        ),
        RagKazanim(
            kod = "SB.7.2.1",
            ders = "Sosyal Bilgiler",
            unite = "Kültür ve Miras",
            konu = "Osmanlı Devleti",
            aciklama = "Osmanlı Devleti'nin kuruluşu, yükselişi ve kültür-sanatını açıklar.",
            ornekler = listOf("Kuruluş dönemi", "Fatih Sultan Mehmet", "Kanuni"),
            keywords = listOf("Osmanlı", "kuruluş", "yükseliş", "fetih", "ıslahat")
        ),
        RagKazanim(
            kod = "SB.7.3.1",
            ders = "Sosyal Bilgiler",
            unite = "Coğrafya",
            konu = "Nüfus ve Göç",
            aciklama = "Nüfus dağılışını, göçleri ve yerleşme hürriyetini açıklar.",
            ornekler = listOf("Kırdan kente göç", "İç göç", "Dış göç"),
            keywords = listOf("nüfus", "göç", "yerleşme", "dağılış")
        ),
        RagKazanim(
            kod = "SB.7.4.1",
            ders = "Sosyal Bilgiler",
            unite = "Bilim ve Teknoloji",
            konu = "Türk-İslam Bilginleri",
            aciklama = "Bilimin öncülerini ve Türk-İslam bilginlerini tanır.",
            ornekler = listOf("İbn-i Sina", "El-Harezmi", "Biruni"),
            keywords = listOf("bilgin", "bilim", "Türk-İslam", "buluş")
        ),
        RagKazanim(
            kod = "SB.7.5.1",
            ders = "Sosyal Bilgiler",
            unite = "Ekonomi",
            konu = "Lonca Teşkilatı ve Vakıflar",
            aciklama = "Tarihte üretimi, loncaları ve vakıfları açıklar.",
            ornekler = listOf("Lonca sistemi", "Sanayi İnkılabı", "Vakıf kültürü"),
            keywords = listOf("lonca", "vakıf", "sanayi", "üretim")
        ),
        RagKazanim(
            kod = "SB.7.6.1",
            ders = "Sosyal Bilgiler",
            unite = "Vatandaşlık",
            konu = "Demokrasi ve Anayasa",
            aciklama = "Demokrasinin tarihsel gelişimini ve anayasayı açıklar.",
            ornekler = listOf("Demokrasi tarihi", "Anayasa kavramı", "Cumhuriyet"),
            keywords = listOf("demokrasi", "anayasa", "cumhuriyet", "yönetim")
        ),
        RagKazanim(
            kod = "SB.7.7.1",
            ders = "Sosyal Bilgiler",
            unite = "Küresel Bağlantılar",
            konu = "Uluslararası İlişkiler ve Küresel Sorunlar",
            aciklama = "Türkiye'nin uluslararası ilişkilerini ve küresel sorunları açıklar.",
            ornekler = listOf("BM", "NATO", "İklim değişikliği"),
            keywords = listOf("uluslararası", "küresel", "sorun", "iklim", "örgüt")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "E.7.1",
            ders = "İngilizce",
            unite = "Appearance and Personality",
            konu = "Describing People",
            aciklama = "İnsanların görünüş ve kişilik özelliklerini tanımlar.",
            ornekler = listOf("She is tall", "He is friendly", "They are hardworking"),
            keywords = listOf("appearance", "personality", "describe", "tall", "friendly")
        ),
        RagKazanim(
            kod = "E.7.3",
            ders = "İngilizce",
            unite = "Biographies",
            konu = "Past Tense and Life Stories",
            aciklama = "Geçmiş zaman kullanarak yaşam öykülerini anlatır.",
            ornekler = listOf("He was born in...", "She studied...", "They lived..."),
            keywords = listOf("biography", "past tense", "life story", "born")
        ),
        RagKazanim(
            kod = "E.7.7",
            ders = "İngilizce",
            unite = "Dreams",
            konu = "Future Tense - Will",
            aciklama = "Gelecek zamanda tahmin ve plan yapar.",
            ornekler = listOf("I will be a doctor", "It will rain", "They will travel"),
            keywords = listOf("future", "will", "prediction", "plan", "dream")
        ),
        RagKazanim(
            kod = "E.7.10",
            ders = "İngilizce",
            unite = "Planets",
            konu = "Solar System and Comparisons",
            aciklama = "Güneş sistemini karşılaştırma yaparak anlatır.",
            ornekler = listOf("Mars is smaller than Earth", "Jupiter is the biggest"),
            keywords = listOf("planet", "solar system", "comparison", "superlative")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val dinKulturu = listOf(
        RagKazanim(
            kod = "DK.7.1.1",
            ders = "Din Kültürü",
            unite = "Melek ve Ahiret İnancı",
            konu = "Melekler ve Ahiret Hayatı",
            aciklama = "Melekleri, kıyameti ve ahiret hayatını açıklar.",
            ornekler = listOf("Dört büyük melek", "Kıyamet alametleri", "Cennet-cehennem"),
            keywords = listOf("melek", "ahiret", "kıyamet", "cennet", "cehennem")
        ),
        RagKazanim(
            kod = "DK.7.2.1",
            ders = "Din Kültürü",
            unite = "Hac ve Kurban",
            konu = "Hac ve Kurban İbadeti",
            aciklama = "Hac ve kurban ibadetlerinin önemini açıklar.",
            ornekler = listOf("Haccın şartları", "Kurban kesimi", "Kurban Bayramı"),
            keywords = listOf("hac", "kurban", "ibadet", "Mekke", "bayram")
        ),
        RagKazanim(
            kod = "DK.7.3.1",
            ders = "Din Kültürü",
            unite = "Ahlaki Davranışlar",
            konu = "Güzel Ahlak ve Adalet",
            aciklama = "Güzel ahlak, adalet ve dostluk değerlerini açıklar.",
            ornekler = listOf("Doğruluk", "Adalet", "Vefakarlık"),
            keywords = listOf("ahlak", "adalet", "dostluk", "değer", "erdem")
        ),
        RagKazanim(
            kod = "DK.7.5.1",
            ders = "Din Kültürü",
            unite = "İslam Düşüncesinde Yorumlar",
            konu = "Mezhepler",
            aciklama = "İtikadi ve fıkhi mezhepleri tanır.",
            ornekler = listOf("Maturidi", "Eş'ari", "Hanefi", "Şafii"),
            keywords = listOf("mezhep", "itikad", "fıkıh", "yorum", "düşünce")
        )
    )

    fun tumKazanimlar(): List<RagKazanim> {
        return turkce + matematik + fenBilimleri + sosyalBilgiler + ingilizce + dinKulturu
    }
}
