package input.comprehensible.data.textadventure.sources.remote

import com.ktin.InjectedSingleton
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

/**
 * Remote source for the authenticated v1 text-adventure endpoints. Defined as an interface so it
 * can be faked in tests; the real Ktor implementation is [DefaultAdventureRemoteDataSource].
 */
interface AdventureRemoteDataSource {
    suspend fun getAdventures(token: String): List<RemoteAdventure>
    suspend fun deleteAdventure(token: String, adventureId: String)
    suspend fun startAdventure(
        token: String,
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse
    suspend fun getMessages(token: String, adventureId: String): TextAdventureMessagesRemoteResponse
    suspend fun sendUserMessage(
        token: String,
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse
    suspend fun generateAiMessage(
        token: String,
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse

    /**
     * Resolves the catalogue [imageId] chosen by the backend into a fully-qualified image URL the
     * app can load, or null when there is no image. The URL points at the backend's static adventure
     * image assets.
     */
    fun imageUrl(imageId: String?): String?

    companion object : InjectedSingleton<AdventureRemoteDataSource>()
}

/**
 * A text adventure summary as returned by `GET /v1/adventures`.
 */
data class RemoteAdventure(
    val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    /** The fully-resolved URL of the adventure's cover image, or null if it has none. */
    val imageUrl: String? = null,
)
