plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.test.sky_delivery_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.test.sky_delivery_app"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson 转换器
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 日志拦截器
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //地图
    /*implementation("com.amap.api:map2d:latest.integration")*/
    implementation("com.amap.api:search:9.7.0")
    /*implementation("com.amap.api:location:latest.integration")*/
    implementation("com.amap.api:navi-3dmap:latest.integration")

    implementation("io.coil-kt:coil-compose:2.4.0")
}