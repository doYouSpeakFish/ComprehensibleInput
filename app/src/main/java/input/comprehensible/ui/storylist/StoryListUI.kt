package input.comprehensible.ui.storylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A screen for displaying a list of stories.
 */
@Composable
fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (id: String) -> Unit,
    viewModel: StoryListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryListUiState.INITIAL)
    StoryListScreen(
        modifier = modifier,
        onStorySelected = { onStorySelected(it.id) },
        state = state
    )
}

@Composable
private fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (StoryListUiState.StoryListItem) -> Unit,
    state: StoryListUiState
) {
    Box(modifier) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.stories) {
                StoryListItem(
                    onClick = { onStorySelected(it) },
                    story = it
                )
            }
        }
    }
}

@Composable
private fun StoryListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    story: StoryListUiState.StoryListItem,
) {
    Card(modifier = modifier, onClick = onClick) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = story.title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@DefaultPreview
@Composable
private fun StoryListScreenPreview() {
    ComprehensibleInputTheme {
        StoryListScreen(
            onStorySelected = {},
            state = StoryListUiState(
                stories = List(100) {
                    StoryListUiState.StoryListItem(
                        id = "$it",
                        title = "Title $it",
                    )
                }
            )
        )
    }
}
