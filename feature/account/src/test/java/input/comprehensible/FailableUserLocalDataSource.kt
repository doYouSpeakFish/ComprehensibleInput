package input.comprehensible

import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.user.UserEntity

/**
 * Wraps the real Room-backed [UserLocalDataSource] so persistence runs against the actual database,
 * while still letting a test force a save to fail. SQLite cannot be made to reject an
 * otherwise-valid upsert on demand, so [upsertError] is the one piece of behaviour that has to be
 * simulated to cover the "sign in succeeds even when the local user record cannot be saved" path;
 * everything else delegates to real Room.
 */
class FailableUserLocalDataSource(
    private val delegate: UserLocalDataSource,
) : UserLocalDataSource by delegate {
    /** When set, [upsertUser] throws this instead of writing, simulating a local persistence failure. */
    var upsertError: Throwable? = null

    override suspend fun upsertUser(user: UserEntity) {
        upsertError?.let { throw it }
        delegate.upsertUser(user)
    }
}
