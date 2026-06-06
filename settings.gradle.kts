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
include(":data:account")
include(":data:languagesettings")
include(":data:textadventure")
include(":feature:account")
include(":feature:home")
include(":feature:textadventure")
include(":test:account")
include(":test:textadventure")

include(":backend")
include(":textadventuremodels")
