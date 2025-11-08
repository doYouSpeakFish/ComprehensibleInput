package input.comprehensible.storygen.rate

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SimpleRateLimiter(
    private val maxRequests: Int,
    private val minDelay: Duration,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) : RateLimiter {

    init {
        require(maxRequests > 0) { "maxRequests must be greater than zero" }
        require(minDelay >= Duration.ZERO) { "minDelay cannot be negative" }
    }

    private var lastMark: TimeMark? = null
    private var count: Int = 0

    override val attempts: Int
        get() = count

    override suspend fun acquire() {
        if (count >= maxRequests) {
            throw RateLimitReachedException("Reached the maximum of $maxRequests story requests for this run")
        }
        val mark = lastMark
        if (mark != null && minDelay > Duration.ZERO) {
            val elapsed = mark.elapsedNow()
            val remaining = minDelay - elapsed
            if (remaining > Duration.ZERO) {
                delay(remaining.inWholeMilliseconds)
            }
        }
        count += 1
        lastMark = timeSource.markNow()
    }
}
