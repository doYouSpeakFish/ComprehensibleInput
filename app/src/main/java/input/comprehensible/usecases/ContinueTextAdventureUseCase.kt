package input.comprehensible.usecases

import input.comprehensible.data.textadventures.TextAdventuresRepository

/**
 * Continues a text adventure with the user's response.
 */
class ContinueTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    suspend operator fun invoke(adventureId: String, userMessage: String) {
        repository.respondToAdventure(adventureId = adventureId, userMessage = userMessage)
    }
}
