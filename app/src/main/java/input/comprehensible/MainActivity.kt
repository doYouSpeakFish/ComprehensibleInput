package input.comprehensible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import input.comprehensible.analytics.AnalyticsLogger
import input.comprehensible.analytics.LocalAnalyticsLogger
import input.comprehensible.ui.ComprehensibleInputApp
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Timber.d("Setting UI content")

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalAnalyticsLogger provides analyticsLogger) {
                ComprehensibleInputApp()
            }
        }
    }
}
