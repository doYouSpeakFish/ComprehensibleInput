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
import input.comprehensible.usecases.GetStoryUseCase
import input.comprehensible.ui.storyreader.StoryReaderUiState.SelectedText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

    private val storyLoadState: Flow<StoryLoadState> = getStoryUseCase(id = id)
        .map { result ->
            when (result) {
                is StoryResult.Success -> StoryLoadState.Loaded(result.story)
                StoryResult.Error -> StoryLoadState.Error
            }
        }
        .onStart {
            emit(StoryLoadState.Loading)
        }
        .distinctUntilChanged()

    private val selectedTextState = MutableStateFlow<SelectedText?>(null)

    val state = combine(
        storyLoadState,
        selectedTextState,
    ) { storyLoadState, selectedText ->
        when (storyLoadState) {
            StoryLoadState.Loading -> StoryReaderUiState.Loading
            StoryLoadState.Error -> StoryReaderUiState.Error
            is StoryLoadState.Loaded -> {
                val story = storyLoadState.story
                val content = story.toContentItems()
                StoryReaderUiState.Loaded(
                    title = if (selectedText is SelectedText.Title && selectedText.isTranslated) {
                        story.translatedTitle
                    } else {
                        story.title
                    },
                    isTitleHighlighted = selectedText is SelectedText.Title,
                    content = content,
                    currentPartId = story.currentPartId,
                    initialContentIndex = story.storyPosition,
                    selectedText = selectedText,
                )
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = StoryReaderUiState.Loading
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

    fun onChoiceSelected(targetPartId: String) {
        viewModelScope.launch {
            storiesRepository.updateStoryPart(
                id = id,
                partId = targetPartId,
            )
        }
    }

    private fun Story.toContentItems(): List<StoryContentPartUiState> {
        var paragraphIndex = 0
        return buildList {
            parts.forEach { part ->
                part.elements.forEach { element ->
                    when (element) {
                        is StoryElement.Paragraph -> {
                            val currentIndex = paragraphIndex++
                            add(element.toParagraphUiState(currentIndex))
                        }

                        is StoryElement.Image -> add(
                            StoryContentPartUiState.Image(
                                contentDescription = element.contentDescription,
                                bitmap = element.bitmap,
                            )
                        )
                    }
                }

                when (val choice = part.choice) {
                    is StoryChoice.Available -> add(
                        StoryContentPartUiState.Choices(
                            options = choice.options.map { option ->
                                StoryContentPartUiState.Choices.Option(
                                    text = option.text,
                                    onClick = { onChoiceSelected(option.targetPartId) },
                                )
                            }
                        )
                    )

                    is StoryChoice.Chosen -> add(
                        StoryContentPartUiState.ChosenChoice(text = choice.option.text)
                    )

                    null -> Unit
                }
            }
        }
    }

    private fun StoryElement.Paragraph.toParagraphUiState(
        paragraphIndex: Int,
    ): StoryContentPartUiState.Paragraph {
        return StoryContentPartUiState.Paragraph(
            paragraphIndex = paragraphIndex,
            sentences = sentences,
            translatedSentences = sentencesTranslations,
            onClick = { sentenceIndex ->
                onSentenceSelected(
                    paragraphIndex = paragraphIndex,
                    sentenceIndex = sentenceIndex,
                )
            },
        )
    }

    private fun onSentenceSelected(
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
}

private sealed interface StoryLoadState {
    data object Loading : StoryLoadState
    data object Error : StoryLoadState
    data class Loaded(val story: Story) : StoryLoadState
}

