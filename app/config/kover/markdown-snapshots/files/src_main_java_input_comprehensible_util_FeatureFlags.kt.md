# src/main/java/input/comprehensible/util/FeatureFlags.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 8-13

Location: `src/main/java/input/comprehensible/util/FeatureFlags.kt:8-13`

```kotlin
⚪    8 | ) {
⚪    9 |     companion object : InjectedSingleton<FeatureFlags>() {
🔴   10 |         fun getDefault() = FeatureFlags(
🔴   11 |             aiTextAdventuresEnabled = BuildConfig.DEBUG,
⚪   12 |         )
⚪   13 |     }
```
