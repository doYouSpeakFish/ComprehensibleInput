package input.comprehensible.extensions

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import input.comprehensible.util.Document
import input.comprehensible.util.toAndroidDocument
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun ComponentActivity.openDocument(mimeTypes: List<String>): Document? =
    suspendCoroutine { continuation ->
        val launcher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { result ->
            val document = result?.toAndroidDocument()
            continuation.resume(document)
        }

        launcher.launch(mimeTypes.toTypedArray())
    }