package input.comprehensible.ui.textadventure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.R
import input.comprehensible.ui.components.storycontent.part.StoryContentPart
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
fun TextAdventureScreen(
    modifier: Modifier = Modifier,
    adventureId: String,
    onNavigateUp: () -> Unit,
    viewModel: TextAdventureViewModel = viewModel { TextAdventureViewModel(adventureId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TextAdventureUiState.Loading)
    TextAdventureScreen(
        modifier = modifier,
        onNavigateUp = onNavigateUp,
        onInputChanged = viewModel::onInputChanged,
        onSendMessage = viewModel::onSendMessage,
        onSentenceSelected = viewModel::onSentenceSelected,
        state = state,
    )
}

@Composable
private fun TextAdventureScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSentenceSelected: (messageId: String, sentenceIndex: Int) -> Unit,
    state: TextAdventureUiState,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.text_adventure_title),
                onNavigateUp = onNavigateUp,
            )
        },
        bottomBar = {
            if (state is TextAdventureUiState.Loaded && state.isInputEnabled) {
                TextAdventureInput(
                    inputText = state.inputText,
                    onInputChanged = onInputChanged,
                    onSendMessage = onSendMessage,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (state) {
                TextAdventureUiState.Error -> Unit
                TextAdventureUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("text_adventure_loading"),
                )
                is TextAdventureUiState.Loaded -> TextAdventureMessages(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onSentenceSelected = onSentenceSelected,
                )
            }
        }
    }
}

@Composable
private fun TextAdventureMessages(
    modifier: Modifier = Modifier,
    state: TextAdventureUiState.Loaded,
    onSentenceSelected: (messageId: String, sentenceIndex: Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("text_adventure_messages"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        items(state.messages, key = { it.id }) { message ->
            when (message) {
                is TextAdventureMessageUiState.Ai -> AiMessage(
                    message = message,
                    selectedText = state.selectedText,
                    onSentenceSelected = onSentenceSelected,
                )
                is TextAdventureMessageUiState.User -> UserMessage(message = message)
            }
        }
    }
}

@Composable
private fun AiMessage(
    message: TextAdventureMessageUiState.Ai,
    selectedText: TextAdventureUiState.SelectedText?,
    onSentenceSelected: (messageId: String, sentenceIndex: Int) -> Unit,
) {
    val selectedSentenceIndex =
        selectedText?.sentenceIndex?.takeIf { selectedText.messageId == message.id }
    val isSelectionTranslated =
        selectedText?.isTranslated?.takeIf { selectedText.messageId == message.id } ?: false
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StoryContentPart(
                state = message.content,
                selectedSentenceIndex = selectedSentenceIndex,
                isSelectionTranslated = isSelectionTranslated,
                onSentenceSelected = { onSentenceSelected(message.id, it) },
            )
            if (message.isEnding) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(R.string.text_adventure_ending_label),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun UserMessage(message: TextAdventureMessageUiState.User) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = message.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun TextAdventureInput(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("text_adventure_input"),
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = {
                    Text(text = stringResource(R.string.text_adventure_input_placeholder))
                },
                maxLines = 4,
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(48.dp)
                    .testTag("text_adventure_send"),
                onClick = onSendMessage,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(R.string.text_adventure_send_button),
                )
            }
        }
    }
}

@DefaultPreview
@Composable
fun TextAdventurePreview() {
    ComprehensibleInputTheme {
        TextAdventureScreen(
            onNavigateUp = {},
            onInputChanged = {},
            onSendMessage = {},
            onSentenceSelected = { _, _ -> },
            state = TextAdventureUiState.Loaded(
                title = "Mountain Path",
                messages = listOf(
                    TextAdventureMessageUiState.Ai(
                        id = "ai-1",
                        content = StoryContentPartUiState.Paragraph(
                            sentences = listOf("You find a fork in the trail."),
                            translatedSentences = listOf("Encuentras una bifurcación en el camino."),
                        ),
                        isEnding = false,
                    ),
                    TextAdventureMessageUiState.User(
                        id = "user-1",
                        text = "I take the left path.",
                    ),
                ),
                selectedText = null,
                inputText = "",
                isInputEnabled = true,
            ),
        )
    }
}
