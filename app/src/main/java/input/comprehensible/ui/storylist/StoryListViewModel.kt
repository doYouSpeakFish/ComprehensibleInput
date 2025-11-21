package input.comprehensible.ui.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.stories.StoriesListResult
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.usecases.GetStoriesListUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel for the StoryList screen.
 */
@HiltViewModel
class StoryListViewModel @Inject constructor(
    private val languageSettingsRepository: LanguageSettingsRepository,
    getStoriesListUseCase: GetStoriesListUseCase,
) : ViewModel() {
    val state = combine(
        getStoriesListUseCase(),
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { storiesResult, learningLanguage, translationsLanguage ->
        val stories = when (storiesResult) {
            is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
                StoryListUiState.StoryListItem(
                    id = story.id,
                    title = story.title,
                    subtitle = story.titleTranslated,
                    featuredImage = story.featuredImage,
                )
            }

            StoriesListResult.Error -> emptyList()
        }
        StoryListUiState(
            stories = stories,
            learningLanguage = LanguageSelection.entries
                .firstOrNull { it.languageCode == learningLanguage },
            translationLanguage = LanguageSelection.entries
                .firstOrNull { it.languageCode == translationsLanguage },
            languagesAvailable = LanguageSelection.entries
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = StoryListUiState.INITIAL
        )

    /**
     * Called when the user selects a language to learn.
     */
    fun onLearningLanguageSelected(learningLanguage: LanguageSelection) {
        viewModelScope.launch {
            languageSettingsRepository.setLearningLanguage(learningLanguage.languageCode)
        }
    }

    /**
     * Called when the user selects a language to learn.
     */
    fun onTranslationLanguageSelected(translationLanguage: LanguageSelection) {
        viewModelScope.launch {
            languageSettingsRepository.setTranslationLanguage(translationLanguage.languageCode)
        }
    }
}
