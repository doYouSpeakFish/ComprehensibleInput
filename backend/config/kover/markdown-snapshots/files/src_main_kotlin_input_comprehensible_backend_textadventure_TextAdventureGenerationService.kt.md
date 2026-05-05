# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 55-59

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:55-59`

```kotlin
🟢   55 |                 translationLanguage = translationsLanguage,
🟢   56 |                 isEnding = response.isEnding,
🟡   57 |                 internalPlan = response.updatedPlan?.takeIf { it.isNotBlank() },
🟢   58 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪   59 |                     (paragraph, translatedParagraph) ->
```

## Lines 112-116

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:112-116`

```kotlin
🟢  112 |                 translationLanguage = translationsLanguage,
🟢  113 |                 isEnding = response.isEnding,
🟡  114 |                 internalPlan = response.updatedPlan?.takeIf { it.isNotBlank() } ?: existingPlan,
🟢  115 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪  116 |                     (paragraph, translatedParagraph) ->
```

## Lines 141-150

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:141-150`

```kotlin
⚪  141 |         )
⚪  142 | 
🟡  143 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  144 |             """
⚪  145 |                 Text adventure paragraph count mismatch:
🔴  146 |                     paragraphs=${response.paragraphs.size}
🔴  147 |                     translations=${response.translatedParagraphs.size}
🔴  148 |             """.trimIndent()
⚪  149 |         }
⚪  150 | 
```

## Lines 153-162

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:153-162`

```kotlin
🟢  153 |         paragraphs.forEachIndexed { index, sentences ->
🟢  154 |             val translatedSentences = translatedParagraphs[index]
🟡  155 |             check(sentences.size == translatedSentences.size) {
⚪  156 |                 """
🔴  157 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  158 |                         sentences=${sentences.size}
🔴  159 |                         translations=${translatedSentences.size}
🔴  160 |                 """.trimIndent()
⚪  161 |             }
🟢  162 |         }
```

## Lines 177-190

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:177-190`

```kotlin
⚪  177 |         block: suspend () -> T,
⚪  178 |     ): T {
🟡  179 |         repeat(maxRetries) { attemptIndex ->
⚪  180 |             try {
🟢  181 |                 return block()
⚪  182 |             } catch (throwable: Throwable) {
🔴  183 |                 if (attemptIndex == maxRetries - 1) {
⚪  184 |                     throw throwable
⚪  185 |                 }
⚪  186 |             }
🔴  187 |         }
🔴  188 |         error("Unreachable")
⚪  189 |     }
⚪  190 | 
```

## Lines 212-216

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:212-216`

```kotlin
⚪  212 | )
⚪  213 | 
🔴  214 | @Serializable
⚪  215 | @SerialName("TextAdventureResponse")
⚪  216 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 228-232

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:228-232`

```kotlin
⚪  228 | )
⚪  229 | 
🔴  230 | @Serializable
⚪  231 | @LLMDescription("A paragraph containing narration sentences.")
⚪  232 | data class TextAdventureStructuredParagraph(
```
