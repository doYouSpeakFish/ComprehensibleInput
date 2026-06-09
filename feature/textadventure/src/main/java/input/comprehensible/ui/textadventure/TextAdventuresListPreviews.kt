package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import input.comprehensible.ui.textadventure.TextAdventuresListUiState.AdventureItem
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@DefaultPreview
@Composable
fun PreviewTextAdventuresSignedOut() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = false,
                isLoading = false,
                adventures = emptyList(),
                showError = false,
                showBusyMessage = false,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventuresList() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = false,
                adventures = listOf(
                    AdventureItem(
                        id = "1",
                        title = "Lantern Trail",
                        subtitle = "German",
                        imageUrl = AdventurePreviewImages.FOREST_PATH,
                    ),
                    AdventureItem(
                        id = "2",
                        title = "Forest Echoes",
                        subtitle = "Spanish",
                        imageUrl = AdventurePreviewImages.MOUNTAIN_PEAK,
                    ),
                    AdventureItem(
                        id = "3",
                        title = "Harbor Watch",
                        subtitle = "French",
                        imageUrl = AdventurePreviewImages.COASTAL_VILLAGE,
                    ),
                ),
                showError = false,
                showBusyMessage = false,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventuresEmpty() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = false,
                adventures = emptyList(),
                showError = false,
                showBusyMessage = false,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventuresLoading() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = true,
                adventures = emptyList(),
                showError = false,
                showBusyMessage = false,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventuresError() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = false,
                adventures = emptyList(),
                showError = true,
                showBusyMessage = false,
            ),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewTextAdventuresBusy() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = false,
                adventures = emptyList(),
                showError = false,
                showBusyMessage = true,
            ),
        )
    }
}

@Composable
private fun PreviewScreen(state: TextAdventuresListUiState) {
    AdventureImagePreview {
        TextAdventuresListScreen(
            state = state,
            onSignInClick = {},
            onStartAdventure = {},
            onAdventureClick = {},
            onDeleteAdventure = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
