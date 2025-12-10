rootProject.name = "backend"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // Ktor 3 artifacts may be published to JetBrains Space
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}
