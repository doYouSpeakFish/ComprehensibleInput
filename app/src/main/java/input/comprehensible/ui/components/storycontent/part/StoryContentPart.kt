package input.comprehensible.ui.components.storycontent.part

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import input.comprehensible.ui.theme.paragraphSpacing

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(modifier: Modifier = Modifier, state: StoryContentPartUiState) {
    Box(modifier) {
        when (state) {
            is StoryContentPartUiState.Paragraph -> Paragraph(text = state.paragraph)
        }
    }
}

@Composable
private fun Paragraph(
    modifier: Modifier = Modifier,
    text: String
) {
    val paragraphSpacing = with(LocalDensity.current) {
        MaterialTheme.typography.paragraphSpacing.toDp()
    }
    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = paragraphSpacing),
            text = text
        )
    }
}
