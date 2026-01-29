package input.comprehensible.data.textadventures.sources.remote

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.structure.executeStructured
import input.comprehensible.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.min

class DefaultTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    private val promptExecutor = simpleGoogleAIExecutor(BuildConfig.KOOG_API_KEY)
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    override suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse = requestAdventureResponse(
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
        userPrompt = "Start a new adventure. Adventure ID: $adventureId.",
    )

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse = requestAdventureResponse(
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
        promptName: String,
        systemPrompt: String,
        userPrompt: String,
    ): TextAdventureRemoteResponse {
        val response = promptExecutor.executeStructured<TextAdventureStructuredResponse>(
            prompt = prompt(promptName) {
                system(systemPrompt)
                user(userPrompt)
            },
            model = GoogleModels.Gemini2_5Pro,
        ).getOrThrow().data
        val normalizedCount = min(response.sentences.size, response.translatedSentences.size)
        return TextAdventureRemoteResponse(
            title = response.title.trim(),
            sentences = response.sentences.take(normalizedCount).map { it.trim() },
            translatedSentences = response.translatedSentences.take(normalizedCount).map { it.trim() },
            isEnding = response.isEnding,
        )
    }

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
