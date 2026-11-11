# Changelog

## Release 1.1.0

- `novel.kotlin-library` plugin: the shared configuration of the Kotlin modules (`java-library`, Kotlin/JVM 2.4.21,
  Java toolchain 25, Maven Central and the novel repository, JUnit Platform, Log4j and test dependencies), plus the BOMs
  listed in the `novel.platforms` property as platforms

## Release 1.0.1

- `novelRepository`: an empty `url` set in the configure block falls back to the default url

## Release 1.0.0
> **Version reset:** the projects moved from GitHub Packages to the novel Maven repository (Reposilite, see `novel-dev`)
> and all versions were reset to `1.0.0`. Earlier releases (and their changelog entries) only exist in the git history and
> GitHub Packages.

- `novel.publication` plugin: applies `maven-publish` and registers a `mavenJava` publication from the `java` or `javaPlatform` component (skipped when a publication with the same `artifactId` already exists, e.g. from `java-gradle-plugin`)
- `novelRepository(project) {}` extension on `RepositoryHandler` for resolution and publishing; settings resolve from `novel.repo.url|user|token` properties, then `NOVEL_REPO_URL|USER|TOKEN` environment variables, the url defaults to `http://localhost:8080/private`; `http://` urls are allowed
- the novel repository is registered as a publishing repository automatically
- `publishWithTag`, `tagAndPush` (root project only) and `listPublications` tasks
