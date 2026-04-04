# CLAUDE.md

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Build Commands

```bash
./gradlew build
./gradlew test
./gradlew clean
```

## Plugins

- **`github.publication`** — Automates Maven publishing with Git tag creation (`publishWithTag` task)
- **`github.repository`** — Configures GitHub Packages as a Maven repository for resolution and deployment

## Project Configuration

- **Group ID**: `com.conas.novel`
- **Version**: Managed via `gradle.properties`
- **Plugin IDs**: `github.publication`, `github.repository`
- **Test Framework**: JUnit 5 with MockK and AssertJ

