package input.comprehensible.ui.storylist

import android.graphics.Bitmap
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.topbar.TopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.ui.theme.backgroundDark
import input.comprehensible.util.DefaultPreview

@Composable
internal fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (id: String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: StoryListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryListUiState.INITIAL)
    StoryListScreen(
        modifier = modifier,
        onStorySelected = { onStorySelected(it.id) },
        onSettingsClick = onSettingsClick,
        onLearningLanguageSelected = viewModel::onLearningLanguageSelected,
        onTranslationLanguageSelected = viewModel::onTranslationLanguageSelected,
        state = state,
    )
}

@Composable
private fun StoryListScreen(
    modifier: Modifier = Modifier,
    onStorySelected: (StoryListUiState.StoryListItem) -> Unit,
    onSettingsClick: () -> Unit,
    onLearningLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    state: StoryListUiState,
) {
    val storiesWithIndex = remember(state.stories) { state.stories.withIndex().toList() }
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
                items = storiesWithIndex,
                key = { it.value.id }
            ) {
                val column = it.index % columns
                val shouldAddTopPadding = column % 2 == 0
                val shouldAddBottomPadding =
                    (column % 2 == 0) && it.index != state.stories.lastIndex
                StoryListItem(
                    modifier = Modifier.padding(
                        top = if (shouldAddTopPadding) 0.dp else 140.dp,
                        bottom = if (shouldAddBottomPadding) 140.dp else 0.dp,
                    ),
                    onClick = { onStorySelected(it.value) },
                    story = it.value
                )
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
    val columns = if (windowSizeClass.windowWidthSizeClass == COMPACT) 2 else 4
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
    story: StoryListUiState.StoryListItem,
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
                image = story.featuredImage.asImageBitmap(),
                contentDescription = story.featuredImageContentDescription
            )
            Box {
                // Add empty title with max lines to force every card to have the same height
                StoryTitle(title = "\n", subtitle = "\n")
                StoryTitle(title = story.title, subtitle = story.subtitle)
            }
        }
    }
}

@Composable
private fun FeatureImage(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    contentDescription: String,
) {
    Box(modifier, propagateMinConstraints = true) {
        Image(
            modifier = Modifier
                .aspectRatio(1f)
                .border(1.dp, color = backgroundDark, shape = CircleShape)
                .clip(CircleShape),
            bitmap = image,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillHeight,
        )
    }
}

@Composable
private fun StoryTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = modifier,
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

@DefaultPreview
@Composable
fun StoryListScreenPreview() {
    ComprehensibleInputTheme {
        StoryListScreen(
            onStorySelected = {},
            onSettingsClick = {},
            onLearningLanguageSelected = {},
            onTranslationLanguageSelected = {},
            state = StoryListUiState(
                stories = List(100) {
                    StoryListUiState.StoryListItem(
                        id = "$it",
                        title = "Title $it",
                        subtitle = "Translated Title $it",
                        featuredImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
                        featuredImageContentDescription = "Content description $it",
                    )
                },
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
            ),
        )
    }
}
