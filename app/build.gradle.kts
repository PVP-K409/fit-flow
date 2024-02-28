import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.gms)
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.github.k409.fitflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.k409.fitflow"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "CLIENT_ID", "\"${keystoreProperties["github.clientId"]}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${keystoreProperties["github.clientSecret"]}\"")
        buildConfigField("String", "REDIRECT_URI", "\"${keystoreProperties["github.redirectUri"]}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("fitflow-debug") {
            storeFile = rootProject.file(keystoreProperties["debug.storeFile"] as String)
            storePassword = keystoreProperties["debug.storePassword"] as String
            keyAlias = keystoreProperties["debug.keyAlias"] as String
            keyPassword = keystoreProperties["debug.keyPassword"] as String
        }
        create("fitflow-release") {
            storeFile = rootProject.file(keystoreProperties["release.storeFile"] as String)
            storePassword = keystoreProperties["release.storePassword"] as String
            keyAlias = keystoreProperties["release.keyAlias"] as String
            keyPassword = keystoreProperties["release.keyPassword"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders += mapOf()
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "FitFlow Debug"
            signingConfig = signingConfigs.getByName("fitflow-debug")
        }

        getByName("release") {
            manifestPlaceholders += mapOf()
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            manifestPlaceholders["appName"] = "FitFlow"
            signingConfig = signingConfigs.getByName("fitflow-release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = "20"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    androidComponents {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.add("META-INF/**")
        }
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.activity:activity:1.6.0-alpha05")
}