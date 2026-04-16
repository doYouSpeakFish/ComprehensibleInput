# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 14-21

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:14-21`

```kotlin
⚪   14 | }
⚪   15 | 
🔴   16 | class DefaultTextAdventureStructuredPromptExecutor(
⚪   17 |     apiKey: String,
⚪   18 | ) : TextAdventureStructuredPromptExecutor {
🔴   19 |     private val promptExecutor = simpleGoogleAIExecutor(apiKey)
⚪   20 | 
⚪   21 |     override suspend fun executeResponse(
```

## Lines 23-32

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:23-32`

```kotlin
⚪   23 |         systemPrompt: String,
⚪   24 |         userPrompt: String,
🔴   25 |     ): TextAdventureStructuredResponse = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
🔴   26 |         prompt = prompt(promptName) {
🔴   27 |             system(systemPrompt)
🔴   28 |             user(userPrompt)
⚪   29 |         },
🔴   30 |         model = GoogleModels.Gemini2_5Pro,
🔴   31 |     ).getOrThrow().data
⚪   32 | }
```
