package com.example.bilgideham

/**
 * 9. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 * Lise 9. Sınıf - Genel Lise Müfredatı
 */
object Lise9Kazanimlari {

    // ==================== MATEMATİK ====================
    
    val matematik = listOf(
        // ==================== 1. ÜNİTE: SAYILAR ====================
        RagKazanim(
            kod = "M.9.1.1",
            ders = "Matematik",
            unite = "Sayılar",
            konu = "Üslü ve Köklü Gösterimler",
            aciklama = "Gerçek sayıların üslü ve köklü gösterimleri ile işlemler yapar.",
            ornekler = listOf("2³·2⁴=2⁷", "∛8=2", "a^(-n)=1/a^n", "√50=5√2"),
            keywords = listOf("üslü sayı", "köklü sayı", "üs kuralları", "rasyonelleştirme"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "M.9.1.2",
            ders = "Matematik",
            unite = "Sayılar",
            konu = "Sayı Aralıkları",
            aciklama = "Gerçek sayı aralıkları ile işlemler yapar, aralık gösterimini kullanır.",
            ornekler = listOf("[2,5]∩[3,7]=[3,5]", "(−∞,4]∪[2,∞)=R", "Açık ve kapalı aralıklar"),
            keywords = listOf("aralık", "birleşim", "kesişim", "açık aralık", "kapalı aralık"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.1.3",
            ders = "Matematik",
            unite = "Sayılar",
            konu = "Özdeşlikler",
            aciklama = "İki kare farkı ve tam kare özdeşliklerini kullanır.",
            ornekler = listOf("a²-b²=(a-b)(a+b)", "(a+b)²=a²+2ab+b²", "x²-9=(x-3)(x+3)"),
            keywords = listOf("özdeşlik", "tam kare", "iki kare farkı", "çarpanlara ayırma"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 2. ÜNİTE: NİCELİKLER VE DEĞİŞİMLER ====================
        RagKazanim(
            kod = "M.9.2.1",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Doğrusal Fonksiyonlar",
            aciklama = "Gerçek sayılarda tanımlı doğrusal fonksiyonları inceler.",
            ornekler = listOf("f(x)=2x+3", "y=mx+n grafiği", "Eğim hesabı", "x ve y kesişim noktaları"),
            keywords = listOf("doğrusal fonksiyon", "eğim", "grafik", "kesişim"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.2.2",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Mutlak Değer",
            aciklama = "Mutlak değer fonksiyonunu ve grafiğini inceler.",
            ornekler = listOf("|x-3|=5", "|2x+1|<7", "y=|x| grafiği", "Mutlak değerli denklemler"),
            keywords = listOf("mutlak değer", "denklem", "eşitsizlik", "grafik"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "M.9.2.3",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Denklem ve Eşitsizlikler",
            aciklama = "Doğrusal fonksiyonlarla ifade edilen denklem ve eşitsizlikleri çözer.",
            ornekler = listOf("3x-5=10 → x=5", "2x+1>7 → x>3", "Çözüm kümesi belirleme"),
            keywords = listOf("denklem", "eşitsizlik", "çözüm kümesi", "doğrusal"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 3. ÜNİTE: ALGORİTMA VE BİLİŞİM ====================
        RagKazanim(
            kod = "M.9.3.1",
            ders = "Matematik",
            unite = "Algoritma ve Bilişim",
            konu = "Algoritma Temelli Problemler",
            aciklama = "Algoritma temelli problemlerde adım adım çözüm üretir.",
            ornekler = listOf("Akış diyagramları", "Adım sayma", "Döngü problemleri", "Karar yapıları"),
            keywords = listOf("algoritma", "akış diyagramı", "adım", "döngü", "bilişimsel düşünme"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.3.2",
            ders = "Matematik",
            unite = "Algoritma ve Bilişim",
            konu = "Mantık Bağlaçları",
            aciklama = "Mantık bağlaçlarını (ve, veya, değil) ve niceleyicileri kullanır.",
            ornekler = listOf("p∧q (ve)", "p∨q (veya)", "¬p (değil)", "∀x, ∃x niceleyicileri"),
            keywords = listOf("mantık", "bağlaç", "niceleyici", "önerme", "doğruluk tablosu"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 4. ÜNİTE: GEOMETRİK ŞEKİLLER ====================
        RagKazanim(
            kod = "M.9.4.1",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Üçgende Açı Özellikleri",
            aciklama = "Üçgende iç açılar toplamı ve dış açı özelliklerini kullanır.",
            ornekler = listOf("İç açılar toplamı=180°", "Dış açı=karşı iki iç açı toplamı", "Açı hesaplama"),
            keywords = listOf("üçgen", "iç açı", "dış açı", "açı hesaplama"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "M.9.4.2",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Üçgende Kenar Özellikleri",
            aciklama = "Üçgende kenar-açı ilişkilerini ve üçgen eşitsizliğini kullanır.",
            ornekler = listOf("|a-b|<c<a+b", "Büyük açının karşısında büyük kenar", "Kenar sıralaması"),
            keywords = listOf("üçgen eşitsizliği", "kenar", "açı-kenar ilişkisi"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 5. ÜNİTE: EŞLİK VE BENZERLİK ====================
        RagKazanim(
            kod = "M.9.5.1",
            ders = "Matematik",
            unite = "Eşlik ve Benzerlik",
            konu = "Geometrik Dönüşümler",
            aciklama = "Yansıma, öteleme ve dönme dönüşümlerini uygular.",
            ornekler = listOf("x eksenine göre yansıma", "3 birim sağa öteleme", "90° döndürme"),
            keywords = listOf("yansıma", "öteleme", "dönme", "dönüşüm", "simetri"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.5.2",
            ders = "Matematik",
            unite = "Eşlik ve Benzerlik",
            konu = "Üçgenlerde Eşlik",
            aciklama = "Üçgenlerde eşlik koşullarını (KAK, AKA, KKK) kullanır.",
            ornekler = listOf("KAK: İki kenar ve aralarındaki açı eşit", "AKA koşulu", "KKK koşulu"),
            keywords = listOf("eşlik", "KAK", "AKA", "KKK", "eş üçgenler"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.5.3",
            ders = "Matematik",
            unite = "Eşlik ve Benzerlik",
            konu = "Üçgenlerde Benzerlik",
            aciklama = "Benzer üçgenlerin özelliklerini ve benzerlik oranını kullanır.",
            ornekler = listOf("AA benzerliği", "Benzerlik oranı k", "Alan oranı=k²", "Tales teoremi"),
            keywords = listOf("benzerlik", "oran", "tales", "benzer üçgenler"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 6. ÜNİTE: İSTATİSTİKSEL ARAŞTIRMA ====================
        RagKazanim(
            kod = "M.9.6.1",
            ders = "Matematik",
            unite = "İstatistiksel Araştırma Süreci",
            konu = "İstatistiksel Problem Oluşturma",
            aciklama = "Tek nicel değişken içeren istatistiksel problem oluşturur.",
            ornekler = listOf("Sınıf boy ortalaması", "Sınav not dağılımı", "Araştırma sorusu yazma"),
            keywords = listOf("istatistik", "değişken", "araştırma", "veri toplama"),
            zorlukSeviyesi = "Kolay"
        ),
        // ==================== 7. ÜNİTE: VERİDEN OLASILIGA ====================
        RagKazanim(
            kod = "M.9.7.1",
            ders = "Matematik",
            unite = "Veriden Olasılığa",
            konu = "Veri Toplama ve Analiz",
            aciklama = "Verileri toplar, düzenler ve analiz eder.",
            ornekler = listOf("Frekans tablosu", "Çubuk grafik", "Histogram", "Ortalama hesaplama"),
            keywords = listOf("veri", "frekans", "grafik", "analiz", "ortalama"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "M.9.7.2",
            ders = "Matematik",
            unite = "Veriden Olasılığa",
            konu = "Olasılık",
            aciklama = "Basit olayların olasılığını hesaplar ve yorumlar.",
            ornekler = listOf("P(A)=n(A)/n(S)", "Zar atma olasılıkları", "Kart çekme problemleri"),
            keywords = listOf("olasılık", "örnek uzay", "olay", "klasik olasılık"),
            zorlukSeviyesi = "Orta"
        )
    )

    // ==================== FİZİK ====================
    
    val fizik = listOf(
        // ==================== 1. ÜNİTE: FİZİK BİLİMİ VE KARİYER KEŞFİ ====================
        RagKazanim(
            kod = "F.9.1.1",
            ders = "Fizik",
            unite = "Fizik Bilimi ve Kariyer Keşfi",
            konu = "Fizik Bilimi",
            aciklama = "Fizik biliminin alt dallarını ve diğer bilimlerle ilişkisini açıklar.",
            ornekler = listOf("Mekanik", "Termodinamik", "Elektromanyetizma", "Optik", "Kuantum fiziği"),
            keywords = listOf("fizik", "bilim dalları", "doğa bilimleri", "fiziksel olaylar"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "F.9.1.2",
            ders = "Fizik",
            unite = "Fizik Bilimi ve Kariyer Keşfi",
            konu = "Fizik Bilimine Yön Verenler",
            aciklama = "Önemli fizikçilerin bilime katkılarını tanır.",
            ornekler = listOf("Newton", "Einstein", "Galileo", "Tesla", "Marie Curie"),
            keywords = listOf("bilim insanı", "fizikçi", "keşif", "buluş"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "F.9.1.3",
            ders = "Fizik",
            unite = "Fizik Bilimi ve Kariyer Keşfi",
            konu = "Kariyer Keşfi",
            aciklama = "Fizikle ilgili meslekleri ve kariyer olanaklarını araştırır.",
            ornekler = listOf("Mühendislik", "Tıp fiziği", "Astronomi", "Araştırmacı", "Öğretmenlik"),
            keywords = listOf("kariyer", "meslek", "fizik alanları", "iş olanakları"),
            zorlukSeviyesi = "Kolay"
        ),
        // ==================== 2. ÜNİTE: KUVVET VE HAREKET ====================
        RagKazanim(
            kod = "F.9.2.1",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Temel ve Türetilmiş Nicelikler",
            aciklama = "Temel ve türetilmiş fiziksel nicelikleri ayırt eder.",
            ornekler = listOf("Uzunluk (m)", "Kütle (kg)", "Zaman (s)", "Hız (m/s)", "İvme (m/s²)"),
            keywords = listOf("nicelik", "birim", "SI sistemi", "ölçme"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "F.9.2.2",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Skaler ve Vektörel Nicelikler",
            aciklama = "Skaler ve vektörel nicelikleri ayırt eder ve vektör işlemleri yapar.",
            ornekler = listOf("Kütle (skaler)", "Kuvvet (vektörel)", "Vektör toplama", "Bileşen ayırma"),
            keywords = listOf("skaler", "vektör", "bileşen", "doğrultu", "yön"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.2.3",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Doğadaki Temel Kuvvetler",
            aciklama = "Doğadaki dört temel kuvveti tanır.",
            ornekler = listOf("Kütle çekim", "Elektromanyetik", "Güçlü nükleer", "Zayıf nükleer"),
            keywords = listOf("temel kuvvet", "çekim", "elektromanyetik", "nükleer"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.2.4",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Hareket Türleri",
            aciklama = "Düzgün doğrusal hareket ve düzgün değişen hareketi analiz eder.",
            ornekler = listOf("Konum-zaman grafiği", "Hız-zaman grafiği", "v=s/t", "a=Δv/Δt"),
            keywords = listOf("hareket", "hız", "ivme", "grafik", "düzgün hareket"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 3. ÜNİTE: AKIŞKANLAR ====================
        RagKazanim(
            kod = "F.9.3.1",
            ders = "Fizik",
            unite = "Akışkanlar",
            konu = "Basınç",
            aciklama = "Katı, sıvı ve gaz basıncını hesaplar ve uygulamalarını açıklar.",
            ornekler = listOf("P=F/A", "Hidrostatik basınç: P=ρgh", "Pascal prensibi"),
            keywords = listOf("basınç", "pascal", "hidrostatik", "akışkan"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.3.2",
            ders = "Fizik",
            unite = "Akışkanlar",
            konu = "Sıvılarda Basınç Uygulamaları",
            aciklama = "Hidrolik sistemlerin çalışma prensibini açıklar.",
            ornekler = listOf("Hidrolik kriko", "Hidrolik fren", "Su cenderesi"),
            keywords = listOf("hidrolik", "pascal prensibi", "basınç iletimi"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.3.3",
            ders = "Fizik",
            unite = "Akışkanlar",
            konu = "Açık Hava Basıncı",
            aciklama = "Atmosfer basıncını ve ölçümünü açıklar.",
            ornekler = listOf("1 atm = 101325 Pa", "Barometre", "Yükseklik-basınç ilişkisi"),
            keywords = listOf("atmosfer", "barometre", "hava basıncı"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "F.9.3.4",
            ders = "Fizik",
            unite = "Akışkanlar",
            konu = "Kaldırma Kuvveti",
            aciklama = "Arşimet prensibini kullanarak kaldırma kuvveti problemleri çözer.",
            ornekler = listOf("Fk=ρ·V·g", "Batma-yüzme koşulları", "Özkütle karşılaştırma"),
            keywords = listOf("arşimet", "kaldırma kuvveti", "yoğunluk", "yüzme"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "F.9.3.5",
            ders = "Fizik",
            unite = "Akışkanlar",
            konu = "Bernoulli İlkesi",
            aciklama = "Bernoulli ilkesini ve uygulamalarını açıklar.",
            ornekler = listOf("Uçak kanadı", "Püskürtücü", "Hız-basınç ilişkisi"),
            keywords = listOf("bernoulli", "akış hızı", "basınç", "aerodinamik"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 4. ÜNİTE: ENERJİ ====================
        RagKazanim(
            kod = "F.9.4.1",
            ders = "Fizik",
            unite = "Enerji",
            konu = "İç Enerji, Isı ve Sıcaklık",
            aciklama = "İç enerji, ısı ve sıcaklık kavramlarını ayırt eder.",
            ornekler = listOf("Moleküler kinetik enerji", "Isı aktarımı", "Termometre"),
            keywords = listOf("iç enerji", "ısı", "sıcaklık", "molekül hareketi"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.4.2",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Isı ve Sıcaklık İlişkisi",
            aciklama = "Öz ısı ve ısı sığası kavramlarını kullanarak hesaplamalar yapar.",
            ornekler = listOf("Q=m·c·ΔT", "Öz ısı tablosu", "Isı sığası: C=m·c"),
            keywords = listOf("öz ısı", "ısı sığası", "ısı hesabı", "kalori"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.4.3",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Hal Değişimi",
            aciklama = "Maddelerin hal değişimlerini ve gizli ısıyı açıklar.",
            ornekler = listOf("Erime", "Buharlaşma", "Süblimleşme", "Q=m·L"),
            keywords = listOf("hal değişimi", "erime", "kaynama", "gizli ısı"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.4.4",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Isıl Denge",
            aciklama = "Isıl denge koşullarını ve hesaplamalarını yapar.",
            ornekler = listOf("Qverilen=Qalınan", "Son sıcaklık hesabı", "Karışım problemleri"),
            keywords = listOf("ısıl denge", "karışım", "sıcaklık eşitlenmesi"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "F.9.4.5",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Isı Aktarım Yolları",
            aciklama = "İletim, konveksiyon ve ışıma ile ısı aktarımını açıklar.",
            ornekler = listOf("İletim: metal çubuk", "Konveksiyon: kalorifer", "Işıma: güneş"),
            keywords = listOf("iletim", "konveksiyon", "ışıma", "ısı transferi"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "F.9.4.6",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Isı İletim Hızı",
            aciklama = "Isı iletim hızını etkileyen faktörleri analiz eder.",
            ornekler = listOf("K katsayısı", "Kesit alanı", "Uzunluk", "Sıcaklık farkı"),
            keywords = listOf("ısı iletim hızı", "iletkenlik", "yalıtım"),
            zorlukSeviyesi = "Zor"
        )
    )

    // ==================== KİMYA ====================
    
    val kimya = listOf(
        // ==================== 1. TEMA: ETKİLEŞİM ====================
        // Kimya Hayattır
        RagKazanim(
            kod = "K.9.1.1",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Kimya Hayattır",
            aciklama = "Günlük hayatta kimyanın yerini ve önemini açıklar.",
            ornekler = listOf("Temizlik ürünleri", "Gıda katkı maddeleri", "İlaçlar", "Kozmetikler"),
            keywords = listOf("kimya", "günlük hayat", "kimyasal madde", "güvenlik"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "K.9.1.2",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Kimyanın Alt Disiplinleri",
            aciklama = "Kimyanın alt dallarını ve kariyer olanaklarını tanır.",
            ornekler = listOf("Analitik", "Organik", "Anorganik", "Biyokimya", "Fizikokimya"),
            keywords = listOf("kimya dalları", "kariyer", "kimyacı", "araştırma"),
            zorlukSeviyesi = "Kolay"
        ),
        // Atomdan Periyodik Tabloya
        RagKazanim(
            kod = "K.9.1.3",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Atom Teorileri",
            aciklama = "Tarihsel atom modellerini ve modern atom teorisini açıklar.",
            ornekler = listOf("Dalton modeli", "Thomson modeli", "Rutherford", "Bohr modeli"),
            keywords = listOf("atom", "model", "çekirdek", "elektron"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.1.4",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Atomun Yapısı",
            aciklama = "Atomun temel taneciklerini ve elektron dizilimini bilir.",
            ornekler = listOf("Proton (+)", "Nötron (0)", "Elektron (-)", "Kütle numarası", "Atom numarası"),
            keywords = listOf("proton", "nötron", "elektron", "orbital", "enerji düzeyi"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.1.5",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Periyodik Tabloda Yer Bulma",
            aciklama = "Elementlerin periyodik tablodaki yerini belirler.",
            ornekler = listOf("Grup ve periyot belirleme", "Elektron dizilimi", "s-p-d-f blokları"),
            keywords = listOf("periyodik tablo", "grup", "periyot", "element"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.1.6",
            ders = "Kimya",
            unite = "Etkileşim",
            konu = "Periyodik Özellikler",
            aciklama = "Elementlerin periyodik özelliklerindeki değişimi açıklar.",
            ornekler = listOf("Atom yarıçapı", "İyonlaşma enerjisi", "Elektron ilgisi", "Elektronegatiflik"),
            keywords = listOf("periyodik özellik", "yarıçap", "iyonlaşma", "elektronegatiflik"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 2. TEMA: ÇEŞİTLİLİK ====================
        // Etkileşimler
        RagKazanim(
            kod = "K.9.2.1",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "Metalik Bağ",
            aciklama = "Metallerdeki bağ yapısını ve özelliklerini açıklar.",
            ornekler = listOf("Elektron denizi", "İletkenlik", "Süneklik", "Parlaklık"),
            keywords = listOf("metalik bağ", "delokalize elektron", "metal özellikleri"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.2.2",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "İyonik Bağ",
            aciklama = "İyonik bileşiklerin oluşumunu ve özelliklerini bilir.",
            ornekler = listOf("NaCl", "MgO", "CaF₂", "Elektron alışverişi"),
            keywords = listOf("iyonik bağ", "katyon", "anyon", "kristal yapı"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.2.3",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "Kovalent Bağ",
            aciklama = "Kovalent bileşiklerin oluşumunu ve Lewis yapısını çizer.",
            ornekler = listOf("H₂O", "CO₂", "CH₄", "Elektron paylaşımı"),
            keywords = listOf("kovalent bağ", "lewis yapısı", "polar", "apolar"),
            zorlukSeviyesi = "Zor"
        ),
        // Etkileşimden Maddeye
        RagKazanim(
            kod = "K.9.2.4",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "Moleküller Arası Etkileşimler",
            aciklama = "Van der Waals kuvvetleri ve hidrojen bağını açıklar.",
            ornekler = listOf("London kuvvetleri", "Dipol-dipol", "Hidrojen bağı"),
            keywords = listOf("moleküller arası", "van der waals", "hidrojen bağı"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "K.9.2.5",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "Katıların Özellikleri",
            aciklama = "Katı maddelerin yapısını ve özelliklerini açıklar.",
            ornekler = listOf("Kristal yapı", "Amorf yapı", "Erime noktası", "Sertlik"),
            keywords = listOf("katı", "kristal", "amorf", "katı özellikleri"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.9.2.6",
            ders = "Kimya",
            unite = "Çeşitlilik",
            konu = "Sıvıların Özellikleri",
            aciklama = "Sıvı maddelerin özelliklerini moleküler düzeyde açıklar.",
            ornekler = listOf("Yüzey gerilimi", "Viskozite", "Buhar basıncı", "Kaynama noktası"),
            keywords = listOf("sıvı", "yüzey gerilimi", "viskozite", "buharlaşma"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 3. TEMA: SÜRDÜRÜLEBİLİRLİK ====================
        RagKazanim(
            kod = "K.9.3.1",
            ders = "Kimya",
            unite = "Sürdürülebilirlik",
            konu = "Nanoparçacıklar",
            aciklama = "Nanoparçacıkların özelliklerini ve uygulama alanlarını açıklar.",
            ornekler = listOf("Nano boyut (1-100 nm)", "Yüzey alanı artışı", "Tıpta kullanım", "Kataliz"),
            keywords = listOf("nanoparçacık", "nanoteknoloji", "boyut etkisi", "yüzey alanı"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "K.9.3.2",
            ders = "Kimya",
            unite = "Sürdürülebilirlik",
            konu = "Metal Nanoparçacıklar",
            aciklama = "Metal nanoparçacıkların özelliklerini ve çevresel etkilerini tartışır.",
            ornekler = listOf("Gümüş nanoparçacık", "Altın nanoparçacık", "Antibakteriyel etki"),
            keywords = listOf("metal nanoparçacık", "çevresel etki", "toksisite"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "K.9.3.3",
            ders = "Kimya",
            unite = "Sürdürülebilirlik",
            konu = "Yeşil Kimya",
            aciklama = "Yeşil kimyanın atık önleme ilkesini ve sürdürülebilirliği açıklar.",
            ornekler = listOf("Atık azaltma", "Enerji verimliliği", "Çevre dostu çözücüler", "Geri dönüşüm"),
            keywords = listOf("yeşil kimya", "sürdürülebilirlik", "atık önleme", "çevre dostu"),
            zorlukSeviyesi = "Orta"
        )
    )

    // ==================== BİYOLOJİ ====================
    
    val biyoloji = listOf(
        // ==================== 1. ÜNİTE: YAŞAM ====================
        // Yaşam Bilimi: Biyoloji
        RagKazanim(
            kod = "B.9.1.1",
            ders = "Biyoloji",
            unite = "Yaşam",
            konu = "Biyolojinin Önemi",
            aciklama = "Biyoloji biliminin önemini ve günlük hayattaki yerini açıklar.",
            ornekler = listOf("Tıp", "Tarım", "Çevre", "Biyoteknoloji"),
            keywords = listOf("biyoloji", "yaşam bilimi", "canlı", "doğa"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "B.9.1.2",
            ders = "Biyoloji",
            unite = "Yaşam",
            konu = "Bilimsel Yöntem",
            aciklama = "Bilimsel araştırma süreçlerini ve bilim etiğini açıklar.",
            ornekler = listOf("Gözlem", "Hipotez", "Deney", "Sonuç", "Kontrollü deney"),
            keywords = listOf("bilimsel yöntem", "hipotez", "deney", "bilim etiği"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "B.9.1.3",
            ders = "Biyoloji",
            unite = "Yaşam",
            konu = "Canlıların Ortak Özellikleri",
            aciklama = "Tüm canlıların sahip olduğu ortak özellikleri bilir.",
            ornekler = listOf("Hücresel yapı", "Beslenme", "Solunum", "Boşaltım", "Üreme", "Hareket"),
            keywords = listOf("canlı", "ortak özellik", "metabolizma", "homeostazi"),
            zorlukSeviyesi = "Kolay"
        ),
        // Sınıflandırma ve Biyoçeşitlilik
        RagKazanim(
            kod = "B.9.1.4",
            ders = "Biyoloji",
            unite = "Yaşam",
            konu = "Sınıflandırma Sistemleri",
            aciklama = "Modern sınıflandırma sistemini ve üç üst alem (domain) sistemini açıklar.",
            ornekler = listOf("Bakteriler", "Arkeler", "Ökaryotlar", "Linne sınıflandırması"),
            keywords = listOf("sınıflandırma", "taksonomi", "domain", "alem"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "B.9.1.5",
            ders = "Biyoloji",
            unite = "Yaşam",
            konu = "Biyoçeşitlilik",
            aciklama = "Biyoçeşitliliğin önemini ve korunmasını tartışır.",
            ornekler = listOf("Tür çeşitliliği", "Genetik çeşitlilik", "Ekosistem çeşitliliği", "Endemik türler"),
            keywords = listOf("biyoçeşitlilik", "tür", "nesli tükenen", "koruma"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 2. ÜNİTE: ORGANİZASYON ====================
        // İnorganik Moleküller
        RagKazanim(
            kod = "B.9.2.1",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Su ve Mineraller",
            aciklama = "Canlılarda suyun ve minerallerin önemini açıklar.",
            ornekler = listOf("Suyun özellikleri", "Çözücü özellik", "Minerallerin görevleri"),
            keywords = listOf("su", "mineral", "inorganik", "çözücü"),
            zorlukSeviyesi = "Kolay"
        ),
        // Organik Moleküller
        RagKazanim(
            kod = "B.9.2.2",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Karbonhidratlar",
            aciklama = "Karbonhidratların yapısını, çeşitlerini ve işlevlerini bilir.",
            ornekler = listOf("Monosakkarit (glikoz)", "Disakkarit (sakaroz)", "Polisakkarit (nişasta)"),
            keywords = listOf("karbonhidrat", "şeker", "glikoz", "nişasta", "enerji"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "B.9.2.3",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Yağlar",
            aciklama = "Lipitlerin yapısını ve işlevlerini açıklar.",
            ornekler = listOf("Doymuş yağ", "Doymamış yağ", "Fosfolipit", "Steroit"),
            keywords = listOf("lipit", "yağ", "yağ asidi", "gliserol"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "B.9.2.4",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Proteinler",
            aciklama = "Proteinlerin yapısını ve işlevlerini bilir.",
            ornekler = listOf("Amino asit", "Peptit bağı", "Enzim", "Yapısal protein"),
            keywords = listOf("protein", "amino asit", "enzim", "peptit"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "B.9.2.5",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Nükleik Asitler ve Vitaminler",
            aciklama = "DNA, RNA ve vitaminlerin yapısını ve işlevlerini açıklar.",
            ornekler = listOf("DNA çift sarmal", "RNA türleri", "Vitaminler (A, B, C, D)"),
            keywords = listOf("nükleik asit", "DNA", "RNA", "vitamin"),
            zorlukSeviyesi = "Zor"
        ),
        // Hücrenin Organizasyonu
        RagKazanim(
            kod = "B.9.2.6",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Prokaryot ve Ökaryot Hücre",
            aciklama = "Prokaryot ve ökaryot hücreleri karşılaştırır.",
            ornekler = listOf("Bakteri (prokaryot)", "Bitki hücresi", "Hayvan hücresi", "Çekirdek zarı"),
            keywords = listOf("prokaryot", "ökaryot", "çekirdek", "hücre çeşitleri"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "B.9.2.7",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Hücre Zarı ve Sitoplazma",
            aciklama = "Hücre zarının yapısını ve sitoplazmanın özelliklerini açıklar.",
            ornekler = listOf("Akıcı mozaik model", "Fosfolipit çift tabaka", "Sitoplazma içeriği"),
            keywords = listOf("hücre zarı", "sitoplazma", "fosfolipit", "zar proteinleri"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "B.9.2.8",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Organeller",
            aciklama = "Hücre organellerinin yapısını ve görevlerini bilir.",
            ornekler = listOf("Mitokondri", "Kloroplast", "Ribozom", "ER", "Golgi", "Lizozom"),
            keywords = listOf("organel", "mitokondri", "kloroplast", "ribozom"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "B.9.2.9",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Hücre Zarından Madde Geçişleri",
            aciklama = "Pasif ve aktif taşıma yollarını açıklar.",
            ornekler = listOf("Difüzyon", "Osmoz", "Aktif taşıma", "Endositoz", "Ekzositoz"),
            keywords = listOf("difüzyon", "osmoz", "aktif taşıma", "pasif taşıma"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "B.9.2.10",
            ders = "Biyoloji",
            unite = "Organizasyon",
            konu = "Hücreden Organizmaya",
            aciklama = "Hücre-doku-organ-sistem-organizma organizasyon düzeylerini açıklar.",
            ornekler = listOf("Epitel doku", "Bağ dokusu", "Sinir sistemi", "Dolaşım sistemi"),
            keywords = listOf("doku", "organ", "sistem", "organizma", "organizasyon"),
            zorlukSeviyesi = "Orta"
        )
    )

    // ==================== TARİH ====================
    
    val tarih = listOf(
        // GEÇMİŞİN İNŞA SÜRECİNDE TARİH
        RagKazanim(
            kod = "T.9.1.1",
            ders = "Tarih",
            unite = "Geçmişin İnşa Sürecinde Tarih",
            konu = "Tarih Bilimi",
            aciklama = "Tarih biliminin konusunu, kaynaklarını ve yöntemlerini bilir.",
            ornekler = listOf("Yazılı kaynaklar", "Arkeolojik bulgular", "Sözlü tarih"),
            keywords = listOf("tarih", "kaynak", "belge", "arkeoloji", "kronoloji")
        ),
        // ESKİ ÇAĞ MEDENİYETLERİ
        RagKazanim(
            kod = "T.9.2.1",
            ders = "Tarih",
            unite = "Eski Çağ Medeniyetleri",
            konu = "İlk Uygarlıklar",
            aciklama = "Mezopotamya, Mısır, Anadolu uygarlıklarını tanır.",
            ornekler = listOf("Sümerler", "Hititler", "Frigler", "Lidyalılar"),
            keywords = listOf("uygarlık", "mezopotamya", "mısır", "anadolu", "eski çağ")
        ),
        RagKazanim(
            kod = "T.9.2.2",
            ders = "Tarih",
            unite = "Eski Çağ Medeniyetleri",
            konu = "Yunan ve Roma",
            aciklama = "Yunan ve Roma medeniyetlerinin özelliklerini bilir.",
            ornekler = listOf("Atina demokrasisi", "Sparta", "Roma İmparatorluğu"),
            keywords = listOf("yunan", "roma", "demokrasi", "imparatorluk")
        ),
        // ORTA ÇAĞ MEDENİYETLERİ
        RagKazanim(
            kod = "T.9.3.1",
            ders = "Tarih",
            unite = "Orta Çağ Medeniyetleri",
            konu = "İslam Medeniyeti",
            aciklama = "İslam medeniyetinin doğuşunu ve yayılışını açıklar.",
            ornekler = listOf("Hz. Muhammed", "Dört Halife", "Emeviler", "Abbasiler"),
            keywords = listOf("islam", "medeniyet", "halife", "emevi", "abbasi")
        ),
        RagKazanim(
            kod = "T.9.3.2",
            ders = "Tarih",
            unite = "Orta Çağ Medeniyetleri",
            konu = "Türklerin İslamiyet'i Kabulü",
            aciklama = "Türklerin İslamiyet'i kabul sürecini ve etkilerini değerlendirir.",
            ornekler = listOf("Talas Savaşı", "Karahanlılar", "Gazneliler", "Selçuklular"),
            keywords = listOf("türk", "islamiyet", "karahanlı", "selçuklu")
        )
    )

    // ==================== COĞRAFYA ====================
    
    val cografya = listOf(
        // COĞRAFYANIN DOĞASI
        RagKazanim(
            kod = "C.9.1.1",
            ders = "Coğrafya",
            unite = "Coğrafyanın Doğası",
            konu = "Coğrafya Bilimi",
            aciklama = "Coğrafyanın konusunu, alt dallarını ve yöntemlerini bilir.",
            ornekler = listOf("Fiziki coğrafya", "Beşeri coğrafya", "Bölgesel coğrafya"),
            keywords = listOf("coğrafya", "fiziki", "beşeri", "bölge")
        ),
        // MEKANSAL BİLGİ TEKNOLOJİLERİ
        RagKazanim(
            kod = "C.9.2.1",
            ders = "Coğrafya",
            unite = "Mekânsal Bilgi Teknolojileri",
            konu = "Harita ve Koordinat Sistemi",
            aciklama = "Harita okuma ve koordinat sistemini kullanma becerisi kazanır.",
            ornekler = listOf("Enlem-boylam", "Ölçek", "Yön", "GPS"),
            keywords = listOf("harita", "koordinat", "enlem", "boylam", "GPS")
        ),
        // DOĞAL SİSTEMLER VE SÜREÇLER
        RagKazanim(
            kod = "C.9.3.1",
            ders = "Coğrafya",
            unite = "Doğal Sistemler ve Süreçler",
            konu = "Yer Şekilleri",
            aciklama = "Dünyanın iç ve dış kuvvetlerle şekillenmesini açıklar.",
            ornekler = listOf("Dağlar", "Ovalar", "Platolar", "Volkanlar", "Depremler"),
            keywords = listOf("yer şekli", "dağ", "ova", "volkan", "deprem")
        ),
        RagKazanim(
            kod = "C.9.3.2",
            ders = "Coğrafya",
            unite = "Doğal Sistemler ve Süreçler",
            konu = "İklim",
            aciklama = "İklim elemanlarını ve iklim tiplerini bilir.",
            ornekler = listOf("Sıcaklık", "Yağış", "Basınç", "Rüzgar", "İklim kuşakları"),
            keywords = listOf("iklim", "sıcaklık", "yağış", "iklim kuşağı")
        ),
        // BEŞERİ SİSTEMLER VE SÜREÇLER
        RagKazanim(
            kod = "C.9.4.1",
            ders = "Coğrafya",
            unite = "Beşerî Sistemler ve Süreçler",
            konu = "Nüfus",
            aciklama = "Nüfus dağılışını etkileyen faktörleri analiz eder.",
            ornekler = listOf("Nüfus yoğunluğu", "Göç", "Kentleşme"),
            keywords = listOf("nüfus", "göç", "kentleşme", "nüfus yoğunluğu")
        )
    )

    // ==================== TÜRK DİLİ VE EDEBİYATI ====================
    
    val turk_dili = listOf(
        RagKazanim(
            kod = "TDE.9.1.1",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Dil ve Anlatım",
            konu = "Dil Bilgisi Temelleri",
            aciklama = "Türkçenin ses, şekil ve cümle yapısını bilir.",
            ornekler = listOf("Ses olayları", "Ek", "Cümle öğeleri"),
            keywords = listOf("dil bilgisi", "ses", "ek", "cümle")
        ),
        RagKazanim(
            kod = "TDE.9.1.2",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Edebiyat Tarihi",
            aciklama = "İslam öncesi Türk edebiyatını ve İslamiyet sonrası gelişmeleri bilir.",
            ornekler = listOf("Destan", "Koşuk", "Divan edebiyatı", "Halk edebiyatı"),
            keywords = listOf("edebiyat", "destan", "divan", "halk edebiyatı")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "İ.9.1.1",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Daily Routines and Habits",
            aciklama = "Günlük rutinler ve alışkanlıklar hakkında konuşur.",
            ornekler = listOf("I wake up at 7 am", "She goes to school by bus"),
            keywords = listOf("daily routine", "habits", "present simple", "frequency adverbs")
        ),
        RagKazanim(
            kod = "İ.9.1.2",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Making Plans",
            aciklama = "Gelecek planları hakkında konuşur ve önerilerde bulunur.",
            ornekler = listOf("I'm going to visit my grandparents", "Let's go to the cinema"),
            keywords = listOf("future plans", "going to", "suggestions", "will")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val din_kulturu = listOf(
        RagKazanim(
            kod = "DK.9.1.1",
            ders = "Din Kültürü",
            unite = "Allah-İnsan İlişkisi",
            konu = "İnanç Esasları",
            aciklama = "İslam'ın temel inanç esaslarını bilir ve açıklar.",
            ornekler = listOf("Allah'a iman", "Meleklere iman", "Kitaplara iman"),
            keywords = listOf("iman", "inanç", "allah", "melek", "kitap")
        ),
        RagKazanim(
            kod = "DK.9.1.2",
            ders = "Din Kültürü",
            unite = "İbadetler",
            konu = "Namaz ve Oruç",
            aciklama = "Namaz ve oruç ibadetlerinin önemini ve uygulanışını bilir.",
            ornekler = listOf("Beş vakit namaz", "Ramazan orucu", "Abdest"),
            keywords = listOf("namaz", "oruç", "ibadet", "abdest")
        )
    )

    // ==================== FELSEFE ====================
    
    val felsefe = listOf(
        RagKazanim(
            kod = "F.9.1.1",
            ders = "Felsefe",
            unite = "Felsefenin Doğası",
            konu = "Felsefe Nedir?",
            aciklama = "Felsefenin konusunu, amacını ve yöntemini kavrar.",
            ornekler = listOf("Sorgulama", "Eleştirel düşünme", "Akıl yürütme"),
            keywords = listOf("felsefe", "düşünme", "sorgulama", "akıl")
        )
    )

    // Tüm dersleri birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fizik + kimya + biyoloji + tarih + cografya + 
               turk_dili + ingilizce + din_kulturu + felsefe
    }
}
