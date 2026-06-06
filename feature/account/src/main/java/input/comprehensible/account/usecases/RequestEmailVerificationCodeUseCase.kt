package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class RequestEmailVerificationCodeUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        accountRepository.requestEmailVerificationCode(email)
}
