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

## Lines 38-52

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:38-52`

```kotlin
⚪   38 |         userPrompt: String,
⚪   39 |     ): StructuredPromptResult<TextAdventureStructuredResponse> {
🔴   40 |         val result = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
🔴   41 |             prompt = prompt(promptName) {
🔴   42 |                 system(systemPrompt)
🔴   43 |                 user(userPrompt)
⚪   44 |             },
🔴   45 |             model = Gemini3_1FlashLite,
🔴   46 |         ).getOrThrow()
🔴   47 |         return StructuredPromptResult(
🔴   48 |             response = result.data,
🔴   49 |             inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
🔴   50 |             outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
⚪   51 |         )
⚪   52 |     }
```

## Lines 57-71

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:57-71`

```kotlin
⚪   57 |         userPrompt: String,
⚪   58 |     ): StructuredPromptResult<TextAdventurePlanStructuredResponse> {
🔴   59 |         val result = promptExecutor.executeStructured<TextAdventurePlanStructuredResponse>(
🔴   60 |             prompt = prompt(promptName) {
🔴   61 |                 system(systemPrompt)
🔴   62 |                 user(userPrompt)
⚪   63 |             },
🔴   64 |             model = Gemini3_1FlashLite,
🔴   65 |         ).getOrThrow()
🔴   66 |         return StructuredPromptResult(
🔴   67 |             response = result.data,
🔴   68 |             inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
🔴   69 |             outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
⚪   70 |         )
⚪   71 |     }
```

## Lines 76-90

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:76-90`

```kotlin
⚪   76 |         userPrompt: String,
⚪   77 |     ): StructuredPromptResult<TextAdventurePlanEvaluationStructuredResponse> {
🔴   78 |         val result = promptExecutor.executeStructured<TextAdventurePlanEvaluationStructuredResponse>(
🔴   79 |             prompt = prompt(promptName) {
🔴   80 |                 system(systemPrompt)
🔴   81 |                 user(userPrompt)
⚪   82 |             },
🔴   83 |             model = Gemini3_1FlashLite,
🔴   84 |         ).getOrThrow()
🔴   85 |         return StructuredPromptResult(
🔴   86 |             response = result.data,
🔴   87 |             inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
🔴   88 |             outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
⚪   89 |         )
⚪   90 |     }
```

## Lines 97-117

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureStructuredPromptExecutor.kt:97-117`

```kotlin
⚪   97 | )
⚪   98 | 
🔴   99 | private val Gemini3_1FlashLite = LLModel(
🔴  100 |     provider = LLMProvider.Google,
🔴  101 |     id = "gemini-3.1-flash-lite-preview",
🔴  102 |     capabilities = listOf(
🔴  103 |         LLMCapability.Temperature,
🔴  104 |         LLMCapability.Completion,
🔴  105 |         LLMCapability.MultipleChoices,
🔴  106 |         LLMCapability.Tools,
🔴  107 |         LLMCapability.ToolChoice,
🔴  108 |         LLMCapability.Vision.Image,
🔴  109 |         LLMCapability.Vision.Video,
🔴  110 |         LLMCapability.Audio,
🔴  111 |         LLMCapability.Thinking,
🔴  112 |         LLMCapability.Schema.JSON.Basic,
🔴  113 |         LLMCapability.Schema.JSON.Standard,
⚪  114 |     ),
🔴  115 |     contextLength = 1_048_576,
🔴  116 |     maxOutputTokens = 65_536,
⚪  117 | )
```
