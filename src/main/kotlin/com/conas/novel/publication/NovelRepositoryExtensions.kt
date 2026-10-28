package com.conas.novel.publication

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

// Note: local Reposilite started from `novel-dev` (see novel-dev/README.md)
const val DEFAULT_REPOSITORY_URL = "http://localhost:8080/private"
const val DEFAULT_REPOSITORY_NAME = "novel_repository"

fun RepositoryHandler.novelRepository(
    project: Project,
    configure: NovelRepositoryConfig.() -> Unit = {}
): MavenArtifactRepository {

    val config = NovelRepositoryConfig().apply {
        url = project.setting("novel.repo.url", "NOVEL_REPO_URL").ifEmpty { DEFAULT_REPOSITORY_URL }
        username = project.setting("novel.repo.user", "NOVEL_REPO_USER")
        token = project.setting("novel.repo.token", "NOVEL_REPO_TOKEN")
    }.apply(configure)

    val repoUrl = config.url.trimEnd('/')

    withType(MavenArtifactRepository::class.java)
        .find { repository -> repository.url.toString().trimEnd('/') == repoUrl }
        ?.let {
            project.logger.lifecycle(
                "Skipping duplicate novel repository '{}': already registered as '{}'",
                repoUrl, it.name
            )
            return it
        }

    return maven {
        name = config.name.ifEmpty { DEFAULT_REPOSITORY_NAME }
        url = URI(repoUrl)
        isAllowInsecureProtocol = repoUrl.startsWith("http://")
        credentials {
            username = config.username
            password = config.token
        }
        project.logger.lifecycle("Registered novel repository '{}' at '{}'", name, repoUrl)
    }
}
