package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse

interface AdventureRepository {
    fun listAdventureSummariesForAccount(accountId: String): List<AdventureSummary>

    fun saveAdventurePart(adventurePart: PersistedAdventurePart)

    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse?

    fun appendUserMessage(
        adventureId: String,
        accountId: String?,
        learningLanguage: String,
        translationLanguage: String,
        userMessage: String,
    )

    fun deleteAdventureForAccount(accountId: String, adventureId: String): Boolean

    fun deleteAllAdventuresForAccount(accountId: String)
}

data class PersistedAdventurePart(
    val adventureId: String,
    val accountId: String? = null,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val isEnding: Boolean,
    val paragraphs: List<PersistedAdventureParagraph>,
)

data class PersistedAdventureParagraph(
    val sentences: List<String>,
    val translatedSentences: List<String>,
)


data class AdventureSummary(
    val adventureId: String,
    val accountId: String? = null,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
)
