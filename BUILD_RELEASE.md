# 🚀 ÜRETİM BUILD ALMA REHBERİ

**Versiyon:** 1.3.0 (Build 15)  
**Tarih:** 18 Ocak 2026

---

## 📦 GOOGLE PLAY İÇİN: AAB (Android App Bundle)

**Neden AAB?**
- ✅ Google Play Store zorunlu format (2021'den beri)
- ✅ Daha küçük indirme boyutu
- ✅ Cihaza özel optimizasyon
- ✅ Dinamik özellik modülleri

**APK Ne Zaman?**
- ⚠️ Sadece test için
- ⚠️ Direkt yükleme için (sideload)
- ⚠️ Alternatif mağazalar için

---

## 🔨 BUILD ALMA ADIMLARI

### 1️⃣ Temizlik ve Hazırlık

```bash
# Önceki build'leri temizle
./gradlew clean

# Cache temizle (opsiyonel)
./gradlew cleanBuildCache
```

### 2️⃣ AAB Oluştur (Google Play için)

```bash
# Release AAB oluştur
./gradlew bundleRelease
```

**Çıktı Konumu:**
```
app/build/outputs/bundle/release/app-release.aab
```

### 3️⃣ APK Oluştur (Test için)

```bash
# Release APK oluştur
./gradlew assembleRelease
```

**Çıktı Konumu:**
```
app/build/outputs/apk/release/app-release.apk
```

---

## ✅ BUILD SONRASI KONTROLLER

### AAB Dosyası Kontrolü

```bash
# Dosya boyutunu kontrol et
ls -lh app/build/outputs/bundle/release/app-release.aab

# Beklenen boyut: 30-50 MB
```

### İmza Kontrolü

```bash
# AAB imzalandı mı kontrol et
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab

# "jar verified" mesajını görmeli
```

---

## 📋 DOSYA YÖNETİMİ

### Dosyayı Kopyala

```bash
# AAB'yi release klasörüne kopyala
copy app\build\outputs\bundle\release\app-release.aab app\release\bilgideham-v1.3.0.aab

# APK'yı kopyala (test için)
copy app\build\outputs\apk\release\app-release.apk app\release\bilgideham-v1.3.0.apk
```

### Dosya İsimlendirme

**Önerilen Format:**
```
bilgideham-v1.3.0-build15.aab
bilgideham-v1.3.0-build15.apk
```

---

## 🔐 KEYSTORE BİLGİLERİ

**Keystore Konumu:**
```
keystore/bilgideham-release.jks
```

**Bilgiler:**
- Store Password: `bilgideham2024` (veya env var)
- Key Alias: `bilgideham`
- Key Password: `bilgideham2024` (veya env var)

**Güvenlik Notu:**
- ⚠️ Keystore dosyasını yedekle!
- ⚠️ Şifreleri güvenli sakla!
- ⚠️ Git'e commit etme!

---

## 📤 GOOGLE PLAY CONSOLE'A YÜKLEME

### 1. Play Console'a Giriş
```
https://play.google.com/console
```

### 2. Uygulama Seç
```
BilgiDeham (com.bilgideham.app)
```

### 3. Release Oluştur

**Yol:**
```
Production → Create new release
```

**veya Internal Test için:**
```
Testing → Internal testing → Create new release
```

### 4. AAB Yükle

```
1. "Upload" butonuna tıkla
2. app-release.aab dosyasını seç
3. Yükleme tamamlanana kadar bekle
```

### 5. Release Notes Ekle

```
Türkçe:
[RELEASE_NOTES_v1.3.0.md içeriğini kopyala]

İngilizce (opsiyonel):
- Added 3rd grade support
- Improved AI features
- Performance optimizations
```

### 6. Gözden Geçir ve Yayınla

```
1. "Review release" tıkla
2. Tüm bilgileri kontrol et
3. "Start rollout to Production" tıkla
```

---

## 🧪 TEST YÜKLEME (Internal Test)

### Internal Test Track

**Avantajları:**
- ✅ Hızlı onay (dakikalar içinde)
- ✅ Sınırlı kullanıcı grubu
- ✅ Gerçek cihazlarda test

**Adımlar:**
```
1. Testing → Internal testing
2. Create new release
3. AAB yükle
4. Release notes ekle
5. Review and rollout
6. Test linkini paylaş
```

**Test Linki:**
```
https://play.google.com/apps/internaltest/...
```

---

## 📊 BUILD BİLGİLERİ

### Versiyon Bilgileri

```kotlin
// build.gradle.kts
versionCode = 15
versionName = "1.3.0"
```

### Build Yapılandırması

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(...)
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### Boyut Bilgileri

**Beklenen Boyutlar:**
- AAB: ~35-45 MB
- APK (Universal): ~50-60 MB
- APK (arm64-v8a): ~40-50 MB
- APK (armeabi-v7a): ~38-48 MB

---

## 🐛 SORUN GİDERME

### Build Hatası

```bash
# Gradle daemon'u yeniden başlat
./gradlew --stop
./gradlew clean

# Tekrar dene
./gradlew bundleRelease
```

### İmza Hatası

```
Error: Keystore not found
```

**Çözüm:**
```bash
# Keystore yolunu kontrol et
ls keystore/bilgideham-release.jks

# Şifreleri kontrol et
echo $KEYSTORE_PASSWORD
```

### ProGuard Hatası

```
Error: Missing classes
```

**Çözüm:**
```
proguard-rules.pro dosyasını kontrol et
Gerekli keep kurallarını ekle
```

---

## ✅ KONTROL LİSTESİ

Build almadan önce:

- [ ] Version code artırıldı mı? (15)
- [ ] Version name güncellendi mi? (1.3.0)
- [ ] Release notes hazır mı?
- [ ] Keystore dosyası mevcut mu?
- [ ] Debug logları temizlendi mi? ✅
- [ ] ProGuard kuralları güncel mi? ✅
- [ ] Test edildi mi?

Build sonrası:

- [ ] AAB dosyası oluştu mu?
- [ ] Dosya boyutu normal mi? (30-50 MB)
- [ ] İmza doğrulandı mı?
- [ ] Dosya yedeklendi mi?

---

## 🚀 HIZLI KOMUTLAR

### Tek Komutla Build

```bash
# Temizle ve AAB oluştur
./gradlew clean bundleRelease

# Temizle ve APK oluştur
./gradlew clean assembleRelease

# Her ikisini de oluştur
./gradlew clean bundleRelease assembleRelease
```

### Build Bilgilerini Göster

```bash
# Build varyantlarını listele
./gradlew tasks --group=build

# Bağımlılıkları göster
./gradlew dependencies
```

---

## 📁 ÇIKTI DOSYALARI

### AAB (Google Play)
```
app/build/outputs/bundle/release/
├── app-release.aab (35-45 MB)
└── output-metadata.json
```

### APK (Test)
```
app/build/outputs/apk/release/
├── app-release.apk (50-60 MB)
└── output-metadata.json
```

### Mapping Files (ProGuard)
```
app/build/outputs/mapping/release/
├── mapping.txt (Crash analizi için)
├── seeds.txt
└── usage.txt
```

**ÖNEMLİ:** `mapping.txt` dosyasını sakla! Crash raporlarını çözmek için gerekli.

---

## 🎯 ÖNERİLEN AKIŞ

### İlk Yayın İçin

```
1. Internal Test → 1-2 gün
2. Closed Beta → 1 hafta
3. Open Beta → 2 hafta
4. Production → Staged rollout
```

### Güncelleme İçin

```
1. Internal Test → 1 gün
2. Production → Staged rollout (10% → 50% → 100%)
```

---

**Hazır mısın? Hadi build alalım! 🚀**

```bash
./gradlew clean bundleRelease
```

**Başarılar!** 🎉
