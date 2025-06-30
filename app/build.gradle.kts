import org.gradle.kotlin.dsl.implementation
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // 🔥 꼭 추가!
    id("dagger.hilt.android.plugin") // 🔥 꼭 추가!
}

android {
    namespace = "com.example.newsbara"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.newsbara"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "BASE_URL", "\"${getLocalProperty("BASE_URL")}\"")

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localFile = rootProject.file("local.properties")
    properties.load(FileInputStream(localFile))
    return properties.getProperty(key) ?: throw GradleException("Property $key not found")
}

    dependencies {
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.activity:activity-ktx:1.8.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

        implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        //implementation("com.github.lopspower:CircularProgressBar:3.1.0")
        implementation("com.github.bumptech.glide:glide:4.15.1")
        implementation("androidx.fragment:fragment-ktx:1.6.2")  // 최신 버전 확인해도 좋아
        // Hilt (DI)
        implementation("com.google.dagger:hilt-android:2.48")
        kapt("com.google.dagger:hilt-compiler:2.48")

// Hilt ViewModel
        implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
        kapt("androidx.hilt:hilt-compiler:1.0.0")

// Hilt Navigation (선택, Navigation 사용 시)
        implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")

        // OkHttp + Logging
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    }


