pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // Türkiye / DNS / proxy durumlarında bazen fark ettiriyor:
        maven(url = "https://dl.google.com/dl/android/maven2/")
        maven(url = "https://maven.google.com")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://dl.google.com/dl/android/maven2/")
        maven(url = "https://maven.google.com")
    }
}

rootProject.name = "bilgidehammmm"
include(":app")
