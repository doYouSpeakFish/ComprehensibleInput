package input.comprehensible.ui.components.storycontent.part

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import input.comprehensible.extensions.orError
import input.comprehensible.ui.LocalNavAnimatedVisibilityScope
import input.comprehensible.ui.LocalSharedTransitionScope
import input.comprehensible.ui.components.SelectableText

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState,
) {
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun StoryImage(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Image,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        .orError { "No SharedTransitionScope provided" }
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        .orError { "No NavAnimatedVisibilityScope provided" }
    Box(modifier) {
        with(sharedTransitionScope) {
            Image(
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = state.contentDescription),
                        animatedVisibilityScope = animatedVisibilityScope,
                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
                    )
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
}
