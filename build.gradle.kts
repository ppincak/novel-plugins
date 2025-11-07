plugins {
    kotlin("jvm") version "2.2.21"

    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.conas.novel"
version = project.findProperty("version") as String

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

gradlePlugin {
    plugins {
        create("publishPlugin") {
            id = "com.conas.novel.publish"
            implementationClass = "com.conas.novel.github.PublishPlugin"
        }

        create("githubRepositoryPlugin") {
            id = "com.conas.novel.github"
            implementationClass = "com.conas.novel.github.GithubRepositoryPlugin"
        }
    }
}
