# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 11-21

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:11-21`

```kotlin
⚪   11 | import java.util.UUID
⚪   12 | 
🔴   13 | class TextAdventureGenerationService(
🔴   14 |     private val structuredPromptExecutor: TextAdventureStructuredPromptExecutor,
🔴   15 |     private val adventureRepository: AdventureRepository,
⚪   16 | ) {
🔴   17 |     private val json = Json {
🔴   18 |         encodeDefaults = true
🔴   19 |         prettyPrint = true
⚪   20 |     }
⚪   21 | 
```

## Lines 24-58

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:24-58`

```kotlin
⚪   24 |         translationsLanguage: String,
⚪   25 |     ): TextAdventureRemoteResponse {
🔴   26 |         val response = requestAdventureResponse(
🔴   27 |             adventureId = UUID.randomUUID().toString(),
🔴   28 |             promptName = "text-adventure-start",
⚪   29 |             systemPrompt = """
⚪   30 |                 You are a text adventure narrator.
🔴   31 |                 Generate the opening scene in $learningLanguage.
⚪   32 |                 Provide a short, evocative title for the adventure.
🔴   33 |                 Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
⚪   34 |                 Do not include extra commentary outside the requested fields.
⚪   35 |                 Avoid markdown and keep punctuation natural for the language.
⚪   36 |                 The story should not end yet, so set isEnding to false.
🔴   37 |             """.trimIndent(),
🔴   38 |             userPrompt = "Start a new adventure.",
⚪   39 |         )
🔴   40 |         adventureRepository.saveAdventurePart(
🔴   41 |             PersistedAdventurePart(
🔴   42 |                 adventureId = response.adventureId,
🔴   43 |                 title = response.title,
🔴   44 |                 learningLanguage = learningLanguage,
🔴   45 |                 translationLanguage = translationsLanguage,
🔴   46 |                 isEnding = response.isEnding,
🔴   47 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪   48 |                     (paragraph, translatedParagraph) ->
🔴   49 |                     PersistedAdventureParagraph(
🔴   50 |                         sentences = paragraph.sentences.map(String::trim),
🔴   51 |                         translatedSentences = translatedParagraph.sentences.map(String::trim),
🔴   52 |                     )
⚪   53 |                 },
⚪   54 |             )
⚪   55 |         )
🔴   56 |         return response.toRemoteResponse()
⚪   57 |     }
⚪   58 | 
```

## Lines 64-111

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:64-111`

```kotlin
⚪   64 |         history: List<TextAdventureHistoryMessage>,
⚪   65 |     ): TextAdventureRemoteResponse {
🔴   66 |         val response = requestAdventureResponse(
🔴   67 |             adventureId = adventureId,
🔴   68 |             promptName = "text-adventure-continue",
⚪   69 |             systemPrompt = """
⚪   70 |                 You are a text adventure narrator continuing an ongoing story.
⚪   71 |                 You will receive a JSON request containing the adventure context and chat history.
🔴   72 |                 Respond to the player in $learningLanguage.
🔴   73 |                 Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
⚪   74 |                 Keep the title consistent with the story so far.
⚪   75 |                 Do not include extra commentary outside the requested fields.
⚪   76 |                 Avoid markdown and keep punctuation natural for the language.
🔴   77 |             """.trimIndent(),
🔴   78 |             userPrompt = json.encodeToString(
🔴   79 |                 ContinueTextAdventureRequest(
🔴   80 |                     adventureId = adventureId,
🔴   81 |                     learningLanguage = learningLanguage,
🔴   82 |                     translationsLanguage = translationsLanguage,
🔴   83 |                     userMessage = userMessage,
🔴   84 |                     history = history,
⚪   85 |                 )
⚪   86 |             ),
⚪   87 |         )
🔴   88 |         adventureRepository.saveAdventurePart(
🔴   89 |             PersistedAdventurePart(
🔴   90 |                 adventureId = response.adventureId,
🔴   91 |                 title = response.title,
🔴   92 |                 learningLanguage = learningLanguage,
🔴   93 |                 translationLanguage = translationsLanguage,
🔴   94 |                 isEnding = response.isEnding,
🔴   95 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪   96 |                     (paragraph, translatedParagraph) ->
🔴   97 |                     PersistedAdventureParagraph(
🔴   98 |                         sentences = paragraph.sentences.map(String::trim),
🔴   99 |                         translatedSentences = translatedParagraph.sentences.map(String::trim),
🔴  100 |                     )
⚪  101 |                 },
⚪  102 |             )
⚪  103 |         )
🔴  104 |         return response.toRemoteResponse()
⚪  105 |     }
⚪  106 | 
⚪  107 | 
⚪  108 |     fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? =
🔴  109 |         adventureRepository.getAdventureMessages(adventureId)
⚪  110 | 
⚪  111 |     private suspend fun requestAdventureResponse(
```

## Lines 114-151

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:114-151`

```kotlin
⚪  114 |         systemPrompt: String,
⚪  115 |         userPrompt: String,
🔴  116 |     ): GeneratedAdventureResponse = runRetrying(maxRetries = MAX_SENTENCE_MATCH_ATTEMPTS) {
🔴  117 |         val response = structuredPromptExecutor.executeResponse(
🔴  118 |             promptName = promptName,
🔴  119 |             systemPrompt = systemPrompt,
🔴  120 |             userPrompt = userPrompt,
⚪  121 |         )
⚪  122 | 
🔴  123 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  124 |             """
⚪  125 |                 Text adventure paragraph count mismatch:
🔴  126 |                     paragraphs=${response.paragraphs.size}
🔴  127 |                     translations=${response.translatedParagraphs.size}
🔴  128 |             """.trimIndent()
⚪  129 |         }
⚪  130 | 
🔴  131 |         val paragraphs = response.paragraphs.map { it.sentences }
🔴  132 |         val translatedParagraphs = response.translatedParagraphs.map { it.sentences }
🔴  133 |         paragraphs.forEachIndexed { index, sentences ->
🔴  134 |             val translatedSentences = translatedParagraphs[index]
🔴  135 |             check(sentences.size == translatedSentences.size) {
⚪  136 |                 """
🔴  137 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  138 |                         sentences=${sentences.size}
🔴  139 |                         translations=${translatedSentences.size}
🔴  140 |                 """.trimIndent()
⚪  141 |             }
🔴  142 |         }
⚪  143 | 
🔴  144 |         GeneratedAdventureResponse(
🔴  145 |             adventureId = adventureId,
🔴  146 |             title = response.title.trim(),
🔴  147 |             paragraphs = response.paragraphs,
🔴  148 |             translatedParagraphs = response.translatedParagraphs,
🔴  149 |             isEnding = response.isEnding,
⚪  150 |         )
⚪  151 |     }
```

## Lines 156-169

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:156-169`

```kotlin
⚪  156 |         block: suspend () -> T,
⚪  157 |     ): T {
🔴  158 |         repeat(maxRetries) { attemptIndex ->
⚪  159 |             try {
🔴  160 |                 return block()
⚪  161 |             } catch (throwable: Throwable) {
🔴  162 |                 if (attemptIndex == maxRetries - 1) {
⚪  163 |                     throw throwable
⚪  164 |                 }
⚪  165 |             }
🔴  166 |         }
🔴  167 |         error("Unreachable")
⚪  168 |     }
⚪  169 | 
```

## Lines 174-211

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:174-211`

```kotlin
⚪  174 | 
⚪  175 | 
🔴  176 | private data class GeneratedAdventureResponse(
🔴  177 |     val adventureId: String,
🔴  178 |     val title: String,
🔴  179 |     val paragraphs: List<TextAdventureStructuredParagraph>,
🔴  180 |     val translatedParagraphs: List<TextAdventureStructuredParagraph>,
🔴  181 |     val isEnding: Boolean,
⚪  182 | )
⚪  183 | 
🔴  184 | private fun GeneratedAdventureResponse.toRemoteResponse(): TextAdventureRemoteResponse = TextAdventureRemoteResponse(
🔴  185 |     adventureId = adventureId,
🔴  186 |     title = title,
🔴  187 |     sentences = paragraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
🔴  188 |     translatedSentences = translatedParagraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
🔴  189 |     isEnding = isEnding,
⚪  190 | )
⚪  191 | 
🔴  192 | @Serializable
⚪  193 | @SerialName("TextAdventureResponse")
⚪  194 | @LLMDescription("A single response from the text adventure narrator.")
⚪  195 | data class TextAdventureStructuredResponse(
🔴  196 |     @property:LLMDescription("Short, evocative title for the adventure.")
🔴  197 |     val title: String,
🔴  198 |     @property:LLMDescription("Narration paragraphs in the learning language.")
🔴  199 |     val paragraphs: List<TextAdventureStructuredParagraph>,
🔴  200 |     @property:LLMDescription("Translated paragraphs matching the narration paragraph order.")
🔴  201 |     val translatedParagraphs: List<TextAdventureStructuredParagraph>,
🔴  202 |     @property:LLMDescription("Whether the story ends after this response.")
🔴  203 |     val isEnding: Boolean,
⚪  204 | )
⚪  205 | 
🔴  206 | @Serializable
⚪  207 | @LLMDescription("A paragraph containing narration sentences.")
⚪  208 | data class TextAdventureStructuredParagraph(
🔴  209 |     @property:LLMDescription("Sentences in the paragraph.")
🔴  210 |     val sentences: List<String>,
⚪  211 | )
```
