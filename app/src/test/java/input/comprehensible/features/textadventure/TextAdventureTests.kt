package input.comprehensible.features.textadventure

import android.app.Application
import android.os.Build
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.data.textadventure.sources.remote.TextAdventureRemoteResponse
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
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextAdventureTests {
    @get:Rule
    val testRule = ComprehensibleInputTestRule()

    @Test
    fun `text adventure supports conversation and translations`() = testRule.runTest {
        // GIVEN a text adventure with a scenario and an ending response
        val scenario = TextAdventureRemoteResponse(
            adventureId = "text-adventure",
            sentences = listOf("You stand at the edge of a forest."),
            translatedSentences = listOf("Estás al borde de un bosque."),
            isEnding = false,
        )
        val endingResponse = TextAdventureRemoteResponse(
            adventureId = "text-adventure",
            sentences = listOf("A guide appears and the adventure ends."),
            translatedSentences = listOf("Aparece un guía y la aventura termina."),
            isEnding = true,
        )
        setTextAdventureScenario(scenario)
        setTextAdventureResponses(listOf(endingResponse))

        // WHEN the text adventure screen is opened
        goToTextAdventure()
        awaitIdle()

        onTextAdventure {
            // THEN the scenario is presented
            assertScenarioIsShown(scenario.sentences.first())

            // WHEN the user taps on the scenario sentence
            tapOnSentence(scenario.sentences.first())
            awaitIdle()

            // THEN the translation is shown
            assertTranslationIsShown(scenario.translatedSentences.first())

            // WHEN the user responds to the adventure
            enterResponse("I walk into the forest.")
            sendResponse()
            awaitIdle()

            // THEN the user response and AI reply are shown
            assertUserResponseIsShown("I walk into the forest.")
            assertAiResponseIsShown(endingResponse.sentences.first())

            // AND the input is hidden after the ending
            assertInputIsHidden()
        }
    }
}
