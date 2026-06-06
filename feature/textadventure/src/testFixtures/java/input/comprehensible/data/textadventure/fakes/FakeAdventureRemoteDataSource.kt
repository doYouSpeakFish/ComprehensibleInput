package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.delay

/**
 * In-memory fake of [AdventureRemoteDataSource]. Tests script the adventures and messages returned,
 * request delays (to observe loading), and failures.
 */
class FakeAdventureRemoteDataSource : AdventureRemoteDataSource {
    var adventures: List<RemoteAdventure> = emptyList()
    var requestDelayMillis: Long = 0
    var userMessageDelayMillis: Long = 0
    var aiMessageDelayMillis: Long = 0
    var failGetAdventures: Boolean = false
    var failDeleteAdventure: Boolean = false
    var failStartAdventure: Boolean = false
    var failGetMessages: Boolean = false
    var failSendUserMessage: Boolean = false
    var failGenerateAiMessage: Boolean = false
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
    var userMessageResponse: TextAdventureMessageRemoteResponse = messageResponse(
        id = "user-message-1",
        type = "user",
        text = "Your move.",
        translation = "Tu turno.",
    )
    var aiMessageResponse: TextAdventureMessageRemoteResponse = messageResponse(
        id = "ai-message-1",
        type = "AI",
        text = "The story continues.",
        translation = "La historia continúa.",
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

    override suspend fun sendUserMessage(
        token: String,
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse {
        delayFor(userMessageDelayMillis)
        if (failSendUserMessage) error("Failed to send user message")
        return userMessageResponse
    }

    override suspend fun generateAiMessage(
        token: String,
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse {
        // Also honours the global delay so the reused retry step can observe the in-flight state.
        delayFor(maxOf(aiMessageDelayMillis, requestDelayMillis))
        if (failGenerateAiMessage) error("Failed to generate AI message")
        return aiMessageResponse
    }

    private suspend fun delayIfConfigured() {
        delayFor(requestDelayMillis)
    }

    private suspend fun delayFor(millis: Long) {
        if (millis > 0) delay(millis)
    }

    companion object {
        fun messageResponse(
            id: String,
            type: String,
            text: String,
            translation: String,
            isEnding: Boolean = false,
        ) = TextAdventureMessageRemoteResponse(
            id = id,
            parentId = null,
            type = type,
            sender = type,
            isEnding = isEnding,
            paragraphs = listOf(
                TextAdventureParagraphRemoteResponse(
                    sentences = listOf(text),
                    translatedSentences = listOf(translation),
                ),
            ),
        )
    }
}
