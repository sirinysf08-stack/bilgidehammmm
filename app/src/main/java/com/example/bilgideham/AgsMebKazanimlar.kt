package com.example.bilgideham

object AgsMebKazanimlar {

    val sozelYetenek = listOf(
        RagKazanim(
            kod = "AGS.MEB.SOZ.1.1",
            ders = "Sözel Yetenek",
            unite = "Dil ve Anlam",
            konu = "Sözcükte anlam ve kavram ilişkileri",
            aciklama = "Sözcüklerin temel/yan anlamlarını ve kavramlar arası ilişkileri (eş/zıt/benzer) ayırt eder.",
            keywords = listOf("sözcükte anlam", "eş anlam", "zıt anlam", "kavram")
        ),
        RagKazanim(
            kod = "AGS.MEB.SOZ.1.2",
            ders = "Sözel Yetenek",
            unite = "Dil ve Anlam",
            konu = "Cümlede anlam",
            aciklama = "Cümlede anlatımın amacını, duygu ve düşünceyi; kesinlik-olasılık ve koşul ilişkilerini yorumlar.",
            keywords = listOf("cümlede anlam", "kesinlik", "olasılık", "koşul")
        ),
        RagKazanim(
            kod = "AGS.MEB.SOZ.1.3",
            ders = "Sözel Yetenek",
            unite = "Dil ve Anlam",
            konu = "Anlatım bozukluğu",
            aciklama = "Anlatım bozukluğu türlerini (anlam, yapı, gereksiz sözcük, çelişki) tespit eder.",
            keywords = listOf("anlatım bozukluğu", "çelişki", "gereksiz sözcük")
        )
    )

    val paragraf = listOf(
        RagKazanim(
            kod = "AGS.MEB.PRG.1.1",
            ders = "Türkçe",
            unite = "Paragraf",
            konu = "Ana düşünce ve yardımcı düşünce",
            aciklama = "Paragrafta ana düşünceyi, yardımcı düşünceleri ve düşünceyi geliştirme yollarını belirler.",
            keywords = listOf("ana düşünce", "yardımcı düşünce", "düşünceyi geliştirme")
        ),
        RagKazanim(
            kod = "AGS.MEB.PRG.1.2",
            ders = "Türkçe",
            unite = "Paragraf",
            konu = "Paragrafın yapısı",
            aciklama = "Paragrafın giriş-gelişme-sonuç bölümlerini ve anlatım akışını değerlendirir.",
            keywords = listOf("giriş", "gelişme", "sonuç", "akış")
        ),
        RagKazanim(
            kod = "AGS.MEB.PRG.1.3",
            ders = "Türkçe",
            unite = "Paragraf",
            konu = "Paragrafta anlam ilişkileri",
            aciklama = "Paragrafta neden-sonuç, amaç-sonuç, koşul, karşılaştırma ve örneklendirme ilişkilerini yorumlar.",
            keywords = listOf("neden-sonuç", "amaç-sonuç", "karşılaştırma", "örnek")
        )
    )

    val sayisalYetenek = listOf(
        RagKazanim(
            kod = "AGS.MEB.SAY.1.1",
            ders = "Sayısal Yetenek",
            unite = "Temel Matematik",
            konu = "Sayılar ve işlemler",
            aciklama = "Temel sayı kümeleri ve işlemlerle ilgili problem çözme stratejilerini uygular.",
            keywords = listOf("sayılar", "işlemler", "problem")
        ),
        RagKazanim(
            kod = "AGS.MEB.SAY.1.2",
            ders = "Sayısal Yetenek",
            unite = "Temel Matematik",
            konu = "Oran-orantı ve yüzde",
            aciklama = "Oran-orantı, yüzde ve kar-zarar ilişkili problemleri çözer.",
            keywords = listOf("oran", "orantı", "yüzde", "kar-zarar")
        ),
        RagKazanim(
            kod = "AGS.MEB.SAY.1.3",
            ders = "Sayısal Yetenek",
            unite = "Mantık ve Akıl Yürütme",
            konu = "Tablo-şekil yorumlama",
            aciklama = "Tablo, grafik ve şekil verilerini okuyarak uygun sonuç çıkarır.",
            keywords = listOf("tablo", "grafik", "yorumlama")
        )
    )

    val tarih = listOf(
        RagKazanim(
            kod = "AGS.MEB.TAR.1.1",
            ders = "Tarih",
            unite = "Atatürk İlkeleri ve İnkılap Tarihi",
            konu = "Kurtuluş Savaşı ve inkılaplar",
            aciklama = "Kurtuluş Savaşı süreci ve inkılapların amaçlarını neden-sonuç ilişkisiyle açıklar.",
            keywords = listOf("kurtuluş", "inkılap", "neden-sonuç")
        ),
        RagKazanim(
            kod = "AGS.MEB.TAR.1.2",
            ders = "Tarih",
            unite = "Atatürk İlkeleri ve İnkılap Tarihi",
            konu = "Atatürk ilkeleri",
            aciklama = "Atatürk ilkelerini ve bu ilkelerin toplumsal-siyasal yansımalarını örneklerle yorumlar.",
            keywords = listOf("cumhuriyetçilik", "milliyetçilik", "laiklik", "devrimcilik")
        )
    )

    val turkiyeCografyasi = listOf(
        RagKazanim(
            kod = "AGS.MEB.COG.1.1",
            ders = "Türkiye Coğrafyası",
            unite = "Fiziki Coğrafya",
            konu = "Yer şekilleri ve iklim",
            aciklama = "Türkiye'nin yer şekilleri ve iklim özelliklerinin beşeri faaliyetlere etkisini değerlendirir.",
            keywords = listOf("iklim", "yer şekilleri", "beşeri faaliyet")
        ),
        RagKazanim(
            kod = "AGS.MEB.COG.1.2",
            ders = "Türkiye Coğrafyası",
            unite = "Beşeri ve Ekonomik Coğrafya",
            konu = "Nüfus ve yerleşme",
            aciklama = "Türkiye'de nüfusun dağılışı, göç ve yerleşme özelliklerini temel kavramlarla yorumlar.",
            keywords = listOf("nüfus", "göç", "yerleşme")
        )
    )

    val egitiminTemelleri = listOf(
        RagKazanim(
            kod = "AGS.MEB.EGT.1.1",
            ders = "Eğitimin Temelleri",
            unite = "Eğitim Bilimine Giriş",
            konu = "Eğitimin işlevleri",
            aciklama = "Eğitimin bireysel ve toplumsal işlevlerini, eğitim-kültür ilişkisini açıklar.",
            keywords = listOf("eğitimin işlevleri", "kültür", "toplumsallaşma")
        ),
        RagKazanim(
            kod = "AGS.MEB.EGT.1.2",
            ders = "Eğitimin Temelleri",
            unite = "Türk Millî Eğitim Sistemi",
            konu = "MEB teşkilatı ve temel ilkeler",
            aciklama = "Türk Millî Eğitim Sistemi'nin temel ilkelerini ve MEB teşkilat yapısının ana hatlarını açıklar.",
            keywords = listOf("TMES", "MEB", "temel ilkeler")
        )
    )

    val mevzuat = listOf(
        RagKazanim(
            kod = "AGS.MEB.MVZ.1.1",
            ders = "Mevzuat",
            unite = "657 ve İdare Hukuku",
            konu = "657 sayılı DMK temel hükümler",
            aciklama = "657 sayılı DMK'da memuriyet, ödev-sorumluluklar ve disiplin hükümlerinin temel çerçevesini açıklar.",
            keywords = listOf("657", "DMK", "disiplin", "ödev")
        ),
        RagKazanim(
            kod = "AGS.MEB.MVZ.1.2",
            ders = "Mevzuat",
            unite = "657 ve İdare Hukuku",
            konu = "İdari işlemler ve yargı",
            aciklama = "İdari işlemin unsurlarını, idari yargı yolunu ve temel kavramları ayırt eder.",
            keywords = listOf("idari işlem", "yetki", "şekil", "sebep", "konu", "amaç")
        )
    )

    fun tumKazanimlar(): List<RagKazanim> {
        return sozelYetenek + paragraf + sayisalYetenek + tarih + turkiyeCografyasi + egitiminTemelleri + mevzuat
    }
}
