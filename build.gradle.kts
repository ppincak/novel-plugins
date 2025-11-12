val projectGroup = "com.conas.novel"
val projectVersion = project.findProperty("version") as String

group = projectGroup
version = projectVersion

plugins {
    id("github.repository") version "1.0.0"

    kotlin("jvm") version "2.2.21"

    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

githubRepository {
    repository = "novel-plugins"
}

gradlePlugin {
    plugins {
        create("githubPublishPlugin") {
            id = "github.publication"
            implementationClass = "com.conas.novel.github.GithubPublicationPlugin"
        }

        create("githubRepositoryPlugin") {
            id = "github.repository"
            implementationClass = "com.conas.novel.github.GithubRepositoryPlugin"
        }
    }
}
