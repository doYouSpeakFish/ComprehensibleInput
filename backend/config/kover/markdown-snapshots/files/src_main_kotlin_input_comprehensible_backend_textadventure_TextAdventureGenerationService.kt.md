# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 99-103

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:99-103`

```kotlin
⚪   99 |         history: List<TextAdventureHistoryMessage>,
⚪  100 |     ): TextAdventureRemoteResponse {
🟡  101 |         val existingPlan = adventureRepository.getAdventurePlan(adventureId).orEmpty()
🟢  102 |         val updatedPlan = requestAdventurePlan(
🟢  103 |             promptName = "text-adventure-plan-update",
```

## Lines 186-195

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:186-195`

```kotlin
⚪  186 |         )
⚪  187 | 
🟡  188 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  189 |             """
⚪  190 |                 Text adventure paragraph count mismatch:
🔴  191 |                     paragraphs=${response.paragraphs.size}
🔴  192 |                     translations=${response.translatedParagraphs.size}
🔴  193 |             """.trimIndent()
⚪  194 |         }
⚪  195 | 
```

## Lines 198-207

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:198-207`

```kotlin
🟢  198 |         paragraphs.forEachIndexed { index, sentences ->
🟢  199 |             val translatedSentences = translatedParagraphs[index]
🟡  200 |             check(sentences.size == translatedSentences.size) {
⚪  201 |                 """
🔴  202 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  203 |                         sentences=${sentences.size}
🔴  204 |                         translations=${translatedSentences.size}
🔴  205 |                 """.trimIndent()
⚪  206 |             }
🟢  207 |         }
```

## Lines 231-244

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:231-244`

```kotlin
⚪  231 |         block: suspend () -> T,
⚪  232 |     ): T {
🟡  233 |         repeat(maxRetries) { attemptIndex ->
⚪  234 |             try {
🟢  235 |                 return block()
⚪  236 |             } catch (throwable: Throwable) {
🔴  237 |                 if (attemptIndex == maxRetries - 1) {
⚪  238 |                     throw throwable
⚪  239 |                 }
⚪  240 |             }
🔴  241 |         }
🔴  242 |         error("Unreachable")
⚪  243 |     }
⚪  244 | 
```

## Lines 265-269

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:265-269`

```kotlin
⚪  265 | )
⚪  266 | 
🔴  267 | @Serializable
⚪  268 | @SerialName("TextAdventureResponse")
⚪  269 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 279-283

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:279-283`

```kotlin
⚪  279 | )
⚪  280 | 
🔴  281 | @Serializable
⚪  282 | @LLMDescription("A paragraph containing narration sentences.")
⚪  283 | data class TextAdventureStructuredParagraph(
```

## Lines 286-290

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:286-290`

```kotlin
⚪  286 | )
⚪  287 | 
🔴  288 | @Serializable
⚪  289 | @LLMDescription("Internal planning output for text adventures.")
⚪  290 | data class TextAdventurePlanStructuredResponse(
```
