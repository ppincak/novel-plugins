# Novel Gradle Plugin

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Usage

```kotlin
plugins {
    id("github.publication") version "1.6.0"
    id("github.repository") version "1.6.0"
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

### Features

- Supports both `java` and `java-platform` projects (publishes from `java` or `javaPlatform` component accordingly)
- Automatically skips publication when a conflicting publication already exists (e.g. from `java-gradle-plugin`)

### Tasks

- `publishWithTag` - Publishes to GitHub Packages and creates Git tag `v${version}`
- `listPublications` - Lists all registered Maven publications

## Development

```bash
./gradlew build                # Build the project
./gradlew test                 # Run tests
./gradlew publishToMavenLocal  # Install locally
```

**Requirements**: Gradle 9.x, Kotlin 2.2.21
