package input.comprehensible.backend.textadventure

import ai.koog.agents.core.tools.annotations.LLMDescription
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

class TextAdventureGenerationService(
    private val structuredPromptExecutor: TextAdventureStructuredPromptExecutor,
    private val adventureRepository: AdventureRepository,
) {
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        val adventureId = UUID.randomUUID().toString()
        val planResponse = requestAdventurePlan(
            promptName = "text-adventure-plan-start",
            systemPrompt = """
                You are creating an internal adventure plan for a text adventure narrator.
                The plan is not shown to the player.
                Create the initial plan using alternating rounds:
                Round 1: create/update the plan.
                Round 2: review whether the plan is sufficient for an exciting and interesting full adventure, and whether details are missing.
                Continue alternating update/review rounds until the review says the plan is acceptable or 5 planning rounds have occurred.
                The plan must set the stage for player agency and must not dictate exactly what will happen.
                The plan must include:
                - Premise.
                - Exactly 5 locations.
                - For each location: what is there, NPCs present (if any), and at least one element from puzzle/role play/action/setback/exploration across the full set.
                - NPC motivations.
                - Important items.
                - Player starting inventory.
                - A grand finale concept.
                - No underspecified elements: every mentioned puzzle must describe how it works and the intended solution.
                The review step must explicitly verify all criteria above.
                After planning is accepted, or after 5 rounds, produce first-scene guidance for generating the first part of the adventure.
                Respond in $learningLanguage.
            """.trimIndent(),
            userPrompt = "Create and iterate the internal plan, then provide first-scene guidance.",
        )

        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-start",
            systemPrompt = """
                You are a text adventure narrator.
                You will receive internal plan context that must remain private.
                Generate the opening scene in $learningLanguage.
                Provide a short, evocative title for the adventure.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
                The story should not end yet, so set isEnding to false.
            """.trimIndent(),
            userPrompt = """
                Internal plan:
                ${planResponse.plan}

                First-scene guidance:
                ${planResponse.firstSceneGuidance}
            """.trimIndent(),
        )
        adventureRepository.saveAdventurePlan(adventureId = adventureId, adventurePlan = planResponse.plan)
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                isEnding = response.isEnding,
                paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
                    (paragraph, translatedParagraph) ->
                    PersistedAdventureParagraph(
                        sentences = paragraph.sentences.map(String::trim),
                        translatedSentences = translatedParagraph.sentences.map(String::trim),
                    )
                },
            )
        )
        return response.toRemoteResponse()
    }

    suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse {
        val existingPlan = adventureRepository.getAdventurePlan(adventureId).orEmpty()
        val updatedPlan = requestAdventurePlan(
            promptName = "text-adventure-plan-update",
            systemPrompt = """
                You are maintaining an internal adventure plan as a private guide for a text adventure narrator.
                Update the existing plan in a single pass based on the player's latest action and the recent history.
                Do not perform multi-round review in this update step.
                Keep the plan stage-setting and preserve player agency.
                Respond in $learningLanguage.
            """.trimIndent(),
            userPrompt = """
                Existing plan:
                $existingPlan

                Latest player action:
                $userMessage

                Recent history:
                ${history.joinToString(separator = "\n") { "${it.role}: ${it.text}" }}
            """.trimIndent(),
        )
        adventureRepository.saveAdventurePlan(adventureId = adventureId, adventurePlan = updatedPlan.plan)

        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-continue",
            systemPrompt = """
                You are a text adventure narrator continuing an ongoing story.
                You will receive a JSON request containing the adventure context and chat history.
                You will also receive internal plan context that must remain private.
                Respond to the player in $learningLanguage.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Keep the title consistent with the story so far.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
            """.trimIndent(),
            userPrompt = """
                Internal plan:
                ${updatedPlan.plan}

                Adventure request:
                ${json.encodeToString(
                    ContinueTextAdventureRequest(
                    adventureId = adventureId,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage,
                    userMessage = userMessage,
                    history = history,
                )
                )}
            """.trimIndent(),
        )
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                isEnding = response.isEnding,
                paragraphs = response.paragraphs.zip(response.translatedParagraphs).map {
                    (paragraph, translatedParagraph) ->
                    PersistedAdventureParagraph(
                        sentences = paragraph.sentences.map(String::trim),
                        translatedSentences = translatedParagraph.sentences.map(String::trim),
                    )
                },
            )
        )
        return response.toRemoteResponse()
    }


    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? =
        adventureRepository.getAdventureMessages(adventureId)

    private suspend fun requestAdventureResponse(
        adventureId: String,
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): GeneratedAdventureResponse = runRetrying(maxRetries = MAX_SENTENCE_MATCH_ATTEMPTS) {
        val response = structuredPromptExecutor.executeResponse(
            promptName = promptName,
            systemPrompt = systemPrompt,
            userPrompt = userPrompt,
        )

        check(response.paragraphs.size == response.translatedParagraphs.size) {
            """
                Text adventure paragraph count mismatch:
                    paragraphs=${response.paragraphs.size}
                    translations=${response.translatedParagraphs.size}
            """.trimIndent()
        }

        val paragraphs = response.paragraphs.map { it.sentences }
        val translatedParagraphs = response.translatedParagraphs.map { it.sentences }
        paragraphs.forEachIndexed { index, sentences ->
            val translatedSentences = translatedParagraphs[index]
            check(sentences.size == translatedSentences.size) {
                """
                    Text adventure sentence count mismatch in paragraph $index:
                        sentences=${sentences.size}
                        translations=${translatedSentences.size}
                """.trimIndent()
            }
        }

        GeneratedAdventureResponse(
            adventureId = adventureId,
            title = response.title.trim(),
            paragraphs = response.paragraphs,
            translatedParagraphs = response.translatedParagraphs,
            isEnding = response.isEnding,
        )
    }

    private suspend fun requestAdventurePlan(
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventurePlanStructuredResponse = structuredPromptExecutor.executePlanResponse(
        promptName = promptName,
        systemPrompt = systemPrompt,
        userPrompt = userPrompt,
    )

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> runRetrying(
        maxRetries: Int,
        block: suspend () -> T,
    ): T {
        repeat(maxRetries) { attemptIndex ->
            try {
                return block()
            } catch (throwable: Throwable) {
                if (attemptIndex == maxRetries - 1) {
                    throw throwable
                }
            }
        }
        error("Unreachable")
    }

    private companion object {
        const val MAX_SENTENCE_MATCH_ATTEMPTS = 3
    }
}


private data class GeneratedAdventureResponse(
    val adventureId: String,
    val title: String,
    val paragraphs: List<TextAdventureStructuredParagraph>,
    val translatedParagraphs: List<TextAdventureStructuredParagraph>,
    val isEnding: Boolean,
)

private fun GeneratedAdventureResponse.toRemoteResponse(): TextAdventureRemoteResponse = TextAdventureRemoteResponse(
    adventureId = adventureId,
    title = title,
    sentences = paragraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
    translatedSentences = translatedParagraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
    isEnding = isEnding,
)

@Serializable
@SerialName("TextAdventureResponse")
@LLMDescription("A single response from the text adventure narrator.")
data class TextAdventureStructuredResponse(
    @property:LLMDescription("Short, evocative title for the adventure.")
    val title: String,
    @property:LLMDescription("Narration paragraphs in the learning language.")
    val paragraphs: List<TextAdventureStructuredParagraph>,
    @property:LLMDescription("Translated paragraphs matching the narration paragraph order.")
    val translatedParagraphs: List<TextAdventureStructuredParagraph>,
    @property:LLMDescription("Whether the story ends after this response.")
    val isEnding: Boolean,
)

@Serializable
@LLMDescription("A paragraph containing narration sentences.")
data class TextAdventureStructuredParagraph(
    @property:LLMDescription("Sentences in the paragraph.")
    val sentences: List<String>,
)

@Serializable
@LLMDescription("Internal planning output for text adventures.")
data class TextAdventurePlanStructuredResponse(
    @property:LLMDescription("The internal adventure plan kept private from users.")
    val plan: String,
    @property:LLMDescription("Guidance for generating the first part of the adventure narrative.")
    val firstSceneGuidance: String = "",
)
