package input.comprehensible.ui.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.StoriesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A ViewModel for the StoryList screen.
 */
@HiltViewModel
class StoryListViewModel @Inject constructor(
    storiesRepository: StoriesRepository
) : ViewModel() {
    val state = storiesRepository.storiesList
        .onEach { println(it) }
        .map {
            StoryListUiState(
                stories = it.stories.map { story ->
                    StoryListUiState.StoryListItem(
                        id = story.id,
                        title = story.title,
                        subtitle = story.subtitle,
                        featuredImage = story.featuredImage,
                        featuredImageContentDescription = story.featuredImageContentDescription,
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = StoryListUiState(emptyList())
        )
}
