
    plugins {
        id("com.android.application")
        id("com.google.gms.google-services")
    }

android {
    namespace = "com.example.careerpilot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.careerpilot"
        minSdk = 27
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

    dependencies {

        // AndroidX and UI
        implementation("androidx.appcompat:appcompat:1.7.0")
        // Corrected version from 2.2.1 to 2.1.4
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        // Corrected version from 1.17.0 to 1.13.1
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("com.google.android.material:material:1.12.0")
        implementation("org.osmdroid:osmdroid-android:6.1.18")
        implementation("androidx.gridlayout:gridlayout:1.0.0")
        implementation("androidx.activity:activity:1.8.0")
        implementation("com.google.firebase:firebase-database:20.3.0")
        implementation("com.google.firebase:firebase-auth:22.1.2")
        implementation("com.google.firebase:firebase-analytics:21.5.0")
        implementation("androidx.preference:preference:1.2.1")
        implementation("com.google.android.gms:play-services-location:21.0.1")
        implementation("com.google.ar:impress:0.0.13")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.github.bumptech.glide:glide:4.15.1")
        implementation("com.google.mlkit:text-recognition:16.0.0")
        implementation("androidx.camera:camera-camera2:1.3.0")
        implementation("androidx.camera:camera-lifecycle:1.3.0")
        implementation("androidx.camera:camera-view:1.3.0")
        implementation("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")

    }
