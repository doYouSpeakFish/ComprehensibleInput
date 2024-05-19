package `in`.comprehensible.ui.storyreader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.comprehensible.data.stories.StoriesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A view model for providing the data for a story to the UI.
 */
@HiltViewModel
class StoryReaderViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository
) : ViewModel() {
    val story = flow { emit(storiesRepository.getStory()) }

    val state = story.map {
        StoryReaderUiState.Loaded(
            title = it.title,
            content = it.content
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = StoryReaderUiState.Loading
        )
}