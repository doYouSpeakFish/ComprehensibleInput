package input.comprehensible.analytics

import android.os.Bundle
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * API for logging analytics events.
 */
interface AnalyticsLogger {
    fun logEvent(
        eventName: String,
        configure: Bundle.() -> Unit = {},
    )
}

/**
 * No-op implementation of [AnalyticsLogger] used for tooling previews.
 */
object NoOpAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(
        eventName: String,
        configure: Bundle.() -> Unit,
    ) = Unit
}

/**
 * Composition local exposing the current [AnalyticsLogger].
 */
val LocalAnalyticsLogger = staticCompositionLocalOf<AnalyticsLogger> { NoOpAnalyticsLogger }
