# src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 35-45

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:35-45`

```kotlin
⚪   35 |         }
🟢   36 |         .catch { throwable ->
🔴   37 |             Timber.e(throwable, "Failed to load text adventures list")
🔴   38 |             emit(TextAdventuresListResult.Error)
⚪   39 |         }
⚪   40 | 
🔴   41 |     fun getAdventure(id: String): Flow<TextAdventureResult> = localDataSource
🔴   42 |         .getAdventureSentenceRows(id)
🔴   43 |         .map { rows ->
⚪   44 |             if (rows.isEmpty()) {
⚪   45 |                 TextAdventureResult.Error
```

## Lines 48-55

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:48-55`

```kotlin
⚪   48 |             }
⚪   49 |         }
🔴   50 |         .distinctUntilChanged()
🔴   51 |         .catch { throwable ->
🔴   52 |         Timber.e(throwable, "Failed to load text adventure %s", id)
🔴   53 |         emit(TextAdventureResult.Error)
⚪   54 |     }
⚪   55 | 
```

## Lines 58-130

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:58-130`

```kotlin
⚪   58 |         translationsLanguage: String,
⚪   59 |     ): String {
🔴   60 |         val languages = TextAdventureLanguages(
🔴   61 |             learningLanguage = learningLanguage,
🔴   62 |             translationLanguage = translationsLanguage,
⚪   63 |         )
🔴   64 |         val response = remoteDataSource.startAdventure(
🔴   65 |             learningLanguage = learningLanguage,
🔴   66 |             translationsLanguage = translationsLanguage,
⚪   67 |         )
🔴   68 |         val now = clock()
🔴   69 |         val adventureId = response.adventureId
🔴   70 |         localDataSource.insertAdventure(
🔴   71 |             TextAdventureEntity(
🔴   72 |                 id = adventureId,
🔴   73 |                 title = response.title,
🔴   74 |                 learningLanguage = learningLanguage,
🔴   75 |                 translationLanguage = translationsLanguage,
🔴   76 |                 createdAt = now,
🔴   77 |                 updatedAt = now,
⚪   78 |             )
⚪   79 |         )
🔴   80 |         insertAiResponse(
🔴   81 |             adventureId = adventureId,
🔴   82 |             response = response,
🔴   83 |             languages = languages,
🔴   84 |             messageIndex = 0,
🔴   85 |             createdAt = now,
⚪   86 |         )
🔴   87 |         return adventureId
⚪   88 |     }
⚪   89 | 
⚪   90 |     suspend fun respondToAdventure(adventureId: String, userMessage: String) {
🔴   91 |         val adventure = localDataSource.getAdventureSnapshot(adventureId) ?: run {
🔴   92 |             Timber.e("Text adventure %s not found when responding", adventureId)
🔴   93 |             return
⚪   94 |         }
🔴   95 |         val languages = TextAdventureLanguages(
🔴   96 |             learningLanguage = adventure.learningLanguage,
🔴   97 |             translationLanguage = adventure.translationLanguage,
⚪   98 |         )
🔴   99 |         val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
🔴  100 |         val now = clock()
🔴  101 |         insertUserMessage(
🔴  102 |             adventureId = adventureId,
🔴  103 |             message = userMessage,
🔴  104 |             language = adventure.learningLanguage,
🔴  105 |             messageIndex = nextIndex,
🔴  106 |             createdAt = now,
⚪  107 |         )
🔴  108 |         val response = remoteDataSource.respondToUser(
🔴  109 |             adventureId = adventureId,
🔴  110 |             learningLanguage = adventure.learningLanguage,
🔴  111 |             translationsLanguage = adventure.translationLanguage,
🔴  112 |             userMessage = userMessage,
🔴  113 |             history = buildHistory(
🔴  114 |                 localDataSource = localDataSource,
🔴  115 |                 adventureId = adventureId,
🔴  116 |                 learningLanguage = adventure.learningLanguage,
⚪  117 |             ),
⚪  118 |         )
🔴  119 |         insertAiResponse(
🔴  120 |             adventureId = adventureId,
🔴  121 |             response = response,
🔴  122 |             languages = languages,
🔴  123 |             messageIndex = nextIndex + 1,
🔴  124 |             createdAt = now,
⚪  125 |         )
🔴  126 |         localDataSource.updateAdventureUpdatedAt(
🔴  127 |             id = adventureId,
🔴  128 |             updatedAt = now,
⚪  129 |         )
⚪  130 |     }
```

## Lines 137-158

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:137-158`

```kotlin
⚪  137 |         createdAt: Long,
⚪  138 |     ) {
🔴  139 |         val messageEntity = TextAdventureMessageEntity(
🔴  140 |             adventureId = adventureId,
🔴  141 |             sender = TextAdventureMessageSender.USER,
🔴  142 |             isEnding = false,
🔴  143 |             createdAt = createdAt,
🔴  144 |             messageIndex = messageIndex,
⚪  145 |         )
🔴  146 |         val sentenceEntities = listOf(
🔴  147 |             TextAdventureSentenceEntity(
🔴  148 |                 adventureId = adventureId,
🔴  149 |                 messageIndex = messageIndex,
🔴  150 |                 paragraphIndex = 0,
🔴  151 |                 language = language,
🔴  152 |                 sentenceIndex = 0,
🔴  153 |                 text = message,
⚪  154 |             )
⚪  155 |         )
🔴  156 |         localDataSource.insertMessageAndSentences(messageEntity, sentenceEntities)
⚪  157 |     }
⚪  158 | 
```

## Lines 164-206

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:164-206`

```kotlin
⚪  164 |         createdAt: Long,
⚪  165 |     ) {
🔴  166 |         val messageEntity = TextAdventureMessageEntity(
🔴  167 |             adventureId = adventureId,
🔴  168 |             sender = TextAdventureMessageSender.AI,
🔴  169 |             isEnding = response.isEnding,
🔴  170 |             createdAt = createdAt,
🔴  171 |             messageIndex = messageIndex,
⚪  172 |         )
🔴  173 |         val sentenceEntities = buildList {
🔴  174 |             response.sentences.forEachIndexed { index, sentence ->
🔴  175 |                 add(
🔴  176 |                     TextAdventureSentenceEntity(
🔴  177 |                         adventureId = adventureId,
🔴  178 |                         messageIndex = messageIndex,
🔴  179 |                         paragraphIndex = 0,
🔴  180 |                         language = languages.learningLanguage,
🔴  181 |                         sentenceIndex = index,
🔴  182 |                         text = sentence,
⚪  183 |                     )
⚪  184 |                 )
🔴  185 |             }
🔴  186 |             response.translatedSentences.forEachIndexed { index, sentence ->
🔴  187 |                 add(
🔴  188 |                     TextAdventureSentenceEntity(
🔴  189 |                         adventureId = adventureId,
🔴  190 |                         messageIndex = messageIndex,
🔴  191 |                         paragraphIndex = 0,
🔴  192 |                         language = languages.translationLanguage,
🔴  193 |                         sentenceIndex = index,
🔴  194 |                         text = sentence,
⚪  195 |                     )
⚪  196 |                 )
🔴  197 |             }
🔴  198 |         }
🔴  199 |         localDataSource.insertMessageAndSentences(messageEntity, sentenceEntities)
⚪  200 |     }
⚪  201 | 
🔴  202 |     private data class TextAdventureLanguages(
🔴  203 |         val learningLanguage: String,
🔴  204 |         val translationLanguage: String,
⚪  205 |     )
⚪  206 | 
```

## Lines 215-253

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:215-253`

```kotlin
⚪  215 | sealed interface TextAdventuresListResult {
🟢  216 |     data class Success(val adventures: List<TextAdventureSummary>) : TextAdventuresListResult
🔴  217 |     object Error : TextAdventuresListResult
⚪  218 | }
⚪  219 | 
🔴  220 | private fun TextAdventureSummaryView.toSummary() = TextAdventureSummary(
🔴  221 |     id = adventureId,
🔴  222 |     title = title,
🔴  223 |     isComplete = isComplete,
🔴  224 |     updatedAt = updatedAt,
⚪  225 | )
⚪  226 | 
⚪  227 | private fun List<TextAdventureMessageSentenceView>.toDomain(): TextAdventure {
🔴  228 |     val adventure = first()
🔴  229 |     val messageGroups = groupBy { it.messageIndex }
🔴  230 |     val messageUi = messageGroups.keys.sorted().map { messageIndex ->
🔴  231 |         val messageRows = messageGroups.getValue(messageIndex)
🔴  232 |         val paragraphs = messageRows
🔴  233 |             .groupBy { it.paragraphIndex }
🔴  234 |             .toSortedMap()
🔴  235 |             .map { (paragraphIndex, paragraphRows) ->
🔴  236 |                 paragraphRows.toDomain(
🔴  237 |                     messageIndex = messageIndex,
🔴  238 |                     paragraphIndex = paragraphIndex,
🔴  239 |                     learningLanguage = adventure.learningLanguage,
🔴  240 |                     translationLanguage = adventure.translationLanguage,
🔴  241 |                 )
⚪  242 |             }
🔴  243 |         messageRows.first().toMessageEntity().toDomain(paragraphs = paragraphs)
⚪  244 |     }
🔴  245 |     return TextAdventure(
🔴  246 |         id = adventure.adventureId,
🔴  247 |         title = adventure.title,
🔴  248 |         learningLanguage = adventure.learningLanguage,
🔴  249 |         translationLanguage = adventure.translationLanguage,
🔴  250 |         messages = messageUi,
🔴  251 |         isComplete = messageUi.lastOrNull()?.isEnding == true,
⚪  252 |     )
⚪  253 | }
```

## Lines 255-263

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:255-263`

```kotlin
⚪  255 | private fun TextAdventureMessageEntity.toDomain(
⚪  256 |     paragraphs: List<TextAdventureParagraph>,
🔴  257 | ) = TextAdventureMessage(
🔴  258 |     id = "${adventureId}-${messageIndex}",
🔴  259 |     sender = sender,
🔴  260 |     paragraphs = paragraphs,
🔴  261 |     isEnding = isEnding,
⚪  262 | )
⚪  263 | 
```

## Lines 268-287

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:268-287`

```kotlin
⚪  268 |     translationLanguage: String,
⚪  269 | ): TextAdventureParagraph {
🔴  270 |     val sentencesByLanguage = sortedBy { it.sentenceIndex }
🔴  271 |         .groupBy { it.language }
🔴  272 |     val adventureId = firstOrNull()?.adventureId.orEmpty()
🔴  273 |     return TextAdventureParagraph(
🔴  274 |         id = "${adventureId}-${messageIndex}-$paragraphIndex",
🔴  275 |         sentences = sentencesByLanguage[learningLanguage].orEmpty().map { it.text },
🔴  276 |         translatedSentences = sentencesByLanguage[translationLanguage].orEmpty().map { it.text },
⚪  277 |     )
⚪  278 | }
⚪  279 | 
🔴  280 | private fun TextAdventureMessageSentenceView.toMessageEntity() = TextAdventureMessageEntity(
🔴  281 |     adventureId = adventureId,
🔴  282 |     sender = sender,
🔴  283 |     isEnding = isEnding,
🔴  284 |     createdAt = 0L,
🔴  285 |     messageIndex = messageIndex,
⚪  286 | )
⚪  287 | 
```

## Lines 291-327

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:291-327`

```kotlin
⚪  291 |     learningLanguage: String,
⚪  292 | ): List<TextAdventureHistoryMessage> {
🔴  293 |     val messages = localDataSource.getMessagesSnapshot(adventureId)
🔴  294 |         .sortedBy { it.messageIndex }
🔴  295 |     val sentencesByMessageIndex = localDataSource.getSentencesSnapshot(adventureId)
🔴  296 |         .filter { it.language == learningLanguage }
🔴  297 |         .groupBy { it.messageIndex }
🔴  298 |     return messages.mapNotNull { message ->
🔴  299 |         val text = sentencesByMessageIndex[message.messageIndex]
🔴  300 |             .orEmpty()
🔴  301 |             .groupBy { it.paragraphIndex }
🔴  302 |             .toSortedMap()
🔴  303 |             .mapNotNull { (_, paragraphSentences) ->
🔴  304 |                 val sentenceText = paragraphSentences
🔴  305 |                     .sortedBy { it.sentenceIndex }
🔴  306 |                     .joinToString(" ") { it.text.trim() }
🔴  307 |                     .trim()
🔴  308 |                 sentenceText.takeIf { it.isNotBlank() }
⚪  309 |             }
🔴  310 |             .joinToString("\n\n")
🔴  311 |             .trim()
🔴  312 |         text.takeIf { it.isNotBlank() }?.let {
🔴  313 |             TextAdventureHistoryMessage(
🔴  314 |                 role = when (message.sender) {
🔴  315 |                     TextAdventureMessageSender.USER -> "user"
🔴  316 |                     TextAdventureMessageSender.AI -> "assistant"
⚪  317 |                 },
🔴  318 |                 text = it,
🔴  319 |             )
🔴  320 |         }
⚪  321 |     }
⚪  322 | }
⚪  323 | 
⚪  324 | sealed interface TextAdventureResult {
🔴  325 |     data class Success(val adventure: TextAdventure) : TextAdventureResult
🔴  326 |     object Error : TextAdventureResult
⚪  327 | }
```
