group = "com.conas.novel"
version = project.findProperty("version") as String

plugins {
    id("github.repository") version "1.0.0"

    kotlin("jvm") version "2.2.21"

    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
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
