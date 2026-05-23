package input.comprehensible.cucumber.steps

import input.comprehensible.cucumber.CucumberTestScopeSingleton
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import input.comprehensible.features.storylist.StoryListRobot
import input.comprehensible.features.textadventure.TextAdventureRobot
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class TextAdventureStepDefinitions {
    private val scope = CucumberTestScopeSingleton()

    @Given("a text adventure scenario {string} titled {string} starts with {string} and translation {string}")
    fun adventureScenarioStarts(id: String, title: String, sentence: String, translation: String) = runBlocking(scope.dispatcher) {
        scope.enqueueTextAdventure(
            scenario = TextAdventureRemoteResponse(
                adventureId = id,
                title = title,
                sentences = listOf(sentence),
                translatedSentences = listOf(translation),
                isEnding = false,
            ),
            responses = emptyList(),
        )
    }

    @And("the text adventure response for {string} is {string} with translation {string} and ending {word}")
    fun addAdventureResponse(id: String, sentence: String, translation: String, ending: String) = runBlocking(scope.dispatcher) {
        // append by re-enqueueing from fake data helper behavior
        scope.enqueueTextAdventure(
            scenario = TextAdventureRemoteResponse(
                adventureId = id,
                title = "",
                sentences = emptyList(),
                translatedSentences = emptyList(),
                isEnding = false,
            ),
            responses = listOf(
                TextAdventureRemoteResponse(
                    adventureId = id,
                    title = "",
                    sentences = listOf(sentence),
                    translatedSentences = listOf(translation),
                    isEnding = ending.toBooleanStrict(),
                )
            ),
        )
    }

    @And("the reader starts a text adventure")
    fun startTextAdventure() = runBlocking(scope.dispatcher) {
        scope.setLocalStories(SampleStoriesData.listOf100Stories)
        scope.goToStoryList()
        scope.awaitIdle()
        StoryListRobot(scope.composeRule).startTextAdventure()
        scope.awaitIdle()
    }

    @When("the reader responds with {string}")
    fun readerResponds(text: String) = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).apply {
            enterResponse(text)
            sendResponse()
        }
        scope.awaitIdle()
    }

    @When("the reader translates sentence {string}")
    fun readerTranslates(sentence: String) = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).tapOnSentence(sentence)
        scope.awaitIdle()
    }

    @Then("the text adventure message {string} is shown")
    fun messageShown(message: String) = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).assertMessageVisible(message)
    }

    @Then("the text adventure translation {string} is shown")
    fun translationShown(text: String) = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).assertTranslationVisible(text)
    }

    @Then("the text adventure input is hidden")
    fun inputHidden() = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).assertInputIsHidden()
    }

    @And("the reader returns to the story list")
    fun backToStoryList() = runBlocking(scope.dispatcher) {
        scope.navigateBack()
        scope.awaitIdle()
    }

    @Then("the text adventure {string} is visible in the list")
    fun adventureVisible(title: String) = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).assertTextAdventureIsVisible(title)
    }

    @When("the reader opens text adventure {string}")
    fun openAdventure(title: String) = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).selectTextAdventure(title)
        scope.awaitIdle()
    }

    @Then("the text adventure input is visible")
    fun inputVisible() = runBlocking(scope.dispatcher) {
        TextAdventureRobot(scope.composeRule).assertInputIsVisible()
    }
}
