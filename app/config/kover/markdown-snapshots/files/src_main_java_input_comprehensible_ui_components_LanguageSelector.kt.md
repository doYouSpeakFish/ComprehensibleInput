# src/main/java/input/comprehensible/ui/components/LanguageSelector.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 98-102

Location: `src/main/java/input/comprehensible/ui/components/LanguageSelector.kt:98-102`

```kotlin
⚪   98 |     translationLanguage: LanguageSelection?,
⚪   99 |     languageOptions: List<LanguageSelection>,
🟡  100 | ) {
🟢  101 |     var isLearningLanguageMenuShown by remember { mutableStateOf(false) }
🟢  102 |     var isTranslationLanguageMenuShown by remember { mutableStateOf(false) }
```

## Lines 152-162

Location: `src/main/java/input/comprehensible/ui/components/LanguageSelector.kt:152-162`

```kotlin
⚪  152 |     isMenuShown: Boolean,
⚪  153 |     onMenuShownChanged: (Boolean) -> Unit,
🔴  154 |     languagesList: List<LanguageSelection> = LanguageSelection.entries,
⚪  155 |     contentDescription: String,
🟡  156 | ) {
🟢  157 |     Box(modifier) {
🟢  158 |         LanguageToggleButton(
🟢  159 |             languageSelection = languageSelection,
🟡  160 |             onClick = { onMenuShownChanged(!isMenuShown) },
🟢  161 |             contentDescription = contentDescription
⚪  162 |         )
```

## Lines 170-174

Location: `src/main/java/input/comprehensible/ui/components/LanguageSelector.kt:170-174`

```kotlin
⚪  170 |                 ),
🟢  171 |             expanded = isMenuShown,
🟡  172 |             onDismissRequest = { onMenuShownChanged(false) },
🟢  173 |         ) {
🟢  174 |             Text(
```

## Lines 232-236

Location: `src/main/java/input/comprehensible/ui/components/LanguageSelector.kt:232-236`

```kotlin
⚪  232 |     languageSelection: LanguageSelection,
⚪  233 |     contentDescription: String,
🟡  234 | ) {
🟢  235 |     OutlinedIconButton(
🟢  236 |         modifier = modifier,
```
