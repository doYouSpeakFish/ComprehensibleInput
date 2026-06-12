package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse

interface AdventureRepository {
    fun listAdventureSummariesForAccount(accountId: String): List<AdventureSummary>

    fun saveAdventurePart(adventurePart: PersistedAdventurePart): String

    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse?

    /**
     * The private narration context for an adventure: the AI-authored plan and the AI-authored notes
     * recorded on its messages so far (oldest first). These are never exposed through the API; they are
     * fed back to the model as context when it narrates the adventure. Returns null when the adventure
     * does not exist.
     */
    fun getAdventureNarrationContext(adventureId: String): AdventureNarrationContext?

    fun appendUserMessage(message: PersistedUserAdventureMessage): TextAdventureMessageRemoteResponse?

    fun deleteAdventureForAccount(accountId: String, adventureId: String): Boolean

    /**
     * Undoes the deletion of an adventure, returning the restored summary, or null when the account
     * has no deleted adventure with that id.
     */
    fun restoreAdventureForAccount(accountId: String, adventureId: String): AdventureSummary?

    fun deleteAllAdventuresForAccount(accountId: String)
}

data class PersistedUserAdventureMessage(
    val adventureId: String,
    val accountId: String?,
    val parentMessageId: String,
    val messageId: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val paragraphs: List<PersistedAdventureParagraph>,
)

data class PersistedAdventurePart(
    val adventureId: String,
    val accountId: String? = null,
    val messageId: String,
    val parentMessageId: String? = null,
    val title: String,
    val translatedTitle: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val isEnding: Boolean,
    val paragraphs: List<PersistedAdventureParagraph>,
    /**
     * The chosen catalogue image for the adventure. Set when the adventure is created; left null
     * when continuing an adventure so the repository preserves the image already stored.
     */
    val imageId: String? = null,
    /**
     * The AI-authored plan for the adventure. Set when the adventure is created; left null when
     * continuing an adventure so the repository preserves the plan already stored. Never exposed
     * through the API.
     */
    val plan: String? = null,
    /**
     * The AI-authored private note attached to this message, if any. Never exposed through the API;
     * only ever fed back to the model as context.
     */
    val note: String? = null,
)

data class PersistedAdventureParagraph(
    val sentences: List<String>,
    val translatedSentences: List<String>,
)

data class AdventureSummary(
    val adventureId: String,
    val accountId: String? = null,
    val title: String,
    val translatedTitle: String = "",
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    val imageId: String? = null,
    /** Progress of the adventure: "not_started", "in_progress" or "complete". */
    val status: String = "in_progress",
    /**
     * The AI-authored plan for the adventure, used to help plan later adventures differently. Never
     * exposed through the API: the remote summary mapping deliberately drops it.
     */
    val plan: String? = null,
)

/**
 * The private narration context for an adventure. Never exposed through the API; supplied to the
 * model as context when narrating.
 */
data class AdventureNarrationContext(
    val plan: String?,
    /** AI-authored notes recorded on the adventure's messages so far, oldest first. */
    val notes: List<String>,
)
