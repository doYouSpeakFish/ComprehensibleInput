# src/main/java/input/comprehensible/ui/components/TranslatableText.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-37

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:33-37`

```kotlin
🟢   33 |         color = MaterialTheme.colorScheme.background,
⚪   34 |     )
🟡   35 | ) {
🟢   36 |     TranslatableText(
🟢   37 |         modifier = modifier,
```

## Lines 48-52

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:48-52`

```kotlin
⚪   48 | 
⚪   49 | @Composable
🔴   50 | fun TranslatableText(
🟢   51 |     modifier: Modifier = Modifier,
⚪   52 |     sentences: List<String>,
```

## Lines 64-68

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:64-68`

```kotlin
🟢   64 |         color = MaterialTheme.colorScheme.background,
⚪   65 |     )
🟡   66 | ) {
🟢   67 |     val textContent = rememberTranslatableTextContent(
🟢   68 |         sentences = sentences,
```

## Lines 85-89

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:85-89`

```kotlin
⚪   85 |     background: Color,
⚪   86 |     color: Color,
🟡   87 | ) = remember(background, color) {
🟢   88 |     SpanStyle(
🟢   89 |         background = background,
```

## Lines 105-111

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:105-111`

```kotlin
🟢  105 |     translatedSentences,
🟢  106 |     highlightedSpanStyle,
🟡  107 |     defaultSpanStyle,
🟡  108 |     selectedSentenceIndex,
🔴  109 |     isSelectionTranslated,
⚪  110 | ) {
🟢  111 |     buildAnnotatedString {
```

## Lines 113-117

Location: `src/main/java/input/comprehensible/ui/components/TranslatableText.kt:113-117`

```kotlin
🟢  113 |             val isSelected = selectedSentenceIndex == index
🟢  114 |             val textToDisplay = if (isSelected && isSelectionTranslated) {
🟡  115 |                 translatedSentences.getOrNull(index) ?: sentence
⚪  116 |             } else {
🟢  117 |                 sentence
```
