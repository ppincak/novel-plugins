package com.conas.novel.publication

import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Prints publication info to stdout, not worth caching")
abstract class ListPublicationsTask : DefaultTask() {

    @TaskAction
    fun execute() {
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        val publications = publishing.publications.filterIsInstance<MavenPublication>()
        logger.lifecycle(formatPublications(publications))
    }
}

internal fun formatPublications(publications: List<MavenPublication>): String = buildString {
    appendLine("Registered Publications:")
    publications.forEachIndexed { index, publication ->
        appendLine()
        appendLine("  [${index + 1}] ${publication.name}")
        appendLine("      ${publication.groupId}:${publication.artifactId}:${publication.version}")
    }
}.trimEnd()
