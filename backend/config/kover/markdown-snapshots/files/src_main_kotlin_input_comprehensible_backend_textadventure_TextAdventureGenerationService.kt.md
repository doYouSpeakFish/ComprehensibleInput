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

## Lines 156-169

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:156-169`

```kotlin
⚪  156 |         block: suspend () -> T,
⚪  157 |     ): T {
🟡  158 |         repeat(maxRetries) { attemptIndex ->
⚪  159 |             try {
🟢  160 |                 return block()
⚪  161 |             } catch (throwable: Throwable) {
🟡  162 |                 if (attemptIndex == maxRetries - 1) {
⚪  163 |                     throw throwable
⚪  164 |                 }
⚪  165 |             }
🟢  166 |         }
🔴  167 |         error("Unreachable")
⚪  168 |     }
⚪  169 | 
```

## Lines 190-194

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:190-194`

```kotlin
⚪  190 | )
⚪  191 | 
🔴  192 | @Serializable
⚪  193 | @SerialName("TextAdventureResponse")
⚪  194 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 204-208

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:204-208`

```kotlin
⚪  204 | )
⚪  205 | 
🔴  206 | @Serializable
⚪  207 | @LLMDescription("A paragraph containing narration sentences.")
⚪  208 | data class TextAdventureStructuredParagraph(
```
