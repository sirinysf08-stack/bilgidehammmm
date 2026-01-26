package com.example.bilgideham

/**
 * KPSS GY-GK Deneme Müfredatı - RAG Veritabanı
 * 
 * 120 Soru Dağılımı:
 * - Genel Yetenek (60): Türkçe 30, Matematik 30
 * - Genel Kültür (60): Tarih 27, Coğrafya 18, Vatandaşlık 9, Güncel 6
 * 
 * Her konu için:
 * - Hedef soru sayısı (min-max)
 * - Alt konular
 * - Örnek soru tipleri
 * - Anti-halüsinasyon kuralları
 */
object KpssDenemeCurriculumData {

    // ==================== TÜRKÇE (30 SORU) ====================
    
    data class KpssTurkceKonu(
        val id: String,
        val baslik: String,
        val minSoru: Int,
        val maxSoru: Int,
        val altKonular: List<String>,
        val soruTipleri: List<String>,
        val keywords: List<String>
    )
    
    val turkceDagilim = listOf(
        KpssTurkceKonu(
            id = "paragraf",
            baslik = "Paragraf",
            minSoru = 13,
            maxSoru = 16,
            altKonular = listOf(
                "Ana Fikir / Ana Düşünce",
                "Yardımcı Fikir / Yardımcı Düşünce", 
                "Paragrafta Çıkarım",
                "Paragraf Yapısı ve Örgüsü",
                "Anlatım Teknikleri (Öyküleme, Betimleme, Açıklama, Tartışma)",
                "Paragrafta Konu",
                "Paragrafta Başlık",
                "Paragrafın Bölümleri (Giriş, Gelişme, Sonuç)",
                "Paragrafta Düşünceyi Geliştirme Yolları"
            ),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi paragrafın ana düşüncesidir?",
                "Bu parçadan aşağıdaki yargılardan hangisine ulaşılabilir?",
                "Paragrafın anlatım tekniği aşağıdakilerden hangisidir?",
                "Paragrafta asıl anlatılmak istenen nedir?",
                "Parçaya göre aşağıdakilerden hangisi söylenemez?"
            ),
            keywords = listOf("ana fikir", "yardımcı fikir", "çıkarım", "anlatım tekniği", "paragraf")
        ),
        KpssTurkceKonu(
            id = "sozel_mantik",
            baslik = "Sözel Mantık",
            minSoru = 4,
            maxSoru = 8,
            altKonular = listOf(
                "Sıralama Problemleri",
                "Tablo/Matris Problemleri",
                "Koşul Zinciri Problemleri",
                "Olasılık-Mantık Problemleri",
                "Eşleştirme Problemleri",
                "Doğru-Yanlış Çıkarımları"
            ),
            soruTipleri = listOf(
                "Verilen bilgilere göre aşağıdakilerden hangisi kesinlikle doğrudur?",
                "Buna göre kim en önde/sonda yer almaktadır?",
                "Bu durumda aşağıdakilerden hangisi mümkün değildir?",
                "Verilen koşullara göre aşağıdaki eşleştirmelerden hangisi doğrudur?"
            ),
            keywords = listOf("sıralama", "eşleştirme", "koşul", "mantık", "çıkarım")
        ),
        KpssTurkceKonu(
            id = "cumlede_anlam",
            baslik = "Cümlede Anlam",
            minSoru = 1,
            maxSoru = 5,
            altKonular = listOf(
                "Cümle Yorumlama",
                "Öznel ve Nesnel Yargı",
                "Neden-Sonuç İlişkisi",
                "Amaç-Sonuç İlişkisi",
                "Koşul-Sonuç İlişkisi",
                "Karşılaştırma"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde neden-sonuç ilişkisi vardır?",
                "Verilen cümle öznel mi nesnel mi?",
                "Cümlenin anlamca en yakın ifadesi hangisidir?"
            ),
            keywords = listOf("cümle", "anlam", "neden-sonuç", "öznel", "nesnel")
        ),
        KpssTurkceKonu(
            id = "sozcukte_anlam",
            baslik = "Sözcükte Anlam",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "Gerçek ve Mecaz Anlam",
                "Eş Anlamlı Sözcükler",
                "Zıt Anlamlı Sözcükler",
                "Eş Sesli Sözcükler",
                "Deyimler ve Atasözleri"
            ),
            soruTipleri = listOf(
                "Altı çizili sözcük hangi anlamda kullanılmıştır?",
                "Hangi cümlede sözcük mecaz anlamda kullanılmıştır?",
                "Verilen atasözünün anlamı hangisidir?"
            ),
            keywords = listOf("sözcük", "mecaz", "gerçek anlam", "deyim", "atasözü")
        ),
        KpssTurkceKonu(
            id = "yazim_kurallari",
            baslik = "Yazım Kuralları",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "Büyük Harflerin Yazımı",
                "Birleşik Sözcüklerin Yazımı",
                "Sayıların Yazımı",
                "Kısaltmaların Yazımı",
                "Yabancı Sözcüklerin Yazımı",
                "-de, -da / -ki / -mi Yazımı"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde yazım yanlışı vardır?",
                "Hangi cümlede 'de' bağlacı doğru yazılmıştır?",
                "Aşağıdakilerden hangisinin yazımı doğrudur?"
            ),
            keywords = listOf("yazım", "büyük harf", "birleşik sözcük", "de-da", "ki")
        ),
        KpssTurkceKonu(
            id = "noktalama",
            baslik = "Noktalama İşaretleri",
            minSoru = 0,
            maxSoru = 1,
            altKonular = listOf(
                "Nokta", "Virgül", "Noktalı Virgül", "İki Nokta",
                "Üç Nokta", "Soru İşareti", "Ünlem İşareti",
                "Tırnak İşareti", "Parantez", "Kesme İşareti"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde noktalama yanlışı vardır?",
                "Hangi cümlede virgül yanlış kullanılmıştır?"
            ),
            keywords = listOf("noktalama", "virgül", "nokta", "tırnak", "kesme işareti")
        ),
        KpssTurkceKonu(
            id = "anlatim_bozuklugu",
            baslik = "Anlatım Bozukluğu",
            minSoru = 0,
            maxSoru = 3,
            altKonular = listOf(
                "Sözcük Düzeyinde Anlatım Bozukluğu",
                "Cümle Düzeyinde Anlatım Bozukluğu",
                "Mantık Hataları",
                "Gereksiz Sözcük Kullanımı",
                "Tamlama Yanlışları"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde anlatım bozukluğu vardır?",
                "Hangi cümlede gereksiz sözcük kullanılmıştır?",
                "Cümledeki anlatım bozukluğunun nedeni nedir?"
            ),
            keywords = listOf("anlatım bozukluğu", "gereksiz sözcük", "mantık hatası")
        ),
        KpssTurkceKonu(
            id = "ses_bilgisi",
            baslik = "Ses Bilgisi",
            minSoru = 0,
            maxSoru = 1,
            altKonular = listOf(
                "Ünlü Uyumu (Büyük ve Küçük)",
                "Ünsüz Benzeşmesi",
                "Ünsüz Yumuşaması",
                "Ünlü Düşmesi",
                "Kaynaştırma Harfleri"
            ),
            soruTipleri = listOf(
                "Hangi sözcükte ünlü uyumu bozulmuştur?",
                "Aşağıdakilerden hangisinde ünsüz yumuşaması vardır?"
            ),
            keywords = listOf("ses bilgisi", "ünlü uyumu", "ünsüz", "kaynaştırma")
        ),
        KpssTurkceKonu(
            id = "sozcukte_yapi",
            baslik = "Sözcükte Yapı",
            minSoru = 0,
            maxSoru = 3,
            altKonular = listOf(
                "Basit Sözcük",
                "Türemiş Sözcük",
                "Birleşik Sözcük",
                "Yapım Ekleri",
                "Çekim Ekleri"
            ),
            soruTipleri = listOf(
                "Aşağıdaki sözcüklerden hangisi yapısı bakımından türemiştir?",
                "Hangi sözcük birleşik sözcüktür?",
                "Altı çizili sözcüğün yapısı aşağıdakilerden hangisiyle aynıdır?"
            ),
            keywords = listOf("yapı", "basit", "türemiş", "birleşik", "ek")
        ),
        KpssTurkceKonu(
            id = "cumle_ogeleri",
            baslik = "Cümlenin Ögeleri",
            minSoru = 0,
            maxSoru = 2,
            altKonular = listOf(
                "Özne",
                "Yüklem",
                "Nesne (Belirtili/Belirtisiz)",
                "Dolaylı Tümleç",
                "Zarf Tümleci"
            ),
            soruTipleri = listOf(
                "Cümlenin öznesi aşağıdakilerden hangisidir?",
                "Altı çizili sözcük hangi öge görevindedir?",
                "Hangi cümlede dolaylı tümleç vardır?"
            ),
            keywords = listOf("özne", "yüklem", "nesne", "tümleç", "öge")
        ),
        KpssTurkceKonu(
            id = "cumle_turleri",
            baslik = "Cümle Türleri",
            minSoru = 0,
            maxSoru = 1,
            altKonular = listOf(
                "Yapısına Göre (Basit, Birleşik, Bağlı, Sıralı)",
                "Anlamına Göre (Olumlu, Olumsuz, Soru)",
                "Yüklemin Yerine Göre (Kurallı, Devrik)"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisi birleşik cümledir?",
                "Hangi cümle yapısına göre sıralı cümledir?"
            ),
            keywords = listOf("cümle türü", "basit", "birleşik", "sıralı", "bağlı")
        )
    )

    // ==================== MATEMATİK (30 SORU) ====================
    
    data class KpssMatematikKonu(
        val id: String,
        val baslik: String,
        val minSoru: Int,
        val maxSoru: Int,
        val altKonular: List<String>,
        val problemTipleri: List<String>,
        val keywords: List<String>
    )
    
    val matematikDagilim = listOf(
        KpssMatematikKonu(
            id = "problemler",
            baslik = "Problemler",
            minSoru = 10,
            maxSoru = 14,
            altKonular = listOf(
                "Sayı Problemleri",
                "Yaş Problemleri",
                "Hareket Problemleri",
                "Yüzde ve Kâr-Zarar Problemleri",
                "Karışım Problemleri",
                "İşçi-Havuz Problemleri",
                "Ortalama Problemleri",
                "Oran-Orantı Problemleri"
            ),
            problemTipleri = listOf(
                "Bir sayının 2 katının 5 fazlası...",
                "Ahmet'in yaşı Ayşe'nin yaşının 3 katı...",
                "A ve B şehirleri arası mesafe...",
                "Maliyet fiyatı X TL olan ürün %20 kârla...",
                "Birlikte çalışırlarsa işi kaç günde bitirirler?"
            ),
            keywords = listOf("problem", "sayı", "yaş", "hareket", "kâr", "işçi", "havuz", "oran")
        ),
        KpssMatematikKonu(
            id = "sayilar_islemler",
            baslik = "Sayılar ve İşlemler",
            minSoru = 6,
            maxSoru = 9,
            altKonular = listOf(
                "Doğal Sayılar ve İşlemler",
                "Tam Sayılar",
                "Rasyonel Sayılar",
                "Ondalık Sayılar",
                "EBOB ve EKOK",
                "Bölme ve Bölünebilme",
                "Asal Sayılar ve Çarpanlar"
            ),
            problemTipleri = listOf(
                "24 ve 36 sayılarının EBOB'u kaçtır?",
                "5/6 + 3/4 işleminin sonucu kaçtır?",
                "Hangi sayı 3, 4 ve 5'e tam bölünür?"
            ),
            keywords = listOf("sayı", "EBOB", "EKOK", "bölünebilme", "asal", "çarpan")
        ),
        KpssMatematikKonu(
            id = "uslu_koklu",
            baslik = "Üslü-Köklü-Eşitsizlik",
            minSoru = 4,
            maxSoru = 6,
            altKonular = listOf(
                "Üslü İfadeler ve Kuralları",
                "Köklü İfadeler",
                "Basit Eşitsizlikler",
                "Mutlak Değer"
            ),
            problemTipleri = listOf(
                "2^3 × 2^4 işleminin sonucu kaçtır?",
                "√50 ifadesinin en sade hali nedir?",
                "|x - 3| < 5 eşitsizliğini sağlayan tam sayılar..."
            ),
            keywords = listOf("üslü", "köklü", "eşitsizlik", "mutlak değer", "kuvvet")
        ),
        KpssMatematikKonu(
            id = "denklem_fonksiyon",
            baslik = "Denklem/Fonksiyon/Kümeler",
            minSoru = 2,
            maxSoru = 4,
            altKonular = listOf(
                "Birinci Dereceden Denklemler",
                "İkinci Dereceden Denklemler",
                "Fonksiyon Kavramı",
                "Küme İşlemleri",
                "Modüler Aritmetik"
            ),
            problemTipleri = listOf(
                "2x + 5 = 11 denkleminin çözüm kümesi...",
                "f(x) = 2x + 1 ise f(3) kaçtır?",
                "A ∩ B kümesinin eleman sayısı kaçtır?"
            ),
            keywords = listOf("denklem", "fonksiyon", "küme", "çözüm kümesi", "modüler")
        ),
        KpssMatematikKonu(
            id = "veri_tablo",
            baslik = "Veri/Tablo/Grafik",
            minSoru = 2,
            maxSoru = 4,
            altKonular = listOf(
                "Tablo Yorumlama",
                "Grafik Okuma",
                "Ortalama Hesaplama",
                "Veri Analizi"
            ),
            problemTipleri = listOf(
                "Tabloya göre en çok satış hangi ayda yapılmıştır?",
                "Grafiğe göre artış oranı yüzde kaçtır?",
                "Verilerin ortalaması kaçtır?"
            ),
            keywords = listOf("tablo", "grafik", "veri", "ortalama", "yorum")
        ),
        KpssMatematikKonu(
            id = "mantik_sekil",
            baslik = "Mantık/Şekil",
            minSoru = 2,
            maxSoru = 6,
            altKonular = listOf(
                "Sayısal Mantık",
                "Örüntü ve Diziler",
                "Şekil Yetenek Soruları",
                "Geometrik Örüntüler"
            ),
            problemTipleri = listOf(
                "2, 5, 11, 23, ? dizisinde soru işareti yerine...",
                "Verilen şekillerin kuralına göre 5. şekil hangidir?",
                "Verilen kalıptan hangi cisim oluşur?"
            ),
            keywords = listOf("mantık", "şekil", "dizi", "örüntü", "kural")
        ),
        KpssMatematikKonu(
            id = "olasilik",
            baslik = "Olasılık-Perm/Komb",
            minSoru = 0,
            maxSoru = 2,
            altKonular = listOf(
                "Temel Olasılık",
                "Permütasyon",
                "Kombinasyon"
            ),
            problemTipleri = listOf(
                "Bir zarın atılmasında tek sayı gelme olasılığı...",
                "5 kişiden 2 kişi seçilecektir. Kaç farklı seçim yapılabilir?",
                "KPSS kelimesinin harfleri kaç farklı şekilde dizilir?"
            ),
            keywords = listOf("olasılık", "permütasyon", "kombinasyon", "seçim", "dizilim")
        )
    )

    // ==================== TARİH (27 SORU) ====================
    
    data class KpssTarihKonu(
        val id: String,
        val baslik: String,
        val minSoru: Int,
        val maxSoru: Int,
        val altKonular: List<String>,
        val onemliTarihler: List<String>,
        val keywords: List<String>
    )
    
    val tarihDagilim = listOf(
        KpssTarihKonu(
            id = "osmanli_siyasi",
            baslik = "Osmanlı Siyaseti",
            minSoru = 3,
            maxSoru = 5,
            altKonular = listOf(
                "Kuruluş Dönemi (1299-1453)",
                "Yükselme Dönemi (1453-1579)",
                "Duraklama Dönemi (1579-1699)",
                "Gerileme Dönemi (1699-1792)",
                "Dağılma Dönemi (1792-1922)"
            ),
            onemliTarihler = listOf(
                "1299 - Osmanlı Kuruluşu",
                "1326 - Bursa'nın Fethi",
                "1361 - Edirne'nin Fethi",
                "1389 - I. Kosova Savaşı",
                "1453 - İstanbul'un Fethi"
            ),
            keywords = listOf("osmanlı", "padişah", "fetih", "savaş", "antlaşma")
        ),
        KpssTarihKonu(
            id = "osmanli_kultur",
            baslik = "Osmanlı Kültür-Uygarlık",
            minSoru = 3,
            maxSoru = 6,
            altKonular = listOf(
                "Devlet Yönetimi (Divan, Eyalet Sistemi)",
                "Ordu Teşkilatı (Yeniçeri, Tımarlı Sipahi)",
                "Toprak Sistemi (Tımar, Zeamet, Has)",
                "Eğitim ve Bilim (Medrese, Mektep)",
                "Sanat ve Mimari (Cami, Han, Hamam)"
            ),
            onemliTarihler = listOf(),
            keywords = listOf("divan", "tımar", "yeniçeri", "medrese", "vakıf")
        ),
        KpssTarihKonu(
            id = "yirminci_yy_osmanli",
            baslik = "20. Yüzyıl Osmanlı",
            minSoru = 3,
            maxSoru = 4,
            altKonular = listOf(
                "II. Meşrutiyet Dönemi",
                "İttihat ve Terakki",
                "Balkan Savaşları (1912-1913)",
                "I. Dünya Savaşı (1914-1918)",
                "Mondros Mütarekesi (1918)"
            ),
            onemliTarihler = listOf(
                "1908 - II. Meşrutiyet İlanı",
                "1912-1913 - Balkan Savaşları",
                "1914-1918 - I. Dünya Savaşı",
                "1915 - Çanakkale Savaşı",
                "1918 - Mondros Mütarekesi"
            ),
            keywords = listOf("meşrutiyet", "ittihat terakki", "balkan", "dünya savaşı", "mondros")
        ),
        KpssTarihKonu(
            id = "kurtulus_savasi",
            baslik = "Kurtuluş Savaşı",
            minSoru = 2,
            maxSoru = 3,
            altKonular = listOf(
                "Milli Mücadelenin Hazırlık Dönemi",
                "Kongreler (Erzurum, Sivas)",
                "TBMM'nin Açılışı (1920)",
                "Cepheler (Doğu, Güney, Batı)",
                "Mudanya ve Lozan"
            ),
            onemliTarihler = listOf(
                "19 Mayıs 1919 - Samsun'a Çıkış",
                "23 Temmuz 1919 - Erzurum Kongresi",
                "4-11 Eylül 1919 - Sivas Kongresi",
                "23 Nisan 1920 - TBMM Açılışı",
                "30 Ağustos 1922 - Büyük Taarruz"
            ),
            keywords = listOf("kurtuluş", "kongre", "TBMM", "cephe", "milli mücadele")
        ),
        KpssTarihKonu(
            id = "inkilap_tarihi",
            baslik = "İnkılap Tarihi",
            minSoru = 4,
            maxSoru = 6,
            altKonular = listOf(
                "Siyasi İnkılaplar (Saltanatın Kaldırılması, Cumhuriyet)",
                "Hukuk İnkılapları (Anayasalar, Medeni Kanun)",
                "Eğitim-Kültür İnkılapları (Harf, Kıyafet)",
                "Ekonomik İnkılaplar (İzmir İktisat Kongresi)",
                "Toplumsal İnkılaplar (Takvim, Ölçü)"
            ),
            onemliTarihler = listOf(
                "1 Kasım 1922 - Saltanatın Kaldırılması",
                "29 Ekim 1923 - Cumhuriyetin İlanı",
                "3 Mart 1924 - Halifeliğin Kaldırılması",
                "1 Kasım 1928 - Harf İnkılabı",
                "17 Şubat 1926 - Medeni Kanun"
            ),
            keywords = listOf("inkılap", "cumhuriyet", "halifelik", "harf", "medeni kanun")
        ),
        KpssTarihKonu(
            id = "ataturk_donemi",
            baslik = "Atatürk Dönemi İç/Dış Politika",
            minSoru = 2,
            maxSoru = 2,
            altKonular = listOf(
                "İç Politika (Çok Partili Hayat Denemeleri)",
                "Dış Politika (Milletler Cemiyeti, Balkan Antantı)",
                "Ekonomi Politikaları"
            ),
            onemliTarihler = listOf(
                "1932 - Milletler Cemiyeti'ne Üyelik",
                "1934 - Balkan Antantı",
                "1936 - Montrö Boğazlar Sözleşmesi",
                "1937 - Sadabat Paktı"
            ),
            keywords = listOf("dış politika", "antant", "pakt", "milletler cemiyeti")
        ),
        KpssTarihKonu(
            id = "ataturk_ilkeleri",
            baslik = "Atatürk İlke ve İnkılapları",
            minSoru = 2,
            maxSoru = 2,
            altKonular = listOf(
                "Cumhuriyetçilik",
                "Milliyetçilik",
                "Halkçılık",
                "Devletçilik",
                "Laiklik",
                "İnkılapçılık (Devrimcilik)"
            ),
            onemliTarihler = listOf(),
            keywords = listOf("ilke", "cumhuriyetçilik", "milliyetçilik", "laiklik", "devletçilik")
        ),
        KpssTarihKonu(
            id = "cagdas_turk_dunya",
            baslik = "Çağdaş Türk ve Dünya Tarihi",
            minSoru = 2,
            maxSoru = 3,
            altKonular = listOf(
                "II. Dünya Savaşı ve Türkiye",
                "Soğuk Savaş Dönemi",
                "NATO ve Türkiye",
                "Kıbrıs Meselesi",
                "Avrupa Birliği Süreci"
            ),
            onemliTarihler = listOf(
                "1945 - II. Dünya Savaşı Sonu",
                "1952 - NATO'ya Üyelik",
                "1974 - Kıbrıs Barış Harekâtı",
                "1999 - AB Adaylığı"
            ),
            keywords = listOf("dünya savaşı", "soğuk savaş", "NATO", "kıbrıs", "AB")
        ),
        KpssTarihKonu(
            id = "turk_islam",
            baslik = "Türk-İslam / İlk Türk Devletleri",
            minSoru = 2,
            maxSoru = 4,
            altKonular = listOf(
                "İlk Türk Devletleri (Hunlar, Göktürkler, Uygurlar)",
                "İslamiyet Öncesi Türk Kültürü",
                "İlk Müslüman Türk Devletleri (Karahanlılar, Gazneliler)",
                "Büyük Selçuklular",
                "Anadolu Selçukluları"
            ),
            onemliTarihler = listOf(
                "751 - Talas Savaşı",
                "1040 - Dandanakan Savaşı",
                "1071 - Malazgirt Savaşı",
                "1176 - Miryokefalon Savaşı"
            ),
            keywords = listOf("türk", "islam", "selçuklu", "göktürk", "uygur", "malazgirt")
        )
    )

    // ==================== COĞRAFYA (18 SORU) ====================
    
    data class KpssCografyaKonu(
        val id: String,
        val baslik: String,
        val minSoru: Int,
        val maxSoru: Int,
        val altKonular: List<String>,
        val turkiyeVerileri: List<String>,
        val keywords: List<String>
    )
    
    val cografyaDagilim = listOf(
        KpssCografyaKonu(
            id = "fiziki_ozellikler",
            baslik = "Türkiye'nin Fiziki Özellikleri",
            minSoru = 4,
            maxSoru = 7,
            altKonular = listOf(
                "Yer Şekilleri (Dağlar, Ovalar, Platolar)",
                "Akarsular ve Göller",
                "Kıyı Şekilleri",
                "Jeolojik Yapı (Deprem, Fay Hatları)"
            ),
            turkiyeVerileri = listOf(
                "En yüksek dağ: Ağrı Dağı (5137 m)",
                "En uzun akarsu: Kızılırmak (1355 km)",
                "En büyük göl: Van Gölü",
                "En geniş ova: Konya Ovası"
            ),
            keywords = listOf("dağ", "ova", "akarsu", "göl", "plato", "fay")
        ),
        KpssCografyaKonu(
            id = "nufus_yerlesme",
            baslik = "Nüfus ve Yerleşme",
            minSoru = 2,
            maxSoru = 3,
            altKonular = listOf(
                "Nüfus Yoğunluğu ve Dağılışı",
                "Göç (İç ve Dış)",
                "Kentleşme",
                "Nüfus Politikaları"
            ),
            turkiyeVerileri = listOf(
                "En kalabalık il: İstanbul",
                "Nüfus yoğunluğu en yüksek: Marmara Bölgesi",
                "Göç alan bölgeler: Marmara, Ege, Akdeniz"
            ),
            keywords = listOf("nüfus", "göç", "kentleşme", "yoğunluk", "yerleşme")
        ),
        KpssCografyaKonu(
            id = "iklim_bitki",
            baslik = "İklim ve Bitki Örtüsü",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "İklim Tipleri (Karadeniz, Akdeniz, Karasal, Marmara)",
                "Sıcaklık ve Yağış Dağılışı",
                "Bitki Örtüsü"
            ),
            turkiyeVerileri = listOf(
                "En yağışlı bölge: Doğu Karadeniz",
                "En kurak bölge: İç Anadolu, Güneydoğu Anadolu",
                "En sıcak bölge: Güneydoğu Anadolu"
            ),
            keywords = listOf("iklim", "yağış", "sıcaklık", "bitki örtüsü", "akdeniz", "karadeniz")
        ),
        KpssCografyaKonu(
            id = "konum",
            baslik = "Coğrafi Konum",
            minSoru = 1,
            maxSoru = 1,
            altKonular = listOf(
                "Matematiksel Konum (Enlem, Boylam)",
                "Özel Konum",
                "Türkiye'nin Konumunun Sonuçları"
            ),
            turkiyeVerileri = listOf(
                "Enlem: 36°-42° Kuzey",
                "Boylam: 26°-45° Doğu",
                "Yüzölçümü: 783.562 km²"
            ),
            keywords = listOf("konum", "enlem", "boylam", "matematiksel", "özel konum")
        ),
        KpssCografyaKonu(
            id = "tarim",
            baslik = "Tarım",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "Tarım Ürünleri (Tahıl, Endüstri Bitkileri)",
                "Tarım Bölgeleri",
                "Tarımı Etkileyen Faktörler"
            ),
            turkiyeVerileri = listOf(
                "Buğday: İç Anadolu, Güneydoğu",
                "Pamuk: Çukurova, Ege",
                "Fındık: Doğu Karadeniz",
                "Çay: Rize, Trabzon"
            ),
            keywords = listOf("tarım", "buğday", "pamuk", "fındık", "çay", "zeytin")
        ),
        KpssCografyaKonu(
            id = "maden_enerji",
            baslik = "Maden ve Enerji",
            minSoru = 1,
            maxSoru = 3,
            altKonular = listOf(
                "Madenler (Demir, Bakır, Bor, Krom)",
                "Enerji Kaynakları",
                "Yenilenebilir Enerji"
            ),
            turkiyeVerileri = listOf(
                "Bor: Dünya 1.'si (Balıkesir, Eskişehir, Kütahya)",
                "Krom: Elazığ, Fethiye, Guleman",
                "Demir: Divriği, Hekimhan"
            ),
            keywords = listOf("maden", "enerji", "bor", "krom", "demir", "yenilenebilir")
        ),
        KpssCografyaKonu(
            id = "sanayi",
            baslik = "Sanayi",
            minSoru = 0,
            maxSoru = 2,
            altKonular = listOf(
                "Sanayi Kolları",
                "Sanayi Bölgeleri",
                "Organize Sanayi"
            ),
            turkiyeVerileri = listOf(
                "En gelişmiş sanayi bölgesi: Marmara",
                "Otomotiv: Bursa, Kocaeli",
                "Tekstil: İstanbul, Denizli, Gaziantep"
            ),
            keywords = listOf("sanayi", "fabrika", "üretim", "organize", "tekstil")
        ),
        KpssCografyaKonu(
            id = "ulasim_ticaret_turizm",
            baslik = "Ulaşım/Ticaret/Turizm",
            minSoru = 2,
            maxSoru = 4,
            altKonular = listOf(
                "Ulaşım Ağları (Kara, Deniz, Hava, Demir)",
                "Dış Ticaret",
                "Turizm Bölgeleri"
            ),
            turkiyeVerileri = listOf(
                "En işlek liman: İstanbul (Ambarlı)",
                "En çok turist çeken: Antalya",
                "İhracat ortakları: Almanya, İngiltere, ABD"
            ),
            keywords = listOf("ulaşım", "ticaret", "turizm", "liman", "ihracat", "ithalat")
        ),
        KpssCografyaKonu(
            id = "hayvancilik",
            baslik = "Hayvancılık",
            minSoru = 0,
            maxSoru = 1,
            altKonular = listOf(
                "Büyükbaş Hayvancılık",
                "Küçükbaş Hayvancılık",
                "Arıcılık, İpekböcekçiliği",
                "Su Ürünleri"
            ),
            turkiyeVerileri = listOf(
                "Küçükbaş: Doğu Anadolu, İç Anadolu",
                "Büyükbaş: Marmara, Ege, Karadeniz",
                "Arıcılık: Muğla, Ordu"
            ),
            keywords = listOf("hayvancılık", "büyükbaş", "küçükbaş", "arıcılık", "balıkçılık")
        )
    )

    // ==================== VATANDAŞLIK (9 SORU) ====================
    
    data class KpssVatandaslikKonu(
        val id: String,
        val baslik: String,
        val minSoru: Int,
        val maxSoru: Int,
        val altKonular: List<String>,
        val anayasaMaddeleri: List<String>,
        val keywords: List<String>
    )
    
    val vatandaslikDagilim = listOf(
        KpssVatandaslikKonu(
            id = "temel_hukuk",
            baslik = "Temel Hukuk",
            minSoru = 2,
            maxSoru = 3,
            altKonular = listOf(
                "Hukukun Tanımı ve Kaynakları",
                "Hukuk Dalları (Kamu, Özel)",
                "Normlar Hiyerarşisi",
                "Hak ve Borç Kavramları"
            ),
            anayasaMaddeleri = listOf(),
            keywords = listOf("hukuk", "norm", "kaynak", "kamu hukuku", "özel hukuk")
        ),
        KpssVatandaslikKonu(
            id = "anayasa_ilkeleri",
            baslik = "1982 Anayasası Temel İlkeleri",
            minSoru = 0,
            maxSoru = 2,
            altKonular = listOf(
                "Devletin Temel Nitelikleri (Md. 1-3)",
                "Değiştirilemez Maddeler",
                "Cumhuriyetin Temel Organları",
                "Egemenlik ve Başlangıç İlkeleri"
            ),
            anayasaMaddeleri = listOf(
                "Madde 1: Devletin şekli",
                "Madde 2: Cumhuriyetin nitelikleri",
                "Madde 3: Devletin bütünlüğü, resmi dil, bayrak, başkent",
                "Madde 4: Değiştirilemez hükümler"
            ),
            keywords = listOf("anayasa", "cumhuriyet", "değiştirilemez", "devlet", "egemenlik")
        ),
        KpssVatandaslikKonu(
            id = "yasama",
            baslik = "Yasama",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "TBMM (Kuruluş, Görevler, Yetkileri)",
                "Milletvekilliği",
                "Kanun Yapma Süreci",
                "TBMM'nin Toplantı ve Karar Yeter Sayısı"
            ),
            anayasaMaddeleri = listOf(
                "Madde 75: Milletvekili sayısı (600)",
                "Madde 76: Milletvekili seçilme yeterliliği",
                "Madde 96: Toplantı ve karar yeter sayısı"
            ),
            keywords = listOf("TBMM", "milletvekili", "yasama", "kanun", "meclis")
        ),
        KpssVatandaslikKonu(
            id = "yurutme",
            baslik = "Yürütme",
            minSoru = 1,
            maxSoru = 2,
            altKonular = listOf(
                "Cumhurbaşkanlığı (Seçim, Görev, Yetki)",
                "Cumhurbaşkanlığı Kararnamesi",
                "Olağanüstü Hal",
                "Cumhurbaşkanlığı Sisteminin İşleyişi"
            ),
            anayasaMaddeleri = listOf(
                "Madde 101: Cumhurbaşkanı adaylığı ve seçimi",
                "Madde 104: Cumhurbaşkanının görev ve yetkileri",
                "Madde 119: Olağanüstü hal yönetimi"
            ),
            keywords = listOf("cumhurbaşkanı", "yürütme", "kararname", "olağanüstü hal")
        ),
        KpssVatandaslikKonu(
            id = "yargi",
            baslik = "Yargı",
            minSoru = 0,
            maxSoru = 2,
            altKonular = listOf(
                "Yargı Bağımsızlığı ve Hakimlik Teminatı",
                "Yüksek Mahkemeler (Anayasa Mahkemesi, Yargıtay, Danıştay)",
                "Bireysel Başvuru",
                "Hakimler ve Savcılar Kurulu"
            ),
            anayasaMaddeleri = listOf(
                "Madde 138: Mahkemelerin bağımsızlığı",
                "Madde 139: Hakimlik ve savcılık teminatı",
                "Madde 146: Anayasa Mahkemesi"
            ),
            keywords = listOf("yargı", "mahkeme", "anayasa mahkemesi", "yargıtay", "danıştay")
        ),
        KpssVatandaslikKonu(
            id = "idare_hukuku",
            baslik = "İdare Hukuku",
            minSoru = 1,
            maxSoru = 3,
            altKonular = listOf(
                "İdari Teşkilat (Merkezi, Yerel)",
                "İdarenin İşlemleri",
                "Kamu Görevlileri",
                "İdari Yargı"
            ),
            anayasaMaddeleri = listOf(
                "Madde 123: İdarenin bütünlüğü",
                "Madde 126: Merkezi idare",
                "Madde 127: Mahalli idareler"
            ),
            keywords = listOf("idare", "valilik", "belediye", "merkezi", "yerel yönetim")
        ),
        KpssVatandaslikKonu(
            id = "temel_haklar",
            baslik = "Temel Hak ve Hürriyetler",
            minSoru = 0,
            maxSoru = 1,
            altKonular = listOf(
                "Kişi Hakları",
                "Sosyal ve Ekonomik Haklar",
                "Siyasi Haklar",
                "Temel Hakların Sınırlandırılması"
            ),
            anayasaMaddeleri = listOf(
                "Madde 12: Temel hak ve hürriyetlerin niteliği",
                "Madde 13: Sınırlama usulü",
                "Madde 17: Kişi dokunulmazlığı"
            ),
            keywords = listOf("hak", "hürriyet", "kişi hakları", "sosyal haklar", "siyasi haklar")
        )
    )

    // ==================== GÜNCEL BİLGİLER (6 SORU) ====================
    
    data class KpssGuncelKonu(
        val id: String,
        val baslik: String,
        val hedefSoru: Int,
        val altKonular: List<String>,
        val ornekKonular: List<String>,
        val keywords: List<String>
    )
    
    val guncelDagilim = listOf(
        KpssGuncelKonu(
            id = "turkiye_gundemi",
            baslik = "Türkiye Gündemi",
            hedefSoru = 1,
            altKonular = listOf(
                "Yeni Kurumlar ve Düzenlemeler",
                "Büyük Kamu Projeleri",
                "Önemli Yasal Değişiklikler"
            ),
            ornekKonular = listOf(
                "Türkiye Yüzyılı vizyonu",
                "Mega projeler (köprüler, tüneller, havalimanları)",
                "Yeni bakanlık/kurum yapılanmaları"
            ),
            keywords = listOf("türkiye", "proje", "kurum", "düzenleme", "yasa")
        ),
        KpssGuncelKonu(
            id = "ekonomi_finans",
            baslik = "Ekonomi ve Finans",
            hedefSoru = 1,
            altKonular = listOf(
                "Temel Ekonomik Göstergeler",
                "Önemli Kurumlar (TCMB, SPK)",
                "Büyük Düzenlemeler"
            ),
            ornekKonular = listOf(
                "Enflasyon, faiz politikaları",
                "Merkez Bankası kararları",
                "Ekonomik reformlar"
            ),
            keywords = listOf("ekonomi", "finans", "TCMB", "enflasyon", "faiz")
        ),
        KpssGuncelKonu(
            id = "uluslararasi",
            baslik = "Uluslararası Gelişmeler",
            hedefSoru = 1,
            altKonular = listOf(
                "Türkiye'nin Dış İlişkileri",
                "Uluslararası Örgütler",
                "Küresel Gelişmeler"
            ),
            ornekKonular = listOf(
                "NATO, AB, BM ilişkileri",
                "Bölgesel gelişmeler",
                "Diplomatik anlaşmalar"
            ),
            keywords = listOf("uluslararası", "NATO", "AB", "BM", "diplomasi")
        ),
        KpssGuncelKonu(
            id = "bilim_teknoloji",
            baslik = "Bilim-Teknoloji/Sağlık/Çevre",
            hedefSoru = 1,
            altKonular = listOf(
                "Türk Bilim İnsanları ve Başarıları",
                "Teknolojik Gelişmeler (Yerli Üretim)",
                "Sağlık ve Çevre Günceleri"
            ),
            ornekKonular = listOf(
                "TOGG, KAAN, uzay projeleri",
                "TÜBİTAK, ASELSAN başarıları",
                "Paris Anlaşması, iklim hedefleri"
            ),
            keywords = listOf("bilim", "teknoloji", "sağlık", "çevre", "yerli üretim")
        ),
        KpssGuncelKonu(
            id = "kultur_sanat_spor",
            baslik = "Kültür-Sanat-Spor",
            hedefSoru = 1,
            altKonular = listOf(
                "Önemli Spor Başarıları",
                "Kültürel Etkinlikler",
                "Sanat ve Edebiyat Ödülleri"
            ),
            ornekKonular = listOf(
                "Olimpiyat, Dünya Kupası başarıları",
                "UNESCO Dünya Mirası listesi",
                "Uluslararası festival ve ödüller"
            ),
            keywords = listOf("spor", "kültür", "sanat", "olimpiyat", "ödül")
        ),
        KpssGuncelKonu(
            id = "yilin_dosyasi",
            baslik = "Yılın Dosyası",
            hedefSoru = 1,
            altKonular = listOf(
                "O Yılın En Çok Konuşulan Konusu",
                "Önemli Dönüm Noktaları"
            ),
            ornekKonular = listOf(
                "Güncel olaylar ve gelişmeler",
                "Yıl dönümleri ve kutlamalar"
            ),
            keywords = listOf("gündem", "aktüel", "dosya", "önemli olay")
        )
    )

    // ==================== DENEME BLUEPRINTI ====================
    
    data class DenemeBlueprint(
        val ders: String,
        val soruSayisi: Int,
        val konuDagilimi: Map<String, IntRange>
    )
    
    val denemeYapisi = listOf(
        DenemeBlueprint(
            ders = "Türkçe",
            soruSayisi = 30,
            konuDagilimi = turkceDagilim.associate { it.id to (it.minSoru..it.maxSoru) }
        ),
        DenemeBlueprint(
            ders = "Matematik",
            soruSayisi = 30,
            konuDagilimi = matematikDagilim.associate { it.id to (it.minSoru..it.maxSoru) }
        ),
        DenemeBlueprint(
            ders = "Tarih",
            soruSayisi = 27,
            konuDagilimi = tarihDagilim.associate { it.id to (it.minSoru..it.maxSoru) }
        ),
        DenemeBlueprint(
            ders = "Coğrafya",
            soruSayisi = 18,
            konuDagilimi = cografyaDagilim.associate { it.id to (it.minSoru..it.maxSoru) }
        ),
        DenemeBlueprint(
            ders = "Vatandaşlık",
            soruSayisi = 9,
            konuDagilimi = vatandaslikDagilim.associate { it.id to (it.minSoru..it.maxSoru) }
        ),
        DenemeBlueprint(
            ders = "Güncel",
            soruSayisi = 6,
            konuDagilimi = guncelDagilim.associate { it.id to (1..1) }
        )
    )
    
    // ==================== YARDIMCI FONKSİYONLAR ====================
    
    /**
     * Belirli bir ders için rastgele konu dağılımı üretir
     * Toplam soru sayısına uygun şekilde dağıtır
     */
    fun generateKonuDagilimi(ders: String): Map<String, Int> {
        val blueprint = denemeYapisi.find { it.ders == ders } ?: return emptyMap()
        val result = mutableMapOf<String, Int>()
        var remainingSoru = blueprint.soruSayisi
        
        val konular = blueprint.konuDagilimi.keys.toList().shuffled()
        
        // Önce minimum değerleri ata
        for (konu in konular) {
            val range = blueprint.konuDagilimi[konu] ?: continue
            result[konu] = range.first
            remainingSoru -= range.first
        }
        
        // Kalan soruları rastgele dağıt
        while (remainingSoru > 0) {
            val availableKonular = konular.filter { konu ->
                val current = result[konu] ?: 0
                val max = blueprint.konuDagilimi[konu]?.last ?: 0
                current < max
            }
            if (availableKonular.isEmpty()) break
            
            val selected = availableKonular.random()
            result[selected] = (result[selected] ?: 0) + 1
            remainingSoru--
        }
        
        return result
    }
    
    /**
     * Türkçe konusu için detaylı prompt bilgisi döner
     */
    fun getTurkceKonuDetay(konuId: String): KpssTurkceKonu? {
        return turkceDagilim.find { it.id == konuId }
    }
    
    /**
     * Matematik konusu için detaylı prompt bilgisi döner
     */
    fun getMatematikKonuDetay(konuId: String): KpssMatematikKonu? {
        return matematikDagilim.find { it.id == konuId }
    }
    
    /**
     * Tarih konusu için detaylı prompt bilgisi döner
     */
    fun getTarihKonuDetay(konuId: String): KpssTarihKonu? {
        return tarihDagilim.find { it.id == konuId }
    }
    
    /**
     * Coğrafya konusu için detaylı prompt bilgisi döner
     */
    fun getCografyaKonuDetay(konuId: String): KpssCografyaKonu? {
        return cografyaDagilim.find { it.id == konuId }
    }
    
    /**
     * Vatandaşlık konusu için detaylı prompt bilgisi döner
     */
    fun getVatandaslikKonuDetay(konuId: String): KpssVatandaslikKonu? {
        return vatandaslikDagilim.find { it.id == konuId }
    }
    
    /**
     * Güncel konusu için detaylı prompt bilgisi döner
     */
    fun getGuncelKonuDetay(konuId: String): KpssGuncelKonu? {
        return guncelDagilim.find { it.id == konuId }
    }
}
