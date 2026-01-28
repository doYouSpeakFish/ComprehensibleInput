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
    val items: List<StoryListItem>,
) {
    /**
     * A list item in the story list.
     */
    sealed interface StoryListItem {
        data class Story(
            val id: String,
            val title: String,
            val subtitle: String,
            val featuredImage: Bitmap,
        ) : StoryListItem

        data class TextAdventure(
            val id: String,
            val title: String,
            val isComplete: Boolean,
        ) : StoryListItem

        data object StartTextAdventure : StoryListItem
    }

    companion object {
        val INITIAL = StoryListUiState(
            items = emptyList(),
            learningLanguage = null,
            translationLanguage = null,
            languagesAvailable = emptyList()
        )
    }
}
