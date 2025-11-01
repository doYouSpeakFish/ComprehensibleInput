package input.comprehensible

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
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
        structures: Map<String, StoryDescriptor>
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
        structure: StoryDescriptor,
        referenceEntry: Map.Entry<String, StoryDescriptor>
    ): List<String> {
        val referenceStructure = referenceEntry.value
        val context = StoryTranslationContext(
            storyDir = storyDir,
            translation = translation,
            referenceKey = referenceEntry.key,
            referenceStructureSize = referenceStructure.content.size,
        )
        val failures = mutableListOf<String>()

        if (structure.featuredImagePath != referenceStructure.featuredImagePath) {
            failures += featuredImageMismatchMessage(
                context = context,
                actualPath = structure.featuredImagePath,
                referencePath = referenceStructure.featuredImagePath,
            )
        }

        if (structure.content.size != referenceStructure.content.size) {
            failures += sizeMismatchMessage(
                context = context,
                structureSize = structure.content.size,
            )
            return failures
        }

        structure.content.forEachIndexed { index, item ->
            val referenceItem = referenceStructure.content[index]
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

    private fun featuredImageMismatchMessage(
        context: StoryTranslationContext,
        actualPath: String,
        referencePath: String,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' featured image '")
        append(actualPath)
        append("' differs from '")
        append(referencePath)
        append("' in '")
        append(context.referenceKey)
        append("'.")
    }

    private data class StoryTranslationContext(
        val storyDir: Path,
        val translation: String,
        val referenceKey: String,
        val referenceStructureSize: Int,
    )

    private fun parseStructure(path: Path): StoryDescriptor {
        val element = json.parseToJsonElement(path.readText()).jsonObject
        val startPartId = element.requireString(key = "startPartId", path = path)
        val featuredImagePath = element.requireString(key = "featuredImagePath", path = path)
        val partsArray = element.requireArray(key = "parts", path = path)
        val partObject = partsArray.findPartObject(startPartId = startPartId, path = path)
        val content = partObject.requireContentArray(path = path, partId = startPartId)

        val descriptors = content.mapIndexed { index, item ->
            item.jsonObject.toContentDescriptor(path = path, index = index)
        }

        val containsFeaturedImage = descriptors.any { it.imagePath == featuredImagePath }
        if (!containsFeaturedImage) {
            error(
                "Story file '$path' featuredImagePath '$featuredImagePath' does not match any image in part '$startPartId'"
            )
        }

        return StoryDescriptor(
            featuredImagePath = featuredImagePath,
            content = descriptors,
        )
    }

    private fun JsonObject.requireString(key: String, path: Path): String {
        return this[key]?.jsonPrimitive?.content
            ?: error("Story file '$path' is missing a '$key'")
    }

    private fun JsonObject.requireArray(key: String, path: Path): JsonArray {
        val element = this[key] ?: error("Story file '$path' is missing a '$key' array")
        return element as? JsonArray
            ?: error("Story file '$path' has a non-array '$key' element")
    }

    private fun JsonArray.findPartObject(startPartId: String, path: Path): JsonObject {
        return map { it.jsonObject }
            .firstOrNull { part ->
                part["id"]?.jsonPrimitive?.content == startPartId
            }
            ?: error("Story file '$path' does not have a part matching startPartId '$startPartId'")
    }

    private fun JsonObject.requireContentArray(path: Path, partId: String): JsonArray {
        val contentElement = this["content"]
            ?: error("Story file '$path' is missing a 'content' array in part '$partId'")
        return contentElement as? JsonArray
            ?: error("Story file '$path' has a non-array 'content' element in part '$partId'")
    }

    private fun JsonObject.toContentDescriptor(path: Path, index: Int): ContentDescriptor {
        val type = this["type"]?.jsonPrimitive?.content
            ?: error("Content item $index in '$path' is missing a 'type'")
        return when (type) {
            "image" -> {
                val imagePath = this["path"]?.jsonPrimitive?.content
                    ?: error("Image item $index in '$path' is missing a 'path'")
                ContentDescriptor(ContentType.IMAGE, imagePath = imagePath)
            }

            "paragraph" -> {
                val sentencesElement = this["sentences"]
                    ?: error("Paragraph item $index in '$path' is missing 'sentences'")
                val sentencesArray = sentencesElement as? JsonArray
                    ?: error("Paragraph item $index in '$path' has non-array 'sentences'")
                ContentDescriptor(ContentType.PARAGRAPH, sentenceCount = sentencesArray.size)
            }

            else -> error("Unsupported content type '$type' in '$path'")
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

    private data class StoryDescriptor(
        val featuredImagePath: String,
        val content: List<ContentDescriptor>,
    )

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

