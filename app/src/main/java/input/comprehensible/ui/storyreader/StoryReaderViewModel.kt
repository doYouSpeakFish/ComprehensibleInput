package input.comprehensible.ui.storyreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.StoriesRepository
import input.comprehensible.data.stories.StoryResult
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryChoice
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.storyreader.StoryReaderUiState.Error
import input.comprehensible.ui.storyreader.StoryReaderUiState.Loaded
import input.comprehensible.ui.storyreader.StoryReaderUiState.Loading
import input.comprehensible.ui.storyreader.StoryReaderUiState.SelectedText
import input.comprehensible.usecases.GetStoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A view model for providing the data for a story to the UI.
 */
@HiltViewModel
class StoryReaderViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository,
    getStoryUseCase: GetStoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = requireNotNull(savedStateHandle["storyId"]) {
        "Story opened without an explicit story ID"
    }

    private val story = getStoryUseCase(id = id)
    private val content = story
        .map {
            (it as? StoryResult.Success)?.story
                ?.toContentItems(onChoiceSelected = ::onChoiceSelected)
                .orEmpty()
        }
    private val selectedTextState = MutableStateFlow<SelectedText?>(null)

    val state = combine(
        story,
        selectedTextState,
        content,
    ) { storyResult, selectedText, content ->
        when (storyResult) {
            StoryResult.Error -> Error
            is StoryResult.Success -> Loaded(
                title = if (selectedText is SelectedText.Title && selectedText.isTranslated) {
                    storyResult.story.translatedTitle
                } else {
                    storyResult.story.title
                },
                content = content,
                currentPartId = storyResult.story.currentPartId,
                initialContentIndex = storyResult.story.storyPosition,
                selectedText = selectedText,
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Loading
    )

    /**
     * Toggles whether translations are enabled for the title.
     */
    fun onTitleSelected() {
        selectedTextState.update { selectedText ->
            if (selectedText is SelectedText.Title) {
                selectedText.copy(isTranslated = !selectedText.isTranslated)
            } else {
                SelectedText.Title(isTranslated = true)
            }
        }
    }

    /**
     * Handles the selection of a sentence within a paragraph.
     *
     * If the selected sentence is the same as the one already selected, it toggles its translated
     * state. Otherwise, it updates the selection to the new sentence and shows its translation.
     *
     * @param paragraphIndex The index of the paragraph containing the selected sentence.
     * @param sentenceIndex The index of the selected sentence within the paragraph.
     */
    fun onSentenceSelected(
        paragraphIndex: Int,
        sentenceIndex: Int,
    ) {
        selectedTextState.update { selectedText ->
            val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
            val sameParagraph = selectedSentence?.paragraphIndex == paragraphIndex
            val sameSentence = selectedSentence?.selectedSentenceIndex == sentenceIndex
            if (sameParagraph && sameSentence) {
                return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
            }

            SelectedText.SentenceInParagraph(
                paragraphIndex = paragraphIndex,
                selectedSentenceIndex = sentenceIndex,
                isTranslated = true
            )
        }
    }

    /**
     * Persists the current story location, so if the story is closed, it can be resumed from this
     * point.
     */
    fun onStoryLocationUpdated(elementIndex: Int) {
        viewModelScope.launch {
            storiesRepository.updateStoryPosition(
                id = id,
                storyPosition = elementIndex,
            )
        }
    }

    /**
     * Persists the user's choice, appending the chosen part to the current story.
     *
     * @param targetPartId The ID of the story part that the user has chosen.
     */
    fun onChoiceSelected(targetPartId: String) {
        viewModelScope.launch {
            storiesRepository.updateStoryPart(
                id = id,
                partId = targetPartId,
            )
        }
    }
}

private fun Story.toContentItems(
    onChoiceSelected: (String) -> Unit,
): List<StoryContentPartUiState> = buildList {
    parts.forEach { part ->
        part.elements.forEach { element ->
            add(element.toStoryContentPartUiState())
        }
        part.choice?.toStoryContentPartUiState(onChoiceSelected = onChoiceSelected)
            ?.let(::add)
    }
}

private fun StoryElement.toStoryContentPartUiState() = when (this) {
    is StoryElement.Paragraph -> toParagraphUiState()
    is StoryElement.Image -> toImageUiState()
}

private fun StoryElement.Paragraph.toParagraphUiState() =
    StoryContentPartUiState.Paragraph(
        sentences = sentences,
        translatedSentences = sentencesTranslations,
    )

private fun StoryElement.Image.toImageUiState() = StoryContentPartUiState.Image(
    contentDescription = contentDescription,
    bitmap = bitmap,
)

private fun StoryChoice.toStoryContentPartUiState(
    onChoiceSelected: (String) -> Unit,
) = when (this) {
    is StoryChoice.Available -> toChoicesUiState(onChoiceSelected = onChoiceSelected)
    is StoryChoice.Chosen -> toChoiceUiState()
}

private fun StoryChoice.Available.toChoicesUiState(
    onChoiceSelected: (String) -> Unit,
) = StoryContentPartUiState.Choices(
    options = options.map { option ->
        StoryContentPartUiState.Choices.Option(
            text = option.text,
            onClick = { onChoiceSelected(option.targetPartId) },
        )
    }
)

private fun StoryChoice.Chosen.toChoiceUiState() =
    StoryContentPartUiState.ChosenChoice(text = option.text)
