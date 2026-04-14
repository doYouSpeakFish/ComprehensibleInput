# src/main/java/input/comprehensible/ui/storyreader/StoryReaderViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 34-38

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderViewModel.kt:34-38`

```kotlin
🟢   34 |         .map {
🟢   35 |             (it as? StoryResult.Success)?.story
🟡   36 |                 ?.toPartUiStates(onChoiceSelected = ::onChoiceSelected)
🟢   37 |                 .orEmpty()
⚪   38 |         }
```

## Lines 95-102

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderViewModel.kt:95-102`

```kotlin
🟢   95 |         selectedTextState.update { selectedText ->
🟢   96 |             val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
🟡   97 |             val samePart = selectedSentence?.partIndex == partIndex
🟡   98 |             val sameParagraph = selectedSentence?.paragraphIndex == paragraphIndex
🟡   99 |             val sameSentence = selectedSentence?.selectedSentenceIndex == sentenceIndex
🟡  100 |             if (samePart && sameParagraph && sameSentence) {
🟢  101 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪  102 |             }
```

## Lines 123-129

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderViewModel.kt:123-129`

```kotlin
🟢  123 |         selectedTextState.update { selectedText ->
🟢  124 |             val selectedChoice = selectedText as? SelectedText.ChoiceOption
🟡  125 |             val samePart = selectedChoice?.partIndex == partIndex
🟡  126 |             val sameOption = selectedChoice?.optionIndex == optionIndex
🟡  127 |             if (samePart && sameOption) {
🟢  128 |                 return@update selectedChoice.copy(isTranslated = !selectedChoice.isTranslated)
⚪  129 |             }
```
