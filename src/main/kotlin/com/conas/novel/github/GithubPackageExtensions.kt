package com.conas.novel.github

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

fun RepositoryHandler.githubPackage(
    project: Project,
    configure: GithubPackageConfig.() -> Unit = {}
): MavenArtifactRepository {

    val config = GithubPackageConfig().apply {
        url = project.stringProperty("gpr.url")
        repository = project.stringProperty("repository")
        username = project.stringProperty("gpr.user")
        token = project.stringProperty("gpr.token")
    }.apply(configure)

    val repoUrl = "${config.url}/${config.repository}"

    withType(MavenArtifactRepository::class.java)
        .find { repository -> repository.url.toString() == repoUrl }
        ?.let {
            project.logger.lifecycle(
                "Skipping duplicate GitHub Package repository '{}': already registered at '{}'",
                config.repository, repoUrl
            )
            return it
        }

    return maven {
        name = config.name.ifEmpty { "github_package_${config.repository}" }
        url = URI(repoUrl)
        credentials {
            username = config.username
            password = config.token
        }
        project.logger.lifecycle(
            "Registered GitHub Package repository '{}' at '{}'",
            name, repoUrl
        )
    }
}