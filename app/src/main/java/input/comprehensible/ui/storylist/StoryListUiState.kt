package input.comprehensible.ui.storylist

/**
 * The UI state for the story list screen.
 */
data class StoryListUiState(
    val stories: List<StoryListItem>
) {
    /**
     * A story list item.
     */
    data class StoryListItem(
        val id: String,
        val title: String,
    )

    companion object {
        val INITIAL = StoryListUiState(stories = emptyList())
    }
}
