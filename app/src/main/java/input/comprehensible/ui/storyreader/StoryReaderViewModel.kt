package input.comprehensible.ui.storyreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.StoriesRepository
import input.comprehensible.ui.components.storycontent.part.toStoryContentPartUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A view model for providing the data for a story to the UI.
 */
@HiltViewModel
class StoryReaderViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val areTranslationsEnabled = MutableStateFlow(false)
    private val story = savedStateHandle
        .getStateFlow<String?>("storyId", null)
        .map {
            requireNotNull(storiesRepository.getStory(it ?: return@map null)) {
                "Story with id $it not found"
            }
        }

    val state = combine(
        story,
        areTranslationsEnabled
    ) { story, areTranslationsEnabled ->
        if (story == null) {
            StoryReaderUiState.Loading
        } else {
            StoryReaderUiState.Loaded(
                title = if (areTranslationsEnabled) story.translatedTitle else story.title,
                content = story.content
                    .map { storyElement ->
                        storyElement.toStoryContentPartUiState(
                            areTranslationsEnabled = areTranslationsEnabled,
                        )
                    },
                areTranslationsEnabled = areTranslationsEnabled
            )
        }
    }

    /**
     * Switch to the translation language, or disable translations to switch back to the learning
     * language.
     */
    fun onTranslationsEnabledChanged(enabled: Boolean) {
        areTranslationsEnabled.value = enabled
    }
}
