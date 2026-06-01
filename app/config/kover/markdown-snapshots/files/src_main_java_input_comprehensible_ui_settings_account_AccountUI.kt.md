# src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 21-25

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:21-25`

```kotlin
🟢   21 |     modifier: Modifier = Modifier,
🟢   22 |     viewModel: AccountViewModel = viewModel(),
🟡   23 | ) {
🟢   24 |     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
🟢   25 |     AccountScreen(
```

## Lines 36-40

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:36-40`

```kotlin
⚪   36 |     onNavigateUp: () -> Unit,
🟢   37 |     modifier: Modifier = Modifier,
🟡   38 | ) {
🟢   39 |     Scaffold(
🟢   40 |         modifier = modifier,
```
