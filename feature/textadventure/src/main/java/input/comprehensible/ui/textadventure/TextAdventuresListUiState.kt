package input.comprehensible.ui.textadventure

import input.comprehensible.data.textadventure.AdventureStatus

/**
 * The UI state for the text adventures list screen.
 *
 * [adventurePendingDeletion] is the adventure the user has swiped to delete but not yet confirmed:
 * while set, the screen shows a confirmation dialog for it and holds its row open.
 */
data class TextAdventuresListUiState(
    val isSignedIn: Boolean,
    val isLoading: Boolean,
    val adventures: List<AdventureItem>,
    val showError: Boolean,
    val showBusyMessage: Boolean,
    val adventurePendingDeletion: AdventureItem? = null,
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
        )
    }
}
