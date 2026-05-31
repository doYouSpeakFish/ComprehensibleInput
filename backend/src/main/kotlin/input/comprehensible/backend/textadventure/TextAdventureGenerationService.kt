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

@Suppress("TooManyFunctions")
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
        accountId: String? = null,
    ): TextAdventureRemoteResponse {
        val response = requestAdventureResponse(
            adventureId = UUID.randomUUID().toString(),
            promptName = "text-adventure-start",
            systemPrompt = """
                You are a text adventure narrator.
                Generate the opening scene in $learningLanguage.
                Provide a short, evocative title for the adventure.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
                The story should not end yet, so set isEnding to false.
            """.trimIndent(),
            userPrompt = "Start a new adventure.",
        )
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                accountId = accountId,
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


    fun listAdventuresForAccount(accountId: String): AdventureListRemoteResponse =
        AdventureListRemoteResponse(
            items = adventureRepository.listAdventureSummariesForAccount(accountId).map {
                AdventureSummaryRemoteResponse(
                    id = it.adventureId,
                    title = it.title,
                    learningLanguage = it.learningLanguage,
                    translationLanguage = it.translationLanguage,
                    updatedAt = it.updatedAt,
                )
            },
        )



    fun getAdventureSummaryForAccount(
        accountId: String,
        adventureId: String,
    ): AdventureSummaryRemoteResponse? =
        adventureRepository.listAdventureSummariesForAccount(accountId)
            .firstOrNull { it.adventureId == adventureId }
            ?.let {
                AdventureSummaryRemoteResponse(
                    id = it.adventureId,
                    title = it.title,
                    learningLanguage = it.learningLanguage,
                    translationLanguage = it.translationLanguage,
                    updatedAt = it.updatedAt,
                )
            }

    fun getAdventureMessagesForAccount(
        accountId: String,
        adventureId: String,
    ): TextAdventureMessagesRemoteResponse? {
        val isOwned = adventureRepository.listAdventureSummariesForAccount(accountId)
            .any { it.adventureId == adventureId }
        if (!isOwned) return null
        return getAdventureMessages(adventureId)
    }

    fun deleteAdventureForAccount(accountId: String, adventureId: String): Boolean =
        adventureRepository.deleteAdventureForAccount(accountId, adventureId)

    fun deleteAllAdventuresForAccount(accountId: String) {
        adventureRepository.deleteAllAdventuresForAccount(accountId)
    }

    suspend fun respondToAdventureForAccount(
        accountId: String,
        adventureId: String,
        userMessage: String,
    ): RespondToAdventureForAccountResult? {
        val isOwned = adventureRepository.listAdventureSummariesForAccount(accountId)
            .any { it.adventureId == adventureId }
        if (!isOwned) return null
        val existing = adventureRepository.getAdventureMessages(adventureId) ?: return null
        if (existing.messages.lastOrNull()?.isEnding == true) {
            return RespondToAdventureForAccountResult.AdventureEnded
        }
        adventureRepository.appendUserMessage(
            adventureId = adventureId,
            accountId = accountId,
            learningLanguage = existing.learningLanguage,
            translationLanguage = existing.translationsLanguage,
            userMessage = userMessage,
        )
        val withUserMessage = adventureRepository.getAdventureMessages(adventureId) ?: return null
        val history = withUserMessage.messages.mapNotNull { message ->
            val text = message.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
            if (text.isBlank()) {
                null
            } else {
                TextAdventureHistoryMessage(
                    role = if (message.sender == "AI") "assistant" else "user",
                    text = text,
                )
            }
        }
        val response = respondToUser(
            adventureId = adventureId,
            learningLanguage = existing.learningLanguage,
            translationsLanguage = existing.translationsLanguage,
            userMessage = userMessage,
            history = history,
            accountId = accountId,
        )
        return RespondToAdventureForAccountResult.Success(response)
    }

    suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
        accountId: String? = null,
    ): TextAdventureRemoteResponse {
        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-continue",
            systemPrompt = """
                You are a text adventure narrator continuing an ongoing story.
                You will receive a JSON request containing the adventure context and chat history.
                Respond to the player in $learningLanguage.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Keep the title consistent with the story so far.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
            """.trimIndent(),
            userPrompt = json.encodeToString(
                ContinueTextAdventureRequest(
                    adventureId = adventureId,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage,
                    userMessage = userMessage,
                    history = history,
                )
            ),
        )
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                accountId = accountId,
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
data class AdventureListRemoteResponse(val items: List<AdventureSummaryRemoteResponse>)

@Serializable
data class AdventureSummaryRemoteResponse(
    val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
)

sealed interface RespondToAdventureForAccountResult {
    data class Success(val response: TextAdventureRemoteResponse) : RespondToAdventureForAccountResult
    data object AdventureEnded : RespondToAdventureForAccountResult
}
