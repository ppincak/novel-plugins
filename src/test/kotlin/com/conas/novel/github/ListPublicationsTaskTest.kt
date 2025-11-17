package com.conas.novel.github

import com.conas.novel.ResourceUtils
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class ListPublicationsTaskTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `task handles multiple publications`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        project.pluginManager.apply("maven-publish")

        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        publishing.publications.create("publication1", MavenPublication::class.java).apply {
            groupId = "com.conas.novel"
            artifactId = "artifact1"
            version = "1.0.0"
        }

        publishing.publications.create("publication2", MavenPublication::class.java).apply {
            groupId = "com.conas.novel"
            artifactId = "artifact2"
            version = "2.0.0"
        }

        val task = project.tasks.register("listPublications", ListPublicationsTask::class.java).get()
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            // WHEN
            task.execute()

            // THEN
            assertThat(outputStream.toString())
                .containsIgnoringNewLines(ResourceUtils.getResourceAsString("./expected_publications.txt"))
        } finally {
            System.setOut(originalOut)
        }
    }
}