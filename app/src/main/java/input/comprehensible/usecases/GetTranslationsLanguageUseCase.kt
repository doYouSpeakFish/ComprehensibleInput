package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Gets the user's translations language setting.
 */
class GetTranslationsLanguageUseCase(
    private val repository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    operator fun invoke(): Flow<String> = repository.translationsLanguage
}
