plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("kotlin-kapt") // Room için gerekli
}

android {
    namespace = "com.example.bilgideham"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bilgideham"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // UI - Temel Bileşenler
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")

    // --- EKLENEN KRİTİK KÜTÜPHANELER (BoxWithConstraints İÇİN ŞART) ---
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    // -----------------------------------------------------------------

    // Coil (Resim Yükleme)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-vertexai")
    implementation("com.google.firebase:firebase-analytics")

    // ML Kit & CameraX
    implementation("com.google.mlkit:text-recognition:16.0.1")
    // ✅ QR okutma (ML Kit Barcode)
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    val cameraxVersion = "1.4.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // ✅ QR üretimi (kod -> QR bitmap)
    implementation("com.google.zxing:core:3.5.3")
    dependencies {
        // Firebase BOM (tüm Firebase kütüphanelerini uyumlu tutar)
        implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

        // Firebase Authentication
        implementation("com.google.firebase:firebase-auth-ktx")

        // Firebase Firestore (zaten var olmalı)
        implementation("com.google.firebase:firebase-firestore-ktx")

        // Diğer mevcut bağımlılıklar...
    }
    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // ✅ Nearby Connections (Bluetooth/Wi-Fi üzerinden cihazlar arası bağlantı)
    implementation("com.google.android.gms:play-services-nearby:19.3.0")
}
