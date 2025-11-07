package com.conas.novel.github

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import java.net.URI

class GithubRepositoryPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val githubExtension = project.extensions.create(
            "githubRepository",
            GithubRepositoryExtension::class.java
        )

        project.repositories {
            maven {
                name = "github_package_repository"
                url = URI.create("${githubExtension.url}/${githubExtension.repository}")

                credentials {
                    username = githubExtension.username
                    password = githubExtension.token
                }
            }
        }
    }
}