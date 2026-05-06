# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 121-130

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:121-130`

```kotlin
⚪  121 |         )
⚪  122 | 
🟡  123 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  124 |             """
⚪  125 |                 Text adventure paragraph count mismatch:
🔴  126 |                     paragraphs=${response.paragraphs.size}
🔴  127 |                     translations=${response.translatedParagraphs.size}
🔴  128 |             """.trimIndent()
⚪  129 |         }
⚪  130 | 
```

## Lines 133-142

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:133-142`

```kotlin
🟢  133 |         paragraphs.forEachIndexed { index, sentences ->
🟢  134 |             val translatedSentences = translatedParagraphs[index]
🟡  135 |             check(sentences.size == translatedSentences.size) {
⚪  136 |                 """
🔴  137 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  138 |                         sentences=${sentences.size}
🔴  139 |                         translations=${translatedSentences.size}
🔴  140 |                 """.trimIndent()
⚪  141 |             }
🟢  142 |         }
```

## Lines 153-164

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:153-164`

```kotlin
⚪  153 | 
⚪  154 |     private fun validatePlanRichness(plan: TextAdventureWorldPlan) {
🟡  155 |         check(plan.premise.isNotBlank())
🟡  156 |         check(plan.playerObjective.isNotBlank())
🟡  157 |         check(plan.setting.locationName.isNotBlank())
🟡  158 |         check(plan.setting.mood.isNotBlank())
🟡  159 |         check(plan.setting.constraints.isNotEmpty())
🟡  160 |         check(plan.keyNpcs.size >= 2)
🟡  161 |         check(plan.inventory.isNotEmpty())
🟡  162 |         check(plan.openThreads.isNotEmpty())
⚪  163 |     }
⚪  164 | 
```

## Lines 168-181

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:168-181`

```kotlin
⚪  168 |         block: suspend () -> T,
⚪  169 |     ): T {
🟡  170 |         repeat(maxRetries) { attemptIndex ->
⚪  171 |             try {
🟢  172 |                 return block()
⚪  173 |             } catch (throwable: Throwable) {
🔴  174 |                 if (attemptIndex == maxRetries - 1) {
⚪  175 |                     throw throwable
⚪  176 |                 }
⚪  177 |             }
🔴  178 |         }
🔴  179 |         error("Unreachable")
⚪  180 |     }
⚪  181 | 
```

## Lines 203-207

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:203-207`

```kotlin
⚪  203 | )
⚪  204 | 
🔴  205 | @Serializable
⚪  206 | @SerialName("TextAdventureResponse")
⚪  207 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 219-223

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:219-223`

```kotlin
⚪  219 | )
⚪  220 | 
🔴  221 | @Serializable
⚪  222 | private data class ContinueTextAdventurePrompt(
🟢  223 |     val adventureId: String,
```

## Lines 229-233

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:229-233`

```kotlin
⚪  229 | )
⚪  230 | 
🔴  231 | @Serializable
⚪  232 | @LLMDescription("A paragraph containing narration sentences.")
⚪  233 | data class TextAdventureStructuredParagraph(
```
