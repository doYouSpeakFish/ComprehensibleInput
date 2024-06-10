package input.comprehensible.ui.storyreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.StoriesRepository
import input.comprehensible.ui.components.storycontent.part.toStoryContentPartUiState
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
    val state = savedStateHandle
        .getStateFlow<String?>("storyId", null)
        .map {
            if (it == null) {
                StoryReaderUiState.Loading
            } else {
                val story = requireNotNull(storiesRepository.getStory(it)) {
                    "Story with id $it not found"
                }
                StoryReaderUiState.Loaded(
                    title = story.title,
                    content = story.content
                        .map { storyElement -> storyElement.toStoryContentPartUiState() }
                )
            }
        }
}
