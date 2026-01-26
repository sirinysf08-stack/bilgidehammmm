package com.example.bilgideham

/**
 * KPSS RAG VERİTABANI
 * 
 * Yayınevi kalitesinde, ÖSYM formatına uyumlu soru üretimi için
 * kapsamlı müfredat veritabanı ve anti-halüsinasyon koruması.
 * 
 * Her ders için:
 * - Detaylı konu ağacı
 * - ÖSYM favori konuları ve ağırlıkları
 * - Örnek soru formatları
 * - Gerçek veriler (tarihler, isimler, sayılar)
 * - Anti-halüsinasyon kuralları
 */
object KpssRagDatabase {

    // ==================== TÜRKÇE ====================
    
    data class TurkceKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int, // 1-10 arası, ÖSYM'de çıkma sıklığı
        val altKonular: List<String>,
        val soruTipleri: List<String>,
        val onemliKurallar: List<String>
    )
    
    val turkceKonulari = listOf(
        // PARAGRAF - EN YOĞUN ALAN (%40-50)
        TurkceKonu(
            id = "paragraf_ana_dusunce",
            baslik = "Ana Düşünce / Ana Fikir",
            agirlik = 10,
            altKonular = listOf(
                "Paragrafın ana düşüncesi",
                "Yazarın vermek istediği mesaj",
                "Paragraftan çıkarılabilecek sonuç"
            ),
            soruTipleri = listOf(
                "Bu parçadan aşağıdaki yargılardan hangisine ulaşılabilir?",
                "Bu parçada asıl anlatılmak istenen aşağıdakilerden hangisidir?",
                "Bu parçanın ana düşüncesi aşağıdakilerden hangisidir?",
                "Bu parçada vurgulanan düşünce aşağıdakilerden hangisidir?"
            ),
            onemliKurallar = listOf(
                "Paragraf 8-12 cümle, 120-180 kelime olmalı",
                "Tek bir ana fikir içermeli",
                "Şıklar birbiriyle karıştırılabilir olmalı ama sadece biri tam doğru"
            )
        ),
        TurkceKonu(
            id = "paragraf_baslik",
            baslik = "Başlık Bulma",
            agirlik = 8,
            altKonular = listOf(
                "Paragrafın konusunu yansıtan başlık",
                "Ana fikri özetleyen başlık"
            ),
            soruTipleri = listOf(
                "Bu parçaya en uygun başlık aşağıdakilerden hangisidir?",
                "Bu parçanın başlığı aşağıdakilerden hangisi olabilir?"
            ),
            onemliKurallar = listOf(
                "Başlık kısa ve öz olmalı (2-4 kelime)",
                "Paragrafın tamamını kapsamalı"
            )
        ),
        TurkceKonu(
            id = "paragraf_akis",
            baslik = "Paragrafta Anlam Akışı",
            agirlik = 9,
            altKonular = listOf(
                "Düşüncenin akışını bozan cümle",
                "Paragrafta yeri değiştirilmesi gereken cümle",
                "Paragraf tamamlama"
            ),
            soruTipleri = listOf(
                "Bu parçada numaralanmış cümlelerden hangisi düşüncenin akışını bozmaktadır?",
                "Bu parçada numaralanmış cümlelerden hangileri yer değiştirirse parçanın anlam bütünlüğü sağlanır?",
                "Bu parçanın sonuna aşağıdakilerden hangisi getirilirse anlam bütünlüğü sağlanır?"
            ),
            onemliKurallar = listOf(
                "Paragrafta (I), (II), (III), (IV), (V) şeklinde numaralandırma kullan",
                "Bir cümle açıkça kopuk olmalı"
            )
        ),
        
        // DİL BİLGİSİ (%25-30)
        TurkceKonu(
            id = "sozcuk_turleri",
            baslik = "Sözcük Türleri",
            agirlik = 8,
            altKonular = listOf(
                "İsim (ad)", "Sıfat", "Zarf", "Zamir", "Fiil",
                "Edat", "Bağlaç", "Ünlem"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde altı çizili sözcük sıfat olarak kullanılmıştır?",
                "\"...\" cümlesinde kaç tane zarf vardır?",
                "Aşağıdakilerin hangisinde isim tamlaması kullanılmıştır?"
            ),
            onemliKurallar = listOf(
                "Altı çizili kelimeyi **_bu şekilde_** göster",
                "Sözcük bağlamda değerlendirilmeli"
            )
        ),
        TurkceKonu(
            id = "cumle_ogeleri",
            baslik = "Cümlenin Öğeleri",
            agirlik = 7,
            altKonular = listOf(
                "Özne", "Yüklem", "Nesne (belirtili/belirtisiz)",
                "Dolaylı tümleç", "Zarf tümleci"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde dolaylı tümleç kullanılmıştır?",
                "\"...\" cümlesinin öğe dizilişi aşağıdakilerden hangisidir?",
                "Aşağıdaki cümlelerin hangisinde özne yoktur?"
            ),
            onemliKurallar = listOf(
                "Devrik cümlelere dikkat et",
                "Gizli özne durumlarını belirt"
            )
        ),
        TurkceKonu(
            id = "anlatim_bozukluklari",
            baslik = "Anlatım Bozuklukları",
            agirlik = 9,
            altKonular = listOf(
                "Gereksiz sözcük kullanımı",
                "Sözcük eksikliği",
                "Anlam belirsizliği",
                "Özne-yüklem uyumsuzluğu",
                "Çelişki"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde anlatım bozukluğu vardır?",
                "Aşağıdaki cümlelerin hangisinde gereksiz sözcük kullanılmıştır?",
                "(I) ... (II) ... (III) ... Yukarıdaki numaralanmış cümlelerin hangilerinde anlatım bozukluğu vardır?"
            ),
            onemliKurallar = listOf(
                "Yaygın bozuklukları kullan: 'nedeninden dolayı', 'şu anda hâlâ' gibi",
                "Şıklarda sadece bir tanesinde bozukluk olmalı"
            )
        ),
        
        // SÖZEL MANTIK (%20-25)
        TurkceKonu(
            id = "deyim_atasozu",
            baslik = "Deyim ve Atasözleri",
            agirlik = 7,
            altKonular = listOf(
                "Deyim anlamları",
                "Atasözü anlamları",
                "Cümleye uygun deyim/atasözü"
            ),
            soruTipleri = listOf(
                "\"...\" deyiminin anlamı aşağıdakilerden hangisidir?",
                "Aşağıdaki cümlelerden hangisinde \"göz\" sözcüğü deyim içinde kullanılmıştır?",
                "Aşağıdaki atasözlerinden hangisi dayanışmayı ifade eder?"
            ),
            onemliKurallar = listOf(
                "Yaygın deyimleri kullan",
                "Mecazi anlam önemli"
            )
        ),
        TurkceKonu(
            id = "cumlede_anlam",
            baslik = "Cümlede Anlam",
            agirlik = 8,
            altKonular = listOf(
                "Öznel/nesnel yargı",
                "Karşılaştırma",
                "Neden-sonuç",
                "Amaç-sonuç",
                "Koşul"
            ),
            soruTipleri = listOf(
                "Aşağıdaki cümlelerin hangisinde koşul anlamı vardır?",
                "Aşağıdakilerden hangisi nesnel bir yargı içermektedir?",
                "Aşağıdaki cümlelerin hangisinde karşılaştırma yapılmıştır?"
            ),
            onemliKurallar = listOf(
                "Cümleler birbirine benzemeli ama anlam farkı net olmalı"
            )
        )
    )
    
    // ==================== MATEMATİK ====================
    
    data class MatematikKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int,
        val altKonular: List<String>,
        val problemTipleri: List<String>,
        val formuller: List<String>,
        val celdiriciStratejileri: List<String>
    )
    
    val matematikKonulari = listOf(
        MatematikKonu(
            id = "sayi_problemleri",
            baslik = "Sayı Problemleri",
            agirlik = 10,
            altKonular = listOf(
                "Ardışık sayılar",
                "Rakamlarla ilgili problemler",
                "EBOB-EKOK",
                "Bölünebilme kuralları",
                "Asal sayılar"
            ),
            problemTipleri = listOf(
                "Üç ardışık çift sayının toplamı 48 ise en büyük sayı kaçtır?",
                "İki basamaklı bir sayının rakamları toplamı 12'dir...",
                "A ile B'nin EBOB'u 6, EKOK'u 72 ise A+B toplamı kaçtır?"
            ),
            formuller = listOf(
                "EBOB × EKOK = A × B",
                "n tane ardışık sayı toplamı = n × orta sayı",
                "Çift sayı: 2n, Tek sayı: 2n+1"
            ),
            celdiriciStratejileri = listOf(
                "EBOB-EKOK karışıklığı",
                "En büyük yerine en küçük",
                "Toplam yerine çarpım"
            )
        ),
        MatematikKonu(
            id = "oran_oranti",
            baslik = "Oran ve Orantı",
            agirlik = 9,
            altKonular = listOf(
                "Basit oran",
                "Doğru orantı",
                "Ters orantı",
                "Bileşik oran"
            ),
            problemTipleri = listOf(
                "A'nın B'ye oranı 3/5 ise B'nin A'ya oranı kaçtır?",
                "Bir işi 6 işçi 12 günde bitiriyor. Aynı işi 9 işçi kaç günde bitirir?",
                "200 TL, 2:3:5 oranında paylaşılırsa en büyük pay kaç TL'dir?"
            ),
            formuller = listOf(
                "Doğru orantı: a₁/b₁ = a₂/b₂",
                "Ters orantı: a₁ × b₁ = a₂ × b₂",
                "Oran payları toplamı = Toplamın böleni"
            ),
            celdiriciStratejileri = listOf(
                "Doğru-ters orantı karışıklığı",
                "Oran çevirme hatası",
                "Pay-payda karışıklığı"
            )
        ),
        MatematikKonu(
            id = "yuzde_faiz",
            baslik = "Yüzde ve Faiz",
            agirlik = 9,
            altKonular = listOf(
                "Yüzde hesaplama",
                "Artış-azalış oranları",
                "Basit faiz",
                "Bileşik faiz",
                "Kar-zarar"
            ),
            problemTipleri = listOf(
                "Bir ürüne önce %20 zam, sonra %25 indirim yapılırsa son fiyat ilk fiyatın yüzde kaçıdır?",
                "1000 TL, yıllık %15 basit faizle 3 yıl bankada kalırsa toplam kaç TL olur?",
                "Bir malı %20 kârla satan tüccar 60 TL kazanıyor. Malın alış fiyatı kaç TL'dir?"
            ),
            formuller = listOf(
                "Basit Faiz: F = A × n × t / 100",
                "Peş peşe değişim: (1±a)(1±b)",
                "Kar = Satış - Alış, Kar% = (Kar/Alış) × 100"
            ),
            celdiriciStratejileri = listOf(
                "Artış üzerine artış hesaplama hatası",
                "Faiz formülünde ay-yıl karışıklığı",
                "Kar-zarar işaret hatası"
            )
        ),
        MatematikKonu(
            id = "denklem",
            baslik = "Denklem ve Eşitsizlik",
            agirlik = 8,
            altKonular = listOf(
                "1. dereceden denklemler",
                "Denklem sistemleri",
                "Eşitsizlikler",
                "Mutlak değer"
            ),
            problemTipleri = listOf(
                "3x - 5 = 2x + 7 denkleminin çözüm kümesi nedir?",
                "x + y = 10 ve 2x - y = 5 ise x kaçtır?",
                "|2x - 3| < 5 eşitsizliğinin çözüm kümesi nedir?"
            ),
            formuller = listOf(
                "ax + b = 0 → x = -b/a",
                "|x| < a → -a < x < a",
                "|x| > a → x < -a veya x > a"
            ),
            celdiriciStratejileri = listOf(
                "İşaret hatası",
                "Mutlak değer açılımında yanlış durum",
                "Eşitsizlikte ters çevirmeyi unutma"
            )
        ),
        MatematikKonu(
            id = "problem_tipleri",
            baslik = "Problem Türleri",
            agirlik = 10,
            altKonular = listOf(
                "Yaş problemleri",
                "İşçi-havuz problemleri",
                "Hareket (yol-hız-zaman)",
                "Karışım problemleri",
                "Bölme-kalan problemleri"
            ),
            problemTipleri = listOf(
                "Ahmet 4 yıl önce Mehmet'in şimdiki yaşındaydı. 3 yıl sonra yaşları toplamı 50 ise...",
                "Bir işi A 6 günde, B 4 günde bitiriyor. Birlikte kaç günde bitirirler?",
                "İki şehir arası 300 km'dir. Saatte 60 km hızla giden araç kaç saatte varır?"
            ),
            formuller = listOf(
                "Yol = Hız × Zaman",
                "Birlikte iş: 1/A + 1/B = 1/T",
                "Karışım: m₁c₁ + m₂c₂ = (m₁+m₂)c"
            ),
            celdiriciStratejileri = listOf(
                "Yaş farkı sabit kalır - unutma",
                "Birim dönüşüm hatası (dk-saat)",
                "Birlikte iş formülünü ters kullanma"
            )
        )
    )
    
    // ==================== TARİH ====================
    
    data class TarihKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int,
        val altKonular: List<String>,
        val onemliTarihler: Map<String, String>, // Tarih -> Olay
        val onemliIsimler: List<String>,
        val soruTipleri: List<String>
    )
    
    val tarihKonulari = listOf(
        // EN YOĞUN: ATATÜRK İLKE VE İNKILAPLARI
        TarihKonu(
            id = "ataturk_ilkeleri",
            baslik = "Atatürk İlkeleri",
            agirlik = 10,
            altKonular = listOf(
                "Cumhuriyetçilik", "Milliyetçilik", "Halkçılık",
                "Devletçilik", "Laiklik", "İnkılapçılık"
            ),
            onemliTarihler = mapOf(
                "1937" to "Atatürk ilkeleri anayasaya girdi"
            ),
            onemliIsimler = listOf("Mustafa Kemal Atatürk"),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi Atatürk'ün laiklik ilkesiyle ilişkilidir?",
                "Cumhuriyetçilik ilkesi aşağıdakilerden hangisini ifade eder?",
                "Aşağıdaki inkılaplardan hangisi doğrudan halkçılık ilkesiyle ilgilidir?"
            )
        ),
        TarihKonu(
            id = "inkilaplar",
            baslik = "İnkılap Hareketleri",
            agirlik = 10,
            altKonular = listOf(
                "Siyasi inkılaplar",
                "Sosyal inkılaplar",
                "Hukuki inkılaplar",
                "Eğitim inkılapları",
                "Ekonomik inkılaplar"
            ),
            onemliTarihler = mapOf(
                "1 Kasım 1922" to "Saltanatın kaldırılması",
                "29 Ekim 1923" to "Cumhuriyet'in ilanı",
                "3 Mart 1924" to "Hilafetin kaldırılması, Tevhid-i Tedrisat",
                "25 Kasım 1925" to "Şapka Kanunu",
                "30 Kasım 1925" to "Tekke ve zaviyelerin kapatılması",
                "17 Şubat 1926" to "Medeni Kanun'un kabulü",
                "1 Kasım 1928" to "Yeni Türk harflerinin kabulü",
                "21 Haziran 1934" to "Soyadı Kanunu",
                "5 Aralık 1934" to "Kadınlara seçme ve seçilme hakkı"
            ),
            onemliIsimler = listOf("Mustafa Kemal Atatürk", "İsmet İnönü"),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi eğitim alanında yapılan inkılaplardandır?",
                "Türk kadınına seçme ve seçilme hakkı hangi yıl verilmiştir?",
                "Aşağıdaki inkılaplardan hangisi laiklik ilkesiyle doğrudan ilişkilidir?"
            )
        ),
        TarihKonu(
            id = "milli_mucadele",
            baslik = "Milli Mücadele Dönemi",
            agirlik = 9,
            altKonular = listOf(
                "Mondros Ateşkes Antlaşması",
                "Kuvayi Milliye",
                "Kongreler",
                "TBMM'nin açılışı",
                "Savaşlar ve Antlaşmalar"
            ),
            onemliTarihler = mapOf(
                "30 Ekim 1918" to "Mondros Ateşkes Antlaşması",
                "19 Mayıs 1919" to "Mustafa Kemal'in Samsun'a çıkışı",
                "23 Temmuz - 7 Ağustos 1919" to "Erzurum Kongresi",
                "4-11 Eylül 1919" to "Sivas Kongresi",
                "23 Nisan 1920" to "TBMM'nin açılışı",
                "10 Ocak 1921" to "I. İnönü Muharebesi",
                "31 Mart 1921" to "II. İnönü Muharebesi",
                "23 Ağustos - 13 Eylül 1921" to "Sakarya Meydan Muharebesi",
                "26 Ağustos 1922" to "Büyük Taarruz",
                "11 Ekim 1922" to "Mudanya Ateşkes Antlaşması",
                "24 Temmuz 1923" to "Lozan Barış Antlaşması"
            ),
            onemliIsimler = listOf(
                "Mustafa Kemal Paşa", "İsmet Paşa", "Fevzi Paşa",
                "Kazım Karabekir", "Ali Fuat Cebesoy", "Refet Bele"
            ),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi Erzurum Kongresi'nin kararlarından biridir?",
                "Sakarya Meydan Muharebesi'nin kazanılmasının sonuçlarından biri aşağıdakilerden hangisidir?",
                "TBMM'nin ilk başkanı kimdir?"
            )
        ),
        TarihKonu(
            id = "osmanli_gerileme",
            baslik = "Osmanlı Duraklama ve Gerileme",
            agirlik = 7,
            altKonular = listOf(
                "Duraklama dönemi ıslahatları",
                "Karlofça ve Pasarofça Antlaşmaları",
                "Tanzimat ve Islahat Fermanları",
                "I. ve II. Meşrutiyet"
            ),
            onemliTarihler = mapOf(
                "1699" to "Karlofça Antlaşması - Osmanlı ilk kez toprak kaybı",
                "1718" to "Pasarofça Antlaşması",
                "1839" to "Tanzimat Fermanı",
                "1856" to "Islahat Fermanı",
                "1876" to "I. Meşrutiyet - Kanun-i Esasi",
                "1908" to "II. Meşrutiyet"
            ),
            onemliIsimler = listOf(
                "Mustafa Reşit Paşa", "Sultan Abdülmecit", "II. Mahmut",
                "Sultan Abdülhamit II", "İttihat ve Terakki"
            ),
            soruTipleri = listOf(
                "Osmanlı Devleti'nin toprak kaybettiği ilk antlaşma hangisidir?",
                "Tanzimat Fermanı'nın özelliklerinden biri aşağıdakilerden hangisidir?",
                "II. Meşrutiyet hangi padişah döneminde ilan edilmiştir?"
            )
        ),
        TarihKonu(
            id = "ilk_turk_devletleri",
            baslik = "İslamiyet Öncesi Türk Devletleri",
            agirlik = 6,
            altKonular = listOf(
                "Hunlar (Asya Hun, Avrupa Hun)",
                "Göktürkler",
                "Uygurlar",
                "Türk devlet geleneği"
            ),
            onemliTarihler = mapOf(
                "M.Ö. 220 - M.S. 216" to "Asya Hun İmparatorluğu",
                "552-744" to "Göktürk Devleti",
                "745-840" to "Uygur Devleti",
                "375" to "Kavimler Göçü"
            ),
            onemliIsimler = listOf(
                "Mete Han", "Attila", "Bilge Kağan", "Kül Tigin",
                "Tonyukuk", "Bögü Kağan"
            ),
            soruTipleri = listOf(
                "Türk tarihinde ilk alfabeyi kullanan devlet hangisidir?",
                "Kavimler Göçü'nü başlatan Türk devleti aşağıdakilerden hangisidir?",
                "Yerleşik hayata geçen ilk Türk devleti hangisidir?"
            )
        )
    )
    
    // ==================== COĞRAFYA ====================
    
    data class CografyaKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int,
        val altKonular: List<String>,
        val turkiyeVerileri: Map<String, String>, // Gerçek veriler
        val soruTipleri: List<String>
    )
    
    val cografyaKonulari = listOf(
        CografyaKonu(
            id = "turkiye_fiziki",
            baslik = "Türkiye'nin Fiziki Coğrafyası",
            agirlik = 10,
            altKonular = listOf(
                "Türkiye'nin konumu",
                "Yer şekilleri",
                "Dağlar ve ovalar",
                "Akarsular ve göller"
            ),
            turkiyeVerileri = mapOf(
                "En yüksek dağ" to "Ağrı Dağı (5137 m)",
                "En uzun akarsu" to "Kızılırmak (1355 km)",
                "En büyük göl" to "Van Gölü (3713 km²)",
                "En büyük ova" to "Konya Ovası",
                "En derin göl" to "Van Gölü (451 m)",
                "En büyük ada" to "Gökçeada",
                "En uzun kıyı" to "Ege kıyıları (girinti-çıkıntılı)"
            ),
            soruTipleri = listOf(
                "Türkiye'nin en uzun akarsuyu aşağıdakilerden hangisidir?",
                "Aşağıdaki ovalardan hangisi Türkiye'nin en büyük ovasıdır?",
                "Karadeniz'e dökülen akarsular aşağıdakilerden hangisinde doğru verilmiştir?"
            )
        ),
        CografyaKonu(
            id = "turkiye_iklim",
            baslik = "Türkiye İklimi",
            agirlik = 9,
            altKonular = listOf(
                "İklim tipleri",
                "Sıcaklık ve yağış dağılışı",
                "Bitki örtüsü"
            ),
            turkiyeVerileri = mapOf(
                "Akdeniz iklimi" to "Yaz kuraklığı, kışın yağış, maki",
                "Karadeniz iklimi" to "Her mevsim yağışlı, orman",
                "Karasal iklim" to "İç bölgeler, sert kış, bozkır",
                "En çok yağış" to "Rize (2500 mm/yıl)",
                "En az yağış" to "Konya, Tuz Gölü çevresi"
            ),
            soruTipleri = listOf(
                "Türkiye'de en fazla yağış alan bölge aşağıdakilerden hangisidir?",
                "Maki bitki örtüsü aşağıdaki iklim tiplerinden hangisinde görülür?",
                "Aşağıdakilerden hangisi Karadeniz ikliminin özelliklerinden biri değildir?"
            )
        ),
        CografyaKonu(
            id = "turkiye_nufus",
            baslik = "Türkiye'de Nüfus ve Yerleşme",
            agirlik = 8,
            altKonular = listOf(
                "Nüfus artışı",
                "Nüfus yoğunluğu",
                "Göç hareketleri",
                "Kır-kent nüfusu"
            ),
            turkiyeVerileri = mapOf(
                "En kalabalık il" to "İstanbul",
                "En az nüfuslu il" to "Bayburt/Tunceli",
                "Nüfus yoğunluğu en fazla" to "İstanbul",
                "Nüfus yoğunluğu en az" to "Tunceli"
            ),
            soruTipleri = listOf(
                "Türkiye'de nüfusun en yoğun olduğu bölge aşağıdakilerden hangisidir?",
                "Aşağıdakilerden hangisi iç göçün nedenlerinden biri değildir?",
                "Türkiye'de kırdan kente göçün temel nedenleri nelerdir?"
            )
        ),
        CografyaKonu(
            id = "turkiye_tarim",
            baslik = "Türkiye'de Tarım",
            agirlik = 9,
            altKonular = listOf(
                "Tarım ürünleri",
                "Hayvancılık",
                "Tarım bölgeleri"
            ),
            turkiyeVerileri = mapOf(
                "Fındık" to "Karadeniz (dünya 1.si, %70)",
                "Kayısı" to "Malatya (dünya 1.si)",
                "Zeytin" to "Ege ve Akdeniz",
                "Pamuk" to "Çukurova, Ege",
                "Çay" to "Doğu Karadeniz (Rize)",
                "Buğday" to "İç Anadolu (Konya, Ankara)",
                "Antep fıstığı" to "Gaziantep (dünya 3.sü)"
            ),
            soruTipleri = listOf(
                "Türkiye'de fındık üretiminin en yoğun olduğu bölge aşağıdakilerden hangisidir?",
                "Aşağıdakilerden hangisi Akdeniz Bölgesi'nin tarım ürünlerinden biri değildir?",
                "Türkiye dünya fındık üretiminde kaçıncı sıradadır?"
            )
        ),
        CografyaKonu(
            id = "turkiye_sanayi_maden",
            baslik = "Türkiye'de Sanayi ve Madenler",
            agirlik = 7,
            altKonular = listOf(
                "Sanayi bölgeleri",
                "Maden yatakları",
                "Enerji kaynakları"
            ),
            turkiyeVerileri = mapOf(
                "Bor madeni" to "Eskişehir, Balıkesir (dünya 1.si)",
                "Krom" to "Fethiye, Elazığ (dünya 3.sü)",
                "Demir" to "Divriği, Hekimhan",
                "Linyit" to "Afyon, Manisa, Muğla",
                "Taşkömürü" to "Zonguldak havzası"
            ),
            soruTipleri = listOf(
                "Türkiye dünyada hangi maden üretiminde birinci sıradadır?",
                "Türkiye'de taşkömürü çıkarılan tek havza aşağıdakilerden hangisidir?",
                "Aşağıdakilerden hangisi Türkiye'nin önemli demir madeni yataklarından biridir?"
            )
        )
    )
    
    // ==================== VATANDAŞLIK ====================
    
    data class VatandaslikKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int,
        val altKonular: List<String>,
        val anayasaBilgileri: Map<String, String>, // 2017 değişiklikleri dahil
        val soruTipleri: List<String>
    )
    
    val vatandaslikKonulari = listOf(
        VatandaslikKonu(
            id = "devlet_yapisi",
            baslik = "Devletin Temel Yapısı",
            agirlik = 10,
            altKonular = listOf(
                "Cumhurbaşkanlığı Hükümet Sistemi (2017)",
                "Yasama (TBMM)",
                "Yürütme",
                "Yargı"
            ),
            anayasaBilgileri = mapOf(
                "Cumhurbaşkanı görev süresi" to "5 yıl, en fazla 2 dönem",
                "Milletvekili sayısı" to "600",
                "TBMM görev süresi" to "5 yıl",
                "Anayasa Mahkemesi üye sayısı" to "15 üye",
                "Cumhurbaşkanı yardımcısı" to "2017 ile geldi, sayı sınırsız",
                "Bakan atama" to "Cumhurbaşkanı atar (2017 sonrası)"
            ),
            soruTipleri = listOf(
                "2017 Anayasa değişikliğiyle kaldırılan kurum aşağıdakilerden hangisidir? (Başbakanlık)",
                "Cumhurbaşkanı en fazla kaç dönem görev yapabilir?",
                "TBMM milletvekili sayısı kaçtır?"
            )
        ),
        VatandaslikKonu(
            id = "temel_haklar",
            baslik = "Temel Haklar ve Özgürlükler",
            agirlik = 9,
            altKonular = listOf(
                "Kişi hakları",
                "Sosyal ve ekonomik haklar",
                "Siyasi haklar",
                "Hakların sınırlandırılması"
            ),
            anayasaBilgileri = mapOf(
                "Seçme yaşı" to "18",
                "Seçilme yaşı (milletvekili)" to "18 (2017 değişikliği)",
                "Olağanüstü hal süresi" to "En fazla 4 ay, TBMM 4 ay uzatabilir",
                "Temel haklar" to "Dokunulamaz, devredilemez, vazgeçilemez"
            ),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi sosyal ve ekonomik haklardandır?",
                "Temel hak ve özgürlükler hangi koşullarda sınırlandırılabilir?",
                "Milletvekili seçilme yaşı kaçtır?"
            )
        ),
        VatandaslikKonu(
            id = "idare_hukuku",
            baslik = "İdare Hukuku",
            agirlik = 7,
            altKonular = listOf(
                "Merkezi yönetim",
                "Yerel yönetimler",
                "Kamu görevlileri",
                "İdari yargı"
            ),
            anayasaBilgileri = mapOf(
                "İl yönetimi" to "Vali (merkezi yönetimin temsilcisi)",
                "Belediye başkanı seçimi" to "5 yıl, doğrudan halk seçimi",
                "İl genel meclisi" to "5 yıl",
                "Köy muhtarı" to "5 yıl"
            ),
            soruTipleri = listOf(
                "Aşağıdakilerden hangisi merkezi yönetimin taşra teşkilatıdır?",
                "Belediye başkanı kaç yıllığına seçilir?",
                "Aşağıdakilerden hangisi yerel yönetim birimlerinden biri değildir?"
            )
        )
    )
    
    // ==================== GÜNCEL BİLGİLER ====================
    
    data class GuncelKonu(
        val id: String,
        val baslik: String,
        val agirlik: Int,
        val altKonular: List<String>,
        val guncelVeriler: List<String>, // Somut, tarihli bilgiler
        val soruTipleri: List<String>
    )
    
    val guncelKonulari = listOf(
        GuncelKonu(
            id = "turkiye_projeleri",
            baslik = "Türkiye'nin Büyük Projeleri",
            agirlik = 10,
            altKonular = listOf(
                "Ulaştırma projeleri",
                "Savunma sanayi",
                "Enerji projeleri"
            ),
            guncelVeriler = listOf(
                "TOGG - Türkiye'nin ilk yerli otomobili, 2022'de seri üretime geçti",
                "KAAN - Milli Muharip Uçak, 2024'te ilk uçuşunu yaptı",
                "Akıncı ve Bayraktar TB2 - İnsansız hava araçları",
                "İstanbul Havalimanı - Dünyanın en büyük havalimanlarından",
                "1915 Çanakkale Köprüsü - 2022'de açıldı, dünyanın en uzun orta açıklıklı köprüsü",
                "Marmaray - Dünyanın en derin denizaltı tüp geçidi",
                "Akkuyu Nükleer Santrali - Türkiye'nin ilk nükleer santrali"
            ),
            soruTipleri = listOf(
                "Türkiye'nin ilk yerli otomobil markası aşağıdakilerden hangisidir?",
                "2022'de açılan ve dünyanın en uzun orta açıklıklı köprüsü olan proje hangisidir?",
                "Aşağıdakilerden hangisi Türkiye'nin savunma sanayi projelerinden biridir?"
            )
        ),
        GuncelKonu(
            id = "uluslararasi",
            baslik = "Uluslararası Gelişmeler",
            agirlik = 6,
            altKonular = listOf(
                "Türkiye'nin uluslararası ilişkileri",
                "Önemli ziyaretler ve anlaşmalar",
                "Önemli kuruluşlar (NATO, BM, AB)"
            ),
            guncelVeriler = listOf(
                "Türkiye 1952'den beri NATO üyesi",
                "BM Genel Sekreteri: António Guterres",
                "G20 ülkesi Türkiye",
                "Türkiye - AB ilişkileri, tam üyelik müzakereleri"
            ),
            soruTipleri = listOf(
                "Türkiye hangi yılda NATO'ya üye olmuştur?",
                "Aşağıdakilerden hangisi G20 ülkelerinden biri değildir?",
                "BM'nin merkezi hangi şehirdedir?"
            )
        )
    )
    
    // ==================== YARDIMCI FONKSİYONLAR ====================
    
    /**
     * Belirtilen ders için RAG verisi döndürür
     */
    fun getKonuDetay(ders: String, konuId: String): Map<String, Any>? {
        return when (ders.lowercase()) {
            "türkçe", "turkce" -> turkceKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "soruTipleri" to konu.soruTipleri,
                    "onemliKurallar" to konu.onemliKurallar,
                    "agirlik" to konu.agirlik
                )
            }
            "matematik" -> matematikKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "problemTipleri" to konu.problemTipleri,
                    "formuller" to konu.formuller,
                    "celdiriciStratejileri" to konu.celdiriciStratejileri,
                    "agirlik" to konu.agirlik
                )
            }
            "tarih" -> tarihKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "onemliTarihler" to konu.onemliTarihler,
                    "onemliIsimler" to konu.onemliIsimler,
                    "soruTipleri" to konu.soruTipleri,
                    "agirlik" to konu.agirlik
                )
            }
            "coğrafya", "cografya" -> cografyaKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "turkiyeVerileri" to konu.turkiyeVerileri,
                    "soruTipleri" to konu.soruTipleri,
                    "agirlik" to konu.agirlik
                )
            }
            "vatandaşlık", "vatandaslik" -> vatandaslikKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "anayasaBilgileri" to konu.anayasaBilgileri,
                    "soruTipleri" to konu.soruTipleri,
                    "agirlik" to konu.agirlik
                )
            }
            "güncel", "guncel" -> guncelKonulari.find { it.id == konuId }?.let { konu ->
                mapOf(
                    "baslik" to konu.baslik,
                    "altKonular" to konu.altKonular,
                    "guncelVeriler" to konu.guncelVeriler,
                    "soruTipleri" to konu.soruTipleri,
                    "agirlik" to konu.agirlik
                )
            }
            else -> null
        }
    }
    
    /**
     * Derse göre tüm konuları ve ağırlıklarını döndürür
     */
    fun getKonuListesi(ders: String): List<Pair<String, Int>> {
        return when (ders.lowercase()) {
            "türkçe", "turkce" -> turkceKonulari.map { it.id to it.agirlik }
            "matematik" -> matematikKonulari.map { it.id to it.agirlik }
            "tarih" -> tarihKonulari.map { it.id to it.agirlik }
            "coğrafya", "cografya" -> cografyaKonulari.map { it.id to it.agirlik }
            "vatandaşlık", "vatandaslik" -> vatandaslikKonulari.map { it.id to it.agirlik }
            "güncel", "guncel" -> guncelKonulari.map { it.id to it.agirlik }
            else -> emptyList()
        }
    }
    
    /**
     * Anti-halüsinasyon kuralları döndürür
     */
    fun getAntiHalucinasyonKurallari(ders: String): String {
        return when (ders.lowercase()) {
            "tarih" -> """
⚠️ TARİH İÇİN KESİN KURALLAR:
1. Tarih UYDURMAK YASAK - sadece yukarıdaki listeyi kullan
2. İsim UYDURMAK YASAK - sadece yukarıdaki isimleri kullan
3. Antlaşma/Savaş adı UYDURMAK YASAK
4. Şüpheliysen genel ifade kullan: "Bu dönemde...", "Osmanlı'nın son döneminde..."
5. Çeldirici olarak YANLIŞ tarih kullanabilirsin ama DOĞRU CEVAP GERÇEK olmalı
""".trimIndent()
            
            "coğrafya", "cografya" -> """
⚠️ COĞRAFYA İÇİN KESİN KURALLAR:
1. Türkiye'nin EN'lerini DOĞRU kullan (yukarıdaki listeye bak)
2. Şehir-ürün eşleştirmelerini DOĞRU yap
3. Bölge sınırlarını DOĞRU belirt
4. Rakam veriyorsan güncel ve DOĞRU olsun
5. Harita/Grafik gerektiren soru YAPMA
""".trimIndent()
            
            "vatandaşlık", "vatandaslik" -> """
⚠️ VATANDAŞLIK İÇİN KESİN KURALLAR:
1. 2017 Anayasa değişikliklerini UNUTMA (Cumhurbaşkanlığı sistemi)
2. Anayasa madde numarası veriyorsan DOĞRU olsun
3. Yürürlükte olmayan kuralları SORMA
4. Eski sistem bilgilerini (Başbakanlık vb.) yanlış şık olarak kullanabilirsin
""".trimIndent()
            
            "güncel", "guncel" -> """
⚠️ GÜNCEL BİLGİLER İÇİN KESİN KURALLAR:
1. SOMUT, TARİHLİ bilgiler kullan (örn: "2024 yılında...")
2. Genel kalıp ifadeler YASAK
3. Kurumlar ve projeler GERÇEK olmalı
4. Sadece yukarıdaki listeden soru sor
""".trimIndent()
            
            else -> """
⚠️ GENEL KURALLAR:
1. Bilmediğin bilgiyi UYDURMA
2. Şüpheliysen genel ifade kullan
3. Doğru cevap kesinlikle DOĞRU olmalı
""".trimIndent()
        }
    }
}
