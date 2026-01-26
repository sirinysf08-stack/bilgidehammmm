package com.example.bilgideham

object AgsOabtEksikKazanimlar {

    val sosyal_tarih = listOf(
        RagKazanim(
            kod = "AGS.SOS.1.1",
            ders = "Sosyal Bilgiler",
            unite = "Tarih",
            konu = "Tarihsel düşünme ve yorum",
            aciklama = "Tarihsel olayları neden-sonuç ilişkisi ve süreklilik-değişim boyutlarıyla yorumlar.",
            keywords = listOf("neden-sonuç", "süreklilik", "değişim", "tarihsel yorum")
        ),
        RagKazanim(
            kod = "AGS.SOS.1.2",
            ders = "Sosyal Bilgiler",
            unite = "Tarih",
            konu = "Kaynak kullanımı",
            aciklama = "Birincil/ikincil kaynak ayrımını yapar; kanıt temelli çıkarım üretir.",
            keywords = listOf("birincil kaynak", "ikincil kaynak", "kanıt", "belge")
        )
    )

    val sosyal_cografya = listOf(
        RagKazanim(
            kod = "AGS.SOS.2.1",
            ders = "Sosyal Bilgiler",
            unite = "Coğrafya",
            konu = "Mekânı algılama",
            aciklama = "Harita okuryazarlığı ve temel mekânsal kavramlarla (konum, bölge, ölçek) analiz yapar.",
            keywords = listOf("harita", "konum", "bölge", "ölçek")
        ),
        RagKazanim(
            kod = "AGS.SOS.2.2",
            ders = "Sosyal Bilgiler",
            unite = "Coğrafya",
            konu = "İnsan-çevre etkileşimi",
            aciklama = "Beşeri faaliyetlerin çevresel etkilerini ve sürdürülebilirlik yaklaşımlarını değerlendirir.",
            keywords = listOf("insan-çevre", "sürdürülebilirlik", "kaynak", "çevre")
        )
    )

    val sosyal_siyasalBilim = listOf(
        RagKazanim(
            kod = "AGS.SOS.3.1",
            ders = "Sosyal Bilgiler",
            unite = "Siyasal Bilim",
            konu = "Devlet ve yönetim",
            aciklama = "Devlet, iktidar, meşruiyet, yurttaşlık gibi temel siyaset bilimi kavramlarını açıklar.",
            keywords = listOf("devlet", "iktidar", "meşruiyet", "yurttaşlık")
        ),
        RagKazanim(
            kod = "AGS.SOS.3.2",
            ders = "Sosyal Bilgiler",
            unite = "Siyasal Bilim",
            konu = "Demokrasi ve katılım",
            aciklama = "Demokratik süreçler ve toplumsal katılım mekanizmalarını örnek durumlar üzerinden yorumlar.",
            keywords = listOf("demokrasi", "katılım", "seçim", "sivil toplum")
        )
    )

    val sosyal_sosyalBilimAlanlari = listOf(
        RagKazanim(
            kod = "AGS.SOS.4.1",
            ders = "Sosyal Bilgiler",
            unite = "Sosyal Bilim Alanları",
            konu = "Sosyal bilimlerde yöntem",
            aciklama = "Temel sosyal bilim disiplinlerini ve araştırma yaklaşımını (gözlem, görüşme, anket) tanımlar.",
            keywords = listOf("yöntem", "anket", "görüşme", "gözlem")
        ),
        RagKazanim(
            kod = "AGS.SOS.4.2",
            ders = "Sosyal Bilgiler",
            unite = "Sosyal Bilim Alanları",
            konu = "Disiplinlerarası yaklaşım",
            aciklama = "Tarih-coğrafya-ekonomi-sosyoloji ilişkisini disiplinlerarası bir çerçevede açıklar.",
            keywords = listOf("disiplinlerarası", "ekonomi", "sosyoloji", "psikoloji")
        )
    )

    val matematik_analiz = listOf(
        RagKazanim("AGS.MAT.1.1", "Matematik", "Analiz", "Limit ve süreklilik", "Limit-süreklilik kavramlarını ve fonksiyon davranışlarını yorumlar.", keywords = listOf("limit", "süreklilik", "fonksiyon")),
        RagKazanim("AGS.MAT.1.2", "Matematik", "Analiz", "Türev ve uygulamalar", "Türev kavramını, türev alma kurallarını ve uygulamalarını kullanır.", keywords = listOf("türev", "teğet", "ekstremum"))
    )

    val matematik_cebir = listOf(
        RagKazanim("AGS.MAT.2.1", "Matematik", "Cebir", "Polinom ve denklemler", "Polinomlar ve denklemlerle ilgili temel işlemleri ve çözüm yöntemlerini uygular.", keywords = listOf("polinom", "denklem", "çözüm")),
        RagKazanim("AGS.MAT.2.2", "Matematik", "Cebir", "Lineer cebir giriş", "Matris, determinant ve lineer sistem çözüm mantığını açıklar.", keywords = listOf("matris", "determinant", "lineer sistem"))
    )

    val matematik_geometri = listOf(
        RagKazanim("AGS.MAT.3.1", "Matematik", "Geometri", "Analitik geometri", "Koordinat düzleminde doğru, çember ve temel konik ilişkilerini analiz eder.", keywords = listOf("analitik", "çember", "doğru", "konik")),
        RagKazanim("AGS.MAT.3.2", "Matematik", "Geometri", "İspat ve teorem", "Geometride temel ispat yaklaşımlarıyla teoremleri gerekçelendirir.", keywords = listOf("ispat", "teorem", "aksiyom"))
    )

    val matematik_uygulamali = listOf(
        RagKazanim("AGS.MAT.4.1", "Matematik", "Uygulamalı Matematik", "Olasılık-İstatistik", "Olasılık ve temel istatistik ölçülerini kullanarak veri yorumlar.", keywords = listOf("olasılık", "istatistik", "varyans")),
        RagKazanim("AGS.MAT.4.2", "Matematik", "Uygulamalı Matematik", "Modelleme", "Problemleri matematiksel modele dönüştürür ve modelin sınırlılıklarını tartışır.", keywords = listOf("modelleme", "varsayım", "optimizasyon"))
    )

    val fizik_mekanik = listOf(
        RagKazanim("AGS.FZK.1.1", "Fizik", "Mekanik", "Kuvvet-hareket", "Newton yasaları ve hareket türlerini temel düzeyde açıklar.", keywords = listOf("newton", "kuvvet", "ivme")),
        RagKazanim("AGS.FZK.1.2", "Fizik", "Mekanik", "Enerji-momentum", "İş-enerji ve momentum korunumu ile problemleri yorumlar.", keywords = listOf("enerji", "momentum", "korunum"))
    )

    val fizik_elektrikManyetizma = listOf(
        RagKazanim("AGS.FZK.2.1", "Fizik", "Elektrik ve Manyetizma", "Elektrik devreleri", "Temel devre büyüklüklerini (V, I, R) ve Ohm yasasını kullanır.", keywords = listOf("ohm", "devre", "akım", "gerilim")),
        RagKazanim("AGS.FZK.2.2", "Fizik", "Elektrik ve Manyetizma", "Manyetik alan", "Manyetik alan ve elektromanyetik etkileşimleri kavramsal düzeyde açıklar.", keywords = listOf("manyetik alan", "indüksiyon", "elektromanyetizma"))
    )

    val fizik_maddeninMekanikIsil = listOf(
        RagKazanim("AGS.FZK.3.1", "Fizik", "Maddenin Mekanik ve Isıl Özellikleri", "Basınç ve kaldırma", "Akışkanlarda basınç ve kaldırma kuvveti ilişkilerini yorumlar.", keywords = listOf("basınç", "kaldırma", "akışkan")),
        RagKazanim("AGS.FZK.3.2", "Fizik", "Maddenin Mekanik ve Isıl Özellikleri", "Isı ve sıcaklık", "Isı-sıcaklık ilişkisini ve hal değişimlerini açıklar.", keywords = listOf("ısı", "sıcaklık", "hal değişimi"))
    )

    val fizik_dalgalarOptik = listOf(
        RagKazanim("AGS.FZK.4.1", "Fizik", "Dalgalar ve Optik", "Dalga özellikleri", "Dalga türlerini ve temel dalga büyüklüklerini açıklar.", keywords = listOf("dalga", "frekans", "genlik")),
        RagKazanim("AGS.FZK.4.2", "Fizik", "Dalgalar ve Optik", "Optik", "Yansıma-kırılma ve mercek sistemlerini temel düzeyde yorumlar.", keywords = listOf("yansıma", "kırılma", "mercek"))
    )

    val fizik_modernFizik = listOf(
        RagKazanim("AGS.FZK.5.1", "Fizik", "Modern Fizik", "Kuantum giriş", "Kuantum fiziğin temel kavramlarını (enerji düzeyleri, foton) kavramsal düzeyde açıklar.", keywords = listOf("kuantum", "foton", "enerji düzeyi")),
        RagKazanim("AGS.FZK.5.2", "Fizik", "Modern Fizik", "Atom modelleri", "Atom modelleri ve modern fizik yaklaşımlarını karşılaştırır.", keywords = listOf("atom modeli", "bohr", "modern"))
    )

    fun tumKazanimlar(): List<RagKazanim> {
        return sosyal_tarih + sosyal_cografya + sosyal_siyasalBilim + sosyal_sosyalBilimAlanlari +
            matematik_analiz + matematik_cebir + matematik_geometri + matematik_uygulamali +
            fizik_mekanik + fizik_elektrikManyetizma + fizik_maddeninMekanikIsil + fizik_dalgalarOptik + fizik_modernFizik
    }
}
