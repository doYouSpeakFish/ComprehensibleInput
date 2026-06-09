package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse

interface AdventureRepository {
    fun listAdventureSummariesForAccount(accountId: String): List<AdventureSummary>

    fun saveAdventurePart(adventurePart: PersistedAdventurePart): String

    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse?

    fun appendUserMessage(message: PersistedUserAdventureMessage): TextAdventureMessageRemoteResponse?

    fun deleteAdventureForAccount(accountId: String, adventureId: String): Boolean

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
)
