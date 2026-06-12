package input.comprehensible.data.textadventure.sources.remote

/**
 * Thrown by [AdventureRemoteDataSource] when the backend rejects a text adventure request with HTTP
 * 429 (Too Many Requests). During early access the feature is rate limited across all users, so
 * callers surface a "the system is busy, try again later" message rather than a generic error.
 */
class RateLimitedException : Exception("Text adventure requests are currently rate limited")

/** Whether a failure was caused by the backend rate limiting the text adventure feature. */
fun Throwable?.isRateLimited(): Boolean = this is RateLimitedException
