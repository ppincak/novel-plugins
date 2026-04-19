# version 1.7
- added standalone `tagAndPush` task that creates and pushes the `v${version}` git tag without triggering `publish` (use when you only need to tag a release separately from publishing)

# version 1.6
- skip `mavenJava` publication when an existing publication with the same `artifactId` already exists (avoids conflict with `java-gradle-plugin`'s `pluginMaven`)

# version 1.5
- added `java-platform` support to `GithubPublicationPlugin` — publishes from `javaPlatform` component when the `java-platform` plugin is applied

# version 1.4
- skipped due to incorrect `run`

# version 1.3
- updated Gradle to 9.4.1
- resolved deprecations
- fixed `TagAndPushTask` double exec issue

# version 1.2
- added `listPublications` task

# version 1.1
- skipped due to error while publishing

# version 1.0
- initial implementation of GithubPublish and GithubRepositoryPlugin