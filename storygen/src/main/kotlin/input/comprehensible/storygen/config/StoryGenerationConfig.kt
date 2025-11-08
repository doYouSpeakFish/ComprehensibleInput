package input.comprehensible.storygen.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class StoryGenerationConfig(
    val maxDepth: Int = 4,
    val maxRetriesPerSegment: Int = 3,
    val maxRequests: Int = 20,
    val inspirationWordCount: Int = 6,
    val minDelayBetweenRequests: Duration = 1.seconds,
) {
    init {
        require(maxDepth > 0) { "maxDepth must be greater than zero" }
        require(maxRetriesPerSegment > 0) { "maxRetriesPerSegment must be greater than zero" }
        require(maxRequests > 0) { "maxRequests must be greater than zero" }
        require(inspirationWordCount > 0) { "inspirationWordCount must be greater than zero" }
        require(minDelayBetweenRequests >= Duration.ZERO) { "minDelayBetweenRequests cannot be negative" }
    }
}
