package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Starts a new text adventure.
 */
class StartTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    fun generateAdventureId(): String = repository.generateAdventureId()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(adventureId: String) {
        try {
            val learningLanguage = languageSettingsRepository.learningLanguage.first()
            val translationsLanguage = languageSettingsRepository.translationsLanguage.first()
            repository.startNewAdventure(
                adventureId = adventureId,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to start text adventure %s", adventureId)
        }
    }
}
