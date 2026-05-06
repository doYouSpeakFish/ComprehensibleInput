# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 93-97

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:93-97`

```kotlin
⚪   93 |     ): TextAdventureRemoteResponse {
🟢   94 |         val usage = UsageCounter()
🟡   95 |         val existingPlan = adventureRepository.getAdventurePlan(adventureId).orEmpty()
🟢   96 |         val updatedPlan = requestAdventurePlan(
🟢   97 |             promptName = "text-adventure-plan-update",
```

## Lines 192-201

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:192-201`

```kotlin
🟢  192 |         val response = responseResult.response
⚪  193 | 
🟡  194 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  195 |             """
⚪  196 |                 Text adventure paragraph count mismatch:
🔴  197 |                     paragraphs=${response.paragraphs.size}
🔴  198 |                     translations=${response.translatedParagraphs.size}
🔴  199 |             """.trimIndent()
⚪  200 |         }
⚪  201 | 
```

## Lines 204-213

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:204-213`

```kotlin
🟢  204 |         paragraphs.forEachIndexed { index, sentences ->
🟢  205 |             val translatedSentences = translatedParagraphs[index]
🟡  206 |             check(sentences.size == translatedSentences.size) {
⚪  207 |                 """
🔴  208 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  209 |                         sentences=${sentences.size}
🔴  210 |                         translations=${translatedSentences.size}
🔴  211 |                 """.trimIndent()
⚪  212 |             }
🟢  213 |         }
```

## Lines 268-272

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:268-272`

```kotlin
⚪  268 |         )
⚪  269 | 
🟡  270 |         repeat(MAX_PLAN_ITERATIONS) { index ->
🟢  271 |             val round = index + 1
🟢  272 |             val writerResponse = requestAdventurePlan(
```

## Lines 306-317

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:306-317`

```kotlin
🟢  306 |                 usage = usage,
⚪  307 |             )
🟡  308 |             if (evaluation.isPlanAcceptable) {
🟢  309 |                 return writerResponse
⚪  310 |             }
🔴  311 |             feedback = evaluation.feedback
🔴  312 |         }
⚪  313 | 
🔴  314 |         return requestAdventurePlan(
🔴  315 |             promptName = "text-adventure-plan-write-final",
⚪  316 |             systemPrompt = """
⚪  317 |                 You are the final writer pass for an internal text-adventure plan.
```

## Lines 319-334

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:319-334`

```kotlin
⚪  319 |                 - plan
⚪  320 |                 - firstSceneGuidance
🔴  321 |                 Genre to use as a creative frame: $genre.
🔴  322 |                 Inspiration words (not strict obligations, only idea sparks): ${inspirationWords.joinToString()}.
🔴  323 |                 Respond in $learningLanguage.
🔴  324 |             """.trimIndent(),
⚪  325 |             userPrompt = """
⚪  326 |                 Current plan:
🔴  327 |                 $currentPlan
⚪  328 | 
⚪  329 |                 Latest reviewer feedback:
🔴  330 |                 ${evaluation.feedback}
🔴  331 |             """.trimIndent(),
🔴  332 |             usage = usage,
⚪  333 |         )
⚪  334 |     }
```

## Lines 339-352

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:339-352`

```kotlin
⚪  339 |         block: suspend () -> T,
⚪  340 |     ): T {
🟡  341 |         repeat(maxRetries) { attemptIndex ->
⚪  342 |             try {
🟢  343 |                 return block()
⚪  344 |             } catch (throwable: Throwable) {
🔴  345 |                 if (attemptIndex == maxRetries - 1) {
⚪  346 |                     throw throwable
⚪  347 |                 }
⚪  348 |             }
🔴  349 |         }
🔴  350 |         error("Unreachable")
⚪  351 |     }
⚪  352 | 
```

## Lines 356-360

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:356-360`

```kotlin
⚪  356 |         const val INSPIRATION_WORD_COUNT = 8
⚪  357 | 
🟡  358 |         val adventureGenres = listOf(
🟢  359 |             "High Fantasy",
🟢  360 |             "Cyberpunk",
```

## Lines 369-373

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:369-373`

```kotlin
⚪  369 |         )
⚪  370 | 
🟡  371 |         val inspirationWordPool = listOf(
🟢  372 |             "lantern", "storm", "echo", "rust", "compass", "whisper", "mask", "clockwork",
🟢  373 |             "ivy", "mirror", "ash", "harbor", "glyph", "ember", "vault", "signal", "crow",
```

## Lines 404-408

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:404-408`

```kotlin
⚪  404 | )
⚪  405 | 
🔴  406 | @Serializable
⚪  407 | @SerialName("TextAdventureResponse")
⚪  408 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 418-422

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:418-422`

```kotlin
⚪  418 | )
⚪  419 | 
🔴  420 | @Serializable
⚪  421 | @LLMDescription("A paragraph containing narration sentences.")
⚪  422 | data class TextAdventureStructuredParagraph(
```

## Lines 425-429

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:425-429`

```kotlin
⚪  425 | )
⚪  426 | 
🔴  427 | @Serializable
⚪  428 | @LLMDescription("Internal planning output for text adventures.")
⚪  429 | data class TextAdventurePlanStructuredResponse(
```

## Lines 434-438

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:434-438`

```kotlin
⚪  434 | )
⚪  435 | 
🔴  436 | @Serializable
⚪  437 | @LLMDescription("Evaluation of whether the current plan meets required criteria.")
⚪  438 | data class TextAdventurePlanEvaluationStructuredResponse(
```

## Lines 440-443

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:440-443`

```kotlin
🟢  440 |     val isPlanAcceptable: Boolean,
🟢  441 |     @property:LLMDescription("Specific feedback about gaps or improvements.")
🔴  442 |     val feedback: String,
⚪  443 | )
```
