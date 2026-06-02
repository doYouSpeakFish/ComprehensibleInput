import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "input.comprehensible.commontest"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui.test.junit4)
    api(libs.roborazzi.core)
    api(libs.roborazzi.compose)
    api(libs.roborazzi.junit)
    api(libs.ktin.test)
    api(libs.junit)
    api(libs.coroutines.test)
    api(libs.robolectric)
    api(libs.timber)
}
