package com.conas.novel.github

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class GithubPublicationPluginTest {

    @TempDir
    lateinit var testProjectDir: File

    @ParameterizedTest
    @ValueSource(strings = ["java", "java-platform"])
    fun `publishes mavenJava publication for supported project types`(pluginId: String) {
        // GIVEN
        val project = createProject(pluginId)

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publication = publishing.publications.getByName("mavenJava") as MavenPublication

        assertThat(publication.groupId).isEqualTo("com.conas.novel")
        assertThat(publication.artifactId).isEqualTo(project.name)
        assertThat(publication.version).isEqualTo("1.0.0")
    }

    @Test
    fun `skips mavenJava publication when existing publication with same artifactId exists`() {
        // GIVEN
        val project = createProject("java")

        // Pre-register a publication with the same artifactId (simulates java-gradle-plugin)
        project.extensions.configure(PublishingExtension::class.java) {
            publications.create("pluginMaven", MavenPublication::class.java) {
                artifactId = project.name
            }
        }

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publicationNames = publishing.publications.map { it.name }

        assertThat(publicationNames).contains("pluginMaven")
        assertThat(publicationNames).doesNotContain("mavenJava")
    }

    @Test
    fun `skips mavenJava publication when java-gradle-plugin is applied`() {
        // GIVEN
        val project = createProject("java-gradle-plugin")

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        assertThat(publishing.publications.map { it.name }).doesNotContain("mavenJava")
    }

    @Test
    fun `auto-wires publishing repository when gpr properties are set`() {
        // GIVEN
        val project = createProject("java")
        project.extensions.extraProperties["gpr.url"] = "https://maven.pkg.github.com"
        project.extensions.extraProperties["repository"] = "test-repo"
        project.extensions.extraProperties["gpr.user"] = "test-user"
        project.extensions.extraProperties["gpr.token"] = "test-token"

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val repo = publishing.repositories.getByName("github_package_test-repo") as MavenArtifactRepository
        assertThat(repo.url.toString()).isEqualTo("https://maven.pkg.github.com/test-repo")
    }

    @Test
    fun `does not duplicate publishing repository when already explicitly configured`() {
        // GIVEN
        val project = createProject("java")
        project.extensions.extraProperties["gpr.url"] = "https://maven.pkg.github.com"
        project.extensions.extraProperties["repository"] = "test-repo"
        project.extensions.extraProperties["gpr.user"] = "test-user"
        project.extensions.extraProperties["gpr.token"] = "test-token"

        project.configure<PublishingExtension> {
            repositories.githubPackage(project)
        }

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val matchingRepos = publishing.repositories
            .withType(MavenArtifactRepository::class.java)
            .filter { it.url.toString() == "https://maven.pkg.github.com/test-repo" }
        assertThat(matchingRepos).hasSize(1)
    }

    @Test
    fun `does not add publishing repository when gpr properties are absent`() {
        // GIVEN
        val project = createProject("java")

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        assertThat(publishing.repositories).isEmpty()
    }

    private fun createProject(pluginId: String): org.gradle.api.internal.project.ProjectInternal {
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        project.pluginManager.apply(pluginId)
        project.pluginManager.apply(GithubPublicationPlugin::class.java)

        project.group = "com.conas.novel"
        project.version = "1.0.0"

        return project as org.gradle.api.internal.project.ProjectInternal
    }
}
