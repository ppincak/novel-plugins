# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This project provides Gradle plugins for Maven publishing automation and GitHub Packages integration. The repository contains two plugins:

1. **GitHub Publication Plugin** (`github.publication`) - Automates Maven publishing with Git tag creation
2. **GitHub Repository Plugin** (`github.repository`) - Configures GitHub Packages as a Maven repository

## Build Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Publish to local Maven repository for testing
./gradlew publishToMavenLocal

# Clean build artifacts
./gradlew clean
```

## Project Configuration

- **Group ID**: `com.conas.novel`
- **Version**: Managed via `gradle.properties` (currently `1.0.0`)
- **Plugin IDs**:
  - `github.publication` → `GithubPublicationPlugin`
  - `github.repository` → `GithubRepositoryPlugin`
- **Kotlin Version**: 2.2.21
- **Gradle Version**: 8.x

## Architecture

### Plugin Interdependencies

The **GitHub Publication Plugin** automatically applies the **GitHub Repository Plugin** when activated. This ensures that projects using the publication plugin have access to GitHub Packages configuration.

### Configuration Flow

1. **GithubRepositoryExtension** (src/main/kotlin/com/conas/novel/github/GithubRepositoryExtension.kt:7-19)
   - Reads configuration from `gradle.properties` (`gpr.url`, `gpr.user`, `gpr.token`, `repository`)
   - Constructs the full GitHub Packages URL by combining `url` and `repository`
   - All properties have default empty strings if not found

2. **GithubRepositoryHelper** (src/main/kotlin/com/conas/novel/github/GithubRepositoryHelper.kt:5-20)
   - Singleton object that configures Maven repositories
   - Used by both the plugin itself (for resolution) and publishing (for deployment)
   - Creates a repository named "github_package_repository" with credentials

3. **GithubRepositoryPlugin** (src/main/kotlin/com/conas/novel/github/GithubRepositoryPlugin.kt:8-28)
   - Creates the `githubRepository` extension
   - Configures repositories in `afterEvaluate` to ensure extension properties are set
   - Applies configuration to both project dependencies AND publishing repositories

4. **GithubPublicationPlugin** (src/main/kotlin/com/conas/novel/github/GithubPublicationPlugin.kt:15-52)
   - Applies `GithubRepositoryPlugin` first
   - Creates a Maven publication named "mavenJava" with project coordinates
   - Registers the `publishWithTag` task that depends on the standard `publish` task

### Publishing Workflow

The `PublishWithTagTask` (src/main/kotlin/com/conas/novel/github/PublishWithTagTask.kt:9-30) performs two Git operations sequentially:
1. Creates an annotated Git tag with format `v${version}` (e.g., `v1.0.0`)
2. Pushes the tag to the `origin` remote

**Note**: The task currently has a bug where it chains two `commandLine` calls in a single `exec` block, which may not execute both commands. The second `commandLine` call likely overwrites the first.

### Self-Hosting Bootstrap

The project uses itself for publishing (build.gradle.kts:8). The plugin is loaded from GitHub Packages via the `pluginManagement` configuration in settings.gradle.kts:3-16. This requires `gpr.user` and `gpr.token` to be set in `gradle.properties` for dependency resolution.

## Key Files

- `build.gradle.kts` - Main build configuration, defines both plugins and applies its own repository plugin
- `settings.gradle.kts` - Configures plugin resolution from GitHub Packages
- `gradle.properties` - Version management and credentials configuration (gpr.user, gpr.token, gpr.url, repository)
- `src/main/kotlin/com/conas/novel/github/` - All plugin implementation code
