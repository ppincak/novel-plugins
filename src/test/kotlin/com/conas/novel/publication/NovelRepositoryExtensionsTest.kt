package com.conas.novel.publication

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class NovelRepositoryExtensionsTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `novelRepository configures maven repository with correct url and credentials`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        project.repositories {
            novelRepository(project) {
                url = "https://repo.example.com/private"
                username = "test-user"
                token = "test-token"
            }
        }

        // THEN
        val repo = project.repositories.getByName(DEFAULT_REPOSITORY_NAME) as MavenArtifactRepository
        assertThat(repo.url.toString()).isEqualTo("https://repo.example.com/private")
        assertThat(repo.isAllowInsecureProtocol).isFalse()
        assertThat(repo.credentials.username).isEqualTo("test-user")
        assertThat(repo.credentials.password).isEqualTo("test-token")
    }

    @Test
    fun `novelRepository resolves settings from novel repo properties`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()
        project.extensions.extraProperties["novel.repo.url"] = "https://repo.example.com/private"
        project.extensions.extraProperties["novel.repo.user"] = "property-user"
        project.extensions.extraProperties["novel.repo.token"] = "property-token"

        // WHEN
        val repo = project.repositories.novelRepository(project)

        // THEN
        assertThat(repo.url.toString()).isEqualTo("https://repo.example.com/private")
        assertThat(repo.credentials.username).isEqualTo("property-user")
        assertThat(repo.credentials.password).isEqualTo("property-token")
    }

    @Test
    fun `novelRepository defaults to local reposilite and allows http`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        val repo = project.repositories.novelRepository(project) {
            // Note: explicit empty url, so the test doesn't depend on NOVEL_REPO_URL of the machine
            url = ""
        }

        // THEN
        assertThat(repo.url.toString()).isEqualTo(DEFAULT_REPOSITORY_URL)
        assertThat(repo.isAllowInsecureProtocol).isTrue()
    }

    @Test
    fun `novelRepository returns existing repository when called twice with same url`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        val first = project.repositories.novelRepository(project) {
            url = "https://repo.example.com/private"
        }
        val second = project.repositories.novelRepository(project) {
            url = "https://repo.example.com/private/"
        }

        // THEN
        assertThat(second).isSameAs(first)
        assertThat(project.repositories).hasSize(1)
    }

    @Test
    fun `novelRepository can be called independently in resolution and publishing blocks`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        // WHEN
        project.repositories {
            novelRepository(project) {
                url = "https://repo.example.com/releases"
                username = "read-user"
                token = "read-token"
            }
        }
        project.configure<PublishingExtension> {
            repositories {
                novelRepository(project) {
                    url = "https://repo.example.com/private"
                    username = "write-user"
                    token = "write-token"
                }
            }
        }

        // THEN
        val resolutionRepo = project.repositories.getByName(DEFAULT_REPOSITORY_NAME) as MavenArtifactRepository
        assertThat(resolutionRepo.url.toString()).isEqualTo("https://repo.example.com/releases")
        assertThat(resolutionRepo.credentials.username).isEqualTo("read-user")

        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publishRepo = publishing.repositories.getByName(DEFAULT_REPOSITORY_NAME) as MavenArtifactRepository
        assertThat(publishRepo.url.toString()).isEqualTo("https://repo.example.com/private")
        assertThat(publishRepo.credentials.username).isEqualTo("write-user")
    }
}
