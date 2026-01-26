# 🚀 RELEASE NOTES - v1.3.2

**Tarih:** 25 Ocak 2026

---

## 📋 ÖZET

Bu sürüm, Global Soru Eşitleme sistemi ekler, AGS modülleri için müfredat/RAG bağlamını güçlendirir, ÖABT ana ekranda Paragraf görünürlüğünü düzenler ve Admin panelde AGS soru sayımlarını Sistem Durumu alanına ekler.

---

## 🔧 YAPILAN DEĞİŞİKLİKLER

### 1) Admin Panel - 🌍 Global Soru Eşitleme Sistemi (24/7 Mod)

**Amaç:** Tüm seviyelerdeki (İlkokul, Ortaokul, Lise, KPSS, AGS) dersleri otomatik olarak tarayıp en düşük soru sayısına sahip dersleri 4x Gemini paralel modda eşitlemek. **Uygulama kapansa bile sabaha kadar çalışabilir.**

**Özellikler:**

**A) Global Eşitleme Algoritması:**
- **Seviye Filtresi**: Tümü / İlkokul / Ortaokul / Lise / KPSS / AGS seçenekleri
- **Akıllı Hedefleme**: Tüm dersleri global olarak tarar, en düşük soru sayısına sahip dersleri önceliklendirir
- **4x Gemini Paralel**: API key sayısı kadar paralel soru üretimi
- **Staggered Start**: 1.5sn arayla başlatma (rate limit koruması)
- **Sonsuz Döngü**: Durdurulana kadar sürekli eşitler
- **Progress Bar**: Anlık ilerleme ve durum gösterimi
- **Log Entegrasyonu**: Tüm işlemler detaylı loglanır

**B) 24/7 Arka Plan Modu (YENİ!):**
- **Foreground Service**: Uygulama kapansa bile çalışır
- **WakeLock**: Telefon uyusa bile çalışır
- **Notification**: Anlık ilerleme takibi (Tur sayısı, toplam soru, durum)
- **Crash Recovery**: Hata olursa otomatik devam eder
- **Battery Optimization Bypass**: Sistem tarafından kapatılmaz
- **START_STICKY**: Sistem servisi kapatırsa otomatik yeniden başlatır

**C) İki Çalışma Modu:**
1. **UI Modda Başlat**: Uygulama açıkken çalışır (test için)
2. **24/7 Mod**: Uygulama kapansa bile çalışır (gece boyunca eşitleme için) ⭐

**Çalışma Mantığı:**
1. Tüm seviyelerdeki tüm dersleri bir havuzda toplar (~136 ders)
2. Soru sayılarına göre global olarak sıralar
3. En düşük 4 dersi seçer (4 API key var)
4. 4 Gemini API'yi paralel çalıştırarak her ders için 15 soru üretir
5. Batch olarak Firestore'a kaydeder
6. İstatistikleri günceller
7. 2 saniye bekler ve ADIM 2'ye döner (sonsuz döngü)

**Performans:**
- **Tek Tur**: ~10-15 saniye, 60 soru
- **1 Saat**: ~240 tur, ~14,400 soru
- **8 Saat (Gece)**: ~1,920 tur, ~115,200 soru

**Teknik Detaylar:**
- `GlobalSyncForegroundService`: 24/7 arka plan servisi
- `CurriculumManager.getSchoolTypesFor()` - Seviye bazlı okul türleri
- `CurriculumManager.getSubjectsFor()` - Ders listesi
- `QuestionRepository.getQuestionCountsForLevel()` - Soru sayıları
- `GeminiApiProvider.generateWithKey()` - Paralel üretim
- `QuestionRepository.saveQuestionsForLevel()` - Batch kayıt
- Staggered start: 0s, 1.5s, 3s, 4.5s (rate limiter 3sn olduğu için)
- Retry mekanizması: Her API çağrısı 3 kez denenir (3s, 6s, 12s backoff)
- Ardışık hata yönetimi: 10 hata olursa 5 dakika bekler

**UI Tasarımı:**
- Mavi-mor gradient arka plan (tüm seviyeleri temsil eden)
- 🌍 emoji ikonu
- Progress bar ve durum gösterimi
- 2 buton: "UI Modda Başlat" ve "24/7 Mod"
- Servis aktifken yeşil durum kartı gösterilir

**Notification Özellikleri:**
- Başlık: "🌍 Global Eşitleme Aktif"
- İçerik: "Tur X | +Y soru | Durum"
- "Durdur" butonu
- Sürekli görünür (ongoing)
- Düşük öncelik (pil tasarrufu)

**Etkilenen dosyalar:**
- `app/src/main/java/com/example/bilgideham/AdminPanelScreen.kt` - Global Eşitleme kartı ve butonlar
- `app/src/main/java/com/example/bilgideham/GlobalSyncForegroundService.kt` (YENİ) - 24/7 arka plan servisi
- `app/src/main/AndroidManifest.xml` - Servis tanımı ve izinler
- `24_7_GLOBAL_ESITLEME_KILAVUZU.md` (YENİ) - Detaylı kullanım kılavuzu

---

### 2) HomeScreen - AGS 2. Oturum (ÖABT) Paragraf görünürlüğü ve UI iyileştirmesi

**Amaç:** AGS 2. Oturum (ÖABT) derslerinde HomeScreen üzerindeki "Paragraf" hızlı erişimini Türkçe alanı hariç kaldırmak ve tek kart kaldığında modern, etkileyici bir görüntü sağlamak.

**Değişiklik:**
- `SchoolType.AGS_OABT` seçiliyken `AppPrefs.getOabtField(context) != "turkce"` durumunda Paragraf kartı gizlenir.
- Paragraf kartı gizlendiğinde Deneme kartı küçük kart yerine **geniş, iki satırlı bilgi içeren özel tasarım** ile gösterilir:
  - **Modern Tema**: Gradient arka plan, büyük ikon, detaylı açıklama ve özellik rozetleri (Zamanlı, Detaylı Analiz)
  - **Playful Tema**: Renkli gradient, emoji, animasyonlu press efekti ve özellik kutuları
  - **NeuralLux Tema**: Minimal gradient, neural stil border ve nokta göstergeleri
  - **Classic Tema**: Outlined card, basit ve temiz tasarım
- Tüm temalarda tutarlı yükseklik (120-170dp) ve profesyonel görünüm sağlandı.

**Etkilenen dosyalar:**
- `app/src/main/java/com/example/bilgideham/ModernThemeHome.kt` - `WideExamCard()` bileşeni eklendi
- `app/src/main/java/com/example/bilgideham/ClassicThemeHome.kt` - `WideExamCardClassic()` bileşeni eklendi
- `app/src/main/java/com/example/bilgideham/PlayfulThemeHome.kt` - `WideExamCardPlayful()` bileşeni eklendi
- `app/src/main/java/com/example/bilgideham/NeuralLuxThemeHome.kt` - `WideExamCardNeuralLux()` bileşeni eklendi

---

### 2) MEB AGS (1. Oturum) - RAG (Müfredat Bağlamı) entegrasyonu

**Amaç:** `SchoolType.AGS_MEB` için AI soru üretiminde müfredat uyumunu artırmak.

**Değişiklikler:**
- Yeni MEB AGS kazanım havuzu eklendi.
- `RagRepository` AGS_MEB için kazanımları döndürecek şekilde genişletildi.
- Ders adları için normalize eşleştirmeleri eklendi.

**Etkilenen dosyalar:**
- `app/src/main/java/com/example/bilgideham/AgsMebKazanimlar.kt` (yeni)
- `app/src/main/java/com/example/bilgideham/RagRepository.kt`

---

### 3) Admin Panel - Sistem Durumu: AGS soru sayıları

**Amaç:** Admin panelde "Sistem Durumu" bölümünde hem `AGS_MEB` hem de `AGS_OABT` için soru sayılarının görünmesi.

**Değişiklikler:**
- Sistem Durumu ekranına iki yeni blok eklendi:
  - "MEB AGS Dersleri" (AGS_MEB ders bazlı)
  - "AGS ÖABT Ünite Dersleri" (AGS_OABT alan/ünite bazlı)
- Sayım işlemleri `refreshStats()` tetikleyicisine entegre edildi.

**Etkilenen dosyalar:**
- `app/src/main/java/com/example/bilgideham/AdminPanelScreen.kt`
- `app/src/main/java/com/example/bilgideham/QuestionRepository.kt`

---

## 🧪 BUILD / RELEASE DOĞRULAMA

- ✅ `:app:compileDebugKotlin` başarılı
- ✅ `:app:assembleRelease` başarılı (R8/minify + lintVital + Crashlytics mapping adımları dahil)

---

## 📝 NOTLAR

- Derlemede bazı deprecation uyarıları mevcut (kritik değil).
- `AdminPanelScreen.kt` içinde label ile ilgili bir uyarı görülebilir (kritik değil, opsiyonel refactor ile temizlenebilir).
