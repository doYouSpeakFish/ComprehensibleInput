package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.account.sources.local.Session
import kotlinx.coroutines.flow.Flow

class GetAccountSessionUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    operator fun invoke(): Flow<Session?> = accountRepository.session
}
