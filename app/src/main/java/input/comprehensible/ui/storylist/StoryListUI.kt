package input.comprehensible.ui.storylist

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.R
import androidx.window.core.layout.WindowSizeClass
import input.comprehensible.extensions.isCompact
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.topbar.TopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.ui.theme.backgroundDark
import input.comprehensible.util.DefaultPreview

@Composable
internal fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (id: String) -> Unit,
    onTextAdventureSelected: (id: String) -> Unit,
    onSettingsClick: () -> Unit,
    onTextAdventureStarted: (id: String) -> Unit,
    viewModel: StoryListViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryListUiState.INITIAL)
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is StoryListEvent.TextAdventureStarted ->
                    onTextAdventureStarted(event.adventureId)
            }
        }
    }
    StoryListScreen(
        modifier = modifier,
        onStorySelected = { onStorySelected(it.id) },
        onTextAdventureSelected = { onTextAdventureSelected(it.id) },
        onSettingsClick = onSettingsClick,
        onLearningLanguageSelected = viewModel::onLearningLanguageSelected,
        onTranslationLanguageSelected = viewModel::onTranslationLanguageSelected,
        onStartTextAdventure = viewModel::onStartTextAdventure,
        state = state,
    )
}

@Composable
private fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (StoryListUiState.StoryListItem.Story) -> Unit,
    onTextAdventureSelected: (StoryListUiState.StoryListItem.TextAdventure) -> Unit,
    onSettingsClick: () -> Unit,
    onLearningLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    onStartTextAdventure: () -> Unit,
    state: StoryListUiState,
) {
    val itemsWithIndex = remember(state.items) { state.items.withIndex().toList() }
    StoryListScaffold(
        modifier = modifier,
        onSettingsClick = onSettingsClick,
        learningLanguage = state.learningLanguage,
        translationLanguage = state.translationLanguage,
        languagesAvailable = state.languagesAvailable,
        onLearningLanguageSelected = onLearningLanguageSelected,
        onTranslationLanguageSelected = onTranslationLanguageSelected,
    ) { paddingValues, columns ->
        LazyVerticalGrid(
            modifier = Modifier.padding(paddingValues),
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy((-120).dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            items(
                items = itemsWithIndex,
                key = {
                    when (val item = it.value) {
                        is StoryListUiState.StoryListItem.Story -> "story-${item.id}"
                        is StoryListUiState.StoryListItem.TextAdventure -> "adventure-${item.id}"
                        StoryListUiState.StoryListItem.StartTextAdventure -> "start-text-adventure"
                    }
                }
            ) {
                val column = it.index % columns
                val shouldAddTopPadding = column % 2 == 0
                val shouldAddBottomPadding =
                    (column % 2 == 0) && it.index != itemsWithIndex.lastIndex
                val itemModifier = Modifier.padding(
                    top = if (shouldAddTopPadding) 0.dp else 140.dp,
                    bottom = if (shouldAddBottomPadding) 140.dp else 0.dp,
                )
                when (val item = it.value) {
                    is StoryListUiState.StoryListItem.Story -> StoryListItem(
                        modifier = itemModifier,
                        onClick = { onStorySelected(item) },
                        story = item,
                    )
                    is StoryListUiState.StoryListItem.TextAdventure -> TextAdventureListItem(
                        modifier = itemModifier,
                        onClick = { onTextAdventureSelected(item) },
                        adventure = item,
                    )
                    StoryListUiState.StoryListItem.StartTextAdventure -> StartTextAdventureItem(
                        modifier = itemModifier,
                        onClick = onStartTextAdventure,
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryListScaffold(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    learningLanguage: LanguageSelection?,
    translationLanguage: LanguageSelection?,
    languagesAvailable: List<LanguageSelection>,
    onLearningLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    content: @Composable (paddingValues: PaddingValues, columns: Int) -> Unit
) {
    val columns = if (windowSizeClass.isCompact) 2 else 4
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier,
                leaningLanguage = learningLanguage,
                translationLanguage = translationLanguage,
                languageOptions = languagesAvailable,
                onLanguageSelected = onLearningLanguageSelected,
                onTranslationLanguageSelected = onTranslationLanguageSelected,
                onSettingsClick = onSettingsClick,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            content = {
                content(paddingValues, columns)
            }
        )
    }
}

@Composable
private fun StoryListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    story: StoryListUiState.StoryListItem.Story,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FeatureImage(
                modifier = Modifier.testTag("story-image-${story.id}"),
                image = story.featuredImage.asImageBitmap(),
            )
            StoryTitle(title = story.title, subtitle = story.subtitle)
        }
    }
}

@Composable
private fun TextAdventureListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    adventure: StoryListUiState.StoryListItem.TextAdventure,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(1.dp, color = backgroundDark, shape = CircleShape)
                    .clip(CircleShape)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            TextAdventureTitle(adventure = adventure)
        }
    }
}

@Composable
private fun TextAdventureTitle(
    modifier: Modifier = Modifier,
    adventure: StoryListUiState.StoryListItem.TextAdventure,
) {
    val subtitle = if (adventure.isComplete) {
        stringResource(R.string.text_adventure_list_complete)
    } else {
        stringResource(R.string.text_adventure_list_in_progress)
    }
    StoryTitle(
        modifier = modifier,
        title = adventure.title,
        subtitle = subtitle,
    )
}

@Composable
private fun StartTextAdventureItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.testTag("text_adventure_start_button"),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(1.dp, color = backgroundDark, shape = CircleShape)
                    .clip(CircleShape)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            StoryTitle(
                title = stringResource(R.string.text_adventure_start_button),
                subtitle = stringResource(R.string.text_adventure_list_in_progress),
            )
        }
    }
}

@Composable
private fun FeatureImage(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    contentDescription: String? = null,
) {
    Box(modifier, propagateMinConstraints = true) {
        Image(
            modifier = Modifier
                .aspectRatio(1f)
                .border(1.dp, color = backgroundDark, shape = CircleShape)
                .clip(CircleShape),
            bitmap = image,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun StoryTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
) {
    @Composable
    fun Title(
        title: String,
        subtitle: String,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
    Box(modifier = modifier) {
        // Add empty title with max line height to force every title to have the same height
        Title(title = "\n", subtitle = "\n")
        Title(title = title, subtitle = subtitle)
    }
}

@DefaultPreview
@Composable
fun StoryListScreenPreview() {
    ComprehensibleInputTheme {
        StoryListScreen(
            onStorySelected = {},
            onTextAdventureSelected = {},
            onSettingsClick = {},
            onStartTextAdventure = {},
            onLearningLanguageSelected = {},
            onTranslationLanguageSelected = {},
            state = StoryListUiState(
                items = buildList {
                    add(
                        StoryListUiState.StoryListItem.TextAdventure(
                            id = "adventure-1",
                            title = "Forest Trails",
                            isComplete = false,
                        )
                    )
                    addAll(
                        List(100) {
                            StoryListUiState.StoryListItem.Story(
                                id = "$it",
                                title = "Title $it",
                                subtitle = "Translated Title $it",
                                featuredImage = createBitmap(100, 100),
                            )
                        }
                    )
                    add(StoryListUiState.StoryListItem.StartTextAdventure)
                },
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
            ),
        )
    }
}
