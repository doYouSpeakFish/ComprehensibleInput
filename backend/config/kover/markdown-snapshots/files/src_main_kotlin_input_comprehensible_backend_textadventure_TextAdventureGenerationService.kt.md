# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 47-51

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:47-51`

```kotlin
🟢   47 |                 translationLanguage = translationsLanguage,
🟢   48 |                 isEnding = response.isEnding,
🟡   49 |                 internalPlan = response.updatedPlan?.takeIf { it.isNotBlank() },
🟢   50 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪   51 |                     (paragraph, translatedParagraph) ->
```

## Lines 99-103

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:99-103`

```kotlin
🟢   99 |                 translationLanguage = translationsLanguage,
🟢  100 |                 isEnding = response.isEnding,
🟡  101 |                 internalPlan = response.updatedPlan?.takeIf { it.isNotBlank() } ?: existingPlan,
🟢  102 |                 paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
⚪  103 |                     (paragraph, translatedParagraph) ->
```

## Lines 128-137

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:128-137`

```kotlin
⚪  128 |         )
⚪  129 | 
🟡  130 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪  131 |             """
⚪  132 |                 Text adventure paragraph count mismatch:
🔴  133 |                     paragraphs=${response.paragraphs.size}
🔴  134 |                     translations=${response.translatedParagraphs.size}
🔴  135 |             """.trimIndent()
⚪  136 |         }
⚪  137 | 
```

## Lines 140-149

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:140-149`

```kotlin
🟢  140 |         paragraphs.forEachIndexed { index, sentences ->
🟢  141 |             val translatedSentences = translatedParagraphs[index]
🟡  142 |             check(sentences.size == translatedSentences.size) {
⚪  143 |                 """
🔴  144 |                     Text adventure sentence count mismatch in paragraph $index:
🔴  145 |                         sentences=${sentences.size}
🔴  146 |                         translations=${translatedSentences.size}
🔴  147 |                 """.trimIndent()
⚪  148 |             }
🟢  149 |         }
```

## Lines 164-177

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:164-177`

```kotlin
⚪  164 |         block: suspend () -> T,
⚪  165 |     ): T {
🟡  166 |         repeat(maxRetries) { attemptIndex ->
⚪  167 |             try {
🟢  168 |                 return block()
⚪  169 |             } catch (throwable: Throwable) {
🔴  170 |                 if (attemptIndex == maxRetries - 1) {
⚪  171 |                     throw throwable
⚪  172 |                 }
⚪  173 |             }
🔴  174 |         }
🔴  175 |         error("Unreachable")
⚪  176 |     }
⚪  177 | 
```

## Lines 199-203

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:199-203`

```kotlin
⚪  199 | )
⚪  200 | 
🔴  201 | @Serializable
⚪  202 | @SerialName("TextAdventureResponse")
⚪  203 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 215-219

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:215-219`

```kotlin
⚪  215 | )
⚪  216 | 
🔴  217 | @Serializable
⚪  218 | @LLMDescription("A paragraph containing narration sentences.")
⚪  219 | data class TextAdventureStructuredParagraph(
```
