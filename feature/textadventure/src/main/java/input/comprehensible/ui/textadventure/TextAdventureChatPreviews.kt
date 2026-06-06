package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import input.comprehensible.ui.textadventure.TextAdventureChatUiState.SelectedSentence
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

private val previewAiMessage = ChatMessage(
    id = "1",
    sender = ChatMessageSender.AI,
    isEnding = false,
    paragraphs = listOf(
        ChatMessage.Paragraph(
            sentences = listOf("You arrive at a quiet harbor.", "Boats sway in the mist."),
            translatedSentences = listOf(
                "Llegas a un puerto tranquilo.",
                "Los barcos se mecen en la niebla.",
            ),
        ),
    ),
)

private val previewUserMessage = ChatMessage(
    id = "2",
    sender = ChatMessageSender.USER,
    isEnding = false,
    paragraphs = listOf(
        ChatMessage.Paragraph(
            sentences = listOf("I walk toward the dock."),
            translatedSentences = listOf("Camino hacia el muelle."),
        ),
    ),
)

@DefaultPreview
@Composable
fun PreviewTextAdventureChatLoading() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(isGenerating = true),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatLoaded() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(messages = listOf(previewAiMessage)),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatTranslated() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(previewAiMessage),
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
            TextAdventureChatUiState.INITIAL.copy(showError = true),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatBusy() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(previewAiMessage),
                showBusyMessage = true,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatUserMessage() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(previewAiMessage, previewUserMessage),
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatSending() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(previewAiMessage),
                optimisticUserMessage = previewUserMessage,
                isGenerating = true,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatMessageError() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(previewAiMessage),
                optimisticUserMessage = previewUserMessage,
                showMessageError = true,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventureChatEnded() {
    ComprehensibleInputTheme {
        PreviewChatScreen(
            TextAdventureChatUiState.INITIAL.copy(
                messages = listOf(
                    previewAiMessage,
                    previewUserMessage,
                    previewAiMessage.copy(
                        id = "3",
                        isEnding = true,
                        paragraphs = listOf(
                            ChatMessage.Paragraph(
                                sentences = listOf("The fog lifts and the journey ends."),
                                translatedSentences = listOf("La niebla se disipa y el viaje termina."),
                            ),
                        ),
                    ),
                ),
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
        onSendMessage = {},
        modifier = Modifier.fillMaxSize(),
    )
}
