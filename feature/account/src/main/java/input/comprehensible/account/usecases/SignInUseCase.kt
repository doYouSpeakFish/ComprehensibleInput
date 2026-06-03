package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class SignInUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        accountRepository.signIn(email, password)
}
