package com.example.bilgideham

/**
 * 5. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 * İlkokul, Ortaokul ve İmam Hatip Ortaokulu için geçerli
 */
object Sinif5Kazanimlari {

    // ==================== TÜRKÇE ====================
    
    val turkce = listOf(
        // 1. SÖZCÜKTE ANLAM
        RagKazanim(
            kod = "T.5.1.1",
            ders = "Türkçe",
            unite = "Sözcükte Anlam",
            konu = "Gerçek, Mecaz ve Terim Anlam",
            aciklama = "Sözcüklerin gerçek, mecaz ve terim anlamını ayırt eder.",
            ornekler = listOf("Bahar geldi (gerçek)", "Yüreği bahar gibi (mecaz)", "Açı (matematik terimi)"),
            keywords = listOf("gerçek anlam", "mecaz anlam", "terim anlam", "anlam türleri")
        ),
        RagKazanim(
            kod = "T.5.1.2",
            ders = "Türkçe",
            unite = "Sözcükte Anlam",
            konu = "Eş ve Zıt Anlamlı Sözcükler",
            aciklama = "Eş anlamlı ve zıt anlamlı sözcükleri bulur ve kullanır.",
            ornekler = listOf("güzel-hoş (eş)", "büyük-küçük (zıt)", "hızlı-yavaş (zıt)"),
            keywords = listOf("eş anlam", "zıt anlam", "anlamdaş", "karşıt anlam")
        ),
        // 2. SÖZ GRUPLARI
        RagKazanim(
            kod = "T.5.2.1",
            ders = "Türkçe",
            unite = "Söz Gruplarında Anlam",
            konu = "Deyimler ve Atasözleri",
            aciklama = "Deyim, atasözü ve özdeyişlerin anlamını bulur.",
            ornekler = listOf("Damlaya damlaya göl olur", "Ağzı açık kalmak", "El ele vererek"),
            keywords = listOf("deyim", "atasözü", "özdeyiş", "kalıp söz")
        ),
        // 3. CÜMLEDE ANLAM
        RagKazanim(
            kod = "T.5.3.1",
            ders = "Türkçe",
            unite = "Cümlede Anlam",
            konu = "Neden-Sonuç ve Amaç-Sonuç",
            aciklama = "Neden-sonuç ve amaç-sonuç ilişkisi kuran cümleleri belirler.",
            ornekler = listOf("Çok çalıştığı için başardı (neden-sonuç)", "Kazanmak için çalışıyor (amaç-sonuç)"),
            keywords = listOf("neden", "sonuç", "amaç", "çünkü", "için", "bu yüzden")
        ),
        RagKazanim(
            kod = "T.5.3.2",
            ders = "Türkçe",
            unite = "Cümlede Anlam",
            konu = "Koşul-Sonuç Cümleleri",
            aciklama = "Koşul-sonuç (şart) ilişkisi kuran cümleleri anlar.",
            ornekler = listOf("Erken kalkarsan yetişirsin", "Çalışırsa başarır"),
            keywords = listOf("koşul", "şart", "-sa/-se", "eğer", "ise")
        ),
        RagKazanim(
            kod = "T.5.3.3",
            ders = "Türkçe",
            unite = "Cümlede Anlam",
            konu = "Öznel ve Nesnel Yargı",
            aciklama = "Öznel ve nesnel yargılı cümleleri ayırt eder.",
            ornekler = listOf("Bugün hava güzel (öznel)", "Bugün hava 25 derece (nesnel)"),
            keywords = listOf("öznel", "nesnel", "kişisel görüş", "kanıtlanabilir")
        ),
        // 4. PARAGRAF
        RagKazanim(
            kod = "T.5.4.1",
            ders = "Türkçe",
            unite = "Paragrafta Anlam",
            konu = "Ana Düşünce ve Yardımcı Düşünce",
            aciklama = "Paragrafın ana düşüncesini ve yardımcı düşüncelerini bulur.",
            ornekler = listOf("Metnin vermek istediği asıl mesaj..."),
            keywords = listOf("ana düşünce", "ana fikir", "yardımcı düşünce", "konu")
        ),
        RagKazanim(
            kod = "T.5.4.2",
            ders = "Türkçe",
            unite = "Paragrafta Anlam",
            konu = "Anlatım Biçimleri",
            aciklama = "Betimleme, öyküleme, açıklama, tartışma anlatım biçimlerini tanır.",
            ornekler = listOf("Betimleme: görsel tasvirler", "Öyküleme: olay anlatımı"),
            keywords = listOf("betimleme", "öyküleme", "açıklama", "tartışma", "anlatım biçimi")
        ),
        // 5. YAZIM VE NOKTALAMA
        RagKazanim(
            kod = "T.5.5.1",
            ders = "Türkçe",
            unite = "Yazım Kuralları",
            konu = "Büyük Harf ve Yazım",
            aciklama = "Büyük harflerin kullanım kurallarını bilir ve uygular.",
            ornekler = listOf("Atatürk", "Türkiye", "Pazartesi"),
            keywords = listOf("büyük harf", "özel isim", "yazım kuralı")
        ),
        RagKazanim(
            kod = "T.5.5.2",
            ders = "Türkçe",
            unite = "Noktalama İşaretleri",
            konu = "Temel Noktalama İşaretleri",
            aciklama = "Nokta, virgül, soru işareti, ünlem işaretini doğru kullanır.",
            ornekler = listOf("Bugün hava güzel.", "Ne güzel bir gün!"),
            keywords = listOf("nokta", "virgül", "soru işareti", "ünlem", "noktalama")
        ),
        // 6. METİN TÜRLERİ
        RagKazanim(
            kod = "T.5.6.1",
            ders = "Türkçe",
            unite = "Metin Türleri",
            konu = "Hikaye ve Masal",
            aciklama = "Hikaye ve masalın özelliklerini tanır, karşılaştırır.",
            ornekler = listOf("Hikaye: gerçekçi", "Masal: olağanüstü öğeler"),
            keywords = listOf("hikaye", "masal", "fabl", "metin türü", "anlatıcı")
        )
    )

    // ==================== MATEMATİK (MEB 2025-2026 TYMM) ====================
    
    val matematik = listOf(
        // TEMA 1: SAYILAR VE NİCELİKLER (1)
        RagKazanim(
            kod = "MAT.5.1.1",
            ders = "Matematik",
            unite = "Sayılar ve Nicelikler",
            konu = "Çok Basamaklı Sayılar",
            aciklama = "Altı basamaklı sayıları okur, yazar ve çok basamaklı sayılara geneller. Bölükleri ve okunuşları arasındaki örüntüleri belirler.",
            ornekler = listOf("1.234.567", "Birler-Binler-Milyonlar bölüğü", "Sayı değeri ve basamak değeri"),
            keywords = listOf("basamak", "bölük", "sayı değeri", "basamak değeri", "çok basamaklı", "okuma", "yazma")
        ),
        RagKazanim(
            kod = "MAT.5.1.2",
            ders = "Matematik",
            unite = "Sayılar ve Nicelikler",
            konu = "Doğal Sayı Problemleri",
            aciklama = "Doğal sayılarla dört işlem içeren gerçek yaşam problemlerini çözer. Problem çözme stratejileri geliştirir ve uygular.",
            ornekler = listOf("Ali marketten 3 kalem aldı...", "Tahmin yapma ve sonuç kontrol"),
            keywords = listOf("problem", "dört işlem", "strateji", "toplama", "çıkarma", "çarpma", "bölme", "gerçek yaşam")
        ),
        // TEMA 2: SAYILAR VE NİCELİKLER (2) - KESİRLER
        RagKazanim(
            kod = "MAT.5.1.3",
            ders = "Matematik",
            unite = "Kesirler",
            konu = "Kesir Gösterimleri",
            aciklama = "Kesirlerin bileşik, tam sayılı, ondalık ve yüzde gösterimlerini ilişkilendirir. Yüzlük kart, somut modeller ve sayı doğrusu kullanır.",
            ornekler = listOf("3/4 = 0,75 = %75", "5/4 = 1 1/4", "Yüzlük kart modeli"),
            keywords = listOf("kesir", "pay", "payda", "ondalık", "yüzde", "bileşik kesir", "tam sayılı kesir", "model")
        ),
        RagKazanim(
            kod = "MAT.5.1.4",
            ders = "Matematik",
            unite = "Kesirler",
            konu = "Kesir Karşılaştırma",
            aciklama = "Farklı gösterimlerdeki kesirleri karşılaştırır ve sıralar. Sayı doğrusu ve şekil temsilleri kullanarak genelleme yapar.",
            ornekler = listOf("1/2 < 3/4", "0,5 = 1/2 = %50", "Sayı doğrusunda gösterim"),
            keywords = listOf("karşılaştırma", "sıralama", "sayı doğrusu", "genelleme", "tahmin")
        ),
        // TEMA 3: İŞLEMLERLE CEBİRSEL DÜŞÜNME
        RagKazanim(
            kod = "MAT.5.2.1",
            ders = "Matematik",
            unite = "Cebirsel Düşünme",
            konu = "Eşitlik ve İşlem Özellikleri",
            aciklama = "Eşitliğin korunumu, toplama/çarpmanın değişme ve birleşme, çarpmanın dağılma özelliğini anlar ve çıkarım yapar.",
            ornekler = listOf("3+5=5+3 (değişme)", "2×(3+4)=2×3+2×4 (dağılma)", "(2+3)+4=2+(3+4) (birleşme)"),
            keywords = listOf("eşitlik", "değişme özelliği", "birleşme özelliği", "dağılma özelliği", "korunum")
        ),
        RagKazanim(
            kod = "MAT.5.2.2",
            ders = "Matematik",
            unite = "Cebirsel Düşünme",
            konu = "İşlem Önceliği",
            aciklama = "Dört işlem içeren ifadelerde işlem önceliğini uygular ve açıklar. Günlük hayat durumlarında yorumlar.",
            ornekler = listOf("3+4×2=11", "Parantez önce", "(5-2)×3=9"),
            keywords = listOf("işlem önceliği", "parantez", "çarpma önce", "toplama sonra")
        ),
        RagKazanim(
            kod = "MAT.5.2.3",
            ders = "Matematik",
            unite = "Cebirsel Düşünme",
            konu = "Sayı ve Şekil Örüntüleri",
            aciklama = "Örüntülerdeki ilişkileri inceler, kuralını bulur ve geneller. Sözel ve sembolik temsil kullanır.",
            ornekler = listOf("2, 4, 6, 8, ... (kural: +2)", "1, 4, 9, 16, ... (kareler)", "Şekil örüntüsü"),
            keywords = listOf("örüntü", "kural", "genelleme", "terim", "sembolik temsil")
        ),
        RagKazanim(
            kod = "MAT.5.2.4",
            ders = "Matematik",
            unite = "Cebirsel Düşünme",
            konu = "Algoritmalar",
            aciklama = "Temel aritmetik işlem içeren algoritmik yapıları inceler ve tablo/işlemlere dönüştürür.",
            ornekler = listOf("Akış şeması", "Adım adım işlem", "Tablo dönüşümü"),
            keywords = listOf("algoritma", "akış", "adım", "tablo", "dönüşüm")
        ),
        // TEMA 4: GEOMETRİK ŞEKİLLER
        RagKazanim(
            kod = "MAT.5.3.1",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Temel Geometrik Çizimler",
            aciklama = "Nokta, doğru, doğru parçası, ışın, açı, çember ve dikme çizer. Pergel, gönye ve cetvel kullanır.",
            ornekler = listOf("Pergel ile çember", "Gönye ile dikme", "Cetvel ile doğru parçası"),
            keywords = listOf("çizim", "pergel", "gönye", "cetvel", "dikme", "çember", "doğru", "ışın")
        ),
        RagKazanim(
            kod = "MAT.5.3.3",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Açı Ölçme",
            aciklama = "Açıları iletki ile ölçer. Dar, dik ve geniş açı türlerini belirler.",
            ornekler = listOf("Dar açı < 90°", "Dik açı = 90°", "Geniş açı > 90°", "Doğru açı = 180°"),
            keywords = listOf("açı", "iletki", "derece", "dar açı", "dik açı", "geniş açı", "ölçme")
        ),
        RagKazanim(
            kod = "MAT.5.3.4",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Doğruların Kesişimi ve Açılar",
            aciklama = "Düzlemde iki veya üç doğrunun kesişimiyle oluşan açıları inceler ve çıkarım yapar.",
            ornekler = listOf("Karşı açılar", "Komşu açılar", "Kesişen doğrular"),
            keywords = listOf("kesişim", "karşı açı", "komşu açı", "doğru", "düzlem")
        ),
        RagKazanim(
            kod = "MAT.5.3.5",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Çokgenler",
            aciklama = "Çokgenleri ardışık kesişen doğruların oluşturduğu kapalı şekiller olarak yorumlar. Kenar ve açı özelliklerini inceler.",
            ornekler = listOf("Üçgen", "Dörtgen", "Beşgen", "Altıgen"),
            keywords = listOf("çokgen", "kenar", "köşe", "üçgen", "dörtgen", "kapalı şekil")
        ),
        RagKazanim(
            kod = "MAT.5.3.7",
            ders = "Matematik",
            unite = "Geometrik Şekiller",
            konu = "Çemberler ve Üçgenler",
            aciklama = "İki noktada kesişen çember çiftinin merkezleri ile üçgen inşa eder. Eşkenar, ikizkenar, çeşitkenar üçgenleri belirler.",
            ornekler = listOf("Eşkenar üçgen", "İkizkenar üçgen", "Çeşitkenar üçgen", "Pergel ile inşa"),
            keywords = listOf("çember", "üçgen", "eşkenar", "ikizkenar", "çeşitkenar", "inşa", "pergel")
        ),
        // TEMA 5: GEOMETRİK NİCELİKLER
        RagKazanim(
            kod = "MAT.5.4.1",
            ders = "Matematik",
            unite = "Geometrik Nicelikler",
            konu = "Dikdörtgen Çevre",
            aciklama = "Dikdörtgenin çevre uzunluğu verildiğinde olası kenar uzunluklarını bulur ve yorumlar.",
            ornekler = listOf("Çevre = 20 cm ise kenarlar: 6-4, 7-3, 8-2...", "Farklı dikdörtgenler aynı çevreye sahip olabilir"),
            keywords = listOf("çevre", "dikdörtgen", "kenar uzunluğu", "doğal sayı")
        ),
        RagKazanim(
            kod = "MAT.5.4.2",
            ders = "Matematik",
            unite = "Geometrik Nicelikler",
            konu = "Dikdörtgen Alan",
            aciklama = "Birim karelerden yola çıkarak dikdörtgenin alanını hesaplar. Alan = a × b bağıntısını çıkarır.",
            ornekler = listOf("Birim kare sayma", "Alan = uzun kenar × kısa kenar", "cm², m²"),
            keywords = listOf("alan", "birim kare", "dikdörtgen", "kare", "ölçme")
        ),
        RagKazanim(
            kod = "MAT.5.4.4",
            ders = "Matematik",
            unite = "Geometrik Nicelikler",
            konu = "Çevre-Alan Problemleri",
            aciklama = "Dikdörtgenin çevre ve alanı ile ilgili problemleri çözer. Aynı çevrede farklı alanlar olabileceğini keşfeder.",
            ornekler = listOf("Bahçe çevreleme", "Oda döşeme", "Çevre 24 cm, en büyük alan?"),
            keywords = listOf("problem", "çevre", "alan", "dikdörtgen", "strateji")
        ),
        // TEMA 6: İSTATİSTİKSEL ARAŞTIRMA SÜRECİ
        RagKazanim(
            kod = "MAT.5.5.1",
            ders = "Matematik",
            unite = "İstatistik",
            konu = "Kategorik Veri ve Araştırma",
            aciklama = "Kategorik veri toplar, anket hazırlar, sıklık tablosu ve grafiklerle gösterir. Araştırma süreci yürütür.",
            ornekler = listOf("Sınıfın en sevdiği renk anketi", "Sıklık tablosu", "Sütun grafiği", "Daire grafiği"),
            keywords = listOf("veri", "anket", "sıklık tablosu", "grafik", "sütun grafik", "daire grafik", "istatistik")
        ),
        RagKazanim(
            kod = "MAT.5.5.2",
            ders = "Matematik",
            unite = "İstatistik",
            konu = "İstatistiksel Sonuç Yorumlama",
            aciklama = "Başkaları tarafından oluşturulan grafikler ve istatistiklerdeki hataları/yanlılıkları tespit eder.",
            ornekler = listOf("Grafik yorumlama", "Yanlış çıkarım tespiti", "Veri doğrulama"),
            keywords = listOf("yorumlama", "grafik okuma", "hata tespiti", "yanlılık", "sonuç")
        ),
        // TEMA 7: VERİDEN OLASILIĞA
        RagKazanim(
            kod = "MAT.5.6.1",
            ders = "Matematik",
            unite = "Veriden Olasılığa",
            konu = "Olasılık Spektrumu",
            aciklama = "Olasılığın 0 (imkansız) ile 1 (kesin) arasında olduğunu yorumlar. Olayları az/çok olasılıklı olarak sınıflandırır.",
            ornekler = listOf("Yazı-tura: 1/2", "İmkansız olay: 0", "Kesin olay: 1", "Az olasılıklı, çok olasılıklı"),
            keywords = listOf("olasılık", "imkansız", "kesin", "az olasılık", "çok olasılık", "spektrum", "0-1 arası")
        )
    )

    // ==================== FEN BİLİMLERİ ====================
    
    val fenBilimleri = listOf(
        // TEMA 1: DÜNYA VE EVREN
        RagKazanim(
            kod = "F.5.1.1",
            ders = "Fen Bilimleri",
            unite = "Dünya ve Evren",
            konu = "Güneş ve Ay",
            aciklama = "Güneş ve Ay'ın özelliklerini ve Dünya ile ilişkisini açıklar.",
            ornekler = listOf("Güneş bir yıldızdır", "Ay Dünya'nın uydusu"),
            keywords = listOf("güneş", "ay", "dünya", "uydu", "yıldız")
        ),
        // TEMA 2: KUVVET
        RagKazanim(
            kod = "F.5.2.1",
            ders = "Fen Bilimleri",
            unite = "Kuvveti Tanıyalım",
            konu = "Kuvvet ve Ölçülmesi",
            aciklama = "Kuvveti ve dinamometre ile ölçülmesini açıklar.",
            ornekler = listOf("Kuvvet birimi Newton (N)", "Dinamometre"),
            keywords = listOf("kuvvet", "newton", "dinamometre", "ölçme")
        ),
        RagKazanim(
            kod = "F.5.2.2",
            ders = "Fen Bilimleri",
            unite = "Kuvveti Tanıyalım",
            konu = "Kütle ve Ağırlık",
            aciklama = "Kütle ve ağırlık arasındaki farkı açıklar.",
            ornekler = listOf("Kütle: kilogram", "Ağırlık: Newton", "Ağırlık = Kütle × g"),
            keywords = listOf("kütle", "ağırlık", "kilogram", "newton", "yerçekimi")
        ),
        RagKazanim(
            kod = "F.5.2.3",
            ders = "Fen Bilimleri",
            unite = "Kuvveti Tanıyalım",
            konu = "Sürtünme Kuvveti",
            aciklama = "Sürtünme kuvvetinin harekete etkisini açıklar.",
            ornekler = listOf("Pürüzlü yüzey = fazla sürtünme", "Buz üzerinde kayma"),
            keywords = listOf("sürtünme", "kuvvet", "hareket", "yüzey")
        ),
        // TEMA 3: CANLILAR
        RagKazanim(
            kod = "F.5.3.1",
            ders = "Fen Bilimleri",
            unite = "Canlıların Yapısı",
            konu = "Hücre ve Organeller",
            aciklama = "Hücrenin temel yapısını ve organelleri tanır.",
            ornekler = listOf("Çekirdek", "Sitoplazma", "Hücre zarı"),
            keywords = listOf("hücre", "organel", "çekirdek", "sitoplazma", "hücre zarı")
        ),
        RagKazanim(
            kod = "F.5.3.2",
            ders = "Fen Bilimleri",
            unite = "Canlıların Yapısı",
            konu = "Bitki ve Hayvan Hücresi",
            aciklama = "Bitki ve hayvan hücresi arasındaki farkları belirler.",
            ornekler = listOf("Bitki: hücre duvarı var", "Hayvan: hücre duvarı yok"),
            keywords = listOf("bitki hücresi", "hayvan hücresi", "hücre duvarı", "kloroplast")
        ),
        // TEMA 4: IŞIK
        RagKazanim(
            kod = "F.5.4.1",
            ders = "Fen Bilimleri",
            unite = "Işığın Dünyası",
            konu = "Işığın Yayılması ve Gölge",
            aciklama = "Işığın düz yayıldığını ve gölge oluşumunu açıklar.",
            ornekler = listOf("Işık doğrusal yayılır", "Tam gölge oluşumu"),
            keywords = listOf("ışık", "gölge", "tam gölge", "yayılma", "ışık kaynağı")
        ),
        // TEMA 5: MADDE
        RagKazanim(
            kod = "F.5.5.1",
            ders = "Fen Bilimleri",
            unite = "Maddenin Doğası",
            konu = "Tanecikli Yapı",
            aciklama = "Maddenin tanecikli yapısını ve hal değişimini açıklar.",
            ornekler = listOf("Katı: tanecikler sıkı", "Sıvı: tanecikler gevşek", "Gaz: tanecikler serbest"),
            keywords = listOf("tanecik", "madde", "katı", "sıvı", "gaz", "hal değişimi")
        ),
        RagKazanim(
            kod = "F.5.5.2",
            ders = "Fen Bilimleri",
            unite = "Maddenin Doğası",
            konu = "Isı ve Sıcaklık",
            aciklama = "Isı ve sıcaklık kavramlarını ayırt eder.",
            ornekler = listOf("Isı: enerji türü", "Sıcaklık: ölçülebilir büyüklük"),
            keywords = listOf("ısı", "sıcaklık", "enerji", "aktarım", "termometre")
        ),
        // TEMA 6: ELEKTRİK
        RagKazanim(
            kod = "F.5.6.1",
            ders = "Fen Bilimleri",
            unite = "Elektrik",
            konu = "Basit Elektrik Devresi",
            aciklama = "Basit elektrik devresi kurar ve çalışma ilkesini açıklar.",
            ornekler = listOf("Pil + kablo + ampul = devre", "Açık/kapalı devre"),
            keywords = listOf("elektrik", "devre", "pil", "ampul", "anahtar", "kablo")
        ),
        // TEMA 7: SÜRDÜRÜLEBİLİRLİK
        RagKazanim(
            kod = "F.5.7.1",
            ders = "Fen Bilimleri",
            unite = "Sürdürülebilir Yaşam",
            konu = "Geri Dönüşüm",
            aciklama = "Evsel atıklar ve geri dönüşümün önemini açıklar.",
            ornekler = listOf("Plastik, cam, kağıt geri dönüşümü"),
            keywords = listOf("geri dönüşüm", "atık", "çevre", "sürdürülebilirlik")
        )
    )

    // ==================== SOSYAL BİLGİLER ====================
    
    val sosyalBilgiler = listOf(
        // 1. BİRLİKTE YAŞAMAK
        RagKazanim(
            kod = "SB.5.1.1",
            ders = "Sosyal Bilgiler",
            unite = "Birlikte Yaşamak",
            konu = "Grup ve Roller",
            aciklama = "Dahil olduğu grupları ve bu gruplardaki rollerini belirler.",
            ornekler = listOf("Aile, sınıf, arkadaş grubu"),
            keywords = listOf("grup", "rol", "sorumluluk", "aile", "toplum")
        ),
        RagKazanim(
            kod = "SB.5.1.2",
            ders = "Sosyal Bilgiler",
            unite = "Birlikte Yaşamak",
            konu = "Kültürel Saygı",
            aciklama = "Farklı kültürlere saygının birlikte yaşamaya etkisini açıklar.",
            ornekler = listOf("Farklı gelenekler", "Kültürel çeşitlilik"),
            keywords = listOf("kültür", "saygı", "farklılık", "birlikte yaşama")
        ),
        // 2. EVİMİZ DÜNYA
        RagKazanim(
            kod = "SB.5.2.1",
            ders = "Sosyal Bilgiler",
            unite = "Evimiz Dünya",
            konu = "Yaşadığı İlin Özellikleri",
            aciklama = "Yaşadığı ilin göreceli konumunu ve özelliklerini belirler.",
            ornekler = listOf("Komşu iller", "Coğrafi konum"),
            keywords = listOf("il", "konum", "coğrafya", "bölge")
        ),
        RagKazanim(
            kod = "SB.5.2.2",
            ders = "Sosyal Bilgiler",
            unite = "Evimiz Dünya",
            konu = "Afet Farkındalığı",
            aciklama = "Afetlerin etkilerini azaltmaya yönelik farkındalık geliştirir.",
            ornekler = listOf("Deprem", "Sel", "Afet çantası"),
            keywords = listOf("afet", "deprem", "sel", "önlem", "farkındalık")
        ),
        // 3. ORTAK MİRASIMIZ
        RagKazanim(
            kod = "SB.5.3.1",
            ders = "Sosyal Bilgiler",
            unite = "Ortak Mirasımız",
            konu = "Kültürel Miras",
            aciklama = "Somut ve somut olmayan kültürel miras ögelerini tanır.",
            ornekler = listOf("Tarihi yapılar", "Gelenekler", "El sanatları"),
            keywords = listOf("kültürel miras", "tarih", "koruma", "UNESCO")
        ),
        RagKazanim(
            kod = "SB.5.3.2",
            ders = "Sosyal Bilgiler",
            unite = "Ortak Mirasımız",
            konu = "Anadolu Medeniyetleri",
            aciklama = "Anadolu ve Mezopotamya medeniyetlerinin katkılarını karşılaştırır.",
            ornekler = listOf("Hititler", "Sümerler", "Yazının icadı"),
            keywords = listOf("medeniyet", "Anadolu", "Mezopotamya", "tarih")
        ),
        // 4. DEMOKRASİ
        RagKazanim(
            kod = "SB.5.4.1",
            ders = "Sosyal Bilgiler",
            unite = "Yaşayan Demokrasimiz",
            konu = "Demokrasi ve Cumhuriyet",
            aciklama = "Demokrasi ve cumhuriyet kavramları arasındaki ilişkiyi açıklar.",
            ornekler = listOf("Halk egemenliği", "Seçim", "Oy hakkı"),
            keywords = listOf("demokrasi", "cumhuriyet", "egemenlik", "seçim", "vatandaş")
        ),
        RagKazanim(
            kod = "SB.5.4.2",
            ders = "Sosyal Bilgiler",
            unite = "Yaşayan Demokrasimiz",
            konu = "Temel Haklar",
            aciklama = "Temel insan hak ve sorumluluklarının önemini sorgular.",
            ornekler = listOf("Yaşam hakkı", "Eğitim hakkı", "İfade özgürlüğü"),
            keywords = listOf("hak", "sorumluluk", "özgürlük", "insan hakları")
        ),
        // 5. EKONOMİ
        RagKazanim(
            kod = "SB.5.5.1",
            ders = "Sosyal Bilgiler",
            unite = "Hayatımızdaki Ekonomi",
            konu = "Kaynak Kullanımı",
            aciklama = "Kaynakları verimli kullanmanın doğa ve insanlara etkisini açıklar.",
            ornekler = listOf("Su tasarrufu", "Enerji tasarrufu"),
            keywords = listOf("kaynak", "tasarruf", "verimlilik", "çevre")
        ),
        RagKazanim(
            kod = "SB.5.5.2",
            ders = "Sosyal Bilgiler",
            unite = "Hayatımızdaki Ekonomi",
            konu = "Bütçe Planlama",
            aciklama = "İhtiyaç ve isteklerini karşılamak için bütçe planlar.",
            ornekler = listOf("Gelir-gider dengesi", "Harçlık yönetimi"),
            keywords = listOf("bütçe", "plan", "gelir", "gider", "tasarruf")
        ),
        // 6. TEKNOLOJİ
        RagKazanim(
            kod = "SB.5.6.1",
            ders = "Sosyal Bilgiler",
            unite = "Teknoloji ve Sosyal Bilimler",
            konu = "Teknoloji ve Toplum",
            aciklama = "Teknolojik gelişmelerin toplum hayatına etkilerini tartışır.",
            ornekler = listOf("İnternet", "Akıllı telefon", "Sosyal medya"),
            keywords = listOf("teknoloji", "toplum", "etki", "gelişme")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "E.5.1",
            ders = "İngilizce",
            unite = "Hello!",
            konu = "Greetings and Introductions",
            aciklama = "Selamlaşma ve tanışma ifadelerini kullanır.",
            ornekler = listOf("Nice to meet you", "What's your name?", "I'm from Turkey"),
            keywords = listOf("greeting", "introduction", "nice to meet", "name")
        ),
        RagKazanim(
            kod = "E.5.2",
            ders = "İngilizce",
            unite = "My Town",
            konu = "Locations and Directions",
            aciklama = "Yer tarif eder ve yön sorar.",
            ornekler = listOf("Where is the bank?", "Turn left", "Go straight"),
            keywords = listOf("location", "direction", "where", "turn", "street")
        ),
        RagKazanim(
            kod = "E.5.3",
            ders = "İngilizce",
            unite = "Games and Hobbies",
            konu = "Abilities and Hobbies",
            aciklama = "Yapabilme ve hobilerini ifade eder.",
            ornekler = listOf("I can swim", "She likes playing chess", "Do you play football?"),
            keywords = listOf("can", "hobby", "play", "like", "game")
        ),
        RagKazanim(
            kod = "E.5.4",
            ders = "İngilizce",
            unite = "My Daily Routine",
            konu = "Daily Activities and Time",
            aciklama = "Günlük rutinleri ve saati ifade eder.",
            ornekler = listOf("I wake up at 7", "She has breakfast", "What time is it?"),
            keywords = listOf("routine", "time", "daily", "wake up", "breakfast")
        ),
        RagKazanim(
            kod = "E.5.5",
            ders = "İngilizce",
            unite = "Health",
            konu = "Illnesses and Suggestions",
            aciklama = "Hastalık ve öneri ifadelerini kullanır.",
            ornekler = listOf("I have a headache", "You should see a doctor", "Take medicine"),
            keywords = listOf("health", "illness", "headache", "doctor", "should")
        ),
        RagKazanim(
            kod = "E.5.6",
            ders = "İngilizce",
            unite = "Movies",
            konu = "Describing Characters",
            aciklama = "Film ve karakterleri tanımlar.",
            ornekler = listOf("It's a comedy", "The hero is brave", "I like action movies"),
            keywords = listOf("movie", "film", "character", "describe", "like")
        ),
        RagKazanim(
            kod = "E.5.7",
            ders = "İngilizce",
            unite = "Party Time",
            konu = "Invitations and Celebrations",
            aciklama = "Davet ve kutlama ifadelerini kullanır.",
            ornekler = listOf("Can I have a party?", "Happy birthday!", "Welcome!"),
            keywords = listOf("party", "birthday", "invitation", "celebration")
        ),
        RagKazanim(
            kod = "E.5.8",
            ders = "İngilizce",
            unite = "Fitness",
            konu = "Sports and Exercise",
            aciklama = "Spor ve egzersiz ifadelerini kullanır.",
            ornekler = listOf("Let's go jogging", "I like cycling", "How about swimming?"),
            keywords = listOf("sport", "exercise", "fitness", "jogging", "cycling")
        ),
        RagKazanim(
            kod = "E.5.9",
            ders = "İngilizce",
            unite = "The Animal Shelter",
            konu = "Present Continuous",
            aciklama = "Şu an yapılan eylemleri ifade eder.",
            ornekler = listOf("The dog is playing", "They are feeding the cats", "What is she doing?"),
            keywords = listOf("present continuous", "now", "doing", "animal", "shelter")
        ),
        RagKazanim(
            kod = "E.5.10",
            ders = "İngilizce",
            unite = "Festivals",
            konu = "Cultural Celebrations",
            aciklama = "Bayram ve festivalleri tanımlar.",
            ornekler = listOf("We celebrate Eid", "Children's Day is in April"),
            keywords = listOf("festival", "celebration", "eid", "holiday", "culture")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val dinKulturu = listOf(
        RagKazanim(
            kod = "DK.5.1.1",
            ders = "Din Kültürü",
            unite = "Allah İnancı",
            konu = "Evrendeki Düzen ve Allah'ın Varlığı",
            aciklama = "Evrendeki düzenden yola çıkarak Allah'ın varlığını kavrar.",
            ornekler = listOf("Güneş sistemi", "Canlıların yapısı", "Doğadaki denge"),
            keywords = listOf("Allah", "yaratıcı", "evren", "düzen", "varlık")
        ),
        RagKazanim(
            kod = "DK.5.1.2",
            ders = "Din Kültürü",
            unite = "Allah İnancı",
            konu = "Allah'ın Güzel İsimleri",
            aciklama = "Allah'ın güzel isimlerini (Esma-ül Hüsna) öğrenir.",
            ornekler = listOf("Rahman", "Rahim", "Kerim", "Halık"),
            keywords = listOf("esma", "isim", "rahman", "rahim", "Allah")
        ),
        RagKazanim(
            kod = "DK.5.2.1",
            ders = "Din Kültürü",
            unite = "Namaz",
            konu = "Namazın Kılınışı ve Önemi",
            aciklama = "Namazın nasıl kılındığını ve insana kazandırdıklarını açıklar.",
            ornekler = listOf("Vakitler", "Rekat", "Rüku", "Secde"),
            keywords = listOf("namaz", "ibadet", "vakit", "kılınış")
        ),
        RagKazanim(
            kod = "DK.5.3.1",
            ders = "Din Kültürü",
            unite = "Kuran-ı Kerim",
            konu = "Kuran'ın Özellikleri",
            aciklama = "Kuran-ı Kerim'in iç düzenini ve temel özelliklerini tanır.",
            ornekler = listOf("Sure", "Ayet", "Cüz", "Hizb"),
            keywords = listOf("Kuran", "sure", "ayet", "mushaf")
        ),
        RagKazanim(
            kod = "DK.5.4.1",
            ders = "Din Kültürü",
            unite = "Peygamber Kıssaları",
            konu = "Peygamberlerin Hayatı",
            aciklama = "Kuran'da geçen peygamber kıssalarından dersler çıkarır.",
            ornekler = listOf("Hz. Nuh", "Hz. İbrahim", "Hz. Yusuf"),
            keywords = listOf("peygamber", "kıssa", "öğüt", "ibret")
        ),
        RagKazanim(
            kod = "DK.5.5.1",
            ders = "Din Kültürü",
            unite = "Mimaride Dini Motifler",
            konu = "Camiler ve Dini Yapılar",
            aciklama = "Türk-İslam mimarisindeki dini motifleri tanır.",
            ornekler = listOf("Minare", "Kubbe", "Mihrap", "Minber"),
            keywords = listOf("cami", "mimari", "kubbe", "minare", "süsleme")
        )
    )

    // İmam Hatip ek dersleri
    val arapca = listOf(
        RagKazanim(
            kod = "AR.5.1",
            ders = "Arapça",
            unite = "Temel Arapça",
            konu = "Harfler ve Kelimeler",
            aciklama = "Arap alfabesini ve temel kelimeleri öğrenir.",
            ornekler = listOf("ا ب ت ث", "Merhaba = مرحبا"),
            keywords = listOf("arapça", "harf", "kelime", "alfabe")
        )
    )

    val kuran = listOf(
        RagKazanim(
            kod = "KK.5.1",
            ders = "Kur'an-ı Kerim",
            unite = "Kur'an Okuma",
            konu = "Tecvid ve Okuma",
            aciklama = "Tecvid kurallarına uygun Kur'an okur.",
            ornekler = listOf("Harflerin mahreci", "Med", "Şedde"),
            keywords = listOf("kuran", "tecvid", "okuma", "mahreç")
        )
    )

    val siyer = listOf(
        RagKazanim(
            kod = "SY.5.1",
            ders = "Peygamberimizin Hayatı",
            unite = "Siyer",
            konu = "Peygamberimizin Hayatı",
            aciklama = "Peygamberimizin hayatını ve örnek davranışlarını öğrenir.",
            ornekler = listOf("Mekke dönemi", "Medine dönemi", "Ahlakı"),
            keywords = listOf("siyer", "peygamber", "Hz. Muhammed", "hayat")
        )
    )

    // Tüm kazanımları birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return turkce + matematik + fenBilimleri + sosyalBilgiler + ingilizce + dinKulturu + arapca + kuran + siyer
    }
}
