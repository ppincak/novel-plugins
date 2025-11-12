package com.conas.novel.github

import org.gradle.api.Project
import java.net.URI
import javax.inject.Inject

open class GithubRepositoryExtension @Inject constructor(
    private val project: Project
) {

    open var url: String = project.findProperty("gpr.url")?.toString() ?: ""
    open var repository: String = project.findProperty("repository")?.toString() ?: ""
    open var username: String = project.findProperty("gpr.user")?.toString() ?: ""
    open var token: String = project.findProperty("gpr.token")?.toString() ?: ""

    fun uri(): URI {
        return project.uri("${url}/${repository}")
    }
}
