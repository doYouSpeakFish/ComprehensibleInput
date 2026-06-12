package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class RequestPasswordResetCodeUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        accountRepository.requestPasswordResetCode(email)
}
