# Akıl Küpü v1.2.9 - Sürüm Notları

## Google Play Console İçin Sürüm Notu

```
<tr-TR>
🎨 Animasyonlu uzay arka planı eklendi
🔧 Yan menü tıklama sorunu giderildi
🔧 Küçük ekranlarda metin taşması düzeltildi
⏰ Bildirimler artık gece gönderilmiyor
📱 Android 15 ve tablet desteği iyileştirildi
🔧 Pixel 8 ve modern cihazlarda buton görünürlüğü düzeltildi
</tr-TR>
```

---

## Teknik Detaylar

### Versiyon Bilgisi
- **versionCode:** 14
- **versionName:** 1.2.9
- **AAB Dosyası:** `app/release/akil-kupu-v1.2.9.aab`
- **Build Tarihi:** 16 Ocak 2026

### Yapılan Değişiklikler

#### 1. Edge-to-Edge Uyumluluğu (Pixel 8 Fix)
- QuizScreen: "Cevabı Kontrol Et" butonu navigation bar'ın üstünde
- PastLgsQuestionsScreen: İçerik navigation bar'a taşmıyor
- PastKpssQuestionsScreen: İçerik navigation bar'a taşmıyor
- PastAgsQuestionsScreen: İçerik navigation bar'a taşmıyor
- AgsTarihScreen: İçerik navigation bar'a taşmıyor

**Çözüm:** `WindowInsets.navigationBars.asPaddingValues()` eklendi

#### 2. Manifest Düzeltmesi (Play Store Uyumluluğu)
- `android.intent.category.HOME` kategorisi kaldırıldı
- `android.intent.category.DEFAULT` kategorisi kaldırıldı
- Uygulama artık sadece normal launcher

#### 3. Privacy Policy Güncellemesi
- Tarih tutarlılığı: 14 Ocak 2026
- E-posta: bilgideham@gmail.com

#### 4. Görsel İyileştirmeler
- Animasyonlu uzay arka planı (nebula, galaksi, yıldızlar)
- Yan menü kozmik tema uyumlu

#### 5. UI Düzeltmeleri
- Küçük ekranlarda metin taşması giderildi
- Quiz ekranında alt çizgi formatı düzeltildi
- Yan menü tıklama sorunu çözüldü

#### 6. Bildirim İyileştirmeleri
- Günlük motivasyon: 17:30
- Gece saatleri (22:00-07:00) bildirim yok

#### 7. Android 15 Uyumluluğu
- targetSdk 35
- Edge-to-Edge desteği
- Deprecated API'ler temizlendi

---

## Test Edilmesi Gerekenler

### Öncelikli
- [ ] Pixel 8'de quiz ekranında buton görünürlüğü
- [ ] Diğer modern cihazlarda (Pixel 7, Samsung S23 vb.) buton görünürlüğü
- [ ] Tablet cihazlarda layout

### İkincil
- [ ] In-app update çalışıyor mu (Play Store'dan indirilen cihazda)
- [ ] Bildirimler gece gelmiyor mu
- [ ] Yan menü tıklama sorunu düzeldi mi

---

## Play Console Yükleme Adımları

1. **Üretim** → **Yeni sürüm oluştur**
2. AAB yükle: `app/release/akil-kupu-v1.2.9.aab`
3. Sürüm notlarını yukarıdaki metinden kopyala
4. **İncele** → **Yayınla**

---

## Önceki Sürümlerden Farklar

### v1.2.8 → v1.2.9
- Versiyon kodu artırıldı (13 → 14)
- Pixel 8 buton sorunu düzeltildi
- Tüm quiz ekranlarında navigation bar padding eklendi

### v1.2.7 → v1.2.8
- Manifest HOME kategorisi kaldırıldı
- Privacy policy güncellendi
- In-app update iyileştirildi

---

## Bilinen Sorunlar

Yok - Tüm kritik sorunlar düzeltildi ✅

---

## Sonraki Sürüm İçin Planlar

- Kullanıcı geri bildirimlerine göre iyileştirmeler
- Performans optimizasyonları
- Yeni özellikler (kullanıcı isteklerine göre)
