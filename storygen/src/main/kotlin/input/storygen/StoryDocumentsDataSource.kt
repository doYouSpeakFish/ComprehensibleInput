package input.storygen

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlinx.serialization.json.Json

interface StoryDocumentsDataSource {
    suspend fun save(document: StoryDocument, language: StoryLanguage): Path
}

class FileStoryDocumentsDataSource(
    private val storiesRoot: Path,
    private val json: Json,
) : StoryDocumentsDataSource {
    override suspend fun save(document: StoryDocument, language: StoryLanguage): Path {
        val storyDir = storiesRoot.resolve(document.id)
        Files.createDirectories(storyDir)
        val destination = storyDir.resolve("${language.code}.json")
        destination.writeText(json.encodeToString(StoryDocument.serializer(), document))
        return destination
    }
}
