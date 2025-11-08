package input.comprehensible.storygen.rate

interface RateLimiter {
    suspend fun acquire()
    val attempts: Int
}

class RateLimitReachedException(message: String) : Exception(message)
