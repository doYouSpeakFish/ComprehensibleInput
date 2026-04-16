package input.comprehensible.backend.textadventure

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.structure.executeStructured

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
        model = GoogleModels.Gemini2_5Pro,
    ).getOrThrow().data
}
