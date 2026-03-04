# src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 32-36

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:32-36`

```kotlin
⚪   32 |     ) { adventureResult, selectedSentence, inputTextValue ->
🟢   33 |         when (adventureResult) {
🟡   34 |             TextAdventureResult.Error -> TextAdventureUiState.Error
🟢   35 |             is TextAdventureResult.Success -> TextAdventureUiState.Loaded(
🟢   36 |                 title = adventureResult.adventure.title,
```

## Lines 53-57

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:53-57`

```kotlin
⚪   53 |     fun onSendMessage() {
🟢   54 |         val message = inputText.value.trim()
🟡   55 |         if (message.isBlank()) return
🟢   56 |         viewModelScope.launch {
🟢   57 |             inputText.value = ""
```

## Lines 62-70

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:62-70`

```kotlin
⚪   62 |     fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
🟢   63 |         selectedText.update { selectedSentence ->
🟡   64 |             if (selectedSentence?.messageId == messageId &&
🔴   65 |                 selectedSentence.paragraphIndex == paragraphIndex &&
🔴   66 |                 selectedSentence.sentenceIndex == sentenceIndex
⚪   67 |             ) {
🔴   68 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪   69 |             }
🟢   70 |             TextAdventureUiState.SelectedText(
```
