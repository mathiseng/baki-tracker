// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath (libs.secrets.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.googleGmsGoogleServices) apply false
}