package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository

/**
 * Sets the language the user is learning.
 */
class SetLearningLanguageUseCase(
    private val repository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    suspend operator fun invoke(languageCode: String) {
        repository.setLearningLanguage(languageCode)
    }
}
