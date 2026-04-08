package com.conas.novel.github

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GithubPublicationPluginTest {

    @TempDir
    lateinit var testProjectDir: File

    @ParameterizedTest
    @ValueSource(strings = ["java", "java-platform"])
    fun `publishes mavenJava publication for supported project types`(pluginId: String) {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        project.pluginManager.apply(pluginId)
        project.pluginManager.apply(GithubPublicationPlugin::class.java)

        val extension = project.extensions.getByName("githubRepository") as GithubRepositoryExtension
        extension.url = "https://maven.pkg.github.com"
        extension.repository = "test-repo"
        extension.username = "test-user"
        extension.token = "test-token"

        project.group = "com.conas.novel"
        project.version = "1.0.0"

        // WHEN
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publication = publishing.publications.getByName("mavenJava") as MavenPublication

        assertThat(publication.groupId).isEqualTo("com.conas.novel")
        assertThat(publication.artifactId).isEqualTo(project.name)
        assertThat(publication.version).isEqualTo("1.0.0")
    }
}
