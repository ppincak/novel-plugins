package com.conas.novel.github

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

class GithubPublicationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        project.afterEvaluate {
            val projectVersion = project.version.toString()
            val projectGroupId = project.group.toString()
            val isJavaPlatform = project.plugins.hasPlugin(JavaPlatformPlugin::class.java)

            val componentName = if (isJavaPlatform) "javaPlatform" else "java"

            project.configure<PublishingExtension> {
                if (shouldSkipPublication(project, publications)) {
                    project.logger.lifecycle(
                        "Skipping 'mavenJava' publication for '{}': publication already managed externally",
                        project.name
                    )
                    return@configure
                }

                publications {
                    register("mavenJava", MavenPublication::class) {
                        groupId = projectGroupId
                        artifactId = project.name
                        version = projectVersion

                        from(project.components[componentName])
                    }
                }
            }
            registerPublication(project)
        }
        registerTasks(project)
    }

    private fun shouldSkipPublication(project: Project, publications: PublicationContainer): Boolean {
        if (project.plugins.hasPlugin("java-gradle-plugin")) {
            return true
        }
        return publications.withType(MavenPublication::class.java).any { it.artifactId == project.name }
    }

    private fun registerPublication(project: Project) {
        // Note: automatically add 'github' package to publishing repositories
        if (project.propertyExists("gpr.url") && project.propertyExists("repository")) {
            project.configure<PublishingExtension> {
                repositories.githubPackage(project)
            }
        } else {
            project.logger.lifecycle(
                "Missing 'gpr.url' or 'repository' property for {} — skipping publication repository definition.",
                project.name
            )
        }
    }

    private fun registerTasks(project: Project) {
        project.tasks.register("listPublications", ListPublicationsTask::class.java)
        project.tasks.register("publishWithTag", TagAndPushTask::class.java) {
            dependsOn("publish")
        }
        project.tasks.register("tagAndPush", TagAndPushTask::class.java)
    }
}