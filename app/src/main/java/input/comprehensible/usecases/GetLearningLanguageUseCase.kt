package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Gets the user's learning language setting.
 */
class GetLearningLanguageUseCase(
    private val repository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    operator fun invoke(): Flow<String> = repository.learningLanguage
}
