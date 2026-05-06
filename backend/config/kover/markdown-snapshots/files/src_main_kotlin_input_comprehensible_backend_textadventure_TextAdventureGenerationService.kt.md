# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 75-79

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:75-79`

```kotlin
⚪   75 |         history: List<TextAdventureHistoryMessage>,
⚪   76 |     ): TextAdventureRemoteResponse {
🟡   77 |         val existingPlan = adventureRepository.getAdventurePlan(adventureId).orEmpty()
🟢   78 |         val updatedPlan = requestAdventurePlan(
🟢   79 |             promptName = "text-adventure-plan-update",
```

## Lines 162-171

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:162-171`

```kotlin
⚪  162 |         )
⚪  163 | 
🟡  164 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  165 |             """
⚪  166 |                 Text adventure paragraph count mismatch:
🔴  167 |                     paragraphs=${response.paragraphs.size}
🔴  168 |                     translations=${response.translatedParagraphs.size}
🔴  169 |             """.trimIndent()
⚪  170 |         }
⚪  171 | 
```

## Lines 174-183

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:174-183`

```kotlin
🟢  174 |         paragraphs.forEachIndexed { index, sentences ->
🟢  175 |             val translatedSentences = translatedParagraphs[index]
🟡  176 |             check(sentences.size == translatedSentences.size) {
⚪  177 |                 """
🔴  178 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  179 |                         sentences=${sentences.size}
🔴  180 |                         translations=${translatedSentences.size}
🔴  181 |                 """.trimIndent()
⚪  182 |             }
🟢  183 |         }
```

## Lines 231-235

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:231-235`

```kotlin
⚪  231 |         )
⚪  232 | 
🟡  233 |         repeat(MAX_PLAN_ITERATIONS) { index ->
🟢  234 |             val round = index + 1
🟢  235 |             val writerResponse = requestAdventurePlan(
```

## Lines 265-276

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:265-276`

```kotlin
🟢  265 |                 learningLanguage = learningLanguage,
⚪  266 |             )
🟡  267 |             if (evaluation.isPlanAcceptable) {
🟢  268 |                 return writerResponse
⚪  269 |             }
🔴  270 |             feedback = evaluation.feedback
🔴  271 |         }
⚪  272 | 
🔴  273 |         return requestAdventurePlan(
🔴  274 |             promptName = "text-adventure-plan-write-final",
⚪  275 |             systemPrompt = """
⚪  276 |                 You are the final writer pass for an internal text-adventure plan.
```

## Lines 278-290

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:278-290`

```kotlin
⚪  278 |                 - plan
⚪  279 |                 - firstSceneGuidance
🔴  280 |                 Respond in $learningLanguage.
🔴  281 |             """.trimIndent(),
⚪  282 |             userPrompt = """
⚪  283 |                 Current plan:
🔴  284 |                 $currentPlan
⚪  285 | 
⚪  286 |                 Latest reviewer feedback:
🔴  287 |                 ${evaluation.feedback}
🔴  288 |             """.trimIndent(),
⚪  289 |         )
⚪  290 |     }
```

## Lines 295-308

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:295-308`

```kotlin
⚪  295 |         block: suspend () -> T,
⚪  296 |     ): T {
🟡  297 |         repeat(maxRetries) { attemptIndex ->
⚪  298 |             try {
🟢  299 |                 return block()
⚪  300 |             } catch (throwable: Throwable) {
🔴  301 |                 if (attemptIndex == maxRetries - 1) {
⚪  302 |                     throw throwable
⚪  303 |                 }
⚪  304 |             }
🔴  305 |         }
🔴  306 |         error("Unreachable")
⚪  307 |     }
⚪  308 | 
```

## Lines 330-334

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:330-334`

```kotlin
⚪  330 | )
⚪  331 | 
🔴  332 | @Serializable
⚪  333 | @SerialName("TextAdventureResponse")
⚪  334 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 344-348

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:344-348`

```kotlin
⚪  344 | )
⚪  345 | 
🔴  346 | @Serializable
⚪  347 | @LLMDescription("A paragraph containing narration sentences.")
⚪  348 | data class TextAdventureStructuredParagraph(
```

## Lines 351-355

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:351-355`

```kotlin
⚪  351 | )
⚪  352 | 
🔴  353 | @Serializable
⚪  354 | @LLMDescription("Internal planning output for text adventures.")
⚪  355 | data class TextAdventurePlanStructuredResponse(
```

## Lines 360-364

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:360-364`

```kotlin
⚪  360 | )
⚪  361 | 
🔴  362 | @Serializable
⚪  363 | @LLMDescription("Evaluation of whether the current plan meets required criteria.")
⚪  364 | data class TextAdventurePlanEvaluationStructuredResponse(
```

## Lines 366-369

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:366-369`

```kotlin
🟢  366 |     val isPlanAcceptable: Boolean,
🟢  367 |     @property:LLMDescription("Specific feedback about gaps or improvements.")
🔴  368 |     val feedback: String,
⚪  369 | )
```
