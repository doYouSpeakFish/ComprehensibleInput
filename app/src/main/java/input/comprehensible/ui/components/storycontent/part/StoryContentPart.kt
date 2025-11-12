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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import input.comprehensible.R

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(
    modifier: Modifier = Modifier,
    selectedSentenceIndex: Int?,
    isSelectionTranslated: Boolean,
    onSentenceSelected: (Int) -> Unit,
    state: StoryContentPartUiState,
) {
    Box(modifier) {
        when (state) {
            is StoryContentPartUiState.Paragraph -> Paragraph(
                selectedSentenceIndex = selectedSentenceIndex,
                isSelectionTranslated = isSelectionTranslated,
                onSentenceSelected = onSentenceSelected,
                state = state,
            )
            is StoryContentPartUiState.Image -> StoryImage(state = state)
            is StoryContentPartUiState.Choices -> StoryChoices(state = state)
        }
    }
}

@Composable
private fun Paragraph(
    modifier: Modifier = Modifier,
    selectedSentenceIndex: Int?,
    isSelectionTranslated: Boolean,
    onSentenceSelected: (Int) -> Unit,
    state: StoryContentPartUiState.Paragraph,
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
        val textContent = rememberParagraphText(
            sentences = state.sentences,
            translatedSentences = state.translatedSentences,
            onSentenceSelected = onSentenceSelected,
            selectedSentenceIndex = selectedSentenceIndex,
            isSelectionTranslated = isSelectionTranslated,
            highlightedSpanStyle = highlightedSpanStyle,
            defaultSpanStyle = defaultSpanStyle,
        )
        BasicText(
            modifier = Modifier.padding(bottom = 16.dp),
            text = textContent,
            style = textStyle,
        )
    }
}

@Composable
private fun rememberParagraphText(
    sentences: List<String>,
    translatedSentences: List<String>,
    onSentenceSelected: (Int) -> Unit,
    selectedSentenceIndex: Int?,
    isSelectionTranslated: Boolean,
    highlightedSpanStyle: SpanStyle,
    defaultSpanStyle: SpanStyle,
) = remember(
    sentences,
    translatedSentences,
    highlightedSpanStyle,
    defaultSpanStyle,
    selectedSentenceIndex,
    isSelectionTranslated,
) {
    buildAnnotatedString {
        sentences.forEachIndexed { index, sentence ->
            val isSelected = selectedSentenceIndex == index
            val textToDisplay = if (isSelected && isSelectionTranslated) {
                translatedSentences.getOrNull(index) ?: sentence
            } else {
                sentence
            }
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = "sentence-$index",
                    linkInteractionListener = LinkInteractionListener {
                        onSentenceSelected(index)
                    }
                )
            ) {
                withStyle(
                    if (isSelected) highlightedSpanStyle else defaultSpanStyle
                ) {
                    append(textToDisplay)
                }
            }
            if (index != sentences.lastIndex) {
                withStyle(defaultSpanStyle) {
                    append(" ")
                }
            }
        }
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
                enabled = !option.isSelected,
                colors = if (option.isSelected) {
                    ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (option.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                    if (option.isSelected) {
                        Text(
                            text = stringResource(id = R.string.story_reader_selected_choice),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
