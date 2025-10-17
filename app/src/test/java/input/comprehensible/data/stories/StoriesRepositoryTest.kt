package input.comprehensible.data.stories

import android.graphics.Bitmap
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
class StoriesRepositoryTest {

    private lateinit var storiesLocalDataSource: RecordingStoriesLocalDataSource
    private lateinit var storiesInfoLocalDataSource: RecordingStoriesInfoLocalDataSource
    private lateinit var repository: StoriesRepository

    @Before
    fun setUp() {
        storiesLocalDataSource = RecordingStoriesLocalDataSource()
        storiesInfoLocalDataSource = RecordingStoriesInfoLocalDataSource()
        repository = StoriesRepository(
            storiesLocalDataSource = storiesLocalDataSource,
            storiesInfoLocalDataSource = storiesInfoLocalDataSource,
        )
    }

    @Test
    fun storyListOnlyIncludesEntriesThatHaveTranslations() = runTest {
        storiesLocalDataSource.storeStories(
            language = "English",
            stories = listOf(
                StoryData(
                    id = "1",
                    title = "Morning Greeting",
                    content = listOf(
                        StoryElementData.ImageData(
                            contentDescription = "A friendly wave",
                            path = "wave.png",
                        ),
                    ),
                ),
                StoryData(
                    id = "2",
                    title = "A Walk in the Park",
                    content = listOf(
                        StoryElementData.ImageData(
                            contentDescription = "Two people strolling",
                            path = "walk.png",
                        ),
                        StoryElementData.ParagraphData(
                            sentences = listOf("It was a bright afternoon."),
                        ),
                    ),
                ),
            ),
        )
        storiesLocalDataSource.storeStories(
            language = "Spanish",
            stories = listOf(
                StoryData(
                    id = "2",
                    title = "Un paseo por el parque",
                    content = emptyList(),
                ),
            ),
        )

        val storiesList = repository.storiesList(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        assertEquals(1, storiesList.stories.size)
        val item = storiesList.stories.single()
        assertEquals("2", item.id)
        assertEquals("A Walk in the Park", item.title)
        assertEquals("Un paseo por el parque", item.titleTranslated)
        assertEquals("Two people strolling", item.featuredImageContentDescription)
        val expectedImage = storiesLocalDataSource.imageFor(
            storyId = "2",
            path = "walk.png",
        )
        assertSame(expectedImage, item.featuredImage)
    }

    @Test
    fun readingAStoryReturnsItsContentWithTranslations() = runTest {
        storiesInfoLocalDataSource.stories["walk"] = StoryEntity(
            id = "walk",
            position = 3,
        )
        storiesLocalDataSource.storeStories(
            language = "English",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "A Walk in the Park",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("It was a bright afternoon."),
                        ),
                        StoryElementData.ImageData(
                            contentDescription = "Two people strolling",
                            path = "walk.png",
                        ),
                    ),
                ),
            ),
        )
        storiesLocalDataSource.storeStories(
            language = "Spanish",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "Un paseo por el parque",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("Era una tarde luminosa."),
                        ),
                        StoryElementData.ImageData(
                            contentDescription = "Dos personas paseando",
                            path = "walk.png",
                        ),
                    ),
                ),
            ),
        )

        val story = repository.getStory(
            id = "walk",
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        assertNotNull(story)
        val detailedStory = story!!
        assertEquals("walk", detailedStory.id)
        assertEquals("A Walk in the Park", detailedStory.title)
        assertEquals("Un paseo por el parque", detailedStory.translatedTitle)
        assertEquals(3, detailedStory.currentStoryElementIndex)

        val paragraphElement = detailedStory.content[0]
        assertTrue(paragraphElement is StoryElement.Paragraph)
        val paragraph = paragraphElement as StoryElement.Paragraph
        assertEquals(listOf("It was a bright afternoon."), paragraph.sentences)
        assertEquals(listOf("Era una tarde luminosa."), paragraph.sentencesTranslations)

        val imageElement = detailedStory.content[1]
        assertTrue(imageElement is StoryElement.Image)
        val image = imageElement as StoryElement.Image
        val expectedBitmap = storiesLocalDataSource.imageFor(
            storyId = "walk",
            path = "walk.png",
        )
        assertSame(expectedBitmap, image.bitmap)
        assertEquals("Two people strolling", image.contentDescription)
    }

    @Test
    fun firstTimeReadingAStoryStoresInitialProgress() = runTest {
        storiesLocalDataSource.storeStories(
            language = "English",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "A Walk in the Park",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("It was a bright afternoon."),
                        ),
                    ),
                ),
            ),
        )
        storiesLocalDataSource.storeStories(
            language = "Spanish",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "Un paseo por el parque",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("Era una tarde luminosa."),
                        ),
                    ),
                ),
            ),
        )

        val story = repository.getStory(
            id = "walk",
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        assertNotNull(story)
        assertEquals(
            listOf(StoryEntity(id = "walk", position = 0)),
            storiesInfoLocalDataSource.inserted,
        )
    }

    @Test
    fun readingStoryWithMissingTranslationReturnsNoStory() = runTest {
        storiesLocalDataSource.storeStories(
            language = "English",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "A Walk in the Park",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("It was a bright afternoon."),
                        ),
                    ),
                ),
            ),
        )

        val story = repository.getStory(
            id = "walk",
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        assertNull(story)
    }

    @Test
    fun readingStoryWithMismatchedParagraphsReturnsNoStory() = runTest {
        storiesLocalDataSource.storeStories(
            language = "English",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "A Walk in the Park",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf("It was a bright afternoon."),
                        ),
                    ),
                ),
            ),
        )
        storiesLocalDataSource.storeStories(
            language = "Spanish",
            stories = listOf(
                StoryData(
                    id = "walk",
                    title = "Un paseo por el parque",
                    content = listOf(
                        StoryElementData.ParagraphData(
                            sentences = listOf(
                                "Era una tarde luminosa.",
                                "Había música en el aire.",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val story = repository.getStory(
            id = "walk",
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        assertNull(story)
    }

    @Test
    fun updatingStoryProgressSavesTheNewPosition() = runTest {
        repository.updateStoryPosition(id = "walk", position = 5)

        assertEquals(
            listOf(StoryEntity(id = "walk", position = 5)),
            storiesInfoLocalDataSource.updated,
        )
    }

    private class RecordingStoriesLocalDataSource : StoriesLocalDataSource {
        private val storiesByLanguage = mutableMapOf<String, MutableMap<String, StoryData>>()
        private val imagesByStory = mutableMapOf<Pair<String, String>, Bitmap>()

        fun storeStories(language: String, stories: List<StoryData>) {
            storiesByLanguage.getOrPut(language) { mutableMapOf() }.apply {
                clear()
                stories.forEach { story ->
                    put(story.id, story)
                    story.content.forEach { element ->
                        if (element is StoryElementData.ImageData) {
                            imagesByStory[story.id to element.path] = createBitmap()
                        }
                    }
                }
            }
        }

        fun imageFor(storyId: String, path: String): Bitmap =
            requireNotNull(imagesByStory[storyId to path])

        override suspend fun getStory(id: String, language: String): StoryData? =
            storiesByLanguage[language]?.get(id)

        override suspend fun getStories(learningLanguage: String): List<StoryData> =
            storiesByLanguage[learningLanguage]?.values?.toList().orEmpty()

        override suspend fun loadStoryImage(storyId: String, path: String): Bitmap =
            imageFor(storyId = storyId, path = path)

        private fun createBitmap(): Bitmap = Bitmap::class.java.getDeclaredConstructor()
            .apply { isAccessible = true }
            .newInstance()
    }

    private class RecordingStoriesInfoLocalDataSource : StoriesInfoLocalDataSource {
        val stories = mutableMapOf<String, StoryEntity>()
        val inserted = mutableListOf<StoryEntity>()
        val updated = mutableListOf<StoryEntity>()

        override suspend fun insertStory(story: StoryEntity) {
            inserted += story
            stories[story.id] = story
        }

        override suspend fun getStory(id: String): StoryEntity? = stories[id]

        override suspend fun updateStory(story: StoryEntity) {
            updated += story
            stories[story.id] = story
        }
    }
}
