package input.comprehensible.analytics

import com.google.firebase.analytics.FirebaseAnalytics

fun AnalyticsLogger.logScreenView(route: String) {
    logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, route)
    }
}
