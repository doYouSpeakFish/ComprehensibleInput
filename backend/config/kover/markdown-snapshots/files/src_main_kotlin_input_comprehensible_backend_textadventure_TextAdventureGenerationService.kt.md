# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 117-131

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:117-131`

```kotlin
🟢  117 |             .any { it.adventureId == adventureId }
🟢  118 |         if (!isOwned) return null
🟡  119 |         val existing = adventureRepository.getAdventureMessages(adventureId) ?: return null
🟡  120 |         if (existing.messages.lastOrNull()?.isEnding == true) {
🟢  121 |             return RespondToAdventureForAccountResult.AdventureEnded
⚪  122 |         }
🟢  123 |         val history = existing.messages.mapNotNull { message ->
🟢  124 |             val text = message.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
🟡  125 |             if (text.isBlank()) {
🔴  126 |                 null
⚪  127 |             } else {
🟢  128 |                 TextAdventureHistoryMessage(
🟡  129 |                     role = if (message.sender == "AI") "assistant" else "user",
🟢  130 |                     text = text,
⚪  131 |                 )
```

## Lines 209-218

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:209-218`

```kotlin
⚪  209 |         )
⚪  210 | 
🟡  211 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  212 |             """
⚪  213 |                 Text adventure paragraph count mismatch:
🔴  214 |                     paragraphs=${response.paragraphs.size}
🔴  215 |                     translations=${response.translatedParagraphs.size}
🔴  216 |             """.trimIndent()
⚪  217 |         }
⚪  218 | 
```

## Lines 221-230

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:221-230`

```kotlin
🟢  221 |         paragraphs.forEachIndexed { index, sentences ->
🟢  222 |             val translatedSentences = translatedParagraphs[index]
🟡  223 |             check(sentences.size == translatedSentences.size) {
⚪  224 |                 """
🔴  225 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  226 |                         sentences=${sentences.size}
🔴  227 |                         translations=${translatedSentences.size}
🔴  228 |                 """.trimIndent()
⚪  229 |             }
🟢  230 |         }
```

## Lines 244-257

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:244-257`

```kotlin
⚪  244 |         block: suspend () -> T,
⚪  245 |     ): T {
🟡  246 |         repeat(maxRetries) { attemptIndex ->
⚪  247 |             try {
🟢  248 |                 return block()
⚪  249 |             } catch (throwable: Throwable) {
🔴  250 |                 if (attemptIndex == maxRetries - 1) {
⚪  251 |                     throw throwable
⚪  252 |                 }
⚪  253 |             }
🔴  254 |         }
🔴  255 |         error("Unreachable")
⚪  256 |     }
⚪  257 | 
```

## Lines 278-282

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:278-282`

```kotlin
⚪  278 | )
⚪  279 | 
🔴  280 | @Serializable
⚪  281 | @SerialName("TextAdventureResponse")
⚪  282 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 292-296

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:292-296`

```kotlin
⚪  292 | )
⚪  293 | 
🔴  294 | @Serializable
⚪  295 | @LLMDescription("A paragraph containing narration sentences.")
⚪  296 | data class TextAdventureStructuredParagraph(
```

## Lines 300-307

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:300-307`

```kotlin
⚪  300 | 
⚪  301 | 
🔴  302 | @Serializable
🟢  303 | data class AdventureListRemoteResponse(val items: List<AdventureSummaryRemoteResponse>)
⚪  304 | 
🔴  305 | @Serializable
⚪  306 | data class AdventureSummaryRemoteResponse(
🟢  307 |     val id: String,
```
