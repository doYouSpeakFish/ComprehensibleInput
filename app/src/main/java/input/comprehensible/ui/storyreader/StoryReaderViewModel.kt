package input.comprehensible.ui.storyreader

import androidx.compose.ui.text.TextRange
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

    private val selectedText = MutableStateFlow<SelectedText?>(null)
    private val pendingChoicePartId = MutableStateFlow<String?>(null)
    private val latestStory = MutableStateFlow<Story?>(null)

    val state = combine(
        storyLoadState,
        selectedText,
    ) { storyLoadState, selectedText ->
        when (storyLoadState) {
            StoryLoadState.Loading -> StoryReaderUiState.Loading
            StoryLoadState.Error -> StoryReaderUiState.Error
            is StoryLoadState.Loaded -> {
                val story = storyLoadState.story
                latestStory.value = story
                if (pendingChoicePartId.value == story.currentPartId) {
                    pendingChoicePartId.value = null
                }
                val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
                val content = story.toContentItems(selectedSentence)
                StoryReaderUiState.Loaded(
                    title = if (selectedText is SelectedText.Title && selectedText.isTranslated) {
                        story.translatedTitle
                    } else {
                        story.title
                    },
                    isTitleHighlighted = selectedText is SelectedText.Title,
                    content = content,
                    currentPartId = story.currentPartId,
                    initialContentIndex = content.findInitialContentIndex(
                        partId = story.currentPartId,
                        elementIndex = story.currentElementIndex,
                    ),
                )
            }
        }
    }

    /**
     * Toggles whether translations are enabled for the title.
     */
    fun onTitleSelected() {
        selectedText.update { selectedText ->
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
    fun onStoryLocationUpdated(partId: String, elementIndex: Int) {
        viewModelScope.launch {
            val pendingChoice = pendingChoicePartId.value
            if (pendingChoice != null && pendingChoice != partId) {
                return@launch
            }
            val story = latestStory.value ?: return@launch
            val partOrder = story.parts.map { it.id }
            val currentIndex = partOrder.indexOf(story.currentPartId).takeIf { it >= 0 } ?: 0
            val newIndex = partOrder.indexOf(partId)
            if (newIndex == -1) {
                return@launch
            }
            if (pendingChoice == partId) {
                pendingChoicePartId.value = null
            }
            val shouldAdvancePart = newIndex >= currentIndex
            val partIdToPersist = if (shouldAdvancePart) {
                partId
            } else {
                story.currentPartId
            }
            val elementIndexToPersist = if (!shouldAdvancePart) {
                story.currentElementIndex
            } else {
                elementIndex
            }
            storiesRepository.updateStoryPosition(
                id = id,
                partId = partIdToPersist,
                elementIndex = elementIndexToPersist,
            )
        }
    }

    fun onChoiceSelected(targetPartId: String) {
        viewModelScope.launch {
            pendingChoicePartId.value = targetPartId
            storiesRepository.updateStoryPosition(
                id = id,
                partId = targetPartId,
                elementIndex = 0,
            )
        }
    }

    private fun Story.toContentItems(selectedSentence: SelectedText.SentenceInParagraph?): List<StoryContentItemUiState> {
        val items = mutableListOf<StoryContentItemUiState>()
        var paragraphCounter = 0

        parts.forEach { part ->
            part.elements.forEachIndexed { elementIndex, element ->
                when (element) {
                    is StoryElement.Paragraph -> {
                        val paragraphIndex = paragraphCounter++
                        val selectedForParagraph = selectedSentence?.takeIf { it.paragraphIndex == paragraphIndex }
                        val paragraphUiState = element.toStoryContentPartUiState(
                            paragraphIndex = paragraphIndex,
                            selectedSentenceIndex = selectedForParagraph?.selectedSentenceIndex,
                            areTranslationsEnabled = selectedForParagraph?.isTranslated == true,
                        )
                        items += StoryContentItemUiState(
                            partId = part.id,
                            elementIndexInPart = elementIndex,
                            content = paragraphUiState,
                        )
                    }

                    is StoryElement.Image -> {
                        items += StoryContentItemUiState(
                            partId = part.id,
                            elementIndexInPart = elementIndex,
                            content = StoryContentPartUiState.Image(
                                contentDescription = element.contentDescription,
                                bitmap = element.bitmap,
                            ),
                        )
                    }
                }
            }

            when (val choice = part.choice) {
                is StoryChoice.Available -> {
                      val options = choice.options.map { option ->
                          StoryContentPartUiState.Choices.Option(
                              text = option.text,
                              onClick = { onChoiceSelected(option.targetPartId) },
                          )
                    }
                    items += StoryContentItemUiState(
                        partId = part.id,
                        elementIndexInPart = part.elements.size,
                        content = StoryContentPartUiState.Choices(options = options),
                    )
                }

                is StoryChoice.Chosen -> {
                    items += StoryContentItemUiState(
                        partId = part.id,
                        elementIndexInPart = part.elements.size,
                        content = StoryContentPartUiState.ChosenChoice(text = choice.option.text),
                    )
                }

                null -> Unit
            }
        }

        return items
    }

    private fun List<StoryContentItemUiState>.findInitialContentIndex(
        partId: String,
        elementIndex: Int,
    ): Int {
        val exactMatch = indexOfFirst { item ->
            item.partId == partId && item.elementIndexInPart == elementIndex
        }
        if (exactMatch >= 0) {
            return exactMatch
        }
        val partMatch = indexOfFirst { item -> item.partId == partId }
        if (partMatch >= 0) {
            return partMatch
        }
        return 0
    }

    private fun StoryElement.toStoryContentPartUiState(
        paragraphIndex: Int,
        selectedSentenceIndex: Int?,
        areTranslationsEnabled: Boolean,
    ) = when (this) {
        is StoryElement.Paragraph -> this.toStoryContentPartUiState(
            paragraphIndex = paragraphIndex,
            selectedSentenceIndex = selectedSentenceIndex,
            areTranslationsEnabled = areTranslationsEnabled,
        )

        is StoryElement.Image -> StoryContentPartUiState.Image(
            contentDescription = contentDescription,
            bitmap = bitmap
        )
    }

    private fun StoryElement.Paragraph.toStoryContentPartUiState(
        paragraphIndex: Int,
        selectedSentenceIndex: Int?,
        areTranslationsEnabled: Boolean,
    ): StoryContentPartUiState.Paragraph {
        val combinedSentences = List(sentences.size) { i ->
            if (i == selectedSentenceIndex && areTranslationsEnabled) {
                sentencesTranslations[i]
            } else {
                sentences[i]
            }
        }
        val sentencesIndex = combinedSentences
            .runningFold(0) { acc, sentence -> acc + sentence.length + 1 }
            .zipWithNext { a, b -> TextRange(a, b) }
        return StoryContentPartUiState.Paragraph(
            paragraph = combinedSentences.joinToString(separator = " "),
            onClick = { characterIndex ->
                onSentenceSelected(
                    paragraphIndex = paragraphIndex,
                    sentenceIndex = sentencesIndex.indexOfFirst { characterIndex in it },
                )
            },
            selectedTextRange = if (selectedSentenceIndex != null) {
                val selectedSentenceRange = sentencesIndex[selectedSentenceIndex]
                TextRange(
                    start = selectedSentenceRange.start,
                    end = selectedSentenceRange.end - 1, // Don't highlight space between sentences
                )
            } else {
                null
            }
        )
    }

    private fun onSentenceSelected(
        paragraphIndex: Int,
        sentenceIndex: Int,
    ) {
        selectedText.update { selectedText ->
            val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
            val sameParagraph = selectedSentence?.paragraphIndex == paragraphIndex
            val sameSentence = selectedSentence?.selectedSentenceIndex == sentenceIndex
            if (sameParagraph && sameSentence) {
                return@update selectedSentence.copy(isTranslated = !selectedText.isTranslated)
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

private sealed interface SelectedText {
    data class Title(
        val isTranslated: Boolean,
    ) : SelectedText

    data class SentenceInParagraph(
        val paragraphIndex: Int,
        val selectedSentenceIndex: Int,
        val isTranslated: Boolean,
    ) : SelectedText
}
