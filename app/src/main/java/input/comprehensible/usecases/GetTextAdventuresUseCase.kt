package input.comprehensible.usecases

import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.textadventures.TextAdventuresListResult
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class GetTextAdventuresUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
    private val textAdventuresRepository: TextAdventuresRepository = TextAdventuresRepository(),
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TextAdventuresListResult> =
        accountRepository.session.flatMapLatest { session ->
            textAdventuresRepository.getAdventures(userId = session?.userId)
        }
}
