import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    application
}

application {
    mainClass.set("input.comprehensible.storygen.app.StoryGeneratorAppKt")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.coroutines.core)
    implementation(libs.serialization.json)
    implementation(libs.koog.agents)

    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.test)
}

