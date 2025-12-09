package input.comprehensible.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

@Composable
fun TranslatableText(
    modifier: Modifier,
    text: String,
    translation: String,
    onTextSelected: () -> Unit,
    isTextSelected: Boolean,
    isTextTranslated: Boolean,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    defaultStyle: SpanStyle = rememberSpanStyle(
        background = Color.Transparent,
        color = LocalContentColor.current,
    ),
    highlightedStyle: SpanStyle = rememberSpanStyle(
        background = MaterialTheme.colorScheme.onBackground,
        color = MaterialTheme.colorScheme.background,
    )
) {
    TranslatableText(
        modifier = modifier,
        sentences = remember(text) { listOf(text) },
        translatedSentences = remember(translation) { listOf(translation) },
        onSentenceSelected = { onTextSelected() },
        selectedSentenceIndex = 0.takeIf { isTextSelected },
        isSelectionTranslated = isTextTranslated,
        textStyle = textStyle,
        defaultSpanStyle = defaultStyle,
        highlightedSpanStyle = highlightedStyle,
    )
}

@Composable
fun TranslatableText(
    modifier: Modifier = Modifier,
    sentences: List<String>,
    translatedSentences: List<String>,
    onSentenceSelected: (Int) -> Unit,
    selectedSentenceIndex: Int?,
    isSelectionTranslated: Boolean,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    defaultSpanStyle: SpanStyle = rememberSpanStyle(
        background = Color.Transparent,
        color = LocalContentColor.current,
    ),
    highlightedSpanStyle: SpanStyle = rememberSpanStyle(
        background = MaterialTheme.colorScheme.onBackground,
        color = MaterialTheme.colorScheme.background,
    )
) {
    val textContent = rememberTranslatableTextContent(
        sentences = sentences,
        translatedSentences = translatedSentences,
        onSentenceSelected = onSentenceSelected,
        selectedSentenceIndex = selectedSentenceIndex,
        isSelectionTranslated = isSelectionTranslated,
        highlightedSpanStyle = highlightedSpanStyle,
        defaultSpanStyle = defaultSpanStyle,
    )
    BasicText(
        modifier = modifier,
        text = textContent,
        style = textStyle,
    )
}

@Composable
private fun rememberSpanStyle(
    background: Color,
    color: Color,
) = remember(background, color) {
    SpanStyle(
        background = background,
        color = color,
    )
}

@Composable
private fun rememberTranslatableTextContent(
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
