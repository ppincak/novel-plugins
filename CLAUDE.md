# CLAUDE.md

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Build Commands

```bash
./gradlew build
./gradlew test
./gradlew clean
```

## Plugins

- **`github.publication`** — Automates Maven publishing with Git tag creation (`publishWithTag`, `tagAndPush` tasks); applies `MavenPublishPlugin` internally

Repository configuration is done via the `githubPackage(project) {}` extension function on `RepositoryHandler`, callable in both `repositories {}` and `publishing { repositories {} }` blocks.

## Project Configuration

- **Group ID**: `com.conas.novel`
- **Version**: Managed via `gradle.properties`
- **Plugin IDs**: `github.publication`
- **Test Framework**: JUnit 5 with MockK and AssertJ

