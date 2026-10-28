# Changelog

## Release 1.0.0
> **Version reset:** the projects moved from GitHub Packages to the novel Maven repository (Reposilite, see `novel-dev`)
> and all versions were reset to `1.0.0`. Earlier releases (and their changelog entries) only exist in the git history and
> GitHub Packages.

- `novel.publication` plugin: applies `maven-publish` and registers a `mavenJava` publication from the `java` or `javaPlatform` component (skipped when a publication with the same `artifactId` already exists, e.g. from `java-gradle-plugin`)
- `novelRepository(project) {}` extension on `RepositoryHandler` for resolution and publishing; settings resolve from `novel.repo.url|user|token` properties, then `NOVEL_REPO_URL|USER|TOKEN` environment variables, the url defaults to `http://localhost:8080/private`; `http://` urls are allowed
- the novel repository is registered as a publishing repository automatically
- `publishWithTag`, `tagAndPush` (root project only) and `listPublications` tasks
