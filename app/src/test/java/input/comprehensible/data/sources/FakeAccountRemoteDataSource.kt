package input.comprehensible.data.sources

import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource

class FakeAccountRemoteDataSource : AccountRemoteDataSource {
    private val createAccountResults = ArrayDeque<Result<Unit>>()
    private val verifyEmailResults = ArrayDeque<Result<Unit>>()

    fun enqueueCreateAccountResult(result: Result<Unit>) {
        createAccountResults.add(result)
    }

    fun enqueueVerifyEmailResult(result: Result<Unit>) {
        verifyEmailResults.add(result)
    }

    override suspend fun createAccount(email: String, password: String) {
        createAccountResults.removeFirstOrNull()
            ?.getOrThrow()
            ?: error("No scripted create account result available")
    }

    override suspend fun verifyEmail(email: String, code: String) {
        verifyEmailResults.removeFirstOrNull()
            ?.getOrThrow()
            ?: error("No scripted verify email result available")
    }
}
