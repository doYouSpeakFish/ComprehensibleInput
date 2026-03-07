package input.comprehensible.features.textadventure

import android.app.Application
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
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
    fun `loading spinner is shown while starting a new adventure`() = testRule.runTest {
        // GIVEN a text adventure with a delayed response
        val adventureId = "adventure-3"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Ocean Depths",
            sentences = listOf("Waves crash against the shore."),
            translatedSentences = listOf("Las olas rompen contra la orilla."),
            isEnding = false,
        )
        enqueueTextAdventure(scenario = scenario, responses = emptyList())
        val gate = holdTextAdventureResponses()

        // WHEN the reader starts a new text adventure
        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        // THEN the adventure screen shows a loading indicator
        composeRule.onNodeWithTag("text_adventure_loading").assertIsDisplayed()

        // WHEN the AI response arrives
        gate.complete(Unit)
        awaitIdle()

        onTextAdventure {
            // THEN the scenario is presented and loading is gone
            assertMessageVisible("Waves crash against the shore.")
            assertMessageLoadingIsHidden()
        }
    }

    @Test
    fun `loading spinner is shown while waiting for AI response`() = testRule.runTest {
        // GIVEN a text adventure that is already loaded
        val adventureId = "adventure-4"
        val scenario = TextAdventureRemoteResponse(
            adventureId = adventureId,
            title = "Desert Sun",
            sentences = listOf("Sand stretches to the horizon."),
            translatedSentences = listOf("La arena se extiende hasta el horizonte."),
            isEnding = false,
        )
        val responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = adventureId,
                title = "Desert Sun",
                sentences = listOf("A mirage appears in the distance."),
                translatedSentences = listOf("Un espejismo aparece en la distancia."),
                isEnding = false,
            ),
        )
        enqueueTextAdventure(scenario = scenario, responses = responses)

        goToStoryList()
        awaitIdle()

        onStoryList {
            startTextAdventure()
        }
        awaitIdle()

        // WHEN the reader sends a response while the AI is delayed
        val gate = holdTextAdventureResponses()
        onTextAdventure {
            enterResponse("I walk toward it.")
            sendResponse()
        }
        awaitIdle()

        onTextAdventure {
            // THEN the loading spinner is shown as a message
            assertMessageLoadingIsVisible()
            // AND the input is hidden while waiting
            assertInputIsHidden()
        }

        // WHEN the AI response arrives
        gate.complete(Unit)
        awaitIdle()

        onTextAdventure {
            // THEN the response is shown and loading is gone
            assertMessageVisible("A mirage appears in the distance.")
            assertMessageLoadingIsHidden()
            // AND the input is visible again
            assertInputIsVisible()
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }
}
