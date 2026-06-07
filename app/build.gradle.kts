plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.proyectoinnovacionpdm2026_gt3_grupo8"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.proyectoinnovacionpdm2026_gt3_grupo8"
        minSdk = 26 // Lineamiento estricto de la cátedra
        targetSdk = 36
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Habilita el soporte de APIs modernas
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    // UI - Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Escaneo de Códigos - ZXing (o ML Kit como opción moderna)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1") //Para compatibilidad de vectores
    // Carga de imágenes - Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Networking/Backend - Retrofit (para conectar a la nube/APIs)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Excel - Apache POI (Para exportar)
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // Inyección oficial del SDK de Firebase y Firestore [cite: 13]
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}