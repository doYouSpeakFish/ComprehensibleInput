package input.comprehensible.ui.textadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.account.sources.local.Session
import input.comprehensible.data.textadventure.AdventureSummary
import input.comprehensible.data.textadventure.TextAdventureRepository
import input.comprehensible.data.textadventure.sources.remote.isRateLimited
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Drives the text adventures list screen. The list itself is offline-first (observed from the local
 * database via [GetAdventuresUseCase]); this view model layers on the signed-in state, a refresh of
 * the v1 backend, and optimistic deletion.
 *
 * Loading and error are derived reactively from the refresh status combined with the current cache,
 * so a refresh failure only surfaces an error when there is nothing cached to show.
 */
class TextAdventuresListViewModel(
    private val accountRepository: AccountRepository = AccountRepository(),
    private val textAdventureRepository: TextAdventureRepository = TextAdventureRepository(),
    getAdventures: GetAdventuresUseCase = GetAdventuresUseCase(),
) : ViewModel() {

    private val refreshStatus = MutableStateFlow(RefreshStatus.IDLE)
    private val deleteOutcome = MutableStateFlow(DeleteOutcome.NONE)
    private val undoableDeleteId = MutableStateFlow<String?>(null)
    private var session: Session? = null

    val state: StateFlow<TextAdventuresListUiState> = combine(
        accountRepository.user,
        getAdventures(),
        refreshStatus,
        deleteOutcome,
        undoableDeleteId,
    ) { user, adventures, status, deleteResult, undoableDelete ->
        // A rate limit (HTTP 429) surfaces a "system busy" message; any other failure surfaces a
        // generic error. A failed refresh only surfaces either when there is nothing cached to show
        // instead, whereas a failed delete always surfaces (the adventure reappears).
        val nothingCached = adventures.isEmpty()
        val rateLimited = (status == RefreshStatus.RATE_LIMITED && nothingCached) ||
            deleteResult == DeleteOutcome.RATE_LIMITED
        val error = (status == RefreshStatus.ERROR && nothingCached) ||
            deleteResult == DeleteOutcome.ERROR
        TextAdventuresListUiState(
            isSignedIn = user != null,
            isLoading = status == RefreshStatus.LOADING && adventures.isEmpty(),
            adventures = adventures.map { it.toItem() },
            showError = error && !rateLimited,
            showBusyMessage = rateLimited,
            undoableDeletedAdventureId = undoableDelete,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = TextAdventuresListUiState.INITIAL,
    )

    init {
        viewModelScope.launch {
            accountRepository.session.collectLatest { current ->
                session = current
                if (current != null) refresh(current)
            }
        }
    }

    /**
     * Deletes an adventure and offers an undo. The repository removes it locally at once and
     * restores it if the backend rejects the delete, in which case the error is surfaced to the
     * user. While the deletion is undoable the UI shows an undo snackbar, resolved by [onUndoDelete]
     * or [onUndoDismissed].
     */
    fun onDeleteAdventure(adventureId: String) {
        val current = session ?: return
        viewModelScope.launch {
            deleteOutcome.value = DeleteOutcome.NONE
            undoableDeleteId.value = adventureId
            textAdventureRepository.deleteAdventure(current.token, adventureId)
                .onFailure {
                    // The adventure is back (the repository restored it), so there is nothing left
                    // to undo.
                    if (undoableDeleteId.value == adventureId) undoableDeleteId.value = null
                    deleteOutcome.value =
                        if (it.isRateLimited()) DeleteOutcome.RATE_LIMITED else DeleteOutcome.ERROR
                }
        }
    }

    /** Undoes the latest deletion, asking the backend to restore the adventure. */
    fun onUndoDelete() {
        val adventureId = undoableDeleteId.value ?: return
        undoableDeleteId.value = null
        val current = session ?: return
        viewModelScope.launch {
            deleteOutcome.value = DeleteOutcome.NONE
            textAdventureRepository.restoreAdventure(current.token, current.userId, adventureId)
                .onFailure {
                    deleteOutcome.value =
                        if (it.isRateLimited()) DeleteOutcome.RATE_LIMITED else DeleteOutcome.ERROR
                }
        }
    }

    /** Lets the deletion stand once the undo snackbar has gone away. */
    fun onUndoDismissed() {
        undoableDeleteId.value = null
    }

    private suspend fun refresh(current: Session) {
        deleteOutcome.value = DeleteOutcome.NONE
        undoableDeleteId.value = null
        refreshStatus.value = RefreshStatus.LOADING
        val result = textAdventureRepository.refreshAdventures(current.token, current.userId)
        refreshStatus.value = when {
            result.isSuccess -> RefreshStatus.SUCCESS
            result.exceptionOrNull().isRateLimited() -> RefreshStatus.RATE_LIMITED
            else -> RefreshStatus.ERROR
        }
    }

    private enum class RefreshStatus { IDLE, LOADING, SUCCESS, ERROR, RATE_LIMITED }

    private enum class DeleteOutcome { NONE, ERROR, RATE_LIMITED }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

private fun AdventureSummary.toItem() = TextAdventuresListUiState.AdventureItem(
    id = id,
    title = title,
    translatedTitle = translatedTitle,
    learningLanguage = learningLanguage,
    translationLanguage = translationLanguage,
    status = status,
    imageUrl = imageUrl,
)
