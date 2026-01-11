package input.comprehensible.features.story

import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.filterParagraphs
import input.comprehensible.features.storylist.onStoryList
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalRoborazziApi::class)
class StoryReaderTests {

    @get:Rule
    val testRule = ComprehensibleInputTestRule()

    @Test
    fun `story reader screenshot`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            assertStoryTitleIsShown(stories.first().germanTitle)
            assertStoryTextIsVisible(stories.first().paragraphs.first().germanSentences)
            assertImageIsShown(stories.first().images.first())
        }
    }

    @Test
    fun `story reader error screenshot`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        hideTranslationForStory(languageCode = "en", story = story)

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `story title is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryReader(stories.first().id)
        runCurrent()

        onStoryReader {
            assertStoryTitleIsShown(stories.first().germanTitle)
        }
    }

    @Test
    fun `story content is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryReader(stories.last().id)
        runCurrent()

        onStoryReader {
            assertStoryTextIsVisible(stories.last().paragraphs.first().germanSentences)
        }
    }

    @Test
    fun `first image for story is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        val story = stories.first()
        goToStoryReader(story.id)
        runCurrent()

        onStoryReader {
            assertImageIsShown(story.images.first())
        }
    }

    @Test
    fun `the story can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        goToStoryReader(stories.first().id)
        runCurrent()
        awaitIdle()

        onStoryReader {
            val paragraph = stories.first().paragraphs.first()
            val germanSentence = paragraph.germanSentences.first()
            val englishSentence = paragraph.englishSentences.first()

            tapOnSentence(sentence = germanSentence)
            awaitIdle()

            assertStoryTextIsVisible(sentence = englishSentence)
        }
    }

    @Test
    fun `the story can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            val paragraph = stories.first().paragraphs.first()
            val germanSentence = paragraph.germanSentences.first()
            val englishSentence = paragraph.englishSentences.first()

            tapOnSentence(sentence = germanSentence)
            awaitIdle()
            tapOnSentence(sentence = englishSentence)
            awaitIdle()

            assertStoryTextIsVisible(sentence = germanSentence)
        }
    }

    @Test
    fun `the story choices can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story with choices is open
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val germanChoice = story.parts
                .first { it.choice?.parentPartId == "start" }
                .choice!!
                .textByLanguage.getValue("de")
            val englishChoice = story.parts
                .first { it.choice?.parentPartId == "start" }
                .choice!!
                .textByLanguage.getValue("en")

            tapOnChoiceText(text = germanChoice)
            awaitIdle()

            waitForChoiceText(text = englishChoice)
            assertChoiceIsShown(englishChoice)
        }
    }

    @Test
    fun `the story choices can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story with choices is open
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val choice = story.parts.first { it.choice?.parentPartId == "start" }.choice!!
            val germanChoice = choice.textByLanguage.getValue("de")
            val englishChoice = choice.textByLanguage.getValue("en")

            tapOnChoiceText(text = germanChoice)
            awaitIdle()
            waitForChoiceText(text = englishChoice)
            tapOnChoiceText(text = englishChoice)
            awaitIdle()

            waitForChoiceText(text = germanChoice)
            assertChoiceIsShown(germanChoice)
        }
    }

    @Test
    fun `choosing a path opens the next part on a new page`() = testRule.runTest {
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        setLocalStories(listOf(story))

        val startSentence = story.parts.first().content.filterParagraphs().first().germanSentences.first()
        val keepChoicePart = story.getPart("keep_key")
        val keepChoiceId = keepChoicePart.id
        val newPathSentence = keepChoicePart.content.filterParagraphs().first().germanSentences.first()

        // GIVEN the first part of the story is visible
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            assertStoryTextIsVisible(startSentence)

            // WHEN the reader chooses the first option
            chooseStoryOption(keepChoiceId)
            awaitIdle()

            // THEN the chosen option and the next part are shown at the top of the new page
            assertStoryTextIsVisible(newPathSentence)
        }
    }

    @Test
    fun `choices stay available when paging backwards`() = testRule.runTest {
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        setLocalStories(listOf(story))

        val keepChoicePart = story.getPart("keep_key")
        val keepChoiceId = keepChoicePart.id
        val keepChoiceText = keepChoicePart.choice!!.textByLanguage.getValue("de")
        val returnChoicePart = story.getPart("return_key")
        val returnChoiceId = returnChoicePart.id
        val returnChoiceText = returnChoicePart.choice!!.textByLanguage.getValue("de")
        val returnPathSentence = story.getPart("return_key").content.filterParagraphs().first()
            .germanSentences.first()

        // GIVEN the user has followed one path
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            chooseStoryOption(keepChoiceId)
            awaitIdle()

            // WHEN they page back to the earlier part
            swipeToPreviousPart()
            awaitIdle()

            // THEN the choices are still available
            assertChoiceIsShown(keepChoiceText)
            assertChoiceIsShown(returnChoiceText)

            // AND a different choice can be selected
            chooseStoryOption(returnChoiceId)
            awaitIdle()
            assertStoryTextIsVisible(returnPathSentence)
        }
    }

    @Test
    fun `the title can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()

            assertStoryTitleIsShown(stories.first().englishTitle)
        }
    }

    @Test
    fun `the title can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()
            tapOnSentence(sentence = stories.first().englishTitle)
            awaitIdle()

            assertStoryTitleIsShown(stories.first().germanTitle)
        }
    }

    @Test
    fun `Partially read story opens at correct position`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        val sentence = story.paragraphs[3].germanSentences[9]

        // GIVEN a story is open
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // AND half of the story is read
            skipToSentence(sentence = sentence)
            awaitIdle()
            // AND the story is closed
            navigateBack()
            awaitIdle()
        }

        // WHEN the story is re-opened
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the midpoint of the story is shown
            assertStoryTextIsVisible(sentence = sentence)
        }
    }

    @Test
    fun `the chosen path is remembered`() = testRule.runTest {
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        setLocalStories(listOf(story))

        val startSentence = story.parts.first().content.filterParagraphs().first().germanSentences.first()
        val keepChoicePart = story.getPart("keep_key")
        val keepChoiceId = keepChoicePart.id
        val newPathSentence = keepChoicePart.content.filterParagraphs().first().germanSentences.first()

        // GIVEN a story with a decision point
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // WHEN the reader chooses to keep the key
            assertStoryTextIsVisible(startSentence)
            chooseStoryOption(keepChoiceId)
            awaitIdle()

            // THEN the next part is shown
            assertStoryTextIsVisible(newPathSentence)
        }

        // AND the story is closed
        navigateBack()
        awaitIdle()

        // WHEN the story is opened again
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN it resumes on the chosen path
            assertStoryTextIsVisible(newPathSentence)

            // AND the original part can still be reached by paging backwards
            swipeToPreviousPart()
            awaitIdle()
            assertStoryTextIsVisible(startSentence)
        }
    }

    @Test
    fun `story shows loading indicator while story loads`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        delayStoryLoads(delayMillis = 1_000L)

        // WHEN the story reader is opened
        goToStoryReader(stories.first().id)

        onStoryReader {
            // THEN a loading indicator is displayed
            assertLoadingIndicatorIsShown()
        }
    }

    @Test
    fun `story shows error when learning story is missing`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        hideStoryForLanguage(languageCode = "de", story = story)

        // GIVEN the user is reading in German with English translations
        goToStoryList()
        awaitIdle()
        onStoryList {
            setLearningLanguage("de")
            setTranslationLanguage("en")
        }

        // WHEN the story reader is opened
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown and can be dismissed
            assertErrorDialogIsShown()
            dismissErrorDialog()
        }
        awaitIdle()

        onStoryList {
            assertLearningLanguageIs("de")
        }
    }

    @Test
    fun `story shows error when translation is missing`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        hideTranslationForStory(languageCode = "en", story = story)

        // GIVEN a story with a missing translation
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `story shows error when translation sentences mismatch`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        mismatchTranslationForStory(languageCode = "en", story = story)

        // GIVEN a story with mismatched translation content
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `story shows error when an image cannot be loaded`() = testRule.runTest {
        // GIVEN a story where images fail to load
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val story = stories.first()
        removeImagesForStory(story = story)

        // WHEN the story reader is opened
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN an error dialog is shown
            assertErrorDialogIsShown()
        }
    }
}
