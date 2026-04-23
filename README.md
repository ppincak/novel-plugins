# Novel Gradle Plugin

Gradle plugins for Maven publishing automation and GitHub Packages integration.

## Usage

Apply the plugin:

```kotlin
plugins {
    id("github.publication") version "2.1.0"
}
```

Configure repositories using the `githubPackage` extension function:

```kotlin
repositories {
    githubPackage(project) {
        repository = "your-deps-repo"
    }
}

publishing {
    repositories {
        githubPackage(project) {
            repository = "your-publish-repo"
        }
    }
}
```

### Global Gradle Properties

`gpr.url`, `gpr.user`, and `gpr.token` are recommended global properties. Set them once in `~/.gradle/gradle.properties` (user home, not committed to source control) so they are shared across all projects:

```properties
gpr.url=https://maven.pkg.github.com/your-org
gpr.user=your-github-username
gpr.token=your-github-token
```

Any of these can be overridden per call if needed:

```kotlin
githubPackage(project) {
    repository = "your-repo"
    token = System.getenv("CUSTOM_TOKEN") ?: ""
}
```

### Features

- Supports both `java` and `java-platform` projects (publishes from `java` or `javaPlatform` component accordingly)
- Automatically skips publication when a conflicting publication already exists (e.g. from `java-gradle-plugin`)

### Tasks

- `publishWithTag` - Publishes to GitHub Packages and creates Git tag `v${version}`
- `tagAndPush` - Creates and pushes Git tag `v${version}` without publishing
- `listPublications` - Lists all registered Maven publications

## Development

```bash
./gradlew build                # Build the project
./gradlew test                 # Run tests
./gradlew publishToMavenLocal  # Install locally
```

**Requirements**: Gradle 9.x, Kotlin 2.2.21
