package input.comprehensible.data.stories.model

/**
 * A list of stories.
 */
data class StoriesList(
    val stories: List<StoriesItem>
) {
    /**
     * A story.
     */
    data class StoriesItem(
        val id: String,
        val title: String,
    )
}
