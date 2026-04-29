plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.shadow)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("input.comprehensible.kover-markdown-report")
}

group = "input.comprehensible"
version = "0.0.1"

application {
    mainClass = "input.comprehensible.backend.ApplicationKt"
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    manifest.attributes["Main-Class"] = "input.comprehensible.backend.ApplicationKt"
    mergeServiceFiles {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":textadventuremodels"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.api.key)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.koog.agents.jvm)
    implementation(libs.serialization.json)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.junit.platform.suite.api)
    testRuntimeOnly(libs.junit.platform.suite.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.h2)
}
