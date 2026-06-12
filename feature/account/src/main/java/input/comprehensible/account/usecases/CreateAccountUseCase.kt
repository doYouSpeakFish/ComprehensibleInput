package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class CreateAccountUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        accountRepository.createAccount(email, password)
}
