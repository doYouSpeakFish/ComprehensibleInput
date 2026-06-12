package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.AdventurePlanLocationResponse
import input.comprehensible.backend.textadventure.AdventurePlanNpcResponse
import input.comprehensible.backend.textadventure.AdventurePlanStructuredResponse
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.UserMessageStructuredResponse

class FakeTextAdventureStructuredPromptExecutor : TextAdventureStructuredPromptExecutor {
    data class Invocation(
        val promptName: String,
        val systemPrompt: String,
        val userPrompt: String,
    )

    private val queuedResponses = ArrayDeque<TextAdventureStructuredResponse>()
    private val queuedUserMessageResponses = ArrayDeque<UserMessageStructuredResponse>()
    private val queuedPlanResponses = ArrayDeque<AdventurePlanStructuredResponse>()
    private val queuedErrors = ArrayDeque<Throwable>()
    val invocations = mutableListOf<Invocation>()

    fun enqueueResponse(response: TextAdventureStructuredResponse) {
        queuedResponses.add(response)
    }

    fun enqueueUserMessageResponse(response: UserMessageStructuredResponse) {
        queuedUserMessageResponses.add(response)
    }

    fun enqueuePlanResponse(response: AdventurePlanStructuredResponse) {
        queuedPlanResponses.add(response)
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
                translatedTitle = "Fallback Adventure (translated)",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("You wait."))
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Esperas."))
                ),
                isEnding = false,
            )
    }

    override suspend fun executeUserMessageResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): UserMessageStructuredResponse {
        invocations.add(
            Invocation(
                promptName = promptName,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
            )
        )

        queuedErrors.removeFirstOrNull()?.let { throw it }
        return queuedUserMessageResponses.removeFirstOrNull()
            ?: UserMessageStructuredResponse(
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf(userPrompt))
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf(userPrompt))
                ),
            )
    }

    override suspend fun executePlanResponse(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): AdventurePlanStructuredResponse {
        invocations.add(
            Invocation(
                promptName = promptName,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
            )
        )

        queuedErrors.removeFirstOrNull()?.let { throw it }
        return queuedPlanResponses.removeFirstOrNull()
            ?: AdventurePlanStructuredResponse(
                characterDescription = "A wandering traveller far from home.",
                inventory = "A lantern, a knife and a folded map.",
                hook = "A stranger begs for help finding a missing child.",
                truthBehindHook = "The stranger is the one who hid the child.",
                coreChallenge = "Uncover the deception and free the child before nightfall.",
                locations = listOf(
                    AdventurePlanLocationResponse(
                        name = "The Crooked Inn",
                        description = "A timber inn at the village edge; a trapdoor behind the bar leads to a cellar.",
                    ),
                ),
                npcs = listOf(
                    AdventurePlanNpcResponse(
                        name = "Mara",
                        description = "The anxious stranger who hid the child in the inn's cellar.",
                    ),
                ),
            )
    }
}
