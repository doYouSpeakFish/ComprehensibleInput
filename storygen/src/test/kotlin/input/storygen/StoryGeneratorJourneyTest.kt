package input.storygen

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText

class StoryGeneratorJourneyTest {

    @Test
    fun storyStopsWhenItReachesTheLimitOrRunsOutOfChoices() = runTest {
        // GIVEN a storyteller who keeps adventures concise
        val journey = StoryGenerationJourney(maxDepth = 3)

        // WHEN the storyteller shares the tale
        journey.tellStory()

        // THEN the requests remind the model how close it is to the limit
        journey.expectDepthFeedback(
            expectedProgress = listOf(
                DepthProgress(partId = "dawn", stepsRemaining = 2),
                DepthProgress(partId = "forest", stepsRemaining = 1),
                DepthProgress(partId = "campfire", stepsRemaining = 0),
                DepthProgress(partId = "river", stepsRemaining = 1),
            )
        )

        // AND the final story is saved in the stories directory
        journey.expectSavedStory(
            expected = StoryDocument(
                id = "evening-walk",
                title = "Evening Walk",
                startPartId = "dawn",
                featuredImagePath = "",
                parts = listOf(
                    StoryPart(
                        id = "dawn",
                        content = listOf(
                            StoryParagraph(sentences = listOf("A traveler watches the last light fade."))
                        ),
                        choice = null,
                    ),
                    StoryPart(
                        id = "forest",
                        content = listOf(
                            StoryParagraph(sentences = listOf("Lanterns sway between tall trees."))
                        ),
                        choice = StoryChoice(text = "Follow the lantern path", parentPartId = "dawn"),
                    ),
                    StoryPart(
                        id = "campfire",
                        content = listOf(
                            StoryParagraph(sentences = listOf("A quiet campfire waits for company."))
                        ),
                        choice = StoryChoice(text = "Inspect the campfire", parentPartId = "forest"),
                    ),
                    StoryPart(
                        id = "river",
                        content = listOf(
                            StoryParagraph(sentences = listOf("Water rushes louder than the wind."))
                        ),
                        choice = StoryChoice(text = "Head toward the sound of water", parentPartId = "dawn"),
                    ),
                )
            )
        )
    }
}

private class StoryGenerationJourney(maxDepth: Int) {
    private val json = Json { prettyPrint = true }
    private val storiesDirectory: Path = Files.createTempDirectory("storygen-test-stories")
    private val fakeModel = FakeStoryPartsDataSource()
    private val storySaver = FileStoryDocumentsDataSource(storiesDirectory, json)
    private val storyteller = StoryGenerationRepository(
        partsDataSource = fakeModel,
        documentsDataSource = storySaver,
    )
    private val plan = StoryPlan(
        storyId = "evening-walk",
        title = "Evening Walk",
        language = StoryLanguage("en", "English"),
        maxDepth = maxDepth,
        startPartId = "dawn",
    )
    private lateinit var result: StoryGenerationResult

    suspend fun tellStory() {
        // WHEN the storyteller shares the tale
        result = storyteller.generate(plan)
    }

    fun expectDepthFeedback(expectedProgress: List<DepthProgress>) {
        // THEN the requests remind the model how close it is to the limit
        assertEquals(expectedProgress, fakeModel.recordedProgress())
    }

    fun expectSavedStory(expected: StoryDocument) {
        // AND the final story is saved in the stories directory
        val savedPath = result.savedPath
        assertTrue("Story directory does not exist", Files.exists(storiesDirectory))
        assertTrue("Story file was not created", Files.exists(savedPath))
        val saved = json.decodeFromString(StoryDocument.serializer(), savedPath.readText())
        assertEquals(expected, saved)
        assertEquals(expected, result.document)
    }
}

data class DepthProgress(val partId: String, val stepsRemaining: Int)

private class FakeStoryPartsDataSource : StoryPartsDataSource {
    private val scriptedResponses = mapOf(
        "dawn" to StoryModelResult(
            part = StoryModelPart(
                id = "dawn",
                content = listOf(StoryParagraph(sentences = listOf("A traveler watches the last light fade."))),
            ),
            choices = listOf(
                StoryModelChoice(nextPartId = "forest", text = "Follow the lantern path"),
                StoryModelChoice(nextPartId = "river", text = "Head toward the sound of water"),
            )
        ),
        "forest" to StoryModelResult(
            part = StoryModelPart(
                id = "forest",
                content = listOf(StoryParagraph(sentences = listOf("Lanterns sway between tall trees."))),
            ),
            choices = listOf(StoryModelChoice(nextPartId = "campfire", text = "Inspect the campfire"))
        ),
        "campfire" to StoryModelResult(
            part = StoryModelPart(
                id = "campfire",
                content = listOf(StoryParagraph(sentences = listOf("A quiet campfire waits for company."))),
            ),
            choices = listOf(
                StoryModelChoice(nextPartId = "hidden-hill", text = "Climb the nearby hill")
            )
        ),
        "river" to StoryModelResult(
            part = StoryModelPart(
                id = "river",
                content = listOf(StoryParagraph(sentences = listOf("Water rushes louder than the wind."))),
            ),
            choices = emptyList()
        ),
    )

    private val depthNotes = mutableListOf<DepthProgress>()

    override suspend fun generatePart(request: StoryModelRequest): StoryModelResult {
        depthNotes += DepthProgress(partId = request.targetPartId, stepsRemaining = request.stepsRemaining)
        return scriptedResponses.getValue(request.targetPartId)
    }

    fun recordedProgress(): List<DepthProgress> = depthNotes.toList()
}
