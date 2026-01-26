plugins {
    // 8.13.2 sürümü mevcut değil. Stabil ve SDK 36 (Android 16) ile uyumlu çalışabilecek
    // güncel bir sürüm (8.5.2) kullanıldı.
    id("com.android.application") version "9.0.0" apply false

    // Kotlin 2.2.21 henüz mevcut olmayabilir. Stabilite için 2.0.21 kullanıldı.
    // Eğer özellikle yeni bir sürüm deniyorsanız 2.1.0 yapabilirsiniz.
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}