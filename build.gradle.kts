// Top-level build file where you can add configuration options common to all sub-projects/modules.
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.detekt)
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    detektPlugins(libs.findLibrary("detekt-formatting").get())
    detektPlugins(libs.findLibrary("detekt-compose-rules").get())
}

detekt {
    toolVersion = libs.findVersion("detekt").get().requiredVersion
    buildUponDefaultConfig = true
    autoCorrect = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    basePath = rootDir.path
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
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
        jvmTarget = "17"
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