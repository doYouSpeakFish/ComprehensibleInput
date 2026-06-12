package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class ResetPasswordUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String, password: String, code: String): Result<Unit> =
        accountRepository.resetPassword(email, password, code)
}
