package input.storygen

import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.serialization.json.Json

class StoryGenerator(
    private val repository: StoryGenerationRepository,
) {
    suspend fun generateEnglishStory(
        storyId: String,
        title: String,
        maxDepth: Int,
        startPartId: String = "start",
        featuredImagePath: String? = null,
    ): StoryGenerationResult {
        val plan = StoryPlan(
            storyId = storyId,
            title = title,
            language = StoryLanguage(code = "en", label = "English"),
            maxDepth = maxDepth,
            startPartId = startPartId,
            featuredImagePath = featuredImagePath,
        )
        return repository.generate(plan)
    }
}

object StoryGeneratorFactory {
    private val defaultJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun withKoog(
        storiesRoot: Path = Paths.get("stories"),
        propertyName: String = "googleAi.apiKey",
        localPropertiesRoot: Path = Paths.get("."),
        json: Json = defaultJson,
    ): StoryGenerator? {
        val koogDataSource = KoogStoryPartsDataSource.fromLocalProperties(
            storiesJson = json,
            root = localPropertiesRoot,
            propertyName = propertyName,
        ) ?: return null

        val documentsDataSource = FileStoryDocumentsDataSource(storiesRoot, json)
        val repository = StoryGenerationRepository(
            partsDataSource = koogDataSource,
            documentsDataSource = documentsDataSource,
        )
        return StoryGenerator(repository)
    }
}
