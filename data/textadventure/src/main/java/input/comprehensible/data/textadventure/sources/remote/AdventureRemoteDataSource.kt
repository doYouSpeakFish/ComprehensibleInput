package input.comprehensible.data.textadventure.sources.remote

import com.ktin.InjectedSingleton

/**
 * Remote source for the authenticated v1 text-adventure endpoints used by the list screen. Defined
 * as an interface so it can be faked in tests; the real Ktor implementation is
 * [DefaultAdventureRemoteDataSource].
 */
interface AdventureRemoteDataSource {
    suspend fun getAdventures(token: String): List<RemoteAdventure>
    suspend fun deleteAdventure(token: String, adventureId: String)

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
)
