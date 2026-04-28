import com.conas.novel.github.githubPackage

group = "com.conas.novel"
version = project.findProperty("version") as String

plugins {
    id("github.publication") version "2.0.1"

    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()

    githubPackage(project) {
        repository = "novel-scraper"
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation(platform("com.conas.novel:novel-dependencies:3.0.2"))

    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("githubPublishPlugin") {
            id = "github.publication"
            implementationClass = "com.conas.novel.github.GithubPublicationPlugin"
        }
    }
}
