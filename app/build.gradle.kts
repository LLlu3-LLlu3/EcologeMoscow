plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ecologemoscow"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.ecologemoscow"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    
    // Location Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    
    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.0.0")
    
    // ZXing for QR code scanning
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    
    // Jsoup for HTML parsing
    implementation("org.jsoup:jsoup:1.15.3")
    
    // Picasso for image loading
    implementation("com.squareup.picasso:picasso:2.8")
    
    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}