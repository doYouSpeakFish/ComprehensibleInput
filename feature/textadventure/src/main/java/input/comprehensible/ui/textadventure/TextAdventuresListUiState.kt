package input.comprehensible.ui.textadventure

/**
 * The UI state for the text adventures list screen.
 */
data class TextAdventuresListUiState(
    val isSignedIn: Boolean,
    val isLoading: Boolean,
    val adventures: List<AdventureItem>,
    val showError: Boolean,
    val showBusyMessage: Boolean,
) {
    /**
     * A single text adventure shown in the list.
     */
    data class AdventureItem(
        val id: String,
        val title: String,
        val subtitle: String,
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
