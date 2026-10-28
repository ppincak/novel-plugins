package com.conas.novel.publication

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
import org.gradle.api.internal.project.ProjectInternal

class NovelPublicationPluginTest {

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
    fun `auto-wires publishing repository from novel repo properties`() {
        // GIVEN - properties must be available before the plugin is applied, mirroring gradle.properties load order
        val project = createProject("java", mapOf(
            "novel.repo.url" to "https://repo.example.com/private",
            "novel.repo.user" to "test-user",
            "novel.repo.token" to "test-token"
        ))

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val repo = publishing.repositories.getByName(DEFAULT_REPOSITORY_NAME) as MavenArtifactRepository
        assertThat(repo.url.toString()).isEqualTo("https://repo.example.com/private")
        assertThat(repo.credentials.username).isEqualTo("test-user")
    }

    @Test
    fun `does not duplicate publishing repository when already explicitly configured`() {
        // GIVEN - properties available before apply so the plugin auto-wires the repo eagerly
        val project = createProject("java", mapOf(
            "novel.repo.url" to "https://repo.example.com/private"
        ))

        // explicit second registration — duplicate detection should suppress it
        project.configure<PublishingExtension> {
            repositories.novelRepository(project)
        }

        // WHEN
        project.evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        assertThat(publishing.repositories).hasSize(1)
    }

    @Test
    fun `tagAndPush and publishWithTag are only registered on the root project`() {
        // GIVEN
        val rootProject = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .withName("root")
            .build()

        val subProject = ProjectBuilder.builder()
            .withParent(rootProject)
            .withName("sub")
            .build()

        rootProject.pluginManager.apply("java")
        rootProject.pluginManager.apply(NovelPublicationPlugin::class.java)
        subProject.pluginManager.apply("java")
        subProject.pluginManager.apply(NovelPublicationPlugin::class.java)

        // THEN
        assertThat(rootProject.tasks.findByName("tagAndPush")).isNotNull()
        assertThat(rootProject.tasks.findByName("publishWithTag")).isNotNull()
        assertThat(subProject.tasks.findByName("tagAndPush")).isNull()
        assertThat(subProject.tasks.findByName("publishWithTag")).isNull()
    }

    private fun createProject(
        pluginId: String,
        properties: Map<String, String> = emptyMap()
    ): ProjectInternal {
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        properties.forEach { (key, value) -> project.extensions.extraProperties[key] = value }

        project.pluginManager.apply(pluginId)
        project.pluginManager.apply(NovelPublicationPlugin::class.java)

        project.group = "com.conas.novel"
        project.version = "1.0.0"

        return project as org.gradle.api.internal.project.ProjectInternal
    }
}
