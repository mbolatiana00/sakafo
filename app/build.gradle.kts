plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sakafo"
    compileSdk = 36  

    defaultConfig {
        applicationId = "com.example.sakafo"
        minSdk = 24
        targetSdk = 36  // ✅ Aligné sur compileSdk
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

    // ✅ CORRECTION MAJEURE : compilerOptions dans android {}, pas en dehors
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

// ✅ Supprimer le bloc kotlin {} dupliqué (déjà dans android {})

dependencies {
    implementation(libs.androidx.compose.foundation)
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ✅ Compose BOM (décommenter pour gérer les versions automatiquement)
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.animation)

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

  
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")  // ✅ Version stable

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")  // ✅ Version stable

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")  // ✅ Version stable
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")  // ✅ Version stable

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")  // ✅ Version stable (3.0.0 n'existe pas)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")  // ✅ Version stable
    implementation("io.coil-kt:coil-compose:2.7.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Google Play Services Auth
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // LibPhoneNumber
    implementation("io.michaelrocks:libphonenumber-android:8.13.52")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.11.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}