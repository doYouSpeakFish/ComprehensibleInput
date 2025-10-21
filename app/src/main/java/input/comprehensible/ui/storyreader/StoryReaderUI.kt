package input.comprehensible.ui.storyreader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.R
import input.comprehensible.ui.components.SelectableText
import input.comprehensible.ui.components.storycontent.part.StoryContentPart
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.ui.theme.backgroundDark
import input.comprehensible.util.DefaultPreview
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * A screen for reading a story.
 */
@Composable
fun StoryReader(
    modifier: Modifier = Modifier,
    viewModel: StoryReaderViewModel = hiltViewModel(),
    onErrorDismissed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryReaderUiState.Loading)
    StoryReader(
        modifier = modifier,
        onTitleClicked = viewModel::onTitleSelected,
        onStoryPartVisible = viewModel::onStoryLocationUpdated,
        state = state,
        onErrorDismissed = onErrorDismissed,
    )
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    onStoryPartVisible: (index: Int) -> Unit,
    state: StoryReaderUiState,
    onErrorDismissed: () -> Unit,
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

                StoryReaderUiState.Error -> Unit
            }

            if (state is StoryReaderUiState.Error) {
                StoryReaderErrorDialog(onDismissRequest = onErrorDismissed)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryReaderErrorDialog(onDismissRequest: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("story_reader_error_dialog"),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.Black),
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .widthIn(min = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sad_robot),
                    contentDescription = stringResource(R.string.story_reader_error_dialog_content_description),
                    modifier = Modifier
                        .size(96.dp)
                        .border(width = 1.dp, color = backgroundDark, shape = CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = stringResource(id = R.string.story_reader_error_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(id = R.string.story_reader_error_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.story_reader_error_dialog_button))
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
            ),
            onErrorDismissed = {},
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
            ),
            onErrorDismissed = {},
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderErrorPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            onStoryPartVisible = {},
            state = StoryReaderUiState.Error,
            onErrorDismissed = {},
        )
    }
}
