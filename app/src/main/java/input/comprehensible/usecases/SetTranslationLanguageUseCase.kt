package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository

/**
 * Sets the language to display translations in.
 */
class SetTranslationLanguageUseCase(
    private val repository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    suspend operator fun invoke(languageCode: String) {
        repository.setTranslationLanguage(languageCode)
    }
}
