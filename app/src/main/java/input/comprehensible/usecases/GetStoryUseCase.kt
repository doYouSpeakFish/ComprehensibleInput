package input.comprehensible.usecases

import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.stories.StoriesRepository
import input.comprehensible.data.stories.StoryResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * A use case for getting a story.
 */
class GetStoryUseCase @Inject constructor(
    private val languageSettingsRepository: LanguageSettingsRepository,
    private val storiesRepository: StoriesRepository,
) {
    /**
     * Gets a story.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: String): Flow<StoryResult> = combine(
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { learningLanguage, translationsLanguage ->
        learningLanguage to translationsLanguage
    }.flatMapLatest { (learningLanguage, translationsLanguage) ->
        storiesRepository.getStory(
            id = id,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
