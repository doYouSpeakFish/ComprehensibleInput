package input.comprehensible.backend.textadventure

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.model.executeStructured
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

interface TextAdventureStructuredPromptExecutor {
    suspend fun executeResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventureStructuredResponse
}

class DefaultTextAdventureStructuredPromptExecutor(
    apiKey: String,
) : TextAdventureStructuredPromptExecutor {
    private val promptExecutor = simpleGoogleAIExecutor(apiKey)

    override suspend fun executeResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventureStructuredResponse = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
        prompt = prompt(promptName) {
            system(systemPrompt)
            user(userPrompt)
        },
        model = Gemini3_1FlashLite,
    ).getOrThrow().data
}

private val Gemini3_1FlashLite = LLModel(
    provider = LLMProvider.Google,
    id = "gemini-3.1-flash-lite-preview",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.Completion,
        LLMCapability.MultipleChoices,
        LLMCapability.Tools,
        LLMCapability.ToolChoice,
        LLMCapability.Vision.Image,
        LLMCapability.Vision.Video,
        LLMCapability.Audio,
        LLMCapability.Thinking,
        LLMCapability.Schema.JSON.Basic,
        LLMCapability.Schema.JSON.Standard,
    ),
    contextLength = 1_048_576,
    maxOutputTokens = 65_536,
)
