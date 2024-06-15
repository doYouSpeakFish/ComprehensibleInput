package input.comprehensible.ui.storyreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    StoryReader(modifier, state)
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    state: StoryReaderUiState
) {
    Scaffold(modifier) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (state) {
                is StoryReaderUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is StoryReaderUiState.Loaded -> {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        state.content.forEach {
                            StoryContentPart(
                                state = it,
                            )
                        }
                    }
                }
            }
        }
    }
}

@DefaultPreview
@Composable
fun StoryReaderPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            state = StoryReaderUiState.Loaded(
                title = "Title",
                content = listOf(
                    StoryContentPartUiState.Paragraph("Content")
                )
            )
        )
    }
}
