package input.comprehensible.ui.storyreader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import input.comprehensible.R
import input.comprehensible.ui.components.rememberPagerState
import input.comprehensible.ui.components.storycontent.part.StoryContentPart
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.ui.theme.backgroundDark
import input.comprehensible.util.DefaultPreview
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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
        onStoryPositionUpdated = viewModel::onStoryLocationUpdated,
        onCurrentPartChanged = viewModel::onCurrentPartChanged,
        onSentenceSelected = viewModel::onSentenceSelected,
        onChoiceTextSelected = viewModel::onChoiceTextSelected,
        onErrorDismissed = onErrorDismissed,
        onPartScrolledTo = viewModel::onPartScrolledTo,
        state = state,
    )
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    onStoryPositionUpdated: (elementIndex: Int) -> Unit,
    onCurrentPartChanged: (String) -> Unit,
    onSentenceSelected: (partIndex: Int, paragraph: Int, sentence: Int) -> Unit,
    onChoiceTextSelected: (partIndex: Int, optionIndex: Int) -> Unit,
    onErrorDismissed: () -> Unit,
    onPartScrolledTo: () -> Unit,
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
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.Center)
                            .testTag("story_reader_loading")
                    )

                is StoryReaderUiState.Loaded -> StoryContent(
                    state = state,
                    onTitleClicked = onTitleClicked,
                    onStoryPositionUpdated = onStoryPositionUpdated,
                    onCurrentPartChanged = onCurrentPartChanged,
                    onSentenceSelected = onSentenceSelected,
                    onChoiceTextSelected = onChoiceTextSelected,
                    onPartScrolledTo = onPartScrolledTo,
                )

                StoryReaderUiState.Error -> Unit
            }

            if (state is StoryReaderUiState.Error) {
                StoryReaderErrorDialog(onDismissRequest = onErrorDismissed)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StoryContent(
    modifier: Modifier = Modifier,
    onTitleClicked: () -> Unit,
    onStoryPositionUpdated: (elementIndex: Int) -> Unit,
    onCurrentPartChanged: (String) -> Unit,
    onSentenceSelected: (partIndex: Int, paragraph: Int, sentence: Int) -> Unit,
    onChoiceTextSelected: (partIndex: Int, optionIndex: Int) -> Unit,
    onPartScrolledTo: () -> Unit,
    state: StoryReaderUiState.Loaded,
) {
    var timesExplainerTapped by rememberSaveable { mutableIntStateOf(0) }
    val isExplainerShownAtStart = timesExplainerTapped < 11
    val currentPartIndex = remember(state.parts, state.currentPartId) {
        state.parts.indexOfFirst { part -> part.id == state.currentPartId }.coerceAtLeast(0)
    }
    val currentParts by rememberUpdatedState(state.parts)
    val pagerState = rememberPagerState(
        initialPage = currentPartIndex,
        pageToScrollTo = state.scrollingToPage,
        pageCount = { state.parts.size },
        onNewPageSettled = { pageIndex ->
            if (pageIndex == state.scrollingToPage) onPartScrolledTo()
            currentParts.getOrNull(pageIndex)?.id?.let(onCurrentPartChanged)
        },
    )

    Box(modifier) {
        HorizontalPager(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("story_reader_pager"),
            state = pagerState,
            key = { index -> state.parts.getOrNull(index)?.id ?: index },
            pageSpacing = 16.dp,
        ) { pageIndex ->
            StoryPage(
                modifier = Modifier.fillMaxSize(),
                partIndex = pageIndex,
                part = state.parts[pageIndex],
                title = state.title,
                onTitleClicked = onTitleClicked,
                selectedText = state.selectedText,
                initialContentIndex = state.initialContentIndex,
                isExplainerShownAtStart = isExplainerShownAtStart,
                timesExplainerTapped = timesExplainerTapped,
                onExplainerTapped = { timesExplainerTapped++ },
                onSentenceSelected = onSentenceSelected,
                onChoiceTextSelected = onChoiceTextSelected,
                onStoryPositionUpdated = onStoryPositionUpdated,
                pageIndex = pageIndex,
                currentlyVisiblePageIndex = pagerState.settledPage,
            )
        }
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
private fun StoryPage(
    modifier: Modifier = Modifier,
    partIndex: Int,
    part: StoryReaderPartUiState,
    title: String,
    onTitleClicked: () -> Unit,
    selectedText: StoryReaderUiState.SelectedText?,
    initialContentIndex: Int,
    isExplainerShownAtStart: Boolean,
    timesExplainerTapped: Int,
    onExplainerTapped: () -> Unit,
    onSentenceSelected: (partIndex: Int, paragraph: Int, sentence: Int) -> Unit,
    onChoiceTextSelected: (partIndex: Int, optionIndex: Int) -> Unit,
    onStoryPositionUpdated: (Int) -> Unit,
    pageIndex: Int,
    currentlyVisiblePageIndex: Int,
) {
    val isFirstPart = partIndex == 0
    val isCurrentPart = partIndex == currentlyVisiblePageIndex
    val initialListIndex = if (isCurrentPart) initialContentIndex else 0
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialListIndex)

    LaunchedEffect(pageIndex, currentlyVisiblePageIndex) {
        when {
            pageIndex < currentlyVisiblePageIndex -> listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
            pageIndex > currentlyVisiblePageIndex -> listState.scrollToItem(0)
        }
    }

    LaunchedEffect(isCurrentPart) {
        if (isCurrentPart) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .conflate()
                .distinctUntilChanged()
                .collect(onStoryPositionUpdated)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("story_reader_page_list"),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isFirstPart) {
            item {
                Title(
                    onTitleClicked = onTitleClicked,
                    title = title,
                    isTitleHighlighted = selectedText is StoryReaderUiState.SelectedText.Title,
                )
                AnimatedVisibility(isExplainerShownAtStart) {
                    TranslateExplainer(
                        modifier = Modifier.padding(vertical = 16.dp),
                        onExplainerTapped = onExplainerTapped,
                        timesExplainerTapped = timesExplainerTapped,
                    )
                }
            }
        }
        itemsIndexed(
            items = part.content,
        ) { contentIndex, item ->
            val selectedSentence =
                selectedText as? StoryReaderUiState.SelectedText.SentenceInParagraph
            val selectedChoice =
                selectedText as? StoryReaderUiState.SelectedText.ChoiceOption
            val sentenceSelectionIndex = selectedSentence?.selectedSentenceIndex
                ?.takeIf {
                    contentIndex == selectedSentence.paragraphIndex &&
                            partIndex == selectedSentence.partIndex
                }
            val choiceSelectionIndex = selectedChoice?.optionIndex
                ?.takeIf {
                    partIndex == selectedChoice.partIndex && item is StoryContentPartUiState.Choices
                }
            val isSelectionTranslated = when {
                sentenceSelectionIndex != null -> selectedSentence.isTranslated
                choiceSelectionIndex != null -> selectedChoice.isTranslated
                else -> false
            }
            StoryContentPart(
                state = item,
                selectedSentenceIndex = sentenceSelectionIndex,
                selectedChoiceIndex = choiceSelectionIndex,
                isSelectionTranslated = isSelectionTranslated,
                onSentenceSelected = { sentenceIndex ->
                    onSentenceSelected(partIndex, contentIndex, sentenceIndex)
                },
                onChoiceTextSelected = { optionIndex ->
                    onChoiceTextSelected(partIndex, optionIndex)
                },
            )
        }

        if (!isExplainerShownAtStart) {
            item {
                TranslateExplainer(
                    modifier = Modifier.padding(vertical = 16.dp),
                    onExplainerTapped = onExplainerTapped,
                    timesExplainerTapped = timesExplainerTapped,
                )
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
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier) {
        TextButton(
            onClick = onTitleClicked,
            colors = ButtonDefaults.textButtonColors(
                containerColor = if (isTitleHighlighted) {
                    colorScheme.onBackground
                } else {
                    Color.Transparent
                },
                contentColor = if (isTitleHighlighted) {
                    colorScheme.background
                } else {
                    colorScheme.onBackground
                }
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
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
            onStoryPositionUpdated = {},
            onCurrentPartChanged = {},
            onSentenceSelected = { _, _, _ -> },
            onChoiceTextSelected = { _, _ -> },
            onPartScrolledTo = {},
            onErrorDismissed = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                parts = listOf(
                    StoryReaderPartUiState(
                        id = "part",
                        content = listOf(
                            StoryContentPartUiState.Paragraph(
                                sentences = listOf("Content"),
                                translatedSentences = listOf("Contenido"),
                            ),
                            StoryContentPartUiState.Choices(
                                options = listOf(
                                    StoryContentPartUiState.Choices.Option(
                                        id = "option-1",
                                        text = "Sie behält den Schlüssel.",
                                        translatedText = "She keeps the key.",
                                        onClick = {},
                                        isChosen = false
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                currentPartId = "part",
                initialContentIndex = 0,
                selectedText = null,
                scrollingToPage = null,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderTranslationPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            onStoryPositionUpdated = {},
            onCurrentPartChanged = {},
            onSentenceSelected = { _, _, _ -> },
            onChoiceTextSelected = { _, _ -> },
            onPartScrolledTo = {},
            onErrorDismissed = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                parts = listOf(
                    StoryReaderPartUiState(
                        id = "part",
                        content = listOf(
                            StoryContentPartUiState.Paragraph(
                                sentences = listOf("Content"),
                                translatedSentences = listOf("Contenido"),
                            ),
                            StoryContentPartUiState.Choices(
                                options = listOf(
                                    StoryContentPartUiState.Choices.Option(
                                        id = "option-1",
                                        text = "Sie behält den Schlüssel.",
                                        translatedText = "She keeps the key.",
                                        onClick = {},
                                        isChosen = false,
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                currentPartId = "part",
                initialContentIndex = 0,
                selectedText = StoryReaderUiState.SelectedText.Title(isTranslated = true),
                scrollingToPage = null,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderErrorPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTitleClicked = {},
            onStoryPositionUpdated = {},
            onCurrentPartChanged = {},
            onSentenceSelected = { _, _, _ -> },
            onChoiceTextSelected = { _, _ -> },
            onPartScrolledTo = {},
            onErrorDismissed = {},
            state = StoryReaderUiState.Error,
        )
    }
}
