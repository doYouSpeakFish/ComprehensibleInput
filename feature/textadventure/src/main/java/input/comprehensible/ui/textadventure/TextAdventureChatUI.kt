package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.data.textadventure.AdventureMessage
import input.comprehensible.data.textadventure.AdventureMessageSender
import input.comprehensible.feature.textadventure.R
import input.comprehensible.ui.components.TranslatableText
import input.comprehensible.ui.textadventure.TextAdventureChatUiState.SelectedSentence
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview
import kotlinx.coroutines.delay

internal const val PLACEHOLDER_CYCLE_MILLIS = 1_500L

@Composable
internal fun TextAdventureChatScreen(
    adventureId: String?,
    modifier: Modifier = Modifier,
    viewModel: TextAdventureChatViewModel = viewModel { TextAdventureChatViewModel(adventureId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle(TextAdventureChatUiState.INITIAL)
    TextAdventureChatScreen(
        state = state,
        onSentenceSelected = viewModel::onSentenceSelected,
        onRetry = viewModel::onRetry,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextAdventureChatScreen(
    state: TextAdventureChatUiState,
    onSentenceSelected: (String, Int, Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag("text_adventure_chat"),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.text_adventure_chat_title)) })
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = state.messages, key = { it.id }) { message ->
                MessageItem(
                    message = message,
                    selectedSentence = state.selectedSentence,
                    onSentenceSelected = onSentenceSelected,
                )
            }
            if (state.isGenerating) {
                item(key = "generating") { GeneratingPlaceholder() }
            }
            if (state.showError) {
                item(key = "error") { GenerationError(onRetry = onRetry) }
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: AdventureMessage,
    selectedSentence: SelectedSentence?,
    onSentenceSelected: (String, Int, Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        message.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
            val selected = selectedSentence?.takeIf {
                it.messageId == message.id && it.paragraphIndex == paragraphIndex
            }
            TranslatableText(
                modifier = Modifier.fillMaxWidth(),
                sentences = paragraph.sentences,
                translatedSentences = paragraph.translatedSentences,
                selectedSentenceIndex = selected?.sentenceIndex,
                isSelectionTranslated = selected?.isTranslated == true,
                onSentenceSelected = { sentenceIndex ->
                    onSentenceSelected(message.id, paragraphIndex, sentenceIndex)
                },
            )
        }
    }
}

@Composable
private fun GeneratingPlaceholder() {
    val phrases = listOf(
        stringResource(R.string.text_adventure_generating_1),
        stringResource(R.string.text_adventure_generating_2),
        stringResource(R.string.text_adventure_generating_3),
    )
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(PLACEHOLDER_CYCLE_MILLIS)
            index = (index + 1) % phrases.size
        }
    }
    Text(
        text = phrases[index],
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("generating_placeholder"),
    )
}

@Composable
private fun GenerationError(onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.text_adventure_generation_error),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("generation_error"),
        )
        Button(onClick = onRetry, modifier = Modifier.testTag("retry_button")) {
            Text(stringResource(R.string.text_adventure_retry))
        }
    }
}

private val previewMessage = AdventureMessage(
    id = "1",
    sender = AdventureMessageSender.AI,
    isEnding = false,
    paragraphs = listOf(
        AdventureMessage.Paragraph(
            sentences = listOf("You arrive at a quiet harbor.", "Boats sway in the mist."),
            translatedSentences = listOf(
                "Llegas a un puerto tranquilo.",
                "Los barcos se mecen en la niebla.",
            ),
        ),
    ),
)

@DefaultPreview
@Composable
fun PreviewTextAdventureChatLoading() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState(
                messages = emptyList(),
                isGenerating = true,
                showError = false,
                selectedSentence = null,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatLoaded() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState(
                messages = listOf(previewMessage),
                isGenerating = false,
                showError = false,
                selectedSentence = null,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatTranslated() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState(
                messages = listOf(previewMessage),
                isGenerating = false,
                showError = false,
                selectedSentence = SelectedSentence(
                    messageId = "1",
                    paragraphIndex = 0,
                    sentenceIndex = 0,
                    isTranslated = true,
                ),
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatError() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState(
                messages = emptyList(),
                isGenerating = false,
                showError = true,
                selectedSentence = null,
            ),
        )
    }
}

@Composable
private fun PreviewChatScreen(state: TextAdventureChatUiState) {
    TextAdventureChatScreen(
        state = state,
        onSentenceSelected = { _, _, _ -> },
        onRetry = {},
        modifier = Modifier.fillMaxSize(),
    )
}
