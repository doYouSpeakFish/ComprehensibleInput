package input.comprehensible.account.usecases

import input.comprehensible.data.account.AccountRepository

class SignOutUseCase(
    private val accountRepository: AccountRepository = AccountRepository(),
) {
    suspend operator fun invoke(): Result<Unit> = accountRepository.signOut()
}
