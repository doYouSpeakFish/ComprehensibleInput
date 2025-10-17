package input.comprehensible.analytics

import android.os.Bundle

class StubAnalyticsLogger : AnalyticsLogger {

    private val _events = mutableListOf<LoggedAnalyticsEvent>()
    val events: List<LoggedAnalyticsEvent> get() = _events

    override fun logEvent(
        eventName: String,
        configure: Bundle.() -> Unit,
    ) {
        val parameters = Bundle().apply(configure)
        _events += LoggedAnalyticsEvent(eventName, parameters)
    }

    fun clear() {
        _events.clear()
    }

    data class LoggedAnalyticsEvent(
        val eventName: String,
        val parameters: Bundle,
    )
}
