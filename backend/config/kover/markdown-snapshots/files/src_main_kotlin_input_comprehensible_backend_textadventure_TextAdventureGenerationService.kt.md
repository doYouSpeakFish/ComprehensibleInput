# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 77-86

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:77-86`

```kotlin
⚪   77 |         )
⚪   78 | 
🟡   79 |         check(response.paragraphs.size == response.translatedParagraphs.size) {
⚪   80 |             """
⚪   81 |                 Text adventure paragraph count mismatch:
🔴   82 |                     paragraphs=${response.paragraphs.size}
🔴   83 |                     translations=${response.translatedParagraphs.size}
🔴   84 |             """.trimIndent()
⚪   85 |         }
⚪   86 | 
```

## Lines 112-125

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:112-125`

```kotlin
⚪  112 |         block: suspend () -> T,
⚪  113 |     ): T {
🟡  114 |         repeat(maxRetries) { attemptIndex ->
⚪  115 |             try {
🟢  116 |                 return block()
⚪  117 |             } catch (throwable: Throwable) {
🟡  118 |                 if (attemptIndex == maxRetries - 1) {
⚪  119 |                     throw throwable
⚪  120 |                 }
⚪  121 |             }
🟢  122 |         }
🔴  123 |         error("Unreachable")
⚪  124 |     }
⚪  125 | 
```

## Lines 129-133

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:129-133`

```kotlin
⚪  129 | }
⚪  130 | 
🔴  131 | @Serializable
⚪  132 | @SerialName("TextAdventureResponse")
⚪  133 | @LLMDescription("A single response from the text adventure narrator.")
```

## Lines 143-147

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureGenerationService.kt:143-147`

```kotlin
⚪  143 | )
⚪  144 | 
🔴  145 | @Serializable
⚪  146 | @LLMDescription("A paragraph containing narration sentences.")
⚪  147 | data class TextAdventureStructuredParagraph(
```
