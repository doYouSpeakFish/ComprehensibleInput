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
     * The UI state when the story fails to load.
     */
    data object Error : StoryReaderUiState

    /**
     * The UI state when the story is loaded.
     */
    data class Loaded(
        val title: String,
        val parts: List<StoryReaderPartUiState>,
        val currentPartId: String,
        val initialContentIndex: Int,
        val selectedText: SelectedText?,
    ) : StoryReaderUiState

    sealed interface SelectedText {
        data class Title(
            val isTranslated: Boolean,
        ) : SelectedText

        data class SentenceInParagraph(
            val partIndex: Int,
            val paragraphIndex: Int,
            val selectedSentenceIndex: Int,
            val isTranslated: Boolean,
        ) : SelectedText

        data class ChoiceOption(
            val partIndex: Int,
            val optionIndex: Int,
            val isTranslated: Boolean,
        ) : SelectedText
    }
}

data class StoryReaderPartUiState(
    val id: String,
    val content: List<StoryContentPartUiState>,
)
