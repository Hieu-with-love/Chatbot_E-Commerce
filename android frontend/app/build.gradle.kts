plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "hcmute.edu.vn.chatbot_ec"
    compileSdk = 35

    defaultConfig {
        applicationId = "hcmute.edu.vn.chatbot_ec"
        minSdk = 28
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Nếu dùng LiveData hoặc ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide core library
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // Nếu dùng annotation-based API (ví dụ @GlideModule), cần thêm compiler
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation (libs.material.v1110)

}