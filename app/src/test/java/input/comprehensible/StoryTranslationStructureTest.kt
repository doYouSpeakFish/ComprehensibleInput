package input.comprehensible

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

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

            failures += validateStoryTranslations(storyDir, structures)
        }

        if (failures.isNotEmpty()) {
            fail(failures.joinToString(separator = "\n"))
        }
    }

    private fun validateStoryTranslations(
        storyDir: Path,
        structures: Map<String, List<ContentDescriptor>>
    ): List<String> {
        if (structures.isEmpty()) {
            return emptyList()
        }

        val referenceEntry = structures.entries.first()
        return structures.flatMap { (translation, structure) ->
            collectTranslationFailures(
                storyDir = storyDir,
                translation = translation,
                structure = structure,
                referenceEntry = referenceEntry,
            )
        }
    }

    private fun collectTranslationFailures(
        storyDir: Path,
        translation: String,
        structure: List<ContentDescriptor>,
        referenceEntry: Map.Entry<String, List<ContentDescriptor>>
    ): List<String> {
        val referenceStructure = referenceEntry.value
        val context = StoryTranslationContext(
            storyDir = storyDir,
            translation = translation,
            referenceKey = referenceEntry.key,
            referenceStructureSize = referenceStructure.size,
        )
        val failures = mutableListOf<String>()

        if (structure.size != referenceStructure.size) {
            failures += sizeMismatchMessage(
                context = context,
                structureSize = structure.size,
            )
            return failures
        }

        structure.forEachIndexed { index, item ->
            val referenceItem = referenceStructure[index]
            if (item.type != referenceItem.type) {
                failures += typeMismatchMessage(
                    context = context,
                    index = index,
                    actualType = item.type,
                    referenceType = referenceItem.type,
                )
                return@forEachIndexed
            }

            when (item.type) {
                ContentType.IMAGE -> {
                    if (item.imagePath != referenceItem.imagePath) {
                        failures += imagePathMismatchMessage(
                            context = context,
                            index = index,
                            actualPath = item.imagePath,
                            referencePath = referenceItem.imagePath,
                        )
                    }
                }

                ContentType.PARAGRAPH -> {
                    if (item.sentenceCount != referenceItem.sentenceCount) {
                        failures += sentenceCountMismatchMessage(
                            context = context,
                            index = index,
                            actualCount = item.sentenceCount,
                            referenceCount = referenceItem.sentenceCount,
                        )
                    }
                }
            }
        }

        return failures
    }

    private fun sizeMismatchMessage(
        context: StoryTranslationContext,
        structureSize: Int,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' has ")
        append(structureSize)
        append(" content items but expected ")
        append(context.referenceStructureSize)
        append(" as in '")
        append(context.referenceKey)
        append("'.")
    }

    private fun typeMismatchMessage(
        context: StoryTranslationContext,
        index: Int,
        actualType: ContentType,
        referenceType: ContentType,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' content index ")
        append(index)
        append(" type ")
        append(actualType)
        append(" differs from ")
        append(referenceType)
        append(" in '")
        append(context.referenceKey)
        append("'.")
    }

    private fun imagePathMismatchMessage(
        context: StoryTranslationContext,
        index: Int,
        actualPath: String?,
        referencePath: String?,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' image at index ")
        append(index)
        append(" uses '")
        append(actualPath)
        append("' but expected '")
        append(referencePath)
        append("' from '")
        append(context.referenceKey)
        append("'.")
    }

    private fun sentenceCountMismatchMessage(
        context: StoryTranslationContext,
        index: Int,
        actualCount: Int?,
        referenceCount: Int?,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' paragraph at index ")
        append(index)
        append(" has ")
        append(actualCount)
        append(" sentences but expected ")
        append(referenceCount)
        append(" as in '")
        append(context.referenceKey)
        append("'.")
    }

    private data class StoryTranslationContext(
        val storyDir: Path,
        val translation: String,
        val referenceKey: String,
        val referenceStructureSize: Int,
    )

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

