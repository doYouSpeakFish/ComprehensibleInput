# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 28-35

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:28-35`

```kotlin
⚪   28 | }
⚪   29 | 
🔴   30 | class DefaultTextAdventureStructuredPromptExecutor(
⚪   31 |     apiKey: String,
⚪   32 | ) : TextAdventureStructuredPromptExecutor {
🔴   33 |     private val promptExecutor = simpleGoogleAIExecutor(apiKey)
⚪   34 | 
⚪   35 |     override suspend fun executeResponse(
```

## Lines 37-47

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:37-47`

```kotlin
⚪   37 |         systemPrompt: String,
⚪   38 |         userPrompt: String,
🔴   39 |     ): TextAdventureStructuredResponse = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
🔴   40 |         prompt = prompt(promptName) {
🔴   41 |             system(systemPrompt)
🔴   42 |             user(userPrompt)
⚪   43 |         },
🔴   44 |         model = Gemini3_1FlashLite,
🔴   45 |     ).getOrThrow().data
⚪   46 | 
⚪   47 |     override suspend fun executePlanResponse(
```

## Lines 49-59

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:49-59`

```kotlin
⚪   49 |         systemPrompt: String,
⚪   50 |         userPrompt: String,
🔴   51 |     ): TextAdventurePlanStructuredResponse = promptExecutor.executeStructured<TextAdventurePlanStructuredResponse>(
🔴   52 |         prompt = prompt(promptName) {
🔴   53 |             system(systemPrompt)
🔴   54 |             user(userPrompt)
⚪   55 |         },
🔴   56 |         model = Gemini3_1FlashLite,
🔴   57 |     ).getOrThrow().data
⚪   58 | 
⚪   59 |     override suspend fun executePlanEvaluationResponse(
```

## Lines 61-90

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:61-90`

```kotlin
⚪   61 |         systemPrompt: String,
⚪   62 |         userPrompt: String,
🔴   63 |     ): TextAdventurePlanEvaluationStructuredResponse = promptExecutor.executeStructured<TextAdventurePlanEvaluationStructuredResponse>(
🔴   64 |         prompt = prompt(promptName) {
🔴   65 |             system(systemPrompt)
🔴   66 |             user(userPrompt)
⚪   67 |         },
🔴   68 |         model = Gemini3_1FlashLite,
🔴   69 |     ).getOrThrow().data
⚪   70 | }
⚪   71 | 
🔴   72 | private val Gemini3_1FlashLite = LLModel(
🔴   73 |     provider = LLMProvider.Google,
🔴   74 |     id = "gemini-3.1-flash-lite-preview",
🔴   75 |     capabilities = listOf(
🔴   76 |         LLMCapability.Temperature,
🔴   77 |         LLMCapability.Completion,
🔴   78 |         LLMCapability.MultipleChoices,
🔴   79 |         LLMCapability.Tools,
🔴   80 |         LLMCapability.ToolChoice,
🔴   81 |         LLMCapability.Vision.Image,
🔴   82 |         LLMCapability.Vision.Video,
🔴   83 |         LLMCapability.Audio,
🔴   84 |         LLMCapability.Thinking,
🔴   85 |         LLMCapability.Schema.JSON.Basic,
🔴   86 |         LLMCapability.Schema.JSON.Standard,
⚪   87 |     ),
🔴   88 |     contextLength = 1_048_576,
🔴   89 |     maxOutputTokens = 65_536,
⚪   90 | )
```
