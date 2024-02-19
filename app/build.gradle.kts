plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
   // id(libs.plugins.kotlin.kapt.get().pluginId)
    //id(libs.plugins.dagger.hilt.get().pluginId)
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    // Apply the application plugin to add support for building a CLI application in Java.
    //id("com.google.devtools.ksp") version "2.0.0-Beta4-1.0.17"
    //id(libs.plugins.ksp.get().pluginId)
    alias(libs.plugins.ksp)
}

// When app incompatible with previous version change this value
val globalVersion = 0
// When you create huge feature(or many) release change this value
val majorVersion = 5
// When you create feature release change this value
val minorVersion = 0
// When you create fix change this value
val fixVersion = 0
// When you create quick fix from master branch change this value
val hotfixVersion = 0
// Based on current CI BUILD_NUMBER
val buildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: hotfixVersion
// Doc says: max number is 2100000000
// Do not use auto numeration when value beyond the edge
val maxSafeVersionCode = 1000000000
val calculatedVersionNumber = globalVersion * 100000 + majorVersion * 10000 + minorVersion * 1000 + fixVersion * 100 + buildNumber

android {
    namespace = "com.velord.composemultiplebackstackdemo"
    compileSdk = libs.versions.targetApi.get().toInt()

    defaultConfig {
        applicationId = "com.velord.composemultiplebackstackdemo"

        minSdk = libs.versions.minApi.get().toInt()
        targetSdk = libs.versions.targetApi.get().toInt()

        //Don"t use number greater than maxSafeVersionCode
        val isLessThanMax = calculatedVersionNumber < maxSafeVersionCode
        versionCode = if (isLessThanMax) calculatedVersionNumber else 0
        versionName = "$globalVersion.$majorVersion.$minorVersion"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        named("debug") {
            buildConfigField("Boolean", "IS_LOGGING_ENABLED", "true")
        }
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("Boolean", "IS_LOGGING_ENABLED", "false")
        }
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("develop") {
            manifestPlaceholders += mapOf()
            dimension = "environment"
            manifestPlaceholders["enableCrashReporting"] = false
            applicationIdSuffix = ".develop"
        }
        create("production") {
            manifestPlaceholders += mapOf()
            dimension = "environment"
            manifestPlaceholders["enableCrashReporting"] = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Module
    implementation(project(":multiplebackstackapplier"))
    // Templates
    implementation(libs.bundles.kotlin.module)
    implementation(libs.bundles.androidx.module)
    implementation(libs.bundles.androidx.activity)
    // Compose
    implementation(libs.bundles.compose.material.third)
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.compose.accompanist.core)
    // DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.navigation)
    implementation(libs.koin.annotation)
    ksp(libs.koin.ksp)
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
    arg("KOIN_DEFAULT_MODULE","false")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}