# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 125-134

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:125-134`

```kotlin
⚪  125 |         )
⚪  126 | 
🟡  127 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  128 |             """
⚪  129 |                 Text adventure paragraph count mismatch:
🔴  130 |                     paragraphs=${response.paragraphs.size}
🔴  131 |                     translations=${response.translatedParagraphs.size}
🔴  132 |             """.trimIndent()
⚪  133 |         }
⚪  134 | 
```

## Lines 137-146

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:137-146`

```kotlin
🟢  137 |         paragraphs.forEachIndexed { index, sentences ->
🟢  138 |             val translatedSentences = translatedParagraphs[index]
🟡  139 |             check(sentences.size == translatedSentences.size) {
⚪  140 |                 """
🔴  141 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  142 |                         sentences=${sentences.size}
🔴  143 |                         translations=${translatedSentences.size}
🔴  144 |                 """.trimIndent()
⚪  145 |             }
🟢  146 |         }
```

## Lines 157-168

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:157-168`

```kotlin
⚪  157 | 
⚪  158 |     private fun validatePlanRichness(plan: TextAdventureWorldPlan) {
🟡  159 |         check(plan.premise.isNotBlank())
🟡  160 |         check(plan.playerObjective.isNotBlank())
🟡  161 |         check(plan.setting.locationName.isNotBlank())
🟡  162 |         check(plan.setting.mood.isNotBlank())
🟡  163 |         check(plan.setting.constraints.isNotEmpty())
🟡  164 |         check(plan.keyNpcs.size >= 2)
🟡  165 |         check(plan.inventory.isNotEmpty())
🟡  166 |         check(plan.openThreads.isNotEmpty())
⚪  167 |     }
⚪  168 | 
```

## Lines 172-185

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:172-185`

```kotlin
⚪  172 |         block: suspend () -> T,
⚪  173 |     ): T {
🟡  174 |         repeat(maxRetries) { attemptIndex ->
⚪  175 |             try {
🟢  176 |                 return block()
⚪  177 |             } catch (throwable: Throwable) {
🔴  178 |                 if (attemptIndex == maxRetries - 1) {
⚪  179 |                     throw throwable
⚪  180 |                 }
⚪  181 |             }
🔴  182 |         }
🔴  183 |         error("Unreachable")
⚪  184 |     }
⚪  185 | 
```

## Lines 207-211

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:207-211`

```kotlin
⚪  207 | )
⚪  208 | 
🔴  209 | @Serializable
⚪  210 | @SerialName("TextAdventureResponse")
⚪  211 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 223-227

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:223-227`

```kotlin
⚪  223 | )
⚪  224 | 
🔴  225 | @Serializable
⚪  226 | private data class ContinueTextAdventurePrompt(
🟢  227 |     val adventureId: String,
```

## Lines 233-237

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:233-237`

```kotlin
⚪  233 | )
⚪  234 | 
🔴  235 | @Serializable
⚪  236 | @LLMDescription("A paragraph containing narration sentences.")
⚪  237 | data class TextAdventureStructuredParagraph(
```
