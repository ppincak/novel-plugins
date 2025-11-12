package com.conas.novel.github

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.*

class GithubRepositoryPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        val githubExtension = project.extensions.create(
            "githubRepository",
            GithubRepositoryExtension::class.java
        )

        project.plugins.withType<MavenPublishPlugin> {
            project.afterEvaluate {
                project.repositories {
                    GithubRepositoryHelper.githubRepository(this, githubExtension)
                }

                project.configure<PublishingExtension> {
                    repositories {
                        GithubRepositoryHelper.githubRepository(this, githubExtension)
                    }
                }
            }
        }
    }
}