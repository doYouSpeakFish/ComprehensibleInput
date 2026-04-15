plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.shadow.jar)
    id("input.comprehensible.kover-markdown-report")
}

group = "input.comprehensible"
version = "0.0.1"

application {
    mainClass = "input.comprehensible.backend.ApplicationKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "input.comprehensible.backend.ApplicationKt"
    }
}

kotlin {
    jvmToolchain(25)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit)
    testImplementation(libs.ktor.server.test.host)
}
