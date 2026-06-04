package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class DeleteAccountUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(password: String): Result<Unit> =
        accountRepository.deleteAccount(password)
}
