package input.comprehensible.usecases

import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

/**
 * Continues a text adventure with the user's response.
 */
class ContinueTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    suspend operator fun invoke(adventureId: String, userMessage: String) {
        try {
            repository.respondToAdventure(adventureId = adventureId, userMessage = userMessage)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to send message for adventure %s", adventureId)
        }
    }

    suspend fun retry(adventureId: String) {
        try {
            repository.retryAdventureResponse(adventureId = adventureId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to retry adventure %s", adventureId)
        }
    }
}
