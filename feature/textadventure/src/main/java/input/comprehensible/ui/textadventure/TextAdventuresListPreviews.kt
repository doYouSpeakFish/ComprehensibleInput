package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import input.comprehensible.data.textadventure.AdventureStatus
import input.comprehensible.ui.textadventure.TextAdventuresListUiState.AdventureItem
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview
import kotlin.math.roundToInt

private val previewAdventures = listOf(
    AdventureItem(
        id = "1",
        title = "El bosque de cristal",
        translatedTitle = "The Glass Forest",
        learningLanguage = "es",
        translationLanguage = "en",
        status = AdventureStatus.NOT_STARTED,
        imageUrl = AdventurePreviewImages.FOREST_PATH,
    ),
    AdventureItem(
        id = "2",
        title = "La porte sous la pluie",
        translatedTitle = "The Door in the Rain",
        learningLanguage = "fr",
        translationLanguage = "en",
        status = AdventureStatus.IN_PROGRESS,
        imageUrl = AdventurePreviewImages.COASTAL_VILLAGE,
    ),
    AdventureItem(
        id = "3",
        title = "Der letzte Zug",
        translatedTitle = "The Last Train",
        learningLanguage = "de",
        translationLanguage = "en",
        status = AdventureStatus.COMPLETE,
        imageUrl = AdventurePreviewImages.MOUNTAIN_PEAK,
    ),
)

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
                adventures = previewAdventures,
                showError = false,
                showBusyMessage = false,
            ),
        )
    }
}

/**
 * The row mid swipe-to-delete, dragged past the threshold with the delete background revealed.
 * [SwipeToDismissBox][androidx.compose.material3.SwipeToDismissBox] offers no way to start a state
 * at a partial offset, so the preview lays the row's own pieces out at the reveal position itself.
 */
@DefaultPreview
@Composable
fun PreviewTextAdventuresRowSwipedToDelete() {
    ComprehensibleInputTheme {
        AdventureImagePreview {
            BoxWithConstraints {
                DeleteBackground(modifier = Modifier.matchParentSize())
                AdventureCard(
                    adventure = previewAdventures.first(),
                    onClick = {},
                    modifier = Modifier.offset {
                        IntOffset(-(constraints.maxWidth * DELETE_SWIPE_FRACTION).roundToInt(), 0)
                    },
                )
            }
        }
    }
}

/** The moment after a swipe deletes an adventure: it has left the list and the undo snackbar shows. */
@DefaultPreview
@Composable
fun PreviewTextAdventuresDeletedSnackbar() {
    ComprehensibleInputTheme {
        PreviewScreen(
            TextAdventuresListUiState(
                isSignedIn = true,
                isLoading = false,
                adventures = previewAdventures.drop(1),
                showError = false,
                showBusyMessage = false,
                undoableDeletedAdventureId = previewAdventures.first().id,
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
            onCreateAccountClick = {},
            onStartAdventure = {},
            onAdventureClick = {},
            onSettingsClick = {},
            onDeleteAdventure = {},
            onUndoDelete = {},
            onUndoDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
