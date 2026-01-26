package com.example.bilgideham

/**
 * 6. Sınıf MEB 2025-2026 Müfredatı (TYMM)
 * RAG sistemi için detaylı kazanım veritabanı
 */
object Sinif6Kazanimlari {

    // ==================== TÜRKÇE ====================
    
    val turkce = listOf(
        // 1. SÖZCÜK VE SÖZCÜK GRUPLARINDA ANLAM
        RagKazanim(
            kod = "T.6.1.1",
            ders = "Türkçe",
            unite = "Sözcükte Anlam",
            konu = "Çok Anlamlılık ve Mecaz Anlam",
            aciklama = "Sözcüklerin çok anlamlılığını, gerçek ve mecaz anlamlarını ayırt eder.",
            ornekler = listOf("Ağız: organ/kapı ağzı/nehir ağzı", "Göz: organ/masa gözü"),
            keywords = listOf("çok anlamlı", "mecaz", "gerçek anlam", "anlam genişlemesi")
        ),
        RagKazanim(
            kod = "T.6.1.2",
            ders = "Türkçe",
            unite = "Söz Gruplarında Anlam",
            konu = "Deyimler ve Atasözleri",
            aciklama = "Deyim ve atasözlerini anlam ve kullanım bakımından değerlendirir.",
            ornekler = listOf("Çam devirmek", "Ağaç yaşken eğilir"),
            keywords = listOf("deyim", "atasözü", "kalıp söz", "mecaz anlam")
        ),
        // 2. CÜMLEDE ANLAM
        RagKazanim(
            kod = "T.6.2.1",
            ders = "Türkçe",
            unite = "Cümlede Anlam",
            konu = "Anlam İlişkileri",
            aciklama = "Neden-sonuç, amaç-sonuç, koşul-sonuç ilişkilerini kurar.",
            ornekler = listOf("Çünkü/Bu yüzden (neden-sonuç)", "için (amaç-sonuç)", "eğer/-sa (koşul)"),
            keywords = listOf("neden", "sonuç", "amaç", "koşul", "şart")
        ),
        RagKazanim(
            kod = "T.6.2.2",
            ders = "Türkçe",
            unite = "Cümlede Anlam",
            konu = "Öznel ve Nesnel Anlatım",
            aciklama = "Öznel ve nesnel yargılı cümleleri ayırt eder.",
            ornekler = listOf("Bu kitap çok güzel (öznel)", "Kitap 200 sayfadır (nesnel)"),
            keywords = listOf("öznel", "nesnel", "kişisel görüş", "kanıtlanabilir")
        ),
        // 3. PARÇADA ANLAM
        RagKazanim(
            kod = "T.6.3.1",
            ders = "Türkçe",
            unite = "Paragrafta Anlam",
            konu = "Metin Yapısı ve Ana Düşünce",
            aciklama = "Metnin konusu, ana fikri ve yardımcı fikirlerini belirler.",
            ornekler = listOf("Ana fikir/Ana düşünce", "Yardımcı düşünce", "Metnin konusu"),
            keywords = listOf("ana fikir", "yardımcı fikir", "konu", "tema")
        ),
        RagKazanim(
            kod = "T.6.3.2",
            ders = "Türkçe",
            unite = "Paragrafta Anlam",
            konu = "Hikaye Unsurları",
            aciklama = "Hikaye metinlerinde olay, yer, zaman ve kahramanları belirler.",
            ornekler = listOf("Olay örgüsü", "Mekan", "Zaman", "Karakterler"),
            keywords = listOf("olay", "yer", "zaman", "kahraman", "hikaye unsuru")
        ),
        // 4. DİL BİLGİSİ: İSİMLER VE SIFATLAR
        RagKazanim(
            kod = "T.6.4.1",
            ders = "Türkçe",
            unite = "Dil Bilgisi",
            konu = "İsimler ve İsim Tamlamaları",
            aciklama = "İsimleri türlerine göre sınıflandırır, isim tamlamalarını tanır.",
            ornekler = listOf("Somut/Soyut isim", "Belirtili/Belirtisiz tamlama", "Zincirleme tamlama"),
            keywords = listOf("isim", "ad", "tamlama", "belirtili", "belirtisiz", "zincirleme")
        ),
        RagKazanim(
            kod = "T.6.4.2",
            ders = "Türkçe",
            unite = "Dil Bilgisi",
            konu = "Sıfatlar",
            aciklama = "Niteleme ve belirtme sıfatlarını tanır ve kullanır.",
            ornekler = listOf("Güzel ev (niteleme)", "Bu/Şu/O (işaret)", "Beş elma (sayı)"),
            keywords = listOf("sıfat", "ön ad", "niteleme", "belirtme", "işaret", "sayı")
        ),
        // 5. ZAMİRLER
        RagKazanim(
            kod = "T.6.5.1",
            ders = "Türkçe",
            unite = "Dil Bilgisi",
            konu = "Zamirler",
            aciklama = "Sözcük ve ek durumundaki zamirleri tanır.",
            ornekler = listOf("Ben, sen, o (kişi)", "Bu, şu, o (işaret)", "Kim, ne (soru)", "-m, -n, -ı (ek)"),
            keywords = listOf("zamir", "adıl", "kişi zamiri", "işaret zamiri", "ek zamir")
        ),
        // 6. EDAT, BAĞLAÇ, ÜNLEM
        RagKazanim(
            kod = "T.6.6.1",
            ders = "Türkçe",
            unite = "Dil Bilgisi",
            konu = "Edat, Bağlaç ve Ünlem",
            aciklama = "Edat, bağlaç ve ünlemlerin işlevlerini kavrar.",
            ornekler = listOf("için, gibi (edat)", "ve, ama, çünkü (bağlaç)", "Eyvah! Bravo! (ünlem)"),
            keywords = listOf("edat", "bağlaç", "ünlem", "ilgeç")
        ),
        // 7-8. METİN TÜRLERİ VE SÖZ SANATLARI
        RagKazanim(
            kod = "T.6.7.1",
            ders = "Türkçe",
            unite = "Metin Türleri",
            konu = "Metin Türleri ve Söz Sanatları",
            aciklama = "Anı, mektup, tiyatro, gezi yazısı türlerini ve konuşturma/tezat sanatlarını tanır.",
            ornekler = listOf("Anı yazısı", "Mektup formatı", "Tiyatro diyalog", "İntak (konuşturma)"),
            keywords = listOf("anı", "mektup", "tiyatro", "gezi yazısı", "intak", "tezat")
        )
    )

    // ==================== MATEMATİK (MEB 2025-2026) ====================
    
    val matematik = listOf(
        // 1. DOĞAL SAYILARLA İŞLEMLER
        RagKazanim(
            kod = "MAT.6.1.1",
            ders = "Matematik",
            unite = "Doğal Sayılarla İşlemler",
            konu = "Üslü İfadeler",
            aciklama = "Bir doğal sayının kendisiyle tekrarlı çarpımını üslü ifade olarak yazar.",
            ornekler = listOf("2×2×2 = 2³", "10² = 100", "5¹ = 5"),
            keywords = listOf("üs", "taban", "kuvvet", "üslü ifade", "tekrarlı çarpım")
        ),
        RagKazanim(
            kod = "MAT.6.1.2",
            ders = "Matematik",
            unite = "Doğal Sayılarla İşlemler",
            konu = "İşlem Önceliği",
            aciklama = "Dört işlem ve üslü ifadelerde işlem önceliğini uygular.",
            ornekler = listOf("2+3×4=14", "2³+5=13", "Parantez önce"),
            keywords = listOf("işlem önceliği", "parantez", "üs", "çarpma", "toplama")
        ),
        RagKazanim(
            kod = "MAT.6.1.3",
            ders = "Matematik",
            unite = "Doğal Sayılarla İşlemler",
            konu = "Dağılma Özelliği ve Ortak Çarpan",
            aciklama = "Dağılma özelliğini kullanır, ortak çarpanı paranteze alır.",
            ornekler = listOf("3×(4+5)=3×4+3×5", "6+9=3×(2+3)", "Ortak çarpan parantezi"),
            keywords = listOf("dağılma", "ortak çarpan", "parantez", "çarpma")
        ),
        // 2. ÇARPANLAR VE KATLAR
        RagKazanim(
            kod = "MAT.6.2.1",
            ders = "Matematik",
            unite = "Çarpanlar ve Katlar",
            konu = "Bölünebilme Kuralları",
            aciklama = "2, 3, 4, 5, 6, 9 ve 10 ile bölünebilme kurallarını uygular.",
            ornekler = listOf("2 ile: son rakam çift", "3 ile: rakamlar toplamı 3'e bölünür", "5 ile: 0 veya 5"),
            keywords = listOf("bölünebilme", "kural", "bölen", "kalansız bölme")
        ),
        RagKazanim(
            kod = "MAT.6.2.2",
            ders = "Matematik",
            unite = "Çarpanlar ve Katlar",
            konu = "Asal Sayılar",
            aciklama = "Asal sayıları tanır, bir sayıyı asal çarpanlarına ayırır.",
            ornekler = listOf("2, 3, 5, 7, 11 asal", "12 = 2² × 3", "Asal ağacı"),
            keywords = listOf("asal", "asal çarpan", "asal ağacı", "çarpanlara ayırma")
        ),
        RagKazanim(
            kod = "MAT.6.2.3",
            ders = "Matematik",
            unite = "Çarpanlar ve Katlar",
            konu = "EBOB ve EKOK Temeli",
            aciklama = "Ortak bölenler ve ortak katlar kavramını anlar.",
            ornekler = listOf("12 ve 18'in ortak bölenleri", "4 ve 6'nın ortak katları"),
            keywords = listOf("ortak bölen", "ortak kat", "EBOB", "EKOK")
        ),
        // 3. KÜMELER
        RagKazanim(
            kod = "MAT.6.3.1",
            ders = "Matematik",
            unite = "Kümeler",
            konu = "Küme Kavramı ve İşlemler",
            aciklama = "Kümeleri tanır, birleşim ve kesişim işlemlerini yapar.",
            ornekler = listOf("A = {1,2,3}", "A ∪ B (birleşim)", "A ∩ B (kesişim)"),
            keywords = listOf("küme", "eleman", "birleşim", "kesişim", "alt küme")
        ),
        // 4. TAM SAYILAR
        RagKazanim(
            kod = "MAT.6.4.1",
            ders = "Matematik",
            unite = "Tam Sayılar",
            konu = "Tam Sayılar ve Sayı Doğrusu",
            aciklama = "Tam sayıları tanır, sayı doğrusunda gösterir.",
            ornekler = listOf("..., -3, -2, -1, 0, 1, 2, 3, ...", "Negatif ve pozitif sayılar"),
            keywords = listOf("tam sayı", "negatif", "pozitif", "sayı doğrusu")
        ),
        RagKazanim(
            kod = "MAT.6.4.2",
            ders = "Matematik",
            unite = "Tam Sayılar",
            konu = "Mutlak Değer",
            aciklama = "Tam sayıların mutlak değerini bulur.",
            ornekler = listOf("|-5| = 5", "|3| = 3", "|0| = 0"),
            keywords = listOf("mutlak değer", "uzaklık", "sıfıra uzaklık")
        ),
        RagKazanim(
            kod = "MAT.6.4.3",
            ders = "Matematik",
            unite = "Tam Sayılar",
            konu = "Tam Sayı Karşılaştırma",
            aciklama = "Tam sayıları karşılaştırır ve sıralar.",
            ornekler = listOf("-5 < -3 < 0 < 2", "Büyükten küçüğe sıralama"),
            keywords = listOf("karşılaştırma", "sıralama", "küçük", "büyük")
        ),
        // 5. KESİRLERLE İŞLEMLER
        RagKazanim(
            kod = "MAT.6.5.1",
            ders = "Matematik",
            unite = "Kesirlerle İşlemler",
            konu = "Kesir Toplama ve Çıkarma",
            aciklama = "Paydaları eşit ve farklı kesirleri toplar ve çıkarır.",
            ornekler = listOf("1/4 + 2/4 = 3/4", "1/2 + 1/3 = 5/6"),
            keywords = listOf("kesir", "toplama", "çıkarma", "pay", "payda", "ortak payda")
        ),
        RagKazanim(
            kod = "MAT.6.5.2",
            ders = "Matematik",
            unite = "Kesirlerle İşlemler",
            konu = "Kesir Çarpma ve Bölme",
            aciklama = "Kesirleri çarpar ve böler.",
            ornekler = listOf("2/3 × 3/4 = 1/2", "2/3 ÷ 1/2 = 4/3"),
            keywords = listOf("kesir", "çarpma", "bölme", "ters çevirme")
        ),
        // 6. ONDALIK GÖSTERİM
        RagKazanim(
            kod = "MAT.6.6.1",
            ders = "Matematik",
            unite = "Ondalık Gösterim",
            konu = "Ondalık Sayı İşlemleri",
            aciklama = "Ondalık gösterimlerle yuvarlama, çarpma ve bölme yapar.",
            ornekler = listOf("3,456 ≈ 3,46", "2,5 × 0,4 = 1", "1,2 ÷ 0,3 = 4"),
            keywords = listOf("ondalık", "yuvarlama", "çarpma", "bölme", "virgül")
        ),
        // 7. ORAN
        RagKazanim(
            kod = "MAT.6.7.1",
            ders = "Matematik",
            unite = "Oran",
            konu = "Oran Kavramı",
            aciklama = "Oran kavramını, birimli ve birimsiz oranları açıklar.",
            ornekler = listOf("3/5 oranı", "km/saat (birimli)", "2:3 (birimsiz)"),
            keywords = listOf("oran", "birimli", "birimsiz", "karşılaştırma")
        ),
        // 8. CEBİRSEL İFADELER
        RagKazanim(
            kod = "MAT.6.8.1",
            ders = "Matematik",
            unite = "Cebirsel İfadeler",
            konu = "Cebirsel İfadeler",
            aciklama = "Cebirsel ifadeleri yazar ve değerini hesaplar.",
            ornekler = listOf("2x + 3", "x = 5 için: 2×5+3 = 13"),
            keywords = listOf("cebir", "değişken", "ifade", "değer hesaplama")
        ),
        // 9. VERİ
        RagKazanim(
            kod = "MAT.6.9.1",
            ders = "Matematik",
            unite = "Veri Analizi",
            konu = "Aritmetik Ortalama ve Açıklık",
            aciklama = "Veri grubunun aritmetik ortalamasını ve açıklığını hesaplar.",
            ornekler = listOf("Ortalama = Toplam/Sayı", "Açıklık = En büyük - En küçük"),
            keywords = listOf("aritmetik ortalama", "açıklık", "veri", "merkezi eğilim")
        ),
        // 10. AÇILAR
        RagKazanim(
            kod = "MAT.6.10.1",
            ders = "Matematik",
            unite = "Açılar",
            konu = "Açı Türleri ve Hesaplama",
            aciklama = "Açı türlerini bilir, bütünler ve tümler açıları hesaplar.",
            ornekler = listOf("Bütünler: toplam 180°", "Tümler: toplam 90°"),
            keywords = listOf("açı", "bütünler", "tümler", "derece", "ölçme")
        ),
        // 11. ALAN ÖLÇME
        RagKazanim(
            kod = "MAT.6.11.1",
            ders = "Matematik",
            unite = "Alan Ölçme",
            konu = "Üçgen ve Paralelkenar Alanı",
            aciklama = "Üçgen ve paralelkenarın alanını hesaplar.",
            ornekler = listOf("Üçgen = taban×yükseklik/2", "Paralelkenar = taban×yükseklik"),
            keywords = listOf("alan", "üçgen", "paralelkenar", "formül")
        ),
        // 12. ÇEMBER VE CİSİMLER
        RagKazanim(
            kod = "MAT.6.12.1",
            ders = "Matematik",
            unite = "Çember ve Geometrik Cisimler",
            konu = "Çember ve Sıvı Ölçme",
            aciklama = "Çemberi tanır, sıvı ölçü birimlerini kullanır.",
            ornekler = listOf("Yarıçap, çap", "Litre, mililitre"),
            keywords = listOf("çember", "yarıçap", "çap", "litre", "hacim")
        )
    )

    // ==================== FEN BİLİMLERİ ====================
    
    val fenBilimleri = listOf(
        // 1. GÜNEŞ SİSTEMİ VE TUTULMALAR
        RagKazanim(
            kod = "F.6.1.1",
            ders = "Fen Bilimleri",
            unite = "Güneş Sistemi",
            konu = "Güneş Sistemi ve Gök Cisimleri",
            aciklama = "Güneş sistemi, gezegenler, meteor, asteroit ve kuyruklu yıldızları tanır.",
            ornekler = listOf("8 gezegen", "Asteroid kuşağı", "Kuyruklu yıldız"),
            keywords = listOf("güneş sistemi", "gezegen", "meteor", "asteroit", "gök cismi")
        ),
        RagKazanim(
            kod = "F.6.1.2",
            ders = "Fen Bilimleri",
            unite = "Güneş Sistemi",
            konu = "Güneş ve Ay Tutulmaları",
            aciklama = "Güneş ve Ay tutulmalarının nasıl oluştuğunu açıklar.",
            ornekler = listOf("Güneş tutulması: Ay araya girer", "Ay tutulması: Dünya araya girer"),
            keywords = listOf("tutulma", "güneş tutulması", "ay tutulması", "gölge")
        ),
        // 2. VÜCUDUMUZDAKİ SİSTEMLER
        RagKazanim(
            kod = "F.6.2.1",
            ders = "Fen Bilimleri",
            unite = "Vücudumuzdaki Sistemler",
            konu = "Destek ve Hareket Sistemi",
            aciklama = "Kemik, eklem ve kasların yapı ve görevlerini açıklar.",
            ornekler = listOf("206 kemik", "Eklem türleri", "Kasların çalışması"),
            keywords = listOf("iskelet", "kemik", "eklem", "kas", "hareket")
        ),
        RagKazanim(
            kod = "F.6.2.2",
            ders = "Fen Bilimleri",
            unite = "Vücudumuzdaki Sistemler",
            konu = "Sindirim Sistemi",
            aciklama = "Sindirim sistemi organlarını ve sindirim sürecini açıklar.",
            ornekler = listOf("Ağız, yemek borusu, mide, bağırsaklar"),
            keywords = listOf("sindirim", "ağız", "mide", "bağırsak", "enzim")
        ),
        RagKazanim(
            kod = "F.6.2.3",
            ders = "Fen Bilimleri",
            unite = "Vücudumuzdaki Sistemler",
            konu = "Dolaşım Sistemi",
            aciklama = "Kalp, kan ve damarların yapı ve görevlerini açıklar.",
            ornekler = listOf("Kalp: 4 odacık", "Atardamar, toplardamar, kılcal damar"),
            keywords = listOf("dolaşım", "kalp", "kan", "damar", "atardamar", "toplardamar")
        ),
        RagKazanim(
            kod = "F.6.2.4",
            ders = "Fen Bilimleri",
            unite = "Vücudumuzdaki Sistemler",
            konu = "Solunum ve Boşaltım Sistemi",
            aciklama = "Solunum ve boşaltım organlarının görevlerini açıklar.",
            ornekler = listOf("Burun, nefes borusu, akciğer", "Böbrek, idrar kesesi"),
            keywords = listOf("solunum", "akciğer", "boşaltım", "böbrek", "oksijen")
        ),
        // 3. KUVVET VE HAREKET
        RagKazanim(
            kod = "F.6.3.1",
            ders = "Fen Bilimleri",
            unite = "Kuvvet ve Hareket",
            konu = "Bileşke Kuvvet ve Sabit Süratli Hareket",
            aciklama = "Bileşke kuvvet ve sabit süratli hareket kavramlarını açıklar.",
            ornekler = listOf("Aynı yöndeki kuvvetler toplanır", "Ters yöndeki kuvvetler çıkarılır"),
            keywords = listOf("bileşke", "kuvvet", "sürat", "hareket", "newton")
        ),
        // 4. MADDE VE ISI
        RagKazanim(
            kod = "F.6.4.1",
            ders = "Fen Bilimleri",
            unite = "Madde ve Isı",
            konu = "Yoğunluk",
            aciklama = "Yoğunluk kavramını açıklar, birimini kullanır.",
            ornekler = listOf("Yoğunluk = Kütle/Hacim", "g/cm³, kg/m³"),
            keywords = listOf("yoğunluk", "kütle", "hacim", "birim")
        ),
        RagKazanim(
            kod = "F.6.4.2",
            ders = "Fen Bilimleri",
            unite = "Madde ve Isı",
            konu = "Isı Yalıtımı ve Yakıtlar",
            aciklama = "Isı yalıtımının önemini ve yakıt türlerini açıklar.",
            ornekler = listOf("Cam yünü, strafor", "Fosil yakıtlar", "Yenilenebilir enerji"),
            keywords = listOf("ısı", "yalıtım", "yakıt", "enerji", "tasarruf")
        ),
        // 5. SES
        RagKazanim(
            kod = "F.6.5.1",
            ders = "Fen Bilimleri",
            unite = "Ses",
            konu = "Sesin Özellikleri",
            aciklama = "Sesin yayılması, sürati ve enerjisini açıklar.",
            ornekler = listOf("Ses titreşimle yayılır", "Katıda en hızlı, gazda en yavaş"),
            keywords = listOf("ses", "titreşim", "yayılma", "sürat", "enerji")
        ),
        // 6. SİNİR SİSTEMİ VE DUYU ORGANLARI
        RagKazanim(
            kod = "F.6.6.1",
            ders = "Fen Bilimleri",
            unite = "Denetleyici Sistemler",
            konu = "Sinir Sistemi ve Duyu Organları",
            aciklama = "Sinir sistemi, iç salgı bezleri ve duyu organlarını tanır.",
            ornekler = listOf("Beyin, omurilik, sinirler", "Göz, kulak, burun, dil, deri"),
            keywords = listOf("sinir", "beyin", "duyu", "organ", "hormon")
        ),
        // 7. ELEKTRİK
        RagKazanim(
            kod = "F.6.7.1",
            ders = "Fen Bilimleri",
            unite = "Elektrik",
            konu = "İletken, Yalıtkan ve Direnç",
            aciklama = "İletken, yalıtkan maddeleri ve elektriksel direnci açıklar.",
            ornekler = listOf("Bakır, demir (iletken)", "Plastik, cam (yalıtkan)", "Direnç birimi: Ohm"),
            keywords = listOf("iletken", "yalıtkan", "direnç", "elektrik", "ohm")
        )
    )

    // ==================== SOSYAL BİLGİLER ====================
    
    val sosyalBilgiler = listOf(
        // 1. BİREY VE TOPLUM
        RagKazanim(
            kod = "SB.6.1.1",
            ders = "Sosyal Bilgiler",
            unite = "Birey ve Toplum",
            konu = "Roller ve Kültür",
            aciklama = "Değişen rolleri ve kültürel ön yargıları inceler.",
            ornekler = listOf("Değişen roller", "Kültürel farklılıklar", "Ön yargı"),
            keywords = listOf("rol", "kültür", "ön yargı", "birey", "toplum")
        ),
        // 2. KÜLTÜR VE MİRAS
        RagKazanim(
            kod = "SB.6.2.1",
            ders = "Sosyal Bilgiler",
            unite = "Kültür ve Miras",
            konu = "İlk Türk Devletleri",
            aciklama = "Orta Asya'daki ilk Türk devletlerini, destanları ve yazıtları tanır.",
            ornekler = listOf("Hunlar, Göktürkler, Uygurlar", "Oğuz Kağan Destanı", "Orhun Yazıtları"),
            keywords = listOf("Türk", "devlet", "Orta Asya", "destan", "yazıt")
        ),
        RagKazanim(
            kod = "SB.6.2.2",
            ders = "Sosyal Bilgiler",
            unite = "Kültür ve Miras",
            konu = "İslamiyet ve Türk-İslam Devletleri",
            aciklama = "İslamiyet'in doğuşunu ve ilk Türk-İslam devletlerini açıklar.",
            ornekler = listOf("Hz. Muhammed", "Karahanlılar", "Gazneliler", "Selçuklular"),
            keywords = listOf("İslam", "Türk", "Karahanlı", "Gazneli", "Selçuklu")
        ),
        // 3. COĞRAFYA
        RagKazanim(
            kod = "SB.6.3.1",
            ders = "Sosyal Bilgiler",
            unite = "Coğrafya",
            konu = "Konum ve Türkiye Coğrafyası",
            aciklama = "Koordinat sistemini ve Türkiye'nin coğrafi özelliklerini açıklar.",
            ornekler = listOf("Enlem, boylam", "İklim bölgeleri", "Yeryüzü şekilleri"),
            keywords = listOf("konum", "koordinat", "iklim", "coğrafya", "Türkiye")
        ),
        // 4. BİLİM VE TEKNOLOJİ
        RagKazanim(
            kod = "SB.6.4.1",
            ders = "Sosyal Bilgiler",
            unite = "Bilim ve Teknoloji",
            konu = "Bilimsel Araştırma ve Haklar",
            aciklama = "Bilimsel araştırma basamakları, telif ve patent haklarını açıklar.",
            ornekler = listOf("Hipotez, deney, sonuç", "Telif hakkı", "Patent"),
            keywords = listOf("bilim", "araştırma", "telif", "patent", "teknoloji")
        ),
        // 5. EKONOMİ
        RagKazanim(
            kod = "SB.6.5.1",
            ders = "Sosyal Bilgiler",
            unite = "Ekonomi",
            konu = "Kaynaklar ve Vergi",
            aciklama = "Doğal kaynakların ekonomiye katkısını ve vergi kavramını açıklar.",
            ornekler = listOf("Toprak, su, maden, orman", "Yatırım", "Vergi"),
            keywords = listOf("kaynak", "ekonomi", "vergi", "yatırım", "pazarlama")
        ),
        // 6. VATANDAŞLIK
        RagKazanim(
            kod = "SB.6.6.1",
            ders = "Sosyal Bilgiler",
            unite = "Vatandaşlık",
            konu = "Yönetim Biçimleri ve Kuvvetler Ayrılığı",
            aciklama = "Demokrasi ve diğer yönetim biçimlerini, yasama-yürütme-yargıyı açıklar.",
            ornekler = listOf("Demokrasi, monarşi", "TBMM (yasama)", "Hükümet (yürütme)"),
            keywords = listOf("demokrasi", "monarşi", "yasama", "yürütme", "yargı")
        ),
        // 7. KÜRESEL BAĞLANTILAR
        RagKazanim(
            kod = "SB.6.7.1",
            ders = "Sosyal Bilgiler",
            unite = "Küresel Bağlantılar",
            konu = "Komşular ve Türk Cumhuriyetleri",
            aciklama = "Türkiye'nin komşularını ve Türk cumhuriyetlerini tanır.",
            ornekler = listOf("Azerbaycan, Kazakistan, Özbekistan", "Uluslararası ticaret"),
            keywords = listOf("komşu", "Türk cumhuriyeti", "ticaret", "uluslararası")
        )
    )

    // ==================== İNGİLİZCE ====================
    
    val ingilizce = listOf(
        RagKazanim(
            kod = "E.6.1",
            ders = "İngilizce",
            unite = "Life",
            konu = "Daily Routines and Time",
            aciklama = "Günlük rutinleri ve saati ifade eder.",
            ornekler = listOf("I get up at 7", "What time is it?", "On Monday"),
            keywords = listOf("daily", "routine", "time", "date")
        ),
        RagKazanim(
            kod = "E.6.2",
            ders = "İngilizce",
            unite = "Yummy Breakfast",
            konu = "Food, Likes and Dislikes",
            aciklama = "Yiyecek içecekler ve beğenileri ifade eder.",
            ornekler = listOf("I like eggs", "She doesn't like milk", "Do you want some?"),
            keywords = listOf("food", "drink", "like", "breakfast")
        ),
        RagKazanim(
            kod = "E.6.3",
            ders = "İngilizce",
            unite = "Downtown",
            konu = "Comparisons and Present Continuous",
            aciklama = "Karşılaştırma ve şimdiki zaman ifade eder.",
            ornekler = listOf("bigger than", "the biggest", "She is walking"),
            keywords = listOf("comparison", "present continuous", "places", "town")
        ),
        RagKazanim(
            kod = "E.6.7",
            ders = "İngilizce",
            unite = "Holidays",
            konu = "Past Tense",
            aciklama = "Geçmiş zaman ve düzensiz fiilleri kullanır.",
            ornekler = listOf("I went to the beach", "Did you visit?", "She didn't come"),
            keywords = listOf("past", "tense", "irregular", "holiday")
        ),
        RagKazanim(
            kod = "E.6.9",
            ders = "İngilizce",
            unite = "Saving the Planet",
            konu = "Environment and Modals",
            aciklama = "Çevre ve should/shouldn't yapılarını kullanır.",
            ornekler = listOf("You should recycle", "We shouldn't waste water"),
            keywords = listOf("environment", "should", "planet", "recycle")
        )
    )

    // ==================== DİN KÜLTÜRÜ ====================
    
    val dinKulturu = listOf(
        RagKazanim(
            kod = "DK.6.1.1",
            ders = "Din Kültürü",
            unite = "Peygamber ve İlahi Kitap İnancı",
            konu = "Peygamberler ve Vahiy",
            aciklama = "Peygamberlerin özelliklerini ve ilahi kitapları tanır.",
            ornekler = listOf("Sıdk, emanet, tebliğ, fetanet", "Tevrat, Zebur, İncil, Kuran"),
            keywords = listOf("peygamber", "vahiy", "ilahi kitap", "Kuran")
        ),
        RagKazanim(
            kod = "DK.6.2.1",
            ders = "Din Kültürü",
            unite = "Namaz",
            konu = "Namaz Çeşitleri ve Abdest",
            aciklama = "Farz, vacip, sünnet namazları ve abdest alınışını açıklar.",
            ornekler = listOf("Farz namazlar", "Cuma namazı", "Abdest organları"),
            keywords = listOf("namaz", "abdest", "farz", "sünnet", "ibadet")
        ),
        RagKazanim(
            kod = "DK.6.4.1",
            ders = "Din Kültürü",
            unite = "Peygamberimizin Hayatı",
            konu = "Mekke ve Medine Dönemi",
            aciklama = "Peygamberimizin hayatının önemli olaylarını açıklar.",
            ornekler = listOf("Cahiliye dönemi", "Hicret", "Mekke'nin fethi"),
            keywords = listOf("Hz. Muhammed", "Mekke", "Medine", "hicret", "siyer")
        ),
        RagKazanim(
            kod = "DK.6.5.1",
            ders = "Din Kültürü",
            unite = "Temel Değerler",
            konu = "Bayramlar ve Milli Değerler",
            aciklama = "Dini bayramları, kandilleri ve vatan sevgisini açıklar.",
            ornekler = listOf("Ramazan Bayramı", "Kurban Bayramı", "Kandil geceleri"),
            keywords = listOf("bayram", "kandil", "vatan", "millet", "değer")
        )
    )

    // Tüm kazanımları birleştir
    fun tumKazanimlar(): List<RagKazanim> {
        return turkce + matematik + fenBilimleri + sosyalBilgiler + ingilizce + dinKulturu
    }
}
