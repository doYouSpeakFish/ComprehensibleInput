package input.comprehensible.features.textadventure

import input.comprehensible.features.AppScenarioHolder
import input.comprehensible.features.storylist.StoryListRobot
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the text adventure flow, delegating Compose interaction to [TextAdventureRobot] (and
 * [StoryListRobot] for starting and resuming adventures from the list).
 */
class TextAdventureStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val storyList get() = StoryListRobot(scope.composeRule)
    private val textAdventure get() = TextAdventureRobot(scope.composeRule)

    @Given("the {string} text adventure is available")
    fun theTextAdventureIsAvailable(name: String) {
        val adventure = TextAdventureFixtures.byName(name)
        scope.enqueueTextAdventure(scenario = adventure.scenario, responses = adventure.responses)
    }

    @When("the reader starts a new text adventure")
    fun theReaderStartsANewTextAdventure() {
        storyList.startTextAdventure()
        scope.idle()
    }

    @When("the reader responds with {string}")
    fun theReaderRespondsWith(text: String) {
        textAdventure.enterResponse(text)
        textAdventure.sendResponse()
        scope.idle()
    }

    @When("the reader taps {string} to translate it")
    fun theReaderTapsToTranslate(sentence: String) {
        textAdventure.tapOnSentence(sentence)
        scope.idle()
    }

    @When("the reader returns to the story list")
    fun theReaderReturnsToTheStoryList() {
        scope.navigateBack()
        scope.idle()
    }

    @When("the reader resumes the {string} text adventure")
    fun theReaderResumesTheTextAdventure(title: String) {
        storyList.selectTextAdventure(title)
        scope.idle()
    }

    @Then("the text adventure shows {string}")
    fun theTextAdventureShows(text: String) {
        textAdventure.assertMessageVisible(text)
    }

    @Then("the {string} text adventure is listed")
    fun theTextAdventureIsListed(title: String) {
        storyList.assertTextAdventureIsVisible(title)
    }

    @Then("the text adventure input is hidden")
    fun theTextAdventureInputIsHidden() {
        textAdventure.assertInputIsHidden()
    }

    @Then("the text adventure input is visible")
    fun theTextAdventureInputIsVisible() {
        textAdventure.assertInputIsVisible()
    }
}
