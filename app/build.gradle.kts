import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.hilt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kover)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "input.comprehensible.data.stories.sources",
                    "input.comprehensible.data.languages.sources",
                    "input.comprehensible.di",
                    "hilt_aggregated_deps",
                    "dagger.hilt.internal.aggregatedroot.codegen",
                )
                classes(
                    "input.comprehensible.App",
                    "input.comprehensible.MainActivity",
                    "input.comprehensible.data.AppDb",
                    "input.comprehensible.BuildConfig",
                    "input.comprehensible.*.BuildConfig",
                    "comprehensible.test.BuildConfig",
                    "input.comprehensible.ComposableSingletons*",
                    "input.comprehensible.data.AppDb_Impl",
                    "input.comprehensible.data.languages.LanguagesDao_Impl",
                    "input.comprehensible.data.stories.StoriesDao_Impl",
                    "input.comprehensible.data.AppDb_Impl*",
                    "input.comprehensible.*.ComposableSingletons*",
                    "input.comprehensible.*.*_Factory",
                    "input.comprehensible.*.*_Provide*",
                    "input.comprehensible.*.*_HiltModules*",
                    "input.comprehensible.Hilt_*",
                    "input.comprehensible.*.Hilt_*",
                )
                annotatedBy(
                    "input.comprehensible.util.DefaultPreview",
                    "androidx.compose.ui.tooling.preview.Preview",
                    "dagger.Module",
                )
            }
        }
    }
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
val hasKeystore = keystorePropertiesFile.exists()
if (hasKeystore) {
    FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
}

android {
    namespace = "input.comprehensible"
    compileSdk = 36

    defaultConfig {
        applicationId = "in.comprehensible"
        minSdk = 24
        targetSdk = 36
        versionCode = 7
        versionName = "0.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (hasKeystore) {
            create("release") {
                storePassword = keystoreProperties["storePassword"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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

roborazzi {
    @OptIn(ExperimentalRoborazziApi::class)
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("input.comprehensible.ui")
        robolectricConfig = mapOf(
            "sdk" to "[34]",
            "qualifiers" to "\"w360dp-h640dp-mdpi\"",
        )
    }
}

dependencies {
    implementation(project(":test"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.icons)
    implementation(libs.hilt)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(libs.coroutines)
    implementation(libs.timber)
    implementation(libs.serialization.json)
    implementation(libs.aboutLibraries.core)
    implementation(libs.aboutLibraries.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.dataStore)
    implementation(libs.bundles.androidx.room)

    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.roborazzi.core)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit)
    testImplementation(libs.roborazzi.compose.preview.scanner.support)
    testImplementation(libs.composable.preview.scanner)

    kspTest(libs.hilt.compiler)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
