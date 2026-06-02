# src/main/java/input/comprehensible/util/FeatureFlags.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 9-15

Location: `src/main/java/input/comprehensible/util/FeatureFlags.kt:9-15`

```kotlin
⚪    9 | ) {
⚪   10 |     companion object : InjectedSingleton<FeatureFlags>() {
🔴   11 |         fun getDefault() = FeatureFlags(
🔴   12 |             aiTextAdventuresEnabled = BuildConfig.AI_TEXT_ADVENTURES_ENABLED,
🔴   13 |             accountManagementEnabled = BuildConfig.ACCOUNT_MANAGEMENT_ENABLED,
⚪   14 |         )
⚪   15 |     }
```
