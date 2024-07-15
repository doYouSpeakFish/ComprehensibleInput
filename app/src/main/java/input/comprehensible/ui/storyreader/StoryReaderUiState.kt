package input.comprehensible.ui.storyreader

import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState

/**
 * The UI state for the story reader screen.
 */
sealed interface StoryReaderUiState {
    /**
     * The UI state when the story is loading.
     */
    data object Loading : StoryReaderUiState

    /**
     * The UI state when the story is loaded.
     */
    data class Loaded(
        val title: String,
        val isTitleHighlighted: Boolean,
        val content: List<StoryContentPartUiState>,
    ) : StoryReaderUiState
}
