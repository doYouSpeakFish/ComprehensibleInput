package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.flow.first

/**
 * Starts a new text adventure.
 */
class StartTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    fun generateAdventureId(): String = repository.generateAdventureId()

    suspend operator fun invoke(adventureId: String) {
        val learningLanguage = languageSettingsRepository.learningLanguage.first()
        val translationsLanguage = languageSettingsRepository.translationsLanguage.first()
        repository.startNewAdventure(
            adventureId = adventureId,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
