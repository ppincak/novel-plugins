package com.conas.novel.github

import org.gradle.api.artifacts.dsl.RepositoryHandler

object GithubRepositoryHelper {

    fun githubRepository(handler: RepositoryHandler, extension: GithubRepositoryExtension) {
        handler.apply {
            maven {
                name = "github_package_repository"
                url = extension.uri()

                credentials {
                    username = extension.username
                    password = extension.token
                }
            }
        }
    }
}