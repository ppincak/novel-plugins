package com.conas.novel.github

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType

class GithubPublicationPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(GithubRepositoryPlugin::class.java)
        val githubExtension = try {
            project.extensions.getByType(
                GithubRepositoryExtension::class.java
            )
        } catch (e: UnknownDomainObjectException) {
            project.extensions.create(
                "githubRepository",
                GithubRepositoryExtension::class.java)
        }

        project.plugins.withType<MavenPublishPlugin> {
            project.afterEvaluate {
                val projectVersion = project.version.toString()
                val projectGroupId = project.group.toString()

                project.configure<PublishingExtension> {
                    publications {
                        register("mavenJava", MavenPublication::class) {
                            groupId = projectGroupId
                            artifactId = project.name
                            version = projectVersion

                            from(project.components["java"])
                        }
                    }
                }
            }
        }

        project.tasks.register("publishWithTag", PublishWithTagTask::class.java) {
            dependsOn("publish")
        }
    }
}