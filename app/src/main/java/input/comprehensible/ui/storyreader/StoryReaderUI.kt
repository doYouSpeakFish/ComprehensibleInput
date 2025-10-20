package input.comprehensible.ui.storyreader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.R
import input.comprehensible.ui.components.SelectableText
import input.comprehensible.ui.components.storycontent.part.StoryContentPart
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

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
        onStoryPartVisible = viewModel::onStoryLocationUpdated,
        state = state,
    )
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    onStoryPartVisible: (index: Int) -> Unit,
    state: StoryReaderUiState,
) {
    Scaffold(modifier) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state) {
                is StoryReaderUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is StoryReaderUiState.Loaded -> StoryContent(
                    state = state,
                    onTitleClicked = onTitleClicked,
                    onStoryPartVisible = onStoryPartVisible,
                )
            }
        }
    }
}

@Composable
private fun StoryContent(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    onStoryPartVisible: (index: Int) -> Unit,
    state: StoryReaderUiState.Loaded,
) {
    var timesExplainerTapped by rememberSaveable { mutableIntStateOf(0) }
    val isExplainerShownAtStart = timesExplainerTapped < 11
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (state.storyPosition == 0 ) {
            0
        } else {
            state.storyPosition + 1 // Accounts for header
        }
    )
    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .conflate()
            .distinctUntilChanged()
            .collect { currentItemIndex ->
                val currentStoryPart = (currentItemIndex - 1) // Don't count header item
                    .coerceAtLeast(0)
                onStoryPartVisible(currentStoryPart)
            }
    }
    Box(modifier) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column {
                    Title(
                        onTitleClicked = onTitleClicked,
                        title = state.title,
                        isTitleHighlighted = state.isTitleHighlighted,
                    )
                    Spacer(Modifier.height(8.dp))
                    AnimatedVisibility(isExplainerShownAtStart) {
                        TranslateExplainer(
                            modifier = Modifier.padding(vertical = 16.dp),
                            onExplainerTapped = { timesExplainerTapped++ },
                            timesExplainerTapped = timesExplainerTapped,
                        )
                    }
                }
            }
            items(state.content) {
                StoryContentPart(
                    state = it,
                )
            }
            if (!isExplainerShownAtStart) {
                item {
                    TranslateExplainer(
                        modifier = Modifier.padding(vertical = 16.dp),
                        onExplainerTapped = { timesExplainerTapped++ },
                        timesExplainerTapped = timesExplainerTapped,
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    title: String,
    isTitleHighlighted: Boolean,
) {
    Column(modifier) {
        SelectableText(
            text = title,
            onTextClicked = { onTitleClicked() },
            selectedText = TextRange(
                start = 0,
                end = title.length,
            ).takeIf { isTitleHighlighted },
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}

@Composable
private fun TranslateExplainer(
    modifier: Modifier = Modifier,
    onExplainerTapped: () -> Unit,
    timesExplainerTapped: Int,
) {
    val explainerMessages =
        stringArrayResource(id = R.array.story_reader_tap_to_translate_explainer)
    Box(modifier, propagateMinConstraints = true) {
        Text(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                )
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .clickable(
                    onClick = onExplainerTapped,
                    enabled = timesExplainerTapped < explainerMessages.lastIndex,
                )
                .animateContentSize(),
            text = explainerMessages.getOrElse(timesExplainerTapped) { explainerMessages.last() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.background,
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            onStoryPartVisible = {},
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
                storyPosition = 0,
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
            onStoryPartVisible = {},
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
                storyPosition = 0,
            )
        )
    }
}
