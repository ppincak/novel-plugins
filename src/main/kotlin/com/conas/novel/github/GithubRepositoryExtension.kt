package com.conas.novel.github

import org.gradle.api.Project
import javax.inject.Inject

open class GithubRepositoryExtension @Inject constructor(
    project: Project
) {

    var url: String = project.findProperty("gpr.url")?.toString() ?: ""
    var repository: String = project.findProperty("repository")?.toString() ?: ""
    var username: String = project.findProperty("gpr.user")?.toString() ?: ""
    var token: String = project.findProperty("gpr.token")?.toString() ?: ""
}
