# CLAUDE.md

## CI Requirements

The following Gradle tasks must ALWAYS pass on every branch before pushing:

- `./gradlew detekt` — Kotlin static analysis
- `./gradlew lint` — Android lint checks
- `./gradlew assemble` — Project compilation
- `./gradlew :app:testDebugUnitTest` — Unit tests

Additionally, `./gradlew koverMarkdownSnapshotDebug` must always run successfully before every commit to keep coverage snapshots up to date.

There are subagent hooks set up to run these checks once the main Claude Code agent is done. If the subagents return any errors relating to these, they must always be resolved and must never be ignored.

## Architecture

- **Layer boundaries**: ViewModels must never directly depend on Repositories. Always go through UseCases.
- **Error handling**: Suspend function error handling (try/catch with CancellationException re-throw) belongs in UseCases, not ViewModels. ViewModels should delegate to UseCases without wrapping calls in try/catch.
- **Offline-first**: Room DB is the source of truth. Remote data sources sync to local DB.
- **Dependency injection**: Uses KTin with `Singleton`/`InjectedSingleton` patterns.

## Testing

- Integration tests use `ComprehensibleInputTestRule` and `ComprehensibleInputTestScope` with the Robot pattern.
- Tests run on Robolectric with Compose UI testing.
- New code must be covered by tests.

## Code Style

- Detekt config: `config/detekt/detekt.yml` (maxIssues: 0, maxLineLength: 140)
- Follow existing patterns in the codebase.
