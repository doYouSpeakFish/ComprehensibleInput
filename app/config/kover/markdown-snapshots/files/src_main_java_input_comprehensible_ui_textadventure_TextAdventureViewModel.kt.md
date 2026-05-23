# src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 18-95

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:18-95`

```kotlin
⚪   18 | 
⚪   19 | class TextAdventureViewModel(
🔴   20 |     private val adventureId: String,
🔴   21 |     getTextAdventureUseCase: GetTextAdventureUseCase = GetTextAdventureUseCase(),
🔴   22 |     private val continueTextAdventureUseCase: ContinueTextAdventureUseCase = ContinueTextAdventureUseCase(),
⚪   23 | ) : ViewModel() {
🔴   24 |     private val adventureFlow = getTextAdventureUseCase(adventureId)
🔴   25 |     private val selectedText = MutableStateFlow<TextAdventureUiState.SelectedText?>(null)
🔴   26 |     private val inputText = MutableStateFlow("")
⚪   27 | 
🔴   28 |     val state = combine(
🔴   29 |         adventureFlow,
🔴   30 |         selectedText,
🔴   31 |         inputText,
⚪   32 |     ) { adventureResult, selectedSentence, inputTextValue ->
🔴   33 |         when (adventureResult) {
🔴   34 |             TextAdventureResult.Error -> TextAdventureUiState.Error
🟡   35 |             is TextAdventureResult.Success -> TextAdventureUiState.Loaded(
🔴   36 |                 title = adventureResult.adventure.title,
🔴   37 |                 messages = adventureResult.adventure.messages.map { it.toUiState() },
🔴   38 |                 selectedText = selectedSentence,
🔴   39 |                 inputText = inputTextValue,
🔴   40 |                 isInputEnabled = !adventureResult.adventure.isComplete,
⚪   41 |             )
⚪   42 |         }
🔴   43 |     }.stateIn(
🔴   44 |         viewModelScope,
🔴   45 |         started = SharingStarted.Lazily,
🔴   46 |         initialValue = TextAdventureUiState.Loading,
⚪   47 |     )
⚪   48 | 
⚪   49 |     fun onInputChanged(value: String) {
🔴   50 |         inputText.value = value
⚪   51 |     }
⚪   52 | 
⚪   53 |     fun onSendMessage() {
🔴   54 |         val message = inputText.value.trim()
🔴   55 |         if (message.isBlank()) return
🔴   56 |         viewModelScope.launch {
🔴   57 |             inputText.value = ""
🔴   58 |             continueTextAdventureUseCase(adventureId = adventureId, userMessage = message)
⚪   59 |         }
⚪   60 |     }
⚪   61 | 
⚪   62 |     fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
🔴   63 |         selectedText.update { selectedSentence ->
🔴   64 |             if (selectedSentence?.messageId == messageId &&
🔴   65 |                 selectedSentence.paragraphIndex == paragraphIndex &&
🔴   66 |                 selectedSentence.sentenceIndex == sentenceIndex
⚪   67 |             ) {
🔴   68 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪   69 |             }
🔴   70 |             TextAdventureUiState.SelectedText(
🔴   71 |                 messageId = messageId,
🔴   72 |                 paragraphIndex = paragraphIndex,
🔴   73 |                 sentenceIndex = sentenceIndex,
🔴   74 |                 isTranslated = true,
🔴   75 |             )
⚪   76 |         }
⚪   77 |     }
⚪   78 | }
⚪   79 | 
🔴   80 | private fun TextAdventureMessage.toUiState(): TextAdventureMessageUiState = when (sender) {
🔴   81 |     TextAdventureMessageSender.AI -> TextAdventureMessageUiState.Ai(
🔴   82 |         id = id,
🔴   83 |         paragraphs = paragraphs.map { it.toUiState() },
🔴   84 |         isEnding = isEnding,
⚪   85 |     )
🔴   86 |     TextAdventureMessageSender.USER -> TextAdventureMessageUiState.User(
🔴   87 |         id = id,
🔴   88 |         text = paragraphs.flatMap { it.sentences }.joinToString(" "),
⚪   89 |     )
⚪   90 | }
⚪   91 | 
🔴   92 | private fun TextAdventureParagraph.toUiState() = StoryContentPartUiState.Paragraph(
🔴   93 |     sentences = sentences,
🔴   94 |     translatedSentences = translatedSentences,
⚪   95 | )
```
