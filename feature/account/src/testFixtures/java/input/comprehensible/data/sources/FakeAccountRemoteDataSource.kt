package input.comprehensible.data.sources

import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.account.sources.remote.SignInData
import kotlinx.coroutines.delay

class FakeAccountRemoteDataSource : AccountRemoteDataSource {
    private val createAccountResults = ArrayDeque<Result<Unit>>()
    private val verifyEmailResults = ArrayDeque<Result<Unit>>()
    private val signInResults = ArrayDeque<Result<SignInData>>()

    /**
     * When greater than zero, requests suspend for this many milliseconds before completing. This
     * keeps a request in-flight so tests can observe the loading state before it finishes.
     */
    var requestDelayMillis: Long = 0L

    fun enqueueCreateAccountResult(result: Result<Unit>) {
        createAccountResults.add(result)
    }

    fun enqueueVerifyEmailResult(result: Result<Unit>) {
        verifyEmailResults.add(result)
    }

    fun enqueueSignInResult(result: Result<SignInData>) {
        signInResults.add(result)
    }

    override suspend fun createAccount(email: String, password: String) {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
        createAccountResults.removeFirstOrNull()
            ?.getOrThrow()
            ?: error("No scripted create account result available")
    }

    override suspend fun verifyEmail(email: String, code: String) {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
        verifyEmailResults.removeFirstOrNull()
            ?.getOrThrow()
            ?: error("No scripted verify email result available")
    }

    override suspend fun signIn(email: String, password: String): SignInData {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
        return signInResults.removeFirstOrNull()
            ?.getOrThrow()
            ?: error("No scripted sign in result available")
    }

    override suspend fun signOut(token: String) {
        if (requestDelayMillis > 0) delay(requestDelayMillis)
    }
}
