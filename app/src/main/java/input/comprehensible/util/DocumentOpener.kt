package input.comprehensible.util

interface Document {
    suspend fun readText(): String
}

data class DocumentOpener(
    val onOpenDocumentRequest: suspend (mimeTypes: List<String>) -> Document?
)
