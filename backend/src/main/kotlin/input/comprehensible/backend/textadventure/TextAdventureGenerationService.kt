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

/**
 * The CEFR difficulty level a new adventure is written at when the client does not specify one.
 * Kept as the request and column default so adventures from app versions that predate the level
 * picker keep behaving as before.
 */
internal const val DEFAULT_LANGUAGE_LEVEL = "B1"

/**
 * The CEFR levels the picker offers and the only values a start request may specify. Requests are
 * validated against this set before any (expensive) generation runs, so an unsupported or oversized
 * level is rejected up front rather than failing the fixed-width database write afterwards.
 */
internal val SUPPORTED_LANGUAGE_LEVELS = setOf("A1", "A2", "B1", "B2", "C1")

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
        languageLevel: String = DEFAULT_LANGUAGE_LEVEL,
    ): TextAdventureRemoteResponse {
        val previousAdventures = accountId
            ?.let { adventureRepository.listAdventureSummariesForAccount(it) }
            .orEmpty()
        val plan = writeAdventurePlan(
            learningLanguage = learningLanguage,
            previousAdventures = previousAdventures,
        )
        val response = requestAdventureResponse(
            adventureId = UUID.randomUUID().toString(),
            promptName = "text-adventure-start",
            systemPrompt = """
                You are a text adventure narrator.
                Generate the opening scene in $learningLanguage.
                Open the scene by introducing who the player's character is and what they are carrying
                in their starting inventory, then lead into the start of the adventure.
                Provide a short, evocative title for the adventure in $learningLanguage.
                Provide that same title translated into $translationsLanguage as translatedTitle.
                Provide translations for each paragraph in $translationsLanguage with matching sentence counts and order.
                Do not include extra commentary outside the requested fields.
                Avoid markdown and keep punctuation natural for the language.
                The story should not end yet, so set isEnding to false.
            """.trimIndent() +
                languageLevelPromptSection(learningLanguage, languageLevel) +
                imageSelectionPromptSection() +
                previousAdventuresPromptSection(previousAdventures) +
                planPromptSection(plan) +
                notePromptSection() +
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
                plan = plan,
                note = response.note,
                languageLevel = languageLevel,
            )
        )
        return response.toRemoteResponse(messageId, imageId = chosenImageId)
    }

    /**
     * Lists the preplanned adventures a player can start, optionally narrowed to a [learningLanguage]
     * and/or [translationLanguage] so the app only offers ones written for the player's language pair.
     * Returns metadata only: the private plan and the opening message stay hidden until the player
     * actually starts the adventure.
     */
    fun listPreplannedAdventures(
        learningLanguage: String? = null,
        translationLanguage: String? = null,
    ): PreplannedAdventureListRemoteResponse =
        PreplannedAdventureListRemoteResponse(
            items = PreplannedAdventureCatalog.adventures
                .filter { learningLanguage == null || it.learningLanguage == learningLanguage }
                .filter { translationLanguage == null || it.translationLanguage == translationLanguage }
                .map { it.toRemoteSummary() },
        )

    /**
     * Starts the preplanned adventure with id [preplannedAdventureId] for [accountId] by copying it
     * into a new, private adventure owned by that account: the opening narrator message, the cover
     * image, the difficulty level and the private plan are all stored just as if they had been
     * generated. From then on it is an ordinary adventure — continued through the normal message
     * endpoints and visible only to its owner, so two players who start the same preplanned adventure
     * never see each other's messages. Each call creates a fresh adventure. Returns null when no
     * preplanned adventure has that id.
     */
    fun startPreplannedAdventureForAccount(
        accountId: String,
        preplannedAdventureId: String,
    ): TextAdventureRemoteResponse? {
        val preplanned = PreplannedAdventureCatalog.findById(preplannedAdventureId) ?: return null
        val adventureId = UUID.randomUUID().toString()
        val messageId = messageIdProvider()
        adventureRepository.saveAdventurePart(
            PersistedAdventurePart(
                adventureId = adventureId,
                accountId = accountId,
                messageId = messageId,
                parentMessageId = null,
                title = preplanned.title,
                translatedTitle = preplanned.translatedTitle,
                learningLanguage = preplanned.learningLanguage,
                translationLanguage = preplanned.translationLanguage,
                isEnding = false,
                paragraphs = preplanned.firstMessage.paragraphs,
                imageId = preplanned.imageId,
                plan = preplanned.plan.toPlanText(),
                note = preplanned.firstMessage.note,
                languageLevel = preplanned.languageLevel,
            )
        )
        return TextAdventureRemoteResponse(
            messageId = messageId,
            adventureId = adventureId,
            title = preplanned.title,
            translatedTitle = preplanned.translatedTitle,
            sentences = preplanned.firstMessage.paragraphs.flatMap { it.sentences },
            translatedSentences = preplanned.firstMessage.paragraphs.flatMap { it.translatedSentences },
            isEnding = false,
            imageId = preplanned.imageId,
        )
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

    /** Undoes an adventure deletion, returning the restored summary, or null when nothing to restore. */
    fun restoreAdventureForAccount(accountId: String, adventureId: String): AdventureSummaryRemoteResponse? =
        adventureRepository.restoreAdventureForAccount(accountId, adventureId)?.toRemoteResponse()

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
        val context = adventureRepository.getAdventureNarrationContext(adventureId, leafMessageId = parentMessageId)
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
            """.trimIndent() +
                languageLevelPromptSection(learningLanguage, context?.languageLevel ?: DEFAULT_LANGUAGE_LEVEL) +
                planPromptSection(context?.plan) +
                notesPromptSection(context?.notes.orEmpty()) +
                notePromptSection() +
                inspirationPromptSection(),
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
                note = response.note,
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

    /**
     * Instructs the narrator to pitch its [learningLanguage] writing at the adventure's CEFR
     * difficulty [languageLevel] (e.g. "B1"). Included when an adventure is started and again on
     * every later turn so the difficulty stays consistent across the whole adventure.
     */
    private fun languageLevelPromptSection(learningLanguage: String, languageLevel: String): String = "\n\n" + """
        Write the $learningLanguage narration at CEFR level $languageLevel.
        Use vocabulary, grammar and sentence length a learner at $languageLevel can follow, keeping the
        difficulty within that level rather than noticeably easier or harder.
    """.trimIndent()

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

    /**
     * Asks the model to write a detailed, private plan for a new adventure before any narration. The
     * plan is given the same random inspiration words as narration, plus the plans behind the player's
     * previous adventures so it can make this one different. The rendered plan text is returned to be
     * stored and fed back as narration context; it is never exposed through the API.
     */
    private suspend fun writeAdventurePlan(
        learningLanguage: String,
        previousAdventures: List<AdventureSummary>,
    ): String = runRetrying(MAX_SENTENCE_MATCH_ATTEMPTS) {
        structuredPromptExecutor.executePlanResponse(
            promptName = "text-adventure-plan",
            systemPrompt = """
                You are the architect of a text adventure that will be played in $learningLanguage.
                Before it begins, write a detailed, private plan for the adventure. The plan is for your
                eyes only: it is never shown to the player and is not story text. You are given it back as
                context whenever you narrate this adventure, so it must hold everything you need to run an
                exciting, coherent adventure.
                A plan must not script what the player will do, because the player drives the story. Instead
                it sets up the adventure in enough detail to stay exciting whatever the player does.
                Describe the player's character and a brief backstory, and their starting inventory.
                Give a clear hook that pulls the player in, and the hidden truth behind that hook.
                Explain the core challenge of the adventure in detail.
                Plan the key locations in concrete detail; for any building, dungeon, cave or castle plan the
                layout and the contents of every room. Plan the NPCs in detail: who they are, what they want
                and where they are.
                Be specific. If something important happens somewhere, say where, what it looks like, who is
                involved, why, what items it needs and what happens if it succeeds. If the player must find
                something, say where it is. If there is a puzzle, give its solution. Write the plan in English.
            """.trimIndent() +
                previousPlansPromptSection(previousAdventures) +
                inspirationPromptSection(),
            userPrompt = "Plan a new adventure.",
        ).toPlanText()
    }

    /**
     * Summarises the plans behind the player's previous adventures so the model can plan a new one that
     * is clearly different. Adventures without a stored plan (e.g. from before plans existed) are omitted.
     */
    private fun previousPlansPromptSection(previousAdventures: List<AdventureSummary>): String {
        val planned = previousAdventures
            .take(MAX_PREVIOUS_ADVENTURES_IN_PROMPT)
            .filter { !it.plan.isNullOrBlank() }
        if (planned.isEmpty()) return ""
        val listing = planned.joinToString("\n\n") { summary -> "Adventure: \"${summary.title}\"\n${summary.plan}" }
        return "\n\n" + """
            This player has already played the adventures below, with the plans behind them. Plan a new
            adventure that is clearly different from all of these in setting, theme, characters and challenge:
        """.trimIndent() + "\n" + listing
    }

    /**
     * Injects the adventure's private plan as narration context. Returns empty when there is no plan, so
     * adventures created before plans existed simply narrate without one.
     */
    private fun planPromptSection(plan: String?): String {
        if (plan.isNullOrBlank()) return ""
        return "\n\n" + """
            Below is the private plan for this adventure. It is for your eyes only and must never be shown
            or described to the player except as it naturally emerges through play. Follow it so the
            adventure stays coherent and exciting:
        """.trimIndent() + "\n" + plan
    }

    /**
     * Explains that the note field is private and how to use it. Offered only when the model writes a
     * narrator message, never when translating a player message.
     */
    private fun notePromptSection(): String = "\n\n" + """
        You may also record a private note in the note field. This note is private: it is never shown to
        the player and is only ever given back to you as context on later turns. Use it to expand on the
        plan as the adventure unfolds — for example to track what the player has discovered, decisions you
        have made about details the plan left open, or threads you are setting up. Leave the note empty if
        there is nothing worth recording this turn.
    """.trimIndent()

    /** Injects the private notes recorded on earlier turns as narration context, oldest first. */
    private fun notesPromptSection(notes: List<String>): String {
        if (notes.isEmpty()) return ""
        val listing = notes.joinToString("\n") { "- $it" }
        return "\n\n" + """
            These are the private notes you recorded on earlier turns, in order. They are private to you
            and expand on the plan as the adventure has unfolded so far:
        """.trimIndent() + "\n" + listing
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
            note = response.note.trim(),
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
    status = status,
)

private fun PreplannedAdventure.toRemoteSummary(): PreplannedAdventureRemoteResponse = PreplannedAdventureRemoteResponse(
    id = id,
    title = title,
    translatedTitle = translatedTitle,
    learningLanguage = learningLanguage,
    translationLanguage = translationLanguage,
    languageLevel = languageLevel,
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
    val note: String,
)

/**
 * Renders a structured plan into the labelled, self-contained text that is stored on the adventure and
 * fed back to the narrator as context. Keeping the stored form as plain text means it can be injected
 * straight into later prompts without re-parsing.
 */
private fun AdventurePlanStructuredResponse.toPlanText(): String = buildString {
    appendLine("PLAYER CHARACTER: ${characterDescription.trim()}")
    appendLine()
    appendLine("PLAYER INVENTORY: ${inventory.trim()}")
    appendLine()
    appendLine("HOOK: ${hook.trim()}")
    appendLine()
    appendLine("TRUTH BEHIND THE HOOK: ${truthBehindHook.trim()}")
    appendLine()
    appendLine("CORE CHALLENGE: ${coreChallenge.trim()}")
    appendLine()
    appendLine("LOCATIONS:")
    locations.forEach { appendLine("- ${it.name.trim()}: ${it.description.trim()}") }
    appendLine()
    appendLine("NPCS:")
    npcs.forEach { appendLine("- ${it.name.trim()}: ${it.description.trim()}") }
}.trim()

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
    @property:LLMDescription(
        "A private note for you, the narrator. It is never shown to the player and is only ever given " +
            "back to you as context on later turns. Use it to expand on the plan as the adventure " +
            "unfolds. Leave it empty when there is nothing worth recording this turn.",
    )
    val note: String = "",
)

@Serializable
@SerialName("AdventurePlanResponse")
@LLMDescription("A detailed private plan that sets up an exciting text adventure for the narrator to run.")
data class AdventurePlanStructuredResponse(
    @property:LLMDescription(
        "Who the player's character is, including a brief description of their backstory.",
    )
    val characterDescription: String,
    @property:LLMDescription(
        "A clear description of the items the player's character is carrying at the start of the adventure.",
    )
    val inventory: String,
    @property:LLMDescription(
        "The clear hook that draws the player into the adventure.",
    )
    val hook: String,
    @property:LLMDescription(
        "The hidden truth behind the hook that the player does not know at the start.",
    )
    val truthBehindHook: String,
    @property:LLMDescription(
        "The core challenge of the adventure, explained in thorough detail.",
    )
    val coreChallenge: String,
    @property:LLMDescription(
        "The key locations of the adventure, each planned out in concrete, specific detail.",
    )
    val locations: List<AdventurePlanLocationResponse>,
    @property:LLMDescription(
        "The named NPCs of the adventure, each planned out in detail.",
    )
    val npcs: List<AdventurePlanNpcResponse>,
)

@Serializable
@LLMDescription("A planned location in the adventure.")
data class AdventurePlanLocationResponse(
    @property:LLMDescription("The name of the location.")
    val name: String,
    @property:LLMDescription(
        "A detailed description of the location: where it is, what it looks like and what it contains. " +
            "For any building, dungeon, cave or castle, plan the layout and the contents of every room, " +
            "and the exact location of anything the player may need to find.",
    )
    val description: String,
)

@Serializable
@LLMDescription("A planned NPC in the adventure.")
data class AdventurePlanNpcResponse(
    @property:LLMDescription("The name of the NPC.")
    val name: String,
    @property:LLMDescription(
        "A detailed description of the NPC: who they are, what they want, their role in the core " +
            "challenge and where in the adventure they can be found.",
    )
    val description: String,
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
    /** Progress of the adventure: "not_started", "in_progress" or "complete". */
    val status: String = "in_progress",
)

@Serializable
data class PreplannedAdventureListRemoteResponse(val items: List<PreplannedAdventureRemoteResponse>)

/**
 * A preplanned adventure a player can start, as returned by `GET /v1/preplanned-adventures`. This is
 * metadata only: [id] is the catalogue id used to start the adventure (not a player's adventure id),
 * and the private plan and opening message are deliberately omitted until the player starts it.
 */
@Serializable
data class PreplannedAdventureRemoteResponse(
    val id: String,
    val title: String,
    val translatedTitle: String,
    val learningLanguage: String,
    val translationLanguage: String,
    /** The CEFR difficulty level (e.g. "B1") the adventure is written at. */
    val languageLevel: String,
    /** The id of the catalogue cover image for this adventure. */
    val imageId: String,
)

