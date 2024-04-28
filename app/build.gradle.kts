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

        buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"${keystoreProperties["google.serverClientId"]}\"")

        resValue("string", "google_maps_key", "\"${keystoreProperties["google.mapsApiKey"]}\"")
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
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.firebase.appcheck.debug)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.connect.client)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)


    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation (libs.androidx.credentials)
    implementation (libs.androidx.credentials.play.services.auth)
    implementation (libs.googleid)

    implementation (libs.compose.pay.button)
    implementation(libs.play.services.wallet)

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.work.runtime)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.core.splashscreen)

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
    implementation(libs.play.services.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.ui.storage)

    implementation(libs.animated.navigation.bar)
    implementation(libs.kotlinx.serialization.json)
}