package com.example.bilgideham

/**
 * 8. Sınıf MEB 2025-2026 Müfredatı (LGS)
 * RAG sistemi için kazanım veritabanı
 * LGS sınavına yönelik en kapsamlı içerik
 */
object Sinif8Kazanimlari {

    // ==================== TÜRKÇE (LGS) ====================
    
    val turkce = listOf(
        RagKazanim(
            kod = "T.8.1.1",
            ders = "Türkçe",
            unite = "Fiilimsiler",
            konu = "İsim-Fiil, Sıfat-Fiil, Zarf-Fiil",
            aciklama = "Fiilimsilerin türlerini ve işlevlerini kavrar.",
            ornekler = listOf("Gelmek istiyorum (isim-fiil)", "Gelen adam (sıfat-fiil)", "Gülerek girdi (zarf-fiil)"),
            keywords = listOf("fiilimsi", "eylemsi", "isim-fiil", "sıfat-fiil", "zarf-fiil")
        ),
        RagKazanim(
            kod = "T.8.2.1",
            ders = "Türkçe",
            unite = "Cümlenin Öğeleri",
            konu = "Temel ve Yardımcı Ögeler",
            aciklama = "Yüklem, özne, nesne, yer tamlayıcısı ve zarf tamlayıcısını belirler.",
            ornekler = listOf("Yüklem: eylemin kendisi", "Özne: işi yapan", "Nesne: işten etkilenen"),
            keywords = listOf("yüklem", "özne", "nesne", "dolaylı tümleç", "zarf tümleci", "öğe")
        ),
        RagKazanim(
            kod = "T.8.3.1",
            ders = "Türkçe",
            unite = "Fiilde Çatı",
            konu = "Etken, Edilgen, Geçişli, Geçişsiz",
            aciklama = "Fiilleri öznesine ve nesnesine göre çatı bakımından inceler.",
            ornekler = listOf("Etken: Ali kapıyı açtı", "Edilgen: Kapı açıldı", "Geçişli: yemek yedi"),
            keywords = listOf("çatı", "etken", "edilgen", "geçişli", "geçişsiz", "dönüşlü")
        ),
        RagKazanim(
            kod = "T.8.4.1",
            ders = "Türkçe",
            unite = "Cümle Türleri",
            konu = "Cümle Sınıflandırması",
            aciklama = "Cümleleri yüklemin türüne, yerine, anlamına ve yapısına göre sınıflandırır.",
            ornekler = listOf("Fiil/isim cümlesi", "Kurallı/devrik", "Olumlu/olumsuz", "Basit/bileşik"),
            keywords = listOf("cümle türü", "fiil cümlesi", "isim cümlesi", "devrik", "sıralı", "bağlı")
        ),
        RagKazanim(
            kod = "T.8.5.1",
            ders = "Türkçe",
            unite = "Anlatım Bozuklukları",
            konu = "Yapısal Bozukluklar",
            aciklama = "Cümlelerdeki yapısal anlatım bozukluklarını tespit eder.",
            ornekler = listOf("Özne-yüklem uyumsuzluğu", "Tamlama yanlışı", "Ek eksikliği"),
            keywords = listOf("anlatım bozukluğu", "yapısal", "özne-yüklem", "tamlama", "ek")
        ),
        RagKazanim(
            kod = "T.8.7.1",
            ders = "Türkçe",
            unite = "Sözel Mantık",
            konu = "Mantık ve Muhakeme",
            aciklama = "Sözel mantık ve muhakeme becerilerini kullanır.",
            ornekler = listOf("Sıralama", "Gruplama", "Eşleştirme", "Sonuç çıkarma"),
            keywords = listOf("mantık", "muhakeme", "sıralama", "gruplama", "çıkarım")
        )
    )

    // ==================== MATEMATİK (LGS) ====================
    
    val matematik = listOf(
        RagKazanim(
            kod = "MAT.8.1.1",
            ders = "Matematik",
            unite = "Çarpanlar ve Katlar",
            konu = "EBOB ve EKOK",
            aciklama = "EBOB ve EKOK hesaplar, problemler çözer.",
            ornekler = listOf("EBOB(12,18)=6", "EKOK(4,6)=12", "EBOB-EKOK problemi"),
            keywords = listOf("EBOB", "EKOK", "ortak bölen", "ortak kat", "çarpan")
        ),
        RagKazanim(
            kod = "MAT.8.2.1",
            ders = "Matematik",
            unite = "Üslü İfadeler",
            konu = "Üslü İfadelerle İşlemler",
            aciklama = "Üslü ifadelerle işlemler yapar, bilimsel gösterimi kullanır.",
            ornekler = listOf("2³ × 2² = 2⁵", "10⁻³ = 0,001", "3,5 × 10⁶"),
            keywords = listOf("üs", "kuvvet", "bilimsel gösterim", "negatif üs")
        ),
        RagKazanim(
            kod = "MAT.8.3.1",
            ders = "Matematik",
            unite = "Kareköklü İfadeler",
            konu = "Karekök ve İşlemler",
            aciklama = "Tam kare sayıları tanır, kareköklü ifadelerle işlem yapar.",
            ornekler = listOf("√16 = 4", "√2 × √8 = 4", "√12 = 2√3"),
            keywords = listOf("karekök", "tam kare", "köklü ifade", "sadeleştirme")
        ),
        RagKazanim(
            kod = "MAT.8.4.1",
            ders = "Matematik",
            unite = "Veri Analizi",
            konu = "Merkezi Eğilim ve Histogram",
            aciklama = "Ortalama, ortanca, tepe değerini hesaplar, histogram yorumlar.",
            ornekler = listOf("Aritmetik ortalama", "Ortanca (medyan)", "Histogram"),
            keywords = listOf("ortalama", "ortanca", "tepe değer", "histogram", "veri")
        ),
        RagKazanim(
            kod = "MAT.8.5.1",
            ders = "Matematik",
            unite = "Olasılık",
            konu = "Basit Olayların Olasılığı",
            aciklama = "Basit olayların olasılığını hesaplar.",
            ornekler = listOf("P(A) = olumlu/toplam", "Zar atma", "Para atma"),
            keywords = listOf("olasılık", "olay", "deney", "sonuç", "olumlu")
        ),
        RagKazanim(
            kod = "MAT.8.6.1",
            ders = "Matematik",
            unite = "Cebirsel İfadeler ve Özdeşlikler",
            konu = "Özdeşlikler",
            aciklama = "Toplam-fark kareleri ve karelerin farkı özdeşliklerini uygular.",
            ornekler = listOf("(a+b)² = a²+2ab+b²", "(a-b)² = a²-2ab+b²", "a²-b² = (a-b)(a+b)"),
            keywords = listOf("özdeşlik", "kare", "toplam", "fark", "çarpanlara ayırma")
        ),
        RagKazanim(
            kod = "MAT.8.7.1",
            ders = "Matematik",
            unite = "Doğrusal Denklemler",
            konu = "Eğim ve Doğru Denklemi",
            aciklama = "Doğrusal ilişkiyi grafik üzerinde gösterir, eğim ve denklem hesaplar.",
            ornekler = listOf("y = mx + n", "Eğim = tan α", "Koordinat düzlemi"),
            keywords = listOf("doğru", "denklem", "eğim", "grafik", "koordinat")
        ),
        RagKazanim(
            kod = "MAT.8.8.1",
            ders = "Matematik",
            unite = "Eşitsizlikler",
            konu = "Birinci Dereceden Eşitsizlikler",
            aciklama = "Birinci dereceden eşitsizlikleri çözer.",
            ornekler = listOf("2x + 3 > 5", "x < 1", "Sayı doğrusunda gösterim"),
            keywords = listOf("eşitsizlik", "küçük", "büyük", "çözüm kümesi")
        ),
        RagKazanim(
            kod = "MAT.8.9.1",
            ders = "Matematik",
            unite = "Üçgenler",
            konu = "Pisagor ve Üçgen Özellikleri",
            aciklama = "Pisagor bağıntısını uygular, kenarortay ve açıortay çizer.",
            ornekler = listOf("a² + b² = c²", "Kenarortay", "Açıortay", "Yükseklik"),
            keywords = listOf("Pisagor", "üçgen", "kenarortay", "açıortay", "yükseklik", "hipotenüs")
        ),
        RagKazanim(
            kod = "MAT.8.10.1",
            ders = "Matematik",
            unite = "Eşlik ve Benzerlik",
            konu = "Eş ve Benzer Üçgenler",
            aciklama = "Eş ve benzer üçgenleri tanır, orantı ilişkilerini kullanır.",
            ornekler = listOf("Kenar-kenar-kenar eşliği", "Benzerlik oranı", "k² alan oranı"),
            keywords = listOf("eşlik", "benzerlik", "oran", "üçgen", "orantı")
        ),
        RagKazanim(
            kod = "MAT.8.11.1",
            ders = "Matematik",
            unite = "Dönüşüm Geometrisi",
            konu = "Öteleme ve Yansıma",
            aciklama = "Öteleme ve yansıma dönüşümlerini uygular.",
            ornekler = listOf("Öteleme vektörü", "Yansıma ekseni", "Koordinat dönüşümü"),
            keywords = listOf("öteleme", "yansıma", "dönüşüm", "koordinat")
        ),
        RagKazanim(
            kod = "MAT.8.12.1",
            ders = "Matematik",
            unite = "Geometrik Cisimler",
            konu = "Prizma, Silindir, Piramit, Koni",
            aciklama = "Geometrik cisimlerin yüzey alanı ve hacmini hesaplar.",
            ornekler = listOf("Silindir hacmi = πr²h", "Koni hacmi = πr²h/3"),
            keywords = listOf("prizma", "silindir", "piramit", "koni", "hacim", "yüzey alanı")
        )
    )

    // ==================== FEN BİLİMLERİ (LGS) ====================
    
    val fenBilimleri = listOf(
        RagKazanim(
            kod = "F.8.1.1",
            ders = "Fen Bilimleri",
            unite = "Mevsimler ve İklim",
            konu = "Mevsimlerin Oluşumu",
            aciklama = "Mevsimlerin oluşumunu, iklim ve hava hareketlerini açıklar.",
            ornekler = listOf("Yer ekseninin eğimi", "Mevsim döngüsü", "İklim türleri"),
            keywords = listOf("mevsim", "iklim", "hava", "eksen", "güneş")
        ),
        RagKazanim(
            kod = "F.8.2.1",
            ders = "Fen Bilimleri",
            unite = "DNA ve Genetik Kod",
            konu = "DNA, Kalıtım ve Biyoteknoloji",
            aciklama = "DNA yapısını, kalıtımı, mutasyon ve biyoteknolojiyi açıklar.",
            ornekler = listOf("DNA çift sarmal", "Gen ve kromozom", "Mutasyon", "GDO"),
            keywords = listOf("DNA", "gen", "kalıtım", "mutasyon", "biyoteknoloji", "kromozom")
        ),
        RagKazanim(
            kod = "F.8.3.1",
            ders = "Fen Bilimleri",
            unite = "Basınç",
            konu = "Katı, Sıvı ve Gaz Basıncı",
            aciklama = "Katı, sıvı ve gaz basıncını hesaplar ve açıklar.",
            ornekler = listOf("P = F/A", "Sıvı basıncı = d.g.h", "Atmosfer basıncı"),
            keywords = listOf("basınç", "kuvvet", "alan", "sıvı", "gaz", "atmosfer")
        ),
        RagKazanim(
            kod = "F.8.4.1",
            ders = "Fen Bilimleri",
            unite = "Madde ve Endüstri",
            konu = "Periyodik Sistem ve Kimyasal Tepkimeler",
            aciklama = "Periyodik sistemi, fiziksel-kimyasal değişimleri ve asit-bazları açıklar.",
            ornekler = listOf("Periyodik tablo", "Yanma tepkimesi", "pH", "Asit-baz"),
            keywords = listOf("periyodik", "element", "tepkime", "asit", "baz", "kimyasal")
        ),
        RagKazanim(
            kod = "F.8.5.1",
            ders = "Fen Bilimleri",
            unite = "Basit Makineler",
            konu = "Kaldıraç, Makara ve Dişli",
            aciklama = "Basit makinelerin çalışma ilkelerini açıklar.",
            ornekler = listOf("1-2-3. tür kaldıraç", "Sabit/hareketli makara", "Dişli çark"),
            keywords = listOf("kaldıraç", "makara", "dişli", "eğik düzlem", "basit makine")
        ),
        RagKazanim(
            kod = "F.8.6.1",
            ders = "Fen Bilimleri",
            unite = "Enerji ve Çevre",
            konu = "Fotosentez, Solunum ve Madde Döngüleri",
            aciklama = "Besin zinciri, fotosentez ve madde döngülerini açıklar.",
            ornekler = listOf("Besin zinciri", "Karbon döngüsü", "Su döngüsü", "Azot döngüsü"),
            keywords = listOf("fotosentez", "solunum", "besin zinciri", "döngü", "enerji")
        ),
        RagKazanim(
            kod = "F.8.7.1",
            ders = "Fen Bilimleri",
            unite = "Elektrik",
            konu = "Elektrik Yükleri ve Enerji Dönüşümü",
            aciklama = "Elektrik yüklerini, elektriklenmeyi ve enerji dönüşümünü açıklar.",
            ornekler = listOf("Pozitif/negatif yük", "Elektriklenme türleri", "Elektrik enerjisi"),
            keywords = listOf("elektrik", "yük", "elektriklenme", "enerji", "dönüşüm")
        )
    )

    // ==================== T.C. İNKILAP TARİHİ ====================
    
    val inkilapTarihi = listOf(
        RagKazanim(
            kod = "IT.8.1.1",
            ders = "İnkılap Tarihi",
            unite = "Bir Kahraman Doğuyor",
            konu = "Mustafa Kemal'in Hayatı",
            aciklama = "Avrupa'daki gelişmeleri ve Mustafa Kemal'in yetiştiğini açıklar.",
            ornekler = listOf("Fransız İhtilali", "Selanik", "Askerlik eğitimi"),
            keywords = listOf("Mustafa Kemal", "Atatürk", "Selanik", "doğum", "eğitim")
        ),
        RagKazanim(
            kod = "IT.8.2.1",
            ders = "İnkılap Tarihi",
            unite = "Milli Uyanış",
            konu = "I. Dünya Savaşı ve Milli Mücadele",
            aciklama = "I. Dünya Savaşı, cemiyetler ve TBMM'nin açılışını açıklar.",
            ornekler = listOf("Mondros", "Sivas Kongresi", "TBMM açılışı 23 Nisan 1920"),
            keywords = listOf("milli mücadele", "kongre", "TBMM", "Kuvayı Milliye")
        ),
        RagKazanim(
            kod = "IT.8.3.1",
            ders = "İnkılap Tarihi",
            unite = "Ya İstiklal Ya Ölüm",
            konu = "Cepheler ve Zaferler",
            aciklama = "Kurtuluş Savaşı cephelerini ve zaferleri açıklar.",
            ornekler = listOf("Sakarya Meydan Muharebesi", "Büyük Taarruz", "Mudanya"),
            keywords = listOf("cephe", "zafer", "Sakarya", "Büyük Taarruz", "Lozan")
        ),
        RagKazanim(
            kod = "IT.8.4.1",
            ders = "İnkılap Tarihi",
            unite = "Atatürkçülük",
            konu = "Atatürk İlkeleri ve İnkılaplar",
            aciklama = "Atatürk ilkelerini ve yapılan inkılapları kavrar.",
            ornekler = listOf("Cumhuriyetçilik", "Milliyetçilik", "Halkçılık", "Devletçilik"),
            keywords = listOf("ilke", "inkılap", "cumhuriyetçilik", "laiklik", "devrimler")
        ),
        RagKazanim(
            kod = "IT.8.6.1",
            ders = "İnkılap Tarihi",
            unite = "Dış Politika",
            konu = "Atatürk Dönemi Dış Politikası",
            aciklama = "Atatürk dönemindeki dış politika gelişmelerini açıklar.",
            ornekler = listOf("Yabancı okullar", "Montrö", "Hatay'ın Anavatana katılışı"),
            keywords = listOf("dış politika", "Montrö", "Hatay", "barış")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "E.8.1",
            ders = "İngilizce",
            unite = "Friendship",
            konu = "Inviting and Responding",
            aciklama = "Davet eder, kabul veya ret ifade eder.",
            ornekler = listOf("Would you like to...?", "I'd love to", "Sorry, I can't"),
            keywords = listOf("friendship", "invite", "accept", "refuse")
        ),
        RagKazanim(
            kod = "E.8.3",
            ders = "İngilizce",
            unite = "In The Kitchen",
            konu = "Recipes and Processes",
            aciklama = "Tarif ve süreç anlatımı yapar.",
            ornekler = listOf("First, add...", "Then, mix...", "Finally, cook..."),
            keywords = listOf("recipe", "cooking", "process", "instructions")
        ),
        RagKazanim(
            kod = "E.8.5",
            ders = "İngilizce",
            unite = "The Internet",
            konu = "Internet Safety",
            aciklama = "İnternet kullanımı ve güvenliğini ifade eder.",
            ornekler = listOf("You should protect your password", "Don't share personal info"),
            keywords = listOf("internet", "safety", "online", "password")
        ),
        RagKazanim(
            kod = "E.8.10",
            ders = "İngilizce",
            unite = "Natural Forces",
            konu = "Natural Disasters and Predictions",
            aciklama = "Doğal afetler ve tahminler hakkında konuşur.",
            ornekler = listOf("There will be an earthquake", "It might rain tomorrow"),
            keywords = listOf("natural", "disaster", "earthquake", "prediction")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val dinKulturu = listOf(
        RagKazanim(
            kod = "DK.8.1.1",
            ders = "Din Kültürü",
            unite = "Kader İnancı",
            konu = "Kader ve İnsan İradesi",
            aciklama = "Kader, kaza ve insanın iradesini açıklar.",
            ornekler = listOf("Kader: Allah'ın bilgisi", "Kaza: gerçekleşme", "İrade: seçim"),
            keywords = listOf("kader", "kaza", "irade", "iman", "tevekkül")
        ),
        RagKazanim(
            kod = "DK.8.2.1",
            ders = "Din Kültürü",
            unite = "Zekat ve Sadaka",
            konu = "Paylaşma ve Yardımlaşma",
            aciklama = "İslam'ın paylaşma ve yardımlaşmaya verdiği önemi açıklar.",
            ornekler = listOf("Zekat", "Sadaka", "İnfak", "Maun Suresi"),
            keywords = listOf("zekat", "sadaka", "yardım", "paylaşım")
        ),
        RagKazanim(
            kod = "DK.8.4.1",
            ders = "Din Kültürü",
            unite = "Hz. Muhammed'in Örnekliği",
            konu = "Peygamberin Ahlakı",
            aciklama = "Hz. Muhammed'in doğruluğunu, merhametini ve adaletini açıklar.",
            ornekler = listOf("Muhammedü'l-Emin", "Merhamet", "Adalet"),
            keywords = listOf("Hz. Muhammed", "ahlak", "örnek", "merhamet", "adalet")
        ),
        RagKazanim(
            kod = "DK.8.5.1",
            ders = "Din Kültürü",
            unite = "Kur'an-ı Kerim",
            konu = "İslam'ın Kaynakları",
            aciklama = "İslam dininin temel kaynaklarını ve Kur'an'ın konularını açıklar.",
            ornekler = listOf("Kur'an", "Sünnet", "İman, ibadet, ahlak konuları"),
            keywords = listOf("Kuran", "kaynak", "sünnet", "ibadet", "ahlak")
        )
    )

    fun tumKazanimlar(): List<RagKazanim> {
        return turkce + matematik + fenBilimleri + inkilapTarihi + ingilizce + dinKulturu
    }
}
