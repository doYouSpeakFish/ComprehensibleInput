package input.comprehensible.ui.storylist

import android.graphics.Bitmap
import input.comprehensible.ui.components.LanguageSelection

/**
 * The UI state for the story list screen.
 */
data class StoryListUiState(
    val learningLanguage: LanguageSelection?,
    val translationLanguage: LanguageSelection?,
    val languagesAvailable: List<LanguageSelection>,
    val stories: List<StoryListItem>,
    val areAiStoriesAvailable: Boolean,
) {
    /**
     * A story list item.
     */
    data class StoryListItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val featuredImage: Bitmap,
        val featuredImageContentDescription: String,
    )

    companion object {
        val INITIAL = StoryListUiState(
            stories = emptyList(),
            learningLanguage = null,
            translationLanguage = null,
            languagesAvailable = emptyList(),
            areAiStoriesAvailable = false,
        )
    }
}
