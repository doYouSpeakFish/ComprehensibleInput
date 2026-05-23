# src/main/java/input/comprehensible/ui/settings/SettingsNavGraph.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 18-26

Location: `src/main/java/input/comprehensible/ui/settings/SettingsNavGraph.kt:18-26`

```kotlin
⚪   18 |     ) {
🟢   19 |         settingsScreen(
🟡   20 |             onNavigateUp = navController::navigateUp,
🔴   21 |             onGoToSoftwareLicences = { navController.navigate(SoftwareLicencesRoute) },
⚪   22 |         )
🟢   23 |         softwareLicences(
🟡   24 |             onNavigateUp = navController::navigateUp,
⚪   25 |         )
⚪   26 |     }
```
