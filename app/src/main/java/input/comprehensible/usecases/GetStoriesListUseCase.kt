package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.stories.StoriesListResult
import input.comprehensible.data.stories.StoriesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

/**
 * A use case for getting the list of stories.
 */
class GetStoriesListUseCase(
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
    private val storiesRepository: StoriesRepository = StoriesRepository(),
) {
    /**
     * Gets the list of stories.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<StoriesListResult> = combine(
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { learningLanguage, translationsLanguage ->
        learningLanguage to translationsLanguage
    }.flatMapLatest { (learningLanguage, translationsLanguage) ->
        storiesRepository.getStories(
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
