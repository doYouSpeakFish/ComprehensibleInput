package input.comprehensible.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {

    override fun logEvent(
        eventName: String,
        configure: Bundle.() -> Unit,
    ) {
        val parameters = Bundle().apply(configure)
        firebaseAnalytics.logEvent(eventName, parameters)
    }
}
