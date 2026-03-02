# src/main/java/input/comprehensible/ui/components/topbar/TopBar.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 29-41

Location: `src/main/java/input/comprehensible/ui/components/topbar/TopBar.kt:29-41`

```kotlin
⚪   29 | @Composable
⚪   30 | fun TopBar(
🔴   31 |     modifier: Modifier = Modifier,
⚪   32 |     onSettingsClick: () -> Unit,
⚪   33 |     title: String,
🔴   34 | ) {
🔴   35 |     TopAppBar(
🔴   36 |         modifier = modifier,
🔴   37 |         title = { Text(title) },
🔴   38 |         actions = {
🔴   39 |             IconButton(onClick = onSettingsClick) {
⚪   40 |                 Icon(
⚪   41 |                     imageVector = Icons.Default.Settings,
```

## Lines 45-49

Location: `src/main/java/input/comprehensible/ui/components/topbar/TopBar.kt:45-49`

```kotlin
⚪   45 |         }
⚪   46 |     )
🔴   47 | }
⚪   48 | 
⚪   49 | /**
```

## Lines 61-65

Location: `src/main/java/input/comprehensible/ui/components/topbar/TopBar.kt:61-65`

```kotlin
⚪   61 |     onTranslationLanguageSelected: (LanguageSelection) -> Unit,
⚪   62 |     onSettingsClick: () -> Unit,
🟡   63 | ) {
🟢   64 |     CenterAlignedTopAppBar(
🟢   65 |         modifier = modifier,
```
