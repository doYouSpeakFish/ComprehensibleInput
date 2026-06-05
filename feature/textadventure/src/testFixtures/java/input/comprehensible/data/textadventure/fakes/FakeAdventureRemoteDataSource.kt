package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import kotlinx.coroutines.delay

/**
 * In-memory fake of [AdventureRemoteDataSource]. Tests script the adventures returned, request
 * delays (to observe loading), and failures.
 */
class FakeAdventureRemoteDataSource : AdventureRemoteDataSource {
    var adventures: List<RemoteAdventure> = emptyList()
    var requestDelayMillis: Long = 0
    var failGetAdventures: Boolean = false
    var failDeleteAdventure: Boolean = false

    override suspend fun getAdventures(token: String): List<RemoteAdventure> {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
        if (failGetAdventures) error("Failed to get adventures")
        return adventures
    }

    override suspend fun deleteAdventure(token: String, adventureId: String) {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
        if (failDeleteAdventure) error("Failed to delete adventure")
        adventures = adventures.filterNot { it.id == adventureId }
    }
}
