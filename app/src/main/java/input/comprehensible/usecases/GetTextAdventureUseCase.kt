package input.comprehensible.usecases

import input.comprehensible.data.textadventures.TextAdventureResult
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.flow.Flow

/**
 * Gets a text adventure by id.
 */
class GetTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    operator fun invoke(id: String): Flow<TextAdventureResult> = repository.getAdventure(id)
}
