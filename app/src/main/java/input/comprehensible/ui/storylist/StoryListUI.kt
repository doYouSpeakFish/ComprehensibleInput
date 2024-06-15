package input.comprehensible.ui.storylist

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun StoryListScreen(
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
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = state.stories,
                key = { it.id }
            ) {
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
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 32.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    ) {
        Image(
            bitmap = story.featuredImage.asImageBitmap(),
            contentDescription = story.featuredImageContentDescription,
        )
        Text(
            modifier = Modifier.padding(8.dp),
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
                        featuredImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
                        featuredImageContentDescription = "Content description $it"
                    )
                }
            )
        )
    }
}
