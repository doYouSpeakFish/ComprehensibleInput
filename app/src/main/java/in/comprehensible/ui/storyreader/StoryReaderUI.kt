package `in`.comprehensible.ui.storyreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.comprehensible.ui.theme.ComprehensibleInputTheme
import `in`.comprehensible.util.DefaultPreview

/**
 * A screen for reading a story.
 */
@Composable
fun StoryReader(
    modifier: Modifier = Modifier,
    viewModel: StoryReaderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoryReader(modifier, state)
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    state: StoryReaderUiState
) {
    Box(modifier) {
        when (state) {
            is StoryReaderUiState.Loading -> {
                CircularProgressIndicator()
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
                    Text(
                        text = state.content,
                    )
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
                content = "Content"
            )
        )
    }
}
