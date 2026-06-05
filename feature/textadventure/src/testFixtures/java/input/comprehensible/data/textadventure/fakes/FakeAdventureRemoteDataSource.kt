package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.delay

/**
 * In-memory fake of [AdventureRemoteDataSource]. Tests script the adventures and messages returned,
 * request delays (to observe loading), and failures.
 */
class FakeAdventureRemoteDataSource : AdventureRemoteDataSource {
    var adventures: List<RemoteAdventure> = emptyList()
    var requestDelayMillis: Long = 0
    var failGetAdventures: Boolean = false
    var failDeleteAdventure: Boolean = false
    var failStartAdventure: Boolean = false
    var failGetMessages: Boolean = false
    var startResponse: TextAdventureRemoteResponse = TextAdventureRemoteResponse(
        messageId = "message-1",
        adventureId = "adventure-1",
        title = "New adventure",
        sentences = listOf("Your adventure begins."),
        translatedSentences = listOf("Tu aventura comienza."),
        isEnding = false,
    )
    var messagesResponse: TextAdventureMessagesRemoteResponse = TextAdventureMessagesRemoteResponse(
        adventureId = "adventure-1",
        title = "New adventure",
        learningLanguage = "en",
        translationsLanguage = "es",
        messages = emptyList(),
    )

    override suspend fun getAdventures(token: String): List<RemoteAdventure> {
        delayIfConfigured()
        if (failGetAdventures) error("Failed to get adventures")
        return adventures
    }

    override suspend fun deleteAdventure(token: String, adventureId: String) {
        delayIfConfigured()
        if (failDeleteAdventure) error("Failed to delete adventure")
        adventures = adventures.filterNot { it.id == adventureId }
    }

    override suspend fun startAdventure(
        token: String,
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse {
        delayIfConfigured()
        if (failStartAdventure) error("Failed to start adventure")
        return startResponse
    }

    override suspend fun getMessages(
        token: String,
        adventureId: String,
    ): TextAdventureMessagesRemoteResponse {
        delayIfConfigured()
        if (failGetMessages) error("Failed to get messages")
        return messagesResponse
    }

    private suspend fun delayIfConfigured() {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
    }
}
