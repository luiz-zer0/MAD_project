import java.util.Properties
import java.net.URI
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val firebaseSettings = Properties().apply {
    val config = rootProject.file("firebase.properties")
    if (config.exists()) config.inputStream().use { load(it) }
}
val demoFirebase = providers.gradleProperty("demoFirebase").orNull == "true"
val demoHost = providers.gradleProperty("demoHost").orElse("10.0.2.2").get()
require(demoHost in setOf("10.0.2.2", "127.0.0.1")) { "Invalid local demo host" }

fun firebaseValue(key: String) = "\"" + firebaseSettings.getProperty(key, "").replace("\\", "\\\\").replace("\"", "\\\"") + "\""

abstract class ValidateReleaseApiConfiguration : DefaultTask() {
    @get:Input
   abstract val firebaseConfigPath: Property<String>

    @TaskAction
    fun validate() {
        val configFile = File(firebaseConfigPath.get())
        require(configFile.isFile) {
            "Release builds require firebase.properties with the deployed API URL and approved Firebase project."
        }
        val settings = Properties().apply { configFile.inputStream().use { load(it) } }
        val endpoint = settings.getProperty("ML_API_BASE_URL", "").trim()
        val uri = runCatching { URI(endpoint) }.getOrNull()
        val host = uri?.host?.lowercase().orEmpty()
        require(uri?.scheme == "https" && host.isNotBlank() &&
                host !in setOf("localhost", "127.0.0.1", "10.0.2.2") &&
                host != "your-deployed-api.example") {
            "Release builds require ML_API_BASE_URL to be the verified deployed HTTPS API URL in firebase.properties."
        }
        require(settings.getProperty("FIREBASE_PROJECT_ID", "") == "lost-and-found-9671c") {
            "Release builds must use the approved Firebase project lost-and-found-9671c."
        }
    }
}

val validateReleaseApi = tasks.register<ValidateReleaseApiConfiguration>("validateReleaseApiConfiguration") {
    firebaseConfigPath.set(rootProject.file("firebase.properties").absolutePath)
}
tasks.configureEach {
    if (name == "preReleaseBuild") dependsOn(validateReleaseApi)
}

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
            if (demoFirebase || firebaseSettings.isEmpty) {
                buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "true")
                buildConfigField("String", "FIREBASE_PROJECT_ID", "\"demo-strathmore-lost-found\"")
                buildConfigField("String", "FIREBASE_APP_ID", "\"1:123456789:android:localdemo\"")
                buildConfigField("String", "FIREBASE_API_KEY", "\"demo-api-key-not-a-secret\"")
                buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"demo-strathmore-lost-found.appspot.com\"")
                buildConfigField("String", "ML_API_BASE_URL", "\"http://$demoHost:8000\"")
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
