package com.example.bilgideham

/**
 * AGS (Akademi Giriş Sınavı) Tarih Öğretmenliği Müfredatı
 * ÖABT Tarih Alan Bilgisi - 14 Ünite
 * MEB ve YÖK müfredatına uyumlu kazanım veritabanı
 */
object AgsTarihKazanimlari {

    // ==================== ÜNİTE 1: TARİH BİLİMİ ====================
    val unite1_tarihBilimi = listOf(
        RagKazanim("AGS.T.1.1", "Tarih", "Tarih Bilimi", "Tarih Bilimine Giriş",
            "Tarihin tanımını, konusunu ve önemini açıklar. Tarihin diğer bilim dallarıyla ilişkisini değerlendirir.",
            listOf("tarih tanımı", "tarih bilimi", "tarihçi", "geçmiş", "belge")),
        RagKazanim("AGS.T.1.2", "Tarih", "Tarih Bilimi", "Tarih Biliminin Yöntemi",
            "Tarih biliminin yöntemlerini (tarama, sınıflandırma, tahlil, tenkit, terkip) açıklar.",
            listOf("tarih yöntemi", "kaynak tarama", "iç tenkit", "dış tenkit", "sentez")),
        RagKazanim("AGS.T.1.3", "Tarih", "Tarih Bilimi", "Tarih Yazıcılığı",
            "Tarih yazıcılığı türlerini (hikayeci, öğretici, araştırmacı, sosyal tarih) karşılaştırır.",
            listOf("tarih yazıcılığı", "hikayeci tarih", "pragmatik tarih", "annales okulu")),
        RagKazanim("AGS.T.1.4", "Tarih", "Tarih Bilimi", "Tarihi Kaynaklar",
            "Birincil ve ikincil kaynakları ayırt eder. Yazılı, sözlü ve görsel kaynakları sınıflandırır.",
            listOf("birincil kaynak", "ikincil kaynak", "arşiv", "kitabe", "sikke")),
        RagKazanim("AGS.T.1.5", "Tarih", "Tarih Bilimi", "Tarihe Yardımcı Bilimler",
            "Arkeoloji, kronoloji, paleografya, epigrafya, nümizmatik, heraldik gibi yardımcı bilimleri tanır.",
            listOf("arkeoloji", "kronoloji", "paleografya", "epigrafya", "nümizmatik", "heraldik", "diplomatik"))
    )

    // ==================== ÜNİTE 2: OSMANLI TÜRKÇESİ ====================
    val unite2_osmanlica = listOf(
        RagKazanim("AGS.T.2.1", "Tarih", "Osmanlı Türkçesi", "Osmanlı Alfabesi",
            "Osmanlı Türkçesi alfabesindeki harfleri tanır ve okur. Harflerin başta, ortada, sonda yazılışlarını bilir.",
            listOf("osmanlıca", "arap alfabesi", "elif", "be", "te", "harfler")),
        RagKazanim("AGS.T.2.2", "Tarih", "Osmanlı Türkçesi", "Harflerin Birleşmesi",
            "Harflerin birleşme kurallarını uygular. Bitişen ve bitişmeyen harfleri ayırt eder.",
            listOf("harf birleşimi", "bitişen harfler", "bitişmeyen harfler", "kelime yazımı")),
        RagKazanim("AGS.T.2.3", "Tarih", "Osmanlı Türkçesi", "Osmanlıca Okuma",
            "Basit Osmanlıca metinleri okur ve anlamlandırır. Tarihî belgeleri çözümler.",
            listOf("osmanlıca okuma", "belge okuma", "ferman", "berat", "vakfiye"))
    )

    // ==================== ÜNİTE 3: UYGARLIĞIN DOĞUŞU ====================
    val unite3_uygarlik = listOf(
        RagKazanim("AGS.T.3.1", "Tarih", "Uygarlığın Doğuşu", "Tarih Öncesi Devirler",
            "Paleolitik, Mezolitik, Neolitik ve Kalkolitik dönemlerin özelliklerini karşılaştırır.",
            listOf("paleolitik", "mezolitik", "neolitik", "kalkolitik", "taş devri", "cilalı taş")),
        RagKazanim("AGS.T.3.2", "Tarih", "Uygarlığın Doğuşu", "Maden Devirleri",
            "Bakır, Tunç ve Demir çağlarının özelliklerini ve insanlık tarihine etkilerini açıklar.",
            listOf("bakır çağı", "tunç çağı", "demir çağı", "maden işleme")),
        RagKazanim("AGS.T.3.3", "Tarih", "Uygarlığın Doğuşu", "Mezopotamya Uygarlıkları",
            "Sümer, Akad, Babil ve Asur uygarlıklarının özelliklerini ve katkılarını değerlendirir.",
            listOf("sümer", "akad", "babil", "asur", "çivi yazısı", "hammurabi", "zigurat")),
        RagKazanim("AGS.T.3.4", "Tarih", "Uygarlığın Doğuşu", "Mısır Uygarlığı",
            "Eski Mısır uygarlığının siyasi, sosyal ve kültürel özelliklerini açıklar.",
            listOf("mısır", "firavun", "piramit", "hiyeroglif", "nil nehri", "mumyalama")),
        RagKazanim("AGS.T.3.5", "Tarih", "Uygarlığın Doğuşu", "Anadolu Uygarlıkları",
            "Hititler, Frigler, Lidyalılar, İyonlar ve Urartular hakkında bilgi verir.",
            listOf("hitit", "frig", "lidya", "iyon", "urartu", "anadolu", "para", "alfabe")),
        RagKazanim("AGS.T.3.6", "Tarih", "Uygarlığın Doğuşu", "Ege ve Yunan Uygarlıkları",
            "Girit, Miken, Yunan şehir devletleri ve Helen kültürünün özelliklerini açıklar.",
            listOf("girit", "miken", "yunan", "atina", "sparta", "demokrasi", "olimpiyat")),
        RagKazanim("AGS.T.3.7", "Tarih", "Uygarlığın Doğuşu", "Roma Uygarlığı",
            "Roma İmparatorluğu'nun kuruluşu, yükselişi ve çöküşünü analiz eder.",
            listOf("roma", "cumhuriyet", "imparatorluk", "sezar", "augustus", "hristiyanlık"))
    )

    // ==================== ÜNİTE 4: İLK TÜRK DEVLETLERİ ====================
    val unite4_ilkTurkler = listOf(
        RagKazanim("AGS.T.4.1", "Tarih", "İlk Türk Devletleri", "Türklerin Anayurdu",
            "Türklerin anayurdu, göçleri ve yayılma alanlarını harita üzerinde gösterir.",
            listOf("orta asya", "türk anayurdu", "göç", "bozkır", "atlı göçebe")),
        RagKazanim("AGS.T.4.2", "Tarih", "İlk Türk Devletleri", "Asya Hun Devleti",
            "Asya Hun Devleti'nin kuruluşu, Mete Han dönemi ve Çin ilişkilerini açıklar.",
            listOf("asya hun", "mete han", "teoman", "onlu sistem", "çin seddi")),
        RagKazanim("AGS.T.4.3", "Tarih", "İlk Türk Devletleri", "Avrupa Hun Devleti",
            "Avrupa Hun Devleti ve Attila dönemini, kavimler göçünü değerlendirir.",
            listOf("avrupa hun", "attila", "balamir", "kavimler göçü", "roma")),
        RagKazanim("AGS.T.4.4", "Tarih", "İlk Türk Devletleri", "Göktürkler",
            "I. ve II. Göktürk Devletleri'nin siyasi tarihini ve Orhun Kitabeleri'ni inceler.",
            listOf("göktürk", "bumin kağan", "bilge kağan", "orhun kitabeleri", "tonyukuk")),
        RagKazanim("AGS.T.4.5", "Tarih", "İlk Türk Devletleri", "Uygurlar",
            "Uygur Devleti'nin özellikleri, yerleşik hayata geçiş ve kültürel katkılarını açıklar.",
            listOf("uygur", "bögü kağan", "maniheizm", "matbaa", "yerleşik hayat")),
        RagKazanim("AGS.T.4.6", "Tarih", "İlk Türk Devletleri", "Diğer Türk Devletleri",
            "Avarlar, Hazarlar, Bulgarlar, Macarlar, Peçenekler, Kıpçaklar hakkında bilgi verir.",
            listOf("avar", "hazar", "bulgar", "macar", "peçenek", "kıpçak", "kuman")),
        RagKazanim("AGS.T.4.7", "Tarih", "İlk Türk Devletleri", "Türk Kültür ve Medeniyeti",
            "İlk Türklerde devlet yönetimi, ordu, hukuk, din, sanat ve edebiyatı açıklar.",
            listOf("kut", "kurultay", "töre", "gök tanrı", "şamanizm", "balbal", "kurgan"))
    )

    // ==================== ÜNİTE 5: İSLAM TARİHİ ====================
    val unite5_islamTarihi = listOf(
        RagKazanim("AGS.T.5.1", "Tarih", "İslam Tarihi", "İslamiyet Öncesi Arabistan",
            "Cahiliye dönemi Arap toplumunun siyasi, sosyal ve ekonomik yapısını açıklar.",
            listOf("cahiliye", "arap yarımadası", "mekke", "medine", "kabe", "kabile")),
        RagKazanim("AGS.T.5.2", "Tarih", "İslam Tarihi", "Peygamberimizin Dönemi",
            "Peygamberimizin hayatı, İslam'ın doğuşu ve yayılışını kronolojik olarak açıklar.",
            listOf("hz. muhammed", "hicret", "bedir", "uhud", "hendek", "mekke fethi")),
        RagKazanim("AGS.T.5.3", "Tarih", "İslam Tarihi", "Dört Halife Dönemi",
            "Hz. Ebubekir, Ömer, Osman ve Ali dönemlerinin özelliklerini karşılaştırır.",
            listOf("dört halife", "ebubekir", "ömer", "osman", "ali", "ridde savaşları", "fetihler")),
        RagKazanim("AGS.T.5.4", "Tarih", "İslam Tarihi", "Emeviler",
            "Emevi Devleti'nin kuruluşu, yönetim anlayışı ve yıkılışını değerlendirir.",
            listOf("emevi", "muaviye", "şam", "arap milliyetçiliği", "mevali")),
        RagKazanim("AGS.T.5.5", "Tarih", "İslam Tarihi", "Abbasiler",
            "Abbasi Devleti'nin özellikleri, altın çağı ve parçalanma sürecini açıklar.",
            listOf("abbasi", "bağdat", "harun reşid", "memun", "beytül hikme")),
        RagKazanim("AGS.T.5.6", "Tarih", "İslam Tarihi", "İslam Medeniyeti",
            "İslam medeniyetinin bilim, sanat, mimari ve edebiyat alanındaki katkılarını değerlendirir.",
            listOf("islam medeniyeti", "cami", "medrese", "kervansaray", "hat sanatı"))
    )

    // ==================== ÜNİTE 6: TÜRK İSLAM DEVLETLERİ ====================
    val unite6_turkIslam = listOf(
        RagKazanim("AGS.T.6.1", "Tarih", "Türk İslam Devletleri", "Karahanlılar",
            "Karahanlı Devleti'nin kuruluşu, İslamiyet'i kabulü ve kültürel özelliklerini açıklar.",
            listOf("karahanlı", "satuk buğra han", "kaşgarlı mahmut", "yusuf has hacip")),
        RagKazanim("AGS.T.6.2", "Tarih", "Türk İslam Devletleri", "Gazneliler",
            "Gazneli Devleti'nin kuruluşu, Hindistan seferleri ve kültürel faaliyetlerini inceler.",
            listOf("gazneli", "mahmut", "hindistan", "firdevsi", "şehname")),
        RagKazanim("AGS.T.6.3", "Tarih", "Türk İslam Devletleri", "Büyük Selçuklu Devleti",
            "Büyük Selçuklu Devleti'nin kuruluşu, yükselişi ve yıkılışını analiz eder.",
            listOf("selçuklu", "tuğrul bey", "alparslan", "melikşah", "nizamülmülk", "malazgirt")),
        RagKazanim("AGS.T.6.4", "Tarih", "Türk İslam Devletleri", "Türkiye Selçuklu Devleti",
            "Türkiye Selçuklu Devleti'nin kuruluşu, Haçlı Seferleri ve Moğol istilasını açıklar.",
            listOf("anadolu selçuklu", "süleymanşah", "kılıçarslan", "alaeddin keykubat", "kösedağ")),
        RagKazanim("AGS.T.6.5", "Tarih", "Türk İslam Devletleri", "Anadolu Beylikleri",
            "Anadolu beyliklerinin kuruluşu, özellikleri ve Osmanlı'ya katılımlarını değerlendirir.",
            listOf("beylik", "karamanoğulları", "germiyanoğulları", "candaroğulları", "menteşeoğulları")),
        RagKazanim("AGS.T.6.6", "Tarih", "Türk İslam Devletleri", "Türk İslam Kültürü",
            "Türk İslam devletlerinde yönetim, ordu, hukuk, eğitim ve sanat anlayışını açıklar.",
            listOf("ikta", "atabeylik", "nizamiye medresesi", "kervansaray", "türbe"))
    )


    // ==================== ÜNİTE 7: TÜRK DÜNYASI (XIII-XIX. YY) ====================
    val unite7_turkDunyasi = listOf(
        RagKazanim("AGS.T.7.1", "Tarih", "Türk Dünyası", "Moğol İmparatorluğu",
            "Cengiz Han ve Moğol İmparatorluğu'nun kuruluşu, yayılması ve parçalanmasını açıklar.",
            listOf("moğol", "cengiz han", "kubilay", "altın orda", "çağatay", "ilhanlı")),
        RagKazanim("AGS.T.7.2", "Tarih", "Türk Dünyası", "Timur Devleti",
            "Timur İmparatorluğu'nun kuruluşu, fetihleri ve kültürel mirasını değerlendirir.",
            listOf("timur", "semerkant", "ankara savaşı", "uluğ bey", "babür")),
        RagKazanim("AGS.T.7.3", "Tarih", "Türk Dünyası", "Babür İmparatorluğu",
            "Babür İmparatorluğu'nun kuruluşu, yükselişi ve Hindistan'daki etkisini açıklar.",
            listOf("babür", "hindistan", "ekber şah", "tac mahal", "şah cihan")),
        RagKazanim("AGS.T.7.4", "Tarih", "Türk Dünyası", "Safevi Devleti",
            "Safevi Devleti'nin kuruluşu, Osmanlı ilişkileri ve kültürel özelliklerini inceler.",
            listOf("safevi", "şah ismail", "çaldıran", "şiilik", "isfahan")),
        RagKazanim("AGS.T.7.5", "Tarih", "Türk Dünyası", "Türk Hanlıkları",
            "Kırım, Kazan, Astrahan, Buhara, Hive ve Hokand hanlıklarını tanır.",
            listOf("kırım hanlığı", "kazan hanlığı", "özbek", "buhara", "hive"))
    )

    // ==================== ÜNİTE 8: OSMANLI TARİHİ ====================
    val unite8_osmanli = listOf(
        RagKazanim("AGS.T.8.1", "Tarih", "Osmanlı Tarihi", "Kuruluş Dönemi",
            "Osmanlı Beyliği'nin kuruluşu, Osman Bey ve Orhan Bey dönemlerini açıklar.",
            listOf("osman bey", "orhan bey", "söğüt", "bursa", "iznik", "rumeli")),
        RagKazanim("AGS.T.8.2", "Tarih", "Osmanlı Tarihi", "Yükseliş Dönemi",
            "I. Murat'tan Kanuni'ye kadar Osmanlı'nın yükselişini ve fetihlerini analiz eder.",
            listOf("fatih", "istanbul fethi", "yavuz", "kanuni", "mohaç", "preveze")),
        RagKazanim("AGS.T.8.3", "Tarih", "Osmanlı Tarihi", "Duraklama Dönemi",
            "XVII. yüzyılda Osmanlı'nın duraklama sebepleri ve ıslahat hareketlerini değerlendirir.",
            listOf("duraklama", "köprülüler", "viyana kuşatması", "karlofça", "ıslahat")),
        RagKazanim("AGS.T.8.4", "Tarih", "Osmanlı Tarihi", "Gerileme Dönemi",
            "XVIII. yüzyılda Osmanlı'nın gerileme süreci ve yapılan ıslahatları açıklar.",
            listOf("lale devri", "patrona halil", "küçük kaynarca", "nizam-ı cedid")),
        RagKazanim("AGS.T.8.5", "Tarih", "Osmanlı Tarihi", "Osmanlı Devlet Teşkilatı",
            "Osmanlı merkez ve taşra teşkilatı, ordu ve hukuk sistemini açıklar.",
            listOf("divan", "sadrazam", "kapıkulu", "tımar", "eyalet", "sancak")),
        RagKazanim("AGS.T.8.6", "Tarih", "Osmanlı Tarihi", "Osmanlı Toplum Yapısı",
            "Osmanlı toplumunda millet sistemi, sosyal sınıflar ve ekonomik yapıyı inceler.",
            listOf("millet sistemi", "reaya", "askeri", "lonca", "vakıf", "iltizam"))
    )

    // ==================== ÜNİTE 9: EN UZUN YÜZYIL (1800-1922) ====================
    val unite9_enUzunYuzyil = listOf(
        RagKazanim("AGS.T.9.1", "Tarih", "En Uzun Yüzyıl", "II. Mahmut Dönemi",
            "II. Mahmut'un ıslahatları, Yeniçeri Ocağı'nın kaldırılması ve Tanzimat öncesi gelişmeleri açıklar.",
            listOf("ii. mahmut", "vaka-i hayriye", "sekban-ı cedid", "asakir-i mansure")),
        RagKazanim("AGS.T.9.2", "Tarih", "En Uzun Yüzyıl", "Tanzimat Dönemi",
            "Tanzimat Fermanı, Islahat Fermanı ve bu dönemdeki yenilikleri değerlendirir.",
            listOf("tanzimat", "gülhane hattı", "ıslahat fermanı", "mustafa reşit paşa")),
        RagKazanim("AGS.T.9.3", "Tarih", "En Uzun Yüzyıl", "I. Meşrutiyet",
            "I. Meşrutiyet'in ilanı, Kanun-i Esasi ve Meclis-i Mebusan'ı açıklar.",
            listOf("i. meşrutiyet", "kanun-i esasi", "mithat paşa", "meclis-i mebusan")),
        RagKazanim("AGS.T.9.4", "Tarih", "En Uzun Yüzyıl", "II. Abdülhamit Dönemi",
            "II. Abdülhamit'in iç ve dış politikası, istibdat dönemi ve eğitim faaliyetlerini inceler.",
            listOf("ii. abdülhamit", "istibdat", "93 harbi", "berlin antlaşması", "düyun-u umumiye")),
        RagKazanim("AGS.T.9.5", "Tarih", "En Uzun Yüzyıl", "II. Meşrutiyet",
            "II. Meşrutiyet'in ilanı, İttihat ve Terakki Cemiyeti ve 31 Mart Olayı'nı açıklar.",
            listOf("ii. meşrutiyet", "ittihat terakki", "31 mart", "hareket ordusu")),
        RagKazanim("AGS.T.9.6", "Tarih", "En Uzun Yüzyıl", "Osmanlı'da Fikir Akımları",
            "Osmanlıcılık, İslamcılık, Türkçülük ve Batıcılık akımlarını karşılaştırır.",
            listOf("osmanlıcılık", "islamcılık", "türkçülük", "batıcılık", "jön türkler"))
    )

    // ==================== ÜNİTE 10: XX. YÜZYIL BAŞLARI ====================
    val unite10_xxYuzyil = listOf(
        RagKazanim("AGS.T.10.1", "Tarih", "XX. Yüzyıl Başları", "Trablusgarp Savaşı",
            "Trablusgarp Savaşı'nın sebepleri, gelişimi ve sonuçlarını açıklar.",
            listOf("trablusgarp", "italya", "uşi antlaşması", "libya")),
        RagKazanim("AGS.T.10.2", "Tarih", "XX. Yüzyıl Başları", "Balkan Savaşları",
            "I. ve II. Balkan Savaşları'nın sebepleri, gelişimi ve sonuçlarını analiz eder.",
            listOf("balkan savaşları", "londra antlaşması", "bükreş antlaşması", "edirne")),
        RagKazanim("AGS.T.10.3", "Tarih", "XX. Yüzyıl Başları", "I. Dünya Savaşı Öncesi",
            "I. Dünya Savaşı'nın sebeplerini, ittifak ve itilaf bloklarını açıklar.",
            listOf("i. dünya savaşı", "üçlü ittifak", "üçlü itilaf", "suikast", "saraybosna")),
        RagKazanim("AGS.T.10.4", "Tarih", "XX. Yüzyıl Başları", "Osmanlı'nın I. Dünya Savaşı'na Girişi",
            "Osmanlı'nın savaşa giriş süreci ve savaştığı cepheleri açıklar.",
            listOf("osmanlı cepheleri", "çanakkale", "kafkas", "kanal", "irak", "hicaz")),
        RagKazanim("AGS.T.10.5", "Tarih", "XX. Yüzyıl Başları", "Çanakkale Savaşları",
            "Çanakkale Savaşları'nın önemi, gelişimi ve sonuçlarını değerlendirir.",
            listOf("çanakkale", "gelibolu", "mustafa kemal", "anafartalar", "conkbayırı")),
        RagKazanim("AGS.T.10.6", "Tarih", "XX. Yüzyıl Başları", "Mondros ve Sevr",
            "Mondros Ateşkes Antlaşması ve Sevr Antlaşması'nın maddelerini ve etkilerini açıklar.",
            listOf("mondros", "sevr", "işgal", "mütareke", "wilson ilkeleri"))
    )

    // ==================== ÜNİTE 11: MİLLİ MÜCADELE ====================
    val unite11_milliMucadele = listOf(
        RagKazanim("AGS.T.11.1", "Tarih", "Milli Mücadele", "İşgaller ve Direniş",
            "Mondros sonrası işgalleri ve halkın ilk direniş hareketlerini açıklar.",
            listOf("işgal", "kuva-yı milliye", "müdafaa-i hukuk", "reddi ilhak")),
        RagKazanim("AGS.T.11.2", "Tarih", "Milli Mücadele", "Mustafa Kemal'in Samsun'a Çıkışı",
            "Mustafa Kemal'in Samsun'a çıkışı ve milli mücadelenin başlangıcını açıklar.",
            listOf("samsun", "havza", "amasya", "9. ordu müfettişi")),
        RagKazanim("AGS.T.11.3", "Tarih", "Milli Mücadele", "Kongreler Dönemi",
            "Erzurum ve Sivas Kongrelerinin kararlarını ve önemini değerlendirir.",
            listOf("erzurum kongresi", "sivas kongresi", "heyet-i temsiliye", "manda")),
        RagKazanim("AGS.T.11.4", "Tarih", "Milli Mücadele", "TBMM'nin Açılışı",
            "TBMM'nin açılışı, ilk anayasa ve meclisin özelliklerini açıklar.",
            listOf("tbmm", "23 nisan", "teşkilat-ı esasiye", "meclis hükümeti")),
        RagKazanim("AGS.T.11.5", "Tarih", "Milli Mücadele", "Kurtuluş Savaşı Cepheleri",
            "Doğu, Güney ve Batı cephelerindeki savaşları ve antlaşmaları açıklar.",
            listOf("doğu cephesi", "güney cephesi", "batı cephesi", "gümrü", "ankara")),
        RagKazanim("AGS.T.11.6", "Tarih", "Milli Mücadele", "Sakarya ve Büyük Taarruz",
            "Sakarya Meydan Muharebesi ve Büyük Taarruz'un gelişimini ve sonuçlarını analiz eder.",
            listOf("sakarya", "büyük taarruz", "başkomutanlık", "dumlupınar", "mudanya")),
        RagKazanim("AGS.T.11.7", "Tarih", "Milli Mücadele", "Lozan Antlaşması",
            "Lozan Barış Antlaşması'nın maddelerini ve önemini değerlendirir.",
            listOf("lozan", "ismet paşa", "kapitülasyonlar", "boğazlar", "azınlıklar"))
    )

    // ==================== ÜNİTE 12: ATATÜRK DÖNEMİ ====================
    val unite12_ataturk = listOf(
        RagKazanim("AGS.T.12.1", "Tarih", "Atatürk Dönemi", "Siyasi İnkılaplar",
            "Saltanatın kaldırılması, Cumhuriyet'in ilanı ve halifeliğin kaldırılmasını açıklar.",
            listOf("saltanat", "cumhuriyet", "halifelik", "29 ekim", "3 mart")),
        RagKazanim("AGS.T.12.2", "Tarih", "Atatürk Dönemi", "Hukuk İnkılapları",
            "Anayasa değişiklikleri, medeni kanun ve hukuk alanındaki yenilikleri değerlendirir.",
            listOf("1924 anayasası", "medeni kanun", "laiklik", "şeriye mahkemeleri")),
        RagKazanim("AGS.T.12.3", "Tarih", "Atatürk Dönemi", "Eğitim ve Kültür İnkılapları",
            "Tevhid-i Tedrisat, harf inkılabı ve eğitim alanındaki yenilikleri açıklar.",
            listOf("tevhid-i tedrisat", "harf inkılabı", "millet mektepleri", "üniversite")),
        RagKazanim("AGS.T.12.4", "Tarih", "Atatürk Dönemi", "Ekonomik İnkılaplar",
            "İzmir İktisat Kongresi, devletçilik politikası ve ekonomik kalkınmayı inceler.",
            listOf("izmir iktisat kongresi", "devletçilik", "sümerbank", "etibank")),
        RagKazanim("AGS.T.12.5", "Tarih", "Atatürk Dönemi", "Toplumsal İnkılaplar",
            "Kıyafet, takvim, saat ve soyadı kanunu gibi toplumsal değişimleri açıklar.",
            listOf("şapka kanunu", "soyadı kanunu", "miladi takvim", "ölçü birimleri")),
        RagKazanim("AGS.T.12.6", "Tarih", "Atatürk Dönemi", "Atatürk İlkeleri",
            "Cumhuriyetçilik, milliyetçilik, halkçılık, devletçilik, laiklik ve inkılapçılığı açıklar.",
            listOf("cumhuriyetçilik", "milliyetçilik", "halkçılık", "devletçilik", "laiklik", "inkılapçılık")),
        RagKazanim("AGS.T.12.7", "Tarih", "Atatürk Dönemi", "Atatürk Dönemi Dış Politika",
            "Atatürk dönemi dış politika ilkeleri, Milletler Cemiyeti ve antlaşmaları açıklar.",
            listOf("milletler cemiyeti", "balkan antantı", "sadabat paktı", "montrö", "hatay"))
    )

    // ==================== ÜNİTE 13: DÜNYA TARİHİ ====================
    val unite13_dunyaTarihi = listOf(
        RagKazanim("AGS.T.13.1", "Tarih", "Dünya Tarihi", "Orta Çağ Avrupası",
            "Feodalite, Haçlı Seferleri ve Orta Çağ Avrupa'sının özelliklerini açıklar.",
            listOf("feodalite", "haçlı seferleri", "kilise", "skolastik", "şövalye")),
        RagKazanim("AGS.T.13.2", "Tarih", "Dünya Tarihi", "Coğrafi Keşifler",
            "Coğrafi keşiflerin sebepleri, önemli kaşifler ve sonuçlarını değerlendirir.",
            listOf("coğrafi keşifler", "kolomb", "vasco da gama", "macellan", "baharat yolu")),
        RagKazanim("AGS.T.13.3", "Tarih", "Dünya Tarihi", "Rönesans ve Reform",
            "Rönesans ve Reform hareketlerinin sebepleri, öncüleri ve sonuçlarını açıklar.",
            listOf("rönesans", "reform", "luther", "kalven", "hümanizm", "matbaa")),
        RagKazanim("AGS.T.13.4", "Tarih", "Dünya Tarihi", "Aydınlanma Çağı",
            "Aydınlanma düşüncesi, önemli düşünürler ve toplumsal etkilerini inceler.",
            listOf("aydınlanma", "voltaire", "rousseau", "montesquieu", "ansiklopedi")),
        RagKazanim("AGS.T.13.5", "Tarih", "Dünya Tarihi", "Fransız İhtilali",
            "Fransız İhtilali'nin sebepleri, gelişimi ve dünya tarihine etkilerini analiz eder.",
            listOf("fransız ihtilali", "insan hakları", "milliyetçilik", "napolyon", "bastille")),
        RagKazanim("AGS.T.13.6", "Tarih", "Dünya Tarihi", "Sanayi Devrimi",
            "Sanayi Devrimi'nin sebepleri, gelişimi ve toplumsal sonuçlarını değerlendirir.",
            listOf("sanayi devrimi", "buhar makinesi", "fabrika", "işçi sınıfı", "kapitalizm")),
        RagKazanim("AGS.T.13.7", "Tarih", "Dünya Tarihi", "Sömürgecilik",
            "Emperyalizm ve sömürgecilik hareketlerini, Afrika ve Asya'daki etkilerini açıklar.",
            listOf("sömürgecilik", "emperyalizm", "afrika", "hindistan", "çin"))
    )

    // ==================== ÜNİTE 14: ÇAĞDAŞ TARİH ====================
    val unite14_cagdasTarih = listOf(
        RagKazanim("AGS.T.14.1", "Tarih", "Çağdaş Tarih", "İki Savaş Arası Dönem",
            "1919-1939 arası dönemde dünyada yaşanan siyasi ve ekonomik gelişmeleri açıklar.",
            listOf("versay", "milletler cemiyeti", "büyük buhran", "faşizm", "nazizm")),
        RagKazanim("AGS.T.14.2", "Tarih", "Çağdaş Tarih", "II. Dünya Savaşı",
            "II. Dünya Savaşı'nın sebepleri, gelişimi ve sonuçlarını analiz eder.",
            listOf("ii. dünya savaşı", "hitler", "pearl harbor", "normandiya", "atom bombası")),
        RagKazanim("AGS.T.14.3", "Tarih", "Çağdaş Tarih", "Soğuk Savaş Dönemi",
            "Soğuk Savaş'ın özellikleri, bloklaşma ve önemli krizleri açıklar.",
            listOf("soğuk savaş", "nato", "varşova paktı", "küba krizi", "berlin duvarı")),
        RagKazanim("AGS.T.14.4", "Tarih", "Çağdaş Tarih", "Türkiye'de Çok Partili Dönem",
            "Türkiye'de çok partili hayata geçiş ve demokratikleşme sürecini değerlendirir.",
            listOf("çok partili dönem", "demokrat parti", "27 mayıs", "12 eylül")),
        RagKazanim("AGS.T.14.5", "Tarih", "Çağdaş Tarih", "Soğuk Savaş Sonrası",
            "SSCB'nin dağılması, küreselleşme ve yeni dünya düzenini açıklar.",
            listOf("sscb dağılması", "küreselleşme", "avrupa birliği", "birleşmiş milletler")),
        RagKazanim("AGS.T.14.6", "Tarih", "Çağdaş Tarih", "Günümüz Dünyası",
            "21. yüzyılda dünyada yaşanan siyasi, ekonomik ve teknolojik gelişmeleri değerlendirir.",
            listOf("terör", "göç", "enerji", "dijitalleşme", "yapay zeka"))
    )

    // ==================== TÜM KAZANIMLAR ====================
    fun tumKazanimlar(): List<RagKazanim> {
        return unite1_tarihBilimi + unite2_osmanlica + unite3_uygarlik + 
               unite4_ilkTurkler + unite5_islamTarihi + unite6_turkIslam +
               unite7_turkDunyasi + unite8_osmanli + unite9_enUzunYuzyil +
               unite10_xxYuzyil + unite11_milliMucadele + unite12_ataturk +
               unite13_dunyaTarihi + unite14_cagdasTarih
    }

    // Ünite bazlı kazanım getirme
    fun getKazanimlarByUnite(uniteId: Int): List<RagKazanim> {
        return when (uniteId) {
            1 -> unite1_tarihBilimi
            2 -> unite2_osmanlica
            3 -> unite3_uygarlik
            4 -> unite4_ilkTurkler
            5 -> unite5_islamTarihi
            6 -> unite6_turkIslam
            7 -> unite7_turkDunyasi
            8 -> unite8_osmanli
            9 -> unite9_enUzunYuzyil
            10 -> unite10_xxYuzyil
            11 -> unite11_milliMucadele
            12 -> unite12_ataturk
            13 -> unite13_dunyaTarihi
            14 -> unite14_cagdasTarih
            else -> emptyList()
        }
    }
}
