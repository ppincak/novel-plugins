import com.conas.novel.github.githubPackage

group = "com.conas.novel"
version = project.findProperty("version") as String

plugins {
    id("github.publication") version "2.5.0"

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
    testImplementation(platform("com.conas.novel:scraper-bom:2.0.1"))

    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.14.6")
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
