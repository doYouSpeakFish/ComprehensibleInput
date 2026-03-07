package input.comprehensible.features.textadventure

import android.app.Application
import android.os.Build
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import input.comprehensible.features.storylist.onStoryList
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextAdventureTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    @Test
    fun `text adventure can be played to completion`() = testRule.runTest {
        // GIVEN a text adventure with a scenario and responses
        val adventureId = "adventure-1"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Harbor Mist",
            sentences = listOf("You arrive at a quiet harbor."),
            translatedSentences = listOf("Llegas a un puerto tranquilo."),
            isEnding = false,
        )
        val responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = adventureId,
                title = "Harbor Mist",
                sentences = listOf("A lantern flickers on the dock."),
                translatedSentences = listOf("Una linterna parpadea en el muelle."),
                isEnding = false,
            ),
            TextAdventureRemoteResponse(
                adventureId = adventureId,
                title = "Harbor Mist",
                sentences = listOf("The fog lifts and the journey ends."),
                translatedSentences = listOf("La niebla se disipa y el viaje termina."),
                isEnding = true,
            ),
        )
        enqueueTextAdventure(scenario = scenario, responses = responses)

        // WHEN the reader starts a new text adventure
        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        onTextAdventure {
            // THEN the scenario is presented
            assertMessageVisible("You arrive at a quiet harbor.")

            // WHEN the reader responds
            enterResponse("I walk toward the dock.")
            sendResponse()
        }
        awaitIdle()

        onTextAdventure {
            // THEN the AI responds
            assertMessageVisible("A lantern flickers on the dock.")

            // WHEN the reader taps to translate a sentence
            tapOnSentence("A lantern flickers on the dock.")

            // THEN the translation is shown
            assertTranslationVisible("Una linterna parpadea en el muelle.")

            // WHEN the reader responds again
            enterResponse("I take the lantern.")
            sendResponse()
        }
        awaitIdle()

        onTextAdventure {
            // THEN the ending is shown
            assertMessageVisible("The fog lifts and the journey ends.")
            // AND there is no textbox for further responses
            assertInputIsHidden()
        }
    }

    @Test
    fun `unfinished adventures can be resumed`() = testRule.runTest {
        // GIVEN a text adventure with a scenario and a follow-up
        val adventureId = "adventure-2"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Forest Echoes",
            sentences = listOf("A trail winds into the forest."),
            translatedSentences = listOf("Un sendero se adentra en el bosque."),
            isEnding = false,
        )
        val responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = adventureId,
                title = "Forest Echoes",
                sentences = listOf("Birdsong follows you between the trees."),
                translatedSentences = listOf("El canto de los pájaros te sigue entre los árboles."),
                isEnding = false,
            )
        )
        enqueueTextAdventure(scenario = scenario, responses = responses)

        // WHEN the reader starts and continues the adventure
        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        onTextAdventure {
            enterResponse("I follow the trail.")
            sendResponse()
        }
        awaitIdle()

        // AND navigates back to the story list
        navigateBack()
        awaitIdle()

        onStoryList {
            // THEN the adventure appears in the list
            assertTextAdventureIsVisible("Forest Echoes")
            // WHEN the reader opens the previous adventure
            selectTextAdventure("Forest Echoes")
        }
        awaitIdle()

        onTextAdventure {
            // THEN the adventure is shown with its history
            assertMessageVisible("A trail winds into the forest.")
            assertMessageVisible("Birdsong follows you between the trees.")
            // AND the reader can keep responding
            assertInputIsVisible()
        }
    }

    @Test
    fun `error is shown when sending a message fails`() = testRule.runTest {
        // GIVEN a text adventure with a scenario
        val adventureId = "adventure-error"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Storm Coast",
            sentences = listOf("Waves crash against the cliffs."),
            translatedSentences = listOf("Las olas rompen contra los acantilados."),
            isEnding = false,
        )
        enqueueTextAdventure(scenario = scenario, responses = emptyList())

        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        onTextAdventure {
            assertMessageVisible("Waves crash against the cliffs.")

            // WHEN the remote source fails on the next response
            setTextAdventureRespondError(RuntimeException("Network error"))
            enterResponse("I climb the cliff.")
            sendResponse()
        }
        awaitIdle()

        onTextAdventure {
            // THEN an error message is shown
            assertErrorMessageVisible()
        }
    }

    @Test
    fun `retry works after a send failure`() = testRule.runTest {
        // GIVEN a text adventure with a scenario and a response
        val adventureId = "adventure-retry"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Desert Path",
            sentences = listOf("Sand stretches in every direction."),
            translatedSentences = listOf("La arena se extiende en todas direcciones."),
            isEnding = false,
        )
        val responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = adventureId,
                title = "Desert Path",
                sentences = listOf("An oasis appears ahead."),
                translatedSentences = listOf("Un oasis aparece adelante."),
                isEnding = true,
            ),
        )
        enqueueTextAdventure(scenario = scenario, responses = responses)

        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        onTextAdventure {
            assertMessageVisible("Sand stretches in every direction.")

            // WHEN the remote source fails on the next response
            setTextAdventureRespondError(RuntimeException("Network error"))
            enterResponse("I walk forward.")
            sendResponse()
        }
        awaitIdle()

        onTextAdventure {
            // THEN an error message is shown
            assertErrorMessageVisible()

            // WHEN the error is cleared and the reader taps retry
            setTextAdventureRespondError(null)
            tapRetry()
        }
        awaitIdle()

        onTextAdventure {
            // THEN the AI response is shown after retry
            assertMessageVisible("An oasis appears ahead.")
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }
}
