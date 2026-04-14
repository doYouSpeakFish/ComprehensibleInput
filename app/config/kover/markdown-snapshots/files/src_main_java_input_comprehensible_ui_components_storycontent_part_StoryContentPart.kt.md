# src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 44-48

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:44-48`

```kotlin
🟢   44 |     onChoiceTextSelected: (Int) -> Unit = {},
⚪   45 |     state: StoryContentPartUiState,
🟡   46 | ) {
🟢   47 |     Box(modifier) {
🟢   48 |         when (state) {
```

## Lines 60-64

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:60-64`

```kotlin
🟢   60 |                 onOptionTextSelected = onChoiceTextSelected,
⚪   61 |             )
🔴   62 |         }
🟢   63 |     }
🟢   64 | }
```

## Lines 71-75

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:71-75`

```kotlin
⚪   71 |     onSentenceSelected: (Int) -> Unit,
⚪   72 |     state: StoryContentPartUiState.Paragraph,
🟡   73 | ) {
🟢   74 |     TranslatableText(
🟢   75 |         modifier = modifier,
```

## Lines 86-90

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:86-90`

```kotlin
🟢   86 |     modifier: Modifier = Modifier,
⚪   87 |     state: StoryContentPartUiState.Image
🟡   88 | ) {
🟢   89 |     Box(modifier) {
🟢   90 |         Image(
```

## Lines 112-116

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:112-116`

```kotlin
⚪  112 |     isSelectionTranslated: Boolean,
⚪  113 |     onOptionTextSelected: (Int) -> Unit,
🟡  114 | ) {
🟢  115 |     Column(
🟢  116 |         modifier = modifier.padding(vertical = 16.dp),
```

## Lines 137-141

Location: `src/main/java/input/comprehensible/ui/components/storycontent/part/StoryContentPart.kt:137-141`

```kotlin
⚪  137 |     onOptionTextSelected: () -> Unit,
⚪  138 |     option: StoryContentPartUiState.Choices.Option,
🟡  139 | ) {
🟢  140 |     val colorScheme = MaterialTheme.colorScheme
🟢  141 |     val buttonContainerColor =
```
