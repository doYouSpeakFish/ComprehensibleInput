package input.comprehensible.ui.textadventure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.R
import input.comprehensible.data.textadventure.model.TextAdventureRole
import input.comprehensible.ui.components.TranslatableText
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
fun TextAdventureScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    viewModel: TextAdventureViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TextAdventureUiState.Loading)
    TextAdventureScreen(
        modifier = modifier,
        onNavigateUp = onNavigateUp,
        onSentenceSelected = viewModel::onSentenceSelected,
        onUserResponseSubmitted = viewModel::onUserResponseSubmitted,
        state = state,
    )
}

@Composable
private fun TextAdventureScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    onSentenceSelected: (messageId: String, sentenceIndex: Int) -> Unit,
    onUserResponseSubmitted: (String) -> Unit,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (state) {
                TextAdventureUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 32.dp)
                            .testTag("text_adventure_loading"),
                    )
                }

                is TextAdventureUiState.Loaded -> {
                    TextAdventureMessages(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        messages = state.messages,
                        selectedSentence = state.selectedSentence,
                        onSentenceSelected = onSentenceSelected,
                    )
                    TextAdventureResponseBar(
                        isInputEnabled = state.isInputEnabled,
                        onUserResponseSubmitted = onUserResponseSubmitted,
                    )
                }
            }
        }
    }
}

@Composable
private fun TextAdventureMessages(
    modifier: Modifier = Modifier,
    messages: List<TextAdventureMessageUiState>,
    selectedSentence: TextAdventureUiState.SelectedSentence?,
    onSentenceSelected: (messageId: String, sentenceIndex: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .testTag("text_adventure_messages"),
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(messages, key = { it.id }) { message ->
            val isFromAi = message.role == TextAdventureRole.AI
            val bubbleColor = if (isFromAi) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            }
            val textColor = if (isFromAi) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isFromAi) Arrangement.Start else Arrangement.End,
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = bubbleColor,
                            shape = RoundedCornerShape(16.dp),
                        )
                        .padding(16.dp),
                ) {
                    if (message.role == TextAdventureRole.AI) {
                        TranslatableText(
                            modifier = Modifier,
                            sentences = message.sentences,
                            translatedSentences = message.translatedSentences,
                            selectedSentenceIndex = selectedSentence
                                ?.takeIf { it.messageId == message.id }
                                ?.sentenceIndex,
                            isSelectionTranslated = selectedSentence
                                ?.takeIf { it.messageId == message.id }
                                ?.isTranslated == true,
                            onSentenceSelected = { sentenceIndex ->
                                onSentenceSelected(message.id, sentenceIndex)
                            },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            defaultSpanStyle = SpanStyle(color = textColor),
                            highlightedSpanStyle = SpanStyle(
                                color = bubbleColor,
                                background = textColor,
                            ),
                        )
                    } else {
                        Text(
                            text = message.displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextAdventureResponseBar(
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean,
    onUserResponseSubmitted: (String) -> Unit,
) {
    if (!isInputEnabled) return

    var response by rememberSaveable { mutableStateOf("") }
    val canSend = response.isNotBlank()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .testTag("text_adventure_input"),
            value = response,
            onValueChange = { response = it },
            placeholder = { Text(text = stringResource(R.string.text_adventure_input_hint)) },
        )
        Button(
            modifier = Modifier.testTag("text_adventure_send"),
            onClick = {
                onUserResponseSubmitted(response)
                response = ""
            },
            enabled = canSend,
        ) {
            Text(text = stringResource(R.string.text_adventure_send))
        }
    }
}

@DefaultPreview
@Composable
fun TextAdventurePreview() {
    ComprehensibleInputTheme {
        TextAdventureScreen(
            onNavigateUp = {},
            onSentenceSelected = { _, _ -> },
            onUserResponseSubmitted = {},
            state = TextAdventureUiState.Loaded(
                messages = listOf(
                    TextAdventureMessageUiState(
                        id = "1",
                        role = TextAdventureRole.AI,
                        sentences = listOf("You see a path ahead."),
                        translatedSentences = listOf("Ves un camino adelante."),
                        isEnding = false,
                    ),
                    TextAdventureMessageUiState(
                        id = "2",
                        role = TextAdventureRole.USER,
                        sentences = listOf("I walk forward."),
                        translatedSentences = emptyList(),
                        isEnding = false,
                    ),
                ),
                selectedSentence = null,
                isInputEnabled = true,
            ),
        )
    }
}
