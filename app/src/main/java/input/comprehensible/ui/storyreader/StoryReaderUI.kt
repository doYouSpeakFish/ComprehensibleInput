package input.comprehensible.ui.storyreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.ui.components.SelectableText
import input.comprehensible.ui.components.storycontent.part.StoryContentPart
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A screen for reading a story.
 */
@Composable
fun StoryReader(
    modifier: Modifier = Modifier,
    viewModel: StoryReaderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryReaderUiState.Loading)
    StoryReader(
        modifier = modifier,
        onTitleClicked = viewModel::onTitleSelected,
        state = state,
    )
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    state: StoryReaderUiState,
) {
    Scaffold(modifier) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (state) {
                is StoryReaderUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is StoryReaderUiState.Loaded -> StoryContent(
                    state = state,
                    onTitleClicked = onTitleClicked,
                )
            }
        }
    }
}

@Composable
private fun StoryContent(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    state: StoryReaderUiState.Loaded,
) {
    Box(modifier) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            state = rememberLazyListState(),
        ) {
            item {
                SelectableText(
                    text = state.title,
                    onTextClicked = { onTitleClicked() },
                    selectedText = TextRange(
                        start = 0,
                        end = state.title.length
                    ).takeIf { state.isTitleHighlighted },
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(modifier = Modifier.padding(8.dp))
            }
            items(state.content) {
                StoryContentPart(
                    state = it,
                )
            }
        }
    }
}

@DefaultPreview
@Composable
fun StoryReaderPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                isTitleHighlighted = false,
                content = listOf(
                    StoryContentPartUiState.Paragraph(
                        paragraph = "Content",
                        onClick = {},
                        selectedTextRange = null
                    )
                ),
            )
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderTranslationPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                isTitleHighlighted = true,
                content = listOf(
                    StoryContentPartUiState.Paragraph(
                        paragraph = "Content",
                        onClick = {},
                        selectedTextRange = null
                    )
                ),
            )
        )
    }
}
