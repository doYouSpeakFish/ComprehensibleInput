# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 16-23

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:16-23`

```kotlin
⚪   16 | }
⚪   17 | 
🔴   18 | class DefaultTextAdventureStructuredPromptExecutor(
⚪   19 |     apiKey: String,
⚪   20 | ) : TextAdventureStructuredPromptExecutor {
🔴   21 |     private val promptExecutor = simpleGoogleAIExecutor(apiKey)
⚪   22 | 
⚪   23 |     override suspend fun executeResponse(
```

## Lines 25-54

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:25-54`

```kotlin
⚪   25 |         systemPrompt: String,
⚪   26 |         userPrompt: String,
🔴   27 |     ): TextAdventureStructuredResponse = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
🔴   28 |         prompt = prompt(promptName) {
🔴   29 |             system(systemPrompt)
🔴   30 |             user(userPrompt)
⚪   31 |         },
🔴   32 |         model = Gemini3_1FlashLite,
🔴   33 |     ).getOrThrow().data
⚪   34 | }
⚪   35 | 
🔴   36 | private val Gemini3_1FlashLite = LLModel(
🔴   37 |     provider = LLMProvider.Google,
🔴   38 |     id = "gemini-3.1-flash-lite-preview",
🔴   39 |     capabilities = listOf(
🔴   40 |         LLMCapability.Temperature,
🔴   41 |         LLMCapability.Completion,
🔴   42 |         LLMCapability.MultipleChoices,
🔴   43 |         LLMCapability.Tools,
🔴   44 |         LLMCapability.ToolChoice,
🔴   45 |         LLMCapability.Vision.Image,
🔴   46 |         LLMCapability.Vision.Video,
🔴   47 |         LLMCapability.Audio,
🔴   48 |         LLMCapability.Thinking,
🔴   49 |         LLMCapability.Schema.JSON.Basic,
🔴   50 |         LLMCapability.Schema.JSON.Standard,
⚪   51 |     ),
🔴   52 |     contextLength = 1_048_576,
🔴   53 |     maxOutputTokens = 65_536,
⚪   54 | )
```
