plugins {
//    id("com.android.application")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.fashionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.husn.fashionapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 9
        versionName = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.all {
    resolutionStrategy {
//        force 'androidx.test.espresso:espresso-core:3.5.0',
    }
}
dependencies {
    // Jetpack Compose UI
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation(libs.play.services.measurement.api)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Coil for Image Loading in Compose
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.2")

    // OkHttp for Networking
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // JSON Parsing with org.json
    implementation("org.json:json:20210307")

    // Coroutines for async tasks (if you're using them)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Compose Runtime for managing state in Compose
    implementation("androidx.compose.runtime:runtime:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")/**/

    // Testing Dependencies (optional)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")

    testImplementation("junit:junit:4.13.2")

    // AndroidX Test for Android-specific unit tests (optional but recommended for Android testing)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // BouncyCastle JSSE provider for OkHttp
    implementation("org.bouncycastle:bctls-jdk15on:1.70")

    // Conscrypt for better TLS support in OkHttp
    implementation("org.conscrypt:conscrypt-android:2.5.2")

    // OpenJSSE for modern TLS support in older Android versions
    implementation("org.openjsse:openjsse:1.1.7")

    implementation("org.conscrypt:conscrypt-android:2.5.2")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("androidx.activity:activity-compose:1.7.2")
//    implementation("androidx.compose.ui:ui:1.4.3")
//    implementation("androidx.compose.material:material:1.4.3")
//    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
////    implementation "io.coil-kt:coil-compose:2.4.0" // For image loading
////    implementation "com.squareup.okhttp3:okhttp:4.11.0" // For network requests
////    implementation "org.json:json:20230227" // For JSON manipulation
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
}