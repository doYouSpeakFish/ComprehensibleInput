package input.comprehensible.ui.storyreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.LanguageSelector
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
        onTranslationsEnabledChanged = viewModel::onTranslationsEnabledChanged,
        state = state,
    )
}

@Composable
private fun StoryReader(
    modifier: Modifier = Modifier,
    onTranslationsEnabledChanged: (Boolean) -> Unit,
    state: StoryReaderUiState,
) {
    Scaffold(modifier) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (state) {
                is StoryReaderUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is StoryReaderUiState.Loaded -> StoryContent(
                    onTranslationsEnabledChanged = onTranslationsEnabledChanged,
                    state = state,
                )
            }
        }
    }
}

@Composable
private fun StoryContent(
    modifier: Modifier = Modifier,
    onTranslationsEnabledChanged: (Boolean) -> Unit,
    state: StoryReaderUiState.Loaded,
) {
    Column(modifier) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            state = rememberLazyListState(),
        ) {
            item {
                Text(
                    text = state.title,
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
        LanguageSelector(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            learningLanguage = LanguageSelection.GERMAN,
            translationLanguage = LanguageSelection.ENGLISH,
            onTranslationsEnabledChanged = onTranslationsEnabledChanged,
            areTranslationsEnabled = state.areTranslationsEnabled,
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTranslationsEnabledChanged = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                content = listOf(
                    StoryContentPartUiState.Paragraph("Content")
                ),
                areTranslationsEnabled = false
            )
        )
    }
}

@DefaultPreview
@Composable
fun StoryReaderTranslationPreview() {
    ComprehensibleInputTheme {
        StoryReader(
            onTranslationsEnabledChanged = {},
            state = StoryReaderUiState.Loaded(
                title = "Title",
                content = listOf(
                    StoryContentPartUiState.Paragraph("Content")
                ),
                areTranslationsEnabled = true
            )
        )
    }
}
