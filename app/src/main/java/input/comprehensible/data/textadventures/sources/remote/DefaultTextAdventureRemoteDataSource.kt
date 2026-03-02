package input.comprehensible.data.textadventures.sources.remote

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.structure.executeStructured
import input.comprehensible.BuildConfig
import input.comprehensible.util.runRetrying
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.UUID

class DefaultTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    private companion object {
        const val MAX_SENTENCE_MATCH_ATTEMPTS = 3
    }

    private val promptExecutor = simpleGoogleAIExecutor(BuildConfig.KOOG_API_KEY)
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    override suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse = requestAdventureResponse(
        adventureId = UUID.randomUUID().toString(),
        promptName = "text-adventure-start",
        systemPrompt = """
            You are a text adventure narrator.
            Generate the opening scene as 1 to 3 short sentences in $learningLanguage.
            Provide a short, evocative title for the adventure.
            Provide translations of each sentence in $translationsLanguage with a matching count and order.
            Do not include extra commentary outside the requested fields.
            Avoid markdown and keep punctuation natural for the language.
            The story should not end yet, so set isEnding to false.
        """.trimIndent(),
        userPrompt = "Start a new adventure.",
    )

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse = requestAdventureResponse(
        adventureId = adventureId,
        promptName = "text-adventure-continue",
        systemPrompt = """
            You are a text adventure narrator continuing an ongoing story.
            You will receive a JSON request containing the adventure context and chat history.
            Respond to the player in 1 to 3 short sentences in $learningLanguage.
            Provide translations of each sentence in $translationsLanguage with a matching count and order.
            Keep the title consistent with the story so far.
            Do not include extra commentary outside the requested fields.
            Avoid markdown and keep punctuation natural for the language.
        """.trimIndent(),
        userPrompt = json.encodeToString(
            TextAdventureContinueRequest(
                adventureId = adventureId,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
                userMessage = userMessage,
                history = history,
            )
        ),
    )

    private suspend fun requestAdventureResponse(
        adventureId: String,
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ) = runRetrying(
        maxRetries = MAX_SENTENCE_MATCH_ATTEMPTS,
        onFailure = { retries, e ->
            Timber.w(
                e,
                "Failed attempt to generate text adventure message with " +
                        "${MAX_SENTENCE_MATCH_ATTEMPTS - retries} retries remaining"
            )
        }
    ) {
        val response = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
            prompt = prompt(promptName) {
                system(systemPrompt)
                user(userPrompt)
            },
            model = GoogleModels.Gemini2_5Pro,
        ).getOrThrow().data

        check(response.sentences.size == response.translatedSentences.size) {
            """
                Text adventure sentence count mismatch:
                    sentences=${response.sentences.size}
                    translations=${response.translatedSentences.size}
            """.trimIndent()
        }

        TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = response.title.trim(),
            sentences = response.sentences.map { it.trim() },
            translatedSentences = response.translatedSentences.map { it.trim() },
            isEnding = response.isEnding,
        )
    }
        .getOrThrow()

    @Serializable
    @SerialName("TextAdventureResponse")
    @LLMDescription("A single response from the text adventure narrator.")
    private data class TextAdventureStructuredResponse(
        @property:LLMDescription("Short, evocative title for the adventure.")
        val title: String,
        @property:LLMDescription("Narration sentences in the learning language.")
        val sentences: List<String>,
        @property:LLMDescription("Translations matching the narration sentence order.")
        val translatedSentences: List<String>,
        @property:LLMDescription("Whether the story ends after this response.")
        val isEnding: Boolean,
    )

    @Serializable
    @SerialName("TextAdventureContinueRequest")
    @LLMDescription("REST-style request payload for continuing a text adventure.")
    private data class TextAdventureContinueRequest(
        @property:LLMDescription("Unique adventure identifier.")
        val adventureId: String,
        @property:LLMDescription("Language used for narration.")
        val learningLanguage: String,
        @property:LLMDescription("Language used for translations.")
        val translationsLanguage: String,
        @property:LLMDescription("Player input message.")
        val userMessage: String,
        @property:LLMDescription("Conversation history in chronological order.")
        val history: List<TextAdventureHistoryMessage>,
    )
}
