# Novel Gradle Plugin

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Usage

```kotlin
plugins {
    id("com.conas.novel.github.publication") version "1.0.0"
    id("com.conas.novel.github.repository") version "1.0.0"
}

githubRepository {
    repository = "your-repo-name"
}
```

Configure credentials in `gradle.properties`:
```properties
gpr.user=username
gpr.token=github_token
gpr.url=https://maven.pkg.github.com/owner
```

### Tasks

- `publishWithTag` - Publishes to GitHub Packages and creates Git tag `v${version}`

## Development

```bash
./gradlew build                # Build the project
./gradlew test                 # Run tests
./gradlew publishToMavenLocal  # Install locally
```

**Requirements**: Gradle 8.x, Kotlin 2.2.21
