plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.3.0"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.serialization.json)
    implementation(libs.koog.agents)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
