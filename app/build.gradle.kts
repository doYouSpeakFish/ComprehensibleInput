import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kover)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.app.distribution)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("input.comprehensible.kover-coverage-report")
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
val enableAllFeatureFlags = providers.gradleProperty("enableAllFeatureFlags")
    .orNull
    ?.toBooleanStrictOrNull()
    ?: false

android {
    namespace = "input.comprehensible"
    compileSdk = 36

    defaultConfig {
        buildConfigField("boolean", "AI_TEXT_ADVENTURES_ENABLED", "false")
        buildConfigField("boolean", "ACCOUNT_MANAGEMENT_ENABLED", "false")
        applicationId = "in.comprehensible"
        minSdk = 24
        targetSdk = 36
        versionCode = 9
        buildConfigField("String", "BACKEND_BASE_URL", "\"https://api.languagethis.com\"")
        versionName = "0.6.0"
        if (prBackendBaseUrl.isNotEmpty()) {
            buildConfigField("String", "BACKEND_BASE_URL", "\"$prBackendBaseUrl\"")
        }
        if (prNumber > 0) {
            versionNameSuffix = "-pr-$prNumber"
        }
        resValue("bool", "is_cleartext_traffic_enabled", "${prBackendBaseUrl.isNotEmpty()}")

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
        debug {
            buildConfigField("String", "BACKEND_API_KEY", "\"$backendApiKey\"")
            buildConfigField("boolean", "AI_TEXT_ADVENTURES_ENABLED", "true")
            buildConfigField("boolean", "ACCOUNT_MANAGEMENT_ENABLED", "true")
            // PR builds are distributed as debug so logs are available for diagnosis.
            firebaseAppDistribution {
                artifactType = "APK"
                testersFile = "./testers.txt"
                serviceCredentialsFile = "./firebase-app-distribution-key.json"
                releaseNotes = (project.findProperty("firebaseReleaseNotes") as String?)?.trim()
            }
        }
        release {
            buildConfigField("String", "BACKEND_API_KEY", "\"$backendApiKey\"")
            buildConfigField("boolean", "AI_TEXT_ADVENTURES_ENABLED", "$enableAllFeatureFlags")
            buildConfigField("boolean", "ACCOUNT_MANAGEMENT_ENABLED", "$enableAllFeatureFlags")
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
                releaseNotes = (project.findProperty("firebaseReleaseNotes") as String?)?.trim()
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
        resValues = true
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
                it.maxHeapSize = "4g"
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
        // Only scan packages owned by this module (plus shared previews from :common).
        // The account previews live in :feature:account, which generates their screenshots
        // itself, so they are intentionally excluded here to avoid duplicate screenshots.
        packages = listOf(
            "input.comprehensible.ui.components",
            "input.comprehensible.ui.settings.settings",
            "input.comprehensible.ui.settings.softwarelicences",
            "input.comprehensible.ui.storylist",
            "input.comprehensible.ui.storyreader",
            "input.comprehensible.ui.textadventure",
        )
        includePrivatePreviews = false
        testerQualifiedClassName = "input.comprehensible.PreviewScreenshotTester"
        robolectricConfig = mapOf(
            "sdk" to "[34]",
            "qualifiers" to "\"w360dp-h640dp-mdpi\"",
            "application" to "android.app.Application::class",
        )
    }
}

// AGP 9.2.0 built_in_kotlinc mode stores compiled Kotlin classes outside the JAR that unit
// tests receive on their classpath. This wires the directory in explicitly.
// afterEvaluate is required because AGP registers these tasks late in the config phase.
// Track: https://issuetracker.google.com/issues/388556987
val builtInKotlincClasses = layout.buildDirectory.dir(
    "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"
)
afterEvaluate {
    tasks.named(
        "compileDebugUnitTestKotlin",
        org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile::class.java,
    ) {
        dependsOn("compileDebugKotlin")
        libraries.from(builtInKotlincClasses)
    }
    tasks.named("testDebugUnitTest", Test::class.java) {
        classpath += files(builtInKotlincClasses)
    }
}

koverCoverageReport {
    sourceProjects(
        project(":common"),
        project(":data:account"),
        project(":feature:account"),
        project(":textadventuremodels"),
    )
}

kover {
    dependencies {
        kover(project(":common"))
        kover(project(":data:account"))
        kover(project(":feature:account"))
        kover(project(":textadventuremodels"))
    }
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
    implementation(project(":common"))
    implementation(project(":feature:account"))
    implementation(project(":data:account"))
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
    implementation(libs.aboutLibraries.core)
    implementation(libs.aboutLibraries.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.bundles.androidx.room)
    implementation(libs.ktin.core)
    implementation(libs.androidx.dataStore)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    ksp(libs.androidx.room.compiler)

    testImplementation(project(":commontest"))
    testImplementation(testFixtures(project(":feature:account")))
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
