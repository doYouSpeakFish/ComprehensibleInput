package input.comprehensible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import dagger.hilt.android.AndroidEntryPoint
import input.comprehensible.extensions.openDocument
import input.comprehensible.ui.ComprehensibleInputApp
import input.comprehensible.util.DocumentOpener
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val androidDocumentOpener = DocumentOpener(onOpenDocumentRequest = ::openDocument)

    @Suppress("PropertyName")
    val LocalDocumentOpener = compositionLocalOf {
        androidDocumentOpener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Timber.d("Setting UI content")

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalDocumentOpener provides androidDocumentOpener) {
                ComprehensibleInputApp()
            }
        }
    }
}
