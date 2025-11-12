package input.comprehensible.ui.components.storycontent.part

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
    selectedSentenceIndex: Int? = null,
    selectedChoiceIndex: Int? = null,
    isSelectionTranslated: Boolean = false,
    isChosenChoiceTranslated: Boolean = false,
    onSentenceSelected: (Int) -> Unit = {},
    onChoiceTextSelected: (Int) -> Unit = {},
    onChosenChoiceSelected: () -> Unit = {},
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
            is StoryContentPartUiState.Choices -> StoryChoices(
                state = state,
                selectedOptionIndex = selectedChoiceIndex,
                isSelectionTranslated = isSelectionTranslated,
                onOptionTextSelected = onChoiceTextSelected,
            )
            is StoryContentPartUiState.ChosenChoice -> StoryChosenChoice(
                state = state,
                isTranslated = isChosenChoiceTranslated,
                onChosenChoiceSelected = onChosenChoiceSelected,
            )
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
    selectedOptionIndex: Int?,
    isSelectionTranslated: Boolean,
    onOptionTextSelected: (Int) -> Unit,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.options.forEachIndexed { index, option ->
            Choice(
                modifier = Modifier.fillMaxWidth(),
                isSelected = selectedOptionIndex == index,
                isSelectionTranslated = isSelectionTranslated,
                onOptionTextSelected = { onOptionTextSelected(index) },
                option = option,
            )
        }
    }
}

@Composable
private fun Choice(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isSelectionTranslated: Boolean,
    onOptionTextSelected: () -> Unit,
    option: StoryContentPartUiState.Choices.Option,
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (isSelected) {
        colorScheme.onBackground
    } else {
        colorScheme.background
    }
    val contentColor = if (isSelected) {
        colorScheme.background
    } else {
        colorScheme.onBackground
    }
    val borderColor = if (isSelected) {
        colorScheme.background
    } else {
        colorScheme.onBackground
    }
    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                    .clickable { onOptionTextSelected() }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                text = if (isSelected && isSelectionTranslated) {
                    option.translatedText
                } else {
                    option.text
                },
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
            )
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics { contentDescription = option.text }
                    .testTag("story_choice_button_${option.id}"),
                onClick = option.onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.onBackground,
                    contentColor = colorScheme.background,
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(text = stringResource(id = R.string.story_reader_choice_select_button))
            }
        }
    }
}

@Composable
private fun StoryChosenChoice(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.ChosenChoice,
    isTranslated: Boolean,
    onChosenChoiceSelected: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (isTranslated) {
        colorScheme.onBackground
    } else {
        colorScheme.background
    }
    val contentColor = if (isTranslated) {
        colorScheme.background
    } else {
        colorScheme.onBackground
    }
    val borderColor = if (isTranslated) {
        colorScheme.background
    } else {
        colorScheme.onBackground
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        onClick = onChosenChoiceSelected,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
            text = if (isTranslated) state.translatedText else state.text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
