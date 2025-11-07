package com.conas.novel.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.*

class PublishPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val projectVersion = project.version.toString()
        val projectGroupId = project.group.toString()

        project.plugins.withType<MavenPublishPlugin> {
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

        project.tasks.register("publishWithTag", PublishWithTagTask::class.java) {
            dependsOn("publish")
        }
    }
}