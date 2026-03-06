package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Gets the user's language settings.
 */
class GetLanguageSettingsUseCase(
    private val repository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    val learningLanguage: Flow<String> = repository.learningLanguage
    val translationsLanguage: Flow<String> = repository.translationsLanguage
}
