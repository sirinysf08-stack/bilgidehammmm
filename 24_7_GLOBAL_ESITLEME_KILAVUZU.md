# 🌍 24/7 Global Soru Eşitleme Sistemi - Kullanım Kılavuzu

**Tarih:** 25 Ocak 2026  
**Özellik:** Uygulama kapansa bile sabaha kadar çalışan arka plan eşitleme

---

## 🎯 ÖZELLİKLER

### ✅ Uygulama Kapansa Bile Çalışır
- **Foreground Service** teknolojisi kullanır
- Android sistem tarafından yüksek öncelikli olarak korunur
- Notification bar'da sürekli görünür

### ✅ Telefon Uyusa Bile Çalışır
- **WakeLock** teknolojisi ile CPU aktif tutulur
- Ekran kapansa bile arka planda çalışmaya devam eder
- 24 saat boyunca kesintisiz çalışabilir

### ✅ Crash Olursa Otomatik Devam Eder
- **START_STICKY** flag ile sistem servisi otomatik yeniden başlatır
- Retry mekanizması: Her API çağrısı 3 kez denenir
- Exponential backoff: 3s, 6s, 12s bekleme süreleri

### ✅ Battery Optimization Bypass
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` izni ile
- Sistem pil tasarrufu modunda bile çalışır

### ✅ Notification ile İlerleme Takibi
- Anlık tur sayısı
- Toplam eklenen soru sayısı
- Şu anki işlem durumu
- "Durdur" butonu ile kolay kontrol

---

## 🚀 KULLANIM

### 1. Admin Panel'i Aç
```
Ana Sayfa → Menü → Admin Panel (şifre: 787878)
```

### 2. Global Eşitleme Kartını Bul
Aşağı kaydırın, "🌍 Global Soru Eşitleme" kartını bulun.

### 3. Seviye Seçin (Opsiyonel)
- **Tümü**: Tüm seviyeleri eşitler (önerilen)
- **İlkokul**: Sadece 3-4. sınıf
- **Ortaokul**: Sadece 5-8. sınıf
- **Lise**: Sadece 9-12. sınıf
- **KPSS**: Ortaöğretim, Önlisans, Lisans
- **AGS**: MEB + ÖABT

### 4. Modu Seçin

#### A) UI Modda Başlat (Geçici)
- Uygulama açıkken çalışır
- Uygulama kapanınca durur
- Test için idealdir

#### B) 24/7 Mod (Kalıcı) ⭐ ÖNERİLEN
- Uygulama kapansa bile çalışır
- Sabaha kadar kesintisiz çalışır
- Notification'dan takip edilir

### 5. Başlat!
"24/7 Mod" butonuna basın.

---

## 📱 NOTIFICATION EKRANI

Notification bar'da şu bilgileri göreceksiniz:

```
🌍 Global Eşitleme Aktif
Tur 15 | +450 soru | Matematik: +15
[Durdur]
```

**Bilgiler:**
- **Tur 15**: 15. tur tamamlandı
- **+450 soru**: Toplam 450 soru eklendi
- **Matematik: +15**: Son işlem
- **[Durdur]**: Butona basarak durdurabilirsiniz

---

## 🛑 DURDURMA

### Yöntem 1: Notification'dan
1. Notification'ı aşağı çekin
2. "Durdur" butonuna basın

### Yöntem 2: Admin Panel'den
1. Admin Panel'i açın
2. "🟢 24/7 Mod Aktif" kartını bulun
3. "Durdur" butonuna basın

### Yöntem 3: Uygulama Ayarlarından
1. Telefon Ayarları → Uygulamalar → BilgiDeham
2. "Zorla Durdur" butonuna basın

---

## ⚙️ TEKNİK DETAYLAR

### Çalışma Mantığı

**ADIM 1: Tüm Dersleri Topla**
```
Tüm Seviyeler → Tüm Okul Türleri → Tüm Sınıflar → Tüm Dersler
Toplam: ~136 ders
```

**ADIM 2: Global Sıralama**
```
Tüm dersleri soru sayısına göre sırala
En düşük 4'ü seç (4 API key var)
```

**ADIM 3: Paralel Üretim**
```
4 Gemini API paralel çalışır
Her biri 15 soru üretir
Staggered start: 0s, 1.5s, 3s, 4.5s
```

**ADIM 4: Kaydet ve Tekrarla**
```
Firestore'a batch kaydet
İstatistikleri güncelle
2 saniye bekle
ADIM 2'ye dön (sonsuz döngü)
```

### Performans

**Tek Tur:**
- Süre: ~10-15 saniye
- Üretilen Soru: 60 soru (4 ders × 15 soru)

**1 Saat:**
- Tur Sayısı: ~240 tur
- Üretilen Soru: ~14,400 soru

**8 Saat (Gece Boyunca):**
- Tur Sayısı: ~1,920 tur
- Üretilen Soru: ~115,200 soru

### Hata Yönetimi

**API Hatası:**
- 3 kez tekrar dener
- Exponential backoff: 3s, 6s, 12s
- Başarısız olursa sonraki derse geçer

**Ardışık Hatalar:**
- 10 ardışık hata olursa 5 dakika bekler
- Sonra tekrar başlar

**Crash:**
- Android sistem servisi otomatik yeniden başlatır
- Kaldığı yerden devam eder

### Pil Tüketimi

**Orta Seviye:**
- CPU: %5-10 (4 paralel API çağrısı)
- Network: Sürekli aktif
- WakeLock: Telefon uyumuyor

**Tahmini Pil Tüketimi:**
- 8 saat: %20-30 pil
- Şarjda bırakmanız önerilir

---

## 🔒 GÜVENLİK

### İzinler
- `FOREGROUND_SERVICE`: Arka plan servisi
- `FOREGROUND_SERVICE_DATA_SYNC`: Veri senkronizasyonu
- `WAKE_LOCK`: CPU'yu aktif tut
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Pil tasarrufu bypass

### Veri Güvenliği
- Tüm API çağrıları HTTPS üzerinden
- Firebase Firestore güvenlik kuralları aktif
- Duplicate kontrolü yapılır

---

## 📊 ÖRNEK SENARYO

### Senaryo: Gece Boyunca Eşitleme

**Saat 23:00 - Başlatma**
```
Admin Panel → Global Eşitleme → 24/7 Mod
Seviye: Tümü
```

**Saat 23:01 - İlk Turlar**
```
🔄 TUR 1: 🔵[AGS]İlköğretim Matematik(0) 🟢[AGS]Türkçe(0) 🟣[AGS]Fen(0) 🟡[AGS]Sosyal(0)
✅ 🔵 [AGS] İlköğretim Matematik: +15 → 15
✅ 🟢 [AGS] Türkçe: +15 → 15
✅ 🟣 [AGS] Fen: +15 → 15
✅ 🟡 [AGS] Sosyal: +15 → 15
```

**Saat 00:00 - 1 Saat Sonra**
```
Tur: 240
Toplam Soru: 14,400
Durum: 🟢 Aktif
```

**Saat 07:00 - Sabah**
```
Tur: 1,920
Toplam Soru: 115,200
Durum: 🟢 Aktif
```

**Saat 07:30 - Durdurma**
```
Notification → Durdur
veya
Admin Panel → Durdur
```

**Sonuç:**
- 8.5 saat çalıştı
- 115,200 soru eklendi
- Tüm dersler eşitlendi

---

## ⚠️ ÖNEMLİ NOTLAR

### 1. Şarjda Bırakın
Gece boyunca çalışacaksa telefonu şarjda bırakın.

### 2. Wi-Fi Bağlantısı
Stabil Wi-Fi bağlantısı önerilir (mobil veri pahalı olabilir).

### 3. Pil Tasarrufu Modunu Kapatın
Ayarlar → Pil → BilgiDeham → Pil Tasarrufu: Kapalı

### 4. Arka Plan Kısıtlamasını Kaldırın
Ayarlar → Uygulamalar → BilgiDeham → Pil → Kısıtlanmamış

### 5. Bildirim İznini Verin
Notification'ları görebilmek için izin gerekli.

### 6. API Kota Limiti
Gemini 2.5 Pro: 20 RPM (dakikada 20 istek)
4 key × 20 RPM = 80 RPM toplam
Sistem otomatik olarak rate limit yönetir.

---

## 🐛 SORUN GİDERME

### Servis Durmuş
**Sebep:** Sistem pil tasarrufu için durdurmuş olabilir.
**Çözüm:** Pil optimizasyonunu kapatın.

### Notification Görünmüyor
**Sebep:** Bildirim izni verilmemiş.
**Çözüm:** Ayarlar → Bildirimler → BilgiDeham → İzin Ver

### Çok Yavaş Çalışıyor
**Sebep:** İnternet bağlantısı yavaş veya API rate limit.
**Çözüm:** Wi-Fi bağlantısını kontrol edin.

### Crash Oluyor
**Sebep:** API hatası veya Firestore bağlantı sorunu.
**Çözüm:** Sistem otomatik yeniden başlatır, bekleyin.

---

## 📞 DESTEK

Sorun yaşarsanız:
1. Admin Panel → Log'ları kontrol edin
2. Notification'daki durumu kontrol edin
3. Servisi durdurup yeniden başlatın

---

**Hazırlayan:** Kiro AI Assistant  
**Tarih:** 25 Ocak 2026  
**Versiyon:** 1.3.2
