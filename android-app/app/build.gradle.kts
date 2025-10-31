import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("jacoco")
}

android {
    namespace = "com.summerveldhoundresort.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.summerveldhoundresort.app"
        minSdk = 25
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("MYAPP_RELEASE_STORE_FILE") ?: "../my-release-key.keystore")
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String?
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String?
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String?
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false  // Disable for now to avoid pipeline issues
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("org.mockito:mockito-android:5.8.0")

    //Added For Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage-ktx")

//    implementation("com.google.firebase:firebase-auth-ktx") // ✅ Required for Firebase.auth
    // Firebase Auth KTX
    implementation("com.google.firebase:firebase-auth-ktx")

    // Google Sign-In and Firebase Authentication dependencies
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Network dependencies for REST API integration
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image picker and camera
    implementation("com.github.dhaval2404:imagepicker:2.1")
    
    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

// Jacoco configuration for code coverage
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = false
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    
    val debugTree = fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory).include("**/*.exec"))
}