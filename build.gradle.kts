// Top-level build file where you can add configuration options common to all sub-projects/modules.
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.room) apply false
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose.rules)
    kover(project(":app"))
}

detekt {
    toolVersion = libs.versions.detekt.get()
    buildUponDefaultConfig = true
    autoCorrect = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    basePath = rootDir.path
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "input.comprehensible.data.stories.sources",
                    "input.comprehensible.data.languages.sources",
                    "input.comprehensible.di",
                )
                classes(
                    "input.comprehensible.App",
                    "input.comprehensible.MainActivity",
                    "input.comprehensible.data.AppDb",
                    $$"input.comprehensible.data.AppDb$Companion",
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

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        buildUponDefaultConfig = true
        autoCorrect = false
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        basePath = rootDir.path
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "21"
        setSource(files(projectDir))
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**")
        baseline.set(file("$rootDir/config/detekt/baseline.xml"))

        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
}