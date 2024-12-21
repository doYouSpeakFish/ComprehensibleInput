package input.comprehensible.util

import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidDocument(private val uri: Uri) : Document {
    override suspend fun readText() = withContext(Dispatchers.IO) {
        uri.toFile().readText()
    }
}

fun Uri.toAndroidDocument() = AndroidDocument(this)
