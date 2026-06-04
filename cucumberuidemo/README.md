# Cucumber + Robolectric Compose UI demo

A self-contained demonstration of running **Cucumber** scenarios against a **Robolectric
Compose UI** test, using the **Cucumber JUnit Platform engine** (`cucumber-junit-platform-engine`)
rather than the old JUnit 4 Cucumber runner (`@RunWith(io.cucumber.junit.Cucumber)`).

Run it with:

```bash
./gradlew :cucumberuidemo:testDebugUnitTest
```

## The problem

Compose-under-Robolectric tests rely on JUnit 4 machinery:

- `@RunWith(RobolectricTestRunner)` (or `ParameterizedRobolectricTestRunner`) sets up the
  Robolectric sandbox — the instrumenting class loader and the Android environment.
- `createComposeRule()` is a JUnit 4 `TestRule` that sets the content up and tears it down
  *around each test*.

The Cucumber JUnit Platform engine runs scenarios as its own `TestEngine`. It **does not honour
JUnit 4 `@Rule`s or `@RunWith`**, so if you launch scenarios the usual way (a `@Suite` class, the
way `:backend` does for its API tests) there is no Robolectric sandbox and no Compose rule — the
UI test infrastructure never gets set up. Cucumber's experimental `@WithJunitRule` support only
exists for the JUnit 4 Cucumber runner, which we are explicitly not using.

## The solution

A thin JUnit 4 **host** test supplies the two "around each test" behaviours the Cucumber engine
cannot, and drives the Cucumber engine programmatically through the JUnit Platform `Launcher`:

1. **Robolectric sandbox** — the host is a normal `@RunWith(RobolectricTestRunner)` test. Because
   everything else happens *inside* that test method, the thread context class loader is the
   Robolectric sandbox loader. The Cucumber engine, the glue classes it loads, and the Compose
   code those steps touch are therefore all loaded with Android shadows in place.
2. **Compose rule per scenario** — the host discovers the scenarios, then executes them one at a
   time. Each scenario's execution is wrapped in a freshly applied `createComposeRule()`
   `Statement`, giving exactly the "set up before / tear down after" lifecycle a JUnit 4 runner
   would normally give a `@Rule`.
3. **Sharing the rule with the glue** — `ComposeRuleHolder` publishes the active rule so the
   ordinary Cucumber step definitions can drive the UI. Everything runs on Robolectric's single
   test thread, so a plain holder is enough.

Crucially, scenarios still run on the **Cucumber JUnit Platform engine**
(`.filters(includeEngines("cucumber"))`) — the host only provides the surrounding rules.

## Files

| File | Role |
| --- | --- |
| `src/main/.../DemoScreen.kt` | A tiny Composable under test (a greeting + a button). |
| `src/test/resources/features/demo.feature` | The Gherkin scenarios. |
| `src/test/.../DemoStepDefinitions.kt` | Ordinary Cucumber glue, written as user-facing actions. |
| `src/test/.../DemoRobot.kt` | Robot/page-object hiding the Compose APIs behind those actions. |
| `src/test/.../ComposeRuleHolder.kt` | Bridges the host-owned rule into the glue. |
| `src/test/.../CucumberComposeUiTest.kt` | The JUnit 4 host: Robolectric + Compose rule + Launcher. |

## Adapting it to real tests

The host applies an arbitrary `TestRule`, so the same pattern works with this project's own
`ComprehensibleInputTestRule` (which chains the KTin singleton reset, the Compose rule and the
Roborazzi screenshot rule). Instead of `createComposeRule()`, build that rule, apply it around the
scenario, and publish `rule.composeRule` through the holder — the step definitions then get the
exact same `ComposeContentTestRule` the rest of the suite uses.
