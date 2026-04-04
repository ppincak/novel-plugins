package com.conas.novel.github

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.inject.Inject

@DisableCachingByDefault(because = "Creates and pushes git tags, not cacheable")
abstract class TagAndPushTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @TaskAction
    fun execute() {
        val releaseTag = "v${project.version}"
        val outputStream = ByteArrayOutputStream()

        try {
            exec(outputStream, "git", "tag", "-a", releaseTag, "-m", "Release $releaseTag")
            exec(outputStream, "git", "push", "origin", releaseTag)
        } finally {
            println(outputStream.toString())
        }
    }

    private fun exec(outputStream: OutputStream, vararg args: String) {
        execOperations.exec {
            errorOutput = outputStream
            standardOutput = outputStream
            commandLine(*args)
        }
    }
}