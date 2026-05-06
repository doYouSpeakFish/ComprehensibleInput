package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventurePlanStructuredResponse
import input.comprehensible.backend.textadventure.TextAdventurePlanEvaluationStructuredResponse
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse

class FakeTextAdventureStructuredPromptExecutor : TextAdventureStructuredPromptExecutor {
    data class Invocation(
        val promptName: String,
        val systemPrompt: String,
        val userPrompt: String,
    )

    private val queuedResponses = ArrayDeque<TextAdventureStructuredResponse>()
    private val queuedPlanResponses = ArrayDeque<TextAdventurePlanStructuredResponse>()
    private val queuedPlanEvaluationResponses = ArrayDeque<TextAdventurePlanEvaluationStructuredResponse>()
    private val queuedErrors = ArrayDeque<Throwable>()
    val invocations = mutableListOf<Invocation>()

    fun enqueueResponse(response: TextAdventureStructuredResponse) {
        queuedResponses.add(response)
    }

    fun enqueueError(error: Throwable) {
        queuedErrors.add(error)
    }

    fun enqueuePlanResponse(response: TextAdventurePlanStructuredResponse) {
        queuedPlanResponses.add(response)
    }

    fun enqueuePlanEvaluationResponse(response: TextAdventurePlanEvaluationStructuredResponse) {
        queuedPlanEvaluationResponses.add(response)
    }

    override suspend fun executeResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventureStructuredResponse {
        invocations.add(
            Invocation(
                promptName = promptName,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
            )
        )

        queuedErrors.removeFirstOrNull()?.let { throw it }
        return queuedResponses.removeFirstOrNull()
            ?: TextAdventureStructuredResponse(
                title = "Fallback Adventure",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("You wait."))
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Esperas."))
                ),
                isEnding = false,
            )
    }

    override suspend fun executePlanResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventurePlanStructuredResponse {
        invocations.add(
            Invocation(
                promptName = promptName,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
            )
        )
        queuedErrors.removeFirstOrNull()?.let { throw it }
        return queuedPlanResponses.removeFirstOrNull()
            ?: TextAdventurePlanStructuredResponse(
                plan = "Fallback plan",
                firstSceneGuidance = "Open with a decision point.",
            )
    }

    override suspend fun executePlanEvaluationResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventurePlanEvaluationStructuredResponse {
        invocations.add(
            Invocation(
                promptName = promptName,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
            )
        )
        queuedErrors.removeFirstOrNull()?.let { throw it }
        return queuedPlanEvaluationResponses.removeFirstOrNull()
            ?: TextAdventurePlanEvaluationStructuredResponse(
                isPlanAcceptable = true,
                feedback = "Plan meets criteria.",
            )
    }
}
