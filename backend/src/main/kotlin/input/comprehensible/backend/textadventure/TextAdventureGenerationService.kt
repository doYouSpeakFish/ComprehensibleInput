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
        val planResponse = generateInitialAdventurePlan(learningLanguage)

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
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                adventurePlan = planResponse.plan,
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

    private suspend fun evaluateAdventurePlan(
        userPrompt: String,
        learningLanguage: String,
    ): TextAdventurePlanEvaluationStructuredResponse = structuredPromptExecutor.executePlanEvaluationResponse(
        promptName = "text-adventure-plan-evaluate",
        systemPrompt = """
            You are evaluating an internal text-adventure plan.
            Evaluate whether the plan satisfies all of these criteria:
            - The plan sets the stage for player agency and does not dictate exact outcomes.
            - It includes premise, exactly 5 locations, NPCs and motivations, key items, player inventory, and a grand finale plan.
            - The 5 locations include a mixture of puzzles, role play, action, setbacks, and exploration.
            - Each location clearly describes what is present at that location.
            - No element is underspecified (every puzzle includes mechanics and intended solution).
            Return:
            - isPlanAcceptable: true only if all criteria are fully met.
            - feedback: concrete improvement guidance.
            Respond in $learningLanguage.
        """.trimIndent(),
        userPrompt = userPrompt,
    )

    private suspend fun generateInitialAdventurePlan(learningLanguage: String): TextAdventurePlanStructuredResponse {
        var currentPlan = ""
        var feedback = "Create the first draft."
        var evaluation = TextAdventurePlanEvaluationStructuredResponse(
            isPlanAcceptable = false,
            feedback = "No evaluation yet.",
        )

        repeat(MAX_PLAN_ITERATIONS) { index ->
            val round = index + 1
            val writerResponse = requestAdventurePlan(
                promptName = "text-adventure-plan-write",
                systemPrompt = """
                    You write internal plans for a text-adventure narrator.
                    The plan is private and not shown to the player.
                    Build or improve the plan using reviewer feedback.
                    The plan must set the stage and preserve player agency.
                    Required content: premise; exactly 5 locations; NPCs and motivations; key items; player inventory; grand finale.
                    Each location must clearly describe what is there.
                    The 5-location set must include puzzle, role-play, action, setback, and exploration content.
                    Every puzzle must include how it works and intended solution.
                    Return updated plan and first-scene guidance.
                    Respond in $learningLanguage.
                """.trimIndent(),
                userPrompt = """
                    Planning round: $round of $MAX_PLAN_ITERATIONS
                    Current plan:
                    $currentPlan

                    Reviewer feedback:
                    $feedback
                """.trimIndent(),
            )
            currentPlan = writerResponse.plan
            evaluation = evaluateAdventurePlan(
                userPrompt = """
                    Planning round: $round of $MAX_PLAN_ITERATIONS
                    Plan to evaluate:
                    $currentPlan
                """.trimIndent(),
                learningLanguage = learningLanguage,
            )
            if (evaluation.isPlanAcceptable) {
                return writerResponse
            }
            feedback = evaluation.feedback
        }

        return requestAdventurePlan(
            promptName = "text-adventure-plan-write-final",
            systemPrompt = """
                You are the final writer pass for an internal text-adventure plan.
                Incorporate the latest reviewer feedback and return:
                - plan
                - firstSceneGuidance
                Respond in $learningLanguage.
            """.trimIndent(),
            userPrompt = """
                Current plan:
                $currentPlan

                Latest reviewer feedback:
                ${evaluation.feedback}
            """.trimIndent(),
        )
    }

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
        const val MAX_PLAN_ITERATIONS = 5
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

@Serializable
@LLMDescription("Evaluation of whether the current plan meets required criteria.")
data class TextAdventurePlanEvaluationStructuredResponse(
    @property:LLMDescription("True when the plan satisfies all required criteria.")
    val isPlanAcceptable: Boolean,
    @property:LLMDescription("Specific feedback about gaps or improvements.")
    val feedback: String,
)
