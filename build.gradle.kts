group = "com.conas.novel"
version = project.findProperty("version") as String

plugins {
    id("github.repository") version "1.6.0"
    id("github.publication") version "1.6.0"

    kotlin("jvm") version "2.2.21"

    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
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
