import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val firebaseSettings = Properties().apply {
    val config = rootProject.file("firebase.properties")
    if (config.exists()) config.inputStream().use { load(it) }
}
fun firebaseValue(key: String) = "\"" + firebaseSettings.getProperty(key, "").replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.example.app_1"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.app_1"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "false")
        listOf("FIREBASE_PROJECT_ID", "FIREBASE_APP_ID", "FIREBASE_API_KEY", "FIREBASE_STORAGE_BUCKET", "ML_API_BASE_URL").forEach {
            buildConfigField("String", it, firebaseValue(it))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            if (firebaseSettings.isEmpty) {
                buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "true")
                buildConfigField("String", "FIREBASE_PROJECT_ID", "\"demo-strathmore-lost-found\"")
                buildConfigField("String", "FIREBASE_APP_ID", "\"1:123456789:android:localdemo\"")
                buildConfigField("String", "FIREBASE_API_KEY", "\"demo-api-key-not-a-secret\"")
                buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"demo-strathmore-lost-found.appspot.com\"")
                buildConfigField("String", "ML_API_BASE_URL", "\"http://10.0.2.2:8000\"")
            }
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}