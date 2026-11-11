# Novel Gradle Plugin

Gradle plugin for Maven publishing automation against the novel Maven repository (a Reposilite instance, see `novel-dev`).

## Usage

Apply the plugin (resolved from the novel repository, see `pluginManagement` below):

```kotlin
plugins {
    id("novel.publication") version "1.1.0"
}
```

The plugin registers the novel repository as a publishing repository automatically. For dependency resolution add it
with the `novelRepository` extension function:

```kotlin
repositories {
    mavenCentral()
    novelRepository(project)
}
```

### Kotlin library modules

`novel.kotlin-library` holds the build configuration every Kotlin module of the novel projects shares: `java-library`
and Kotlin/JVM (the Kotlin version comes with the plugin), Java toolchain 25, Maven Central and the novel repository,
JUnit Platform, Log4j and the test libraries (AssertJ, MockK, JUnit params, kotlin-test).

Each module applies the plugins it needs; declare them once with their version in the root `build.gradle.kts`:

```kotlin
// build.gradle.kts (root)
plugins {
    id("novel.kotlin-library") version "1.1.0" apply false
    id("novel.publication") version "1.1.0" apply false
}

// <module>/build.gradle.kts
plugins {
    id("novel.kotlin-library")
    id("novel.publication")  // left out for modules that aren't published
}
```

The BOMs the modules align their versions with are listed once per project in `gradle.properties`, comma separated:

```properties
novel.platforms=com.conas.novel:novel-dependencies:1.1.0,com.conas.novel:scraper-bom:1.0.0
```

### Repository settings

Each setting is resolved from the Gradle property first, then from the environment variable:

| Gradle property    | Environment variable | Default                         |
|--------------------|----------------------|---------------------------------|
| `novel.repo.url`   | `NOVEL_REPO_URL`     | `http://localhost:8080/private` |
| `novel.repo.user`  | `NOVEL_REPO_USER`    |                                 |
| `novel.repo.token` | `NOVEL_REPO_TOKEN`   |                                 |

Set them once as user environment variables or in `~/.gradle/gradle.properties` (never commit the token).
`http://` URLs are allowed (`isAllowInsecureProtocol`), as the default repository runs locally.

Any of them can be overridden per call:

```kotlin
novelRepository(project) {
    url = "https://repo.example.com/private"
    token = System.getenv("CUSTOM_TOKEN") ?: ""
}
```

The plugin itself has to be resolved before it is applied, so `settings.gradle.kts` resolves the same settings manually:

```kotlin
pluginManagement {
    repositories {
        maven {
            val repoUrl = providers.gradleProperty("novel.repo.url")
                .orElse(providers.environmentVariable("NOVEL_REPO_URL"))
                .getOrElse("http://localhost:8080/private")

            name = "novel_repository"
            url = uri(repoUrl)
            isAllowInsecureProtocol = repoUrl.startsWith("http://")
            credentials {
                username = providers.gradleProperty("novel.repo.user").orElse(providers.environmentVariable("NOVEL_REPO_USER")).orNull
                password = providers.gradleProperty("novel.repo.token").orElse(providers.environmentVariable("NOVEL_REPO_TOKEN")).orNull
            }
        }
        gradlePluginPortal()
    }
}
```

### Features

- Supports both `java` and `java-platform` projects (publishes from `java` or `javaPlatform` component accordingly)
- Automatically skips publication when a conflicting publication already exists (e.g. from `java-gradle-plugin`)

### Tasks

- `publishWithTag` - Publishes to the novel repository and creates Git tag `v${version}`
- `tagAndPush` - Creates and pushes Git tag `v${version}` without publishing
- `listPublications` - Lists all registered Maven publications

## Development

```bash
./gradlew build                # Build the project
./gradlew test                 # Run tests
./gradlew publish              # Publish to the novel repository (start it first: `task dev:up` in the workspace)
```

**Requirements**: Gradle 9.x, Kotlin 2.2.21
