package input.comprehensible.backend.textadventure

import ai.koog.agents.core.tools.annotations.LLMDescription
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
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
    private val inspirationWordSampler: InspirationWordSampler = DefaultInspirationWordSampler(),
    private val messageIdProvider: () -> String = { UUID.randomUUID().toString() },
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
        val previousAdventures = accountId
            ?.let { adventureRepository.listAdventureSummariesForAccount(it) }
            .orEmpty()
        val response = requestAdventureResponse(
            adventureId = UUID.randomUUID().toString(),
            promptName = "text-adventure-start",
            systemPrompt = """
                You are a text adventure narrator.
                Generate the opening scene in $learningLanguage.
                Provide a short, evocative title for the adventure in $learningLanguage.
                Provide that same title translated into $translationsLanguage as translatedTitle.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
                The story should not end yet, so set isEnding to false.
            """.trimIndent() +
                imageSelectionPromptSection() +
                previousAdventuresPromptSection(previousAdventures) +
                inspirationPromptSection(),
            userPrompt = "Start a new adventure.",
        )
        val chosenImageId = resolveChosenImage(
            pickedId = response.imageId,
            previousImageIds = previousAdventures.mapNotNull { it.imageId }.toSet(),
        )
        val messageId = messageIdProvider()
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = response.adventureId,
                accountId = accountId,
                messageId = messageId,
                parentMessageId = null,
                title = response.title,
                translatedTitle = response.translatedTitle,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                isEnding = response.isEnding,
                paragraphs = response.toPersistedParagraphs(),
                imageId = chosenImageId,
            )
        )
        return response.toRemoteResponse(messageId, imageId = chosenImageId)
    }

    fun listAdventuresForAccount(accountId: String): AdventureListRemoteResponse =
        AdventureListRemoteResponse(
            items = adventureRepository.listAdventureSummariesForAccount(accountId).map { it.toRemoteResponse() },
        )

    fun getAdventureSummaryForAccount(
        accountId: String,
        adventureId: String,
    ): AdventureSummaryRemoteResponse? =
        adventureRepository.listAdventureSummariesForAccount(accountId)
            .firstOrNull { it.adventureId == adventureId }
            ?.toRemoteResponse()

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

    suspend fun createUserMessageForAccount(
        accountId: String,
        adventureId: String,
        parentMessageId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse? {
        val existing = getAdventureMessagesForAccount(accountId = accountId, adventureId = adventureId) ?: return null
        if (existing.messages.none { it.id == parentMessageId }) return null
        val paragraphs = structureUserMessage(
            userMessage = text,
            learningLanguage = existing.learningLanguage,
            translationsLanguage = existing.translationsLanguage,
        )
        return adventureRepository.appendUserMessage(
            PersistedUserAdventureMessage(
                adventureId = adventureId,
                accountId = accountId,
                parentMessageId = parentMessageId,
                messageId = messageIdProvider(),
                learningLanguage = existing.learningLanguage,
                translationLanguage = existing.translationsLanguage,
                paragraphs = paragraphs,
            )
        )
    }

    suspend fun generateAiMessageForAccount(
        accountId: String,
        adventureId: String,
        parentMessageId: String,
    ): TextAdventureMessageRemoteResponse? {
        val existing = getAdventureMessagesForAccount(accountId = accountId, adventureId = adventureId) ?: return null
        val parent = existing.messages.firstOrNull { it.id == parentMessageId } ?: return null
        val userText = parent.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
        val messageId = messageIdProvider()
        respondToUser(
            adventureId = adventureId,
            learningLanguage = existing.learningLanguage,
            translationsLanguage = existing.translationsLanguage,
            userMessage = userText,
            history = existing.toHistory(),
            accountId = accountId,
            messageId = messageId,
            parentMessageId = parentMessageId,
        )
        return getAdventureMessagesForAccount(accountId, adventureId)?.messages?.firstOrNull { it.id == messageId }
    }

    suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
        accountId: String? = null,
        messageId: String = messageIdProvider(),
        parentMessageId: String? = null,
    ): TextAdventureRemoteResponse {
        val response = requestAdventureResponse(
            adventureId = adventureId,
            promptName = "text-adventure-continue",
            systemPrompt = """
                You are a text adventure narrator continuing an ongoing story.
                You will receive a JSON request containing the adventure context and chat history.
                Respond to the player in $learningLanguage.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Keep the title (and its $translationsLanguage translatedTitle) consistent with the story so far.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
            """.trimIndent() + inspirationPromptSection(),
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
                messageId = messageId,
                parentMessageId = parentMessageId,
                title = response.title,
                translatedTitle = response.translatedTitle,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                isEnding = response.isEnding,
                paragraphs = response.toPersistedParagraphs(),
            )
        )
        return response.toRemoteResponse(messageId)
    }

    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? =
        adventureRepository.getAdventureMessages(adventureId)

    private suspend fun structureUserMessage(
        userMessage: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): List<PersistedAdventureParagraph> = runRetrying(MAX_SENTENCE_MATCH_ATTEMPTS) {
        val response = structuredPromptExecutor.executeUserMessageResponse(
            promptName = "text-adventure-user-message",
            systemPrompt = """
                The player is responding in a text adventure game.
                Rewrite their response as natural sentences in $learningLanguage.
                Provide matching translations in $translationsLanguage with identical sentence counts and order.
                Do not add extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
            """.trimIndent(),
            userPrompt = userMessage,
        )

        check(response.paragraphs.size == response.translatedParagraphs.size) {
            """
                User message paragraph count mismatch:
                    paragraphs=${response.paragraphs.size}
                    translations=${response.translatedParagraphs.size}
            """.trimIndent()
        }

        response.paragraphs.zip(response.translatedParagraphs).map { (paragraph, translatedParagraph) ->
            check(paragraph.sentences.size == translatedParagraph.sentences.size) {
                "User message sentence count mismatch"
            }
            PersistedAdventureParagraph(
                sentences = paragraph.sentences.map(String::trim),
                translatedSentences = translatedParagraph.sentences.map(String::trim),
            )
        }
    }

    /**
     * Offers the model the full catalogue of cover images (id and description) and asks it to set
     * [TextAdventureStructuredResponse.imageId] to the best-fitting one for the opening scene.
     */
    private fun imageSelectionPromptSection(): String = "\n\n" + """
        Choose a cover image for this adventure from the list below.
        Set imageId to the exact id of the single image whose description best fits the opening scene.
        Available images (id: description):
    """.trimIndent() + "\n" + AdventureImageCatalog.promptListing()

    /**
     * Lists the player's previous adventures (title and chosen image) so the model can make this new
     * one feel different and avoid reusing the same image, reducing repetition across adventures.
     */
    private fun previousAdventuresPromptSection(previousAdventures: List<AdventureSummary>): String {
        if (previousAdventures.isEmpty()) return ""
        val listing = previousAdventures
            .take(MAX_PREVIOUS_ADVENTURES_IN_PROMPT)
            .joinToString("\n") { summary ->
                val image = summary.imageId?.let { " (image: $it)" }.orEmpty()
                "- \"${summary.title}\"$image"
            }
        return "\n\n" + """
            This player has already played the adventures listed below.
            Make this new adventure clearly different in setting, theme and tone,
            and prefer a cover image they have not used before:
        """.trimIndent() + "\n" + listing
    }

    /**
     * Returns the id of the image to store for the adventure. A valid catalogue pick from the AI is
     * respected; otherwise a deterministic catalogue image is used, preferring one the player has not
     * used so repeated adventures still look distinct.
     */
    private fun resolveChosenImage(pickedId: String, previousImageIds: Set<String>): String {
        AdventureImageCatalog.findById(pickedId)?.let { return it.id }
        val unused = AdventureImageCatalog.images.firstOrNull { it.id !in previousImageIds }
        return (unused ?: AdventureImageCatalog.fallback).id
    }

    private fun inspirationPromptSection(): String {
        val words = inspirationWordSampler.sample()
        if (words.isEmpty()) return ""
        return "\n\n" + """
            For added variety, here are some random words as loose inspiration: ${words.joinToString(", ")}.
            They are only a creative spark to make the story feel fresh.
            You do not need to use any of them, you must not force them into the narrative,
            and you must not mention this instruction or list the words to the player.
        """.trimIndent()
    }

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
            translatedTitle = response.translatedTitle.trim(),
            paragraphs = response.paragraphs,
            translatedParagraphs = response.translatedParagraphs,
            isEnding = response.isEnding,
            imageId = response.imageId.trim(),
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
        const val MAX_PREVIOUS_ADVENTURES_IN_PROMPT = 20
    }
}

private fun AdventureSummary.toRemoteResponse(): AdventureSummaryRemoteResponse = AdventureSummaryRemoteResponse(
    id = adventureId,
    title = title,
    translatedTitle = translatedTitle,
    learningLanguage = learningLanguage,
    translationLanguage = translationLanguage,
    updatedAt = updatedAt,
    imageId = imageId,
)

private fun TextAdventureMessagesRemoteResponse.toHistory(): List<TextAdventureHistoryMessage> = messages.mapNotNull { message ->
    val text = message.paragraphs.flatMap { it.sentences }.joinToString(" ").trim()
    if (text.isBlank()) {
        null
    } else {
        TextAdventureHistoryMessage(
            role = if (message.type == "AI") "assistant" else "user",
            text = text,
        )
    }
}

private fun GeneratedAdventureResponse.toPersistedParagraphs(): List<PersistedAdventureParagraph> =
    paragraphs.zip(translatedParagraphs).map { (paragraph, translatedParagraph) ->
        PersistedAdventureParagraph(
            sentences = paragraph.sentences.map(String::trim),
            translatedSentences = translatedParagraph.sentences.map(String::trim),
        )
    }

private data class GeneratedAdventureResponse(
    val adventureId: String,
    val title: String,
    val translatedTitle: String,
    val paragraphs: List<TextAdventureStructuredParagraph>,
    val translatedParagraphs: List<TextAdventureStructuredParagraph>,
    val isEnding: Boolean,
    val imageId: String,
)

private fun GeneratedAdventureResponse.toRemoteResponse(
    messageId: String,
    imageId: String? = null,
): TextAdventureRemoteResponse =
    TextAdventureRemoteResponse(
        messageId = messageId,
        adventureId = adventureId,
        title = title,
        translatedTitle = translatedTitle,
        sentences = paragraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
        translatedSentences = translatedParagraphs.flatMap { paragraph -> paragraph.sentences.map(String::trim) },
        isEnding = isEnding,
        imageId = imageId,
    )

@Serializable
@SerialName("UserMessageResponse")
@LLMDescription("The player's adventure message converted into structured sentences.")
data class UserMessageStructuredResponse(
    @property:LLMDescription("The player's message as sentences in the learning language.")
    val paragraphs: List<TextAdventureStructuredParagraph>,
    @property:LLMDescription("Translated sentences in the translation language matching paragraph structure.")
    val translatedParagraphs: List<TextAdventureStructuredParagraph>,
)

@Serializable
@SerialName("TextAdventureResponse")
@LLMDescription("A single response from the text adventure narrator.")
data class TextAdventureStructuredResponse(
    @property:LLMDescription("Short, evocative title for the adventure, in the learning language.")
    val title: String,
    @property:LLMDescription("The title translated into the translation language.")
    val translatedTitle: String,
    @property:LLMDescription("Narration paragraphs in the learning language.")
    val paragraphs: List<TextAdventureStructuredParagraph>,
    @property:LLMDescription("Translated paragraphs matching the narration paragraph order.")
    val translatedParagraphs: List<TextAdventureStructuredParagraph>,
    @property:LLMDescription("Whether the story ends after this response.")
    val isEnding: Boolean,
    @property:LLMDescription(
        "When starting a new adventure, the exact id of the cover image chosen from the provided " +
            "list whose description best fits the opening scene. Empty when continuing an adventure.",
    )
    val imageId: String = "",
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
    val translatedTitle: String = "",
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    val imageId: String? = null,
)

