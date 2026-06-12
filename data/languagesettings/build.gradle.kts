import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

android {
    namespace = "input.comprehensible.data.languagesettings"
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
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(project(":common"))

    implementation(libs.ktin.core)
    implementation(libs.coroutines)
    implementation(libs.androidx.dataStore)

    testFixturesImplementation(project(":common"))
    testFixturesImplementation(libs.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(kotlin("test"))
}
