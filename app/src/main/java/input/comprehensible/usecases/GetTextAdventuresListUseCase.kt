package input.comprehensible.usecases

import input.comprehensible.data.textadventures.TextAdventuresListResult
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.flow.Flow

/**
 * Gets the list of text adventures.
 */
class GetTextAdventuresListUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    operator fun invoke(): Flow<TextAdventuresListResult> = repository.getAdventures()
}
