package com.example.bilgideham

/**
 * 3. Sınıf (İlkokul) - TYMM uyumlu kazanım veritabanı (RAG)
 * Matematik, Fen Bilimleri, Hayat Bilgisi, Türkçe, İngilizce
 *
 * Not: Kodlar derslere göre resmî kaynaklardaki biçimle korunmuştur:
 *  - Matematik: MAT.3.x.y
 *  - Fen: FB.3.x.y
 *  - Hayat Bilgisi: HB.3.x.y
 *  - Türkçe: T.3.x.y
 *
 * RagKazanim data class'ının senin projendeki haliyle uyumlu olacak şekilde kullanılmıştır.
 */
object Ilkokul3Kazanimlari {

    // ==================== MATEMATİK (TYMM) ====================
    val matematik = listOf(
        // TEMA 1: SAYILAR VE NİCELİKLER (1)
        RagKazanim("MAT.3.1.1", "Matematik", "Sayılar ve Nicelikler (1)", "Doğal Sayılar",
            "Doğal sayıları çözümleyebilme.",
            listOf("doğal sayı", "basamak", "çözümleme", "sayı değeri", "basamak değeri")),
        RagKazanim("MAT.3.1.2", "Matematik", "Sayılar ve Nicelikler (1)", "Karşılaştırma-Sıralama",
            "Doğal sayıları karşılaştırabilme ve sıralayabilme.",
            listOf("karşılaştırma", "sıralama", "büyüktür", "küçüktür")),
        RagKazanim("MAT.3.1.3", "Matematik", "Sayılar ve Nicelikler (1)", "Sayı Örüntüleri",
            "Sayı örüntülerini bulabilme ve oluşturabilme.",
            listOf("örüntü", "kural", "dizi", "artış", "azalış")),
        RagKazanim("MAT.3.1.4", "Matematik", "Sayılar ve Nicelikler (1)", "Yuvarlama-Tahmin",
            "Doğal sayıları en yakın onluğa/yüzlüğe yuvarlayarak tahmin yapabilme.",
            listOf("yuvarlama", "tahmin", "onluk", "yüzlük")),
        RagKazanim("MAT.3.1.5", "Matematik", "Sayılar ve Nicelikler (1)", "Ritmik Sayma",
            "Ritmik sayma yapabilme (ileri/geri).",
            listOf("ritmik sayma", "ileri sayma", "geri sayma", "yüzer", "oner")),
        RagKazanim("MAT.3.1.6", "Matematik", "Sayılar ve Nicelikler (1)", "Kesir - Birim Kesir",
            "Bir bütünü eş parçalara ayırarak birim kesirleri yorumlayabilme.",
            listOf("kesir", "birim kesir", "bütün", "eş parça", "pay", "payda")),
        RagKazanim("MAT.3.1.7", "Matematik", "Sayılar ve Nicelikler (1)", "Kesir - Modelleme",
            "Kesirleri modellerle gösterebilme.",
            listOf("kesir", "model", "pay", "payda")),
        RagKazanim("MAT.3.1.8", "Matematik", "Sayılar ve Nicelikler (1)", "Kesir - Karşılaştırma",
            "Basit kesirleri karşılaştırabilme.",
            listOf("kesir karşılaştırma", "büyüklük", "sıralama")),

        // TEMA 2: SAYILAR VE NİCELİKLER (2)
        RagKazanim("MAT.3.1.9", "Matematik", "Sayılar ve Nicelikler (2)", "Bütün-Yarım-Çeyrek",
            "Bütün, yarım ve çeyreği kesirle gösterimi için modellerden yararlanabilme.",
            listOf("bütün", "yarım", "çeyrek", "kesir", "model")),
        RagKazanim("MAT.3.1.10", "Matematik", "Sayılar ve Nicelikler (2)", "Birim Kesirle Çözümleme",
            "Bir bütünü eş parçalar oluşturacak şekilde birim kesir olarak çözümleyebilme.",
            listOf("birim kesir", "eş parça", "bütün", "çözümleme")),
        RagKazanim("MAT.3.1.11", "Matematik", "Sayılar ve Nicelikler (2)", "Pay-Payda İlişkisi",
            "Bir kesrin payı ile paydası arasındaki ilişkiyi çözümleyebilme.",
            listOf("pay", "payda", "kesir", "ilişki")),
        RagKazanim("MAT.3.1.12", "Matematik", "Sayılar ve Nicelikler (2)", "Zaman Okuma",
            "Analog ve dijital saatlerde zamanı okuyabilme ve yazabilme.",
            listOf("saat", "analog", "dijital", "zaman okuma")),
        RagKazanim("MAT.3.1.13", "Matematik", "Sayılar ve Nicelikler (2)", "Zaman Birimleri",
            "Zaman ölçü birimlerini çözümleyebilme.",
            listOf("saniye", "dakika", "saat", "gün", "hafta", "ay", "yıl")),
        RagKazanim("MAT.3.1.14", "Matematik", "Sayılar ve Nicelikler (2)", "Süre Tahmini",
            "Olayların oluş sürelerini tahmin ederek yargıda bulunabilme.",
            listOf("süre", "tahmin", "zaman", "deneyim")),
        RagKazanim("MAT.3.1.15", "Matematik", "Sayılar ve Nicelikler (2)", "Uzunluk-Kütle İlişkileri",
            "Uzunluk ve kütle birimleri arasındaki ilişkileri kullanarak bu birimleri çözümleyebilme.",
            listOf("santimetre", "metre", "kilometre", "gram", "kilogram", "ton")),
        RagKazanim("MAT.3.1.16", "Matematik", "Sayılar ve Nicelikler (2)", "Paralarımız",
            "Madenî ve kâğıt paraları değerlerine göre ilişkilendirerek yorumlayabilme.",
            listOf("para", "madenî", "kâğıt", "TL", "dönüştürme")),

        // TEMA 3: İŞLEMLERDEN CEBİRSEL DÜŞÜNMEYE
        RagKazanim("MAT.3.2.1", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Toplama-Çıkarma Tahmin/Zihinden",
            "Toplama ve çıkarma işlemlerinin sonuçlarını tahmin ederek ve zihinden işlem yaparak muhakeme edebilme.",
            listOf("toplama", "çıkarma", "tahmin", "zihinden", "muhakeme")),
        RagKazanim("MAT.3.2.2", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Toplama-Çıkarma",
            "Toplama ve çıkarma işlemlerini çözümleyebilme.",
            listOf("toplama", "çıkarma", "işlem adımları", "strateji")),
        RagKazanim("MAT.3.2.3", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Çarpma-Bölme Muhakeme",
            "Çarpma ve bölme işlemlerinin sonuçlarını muhakeme edebilme.",
            listOf("çarpma", "bölme", "tahmin", "zihinden", "muhakeme")),
        RagKazanim("MAT.3.2.4", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Çarpma-Bölme",
            "Çarpma ve bölme işlemlerini çözümleyebilme.",
            listOf("çarpma", "bölme", "işlem adımları", "ilişkilendirme")),
        RagKazanim("MAT.3.2.5", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Yönerge ile Dört İşlem",
            "Dört işlem gerektiren durumlarda yönergeleri takip ederek yorumlayabilme.",
            listOf("dört işlem", "yönerge", "temsil", "problem")),

        RagKazanim("MAT.3.2.6", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Problem Çözme",
            "Dört işlem gerektiren günlük yaşam problemlerini çözebilme.",
            listOf("problem", "strateji", "tahmin", "kontrol", "çözüm")),
        RagKazanim("MAT.3.2.7", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Problem Kurma",
            "Dört işlem gerektiren problem durumlarını yapılandırabilme.",
            listOf("problem kurma", "ilişki", "mantıksal", "nedensel")),
        RagKazanim("MAT.3.2.8", "Matematik", "İşlemlerden Cebirsel Düşünmeye", "Eşitlik",
            "Dört işlem bağlamında eşitliğin farklı anlamlarını yorumlayabilme.",
            listOf("eşitlik", "=", "dört işlem", "denk")),

        // TEMA 4: NESNELERİN GEOMETRİSİ (1)
        RagKazanim("MAT.3.3.1", "Matematik", "Nesnelerin Geometrisi (1)", "Geometrik Cisimler",
            "Geometrik cisimlerin özelliklerini yorumlayabilme.",
            listOf("cisim", "köşe", "yüz", "ayrıt", "özellik")),
        RagKazanim("MAT.3.3.2", "Matematik", "Nesnelerin Geometrisi (1)", "Çokgenler",
            "Kenar sayılarına göre geometrik şekilleri sınıflandırabilme.",
            listOf("çokgen", "kenar", "sınıflandırma")),
        RagKazanim("MAT.3.3.3", "Matematik", "Nesnelerin Geometrisi (1)", "Çizim",
            "Araç/teknolojilerle geometrik cisim ve şekilleri çizebilme.",
            listOf("cetvel", "çizim", "geometrik şekil", "teknoloji")),
        RagKazanim("MAT.3.3.4", "Matematik", "Nesnelerin Geometrisi (1)", "Çevre Tahmini",
            "Standart/standart olmayan araçlarla şekillerin çevresini tahmin edebilme.",
            listOf("çevre", "tahmin", "ölçme", "uzunluk")),
        RagKazanim("MAT.3.3.5", "Matematik", "Nesnelerin Geometrisi (1)", "Sıvı Ölçme",
            "Standart sıvı ölçü birimleriyle sıvı miktarını tahmin edebilme.",
            listOf("litre", "mililitre", "sıvı ölçme", "tahmin")),

        // TEMA 5: NESNELERİN GEOMETRİSİ (2)
        RagKazanim("MAT.3.3.6", "Matematik", "Nesnelerin Geometrisi (2)", "Simetri",
            "Birden fazla simetri doğrusu olan şekilleri çözümleyebilme.",
            listOf("simetri", "simetri doğrusu", "kare", "dikdörtgen", "daire")),
        RagKazanim("MAT.3.3.7", "Matematik", "Nesnelerin Geometrisi (2)", "Simetrik Şekil Tamamlama",
            "Bir parçası verilen simetrik şekli simetri doğrusuna göre yapılandırabilme.",
            listOf("simetri", "tamamlama", "yatay", "dikey")),
        RagKazanim("MAT.3.3.8", "Matematik", "Nesnelerin Geometrisi (2)", "Kodlama ile Simetri",
            "Kodlama stratejileriyle simetri oluşturmaya ilişkin yargıda bulunabilme.",
            listOf("kodlama", "simetri", "yönerge", "strateji")),

        // TEMA 6: VERİYE DAYALI ARAŞTIRMA
        RagKazanim("MAT.3.4.1", "Matematik", "Veriye Dayalı Araştırma", "Veri Toplama-Yorumlama",
            "Kategorik ve nicel verilerle çalışabilme ve veriye dayalı karar verebilme.",
            listOf("veri", "kategorik", "nicel", "araştırma sorusu", "tablo"))
    )


    // ==================== FEN BİLİMLERİ (TYMM) ====================
    val fenBilimleri = listOf(
        RagKazanim("FB.3.1.1", "Fen Bilimleri", "Bilimsel Keşif Yolculuğu", "Bilimsel Bilgi",
            "Bilimsel bilgiye ulaşma yollarını sorgulayabilme.",
            listOf("bilimsel bilgi", "sorgulama", "gözlem", "deney", "veri")),
        RagKazanim("FB.3.1.2", "Fen Bilimleri", "Bilimsel Keşif Yolculuğu", "Bilim İnsanı",
            "Bilim insanlarının özelliklerine ilişkin genelleme yapabilme.",
            listOf("bilim insanı", "özellik", "genelleme")),
        RagKazanim("FB.3.2.1", "Fen Bilimleri", "Canlılar Dünyasına Yolculuk", "Sınıflandırma",
            "Canlıları mikroskopla görülebilenler, mantarlar, bitkiler ve hayvanlar olarak sınıflandırabilme.",
            listOf("canlı", "bitki", "hayvan", "mantar", "sınıflandırma")),
        RagKazanim("FB.3.2.2", "Fen Bilimleri", "Canlılar Dünyasına Yolculuk", "Duyular",
            "Canlıların çevrelerini farklı yollarla algılamaları konusunda bilimsel çıkarım yapabilme.",
            listOf("duyu", "algı", "çıkarım", "veri")),
        RagKazanim("FB.3.2.3", "Fen Bilimleri", "Canlılar Dünyasına Yolculuk", "Yaşam Döngüsü",
            "Canlıların yaşam döngülerini açıklamada tümevarımsal akıl yürütebilme.",
            listOf("yaşam döngüsü", "örüntü", "genelleme", "tümevarım")),
        RagKazanim("FB.3.3.1", "Fen Bilimleri", "Yer Bilimciler İş Başında", "Kayaç-Maden-Mineral",
            "Kayaçlar, madenler ve mineraller ile ilgili tümdengelimsel akıl yürütebilme.",
            listOf("kayaç", "maden", "mineral", "tümdengelim")),
        RagKazanim("FB.3.3.2", "Fen Bilimleri", "Yer Bilimciler İş Başında", "Fosil",
            "Fosil oluşumu ile ilgili sentez yapabilme.",
            listOf("fosil", "oluşum", "süreç", "sentez")),
        RagKazanim("FB.3.4.1", "Fen Bilimleri", "Maddeyi Tanıyalım, Karıştırıp Ayıralım", "Madde Halleri",
            "Çevresindeki maddeleri hâllerine göre sınıflandırabilme.",
            listOf("madde", "katı", "sıvı", "gaz", "sınıflandırma")),
        RagKazanim("FB.3.4.2", "Fen Bilimleri", "Maddeyi Tanıyalım, Karıştırıp Ayıralım", "Karışımlar",
            "Karışımların ayrılmasında uygun yöntemlerle deney yapabilme.",
            listOf("karışım", "eleme", "süzme", "mıknatıs", "deney")),
        RagKazanim("FB.3.4.3", "Fen Bilimleri", "Maddeyi Tanıyalım, Karıştırıp Ayıralım", "Atıklar",
            "Atıkların ayrıştırılmasına ilişkin problem çözebilme.",
            listOf("atık", "ayrıştırma", "geri dönüşüm", "problem")),
        RagKazanim("FB.3.5.1", "Fen Bilimleri", "Hareketi Keşfediyorum", "Hareket",
            "Varlıkların hareket durumlarını gözleme dayalı tahmin edebilme.",
            listOf("hareket", "gözlem", "tahmin")),
        RagKazanim("FB.3.5.2", "Fen Bilimleri", "Hareketi Keşfediyorum", "Kuvvet",
            "Kuvvetin varlıklar üzerindeki etkilerini gözleme dayalı tahmin edebilme.",
            listOf("kuvvet", "itme", "çekme", "etki", "gözlem")),
        RagKazanim("FB.3.6.1", "Fen Bilimleri", "Yaşamımızı Kolaylaştıran Elektrik", "Elektrikli Araçlar",
            "Bazı araç gereçlerin elektrikli olduğuna ilişkin bilimsel çıkarım yapabilme.",
            listOf("elektrik", "araç gereç", "çıkarım")),
        RagKazanim("FB.3.6.2", "Fen Bilimleri", "Yaşamımızı Kolaylaştıran Elektrik", "Güvenlik",
            "Elektrikli araç gereçlerin güvenli kullanımı ile ilgili eleştirel düşünebilme.",
            listOf("güvenlik", "elektrik", "eleştirel düşünme")),
        RagKazanim("FB.3.6.3", "Fen Bilimleri", "Yaşamımızı Kolaylaştıran Elektrik", "Tasarruf",
            "Elektriği tasarruflu kullanma konusunda veriye dayalı tahmin edebilme.",
            listOf("tasarruf", "ölçüm", "veri", "tahmin")),

        RagKazanim("FB.3.7.1", "Fen Bilimleri", "Toprağı Tanıyorum, Tarımı Keşfediyorum", "Toprak",
            "Toprak oluşumuna ve yapısına ilişkin bilimsel gözlem yapabilme.",
            listOf("toprak", "oluşum", "yapı", "gözlem")),
        RagKazanim("FB.3.7.2", "Fen Bilimleri", "Toprağı Tanıyorum, Tarımı Keşfediyorum", "Bitki Yetiştirme",
            "Bir bitkinin yetişmesi için gerekenlere ilişkin genelleme yapabilme.",
            listOf("bitki", "yetiştirme", "ihtiyaç", "genelleme")),
        RagKazanim("FB.3.8.1", "Fen Bilimleri", "Canlıların Yaşam Alanlarına Yolculuk", "Yaşam Alanı",
            "Canlıların yaşam alanlarının özelliklerini belirlemede kanıt kullanabilme.",
            listOf("yaşam alanı", "kanıt", "veri", "açıklama")),
        RagKazanim("FB.3.8.2", "Fen Bilimleri", "Canlıların Yaşam Alanlarına Yolculuk", "Canlı Çeşitliliği",
            "Yaşam alanındaki canlı çeşitliliğini operasyonel olarak tanımlayabilme.",
            listOf("çeşitlilik", "ölçüm", "tanım", "canlı")),
        RagKazanim("FB.3.8.3", "Fen Bilimleri", "Canlıların Yaşam Alanlarına Yolculuk", "Koruma",
            "Yaşam alanlarının korunması için yapılacakları sorgulayabilme.",
            listOf("koruma", "sorgulama", "çevre", "doğa"))
    )

    // ==================== HAYAT BİLGİSİ (TYMM) ====================
    val hayatBilgisi = listOf(
        RagKazanim("HB.3.1.1", "Hayat Bilgisi", "Ben ve Okulum", "Kişisel Gelişim",
            "Kendini geliştirmek istediği alana ilişkin plan yapabilme.",
            listOf("hedef", "plan", "kişisel gelişim")),
        RagKazanim("HB.3.1.2", "Hayat Bilgisi", "Ben ve Okulum", "Hak-Sorumluluk",
            "Okuldaki hak ve sorumluluklarına uygun davranabilme.",
            listOf("hak", "sorumluluk", "kural")),
        RagKazanim("HB.3.1.3", "Hayat Bilgisi", "Ben ve Okulum", "Çocuk Hakları",
            "Çocuk haklarını tanıtmak için fikirlerini eyleme dönüştürebilme.",
            listOf("çocuk hakları", "farkındalık", "proje")),
        RagKazanim("HB.3.2.1", "Hayat Bilgisi", "Sağlığım ve Güvenliğim", "Sağlık",
            "Sağlığını korumaya yönelik davranışlarını düzenleyebilme.",
            listOf("sağlık", "alışkanlık", "temizlik", "beslenme")),
        RagKazanim("HB.3.2.2", "Hayat Bilgisi", "Sağlığım ve Güvenliğim", "Güvenlik",
            "Güvenliğini tehdit eden durumlarda yapılması gerekenleri sorgulayabilme.",
            listOf("güvenlik", "tehdit", "acil durum", "önlem")),
        RagKazanim("HB.3.2.3", "Hayat Bilgisi", "Sağlığım ve Güvenliğim", "Trafik",
            "Trafik kurallarına uymanın önemine ilişkin özgün ürün oluşturabilme.",
            listOf("trafik", "kural", "güvenlik", "ürün")),
        RagKazanim("HB.3.3.1", "Hayat Bilgisi", "Ailem ve Toplum", "Aile-Toplum",
            "Aile ve toplum arasındaki ilişkiyi çözümleyebilme.",
            listOf("aile", "toplum", "ilişki")),
        RagKazanim("HB.3.3.2", "Hayat Bilgisi", "Ailem ve Toplum", "Yardımlaşma",
            "Yardıma ihtiyacı olanların yaşamını kolaylaştırmak için fikirlerini eyleme dönüştürebilme.",
            listOf("yardımlaşma", "empati", "proje")),
        RagKazanim("HB.3.3.3", "Hayat Bilgisi", "Ailem ve Toplum", "Meslekler",
            "Mesleklerin toplumsal yaşamdaki önemini yorumlayabilme.",
            listOf("meslek", "toplum", "önem")),

        RagKazanim("HB.3.4.1", "Hayat Bilgisi", "Yaşadığım Yer ve Ülkem", "Koruma",
            "Yakın çevresindeki tarihî mekân ve doğal güzelliklerin korunmasının önemini fark edebilme.",
            listOf("tarihî mekân", "doğal güzellik", "koruma")),
        RagKazanim("HB.3.4.2", "Hayat Bilgisi", "Yaşadığım Yer ve Ülkem", "Cumhuriyet",
            "Ülkemizin yönetim şekli ile ilgili kaynaklardan bilgi toplayabilme.",
            listOf("cumhuriyet", "yönetim", "kaynak", "bilgi toplama")),
        RagKazanim("HB.3.4.3", "Hayat Bilgisi", "Yaşadığım Yer ve Ülkem", "Atatürk",
            "Mustafa Kemal Atatürk'ün kişilik özelliklerini çözümleyebilme.",
            listOf("Atatürk", "kişilik", "başarı", "ilişki")),
        RagKazanim("HB.3.4.4", "Hayat Bilgisi", "Yaşadığım Yer ve Ülkem", "Millî Birlik",
            "Millî birlik ve beraberliğin toplum hayatına katkılarını açıklayabilme.",
            listOf("birlik", "beraberlik", "toplum")),
        RagKazanim("HB.3.5.1", "Hayat Bilgisi", "Doğa ve Çevre", "Doğadaki Varlıklar",
            "Doğadaki varlıkların insan yaşamı için önemini yorumlayabilme.",
            listOf("doğa", "varlık", "insan yaşamı")),
        RagKazanim("HB.3.5.2", "Hayat Bilgisi", "Doğa ve Çevre", "Kroki",
            "Krokiyi kullanarak bulunduğu yerin konumunu algılayabilme.",
            listOf("kroki", "konum", "yön")),
        RagKazanim("HB.3.5.3", "Hayat Bilgisi", "Doğa ve Çevre", "Afet",
            "Afetlere yönelik yapılması gerekenleri sınıflandırabilme.",
            listOf("afet", "öncesi", "sırası", "sonrası")),
        RagKazanim("HB.3.5.4", "Hayat Bilgisi", "Doğa ve Çevre", "Sürdürülebilirlik",
            "Çevresel sürdürülebilirliğe yönelik kaynaklardan bilgi toplayabilme.",
            listOf("sürdürülebilirlik", "kaynak", "çevre")),
        RagKazanim("HB.3.6.1", "Hayat Bilgisi", "Bilim, Teknoloji ve Sanat", "Bilim",
            "Bilimsel gelişmelerin günlük yaşama etkisini yorumlayabilme.",
            listOf("bilim", "gelişme", "günlük yaşam")),
        RagKazanim("HB.3.6.2", "Hayat Bilgisi", "Bilim, Teknoloji ve Sanat", "Teknoloji",
            "Teknolojik gelişmelerin günlük yaşama etkisini çözümleyebilme.",
            listOf("teknoloji", "iletişim", "ulaşım", "etki")),
        RagKazanim("HB.3.6.3", "Hayat Bilgisi", "Bilim, Teknoloji ve Sanat", "Sanatçı",
            "Sanatçıların sanata katkılarına yönelik kaynaklardan bilgi toplayabilme.",
            listOf("sanat", "sanatçı", "katkı", "kaynak"))
    )

    // ==================== TÜRKÇE (T.3.* kazanımları) ====================
    val turkce = listOf(
        // Dinleme/İzleme
        RagKazanim("T.3.1.1", "Türkçe", "Dinleme/İzleme", "Konu Tahmini",
            "Görsellerden hareketle dinleyeceği/izleyeceği metnin konusunu tahmin eder.",
            listOf("dinleme", "tahmin", "görsel", "konu")),
        RagKazanim("T.3.1.6", "Türkçe", "Dinleme/İzleme", "Ana Fikir/Ana Duygu",
            "Dinlediklerinin/izlediklerinin ana fikrini/ana duygusunu belirler.",
            listOf("ana fikir", "ana duygu", "dinleme", "anlama")),
        RagKazanim("T.3.1.3", "Türkçe", "Dinleme/İzleme", "Özetleme",
            "Dinlediği/izlediği metni ana hatlarıyla anlatır.",
            listOf("özet", "ana hat", "dinleme")),

        // Okuma - Anlama
        RagKazanim("T.3.3.14", "Türkçe", "Okuma", "Konu",
            "Okuduğu metnin konusunu belirler.",
            listOf("okuma", "konu", "metin")),
        RagKazanim("T.3.3.15", "Türkçe", "Okuma", "Ana Fikir/Ana Duygu",
            "Metnin ana fikrini/ana duygusunu belirler.",
            listOf("ana fikir", "ana duygu", "okuma", "metin")),
        RagKazanim("T.3.3.20", "Türkçe", "Okuma", "Metin Türleri",
            "Metin türlerini ayırt eder (hikâye edici, bilgilendirici, şiir).",
            listOf("metin türü", "hikâye", "bilgilendirici", "şiir")),
        RagKazanim("T.3.3.24", "Türkçe", "Okuma", "Çıkarım",
            "Okudukları ile ilgili çıkarımlar yapar.",
            listOf("çıkarım", "neden-sonuç", "yorum", "akıl yürütme")),

        // Söz Varlığı
        RagKazanim("T.3.3.8", "Türkçe", "Söz Varlığı", "Zıt Anlam",
            "Kelimelerin zıt anlamlılarını bulur.",
            listOf("zıt anlam", "sözcük", "anlam")),
        RagKazanim("T.3.3.9", "Türkçe", "Söz Varlığı", "Eş Anlam",
            "Kelimelerin eş anlamlılarını bulur.",
            listOf("eş anlam", "sözcük", "anlam")),
        RagKazanim("T.3.3.10", "Türkçe", "Söz Varlığı", "Eş Sesli",
            "Eş sesli kelimelerin anlamlarını ayırt eder.",
            listOf("eş sesli", "sözcük", "anlam")),

        // Yazma
        RagKazanim("T.3.4.2", "Türkçe", "Yazma", "Kısa Metin",
            "Kısa metinler yazar.",
            listOf("yazma", "kısa metin", "planlama")),
        RagKazanim("T.3.4.7", "Türkçe", "Yazma", "İmla ve Noktalama",
            "Büyük harfleri ve noktalama işaretlerini uygun yerlerde kullanır.",
            listOf("büyük harf", "noktalama", "nokta", "virgül", "soru işareti"))
    )

    // ==================== İNGİLİZCE (3. sınıf ünite bazlı) ====================
    val ingilizce = listOf(
        RagKazanim("E3.U1", "İngilizce", "Unit 1", "Greeting",
            "Basic greetings, alphabet, numbers 1-20; simple self-introduction.",
            listOf("greetings", "alphabet", "numbers 1-20", "introduce yourself")),
        RagKazanim("E3.U2", "İngilizce", "Unit 2", "My Family",
            "Kinship terms; ask and talk about family members.",
            listOf("family", "kinship", "mother", "father", "sister", "brother")),
        RagKazanim("E3.U3", "İngilizce", "Unit 3", "People I Love",
            "Physical qualities and abilities; short instructions.",
            listOf("people", "appearance", "abilities", "can/can't")),
        RagKazanim("E3.U4", "İngilizce", "Unit 4", "Feelings",
            "Emotions/feelings; simple suggestions.",
            listOf("feelings", "emotions", "suggestions")),
        RagKazanim("E3.U5", "İngilizce", "Unit 5", "Toys and Games",
            "Toys; colors and quantity; possessions in short dialogues.",
            listOf("toys", "games", "colors", "how many")),
        RagKazanim("E3.U6", "İngilizce", "Unit 6", "My House",
            "Parts of a house; shapes; locations and possessions.",
            listOf("house", "rooms", "shapes", "there is/are", "location")),
        RagKazanim("E3.U7", "İngilizce", "Unit 7", "In My City",
            "Buildings/places on a city map; where people are; apologies.",
            listOf("city", "buildings", "map", "where", "apologies")),
        RagKazanim("E3.U8", "İngilizce", "Unit 8", "Transportation",
            "Vehicles; short oral texts; talk about using transportation.",
            listOf("transportation", "vehicles", "bus", "car", "train")),
        RagKazanim("E3.U9", "İngilizce", "Unit 9", "Weather",
            "Identify and talk about weather conditions.",
            listOf("weather", "sunny", "rainy", "cloudy")),
        RagKazanim("E3.U10", "İngilizce", "Unit 10", "Nature",
            "Nature and animals; likes/dislikes; simple instructions.",
            listOf("nature", "animals", "like/dislike"))
    )

    /**
     * Tüm 3. sınıf kazanımlarını döndür
     */
    fun tumKazanimlar(): List<RagKazanim> {
        return matematik + fenBilimleri + hayatBilgisi + turkce + ingilizce
    }

    /**
     * Ders adına göre kazanımları getir
     */
    fun getByDers(ders: String): List<RagKazanim> {
        return tumKazanimlar().filter {
            it.ders.equals(ders, ignoreCase = true) ||
            it.ders.contains(ders, ignoreCase = true) ||
            ders.contains(it.ders, ignoreCase = true)
        }
    }
}
