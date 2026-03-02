# src/main/java/input/comprehensible/ui/theme/Theme.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported


## Lines 84-91

Location: `src/main/java/input/comprehensible/ui/theme/Theme.kt:84-91`

```kotlin
⚪   84 | 
⚪   85 | @Composable
🔴   86 | fun ComprehensibleInputTheme(
🔴   87 |     darkTheme: Boolean = isSystemInDarkTheme(),
⚪   88 |     content: @Composable() () -> Unit
🟡   89 | ) {
🟢   90 |     MaterialTheme(
🟢   91 |         colorScheme = if (darkTheme) darkScheme else lightScheme,
```

