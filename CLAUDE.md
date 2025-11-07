# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin project (`novel-gradle-plugin`) that provides custom plugins for publishing and GitHub repository management. The project is built using Kotlin and follows the Gradle plugin development conventions.

**Group ID**: `com.conas.novel`
**Version**: Defined in `gradle.properties` (currently 1.0.0)

## Build Commands

### Basic Commands
- **Build the plugin**: `./gradlew build`
- **Run tests**: `./gradlew test`
- **Clean build**: `./gradlew clean build`
- **Publish locally**: `./gradlew publishToMavenLocal`

### Testing
The project uses JUnit 5 (Jupiter) for testing. Tests are configured to run with `useJUnitPlatform()`.

## Architecture

### Plugin Structure

The project contains two main Gradle plugins:

#### 1. PublishPlugin (`com.conas.novel.publish`)
Located in `src/main/kotlin/com/conas/novel/publish/`

- **Purpose**: Automates Maven publishing with Git tag creation
- **Key Components**:
  - `PublishPlugin.kt`: Main plugin class that configures Maven publishing
  - `PublishWithTagTask.kt`: Custom task that creates and pushes Git tags
- **Behavior**:
  - Applies to projects using `MavenPublishPlugin`
  - Registers a `mavenJava` publication with project coordinates
  - Provides `publishWithTag` task that depends on `publish` and creates/pushes a Git tag in format `v${version}`
- **Git Operations**: The `PublishWithTagTask` executes two git commands:
  1. Creates an annotated tag: `git tag -a v${version} -m "Release v${version}"`
  2. Pushes to origin: `git push origin v${version}`

#### 2. GithubRepositoryPlugin (`com.conas.novel.github`)
Located in `src/main/kotlin/com/conas/novel/github/`

- **Purpose**: Configures GitHub Packages as a Maven repository
- **Key Components**:
  - `GithubRepositoryPlugin.kt`: Main plugin class
  - `GithubRepositoryExtension.kt`: DSL extension for configuration
- **Configuration**: Requires the following Gradle properties:
  - `gpr.user`: GitHub username
  - `gpr.token`: GitHub personal access token
  - `gpr.url`: Base URL for GitHub Packages
  - `repository`: Repository identifier
- **Extension Usage**: Projects can configure via `githubRepository { }` block
- **Repository Setup**: Creates a Maven repository named `github_package_repository` with GitHub credentials

### Project Configuration

- **Kotlin Version**: 2.2.21
- **Applied Plugins**: `kotlin-jvm`, `java-gradle-plugin`, `kotlin-dsl`, `maven-publish`
- **Test Framework**: JUnit 5 Platform (5.10.0)

### Package Structure
```
com.conas.novel
├── publish/          # Maven publishing with Git tagging
│   ├── PublishPlugin
│   └── PublishWithTagTask
└── github/           # GitHub Packages repository configuration
    ├── GithubRepositoryPlugin
    └── GithubRepositoryExtension
```

## Development Notes

### Version Management
The project version is read from `gradle.properties` using `project.findProperty("version")` in `build.gradle.kts`.

### Gradle Plugin Development
This project uses `java-gradle-plugin` and `kotlin-dsl` plugins, which provide the necessary infrastructure for developing Gradle plugins in Kotlin.

### Injection and Services
Both plugins use Gradle's service injection pattern (note `@Inject` constructor in `PublishWithTagTask` for `ExecOperations`).
