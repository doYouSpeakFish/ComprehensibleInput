package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import input.comprehensible.data.textadventure.AdventureStatus
import input.comprehensible.ui.components.LanguageSelection
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
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
                ),
                showError = false,
                showBusyMessage = false,
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
                learningLanguage = LanguageSelection.GERMAN,
                translationLanguage = LanguageSelection.ENGLISH,
                languagesAvailable = LanguageSelection.entries,
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
            onNavigateUp = {},
            onSettingsClick = {},
            onDeleteAdventure = {},
            onLearningLanguageSelected = {},
            onTranslationLanguageSelected = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
