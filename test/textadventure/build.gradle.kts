import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "input.comprehensible.test.textadventure"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
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
}

dependencies {
    // Depends on both data modules so Room's KSP processor can resolve the cross-module entities and
    // DAOs the test @Database wires together: AdventureEntity / MessageEntity / SentenceEntity and
    // the AdventureLocalDataSource DAO from :data-textadventure, and the UserLocalDataSource DAO from
    // :data-account. :common (which owns UserEntity, referenced by the Adventure foreign key) is
    // depended on directly because the data modules expose it only as `implementation`, so it is not
    // transitive. This is the same set of project dependencies :app declares for its AppDb.
    implementation(project(":common"))
    implementation(project(":data-textadventure"))
    implementation(project(":data-account"))

    // Compose is on the classpath even though this module defines no UI: :common's compiled classes
    // reference Compose types, and KSP2 has to resolve those references while indexing :common to
    // read UserEntity. Without Compose on the classpath KSP cannot resolve the entity and fails with
    // "[MissingType]: Element 'UserEntity' references a type that is not present". :app processes the
    // same entities successfully precisely because it already has Compose on its classpath.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    // Exposed as `api` so consuming test source sets can build the database with
    // Room.inMemoryDatabaseBuilder<TextAdventureTestDatabase>() through their dependency on this
    // module.
    api(libs.bundles.androidx.room)
    implementation(libs.ktin.core)
    implementation(libs.coroutines)

    ksp(libs.androidx.room.compiler)
}
