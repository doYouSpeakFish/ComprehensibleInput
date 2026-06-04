package input.comprehensible.usecases

import input.comprehensible.data.textadventures.TextAdventuresRepository

class ContinueTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    suspend operator fun invoke(token: String, adventureId: String, userMessage: String) {
        repository.respondToAdventure(
            token = token,
            adventureId = adventureId,
            userMessage = userMessage,
        )
    }
}
