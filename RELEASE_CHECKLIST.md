# 🚀 RELEASE CHECKLIST - Bilgideham

> Bu doküman her release öncesi kontrol edilmesi gereken adımları içerir.

---

## 📋 CI Pipeline Komutları

```bash
# 1. Lint Kontrolü
./gradlew lintDebug lintRelease

# 2. Unit Testler
./gradlew testDebugUnitTest

# 3. Release Build (Minify + R8)
./gradlew assembleRelease

# 4. Crashlytics Mapping Upload
./gradlew uploadCrashlyticsMappingFileRelease
```

---

## ⏱️ 5 Dakika Hızlı Test

| ✅ | Test | Beklenen Sonuç |
|----|------|----------------|
| ⬜ | Uygulama açılış | < 3 saniye |
| ⬜ | Quiz başlat | Sorular yüklensin |
| ⬜ | Ayarlar → Tema değiştir | Crash yok |
| ⬜ | Kelime Avı → Test ol | Quiz tamamlansın |
| ⬜ | Release APK yükle | Çalışsın |

---

## 🔬 30 Dakika Derin Test

| ✅ | Test | Repro | Bakılacak Dosya |
|----|------|-------|-----------------|
| ⬜ | DB Migration | Eski APK → Yeni APK | `GameDatabase.kt` |
| ⬜ | Low Memory | 50 uygulama aç | Memory leak |
| ⬜ | Offline Mode | Uçak modu + Quiz | Firestore cache |
| ⬜ | Crash Test | Debug'da throw | Crashlytics |
| ⬜ | ANR Test | 100 soru hızlı geç | Main thread |

---

## 📊 Crashlytics Hedef Metrikleri

| Metrik | ✅ Hedef | ❌ Alarm |
|--------|---------|---------|
| Crash-free users | ≥ 99.5% | < 99% |
| Crash rate | < 0.5% | > 1% |
| ANR rate | < 0.1% | > 0.3% |

---

## 🗺️ Mapping Dosyası Kontrolü

```bash
# Mapping dosyası oluştu mu?
ls app/build/outputs/mapping/release/mapping.txt

# Firebase Console → Crashlytics → Settings → Mapping files
# Son upload tarihi bugün mü?
```

---

## ✅ Release Onay

- [ ] Hızlı test tamamlandı
- [ ] Derin test tamamlandı
- [ ] Crashlytics metrikleri normal
- [ ] Mapping dosyası yüklendi
- [ ] Version code artırıldı

**Son Güncelleme:** 2026-01-23
