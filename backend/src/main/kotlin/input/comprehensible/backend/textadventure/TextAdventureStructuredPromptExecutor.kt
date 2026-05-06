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
    ): StructuredPromptResult<TextAdventureStructuredResponse>

    suspend fun executePlanResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): StructuredPromptResult<TextAdventurePlanStructuredResponse>

    suspend fun executePlanEvaluationResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): StructuredPromptResult<TextAdventurePlanEvaluationStructuredResponse>
}

class DefaultTextAdventureStructuredPromptExecutor(
    apiKey: String,
) : TextAdventureStructuredPromptExecutor {
    private val promptExecutor = simpleGoogleAIExecutor(apiKey)

    override suspend fun executeResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): StructuredPromptResult<TextAdventureStructuredResponse> {
        val result = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
            prompt = prompt(promptName) {
                system(systemPrompt)
                user(userPrompt)
            },
            model = Gemini3_1FlashLite,
        ).getOrThrow()
        return StructuredPromptResult(
            response = result.data,
            inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
            outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
        )
    }

    override suspend fun executePlanResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): StructuredPromptResult<TextAdventurePlanStructuredResponse> {
        val result = promptExecutor.executeStructured<TextAdventurePlanStructuredResponse>(
            prompt = prompt(promptName) {
                system(systemPrompt)
                user(userPrompt)
            },
            model = Gemini3_1FlashLite,
        ).getOrThrow()
        return StructuredPromptResult(
            response = result.data,
            inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
            outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
        )
    }

    override suspend fun executePlanEvaluationResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): StructuredPromptResult<TextAdventurePlanEvaluationStructuredResponse> {
        val result = promptExecutor.executeStructured<TextAdventurePlanEvaluationStructuredResponse>(
            prompt = prompt(promptName) {
                system(systemPrompt)
                user(userPrompt)
            },
            model = Gemini3_1FlashLite,
        ).getOrThrow()
        return StructuredPromptResult(
            response = result.data,
            inputTokens = result.message.metaInfo.inputTokensCount?.toLong() ?: 0L,
            outputTokens = result.message.metaInfo.outputTokensCount?.toLong() ?: 0L,
        )
    }
}

data class StructuredPromptResult<T>(
    val response: T,
    val inputTokens: Long,
    val outputTokens: Long,
)

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
