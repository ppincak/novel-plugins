# Novel Gradle Plugin

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Plugins

### GitHub Publication Plugin (`com.conas.novel.github.publication`)

Automates Maven publishing with Git tag creation.

```kotlin
plugins {
    id("com.conas.novel.github.publication") version "1.0.0"
}
```

**Task**: `publishWithTag` - Publishes artifacts and creates a Git tag (`v${version}`)

### GitHub Repository Plugin (`com.conas.novel.github.repository`)

Configures GitHub Packages as a Maven repository.

```kotlin
plugins {
    id("com.conas.novel.github.repository") version "1.0.0"
}

githubRepository {
    // Configuration via gradle.properties:
    // gpr.user, gpr.token, gpr.url, repository
}
```

## Building

```bash
./gradlew build
./gradlew publishToMavenLocal
```

## Requirements

- Gradle 8.x
- Kotlin 2.2.21
