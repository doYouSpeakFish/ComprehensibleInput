# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 114-119

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:114-119`

```kotlin
⚪  114 |         parentMessageId: String,
⚪  115 |     ): TextAdventureMessageRemoteResponse? {
🟡  116 |         val existing = getAdventureMessagesForAccount(accountId = accountId, adventureId = adventureId) ?: return null
🟡  117 |         val parent = existing.messages.firstOrNull { it.id == parentMessageId } ?: return null
🟢  118 |         val messageId = messageIdProvider()
🟢  119 |         respondToUser(
```

## Lines 121-125

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:121-125`

```kotlin
🟢  121 |             learningLanguage = existing.learningLanguage,
🟢  122 |             translationsLanguage = existing.translationsLanguage,
🟡  123 |             userMessage = parent.text.orEmpty(),
🟢  124 |             history = existing.toHistory(),
🟢  125 |             accountId = accountId,
```

## Lines 127-131

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:127-131`

```kotlin
🟢  127 |             parentMessageId = parentMessageId,
⚪  128 |         )
🟡  129 |         return getAdventureMessagesForAccount(accountId, adventureId)?.messages?.firstOrNull { it.id == messageId }
⚪  130 |     }
⚪  131 | 
```

## Lines 135-164

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:135-164`

```kotlin
⚪  135 |         userMessage: String,
⚪  136 |     ): RespondToAdventureForAccountResult? {
🔴  137 |         val existing = getAdventureMessagesForAccount(accountId = accountId, adventureId = adventureId) ?: return null
🔴  138 |         if (existing.messages.lastOrNull()?.isEnding == true) {
🔴  139 |             return RespondToAdventureForAccountResult.AdventureEnded
⚪  140 |         }
🔴  141 |         val userResponse = adventureRepository.appendUserMessage(
🔴  142 |             PersistedUserAdventureMessage(
🔴  143 |                 adventureId = adventureId,
🔴  144 |                 accountId = accountId,
🔴  145 |                 parentMessageId = existing.messages.last().id,
🔴  146 |                 messageId = messageIdProvider(),
🔴  147 |                 learningLanguage = existing.learningLanguage,
🔴  148 |                 translationLanguage = existing.translationsLanguage,
🔴  149 |                 userMessage = userMessage,
⚪  150 |             )
🔴  151 |         ) ?: return null
🔴  152 |         val response = respondToUser(
🔴  153 |             adventureId = adventureId,
🔴  154 |             learningLanguage = existing.learningLanguage,
🔴  155 |             translationsLanguage = existing.translationsLanguage,
🔴  156 |             userMessage = userMessage,
🔴  157 |             history = getAdventureMessages(adventureId)?.toHistory().orEmpty(),
🔴  158 |             accountId = accountId,
🔴  159 |             messageId = messageIdProvider(),
🔴  160 |             parentMessageId = userResponse.id,
⚪  161 |         )
🔴  162 |         return RespondToAdventureForAccountResult.Success(response)
⚪  163 |     }
⚪  164 | 
```

## Lines 226-235

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:226-235`

```kotlin
⚪  226 |         )
⚪  227 | 
🟡  228 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  229 |             """
⚪  230 |                 Text adventure paragraph count mismatch:
🔴  231 |                     paragraphs=${response.paragraphs.size}
🔴  232 |                     translations=${response.translatedParagraphs.size}
🔴  233 |             """.trimIndent()
⚪  234 |         }
⚪  235 | 
```

## Lines 238-247

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:238-247`

```kotlin
🟢  238 |         paragraphs.forEachIndexed { index, sentences ->
🟢  239 |             val translatedSentences = translatedParagraphs[index]
🟡  240 |             check(sentences.size == translatedSentences.size) {
⚪  241 |                 """
🔴  242 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  243 |                         sentences=${sentences.size}
🔴  244 |                         translations=${translatedSentences.size}
🔴  245 |                 """.trimIndent()
⚪  246 |             }
🟢  247 |         }
```

## Lines 261-274

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:261-274`

```kotlin
⚪  261 |         block: suspend () -> T,
⚪  262 |     ): T {
🟡  263 |         repeat(maxRetries) { attemptIndex ->
⚪  264 |             try {
🟢  265 |                 return block()
⚪  266 |             } catch (throwable: Throwable) {
🔴  267 |                 if (attemptIndex == maxRetries - 1) {
⚪  268 |                     throw throwable
⚪  269 |                 }
⚪  270 |             }
🔴  271 |         }
🔴  272 |         error("Unreachable")
⚪  273 |     }
⚪  274 | 
```

## Lines 288-293

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:288-293`

```kotlin
🟢  288 | private fun TextAdventureMessagesRemoteResponse.toHistory(): List<TextAdventureHistoryMessage> = messages.mapNotNull { message ->
🟢  289 |     val text = message.text ?: message.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
🟡  290 |     if (text.isBlank()) {
🔴  291 |         null
⚪  292 |     } else {
🟢  293 |         TextAdventureHistoryMessage(
```

## Lines 324-328

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:324-328`

```kotlin
⚪  324 |     )
⚪  325 | 
🔴  326 | @Serializable
⚪  327 | @SerialName("TextAdventureResponse")
⚪  328 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 338-342

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:338-342`

```kotlin
⚪  338 | )
⚪  339 | 
🔴  340 | @Serializable
⚪  341 | @LLMDescription("A paragraph containing narration sentences.")
⚪  342 | data class TextAdventureStructuredParagraph(
```

## Lines 345-352

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:345-352`

```kotlin
⚪  345 | )
⚪  346 | 
🔴  347 | @Serializable
🟢  348 | data class AdventureListRemoteResponse(val items: List<AdventureSummaryRemoteResponse>)
⚪  349 | 
🔴  350 | @Serializable
⚪  351 | data class AdventureSummaryRemoteResponse(
🟢  352 |     val id: String,
```

## Lines 358-362

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:358-362`

```kotlin
⚪  358 | 
⚪  359 | sealed interface RespondToAdventureForAccountResult {
🔴  360 |     data class Success(val response: TextAdventureRemoteResponse) : RespondToAdventureForAccountResult
🔴  361 |     data object AdventureEnded : RespondToAdventureForAccountResult
⚪  362 | }
```
