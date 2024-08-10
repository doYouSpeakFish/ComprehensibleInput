package input.comprehensible.ui.storyreader

import androidx.compose.ui.text.TextRange
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.usecases.GetAiStoryUseCase
import input.comprehensible.usecases.GetStoryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

/**
 * A view model for providing the data for a story to the UI.
 */
@HiltViewModel
class StoryReaderViewModel @Inject constructor(
    getStoryUseCase: GetStoryUseCase,
    getAiStoryUseCase: GetAiStoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storyId: String? = savedStateHandle["storyId"]

    private val story: Flow<Story?> = if (storyId == null) {
        Timber.d("Generating AI story")
        getAiStoryUseCase()
    } else {
        Timber.d("Loading story with id $storyId")
        getStoryUseCase(id = storyId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    private val selectedText = MutableStateFlow<SelectedText?>(null)

    val state = combine(
        story,
        selectedText,
    ) { story, selectedText ->
        if (story == null) {
            StoryReaderUiState.Loading
        } else {
            val selectedSentence = selectedText as? SelectedText.SentenceInParagraph
            StoryReaderUiState.Loaded(
                title = if (selectedText is SelectedText.Title && selectedText.isTranslated) {
                    story.translatedTitle
                } else {
                    story.title
                },
                isTitleHighlighted = selectedText is SelectedText.Title,
                content = story.content
                    .mapIndexed { i, storyElement ->
                        storyElement.toStoryContentPartUiState(
                            paragraphIndex = i,
                            selectedSentenceIndex = selectedSentence?.takeIf { it.paragraphIndex == i }?.selectedSentenceIndex,
                            areTranslationsEnabled = selectedSentence?.isTranslated == true,
                        )
                    },
            )
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
                return@update selectedSentence?.copy(isTranslated = !selectedText.isTranslated)
            }

            SelectedText.SentenceInParagraph(
                paragraphIndex = paragraphIndex,
                selectedSentenceIndex = sentenceIndex,
                isTranslated = true
            )
        }
    }
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