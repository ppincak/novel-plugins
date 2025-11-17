package com.conas.novel.github

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GithubRepositoryPluginTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `task handles multiple publications`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        project.pluginManager.apply(GithubRepositoryPlugin::class.java)

        val extension = project.extensions.getByName("githubRepository") as GithubRepositoryExtension
        extension.url = "https://maven.pkg.github.com"
        extension.repository = "test-repo"
        extension.username = "test-user"
        extension.token = "test-token"

        // WHEN
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

        // THEN
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        assertThat(publishing.repositories.names).contains("github_package_repository")

        val projectRepo = project.repositories.getByName("github_package_repository") as MavenArtifactRepository
        assertThat(projectRepo.url.toString()).isEqualTo("${extension.url}/${extension.repository}")
        assertThat(projectRepo.credentials.username).isEqualTo(extension.username)
        assertThat(projectRepo.credentials.password).isEqualTo(extension.token)
    }
}