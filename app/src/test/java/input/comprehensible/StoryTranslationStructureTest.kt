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
                path.fileName.toString() to parseStory(path)
            }

            failures += validateStructureConsistency(storyDir, structures)

            val referenceEntry = structures.entries.first()
            failures += validateStoryGraph(
                storyDir = storyDir,
                translation = referenceEntry.key,
                story = referenceEntry.value,
            )
        }

        if (failures.isNotEmpty()) {
            fail(failures.joinToString(separator = "\n"))
        }
    }

    private fun validateStructureConsistency(
        storyDir: Path,
        structures: Map<String, StoryDescriptor>,
    ): List<String> {
        if (structures.isEmpty()) {
            return emptyList()
        }

        val referenceEntry = structures.entries.first()
        val reference = referenceEntry.value

        return structures.flatMap { (translation, structure) ->
            val context = StoryTranslationContext(
                storyDir = storyDir,
                translation = translation,
                referenceKey = referenceEntry.key,
            )

            buildList {
                addAll(checkStartPart(context, structure, reference))
                addAll(checkFeaturedImage(context, structure, reference))

                val partKeyResult = checkPartKeys(context, structure, reference)
                addAll(partKeyResult.failures)
                if (!partKeyResult.matches) {
                    return@flatMap this
                }

                addAll(checkPartContent(context, structure, reference))
                addAll(checkChoices(context, structure, reference))
            }
        }
    }

    private fun checkStartPart(
        context: StoryTranslationContext,
        structure: StoryDescriptor,
        reference: StoryDescriptor,
    ): List<String> {
        if (structure.startPartId == reference.startPartId) {
            return emptyList()
        }

        return listOf(
            context.storyMessage(
                "starts at part '${structure.startPartId}' but expected '${reference.startPartId}' as in '${context.referenceKey}'."
            )
        )
    }

    private fun checkFeaturedImage(
        context: StoryTranslationContext,
        structure: StoryDescriptor,
        reference: StoryDescriptor,
    ): List<String> {
        if (structure.featuredImagePath == reference.featuredImagePath) {
            return emptyList()
        }

        return listOf(
            featuredImageMismatchMessage(
                context = context,
                actualPath = structure.featuredImagePath,
                referencePath = reference.featuredImagePath,
            )
        )
    }

    private fun checkPartKeys(
        context: StoryTranslationContext,
        structure: StoryDescriptor,
        reference: StoryDescriptor,
    ): PartKeyCheckResult {
        if (structure.parts.keys == reference.parts.keys) {
            return PartKeyCheckResult(matches = true, failures = emptyList())
        }

        val missing = (reference.parts.keys - structure.parts.keys).sorted()
        val extra = (structure.parts.keys - reference.parts.keys).sorted()

        val failures = buildList {
            if (missing.isNotEmpty()) {
                add(
                    context.storyMessage(
                        "is missing parts: ${missing.joinToString()}."
                    )
                )
            }
            if (extra.isNotEmpty()) {
                add(
                    context.storyMessage(
                        "has unexpected parts: ${extra.joinToString()}."
                    )
                )
            }
        }

        return PartKeyCheckResult(matches = false, failures = failures)
    }

    private data class PartKeyCheckResult(
        val matches: Boolean,
        val failures: List<String>,
    )

    private fun StoryTranslationContext.storyMessage(content: String): String {
        return storyMessage(storyDir, translation, content)
    }

    private fun StoryTranslationContext.partMessage(partId: String, content: String): String {
        return storyMessage("part '$partId' $content")
    }

    private fun storyMessage(storyDir: Path, translation: String, content: String): String {
        return "Story '${storyDir.fileName}' translation '$translation' $content"
    }

    private fun checkPartContent(
        context: StoryTranslationContext,
        structure: StoryDescriptor,
        reference: StoryDescriptor,
    ): List<String> {
        val failures = mutableListOf<String>()

        structure.parts.keys.sorted().forEach { partId ->
            val part = structure.parts.getValue(partId)
            val referencePart = reference.parts.getValue(partId)

            if (part.content.size != referencePart.content.size) {
                failures += sizeMismatchMessage(
                    context = context,
                    partId = partId,
                    structureSize = part.content.size,
                    referenceSize = referencePart.content.size,
                )
                return@forEach
            }

            part.content.forEachIndexed { index, item ->
                val referenceItem = referencePart.content[index]
                failures += compareContentItem(
                    context = context,
                    partId = partId,
                    index = index,
                    item = item,
                    referenceItem = referenceItem,
                )
            }
        }

        return failures
    }

    private fun compareContentItem(
        context: StoryTranslationContext,
        partId: String,
        index: Int,
        item: ContentDescriptor,
        referenceItem: ContentDescriptor,
    ): List<String> {
        if (item.type != referenceItem.type) {
            return listOf(
                typeMismatchMessage(
                    context = context,
                    partId = partId,
                    index = index,
                    actualType = item.type,
                    referenceType = referenceItem.type,
                )
            )
        }

        return when (item.type) {
            ContentType.IMAGE -> compareImageItem(context, partId, index, item, referenceItem)
            ContentType.PARAGRAPH -> compareParagraphItem(context, partId, index, item, referenceItem)
        }
    }

    private fun compareImageItem(
        context: StoryTranslationContext,
        partId: String,
        index: Int,
        item: ContentDescriptor,
        referenceItem: ContentDescriptor,
    ): List<String> {
        if (item.imagePath == referenceItem.imagePath) {
            return emptyList()
        }

        return listOf(
            imagePathMismatchMessage(
                context = context,
                partId = partId,
                index = index,
                actualPath = item.imagePath,
                referencePath = referenceItem.imagePath,
            )
        )
    }

    private fun compareParagraphItem(
        context: StoryTranslationContext,
        partId: String,
        index: Int,
        item: ContentDescriptor,
        referenceItem: ContentDescriptor,
    ): List<String> {
        if (item.sentenceCount == referenceItem.sentenceCount) {
            return emptyList()
        }

        return listOf(
            sentenceCountMismatchMessage(
                context = context,
                partId = partId,
                index = index,
                actualCount = item.sentenceCount,
                referenceCount = referenceItem.sentenceCount,
            )
        )
    }

    private fun checkChoices(
        context: StoryTranslationContext,
        structure: StoryDescriptor,
        reference: StoryDescriptor,
    ): List<String> {
        val failures = mutableListOf<String>()

        structure.parts.keys.sorted().forEach { partId ->
            val part = structure.parts.getValue(partId)
            val referencePart = reference.parts.getValue(partId)

            if (part.choices.size != referencePart.choices.size) {
                failures += context.partMessage(
                    partId = partId,
                    content = "has ${part.choices.size} choices but expected ${referencePart.choices.size} as in '${context.referenceKey}'."
                )
                return@forEach
            }

            part.choices.forEachIndexed { index, choice ->
                val referenceChoice = referencePart.choices[index]
                if (choice.targetPartId != referenceChoice.targetPartId) {
                    val message = buildString {
                        append("choice $index targets '")
                        append(choice.targetPartId)
                        append("' but expected '")
                        append(referenceChoice.targetPartId)
                        append("' as in '")
                        append(context.referenceKey)
                        append("'.")
                    }
                    failures += context.partMessage(partId = partId, content = message)
                }
            }
        }

        return failures
    }

    private fun validateStoryGraph(
        storyDir: Path,
        translation: String,
        story: StoryDescriptor,
    ): List<String> {
        val failures = mutableListOf<String>()
        val visited = mutableSetOf<String>()
        val visiting = mutableSetOf<String>()

        fun dfs(partId: String) {
            val part = story.parts[partId]
            if (part == null) {
                  failures += storyMessage(
                      storyDir = storyDir,
                      translation = translation,
                      content = "references missing part '$partId'.",
                  )
                return
            }
            if (!visited.add(partId)) {
                  failures += storyMessage(
                      storyDir = storyDir,
                      translation = translation,
                      content = "part '$partId' can be reached through more than one path.",
                  )
                return
            }
            visiting.add(partId)
            part.choices.forEach { choice ->
                val targetId = choice.targetPartId
                if (!story.parts.containsKey(targetId)) {
                  failures += storyMessage(
                      storyDir = storyDir,
                      translation = translation,
                      content = "part '$partId' has a choice targeting unknown part '$targetId'.",
                  )
                    return@forEach
                }
                if (visiting.contains(targetId)) {
                  failures += storyMessage(
                      storyDir = storyDir,
                      translation = translation,
                      content = "part '$partId' choice to '$targetId' creates a loop.",
                  )
                    return@forEach
                }
                dfs(targetId)
            }
            visiting.remove(partId)
        }

        dfs(story.startPartId)

        val unreachable = story.parts.keys - visited
        if (unreachable.isNotEmpty()) {
              failures += storyMessage(
                  storyDir = storyDir,
                  translation = translation,
                  content = "has unreachable parts: ${unreachable.sorted().joinToString()}.",
              )
        }

        return failures
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

    private fun sizeMismatchMessage(
        context: StoryTranslationContext,
        partId: String,
        structureSize: Int,
        referenceSize: Int,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' part '")
        append(partId)
        append("' has ")
        append(structureSize)
        append(" content items but expected ")
        append(referenceSize)
        append(" as in '")
        append(context.referenceKey)
        append("'.")
    }

    private fun typeMismatchMessage(
        context: StoryTranslationContext,
        partId: String,
        index: Int,
        actualType: ContentType,
        referenceType: ContentType,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' part '")
        append(partId)
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
        partId: String,
        index: Int,
        actualPath: String?,
        referencePath: String?,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' part '")
        append(partId)
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
        partId: String,
        index: Int,
        actualCount: Int?,
        referenceCount: Int?,
    ) = buildString {
        append("Story '")
        append(context.storyDir.fileName)
        append("' translation '")
        append(context.translation)
        append("' part '")
        append(partId)
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

    private fun parseStory(path: Path): StoryDescriptor {
        val element = json.parseToJsonElement(path.readText()).jsonObject
        val startPartId = element.requireString(key = "startPartId", path = path)
        val featuredImagePath = element.requireString(key = "featuredImagePath", path = path)
        val partsArray = element.requireArray(key = "parts", path = path)

        val parts = partsArray.map { partElement ->
            val partObject = partElement.jsonObject
            val partId = partObject.requireString(key = "id", path = path, context = "part")
            val contentArray = partObject.requireContentArray(path = path, partId = partId)
            val contentDescriptors = contentArray.mapIndexed { index, item ->
                item.jsonObject.toContentDescriptor(path = path, partId = partId, index = index)
            }
            val choicesArray = partObject["choices"] as? JsonArray ?: JsonArray(emptyList())
            val choices = choicesArray.mapIndexed { index, choiceElement ->
                val choiceObject = choiceElement.jsonObject
                val targetId = choiceObject.requireString(key = "targetPartId", path = path, context = "choice $index in part '$partId'")
                val text = choiceObject.requireString(key = "text", path = path, context = "choice $index in part '$partId'")
                ChoiceDescriptor(
                    text = text,
                    targetPartId = targetId,
                )
            }
            StoryPartDescriptor(
                id = partId,
                content = contentDescriptors,
                choices = choices,
            )
        }

        val duplicateParts = parts.groupBy { it.id }.filterValues { it.size > 1 }.keys
        if (duplicateParts.isNotEmpty()) {
            error("Story file '$path' has duplicate part ids: ${duplicateParts.sorted().joinToString()}")
        }

        val partsById = parts.associateBy { it.id }

        if (featuredImagePath.isNotBlank()) {
            val containsFeaturedImage = partsById.values.any { part ->
                part.content.any { it.imagePath == featuredImagePath }
            }
            if (!containsFeaturedImage) {
                error("Story file '$path' featuredImagePath '$featuredImagePath' does not match any image in the story")
            }
        }

        if (!partsById.containsKey(startPartId)) {
            error("Story file '$path' startPartId '$startPartId' does not match any part")
        }

        return StoryDescriptor(
            startPartId = startPartId,
            featuredImagePath = featuredImagePath,
            parts = partsById,
        )
    }

    private fun JsonObject.requireString(key: String, path: Path, context: String? = null): String {
        val value = this[key]?.jsonPrimitive?.content
        if (value != null) {
            return value
        }
        if (context != null) {
            error("Story file '$path' $context is missing a '$key'")
        } else {
            error("Story file '$path' is missing a '$key'")
        }
    }

    private fun JsonObject.requireArray(key: String, path: Path): JsonArray {
        val element = this[key] ?: error("Story file '$path' is missing a '$key' array")
        return element as? JsonArray
            ?: error("Story file '$path' has a non-array '$key' element")
    }

    private fun JsonObject.requireContentArray(path: Path, partId: String): JsonArray {
        val contentElement = this["content"]
            ?: error("Story file '$path' is missing a 'content' array in part '$partId'")
        return contentElement as? JsonArray
            ?: error("Story file '$path' has a non-array 'content' element in part '$partId'")
    }

    private fun JsonObject.toContentDescriptor(
        path: Path,
        partId: String,
        index: Int,
    ): ContentDescriptor {
        val type = this["type"]?.jsonPrimitive?.content
            ?: error("Content item $index in part '$partId' of '$path' is missing a 'type'")
        return when (type) {
            "image" -> {
                val imagePath = this["path"]?.jsonPrimitive?.content
                    ?: error("Image item $index in part '$partId' of '$path' is missing a 'path'")
                ContentDescriptor(ContentType.IMAGE, imagePath = imagePath)
            }

            "paragraph" -> {
                val sentencesElement = this["sentences"]
                    ?: error("Paragraph item $index in part '$partId' of '$path' is missing 'sentences'")
                val sentencesArray = sentencesElement as? JsonArray
                    ?: error("Paragraph item $index in part '$partId' of '$path' has non-array 'sentences'")
                ContentDescriptor(ContentType.PARAGRAPH, sentenceCount = sentencesArray.size)
            }

            else -> error("Unsupported content type '$type' in part '$partId' of '$path'")
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
        val startPartId: String,
        val featuredImagePath: String,
        val parts: Map<String, StoryPartDescriptor>,
    )

    private data class StoryPartDescriptor(
        val id: String,
        val content: List<ContentDescriptor>,
        val choices: List<ChoiceDescriptor>,
    )

    private data class ChoiceDescriptor(
        val text: String,
        val targetPartId: String,
    )

    private data class ContentDescriptor(
        val type: ContentType,
        val sentenceCount: Int? = null,
        val imagePath: String? = null,
    )

    private enum class ContentType {
        IMAGE,
        PARAGRAPH
    }

    private data class StoryTranslationContext(
        val storyDir: Path,
        val translation: String,
        val referenceKey: String,
    )
}
