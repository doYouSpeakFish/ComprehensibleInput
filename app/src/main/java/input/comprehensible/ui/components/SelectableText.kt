package input.comprehensible.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A text component that allows selecting text ranges.
 */
@Composable
fun SelectableText(
    modifier: Modifier = Modifier,
    text: String,
    onTextClicked: (index: Int) -> Unit,
    selectedText: TextRange?,
    selectedTextColor: Color = MaterialTheme.colorScheme.background,
    selectedTextBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val span = rememberHighlightSpanStyle(
        highlightedTextColor = selectedTextColor,
        highlightedBackgroundColor = selectedTextBackgroundColor
    )
    val annotatedText = rememberHighlightedText(
        text = text,
        selectedText = selectedText,
        span = span,
        defaultStyle = style,
    )
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    BasicText(
        modifier = modifier.pointerInput(text, onTextClicked) {
            detectTapGestures { offset ->
                textLayoutResult?.let { layoutResult ->
                    val characterIndex = layoutResult.getOffsetForPosition(offset)
                    onTextClicked(characterIndex)
                }
            }
        },
        text = annotatedText,
        style = style,
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        }
    )
}

@Composable
private fun rememberHighlightSpanStyle(
    highlightedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    highlightedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) = remember(highlightedTextColor, highlightedBackgroundColor) {
    SpanStyle(
        color = highlightedTextColor,
        background = highlightedBackgroundColor,
    )
}

@Composable
private fun rememberHighlightedText(
    text: String,
    selectedText: TextRange?,
    span: SpanStyle,
    defaultStyle: TextStyle,
): AnnotatedString {
    val defaultSpanStyle = defaultStyle.toSpanStyle().copy(
        background = Color.Transparent,
        color = LocalContentColor.current,
    )
    return remember(text, selectedText) {
        buildAnnotatedString {
            val selectionStartIndex = selectedText?.start?.coerceAtLeast(0)
            val selectionEndIndex = selectedText?.end?.coerceAtMost(text.length)
            withStyle(defaultSpanStyle) {
                append(
                    text.substring(
                        startIndex = 0,
                        endIndex = selectionStartIndex ?: text.lastIndex
                    )
                )
            }
            withStyle(span) {
                append(
                    text.substring(
                        startIndex = selectionStartIndex ?: text.lastIndex,
                        endIndex = selectionEndIndex ?: text.lastIndex,
                    )
                )
            }
            withStyle(defaultSpanStyle) {
                append(
                    text.substring(
                        startIndex = selectionEndIndex ?: text.lastIndex
                    )
                )
            }
        }
    }
}

@DefaultPreview
@Composable
private fun TextHighlightPreview() {
    ComprehensibleInputTheme {
        val sentences = List(10) { "This is a sentence!" }
        val text = sentences.joinToString(" ")
        val sentenceRangeIndex = sentences
            .runningFold(0) { acc, sentence -> acc + sentence.length + 1 }
            .zipWithNext { a, b -> TextRange(a, b) }
        var selectedText: TextRange? by remember { mutableStateOf(TextRange(0, 19)) }
        Surface {
            SelectableText(
                text = text,
                onTextClicked = { characterIndex ->
                    val sentenceIndex = sentenceRangeIndex.binarySearch {
                        when {
                            characterIndex < it.start -> 1
                            characterIndex > it.end -> -1
                            else -> 0
                        }
                    }
                    selectedText = sentenceRangeIndex[sentenceIndex].takeIf { selectedText != it }
                },
                selectedText = selectedText,
            )
        }
    }
}
