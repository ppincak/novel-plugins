package com.conas.novel.github

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Action
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TagAndPushTaskTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `task creates correct git tag format`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        project.version = "1.2.3"

        val mockExecOperations = mockk<ExecOperations>()
        val mockExecSpec = mockk<ExecSpec>(relaxed = true)
        val mockExecResult = mockk<ExecResult>()

        val actionSlots = mutableListOf<Action<ExecSpec>>()
        every {
            mockExecOperations.exec(capture(actionSlots))
        } returns mockExecResult

        val task = project.tasks.register(
            "tagWithGit",
            TagAndPushTask::class.java,
            mockExecOperations
        ).get()
        task.project.version = "1.0.0"

        // WHEN
        task.execute()

        // THEN
        verify(exactly = 2) { mockExecOperations.exec(any<Action<ExecSpec>>()) }
        assertThat(actionSlots).hasSize(2)

        actionSlots[0].execute(mockExecSpec)
        verify {
            mockExecSpec.commandLine("git", "tag", "-a", "v1.0.0", "-m", "Release v1.0.0")
        }

        actionSlots[1].execute(mockExecSpec)
        verify {
            mockExecSpec.commandLine("git", "push", "origin", "v1.0.0")
        }
    }

    @Test
    fun `task can be registered in project`() {
        // GIVEN
        val project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .build()

        // WHEN
        val task = project.tasks.register("testPublishWithTag", TagAndPushTask::class.java)

        // THEN
        assertThat(task.get()).isNotNull
        assertThat(task.get().name).isEqualTo("testPublishWithTag")
        assertThat(task.get()).isInstanceOf(TagAndPushTask::class.java)
    }
}
