package com.conas.novel.github

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

class GithubPackageExtensionsTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `githubPackage configures maven repository with correct url and credentials`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        project.repositories {
            githubPackage(project) {
                url = "https://maven.pkg.github.com"
                repository = "test-repo"
                username = "test-user"
                token = "test-token"
            }
        }

        // THEN
        val repo = project.repositories.getByName("github_package_test-repo") as MavenArtifactRepository
        assertThat(repo.url.toString()).isEqualTo("https://maven.pkg.github.com/test-repo")
        assertThat(repo.credentials.username).isEqualTo("test-user")
        assertThat(repo.credentials.password).isEqualTo("test-token")
    }

    @Test
    fun `githubPackage returns existing repository when called twice with same url`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        val first = project.repositories.githubPackage(project) {
            url = "https://maven.pkg.github.com"
            repository = "test-repo"
            username = "test-user"
            token = "test-token"
        }
        val second = project.repositories.githubPackage(project) {
            url = "https://maven.pkg.github.com"
            repository = "test-repo"
            username = "test-user"
            token = "test-token"
        }

        // THEN
        assertThat(second).isSameAs(first)
        assertThat(project.repositories).hasSize(1)
    }

    @Test
    fun `githubPackage can be called independently in resolution and publishing blocks`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        // WHEN
        project.repositories {
            githubPackage(project) {
                url = "https://maven.pkg.github.com"
                repository = "deps-repo"
                username = "read-user"
                token = "read-token"
            }
        }
        project.configure<PublishingExtension> {
            repositories {
                githubPackage(project) {
                    url = "https://maven.pkg.github.com"
                    repository = "publish-repo"
                    username = "write-user"
                    token = "write-token"
                }
            }
        }

        // THEN
        val resolutionRepo = project.repositories.getByName("github_package_deps-repo") as MavenArtifactRepository
        assertThat(resolutionRepo.url.toString())
            .isEqualTo("https://maven.pkg.github.com/deps-repo")
        assertThat(resolutionRepo.credentials.username).isEqualTo("read-user")

        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publishRepo = publishing.repositories
            .getByName("github_package_publish-repo") as MavenArtifactRepository
        assertThat(publishRepo.url.toString())
            .isEqualTo("https://maven.pkg.github.com/publish-repo")
        assertThat(publishRepo.credentials.username).isEqualTo("write-user")
    }
}
