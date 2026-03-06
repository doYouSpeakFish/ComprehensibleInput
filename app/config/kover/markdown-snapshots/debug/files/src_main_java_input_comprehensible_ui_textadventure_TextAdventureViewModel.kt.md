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
🟢   35 |             TextAdventureResult.Loading -> TextAdventureUiState.Loading
🟢   36 |             is TextAdventureResult.Success -> {
```

## Lines 72-85

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:72-85`

```kotlin
⚪   72 |     fun onSendMessage() {
🟢   73 |         val message = inputText.value.trim()
🟡   74 |         if (message.isBlank()) return
🟢   75 |         val currentState = state.value
🟡   76 |         if (currentState is TextAdventureUiState.Loaded && !currentState.isSendEnabled) return
🟢   77 |         inputText.value = ""
🟢   78 |         viewModelScope.launch {
⚪   79 |             try {
🟢   80 |                 continueTextAdventureUseCase(adventureId = adventureId, userMessage = message)
🟡   81 |             } catch (e: Exception) {
🟢   82 |                 ensureActive()
🟢   83 |                 Timber.e(e, "Failed to send message for adventure %s", adventureId)
⚪   84 |             }
⚪   85 |         }
```

## Lines 88-97

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:88-97`

```kotlin
⚪   88 |     fun onRetry() {
🟢   89 |         viewModelScope.launch {
⚪   90 |             try {
🟢   91 |                 continueTextAdventureUseCase.retry(adventureId = adventureId)
🟡   92 |             } catch (e: Exception) {
🟢   93 |                 ensureActive()
🟢   94 |                 Timber.e(e, "Failed to retry adventure %s", adventureId)
⚪   95 |             }
⚪   96 |         }
⚪   97 |     }
```

## Lines 99-114

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:99-114`

```kotlin
⚪   99 |     fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
🟢  100 |         selectedText.update { selectedSentence ->
🟡  101 |             if (selectedSentence?.messageId == messageId &&
🔴  102 |                 selectedSentence.paragraphIndex == paragraphIndex &&
🔴  103 |                 selectedSentence.sentenceIndex == sentenceIndex
⚪  104 |             ) {
🔴  105 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪  106 |             }
🟢  107 |             TextAdventureUiState.SelectedText(
```
