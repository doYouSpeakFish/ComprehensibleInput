# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 87-91

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:87-91`

```kotlin
⚪   87 |         history: List<TextAdventureHistoryMessage>,
⚪   88 |     ): TextAdventureRemoteResponse {
🟡   89 |         val existingPlan = adventureRepository.getAdventurePlan(adventureId).orEmpty()
🟢   90 |         val updatedPlan = requestAdventurePlan(
🟢   91 |             promptName = "text-adventure-plan-update",
```

## Lines 179-188

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:179-188`

```kotlin
⚪  179 |         )
⚪  180 | 
🟡  181 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  182 |             """
⚪  183 |                 Text adventure paragraph count mismatch:
🔴  184 |                     paragraphs=${response.paragraphs.size}
🔴  185 |                     translations=${response.translatedParagraphs.size}
🔴  186 |             """.trimIndent()
⚪  187 |         }
⚪  188 | 
```

## Lines 191-200

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:191-200`

```kotlin
🟢  191 |         paragraphs.forEachIndexed { index, sentences ->
🟢  192 |             val translatedSentences = translatedParagraphs[index]
🟡  193 |             check(sentences.size == translatedSentences.size) {
⚪  194 |                 """
🔴  195 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  196 |                         sentences=${sentences.size}
🔴  197 |                         translations=${translatedSentences.size}
🔴  198 |                 """.trimIndent()
⚪  199 |             }
🟢  200 |         }
```

## Lines 252-256

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:252-256`

```kotlin
⚪  252 |         )
⚪  253 | 
🟡  254 |         repeat(MAX_PLAN_ITERATIONS) { index ->
🟢  255 |             val round = index + 1
🟢  256 |             val writerResponse = requestAdventurePlan(
```

## Lines 288-299

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:288-299`

```kotlin
🟢  288 |                 learningLanguage = learningLanguage,
⚪  289 |             )
🟡  290 |             if (evaluation.isPlanAcceptable) {
🟢  291 |                 return writerResponse
⚪  292 |             }
🔴  293 |             feedback = evaluation.feedback
🔴  294 |         }
⚪  295 | 
🔴  296 |         return requestAdventurePlan(
🔴  297 |             promptName = "text-adventure-plan-write-final",
⚪  298 |             systemPrompt = """
⚪  299 |                 You are the final writer pass for an internal text-adventure plan.
```

## Lines 301-315

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:301-315`

```kotlin
⚪  301 |                 - plan
⚪  302 |                 - firstSceneGuidance
🔴  303 |                 Genre to use as a creative frame: $genre.
🔴  304 |                 Inspiration words (not strict obligations, only idea sparks): ${inspirationWords.joinToString()}.
🔴  305 |                 Respond in $learningLanguage.
🔴  306 |             """.trimIndent(),
⚪  307 |             userPrompt = """
⚪  308 |                 Current plan:
🔴  309 |                 $currentPlan
⚪  310 | 
⚪  311 |                 Latest reviewer feedback:
🔴  312 |                 ${evaluation.feedback}
🔴  313 |             """.trimIndent(),
⚪  314 |         )
⚪  315 |     }
```

## Lines 320-333

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:320-333`

```kotlin
⚪  320 |         block: suspend () -> T,
⚪  321 |     ): T {
🟡  322 |         repeat(maxRetries) { attemptIndex ->
⚪  323 |             try {
🟢  324 |                 return block()
⚪  325 |             } catch (throwable: Throwable) {
🔴  326 |                 if (attemptIndex == maxRetries - 1) {
⚪  327 |                     throw throwable
⚪  328 |                 }
⚪  329 |             }
🔴  330 |         }
🔴  331 |         error("Unreachable")
⚪  332 |     }
⚪  333 | 
```

## Lines 337-341

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:337-341`

```kotlin
⚪  337 |         const val INSPIRATION_WORD_COUNT = 8
⚪  338 | 
🟡  339 |         val adventureGenres = listOf(
🟢  340 |             "High Fantasy",
🟢  341 |             "Cyberpunk",
```

## Lines 350-354

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:350-354`

```kotlin
⚪  350 |         )
⚪  351 | 
🟡  352 |         val inspirationWordPool = listOf(
🟢  353 |             "lantern", "storm", "echo", "rust", "compass", "whisper", "mask", "clockwork",
🟢  354 |             "ivy", "mirror", "ash", "harbor", "glyph", "ember", "vault", "signal", "crow",
```

## Lines 375-379

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:375-379`

```kotlin
⚪  375 | )
⚪  376 | 
🔴  377 | @Serializable
⚪  378 | @SerialName("TextAdventureResponse")
⚪  379 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 389-393

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:389-393`

```kotlin
⚪  389 | )
⚪  390 | 
🔴  391 | @Serializable
⚪  392 | @LLMDescription("A paragraph containing narration sentences.")
⚪  393 | data class TextAdventureStructuredParagraph(
```

## Lines 396-400

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:396-400`

```kotlin
⚪  396 | )
⚪  397 | 
🔴  398 | @Serializable
⚪  399 | @LLMDescription("Internal planning output for text adventures.")
⚪  400 | data class TextAdventurePlanStructuredResponse(
```

## Lines 405-409

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:405-409`

```kotlin
⚪  405 | )
⚪  406 | 
🔴  407 | @Serializable
⚪  408 | @LLMDescription("Evaluation of whether the current plan meets required criteria.")
⚪  409 | data class TextAdventurePlanEvaluationStructuredResponse(
```

## Lines 411-414

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:411-414`

```kotlin
🟢  411 |     val isPlanAcceptable: Boolean,
🟢  412 |     @property:LLMDescription("Specific feedback about gaps or improvements.")
🔴  413 |     val feedback: String,
⚪  414 | )
```
