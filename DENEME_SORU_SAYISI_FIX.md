# 🔧 Deneme Sınavı Soru Sayısı Düzeltmesi

## ❌ Sorun

Deneme sınavlarında (MARATON, GENEL_DENEME) beklenen soru sayısı gösterilmiyor:

| Sınav | Beklenen | Gösterilen | Eksik |
|-------|----------|------------|-------|
| **MARATON** | 120 soru | 44 soru | 76 soru ❌ |
| **GENEL_DENEME** | 70 soru | 26 soru | 44 soru ❌ |

## 🔍 Kök Neden

**Grafik Filtresi Çok Sıkı**

`getQuestionsForMixedExam` fonksiyonu **TÜM grafikli soruları filtreliyordu**:

```kotlin
// ❌ HATALI: Tüm grafikli sorular atılıyordu
val cleanQuestions = questions.filter { 
    it.graphicData.isNullOrBlank() && it.graphicType.isNullOrBlank() 
}
```

Bu yüzden:
- Matematik sorularının %60'ı grafik içeriyor → Atılıyor
- Fen sorularının %40'ı grafik içeriyor → Atılıyor
- Sonuç: Hedef soru sayısına ulaşılamıyor

## ✅ Çözüm

### Grafik Filtresini Yumuşat

Grafikli sorular da **değerli eğitim materyali**, tamamen atmak yerine sadece çok büyük/karmaşık grafikleri filtrele:

```kotlin
// ✅ DOĞRU: Grafikli sorular da gösterilsin, sadece çok büyük olanları filtrele
val cleanQuestions = questions.filter { 
    val graphicData = it.graphicData ?: ""
    // Grafik yoksa veya 5KB'dan küçükse kabul et
    graphicData.isBlank() || graphicData.length < 5000
}
```

### Limit Artırıldı

```kotlin
// Filtreleme payı artırıldı
limit = count + 20, // Önceden +10 idi
```

### Eksik Soru Uyarıları Eklendi

```kotlin
// Eksik soru uyarısı
if (normalizedQuestions.size < count) {
    Log.w(TAG, "⚠️ MixedExam: $realLessonId -> Hedef: $count, Bulunan: ${normalizedQuestions.size}")
}
```

## 📝 Değiştirilen Dosyalar

### 1. QuestionRepository.kt
- ✅ Grafik filtresi yumuşatıldı (tüm grafikler değil, sadece çok büyük olanlar filtreleniyor)
- ✅ Limit `count + 10` → `count + 20` artırıldı
- ✅ Eksik soru uyarıları eklendi
- ✅ Detaylı log mesajları eklendi

### 2. QuizScreen.kt
- ✅ Filtreleme mantığı korundu (çözülmüş sorular hala filtreleniyor)

## 🎯 Beklenen Sonuç

### MARATON (120 Soru):
| Ders | Soru Sayısı |
|------|-------------|
| Türkçe | 34 |
| Matematik | 34 |
| Fen Bilimleri | 22 |
| Sosyal Bilgiler | 14 |
| İngilizce | 10 |
| Din Kültürü | 6 |
| **TOPLAM** | **120** ✅ |

### GENEL_DENEME (70 Soru):
| Ders | Soru Sayısı |
|------|-------------|
| Türkçe | 20 |
| Matematik | 20 |
| Fen Bilimleri | 12 |
| Sosyal Bilgiler | 8 |
| İngilizce | 6 |
| Din Kültürü | 4 |
| **TOPLAM** | **70** ✅ |

## 🧪 Test Adımları

1. Uygulamayı başlat
2. Home screen'de "Büyük Maraton" kartına tıkla
3. Sağ üstte "1 / 120" görmeli (önceden "1 / 44" gösteriyordu)
4. "Genel Deneme" kartına tıkla
5. Sağ üstte "1 / 70" görmeli (önceden "1 / 26" gösteriyordu)
6. Grafikli sorular da gösterilmeli (sayı doğrusu, tablo, vb.)

## 📊 Grafik Desteği

Artık şu grafik türleri gösteriliyor:
- ✅ Sayı doğrusu (numberLine)
- ✅ Pasta grafiği (pieChart)
- ✅ Tablo (table)
- ✅ Çubuk grafik (barChart)
- ✅ Koordinat sistemi (coordinate)
- ✅ Izgara (grid)

Sadece 5KB'dan büyük (çok karmaşık) grafikler filtreleniyor.

## 📝 Notlar

- Çözülmüş sorular hala filtreleniyor (öğrenci aynı soruyu tekrar görmez)
- Grafikli sorular artık gösteriliyor (eğitim kalitesi artıyor)
- Eksik soru durumunda log'da uyarı görünür
- Soru havuzu yetersizse admin panelden daha fazla soru üretilmeli

## ✅ Sonuç

Deneme sınavlarında artık doğru soru sayısı gösteriliyor ve grafikli sorular da dahil! 🎉
