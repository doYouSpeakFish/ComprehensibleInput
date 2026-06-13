package input.comprehensible.ui.textadventure

import input.comprehensible.data.textadventure.AdventureStatus
import input.comprehensible.ui.components.LanguageLevel
import input.comprehensible.ui.components.LanguageSelection

/**
 * The UI state for the text adventures list screen.
 *
 * [languageLevel] is the CEFR difficulty new adventures are written at, shown in the top bar's
 * level picker; null hides the picker (e.g. before settings have loaded).
 *
 * [undoableDeletedAdventureId] is the id of the adventure the user has just deleted: while set, the
 * screen shows a snackbar offering to undo the deletion.
 */
data class TextAdventuresListUiState(
    val isSignedIn: Boolean,
    val isLoading: Boolean,
    val adventures: List<AdventureItem>,
    val showError: Boolean,
    val showBusyMessage: Boolean,
    val learningLanguage: LanguageSelection?,
    val translationLanguage: LanguageSelection?,
    val languagesAvailable: List<LanguageSelection>,
    val languageLevel: LanguageLevel? = null,
    val undoableDeletedAdventureId: String? = null,
) {
    /**
     * A single text adventure shown in the list. The [title] is in the learning language and
     * [translatedTitle] is the same title in the player's translation language; the list shows both.
     * [learningLanguage] and [translationLanguage] are language codes (e.g. "de"), resolved to
     * display names in the UI. [status] reflects how far the player has progressed.
     */
    data class AdventureItem(
        val id: String,
        val title: String,
        val translatedTitle: String,
        val learningLanguage: String,
        val translationLanguage: String,
        val status: AdventureStatus,
        val imageUrl: String? = null,
    )

    companion object {
        val INITIAL = TextAdventuresListUiState(
            isSignedIn = false,
            isLoading = false,
            adventures = emptyList(),
            showError = false,
            showBusyMessage = false,
            learningLanguage = null,
            translationLanguage = null,
            languagesAvailable = emptyList(),
        )
    }
}
