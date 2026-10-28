package com.conas.novel.publication

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

class NovelPublicationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        registerPublicationRepository(project)
        registerTasks(project)

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
        }
    }

    private fun shouldSkipPublication(project: Project, publications: PublicationContainer): Boolean {
        if (project.plugins.hasPlugin("java-gradle-plugin")) {
            return true
        }
        return publications.withType(MavenPublication::class.java).any { it.artifactId == project.name }
    }

    private fun registerPublicationRepository(project: Project) {
        // Note: automatically add the novel repository to publishing repositories
        project.configure<PublishingExtension> {
            repositories.novelRepository(project)
        }
    }

    private fun registerTasks(project: Project) {
        project.tasks.register("listPublications", ListPublicationsTask::class.java)
        // repo wide operation should not be done per `subproject`
        if (project == project.rootProject) {
            project.tasks.register("publishWithTag", TagAndPushTask::class.java) {
                dependsOn("publish")
            }
            project.tasks.register("tagAndPush", TagAndPushTask::class.java)
        }
    }
}