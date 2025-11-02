package input.comprehensible.data.stories.model

import android.graphics.Bitmap

/**
 * A list of stories.
 */
data class StoriesList(
    val stories: List<StoriesItem>,
) {
    /**
     * A story.
     */
    data class StoriesItem(
        val id: String,
        val title: String,
        val titleTranslated: String,
        val featuredImage: Bitmap,
    )
}

sealed interface StoriesListResult {
    data class Success(val storiesList: StoriesList) : StoriesListResult
    object Error : StoriesListResult
}
