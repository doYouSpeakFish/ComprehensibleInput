# src/main/java/input/comprehensible/ui/ComprehensibleInputApp.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 24-28

Location: `src/main/java/input/comprehensible/ui/ComprehensibleInputApp.kt:24-28`

```kotlin
⚪   24 |     navController: NavHostController,
⚪   25 |     darkTheme: Boolean,
🟡   26 | ) {
🟢   27 |     ComprehensibleInputTheme(darkTheme = darkTheme) {
🟢   28 |         val featureFlags = FeatureFlags()
```

## Lines 41-50

Location: `src/main/java/input/comprehensible/ui/ComprehensibleInputApp.kt:41-50`

```kotlin
🟢   41 |                     { navController.navigate(TextAdventureRoute(adventureId = it)) }
⚪   42 |                 } else {
🔴   43 |                     {}
⚪   44 |                 },
🟢   45 |                 onTextAdventureStarted = if (featureFlags.aiTextAdventuresEnabled) {
🟢   46 |                     { navController.navigate(TextAdventureRoute(adventureId = it)) }
⚪   47 |                 } else {
🔴   48 |                     {}
⚪   49 |                 },
🟢   50 |                 onSettingsClick = { navController.navigate(SettingsRoute) },
```
