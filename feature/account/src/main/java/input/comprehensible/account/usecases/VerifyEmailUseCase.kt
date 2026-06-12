package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class VerifyEmailUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String, code: String): Result<Unit> =
        accountRepository.verifyEmail(email, code)
}
