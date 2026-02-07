package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

/**
 * Starts a new text adventure.
 */
class StartTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): String {
        val learningLanguage = languageSettingsRepository.learningLanguage.first()
        val translationsLanguage = languageSettingsRepository.translationsLanguage.first()
        return repository.startNewAdventure(
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
