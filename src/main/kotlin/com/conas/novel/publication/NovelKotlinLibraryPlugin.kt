package com.conas.novel.publication

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

const val JAVA_VERSION = 25
const val PLATFORMS_PROPERTY = "novel.platforms"

/**
 * Kotlin/JVM library module of the novel projects: Kotlin, the Java toolchain, the Maven Central and novel repositories,
 * JUnit and the dependencies every module shares.
 *
 * The BOMs a project aligns its versions with are listed in the Gradle property `novel.platforms`
 * (comma separated coordinates, e.g. in `gradle.properties`) and added to every module as platforms.
 */
class NovelKotlinLibraryPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
        project.pluginManager.apply("org.jetbrains.kotlin.jvm")

        project.repositories.mavenCentral()
        project.repositories.novelRepository(project)

        // Note: the same as `kotlin { jvmToolchain(25) }`, the Kotlin plugin picks up the Java toolchain
        project.extensions.getByType(JavaPluginExtension::class.java).toolchain {
            languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
        }

        project.tasks.withType(Test::class.java).configureEach {
            useJUnitPlatform()
        }

        val dependencies = project.dependencies
        platforms(project).forEach { dependencies.add("implementation", dependencies.platform(it)) }

        dependencies.add("implementation", "org.apache.logging.log4j:log4j-core")
        dependencies.add("implementation", "org.apache.logging.log4j:log4j-slf4j-impl")

        dependencies.add("testImplementation", "org.assertj:assertj-core")
        dependencies.add("testImplementation", "io.mockk:mockk")
        dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-params")
        dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-test")
    }

    private fun platforms(project: Project): List<String> =
        project.stringProperty(PLATFORMS_PROPERTY).split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
