rootProject.name = "novel-gradle-plugin"

pluginManagement {
    repositories {
        maven {
            name = "github_package_repository"
            url = uri("https://maven.pkg.github.com/ppincak/novel-plugins")

            credentials {
                username = providers.gradleProperty("gpr.user").get()
                password = providers.gradleProperty("gpr.token").get()
            }
        }
        gradlePluginPortal()
    }
}
