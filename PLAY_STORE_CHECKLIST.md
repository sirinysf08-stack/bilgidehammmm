# 🚀 Google Play Store Yayın Kontrol Listesi - Akıl Küpü v1.2.8

## ✅ TAMAMLANAN DÜZELTMELER

### 1. Kritik Manifest Düzeltmesi
- ❌ **KALDIRILDI:** `android.intent.category.HOME` ve `DEFAULT` kategorileri
- ✅ Uygulama artık sadece normal launcher olarak işaretli
- Bu Google'ın launcher replacement politikasına uygun

### 2. Privacy Policy Güncellemesi
- ✅ Tarih tutarlılığı sağlandı: **14 Ocak 2026**
- ✅ E-posta adresi eklendi: **bilgideham@gmail.com**
- ✅ HTML ve MD dosyaları senkronize

### 3. AAB Hazır
- ✅ Dosya: `app/release/akil-kupu-v1.2.8-final.aab`
- ✅ Versiyon: 1.2.8 (versionCode: 13)
- ✅ İmzalı ve optimize edilmiş
- ✅ Build başarılı (9m 40s)

---

## 📋 PLAY CONSOLE'DA YAPILACAKLAR

### ADIM 1: Yeni Sürüm Yükle
1. Play Console → **Üretim** → **Yeni sürüm oluştur**
2. AAB yükle: `app/release/akil-kupu-v1.2.8-final.aab`
3. Sürüm notlarını ekle:

```
<tr-TR>
🎨 Animasyonlu uzay arka planı eklendi
🔧 Yan menü tıklama sorunu giderildi
🔧 Küçük ekranlarda metin taşması düzeltildi
⏰ Bildirimler artık gece gönderilmiyor
📱 Android 15 ve tablet desteği iyileştirildi
</tr-TR>
```

---

### ADIM 2: Veri Güvenliği (Data Safety) Formu

**Toplanan Veriler:**
- ✅ **Uygulama etkileşimleri** (quiz sonuçları, ilerleme)
- ✅ **Cihaz bilgileri** (model, OS versiyonu)
- ✅ **Uygulama performansı** (crash raporları)
- ❌ **Kişisel bilgi toplamıyoruz** (ad, e-posta, telefon, konum)

**Veri Kullanım Amacı:**
- ✅ Uygulama işlevselliği
- ✅ Analitik
- ✅ Kişiselleştirme

**Veri Paylaşımı:**
- ✅ Firebase/Google ile (hizmet sağlayıcı olarak)
- ❌ Üçüncü taraflarla satış YOK
- ❌ Reklam amaçlı paylaşım YOK

**Güvenlik Önlemleri:**
- ✅ Veriler şifreli aktarılıyor (SSL/TLS)
- ✅ Firebase güvenlik kuralları aktif
- ✅ Kullanıcı veri silme hakkına sahip

**İzinler ve Kullanım:**
- **Kamera:** QR kod okuma, soru tarama (opsiyonel)
- **Mikrofon:** Sesli yanıt özelliği (opsiyonel)
- **Konum:** Nearby Connections için (opsiyonel, veri saklanmıyor)
- **Bildirim:** Günlük motivasyon (opsiyonel)

---

### ADIM 3: İçerik Derecelendirmesi

**Hedef Kitle:**
- Yaş: **3+** veya **7+** (eğitim uygulaması)
- Çocuklara yönelik: **Evet**
- COPPA uyumlu: **Evet**

**İçerik Özellikleri:**
- Şiddet: **Yok**
- Cinsel içerik: **Yok**
- Küfür: **Yok**
- Korku/Dehşet: **Yok**
- AI içerik: **Filtrelenmiş ve güvenli**

---

### ADIM 4: Uygulama Kategorisi

- **Ana Kategori:** Eğitim
- **Alt Kategori:** Öğrenme Araçları
- **Etiketler:** yapay zeka, öğrenci, sınav, eğitim asistanı

---

### ADIM 5: Gizlilik Politikası URL'i

Privacy policy HTML dosyasını host etmen gerekiyor. Seçenekler:

**Önerilen: Firebase Hosting (Ücretsiz)**
```bash
# Firebase CLI kur
npm install -g firebase-tools

# Firebase'e giriş yap
firebase login

# Hosting başlat
firebase init hosting

# privacy-policy.html dosyasını public/ klasörüne koy
# Deploy et
firebase deploy --only hosting
```

**Alternatif: GitHub Pages**
1. GitHub'da yeni repo oluştur: `akil-kupu-privacy`
2. `privacy-policy.html` dosyasını yükle
3. Settings → Pages → Enable
4. URL: `https://[username].github.io/akil-kupu-privacy/privacy-policy.html`

Play Console'a bu URL'i ekle.

---

### ADIM 6: Mağaza Girişi (Store Listing)

**Uygulama Adı:**
```
Akıl Küpü AI: Eğitim Asistanın
```

**Kısa Açıklama (80 karakter):**
```
Yapay zeka asistanınla İngilizce konuş, matematik çöz ve tarihi keşfet!
```

**Tam Açıklama:**
(store_description.md dosyasındaki metni kullan)

**Ekran Görüntüleri:**
- Minimum 2, maksimum 8 adet
- Telefon: 16:9 veya 9:16 oran
- Tablet: 16:9 veya 9:16 oran (opsiyonel ama önerilir)

**Öne Çıkan Grafik:**
- Boyut: 1024 x 500 px
- Format: PNG veya JPG

---

## ⚠️ ÖNEMLİ NOTLAR

### 1. In-App Update Test
- Uygulamayı Google Play'den indirmiş bir cihazda test et
- Eski sürüm (1.2.7) yüklü olmalı
- Yeni sürüm (1.2.8) Play Console'da yayınlanmalı
- Rollout %100'e ulaşmalı

### 2. Çocuk Gizliliği (COPPA)
- 13 yaş altı için ebeveyn izni gerekli
- Kişisel bilgi toplamıyoruz
- AI içeriği filtrelenmiş

### 3. İzin Açıklamaları
Play Console'da her izin için açıklama ekle:
- **Kamera:** "Matematik sorularını taramak ve QR kod okumak için"
- **Mikrofon:** "İngilizce telaffuz pratiği için"
- **Konum:** "Yakındaki cihazlarla eğitim içeriği paylaşmak için"
- **Bildirim:** "Günlük motivasyon mesajları göndermek için"

---

## 🎯 ONAY SÜRECİ

**Beklenen Süre:** 1-7 gün

**Olası Red Nedenleri ve Çözümleri:**

1. **Privacy Policy Erişilemiyor**
   - Çözüm: URL'in çalıştığından emin ol

2. **İzin Açıklaması Yetersiz**
   - Çözüm: Her izin için detaylı açıklama ekle

3. **Veri Güvenliği Formu Eksik**
   - Çözüm: Tüm soruları yukarıdaki bilgilere göre doldur

4. **Ekran Görüntüleri Eksik**
   - Çözüm: En az 2 adet telefon screenshot ekle

---

## ✅ SON KONTROL

- [ ] AAB yüklendi
- [ ] Sürüm notları eklendi
- [ ] Veri güvenliği formu dolduruldu
- [ ] İçerik derecelendirmesi yapıldı
- [ ] Privacy policy URL'i eklendi
- [ ] Ekran görüntüleri yüklendi
- [ ] Öne çıkan grafik yüklendi
- [ ] İzin açıklamaları eklendi
- [ ] Test cihazda kontrol edildi

---

## 📞 DESTEK

Herhangi bir sorun olursa:
- E-posta: bilgideham@gmail.com
- Play Console Destek: https://support.google.com/googleplay/android-developer

**Başarılar! 🚀**
