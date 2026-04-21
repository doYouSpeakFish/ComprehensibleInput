plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kover)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("input.comprehensible.kover-markdown-report")
}

group = "input.comprehensible"
version = "0.0.1"

application {
    mainClass = "input.comprehensible.backend.ApplicationKt"
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    manifest.attributes["Main-Class"] = "input.comprehensible.backend.ApplicationKt"
    from(
        configurations.runtimeClasspath.map { classpath ->
            classpath.map(::zipTree)
        }
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
    runtimeOnly(libs.mysql.connector.j)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.h2)
}
