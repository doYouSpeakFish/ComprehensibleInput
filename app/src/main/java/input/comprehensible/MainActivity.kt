package input.comprehensible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.rememberNavController
import input.comprehensible.ui.ComprehensibleInputApp
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Timber.d("Setting UI content")

        enableEdgeToEdge()
        setContent {
            ComprehensibleInputApp(
                navController = rememberNavController(),
                darkTheme = isSystemInDarkTheme()
            )
        }
    }
}
