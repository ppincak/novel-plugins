package com.conas.novel.publication

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.attributes.Category
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class NovelKotlinLibraryPluginTest {

    companion object {
        @JvmStatic
        fun platforms(): List<Arguments> = listOf(
            Arguments.of(null, emptyList<String>()),
            Arguments.of(
                "com.conas.novel:novel-dependencies:1.1.0, com.conas.novel:scraper-bom:1.0.0",
                listOf("com.conas.novel:novel-dependencies:1.1.0", "com.conas.novel:scraper-bom:1.0.0")
            )
        )
    }

    @TempDir
    lateinit var testProjectDir: File

    @ParameterizedTest
    @MethodSource("platforms")
    fun `configures a kotlin library module`(platformsProperty: String?, expectedPlatforms: List<String>) {
        // GIVEN
        val project = ProjectBuilder.builder().withProjectDir(testProjectDir).build()
        platformsProperty?.let { project.extensions.extraProperties[PLATFORMS_PROPERTY] = it }

        // WHEN
        project.pluginManager.apply(NovelKotlinLibraryPlugin::class.java)

        // THEN
        assertThat(project.pluginManager.hasPlugin("java-library")).isTrue()
        assertThat(project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")).isTrue()

        val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain
        assertThat(toolchain.languageVersion.get()).isEqualTo(JavaLanguageVersion.of(JAVA_VERSION))

        val repositoryNames = project.repositories.withType(MavenArtifactRepository::class.java).map { it.name }
        assertThat(repositoryNames).contains("MavenRepo", DEFAULT_REPOSITORY_NAME)

        val test = project.tasks.withType(Test::class.java).getByName("test")
        assertThat(test.options).isInstanceOf(JUnitPlatformOptions::class.java)

        assertThat(platforms(project)).containsExactlyElementsOf(expectedPlatforms)
        assertThat(coordinates(project, "implementation"))
            .contains("org.apache.logging.log4j:log4j-core", "org.apache.logging.log4j:log4j-slf4j-impl")
        assertThat(coordinates(project, "testImplementation"))
            .contains("org.assertj:assertj-core", "io.mockk:mockk", "org.junit.jupiter:junit-jupiter-params")
    }

    private fun platforms(project: Project): List<String> =
        project.configurations.getByName("implementation").dependencies
            .filterIsInstance<ModuleDependency>()
            .filter { it.attributes.getAttribute(Category.CATEGORY_ATTRIBUTE)?.name == Category.REGULAR_PLATFORM }
            .map { "${it.group}:${it.name}:${it.version}" }

    private fun coordinates(project: Project, configuration: String): List<String> =
        project.configurations.getByName(configuration).dependencies.map { "${it.group}:${it.name}" }
}
