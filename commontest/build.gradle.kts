import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
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
    api(libs.roborazzi.compose.preview)
    api(libs.composable.preview.scanner)
    api(libs.ktin.test)
    api(libs.junit)
    api(libs.coroutines.test)
    api(libs.robolectric)
    api(libs.timber)

    // Cucumber harness for behavioural UI tests. Exposed as `api` so the consuming modules'
    // test source sets (which hold the feature files and step definitions) pick it up through
    // their existing `testImplementation(project(":commontest"))` dependency.
    api(libs.cucumber.java)
    api(libs.cucumber.junit.platform.engine)
    api(libs.junit.platform.launcher)
}
