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
                ?.toPartUiStates(onChoiceSelected = ::onChoiceSelected)
                .orEmpty()
        }
    private val selectedTextState = MutableStateFlow<SelectedText?>(null)
    private val partIdToScrollTo = MutableStateFlow<String?>(null)

    val state = combine(
        story,
        selectedTextState,
        content,
        partIdToScrollTo,
    ) { storyResult, selectedText, content, latestSelectedPartId ->
        when (storyResult) {
            StoryResult.Error -> Error
            is StoryResult.Success -> Loaded(
                title = if (selectedText is SelectedText.Title && selectedText.isTranslated) {
                    storyResult.story.translatedTitle
                } else {
                    storyResult.story.title
                },
                parts = content,
                currentPartId = storyResult.story.currentPartId,
                initialContentIndex = storyResult.story.storyPosition,
                selectedText = selectedText,
                scrollingToPage = content
                    .indexOfLast { it.id == latestSelectedPartId }
                    .takeIf { it > 0 }
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
     */
    fun onSentenceSelected(
        partIndex: Int,
        paragraphIndex: Int,
        sentenceIndex: Int,
    ) {
        selectedTextState.update { selectedText ->
            val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
            val samePart = selectedSentence?.partIndex == partIndex
            val sameParagraph = selectedSentence?.paragraphIndex == paragraphIndex
            val sameSentence = selectedSentence?.selectedSentenceIndex == sentenceIndex
            if (samePart && sameParagraph && sameSentence) {
                return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
            }

            SelectedText.SentenceInParagraph(
                partIndex = partIndex,
                paragraphIndex = paragraphIndex,
                selectedSentenceIndex = sentenceIndex,
                isTranslated = true
            )
        }
    }

    /**
     * Handles the selection of a story choice's text so it can be translated.
     *
     * If the same choice option is selected again, it toggles its translated state.
     * Otherwise, it selects the new option and shows its translation.
     */
    fun onChoiceTextSelected(
        partIndex: Int,
        optionIndex: Int,
    ) {
        selectedTextState.update { selectedText ->
            val selectedChoice = selectedText as? SelectedText.ChoiceOption
            val samePart = selectedChoice?.partIndex == partIndex
            val sameOption = selectedChoice?.optionIndex == optionIndex
            if (samePart && sameOption) {
                return@update selectedChoice.copy(isTranslated = !selectedChoice.isTranslated)
            }

            SelectedText.ChoiceOption(
                partIndex = partIndex,
                optionIndex = optionIndex,
                isTranslated = true,
            )
        }
    }

    /**
     * Persists the current story location within a page, so if the story is closed, it can be
     * resumed from this point.
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
     * Persists the current part, so if the story is closed, it can be resumed from this point.
     */
    fun onCurrentPartChanged(partId: String) {
        viewModelScope.launch {
            storiesRepository.updateStoryPart(
                id = id,
                partId = partId,
            )
        }
    }

    /**
     * Persists the user's choice, appending the chosen part to the current story.
     */
    fun onChoiceSelected(targetPartId: String) {
        viewModelScope.launch {
            partIdToScrollTo.value = targetPartId
            storiesRepository.updateStoryChoice(
                id = id,
                partId = targetPartId,
            )
        }
    }

    fun onPartScrolledTo() {
        partIdToScrollTo.value = null
    }
}

private fun Story.toPartUiStates(
    onChoiceSelected: (String) -> Unit,
): List<StoryReaderPartUiState> = parts.map { part ->
    StoryReaderPartUiState(
        id = part.id,
        content = buildList {
            part.elements.forEach { element ->
                add(element.toStoryContentPartUiState())
            }
            add(part.choices.toChoicesUiState(onChoiceSelected = onChoiceSelected))
        },
    )
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

private fun List<StoryChoice>.toChoicesUiState(
    onChoiceSelected: (String) -> Unit,
): StoryContentPartUiState.Choices {
    return StoryContentPartUiState.Choices(
        options = map { choice ->
            choice.toChoiceUiState(
                onChoiceSelected = onChoiceSelected,
                isChosen = choice.isChosen,
            )
        }
    )
}

private fun StoryChoice.toChoiceUiState(
    onChoiceSelected: (String) -> Unit,
    isChosen: Boolean,
) = StoryContentPartUiState.Choices.Option(
    id = targetPartId,
    text = text,
    translatedText = translatedText,
    onClick = { onChoiceSelected(targetPartId) },
    isChosen = isChosen,
)
