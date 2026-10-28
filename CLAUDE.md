# CLAUDE.md

Gradle plugin for Maven publishing automation against the novel Maven repository (Reposilite, see `novel-dev`).

## Build Commands

```bash
./gradlew build
./gradlew test
./gradlew clean
```

## Plugins

- **`novel.publication`** — Automates Maven publishing with Git tag creation (`publishWithTag`, `tagAndPush` tasks); applies `MavenPublishPlugin` internally

Repository configuration is done via the `novelRepository(project) {}` extension function on `RepositoryHandler`, callable in both `repositories {}` and `publishing { repositories {} }` blocks. Settings come from `novel.repo.url|user|token` properties or `NOVEL_REPO_URL|USER|TOKEN` environment variables.

## Project Configuration

- **Group ID**: `com.conas.novel`
- **Version**: Managed via `gradle.properties`
- **Plugin IDs**: `novel.publication`
- **Test Framework**: JUnit 5 with MockK and AssertJ

