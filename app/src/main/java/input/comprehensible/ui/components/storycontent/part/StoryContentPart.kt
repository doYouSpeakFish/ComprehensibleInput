package input.comprehensible.ui.components.storycontent.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import input.comprehensible.ui.components.SelectableText

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(modifier: Modifier = Modifier, state: StoryContentPartUiState) {
    Box(modifier) {
        when (state) {
            is StoryContentPartUiState.Paragraph -> Paragraph(state = state)
            is StoryContentPartUiState.Image -> StoryImage(state = state)
        }
    }
}

@Composable
private fun Paragraph(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Paragraph
) {
    Box(modifier) {
        SelectableText(
            modifier = Modifier.padding(bottom = 16.dp),
            text = state.paragraph,
            onTextClicked = state.onClick,
            selectedText = state.selectedTextRange,
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
                .clip(RoundedCornerShape(16.dp)),
            bitmap = state.bitmap.asImageBitmap(),
            contentDescription = state.contentDescription,
            contentScale = ContentScale.FillWidth,
        )
    }
}
