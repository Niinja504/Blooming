plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "proyecto.expotecnica.blooming"
    compileSdk = 34

    defaultConfig {
        applicationId = "proyecto.expotecnica.blooming"
        minSdk = 26
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-auth:21.0.3")

    // Play services auth
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // AndroidX and other dependencies
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.1")
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.play.services.cast.framework)
    implementation(libs.filament.android)
    testImplementation(libs.junit)
    implementation("com.oracle.database.jdbc:ojdbc6:11.2.0.4")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Mail dependencies
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    // Android Keystore
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    // Glide for image handling
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase App Check
    implementation("com.google.firebase:firebase-appcheck:16.0.0")
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.0.0")
    implementation ("com.google.firebase:firebase-bom:28.4.0")

    implementation ("com.google.firebase:firebase-firestore:24.0.0")

    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    implementation ("com.google.android.material:material:1.7.0")

    implementation("com.airbnb.android:lottie:6.4.1")

    implementation ("com.github.bumptech.glide:glide:4.13.0")

    implementation("com.google.android.libraries.places:places:3.5.0")

    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation ("com.google.android.material:material:1.4.0")
}
