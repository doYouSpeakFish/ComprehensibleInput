# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 117-122

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:117-122`

```kotlin
🟢  117 |             .any { it.adventureId == adventureId }
🟢  118 |         if (!isOwned) return null
🟡  119 |         val existing = adventureRepository.getAdventureMessages(adventureId) ?: return null
🟡  120 |         if (existing.messages.lastOrNull()?.isEnding == true) {
🟢  121 |             return RespondToAdventureForAccountResult.AdventureEnded
⚪  122 |         }
```

## Lines 128-136

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:128-136`

```kotlin
🟢  128 |             userMessage = userMessage,
⚪  129 |         )
🟡  130 |         val withUserMessage = adventureRepository.getAdventureMessages(adventureId) ?: return null
🟢  131 |         val history = withUserMessage.messages.mapNotNull { message ->
🟢  132 |             val text = message.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
🟡  133 |             if (text.isBlank()) {
🔴  134 |                 null
⚪  135 |             } else {
🟢  136 |                 TextAdventureHistoryMessage(
```

## Lines 217-226

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:217-226`

```kotlin
⚪  217 |         )
⚪  218 | 
🟡  219 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  220 |             """
⚪  221 |                 Text adventure paragraph count mismatch:
🔴  222 |                     paragraphs=${response.paragraphs.size}
🔴  223 |                     translations=${response.translatedParagraphs.size}
🔴  224 |             """.trimIndent()
⚪  225 |         }
⚪  226 | 
```

## Lines 229-238

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:229-238`

```kotlin
🟢  229 |         paragraphs.forEachIndexed { index, sentences ->
🟢  230 |             val translatedSentences = translatedParagraphs[index]
🟡  231 |             check(sentences.size == translatedSentences.size) {
⚪  232 |                 """
🔴  233 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  234 |                         sentences=${sentences.size}
🔴  235 |                         translations=${translatedSentences.size}
🔴  236 |                 """.trimIndent()
⚪  237 |             }
🟢  238 |         }
```

## Lines 252-265

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:252-265`

```kotlin
⚪  252 |         block: suspend () -> T,
⚪  253 |     ): T {
🟡  254 |         repeat(maxRetries) { attemptIndex ->
⚪  255 |             try {
🟢  256 |                 return block()
⚪  257 |             } catch (throwable: Throwable) {
🔴  258 |                 if (attemptIndex == maxRetries - 1) {
⚪  259 |                     throw throwable
⚪  260 |                 }
⚪  261 |             }
🔴  262 |         }
🔴  263 |         error("Unreachable")
⚪  264 |     }
⚪  265 | 
```

## Lines 286-290

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:286-290`

```kotlin
⚪  286 | )
⚪  287 | 
🔴  288 | @Serializable
⚪  289 | @SerialName("TextAdventureResponse")
⚪  290 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 300-304

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:300-304`

```kotlin
⚪  300 | )
⚪  301 | 
🔴  302 | @Serializable
⚪  303 | @LLMDescription("A paragraph containing narration sentences.")
⚪  304 | data class TextAdventureStructuredParagraph(
```

## Lines 308-315

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:308-315`

```kotlin
⚪  308 | 
⚪  309 | 
🔴  310 | @Serializable
🟢  311 | data class AdventureListRemoteResponse(val items: List<AdventureSummaryRemoteResponse>)
⚪  312 | 
🔴  313 | @Serializable
⚪  314 | data class AdventureSummaryRemoteResponse(
🟢  315 |     val id: String,
```
