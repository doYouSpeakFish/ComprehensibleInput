package input.comprehensible.ui.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.StoriesRepository
import input.comprehensible.ui.components.LanguageSelection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A ViewModel for the StoryList screen.
 */
@HiltViewModel
class StoryListViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository
) : ViewModel() {
    val state = combine(
        storiesRepository.storiesList,
        storiesRepository.learningLanguage,
    ) { storiesList, learningLanguage ->
        StoryListUiState(
            stories = storiesList.stories.map { story ->
                StoryListUiState.StoryListItem(
                    id = story.id,
                    title = story.title,
                    subtitle = story.titleTranslated,
                    featuredImage = story.featuredImage,
                    featuredImageContentDescription = story.featuredImageContentDescription,
                )
            },
            learningLanguage = LanguageSelection.entries
                .firstOrNull { it.languageCode == learningLanguage },
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
        storiesRepository.setLearningLanguage(learningLanguage.languageCode)
    }
}
