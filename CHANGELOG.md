# version 2.0.0
- replaced `githubRepository {}` shared extension with a `githubPackage(project)` extension function on `RepositoryHandler` — call it directly in `repositories {}` for dependency resolution and in `publishing { repositories {} }` for publishing, each configured independently
- `gpr.url`, `gpr.user`, `gpr.token` are loaded automatically from project properties as defaults and can be overridden per call
- removed `github.repository` plugin entirely; `github.publication` now applies `MavenPublishPlugin` directly; repository configuration is now explicit
- removed redundant `plugins.withType<MavenPublishPlugin>` wrapper in `GithubPublicationPlugin`; `afterEvaluate` is now called directly after applying the plugin
- `listPublications` and `tagAndPush` tasks now use Gradle's logger instead of `print`/`println` so output respects `--quiet`

# version 1.7.0
- added standalone `tagAndPush` task that creates and pushes the `v${version}` git tag without triggering `publish` (use when you only need to tag a release separately from publishing)

# version 1.6.0
- skip `mavenJava` publication when an existing publication with the same `artifactId` already exists (avoids conflict with `java-gradle-plugin`'s `pluginMaven`)

# version 1.5.0
- added `java-platform` support to `GithubPublicationPlugin` — publishes from `javaPlatform` component when the `java-platform` plugin is applied

# version 1.4.0
- skipped due to incorrect `run`

# version 1.3.0
- updated Gradle to 9.4.1
- resolved deprecations
- fixed `TagAndPushTask` double exec issue

# version 1.2.0
- added `listPublications` task

# version 1.1.0
- skipped due to error while publishing

# version 1.0.0
- initial implementation of GithubPublish and GithubRepositoryPlugin