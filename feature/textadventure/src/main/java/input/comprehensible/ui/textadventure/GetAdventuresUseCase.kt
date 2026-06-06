package input.comprehensible.ui.textadventure

import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.textadventure.AdventureSummary
import input.comprehensible.data.textadventure.TextAdventureRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Streams the signed-in user's adventures by flat-mapping the account's user flow onto the
 * offline-first adventures flow. Keeping the composition here means `:data:textadventure` never has
 * to depend on `:data:account`. Emits an empty list while signed out.
 */
class GetAdventuresUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
    private val textAdventureRepository: TextAdventureRepository = TextAdventureRepository(),
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AdventureSummary>> =
        accountRepository.user.flatMapLatest { user ->
            user?.let { textAdventureRepository.getAdventures(userId = it.id) } ?: flowOf(emptyList())
        }
}
