rootProject.name = "ComposeMultipleBackstackDemo"
include(":app")
include(":composemultiplebackstack")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
include(":composemultiplebackstack")
