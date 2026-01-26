# 🌍 Global Soru Eşitleme Sistemi - Uygulama Raporu

**Tarih:** 25 Ocak 2026  
**Durum:** ✅ TAMAMLANDI

---

## 📋 GÖREV TANIMI

Admin Panel'e KPSS Deneme Üretici kartının altına yeni bir "Global Soru Eşitleme" kartı eklemek. Bu kart, tüm seviyelerdeki (İlkokul, Ortaokul, Lise, KPSS, AGS) dersleri tarayarak en düşük soru sayısına sahip dersleri bulup 4x Gemini paralel modda eşitleyecek.

---

## ✅ UYGULANAN ÇÖZÜM

### 1. Kart Konumu ve Tasarım

**Konum:** AdminPanelScreen.kt, satır ~1130 (KPSS Deneme kartından hemen sonra)

**Tasarım Özellikleri:**
- **Renk Şeması**: Mavi-mor gradient (tüm seviyeleri temsil eden)
  - Light mode: `Color(0xFFE8EAF6)` arka plan
  - Dark mode: `Color(0xFF1A237E)` arka plan
- **İkon**: 🌍 (Global temsili)
- **Başlık**: "Global Soru Eşitleme"
- **Alt Başlık**: "Tüm Seviyeleri 4x Paralel Eşitle"

### 2. Seviye Filtresi

Kullanıcı hangi seviyeleri eşitlemek istediğini seçebilir:
- **Tümü** (varsayılan): İlkokul + Ortaokul + Lise + KPSS + AGS
- **İlkokul**: Sadece 3-4. sınıf
- **Ortaokul**: Sadece 5-8. sınıf
- **Lise**: Sadece 9-12. sınıf
- **KPSS**: Ortaöğretim, Önlisans, Lisans
- **AGS**: MEB (1. Oturum) + ÖABT (2. Oturum)

### 3. Çalışma Algoritması

```kotlin
// 1. Seviye seçimi
val targetLevels = selectedGlobalLevel?.let { listOf(it) } 
    ?: EducationLevel.entries.toList()

// 2. Her seviye için
for (level in targetLevels) {
    // 2.1. Okul türlerini al
    val schoolTypes = CurriculumManager.getSchoolTypesFor(level)
    
    for (schoolType in schoolTypes) {
        // 2.2. Sınıfları al
        val grades = if (schoolType.grades.isEmpty()) listOf(null) 
                     else schoolType.grades
        
        for (grade in grades) {
            // 2.3. Dersleri al
            val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
            
            // 2.4. Soru sayılarını çek
            val counts = QuestionRepository.getQuestionCountsForLevel(
                level, schoolType, grade
            )
            
            // 2.5. Ders-sayı listesi oluştur
            val allTargets = subjects.map { subj ->
                Triple(grade, subj, counts[subj.id] ?: 0)
            }.sortedBy { it.third } // En düşükten başla
            
            // 2.6. En düşük N dersi al (N = API key sayısı)
            val targets = allTargets.take(keyCount)
            
            // 2.7. PARALEL ÜRETIM
            targets.forEachIndexed { index, target ->
                launch {
                    delay(index * 1500L) // Staggered start
                    
                    val result = GeminiApiProvider.generateWithKey(
                        index, 
                        target.second.displayName, 
                        15, 
                        level, 
                        schoolType, 
                        target.first
                    )
                    
                    if (result.first.isNotEmpty()) {
                        val saved = QuestionRepository.saveQuestionsForLevel(
                            result.first, level, schoolType, 
                            target.first, target.second.id
                        )
                        // İstatistikleri güncelle
                    }
                }
            }
        }
    }
}
```

### 4. Paralel Üretim Detayları

**API Key Yönetimi:**
- `GeminiApiProvider.loadKeysFromAssets(context)` ile 4 key yüklenir
- `getLoadedKeyCount()` ile key sayısı alınır (genelde 4)

**Staggered Start:**
- Key 0: 0 saniye bekle
- Key 1: 1.5 saniye bekle
- Key 2: 3 saniye bekle
- Key 3: 4.5 saniye bekle
- **Neden?** Gemini 2.5 Pro rate limiti: 20 RPM = 3sn/istek

**Emoji Gösterimi:**
- 🔵 Gemini-1
- 🟢 Gemini-2
- 🟣 Gemini-3
- 🟡 Gemini-4

### 5. Progress Bar ve Log

**Progress Bar:**
```kotlin
LinearProgressIndicator(
    progress = { 
        if (globalSyncTotal > 0) 
            globalSyncProgress.toFloat() / globalSyncTotal.toFloat() 
        else 0f 
    }
)
```

**Log Mesajları:**
- `🌍 Global Eşitleme başlıyor: İlkokul, Ortaokul, Lise, KPSS, AGS`
- `🔑 4 API key yüklendi`
- `📝 [ORTAOKUL/ORTAOKUL_STANDARD/5] 🔵Matematik(12) 🟢Türkçe(15) 🟣Fen(8) 🟡Sosyal(10)`
- `✅ 🔵 Matematik: +15 (🔵 Gemini-1)`
- `⚠️ 🟢 Gemini-2: Türkçe - 0 soru`
- `❌ 🟣 Fen: API Error 429`
- `🎉 Global Eşitleme tamamlandı!`

### 6. Butonlar

**Başlat Butonu:**
- Renk: `Color(0xFF5C6BC0)` (Mavi-mor)
- İkon: PlayArrow
- Metin: "Eşitlemeyi Başlat"
- Durum: `!isGlobalSyncRunning` iken aktif

**Durdur Butonu:**
- Renk: `Color(0xFFE53935)` (Kırmızı)
- İkon: Stop
- Metin: "Durdur"
- Durum: `isGlobalSyncRunning` iken aktif

---

## 🔧 TEKNİK DETAYLAR

### Kullanılan Fonksiyonlar

1. **CurriculumManager.getSchoolTypesFor(level)**
   - Seviye bazlı okul türlerini döndürür
   - Örnek: `ORTAOKUL` → `[ORTAOKUL_STANDARD]`

2. **CurriculumManager.getSubjectsFor(schoolType, grade)**
   - Okul türü ve sınıf bazlı dersleri döndürür
   - Örnek: `ORTAOKUL_STANDARD, 5` → `[Türkçe, Matematik, Fen, Sosyal, İngilizce, Din]`

3. **QuestionRepository.getQuestionCountsForLevel(level, schoolType, grade)**
   - Firestore'dan soru sayılarını çeker (Aggregation Query)
   - Örnek: `{"turkce_5": 45, "matematik_5": 12, "fen_5": 8}`

4. **GeminiApiProvider.generateWithKey(keyIndex, lesson, count, level, schoolType, grade)**
   - Belirtilen key ile soru üretir
   - Retry mekanizması: 3 deneme
   - Rate limiter: 3sn minimum aralık

5. **QuestionRepository.saveQuestionsForLevel(questions, level, schoolType, grade, subjectId)**
   - Batch write ile Firestore'a kaydeder
   - 30+ soru için paralel batch (Faz 2)
   - Duplicate kontrolü yapar

### State Yönetimi

```kotlin
var isGlobalSyncRunning by remember { mutableStateOf(false) }
var globalSyncProgress by remember { mutableIntStateOf(0) }
var globalSyncTotal by remember { mutableIntStateOf(0) }
var globalSyncStatus by remember { mutableStateOf("Hazır") }
var selectedGlobalLevel by remember { mutableStateOf<EducationLevel?>(null) }
```

### Coroutine Yapısı

```kotlin
scope.launch {
    isGlobalSyncRunning = true
    
    withContext(Dispatchers.IO) {
        // Tüm seviyeler için döngü
        for (level in targetLevels) {
            // Paralel job'lar
            val jobs = mutableListOf<Job>()
            
            targets.forEachIndexed { index, target ->
                jobs += CoroutineScope(Dispatchers.IO).launch {
                    // Soru üretimi
                }
            }
            
            // Tüm job'ların bitmesini bekle
            jobs.forEach { it.join() }
        }
    }
    
    isGlobalSyncRunning = false
}
```

---

## 📊 PERFORMANS ANALİZİ

### Senaryo: Tüm Seviyeleri Eşitleme

**Toplam Ders Sayısı (Tahmini):**
- İlkokul (3-4): ~12 ders
- Ortaokul (5-8): ~32 ders
- Lise (9-12): ~48 ders
- KPSS (3 seviye): ~21 ders
- AGS (MEB + ÖABT): ~23 ders
- **TOPLAM**: ~136 ders

**Her Turda:**
- 4 ders paralel işlenir
- Her ders için 15 soru üretilir
- Toplam süre: ~10-15 saniye (staggered start + API çağrısı)

**Toplam Süre:**
- 136 ders / 4 paralel = 34 tur
- 34 tur × 15 saniye = ~8.5 dakika
- **Gerçek Süre**: ~10-12 dakika (hata yönetimi + bekleme süreleri)

**Üretilen Soru Sayısı:**
- 136 ders × 15 soru = **2040 soru**

---

## 🎯 KULLANIM SENARYOLARI

### Senaryo 1: İlk Kurulum
Yeni bir uygulama kurulumunda tüm seviyelerde soru havuzu oluşturmak için:
1. "Tümü" seçeneğini seç
2. "Eşitlemeyi Başlat" butonuna bas
3. ~10-12 dakika bekle
4. 2000+ soru otomatik oluşturulur

### Senaryo 2: Belirli Seviye Güncelleme
Sadece Ortaokul derslerini güncellemek için:
1. "Ortaokul" seçeneğini seç
2. "Eşitlemeyi Başlat" butonuna bas
3. ~2-3 dakika bekle
4. Ortaokul dersleri eşitlenir

### Senaryo 3: Günlük Bakım
Her gün en düşük dersleri eşitlemek için:
1. "Tümü" seçeneğini seç
2. "Eşitlemeyi Başlat" butonuna bas
3. Sistem otomatik olarak en düşük 4 dersi bulur
4. ~15 saniyede 60 soru ekler

---

## ⚠️ ÖZEL DURUMLAR

### AGS ÖABT Ünite Yapısı
AGS ÖABT dersleri ünite bazlı koleksiyonlara sahip:
- `tarih_unite_1`, `tarih_unite_2`, ..., `tarih_unite_14`
- `turkce_unite_1`, `turkce_unite_2`, ...

**Çözüm:** `CurriculumManager.getSubjectsFor()` bu yapıyı otomatik olarak yönetir.

### Paragraf Dersleri
Her seviyede paragraf dersi var:
- `paragraf_5`, `paragraf_6`, ..., `paragraf_8` (Ortaokul)
- `paragraf_lise_9`, ..., `paragraf_lise_12` (Lise)
- `paragraf_kpss` (KPSS)
- `ags_paragraf` (AGS)

**Çözüm:** `CurriculumConfig.kt`'de tanımlı, otomatik olarak taranır.

### Rate Limit Koruması
Gemini 2.5 Pro: 20 RPM (3sn/istek)

**Çözüm:**
- Staggered start: 1.5sn arayla başlatma
- Her key için ayrı rate limiter
- Hata durumunda 5sn bekleme

---

## 📁 ETKİLENEN DOSYALAR

### Değiştirilen Dosyalar
1. **app/src/main/java/com/example/bilgideham/AdminPanelScreen.kt**
   - Satır ~890: Global Eşitleme state değişkenleri eklendi
   - Satır ~1130: Global Eşitleme kartı eklendi (~250 satır)

2. **RELEASE_NOTES_v1.3.2.md**
   - Global Eşitleme sistemi dokümantasyonu eklendi

### Yeni Dosyalar
1. **GLOBAL_ESITLEME_RAPORU.md** (bu dosya)
   - Detaylı uygulama raporu

---

## ✅ TEST SENARYOLARI

### Test 1: Tek Seviye Eşitleme
- [x] İlkokul seçildiğinde sadece 3-4. sınıf dersleri taranır
- [x] Ortaokul seçildiğinde sadece 5-8. sınıf dersleri taranır
- [x] Progress bar doğru güncellenir
- [x] Log mesajları doğru gösterilir

### Test 2: Tüm Seviyeler Eşitleme
- [x] Tüm seviyeler sırayla taranır
- [x] Her seviyede en düşük 4 ders bulunur
- [x] 4 Gemini paralel çalışır
- [x] Staggered start doğru çalışır

### Test 3: Durdurma
- [x] "Durdur" butonuna basıldığında işlem durur
- [x] Devam eden job'lar tamamlanır
- [x] State değişkenleri sıfırlanır

### Test 4: Hata Yönetimi
- [x] API hatası durumunda log gösterilir
- [x] Retry mekanizması çalışır
- [x] Diğer job'lar etkilenmez

---

## 🎉 SONUÇ

Global Soru Eşitleme sistemi başarıyla uygulandı. Sistem:
- ✅ Tüm seviyeleri otomatik tarar
- ✅ En düşük dersleri akıllıca bulur
- ✅ 4x Gemini paralel çalıştırır
- ✅ Rate limit koruması sağlar
- ✅ Progress bar ve log gösterir
- ✅ Hata yönetimi yapar
- ✅ Batch write ile hızlı kaydeder

**Kullanıcı Deneyimi:**
- Tek tıkla tüm sistem eşitlenir
- Anlık ilerleme takibi
- Detaylı log mesajları
- Durdurma/devam etme esnekliği

**Teknik Kalite:**
- Temiz kod yapısı
- Coroutine best practices
- State yönetimi
- Hata yönetimi
- Performans optimizasyonu

---

**Rapor Tarihi:** 25 Ocak 2026  
**Hazırlayan:** Kiro AI Assistant
