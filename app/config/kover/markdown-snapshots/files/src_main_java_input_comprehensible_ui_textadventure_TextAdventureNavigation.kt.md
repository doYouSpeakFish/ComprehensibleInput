# src/main/java/input/comprehensible/ui/textadventure/TextAdventureNavigation.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 12-18

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureNavigation.kt:12-18`

```kotlin
⚪   12 | import kotlinx.serialization.Serializable
⚪   13 | 
🔴   14 | @Serializable
⚪   15 | data class TextAdventureRoute(
🔴   16 |     val adventureId: String,
⚪   17 | )
⚪   18 | 
```

## Lines 26-34

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureNavigation.kt:26-34`

```kotlin
🟢   26 |         popExitTransition = defaultPopExitTransition,
🟢   27 |     ) { backStackEntry ->
🔴   28 |         val args = backStackEntry.toRoute<TextAdventureRoute>()
🔴   29 |         TextAdventureScreen(
🔴   30 |             modifier = Modifier.fillMaxSize(),
🔴   31 |             adventureId = args.adventureId,
🔴   32 |             onNavigateUp = onNavigateUp,
⚪   33 |         )
⚪   34 |     }
```
