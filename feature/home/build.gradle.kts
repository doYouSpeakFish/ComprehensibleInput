import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kover)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
}

android {
    namespace = "input.comprehensible.feature.home"
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
    testFixtures {
        enable = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
            }
        }
    }
}

@OptIn(ExperimentalRoborazziApi::class)
roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("input.comprehensible.ui.home")
        includePrivatePreviews = false
        testerQualifiedClassName = "input.comprehensible.PreviewScreenshotTester"
        robolectricConfig = mapOf(
            "sdk" to "[34]",
            "qualifiers" to "\"w360dp-h640dp-mdpi\"",
            "application" to "android.app.Application::class",
        )
    }
}

dependencies {
    implementation(project(":common"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.serialization.json)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testFixturesImplementation(platform(libs.androidx.compose.bom))
    testFixturesImplementation(libs.androidx.ui.test.junit4)
    testFixturesImplementation(project(":common"))

    testImplementation(project(":commontest"))
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.roborazzi.core)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit)
    testImplementation(libs.roborazzi.compose.preview)
    testImplementation(libs.composable.preview.scanner)
}
