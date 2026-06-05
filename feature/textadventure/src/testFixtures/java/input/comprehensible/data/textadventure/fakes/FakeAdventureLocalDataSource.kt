package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory fake of [AdventureLocalDataSource] backed by a [MutableStateFlow] so the observed list
 * updates reactively, mirroring Room. Tests seed rows with [seed].
 */
class FakeAdventureLocalDataSource : AdventureLocalDataSource {
    private val rows = MutableStateFlow<List<AdventureEntity>>(emptyList())

    override fun observeAdventures(userId: String): Flow<List<AdventureEntity>> =
        rows.map { all ->
            all.filter { it.userId == userId }.sortedByDescending { it.updatedAt }
        }

    override suspend fun getAdventure(id: String): AdventureEntity? =
        rows.value.firstOrNull { it.id == id }

    override suspend fun upsertAdventures(adventures: List<AdventureEntity>) {
        adventures.forEach(::upsert)
    }

    override suspend fun upsertAdventure(adventure: AdventureEntity) {
        upsert(adventure)
    }

    override suspend fun deleteAdventure(id: String) {
        rows.value = rows.value.filterNot { it.id == id }
    }

    fun seed(adventure: AdventureEntity) {
        upsert(adventure)
    }

    private fun upsert(adventure: AdventureEntity) {
        rows.value = rows.value.filterNot { it.id == adventure.id } + adventure
    }
}
