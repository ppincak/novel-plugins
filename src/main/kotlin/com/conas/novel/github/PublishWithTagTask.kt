package com.conas.novel.github

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class PublishWithTagTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @TaskAction
    fun execute() {
        val releaseTag = "v${project.version}"
        val outputStream = ByteArrayOutputStream()

        try {
            execOperations.exec {
                errorOutput = outputStream
                standardOutput = outputStream

                commandLine("git", "tag", "-a", releaseTag, "-m", "\"Release $releaseTag\"")
                commandLine("git", "push", "origin", releaseTag)
            }
        } finally {
            println(outputStream.toString())
        }
    }
}