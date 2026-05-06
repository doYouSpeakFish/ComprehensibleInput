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
import kotlin.random.Random

class TextAdventureGenerationService(
    private val structuredPromptExecutor: TextAdventureStructuredPromptExecutor,
    private val adventureRepository: AdventureRepository,
    private val random: Random = Random.Default,
) {
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        val usage = UsageCounter()
        val adventureId = UUID.randomUUID().toString()
        val genre = adventureGenres.random(random)
        val inspirationWords = inspirationWordPool
            .shuffled(random)
            .take(INSPIRATION_WORD_COUNT)
        val planResponse = generateInitialAdventurePlan(
            learningLanguage = learningLanguage,
            genre = genre,
            inspirationWords = inspirationWords,
            usage = usage,
        )

        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-start",
            systemPrompt = """
                You are a text adventure narrator.
                You will receive internal plan context that must remain private.
                Never tell the player what to do. Set the scene and narrate outcomes of player actions.
                Generate the opening scene in $learningLanguage.
                Clearly tell the player who they are and what items are currently in their inventory.
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
            usage = usage,
        )
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                adventurePlan = planResponse.plan,
                inputTokensUsed = usage.inputTokens,
                outputTokensUsed = usage.outputTokens,
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
        val usage = UsageCounter()
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
            usage = usage,
        )
        adventureRepository.saveAdventurePlan(adventureId = adventureId, adventurePlan = updatedPlan.plan)

        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-continue",
            systemPrompt = """
                You are a text adventure narrator continuing an ongoing story.
                You will receive a JSON request containing the adventure context and chat history.
                You will also receive internal plan context that must remain private.
                Never tell the player what to do. Set the scene and narrate outcomes of player actions.
                Evaluate whether requested player actions are possible in this world state:
                - does the player have required items,
                - does the action make sense in the current location and situation.
                If an action is impossible, explain why in-story and continue with sensible consequences.
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
            usage = usage,
        )
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                inputTokensUsed = usage.inputTokens,
                outputTokensUsed = usage.outputTokens,
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
        usage: UsageCounter,
    ): GeneratedAdventureResponse = runRetrying(maxRetries = MAX_SENTENCE_MATCH_ATTEMPTS) {
        usage.record(input = "$systemPrompt\n$userPrompt")
        val response = structuredPromptExecutor.executeResponse(
            promptName = promptName,
            systemPrompt = systemPrompt,
            userPrompt = userPrompt,
        )
        usage.record(output = response.toString())

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
        usage: UsageCounter,
    ): TextAdventurePlanStructuredResponse = structuredPromptExecutor.executePlanResponse(
        promptName = promptName,
        systemPrompt = systemPrompt,
        userPrompt = userPrompt,
    ).also { usage.record(input = "$systemPrompt\n$userPrompt", output = it.toString()) }

    private suspend fun evaluateAdventurePlan(
        userPrompt: String,
        learningLanguage: String,
        usage: UsageCounter,
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
    ).also { usage.record(input = userPrompt, output = it.toString()) }

    private suspend fun generateInitialAdventurePlan(
        learningLanguage: String,
        genre: String,
        inspirationWords: List<String>,
        usage: UsageCounter,
    ): TextAdventurePlanStructuredResponse {
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
                    Genre to use as a creative frame: $genre.
                    Inspiration words (not strict obligations, only idea sparks): ${inspirationWords.joinToString()}.
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
                usage = usage,
            )
            currentPlan = writerResponse.plan
            evaluation = evaluateAdventurePlan(
                userPrompt = """
                    Planning round: $round of $MAX_PLAN_ITERATIONS
                    Plan to evaluate:
                    $currentPlan
                """.trimIndent(),
                learningLanguage = learningLanguage,
                usage = usage,
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
                Genre to use as a creative frame: $genre.
                Inspiration words (not strict obligations, only idea sparks): ${inspirationWords.joinToString()}.
                Respond in $learningLanguage.
            """.trimIndent(),
            userPrompt = """
                Current plan:
                $currentPlan

                Latest reviewer feedback:
                ${evaluation.feedback}
            """.trimIndent(),
            usage = usage,
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
        const val INSPIRATION_WORD_COUNT = 8

        val adventureGenres = listOf(
            "High Fantasy",
            "Cyberpunk",
            "Mystery Noir",
            "Post-Apocalyptic Survival",
            "Steampunk",
            "Space Opera",
            "Gothic Horror",
            "Mythic Adventure",
            "Archaeological Thriller",
            "Whimsical Fairy Tale",
        )

        val inspirationWordPool = listOf(
            "lantern", "storm", "echo", "rust", "compass", "whisper", "mask", "clockwork",
            "ivy", "mirror", "ash", "harbor", "glyph", "ember", "vault", "signal", "crow",
            "river", "throne", "key", "frost", "market", "cathedral", "starfall", "tide",
        )
    }
}

private data class UsageCounter(
    var inputTokens: Long = 0,
    var outputTokens: Long = 0,
) {
    fun record(input: String? = null, output: String? = null) {
        if (input != null) inputTokens += estimateTokenCount(input)
        if (output != null) outputTokens += estimateTokenCount(output)
    }
}

private fun estimateTokenCount(text: String): Long = (text.length / 4.0).toLong().coerceAtLeast(1L)


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
