# AGENTS.md

<!-- shared:start: generated from the novel workspace (agents/shared.md) by `task agents:sync`, do not edit -->
## Workspace

This repository is part of the novel-proofreading workspace. The sibling repositories, in dependency order:

| Repository            | Contents                                                      |
|-----------------------|---------------------------------------------------------------|
| `novel-gradle-plugin` | `novel.publication` Gradle plugin (publishing + git tags)     |
| `novel-dependencies`  | Shared dependency BOM                                         |
| `novel-scraper`       | Web novel scraper (`scraper-*` modules, `scraper-bom`)        |
| `novel-llm`           | LLM provider clients and prompts (`novel-llm-*`, `novel-llm-bom`) |
| `novel-proofreader`   | Translation / proofreading pipeline                           |
| `novel-dev`           | Local infrastructure (Reposilite Maven repository, Docker)    |

Each one builds standalone against the published versions of the others. In the workspace checkout, the root composite
build uses the local sources instead.

## Build conventions

- Kotlin/JVM, Gradle 9 with Kotlin DSL; `./gradlew build`, `./gradlew test`, `./gradlew :module:test`.
- Group IDs `com.conas.*`; the version is in `gradle.properties`.
- Publishing goes to the novel Maven repository (Reposilite, started from `novel-dev`) via the `novel.publication`
  plugin and `novelRepository(project)`. Settings come from the Gradle properties `novel.repo.url|user|token`, falling
  back to the env vars `NOVEL_REPO_URL|USER|TOKEN`, default `http://localhost:8080/private`.
- Redeploying an existing version is rejected, so bump the version before publishing again.
- `PKIX path building failed` errors on Maven Central come from Avast HTTPS scanning on the dev machine, not from the
  build.

## Testing

- JUnit 5 with AssertJ; use `mockk` for all mocking, never Mockito or other mock libraries.
- Do not duplicate tests. If two tests verify the same behavior, collapse them.
- Use parametrized tests (`@ParameterizedTest` / `@MethodSource`) when the same logic is exercised with multiple inputs.
- Structure every test body with `// GIVEN`, `// WHEN`, `// THEN` section comments.

## Coding guideline

Behavioral guidelines to reduce common LLM coding mistakes.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

### 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

### 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

### 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

### 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and
clarifying questions come before implementation rather than after mistakes.
<!-- shared:end -->

## novel-gradle-plugin

Gradle plugin for Maven publishing automation against the novel Maven repository.

- **`novel.publication`**: automates Maven publishing with git tag creation (`publishWithTag`, `tagAndPush` tasks) and
  applies `MavenPublishPlugin` internally.
- Repository configuration goes through the `novelRepository(project) {}` extension on `RepositoryHandler`, usable in
  both `repositories {}` and `publishing { repositories {} }`.
- The plugin's own build doesn't apply the plugin (bootstrap loop); it publishes with plain `maven-publish`.
- Group ID `com.conas.novel`, package `com.conas.novel.publication`. Tests use JUnit 5, MockK and AssertJ.
