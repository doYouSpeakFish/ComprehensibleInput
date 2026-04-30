import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kover)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.app.distribution)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("input.comprehensible.kover-markdown-report")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
val hasKeystore = keystorePropertiesFile.exists()
if (hasKeystore) {
    FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
}

val localPropertiesFile: File = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { localProperties.load(it) }
}
val backendApiKey = localProperties.getProperty("backendApiKey")
    ?: System.getenv("BACKEND_API_KEY") ?: ""
val prBackendBaseUrl = providers.gradleProperty("prBackendBaseUrl").orNull.orEmpty()
val prNumber = providers.gradleProperty("prNumber").orNull?.toIntOrNull() ?: 0

android {
    namespace = "input.comprehensible"
    compileSdk = 36

    defaultConfig {
        applicationId = "in.comprehensible"
        minSdk = 24
        targetSdk = 36
        versionCode = 9
        buildConfigField("String", "BACKEND_BASE_URL", "\"https://comprehensibleinput-844851864443.europe-west1.run.app\"")
        versionName = "0.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("production") {
            dimension = "environment"
        }
        create("pr") {
            dimension = "environment"
            versionNameSuffix = "-pr-$prNumber"
            buildConfigField("String", "BACKEND_BASE_URL", "\"$prBackendBaseUrl\"")
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
        debug {
            buildConfigField("String", "BACKEND_API_KEY", "\"$backendApiKey\"")
        }
        release {
            buildConfigField("String", "BACKEND_API_KEY", "\"$backendApiKey\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
            firebaseAppDistribution {
                artifactType = "APK"
                testersFile = "./testers.txt"
                serviceCredentialsFile = "./firebase-app-distribution-key.json"
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
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/INDEX.LIST"
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
    sourceSets {
        getByName("test").assets.srcDir("$projectDir/schemas")
        getByName("debug").assets.srcDirs(files("$projectDir/schemas"))
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

@OptIn(ExperimentalRoborazziApi::class)
roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("input.comprehensible")
        includePrivatePreviews = false
        testerQualifiedClassName = "input.comprehensible.PreviewScreenshotTester"
        robolectricConfig = mapOf(
            "sdk" to "[34]",
            "qualifiers" to "\"w360dp-h640dp-mdpi\"",
            "application" to "android.app.Application::class",
        )
    }
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "input.comprehensible.data.stories.sources",
                    "input.comprehensible.data.languages.sources",
                    "input.comprehensible.data.textadventures.sources.remote",
                    "input.comprehensible.di",
                )
                classes(
                    "input.comprehensible.App",
                    "input.comprehensible.MainActivity",
                    "input.comprehensible.data.AppDb*",
                    "input.comprehensible.BuildConfig",
                    "input.comprehensible.*.BuildConfig",
                    "comprehensible.test.BuildConfig",
                    "input.comprehensible.ComposableSingletons*",
                    "input.comprehensible.data.AppDb_Impl",
                    "input.comprehensible.data.languages.LanguagesDao_Impl",
                    "input.comprehensible.data.stories.StoriesDao_Impl",
                    "input.comprehensible.data.AppDb_Impl*",
                    "input.comprehensible.*.ComposableSingletons*",
                )
                annotatedBy(
                    "input.comprehensible.util.DefaultPreview",
                    "androidx.compose.ui.tooling.preview.Preview",
                )
                androidGeneratedClasses()
            }
        }
    }
}

dependencies {
    implementation(project(":textadventuremodels"))

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
    implementation(libs.coroutines)
    implementation(libs.timber)
    implementation(libs.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.aboutLibraries.core)
    implementation(libs.aboutLibraries.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.dataStore)
    implementation(libs.bundles.androidx.room)
    implementation(libs.ktin.core)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.roborazzi.core)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit)
    testImplementation(libs.roborazzi.compose.preview)
    testImplementation(libs.composable.preview.scanner)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.ktin.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.core)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

