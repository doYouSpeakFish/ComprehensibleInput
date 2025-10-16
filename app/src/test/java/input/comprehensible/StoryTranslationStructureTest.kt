package input.comprehensible

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.streams.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class StoryTranslationStructureTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun translationsHaveMatchingStructure() {
        val storiesRoot = locateStoriesRoot()
        val storyDirectories = Files.list(storiesRoot).use { stream ->
            stream.filter(Files::isDirectory)
                .sorted(compareBy { it.fileName.toString() })
                .toList()
        }

        assertTrue("No stories found in ${storiesRoot.toAbsolutePath()}", storyDirectories.isNotEmpty())

        val failures = mutableListOf<String>()

        for (storyDir in storyDirectories) {
            val translations = Files.list(storyDir).use { stream ->
                stream.filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".json") }
                    .sorted(compareBy { it.fileName.toString() })
                    .toList()
            }

            if (translations.size <= 1) {
                continue
            }

            val structures = translations.associate { path ->
                path.fileName.toString() to parseStructure(path)
            }

            val referenceEntry = structures.entries.first()
            val referenceStructure = referenceEntry.value

            structures.forEach { (translation, structure) ->
                if (structure.size != referenceStructure.size) {
                    failures += buildString {
                        append("Story '")
                        append(storyDir.fileName)
                        append("' translation '")
                        append(translation)
                        append("' has ")
                        append(structure.size)
                        append(" content items but expected ")
                        append(referenceStructure.size)
                        append(" as in '")
                        append(referenceEntry.key)
                        append("'.")
                    }
                    return@forEach
                }

                structure.forEachIndexed { index, item ->
                    val referenceItem = referenceStructure[index]
                    if (item.type != referenceItem.type) {
                        failures += "Story '${storyDir.fileName}' translation '$translation' content index $index type ${item.type} differs from ${referenceItem.type} in '${referenceEntry.key}'."
                        return@forEachIndexed
                    }

                    when (item.type) {
                        ContentType.IMAGE -> {
                            if (item.imagePath != referenceItem.imagePath) {
                                failures += "Story '${storyDir.fileName}' translation '$translation' image at index $index uses '${item.imagePath}' but expected '${referenceItem.imagePath}' from '${referenceEntry.key}'."
                            }
                        }

                        ContentType.PARAGRAPH -> {
                            if (item.sentenceCount != referenceItem.sentenceCount) {
                                failures += "Story '${storyDir.fileName}' translation '$translation' paragraph at index $index has ${item.sentenceCount} sentences but expected ${referenceItem.sentenceCount} as in '${referenceEntry.key}'."
                            }
                        }
                    }
                }
            }
        }

        if (failures.isNotEmpty()) {
            fail(failures.joinToString(separator = "\n"))
        }
    }

    private fun parseStructure(path: Path): List<ContentDescriptor> {
        val element = json.parseToJsonElement(path.readText())
        val content = element.jsonObject["content"] ?: error("Story file '$path' is missing a 'content' array")
        if (content !is JsonArray) {
            error("Story file '$path' has a non-array 'content' element")
        }

        return content.mapIndexed { index, item ->
            val obj = item.jsonObject
            val type = obj["type"]?.jsonPrimitive?.content ?: error("Content item $index in '$path' is missing a 'type'")
            when (type) {
                "image" -> {
                    val imagePath = obj["path"]?.jsonPrimitive?.content
                        ?: error("Image item $index in '$path' is missing a 'path'")
                    ContentDescriptor(ContentType.IMAGE, imagePath = imagePath)
                }

                "paragraph" -> {
                    val sentencesElement = obj["sentences"] ?: error("Paragraph item $index in '$path' is missing 'sentences'")
                    val sentencesArray = sentencesElement as? JsonArray
                        ?: error("Paragraph item $index in '$path' has non-array 'sentences'")
                    ContentDescriptor(ContentType.PARAGRAPH, sentenceCount = sentencesArray.size)
                }

                else -> error("Unsupported content type '$type' in '$path'")
            }
        }
    }

    private fun locateStoriesRoot(): Path {
        val projectRoot = Paths.get("").toAbsolutePath()
        val candidates = listOf(
            projectRoot.resolve("app/src/main/assets/stories"),
            projectRoot.resolve("src/main/assets/stories")
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Could not locate the stories directory. Looked in: ${candidates.joinToString()}")
    }

    private data class ContentDescriptor(
        val type: ContentType,
        val sentenceCount: Int? = null,
        val imagePath: String? = null
    )

    private enum class ContentType {
        IMAGE,
        PARAGRAPH
    }
}
