buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.spotless.plugin.gradle)
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.daggerHilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

subprojects {
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
        }
    }
}