package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse

class FakeTextAdventureStructuredPromptExecutor : TextAdventureStructuredPromptExecutor {
    data class Invocation(
        val promptName: String,
        val systemPrompt: String,
        val userPrompt: String,
    )

    private val queuedResponses = ArrayDeque<TextAdventureStructuredResponse>()
    private val queuedErrors = ArrayDeque<Throwable>()
    val invocations = mutableListOf<Invocation>()

    fun enqueueResponse(response: TextAdventureStructuredResponse) {
        queuedResponses.add(response)
    }

    fun enqueueError(error: Throwable) {
        queuedErrors.add(error)
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
}
