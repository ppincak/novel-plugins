package com.conas.novel.github

import org.gradle.api.Project

internal fun Project.stringProperty(key: String): String = findProperty(key) as? String ?: ""
internal fun Project.propertyExists(key: String): Boolean = (findProperty(key) as? String)?.isNotEmpty() ?: false
