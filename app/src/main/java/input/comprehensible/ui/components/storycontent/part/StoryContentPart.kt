package input.comprehensible.ui.components.storycontent.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import input.comprehensible.ui.storyreader.StoryReaderUiState

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState,
    selectedText: StoryReaderUiState.SelectedText? = null,
) {
    Box(modifier) {
        when (state) {
            is StoryContentPartUiState.Paragraph -> Paragraph(
                state = state,
                selectedText = selectedText,
            )
            is StoryContentPartUiState.Image -> StoryImage(state = state)
            is StoryContentPartUiState.Choices -> StoryChoices(state = state)
            is StoryContentPartUiState.ChosenChoice -> StoryChosenChoice(state = state)
        }
    }
}

@Composable
private fun Paragraph(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Paragraph,
    selectedText: StoryReaderUiState.SelectedText?,
) {
    Box(modifier) {
        val textStyle = MaterialTheme.typography.bodyLarge
        val defaultSpanStyle = SpanStyle(
            background = Color.Transparent,
            color = LocalContentColor.current,
        )
        val colorScheme = MaterialTheme.colorScheme
        val highlightedSpanStyle = remember(colorScheme.background, colorScheme.onBackground) {
            SpanStyle(
                color = colorScheme.background,
                background = colorScheme.onBackground,
            )
        }
        val selectedSentence = (selectedText as? StoryReaderUiState.SelectedText.SentenceInParagraph)
            ?.takeIf { it.paragraphIndex == state.paragraphIndex }
        val annotatedText = remember(
            state.paragraphIndex,
            state.sentences,
            state.translatedSentences,
            highlightedSpanStyle,
            defaultSpanStyle,
            selectedSentence,
        ) {
            buildAnnotatedString {
                state.sentences.forEachIndexed { index, sentence ->
                    val isSelected = selectedSentence?.selectedSentenceIndex == index
                    val textToDisplay = if (isSelected && selectedSentence.isTranslated) {
                        state.translatedSentences.getOrNull(index) ?: sentence
                    } else {
                        sentence
                    }
                    withLink(
                        link = LinkAnnotation.Clickable(
                            tag = "sentence-$index",
                            linkInteractionListener = LinkInteractionListener {
                                state.onClick(index)
                            }
                        )
                    ) {
                        withStyle(
                            if (isSelected) {
                                highlightedSpanStyle
                            } else {
                                defaultSpanStyle
                            }
                        ) {
                            append(textToDisplay)
                        }
                    }
                    if (index != state.sentences.lastIndex) {
                        withStyle(defaultSpanStyle) {
                            append(" ")
                        }
                    }
                }
            }
        }
        BasicText(
            modifier = Modifier.padding(bottom = 16.dp),
            text = annotatedText,
            style = textStyle,
        )
    }
}

@Composable
private fun StoryImage(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Image
) {
    Box(modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp)
                ),
            bitmap = state.bitmap.asImageBitmap(),
            contentDescription = state.contentDescription,
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Composable
private fun StoryChoices(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Choices,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.options.forEach { option ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = option.onClick,
            ) {
                Text(option.text)
            }
        }
    }
}

@Composable
private fun StoryChosenChoice(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.ChosenChoice,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = state.text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
