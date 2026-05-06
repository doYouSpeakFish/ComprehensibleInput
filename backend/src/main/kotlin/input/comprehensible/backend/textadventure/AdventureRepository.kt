package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse

interface AdventureRepository {
    fun saveAdventurePart(adventurePart: PersistedAdventurePart)

    fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse?

    fun getAdventurePlan(adventureId: String): TextAdventureWorldPlan?
}

data class PersistedAdventurePart(
    val adventureId: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val isEnding: Boolean,
    val paragraphs: List<PersistedAdventureParagraph>,
    val internalPlan: TextAdventureWorldPlan?,
)

data class PersistedAdventureParagraph(
    val sentences: List<String>,
    val translatedSentences: List<String>,
)
