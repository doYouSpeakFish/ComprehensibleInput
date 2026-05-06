# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 22-29

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:22-29`

```kotlin
⚪   22 | }
⚪   23 | 
🔴   24 | class DefaultTextAdventureStructuredPromptExecutor(
⚪   25 |     apiKey: String,
⚪   26 | ) : TextAdventureStructuredPromptExecutor {
🔴   27 |     private val promptExecutor = simpleGoogleAIExecutor(apiKey)
⚪   28 | 
⚪   29 |     override suspend fun executeResponse(
```

## Lines 31-41

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:31-41`

```kotlin
⚪   31 |         systemPrompt: String,
⚪   32 |         userPrompt: String,
🔴   33 |     ): TextAdventureStructuredResponse = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
🔴   34 |         prompt = prompt(promptName) {
🔴   35 |             system(systemPrompt)
🔴   36 |             user(userPrompt)
⚪   37 |         },
🔴   38 |         model = Gemini3_1FlashLite,
🔴   39 |     ).getOrThrow().data
⚪   40 | 
⚪   41 |     override suspend fun executePlanResponse(
```

## Lines 43-72

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:43-72`

```kotlin
⚪   43 |         systemPrompt: String,
⚪   44 |         userPrompt: String,
🔴   45 |     ): TextAdventurePlanStructuredResponse = promptExecutor.executeStructured<TextAdventurePlanStructuredResponse>(
🔴   46 |         prompt = prompt(promptName) {
🔴   47 |             system(systemPrompt)
🔴   48 |             user(userPrompt)
⚪   49 |         },
🔴   50 |         model = Gemini3_1FlashLite,
🔴   51 |     ).getOrThrow().data
⚪   52 | }
⚪   53 | 
🔴   54 | private val Gemini3_1FlashLite = LLModel(
🔴   55 |     provider = LLMProvider.Google,
🔴   56 |     id = "gemini-3.1-flash-lite-preview",
🔴   57 |     capabilities = listOf(
🔴   58 |         LLMCapability.Temperature,
🔴   59 |         LLMCapability.Completion,
🔴   60 |         LLMCapability.MultipleChoices,
🔴   61 |         LLMCapability.Tools,
🔴   62 |         LLMCapability.ToolChoice,
🔴   63 |         LLMCapability.Vision.Image,
🔴   64 |         LLMCapability.Vision.Video,
🔴   65 |         LLMCapability.Audio,
🔴   66 |         LLMCapability.Thinking,
🔴   67 |         LLMCapability.Schema.JSON.Basic,
🔴   68 |         LLMCapability.Schema.JSON.Standard,
⚪   69 |     ),
🔴   70 |     contextLength = 1_048_576,
🔴   71 |     maxOutputTokens = 65_536,
⚪   72 | )
```
