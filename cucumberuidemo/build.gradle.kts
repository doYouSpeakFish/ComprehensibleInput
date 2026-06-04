import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

android {
    namespace = "input.comprehensible.cucumberuidemo"
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)

    // JUnit 4 is only used to host the Robolectric runner; scenarios run on the
    // Cucumber JUnit Platform engine, driven programmatically by the host test.
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.junit.platform.launcher)

    debugImplementation(libs.androidx.ui.test.manifest)
}
