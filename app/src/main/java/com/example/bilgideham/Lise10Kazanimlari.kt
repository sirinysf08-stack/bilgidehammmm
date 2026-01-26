package com.example.bilgideham

/**
 * 10. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 * Lise 10. Sınıf - Genel Lise Müfredatı
 */
object Lise10Kazanimlari {

    // ==================== MATEMATİK ====================
    
    val matematik = listOf(
        RagKazanim(
            kod = "M.10.1.1",
            ders = "Matematik",
            unite = "Sayılar",
            konu = "Rasyonel Sayılar",
            aciklama = "Rasyonel sayılarla işlemler yapar ve problemlerde kullanır.",
            ornekler = listOf("Kesir işlemleri", "Ondalık gösterim", "Yüzde hesaplamaları"),
            keywords = listOf("rasyonel", "kesir", "ondalık", "yüzde")
        ),
        RagKazanim(
            kod = "M.10.2.1",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "İkinci Dereceden Denklemler",
            aciklama = "İkinci dereceden denklemleri çözer ve problemlerde uygular.",
            ornekler = listOf("ax² + bx + c = 0", "Diskriminant", "Kökler toplamı ve çarpımı"),
            keywords = listOf("ikinci derece", "denklem", "diskriminant", "kök")
        ),
        RagKazanim(
            kod = "M.10.2.2",
            ders = "Matematik",
            unite = "Nicelikler ve Değişimler",
            konu = "Fonksiyonlar",
            aciklama = "Fonksiyon kavramını derinleştirir, farklı fonksiyon türlerini inceler.",
            ornekler = listOf("Doğrusal fonksiyon", "Parabolik fonksiyon", "Fonksiyon grafikleri"),
            keywords = listOf("fonksiyon", "grafik", "doğrusal", "parabolik")
        ),
        RagKazanim(
            kod = "M.10.3.1",
            ders = "Matematik",
            unite = "Sayma, Algoritma ve Bilişim",
            konu = "Permütasyon ve Kombinasyon",
            aciklama = "Permütasyon ve kombinasyon kavramlarını kullanarak sayma problemleri çözer.",
            ornekler = listOf("n!", "P(n,r)", "C(n,r)", "Sıralama ve seçme problemleri"),
            keywords = listOf("permütasyon", "kombinasyon", "faktöriyel", "sayma")
        ),
        RagKazanim(
            kod = "M.10.4.1",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Trigonometri",
            aciklama = "Dik üçgende trigonometrik oranları kullanır.",
            ornekler = listOf("sin, cos, tan", "Özel açılar: 30°, 45°, 60°", "Trigonometrik problemler"),
            keywords = listOf("trigonometri", "sinüs", "kosinüs", "tanjant")
        ),
        RagKazanim(
            kod = "M.10.5.1",
            ders = "Matematik",
            unite = "Analitik İnceleme",
            konu = "Doğrunun Denklemi",
            aciklama = "Koordinat düzleminde doğrunun denklemini bulur ve yorumlar.",
            ornekler = listOf("y = mx + n", "Eğim", "Paralel ve dik doğrular"),
            keywords = listOf("doğru denklemi", "eğim", "koordinat", "analitik geometri")
        ),
        RagKazanim(
            kod = "M.10.6.1",
            ders = "Matematik",
            unite = "Veriden Olasılığa",
            konu = "Olasılık Hesaplamaları",
            aciklama = "Olasılık hesaplamalarını yapar ve yorumlar.",
            ornekler = listOf("Bağımsız olaylar", "Koşullu olasılık", "Beklenen değer"),
            keywords = listOf("olasılık", "bağımsız olay", "koşullu olasılık")
        )
    )

    // ==================== FİZİK ====================
    
    val fizik = listOf(
        RagKazanim(
            kod = "F.10.1.1",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "Vektörler",
            aciklama = "Vektörel büyüklükleri anlar ve vektör işlemleri yapar.",
            ornekler = listOf("Vektör toplama", "Bileşenlerine ayırma", "Skaler çarpım"),
            keywords = listOf("vektör", "bileşen", "toplama", "skaler")
        ),
        RagKazanim(
            kod = "F.10.1.2",
            ders = "Fizik",
            unite = "Kuvvet ve Hareket",
            konu = "İki Boyutlu Hareket",
            aciklama = "İki boyutlu hareket problemlerini çözer.",
            ornekler = listOf("Eğik atış", "Yatay atış", "Dairesel hareket"),
            keywords = listOf("iki boyutlu", "eğik atış", "dairesel hareket")
        ),
        RagKazanim(
            kod = "F.10.2.1",
            ders = "Fizik",
            unite = "Enerji",
            konu = "Mekanik Enerji Korunumu",
            aciklama = "Mekanik enerjinin korunumunu anlar ve problemlerde uygular.",
            ornekler = listOf("Kinetik + Potansiyel = Sabit", "Sarkaç hareketi", "Yaylı cisim"),
            keywords = listOf("enerji korunumu", "kinetik", "potansiyel", "mekanik")
        ),
        RagKazanim(
            kod = "F.10.3.1",
            ders = "Fizik",
            unite = "Elektrik",
            konu = "Elektrik Akımı",
            aciklama = "Elektrik akımını ve Ohm yasasını anlar.",
            ornekler = listOf("I = V/R", "Direnç", "Elektrik devresi"),
            keywords = listOf("akım", "ohm", "direnç", "voltaj")
        ),
        RagKazanim(
            kod = "F.10.3.2",
            ders = "Fizik",
            unite = "Elektrik",
            konu = "Elektrik Devreleri",
            aciklama = "Seri ve paralel bağlı devreleri analiz eder.",
            ornekler = listOf("Seri bağlama", "Paralel bağlama", "Eşdeğer direnç"),
            keywords = listOf("devre", "seri", "paralel", "eşdeğer direnç")
        ),
        RagKazanim(
            kod = "F.10.4.1",
            ders = "Fizik",
            unite = "Dalgalar",
            konu = "Dalga Hareketi",
            aciklama = "Dalga hareketini ve özelliklerini açıklar.",
            ornekler = listOf("Frekans", "Dalga boyu", "Hız", "Genlik"),
            keywords = listOf("dalga", "frekans", "dalga boyu", "periyot")
        ),
        RagKazanim(
            kod = "F.10.4.2",
            ders = "Fizik",
            unite = "Dalgalar",
            konu = "Ses Dalgaları",
            aciklama = "Ses dalgalarının özelliklerini ve yayılmasını açıklar.",
            ornekler = listOf("Ses hızı", "Yankı", "Doppler olayı"),
            keywords = listOf("ses", "dalga", "yankı", "doppler")
        )
    )

    // ==================== KİMYA ====================
    
    val kimya = listOf(
        // ==================== 1. ÜNİTE: KİMYASAL TEPKİMELER ====================
        RagKazanim(
            kod = "K.10.1.1",
            ders = "Kimya",
            unite = "Kimyasal Tepkimeler",
            konu = "Tepkime Türleri",
            aciklama = "Kimyasal tepkime türlerini tanır ve sınıflandırır.",
            ornekler = listOf("Bileşme", "Ayrışma", "Yanma", "Yer değiştirme", "Nötrleşme"),
            keywords = listOf("tepkime", "bileşme", "ayrışma", "yanma"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.1.2",
            ders = "Kimya",
            unite = "Kimyasal Tepkimeler",
            konu = "Mol Kavramı",
            aciklama = "Mol kavramını ve Avogadro sayısını kullanarak hesaplamalar yapar.",
            ornekler = listOf("1 mol = 6.02×10²³", "Molar kütle", "n = m/M", "Mol-tanecik dönüşümü"),
            keywords = listOf("mol", "avogadro", "molar kütle", "tanecik sayısı"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.1.3",
            ders = "Kimya",
            unite = "Kimyasal Tepkimeler",
            konu = "Stokiyometrik Hesaplamalar",
            aciklama = "Kimyasal tepkimelerde kütle ve mol hesaplamalarını yapar.",
            ornekler = listOf("Kütlece korunum", "Tepkimeye giren/çıkan miktar", "Sınırlayıcı madde"),
            keywords = listOf("stokiyometri", "kütle", "mol hesabı", "tepkime hesabı"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 2. ÜNİTE: GAZLAR (YENİ) ====================
        RagKazanim(
            kod = "K.10.2.1",
            ders = "Kimya",
            unite = "Gazlar",
            konu = "Gazların Özellikleri",
            aciklama = "Gazların genel özelliklerini ve davranışlarını açıklar.",
            ornekler = listOf("Sıkıştırılabilirlik", "Dağılma", "Basınç", "Hacim"),
            keywords = listOf("gaz", "gaz özellikleri", "basınç", "hacim"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "K.10.2.2",
            ders = "Kimya",
            unite = "Gazlar",
            konu = "Gaz Yasaları",
            aciklama = "Boyle, Charles ve Avogadro yasalarını uygular.",
            ornekler = listOf("P₁V₁=P₂V₂ (Boyle)", "V₁/T₁=V₂/T₂ (Charles)", "V∝n (Avogadro)"),
            keywords = listOf("boyle", "charles", "avogadro yasası", "gaz yasası"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.2.3",
            ders = "Kimya",
            unite = "Gazlar",
            konu = "İdeal Gaz Yasası",
            aciklama = "İdeal gaz denklemini kullanarak hesaplamalar yapar.",
            ornekler = listOf("PV=nRT", "R=0.082 L·atm/(mol·K)", "STP koşulları"),
            keywords = listOf("ideal gaz", "PV=nRT", "gaz sabiti", "STP"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "K.10.2.4",
            ders = "Kimya",
            unite = "Gazlar",
            konu = "Kinetik Teori",
            aciklama = "Gazların kinetik teorisini ve moleküler davranışını açıklar.",
            ornekler = listOf("Ortalama kinetik enerji", "Sıcaklık-hız ilişkisi", "Maxwell-Boltzmann"),
            keywords = listOf("kinetik teori", "moleküler hız", "enerji", "sıcaklık"),
            zorlukSeviyesi = "Zor"
        ),
        RagKazanim(
            kod = "K.10.2.5",
            ders = "Kimya",
            unite = "Gazlar",
            konu = "Difüzyon ve Efüzyon",
            aciklama = "Graham yasasını kullanarak difüzyon ve efüzyon hızlarını karşılaştırır.",
            ornekler = listOf("Graham yasası", "r₁/r₂=√(M₂/M₁)", "Koku yayılması"),
            keywords = listOf("difüzyon", "efüzyon", "graham", "yayılma hızı"),
            zorlukSeviyesi = "Orta"
        ),
        // ==================== 3. ÜNİTE: ÇÖZELTİLER ====================
        RagKazanim(
            kod = "K.10.3.1",
            ders = "Kimya",
            unite = "Çözeltiler",
            konu = "Çözünme Süreci",
            aciklama = "Çözünme olayını ve etkileyen faktörleri açıklar.",
            ornekler = listOf("Çözücü-çözünen etkileşimi", "Sıcaklık etkisi", "Karıştırma etkisi"),
            keywords = listOf("çözünme", "çözücü", "çözünen", "çözelti"),
            zorlukSeviyesi = "Kolay"
        ),
        RagKazanim(
            kod = "K.10.3.2",
            ders = "Kimya",
            unite = "Çözeltiler",
            konu = "Derişim Birimleri",
            aciklama = "Farklı derişim birimlerini kullanarak hesaplamalar yapar.",
            ornekler = listOf("Molarite (M)", "Molalite (m)", "Kütle yüzdesi", "ppm"),
            keywords = listOf("derişim", "molarite", "molalite", "yüzde"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.3.3",
            ders = "Kimya",
            unite = "Çözeltiler",
            konu = "Çözünürlük",
            aciklama = "Çözünürlük kavramını ve çözünürlüğe etki eden faktörleri bilir.",
            ornekler = listOf("Doymuş çözelti", "Sıcaklık-çözünürlük", "Basınç etkisi (gazlar)"),
            keywords = listOf("çözünürlük", "doymuş", "aşırı doymuş", "çözünürlük eğrisi"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.3.4",
            ders = "Kimya",
            unite = "Çözeltiler",
            konu = "Koligatif Özellikler",
            aciklama = "Buhar basıncı alçalması, kaynama noktası yükselmesi ve donma noktası düşmesini açıklar.",
            ornekler = listOf("ΔTk=Kb·m", "ΔTd=Kd·m", "Antifreeze etkisi", "Tuz atma"),
            keywords = listOf("koligatif", "kaynama noktası", "donma noktası", "buhar basıncı"),
            zorlukSeviyesi = "Zor"
        ),
        // ==================== 4. ÜNİTE: YEŞİL KİMYA ====================
        RagKazanim(
            kod = "K.10.4.1",
            ders = "Kimya",
            unite = "Yeşil Kimya",
            konu = "Çevresel Sürdürülebilirlik",
            aciklama = "Yeşil kimya prensiplerini ve çevre dostu uygulamaları açıklar.",
            ornekler = listOf("Atık azaltma", "Enerji verimliliği", "Yenilenebilir hammadde"),
            keywords = listOf("yeşil kimya", "sürdürülebilirlik", "çevre", "atık"),
            zorlukSeviyesi = "Orta"
        ),
        RagKazanim(
            kod = "K.10.4.2",
            ders = "Kimya",
            unite = "Yeşil Kimya",
            konu = "Atmosfer ve Çevre",
            aciklama = "Atmosfere salınan gazların çevresel etkilerini değerlendirir.",
            ornekler = listOf("Sera gazları", "Ozon tabakası", "Küresel ısınma", "Karbon ayak izi"),
            keywords = listOf("atmosfer", "sera etkisi", "ozon", "karbon"),
            zorlukSeviyesi = "Orta"
        )
    )

    // ==================== BİYOLOJİ ====================
    
    val biyoloji = listOf(
        RagKazanim(
            kod = "B.10.1.1",
            ders = "Biyoloji",
            unite = "Enerji",
            konu = "Fotosentez",
            aciklama = "Fotosentez olayını ve aşamalarını açıklar.",
            ornekler = listOf("Işık reaksiyonları", "Karanlık reaksiyonları", "Kloroplast"),
            keywords = listOf("fotosentez", "kloroplast", "ışık", "glikoz")
        ),
        RagKazanim(
            kod = "B.10.1.2",
            ders = "Biyoloji",
            unite = "Enerji",
            konu = "Solunum",
            aciklama = "Hücresel solunumu ve ATP üretimini açıklar.",
            ornekler = listOf("Glikoliz", "Krebs döngüsü", "Elektron taşıma zinciri"),
            keywords = listOf("solunum", "ATP", "mitokondri", "enerji")
        ),
        RagKazanim(
            kod = "B.10.2.1",
            ders = "Biyoloji",
            unite = "Ekoloji",
            konu = "Ekosistem",
            aciklama = "Ekosistem kavramını ve bileşenlerini anlar.",
            ornekler = listOf("Üretici", "Tüketici", "Ayrıştırıcı", "Besin zinciri"),
            keywords = listOf("ekosistem", "besin zinciri", "üretici", "tüketici")
        ),
        RagKazanim(
            kod = "B.10.2.2",
            ders = "Biyoloji",
            unite = "Ekoloji",
            konu = "Madde Döngüleri",
            aciklama = "Doğadaki madde döngülerini açıklar.",
            ornekler = listOf("Karbon döngüsü", "Azot döngüsü", "Su döngüsü"),
            keywords = listOf("döngü", "karbon", "azot", "su", "madde")
        ),
        RagKazanim(
            kod = "B.10.2.3",
            ders = "Biyoloji",
            unite = "Ekoloji",
            konu = "Çevre Sorunları",
            aciklama = "Çevre sorunlarını ve çözüm önerilerini tartışır.",
            ornekler = listOf("Küresel ısınma", "Hava kirliliği", "Su kirliliği"),
            keywords = listOf("çevre", "kirlilik", "küresel ısınma", "sürdürülebilirlik")
        )
    )

    // ==================== TARİH ====================
    
    val tarih = listOf(
        RagKazanim(
            kod = "T.10.1.1",
            ders = "Tarih",
            unite = "Türkistan'dan Türkiye'ye",
            konu = "Büyük Selçuklu Devleti",
            aciklama = "Büyük Selçuklu Devleti'nin kuruluşunu ve gelişimini açıklar.",
            ornekler = listOf("Tuğrul Bey", "Alparslan", "Malazgirt Savaşı"),
            keywords = listOf("selçuklu", "tuğrul bey", "malazgirt", "anadolu")
        ),
        RagKazanim(
            kod = "T.10.1.2",
            ders = "Tarih",
            unite = "Türkistan'dan Türkiye'ye",
            konu = "Anadolu Selçuklu Devleti",
            aciklama = "Anadolu Selçuklu Devleti'nin siyasi ve kültürel yapısını bilir.",
            ornekler = listOf("I. Kılıçarslan", "Alaeddin Keykubat", "Moğol istilası"),
            keywords = listOf("anadolu selçuklu", "kılıçarslan", "keykubat")
        ),
        RagKazanim(
            kod = "T.10.2.1",
            ders = "Tarih",
            unite = "Beylikten Devlete Osmanlı",
            konu = "Osmanlı Devleti'nin Kuruluşu",
            aciklama = "Osmanlı Devleti'nin kuruluş sürecini ve ilk dönemini açıklar.",
            ornekler = listOf("Osman Bey", "Orhan Bey", "İlk fetihler"),
            keywords = listOf("osmanlı", "kuruluş", "osman bey", "beylik")
        ),
        RagKazanim(
            kod = "T.10.2.2",
            ders = "Tarih",
            unite = "Beylikten Devlete Osmanlı",
            konu = "Osmanlı'nın Yükselişi",
            aciklama = "Osmanlı'nın Balkanlar'daki genişlemesini ve güçlenmesini anlar.",
            ornekler = listOf("I. Murad", "Yıldırım Bayezid", "Kosova Savaşı"),
            keywords = listOf("yükseliş", "balkanlar", "fetih", "genişleme")
        ),
        RagKazanim(
            kod = "T.10.3.1",
            ders = "Tarih",
            unite = "Cihan Devleti Osmanlı",
            konu = "İstanbul'un Fethi",
            aciklama = "İstanbul'un fethinin önemini ve sonuçlarını değerlendirir.",
            ornekler = listOf("II. Mehmed", "1453", "Bizans'ın sonu"),
            keywords = listOf("istanbul", "fetih", "fatih", "1453")
        ),
        RagKazanim(
            kod = "T.10.3.2",
            ders = "Tarih",
            unite = "Cihan Devleti Osmanlı",
            konu = "Yavuz ve Kanuni Dönemi",
            aciklama = "Osmanlı'nın en güçlü dönemini ve fetihlerini bilir.",
            ornekler = listOf("Yavuz Sultan Selim", "Kanuni Sultan Süleyman", "Altın çağ"),
            keywords = listOf("yavuz", "kanuni", "altın çağ", "fetih")
        )
    )

    // ==================== COĞRAFYA ====================
    
    val cografya = listOf(
        RagKazanim(
            kod = "C.10.1.1",
            ders = "Coğrafya",
            unite = "Doğal Sistemler",
            konu = "Türkiye'nin Yer Şekilleri",
            aciklama = "Türkiye'nin yer şekillerini ve oluşumlarını açıklar.",
            ornekler = listOf("Dağlar", "Ovalar", "Platolar", "Akdeniz kıyıları"),
            keywords = listOf("yer şekli", "dağ", "ova", "plato", "türkiye")
        ),
        RagKazanim(
            kod = "C.10.1.2",
            ders = "Coğrafya",
            unite = "Doğal Sistemler",
            konu = "Türkiye'nin İklimi",
            aciklama = "Türkiye'nin iklim özelliklerini ve iklim tiplerini bilir.",
            ornekler = listOf("Akdeniz iklimi", "Karasal iklim", "Karadeniz iklimi"),
            keywords = listOf("iklim", "yağış", "sıcaklık", "türkiye")
        ),
        RagKazanim(
            kod = "C.10.2.1",
            ders = "Coğrafya",
            unite = "Beşeri Sistemler",
            konu = "Türkiye'nin Nüfus Yapısı",
            aciklama = "Türkiye'nin nüfus özelliklerini ve dağılışını analiz eder.",
            ornekler = listOf("Nüfus yoğunluğu", "Göç", "Kentleşme", "Demografik yapı"),
            keywords = listOf("nüfus", "göç", "kentleşme", "demografi")
        ),
        RagKazanim(
            kod = "C.10.3.1",
            ders = "Coğrafya",
            unite = "Ekonomik Faaliyetler",
            konu = "Türkiye'de Tarım",
            aciklama = "Türkiye'nin tarımsal faaliyetlerini ve ürünlerini bilir.",
            ornekler = listOf("Tahıl", "Endüstri bitkileri", "Hayvancılık"),
            keywords = listOf("tarım", "ürün", "hayvancılık", "ekonomi")
        ),
        RagKazanim(
            kod = "C.10.3.2",
            ders = "Coğrafya",
            unite = "Ekonomik Faaliyetler",
            konu = "Türkiye'de Sanayi",
            aciklama = "Türkiye'nin sanayi faaliyetlerini ve dağılışını açıklar.",
            ornekler = listOf("Demir-çelik", "Tekstil", "Otomotiv", "Sanayi bölgeleri"),
            keywords = listOf("sanayi", "üretim", "fabrika", "ekonomi")
        )
    )

    // ==================== TÜRK DİLİ VE EDEBİYATI ====================
    
    val turk_dili = listOf(
        RagKazanim(
            kod = "TDE.10.1.1",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Dil ve Anlatım",
            konu = "Cümle Bilgisi",
            aciklama = "Cümle türlerini ve yapılarını bilir.",
            ornekler = listOf("Basit cümle", "Birleşik cümle", "Sıralı cümle"),
            keywords = listOf("cümle", "yapı", "basit", "birleşik")
        ),
        RagKazanim(
            kod = "TDE.10.1.2",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Divan Edebiyatı",
            aciklama = "Divan edebiyatının özelliklerini ve önemli şairlerini bilir.",
            ornekler = listOf("Gazel", "Kaside", "Fuzuli", "Baki"),
            keywords = listOf("divan", "gazel", "kaside", "şair")
        ),
        RagKazanim(
            kod = "TDE.10.1.3",
            ders = "Türk Dili ve Edebiyatı",
            unite = "Edebiyat",
            konu = "Halk Edebiyatı",
            aciklama = "Halk edebiyatının özelliklerini ve türlerini bilir.",
            ornekler = listOf("Türkü", "Mani", "Destan", "Yunus Emre"),
            keywords = listOf("halk edebiyatı", "türkü", "destan", "yunus emre")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "İ.10.1.1",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Expressing Opinions",
            aciklama = "Görüş ve düşüncelerini İngilizce ifade eder.",
            ornekler = listOf("I think...", "In my opinion...", "I agree/disagree"),
            keywords = listOf("opinion", "think", "agree", "disagree")
        ),
        RagKazanim(
            kod = "İ.10.1.2",
            ders = "İngilizce",
            unite = "Communication",
            konu = "Making Comparisons",
            aciklama = "Karşılaştırma yapar ve tercihlerini belirtir.",
            ornekler = listOf("Comparative adjectives", "Superlative adjectives", "as...as"),
            keywords = listOf("comparison", "comparative", "superlative", "prefer")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val din_kulturu = listOf(
        RagKazanim(
            kod = "DK.10.1.1",
            ders = "Din Kültürü",
            unite = "Varlık ve Bilgi",
            konu = "İslam'da Varlık Anlayışı",
            aciklama = "İslam'ın varlık ve bilgi anlayışını kavrar.",
            ornekler = listOf("Yaratılış", "Hikmet", "İlim"),
            keywords = listOf("varlık", "yaratılış", "bilgi", "hikmet")
        ),
        RagKazanim(
            kod = "DK.10.1.2",
            ders = "Din Kültürü",
            unite = "Allah'ı Tanımak",
            konu = "Allah'ın Sıfatları",
            aciklama = "Allah'ın sıfatlarını bilir ve anlamlarını açıklar.",
            ornekler = listOf("Zati sıfatlar", "Subuti sıfatlar", "Selbi sıfatlar"),
            keywords = listOf("sıfat", "zati", "subuti", "selbi")
        )
    )

    // ==================== FELSEFE ====================
    
    val felsefe = listOf(
        RagKazanim(
            kod = "F.10.1.1",
            ders = "Felsefe",
            unite = "Felsefenin Doğası",
            konu = "Felsefe ve Bilim",
            aciklama = "Felsefe ile bilim arasındaki ilişkiyi ve farkları anlar.",
            ornekler = listOf("Bilimsel yöntem", "Felsefi sorgulama", "Epistemoloji"),
            keywords = listOf("felsefe", "bilim", "yöntem", "epistemoloji")
        ),
        RagKazanim(
            kod = "F.10.2.1",
            ders = "Felsefe",
            unite = "Mantık",
            konu = "Mantık İlkeleri",
            aciklama = "Temel mantık ilkelerini bilir ve uygular.",
            ornekler = listOf("Özdeşlik", "Çelişmezlik", "Üçüncü halin olanaksızlığı"),
            keywords = listOf("mantık", "ilke", "çelişki", "akıl yürütme")
        ),
        RagKazanim(
            kod = "F.10.3.1",
            ders = "Felsefe",
            unite = "Varlık Felsefesi",
            konu = "Ontoloji",
            aciklama = "Varlık felsefesinin temel sorunlarını tartışır.",
            ornekler = listOf("Varlık nedir?", "Madde ve ruh", "Değişim ve süreklilik"),
            keywords = listOf("ontoloji", "varlık", "madde", "ruh")
        ),
        RagKazanim(
            kod = "F.10.4.1",
            ders = "Felsefe",
            unite = "Bilgi Felsefesi",
            konu = "Bilginin Kaynağı",
            aciklama = "Bilginin kaynaklarını ve türlerini inceler.",
            ornekler = listOf("Duyular", "Akıl", "Sezgi", "Deneyim"),
            keywords = listOf("bilgi", "kaynak", "duyum", "akıl", "sezgi")
        ),
        RagKazanim(
            kod = "F.10.5.1",
            ders = "Felsefe",
            unite = "Ahlak Felsefesi",
            konu = "Ahlak Kuramları",
            aciklama = "Temel ahlak kuramlarını karşılaştırır.",
            ornekler = listOf("Faydacılık", "Ödev ahlakı", "Erdem etiği"),
            keywords = listOf("ahlak", "etik", "faydacılık", "ödev")
        )
    )

    // Tüm dersleri birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fizik + kimya + biyoloji + tarih + cografya + 
               turk_dili + ingilizce + din_kulturu + felsefe
    }
}
