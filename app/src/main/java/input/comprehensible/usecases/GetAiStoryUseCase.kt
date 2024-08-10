package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.stories.StoriesRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case for getting an AI story.
 */
class GetAiStoryUseCase @Inject constructor(
    private val languageSettingsRepository: LanguageSettingsRepository,
    private val storiesRepository: StoriesRepository,
) {
    /**
     * Gets an AI story.
     */
    operator fun invoke() = combine(
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { learningLanguage, translationsLanguage ->
        storiesRepository.getAiStory(
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
