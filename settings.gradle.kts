pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComprehensibleInput"
include(":app")
include(":common")
include(":commontest")

// Modules that live in nested directories are declared with flat project names
// and an explicit projectDir. Including them hierarchically (e.g. ":data:account")
// makes Gradle synthesise empty container projects (":data", ":feature", ":test")
// that have no build script yet are still configured on every build. Using flat
// names avoids those empty projects and the configuration overhead they add.
include(":data-account")
project(":data-account").projectDir = file("data/account")
include(":data-languagesettings")
project(":data-languagesettings").projectDir = file("data/languagesettings")
include(":data-textadventure")
project(":data-textadventure").projectDir = file("data/textadventure")
include(":feature-account")
project(":feature-account").projectDir = file("feature/account")
include(":feature-home")
project(":feature-home").projectDir = file("feature/home")
include(":feature-textadventure")
project(":feature-textadventure").projectDir = file("feature/textadventure")
include(":test-account")
project(":test-account").projectDir = file("test/account")
include(":test-textadventure")
project(":test-textadventure").projectDir = file("test/textadventure")

include(":backend")
include(":textadventuremodels")
