group = "com.conas.novel"
version = project.findProperty("version") as String

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    // Note: the Kotlin version of all novel projects, `novel.kotlin-library` applies the Kotlin plugin by id
    runtimeOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")

    // Note: explicit versions (same as novel-dependencies), the plugin is published first and can't depend on the BOM
    testImplementation(platform("org.junit:junit-bom:6.1.3"))

    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("novelPublicationPlugin") {
            id = "novel.publication"
            implementationClass = "com.conas.novel.publication.NovelPublicationPlugin"
        }
        create("novelKotlinLibraryPlugin") {
            id = "novel.kotlin-library"
            implementationClass = "com.conas.novel.publication.NovelKotlinLibraryPlugin"
        }
    }
}

// Note: the plugin can't apply itself, so the repository is resolved the same way as `novelRepository` does
publishing {
    repositories {
        maven {
            val repoUrl = providers.gradleProperty("novel.repo.url")
                .orElse(providers.environmentVariable("NOVEL_REPO_URL"))
                .getOrElse("http://localhost:8080/private")

            name = "novel_repository"
            url = uri(repoUrl)
            isAllowInsecureProtocol = repoUrl.startsWith("http://")
            credentials {
                username = providers.gradleProperty("novel.repo.user").orElse(providers.environmentVariable("NOVEL_REPO_USER")).orNull
                password = providers.gradleProperty("novel.repo.token").orElse(providers.environmentVariable("NOVEL_REPO_TOKEN")).orNull
            }
        }
    }
}
