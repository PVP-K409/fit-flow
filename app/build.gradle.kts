plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.ksp)
}

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
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders += mapOf()
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "FitFlow Debug"
        }

        getByName("release") {
            manifestPlaceholders += mapOf()
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            manifestPlaceholders["appName"] = "FitFlow"
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
    buildToolsVersion = "34.0.0"
    androidComponents {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.add("META-INF/**")
        }
    }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.firebase.firestore.ktx)
    ksp(libs.hilt.android.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

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
}
