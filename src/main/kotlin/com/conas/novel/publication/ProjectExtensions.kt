package com.conas.novel.publication

import org.gradle.api.Project

internal fun Project.stringProperty(key: String): String = findProperty(key) as? String ?: ""

/**
 * Resolves a setting from the Gradle property [propertyKey], falling back to the environment variable [envKey]
 */
internal fun Project.setting(propertyKey: String, envKey: String): String =
    stringProperty(propertyKey).ifEmpty { providers.environmentVariable(envKey).orNull ?: "" }
