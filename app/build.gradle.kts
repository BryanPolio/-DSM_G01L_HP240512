import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Cargar variables desde local.properties (o gradle.properties como fallback)
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun getSecret(key: String): String {
    val value = localProperties.getProperty(key) ?: (project.findProperty(key) as String?) ?: ""
    return value.trim().removeSurrounding("\"").removeSurrounding("'")
}

android {
    namespace = "com.udb.desafio03"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.udb.desafio03"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "REST_COUNTRIES_TOKEN", "\"${getSecret("REST_COUNTRIES_TOKEN")}\"")
        buildConfigField("String", "GEONAMES_USER", "\"${getSecret("GEONAMES_USER")}\"")
        buildConfigField("String", "WEATHER_API_KEY", "\"${getSecret("WEATHER_API_KEY")}\"")

        manifestPlaceholders["MAPS_API_KEY"] = getSecret("MAPS_API_KEY")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = false
        buildConfig = true
    }

    // Estas opciones de compatibilidad evitan conflictos con el compilador de ViewBinding
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil (Images)
    implementation("io.coil-kt:coil:2.5.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}
