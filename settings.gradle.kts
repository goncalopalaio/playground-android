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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Playground"
include(":app")
include(":module:api:logger")
include(":module:api:device")
include(":module:api:space")
include(":module:logger")
include(":module:common")
include(":domain")
include(":module:data")
include(":module:device")
include(":module:space")
