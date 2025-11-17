package com.conas.novel.github

import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction

abstract class ListPublicationsTask: DefaultTask() {

    @TaskAction
    fun execute() {
        val publishing = project.extensions.getByType(PublishingExtension::class.java)

        println("Registered Publications:")

        publishing.publications.forEach { publication ->
            if (publication !is MavenPublication) {
                return@forEach
            }

            println("Maven Publication: \"${publication.name}\"")
            println("    Group: ${publication.groupId}")
            println("    Artifact: ${publication.artifactId}")
            println("    Version: ${publication.version}")
            print("\n")
        }
    }
}