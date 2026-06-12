plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("input.comprehensible.kover-coverage-report")
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
    // `./gradlew :backend:test -Popenapi.update` rewrites the committed OpenAPI
    // contract (backend/openapi/openapi.yaml) instead of asserting against it.
    if (project.hasProperty("openapi.update")) {
        systemProperty("openapi.update", "true")
    }
}

// Enables Ktor's OpenAPI compiler plugin, which generates the route metadata
// (request/response body schemas inferred from kotlinx.serialization types, plus
// KDoc annotations) that OpenApiSpecGenerationTest renders into the committed
// backend/openapi/openapi.json contract used by the CI backwards-compatibility check.
ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
    }
}

koverCoverageReport {
    sourceProjects(project(":textadventuremodels"))
}

kover {
    dependencies {
        kover(project(":textadventuremodels"))
    }
}

dependencies {
    implementation(project(":textadventuremodels"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.api.key)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.routing.openapi)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.koog.agents.jvm)
    implementation(libs.serialization.json)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.jbcrypt)
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.junit.platform.suite.api)
    testRuntimeOnly(libs.junit.platform.suite.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.h2)
}
