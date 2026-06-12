package input.comprehensible.features.story

import input.comprehensible.features.AppScenarioHolder
import input.comprehensible.features.StoryFixtures
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the branching "choose your own adventure" story: its choices and paging between parts.
 */
class StoryChoicesStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val storyReader get() = StoryReaderRobot(scope.composeRule)

    @When("the reader taps the {string} choice in {word}")
    fun theReaderTapsTheChoice(choice: String, language: String) {
        storyReader.tapOnChoiceText(StoryFixtures.adventureChoiceText(choice, StoryFixtures.languageCode(language)))
        scope.idle()
    }

    @Then("the {string} choice is shown in {word}")
    fun theChoiceIsShownIn(choice: String, language: String) {
        val text = StoryFixtures.adventureChoiceText(choice, StoryFixtures.languageCode(language))
        storyReader.waitForChoiceText(text)
        storyReader.assertChoiceIsShown(text)
    }

    @When("the reader chooses to keep the key")
    fun theReaderChoosesToKeepTheKey() {
        storyReader.chooseStoryOption("keep_key")
        scope.idle()
    }

    @When("the reader chooses to return the key")
    fun theReaderChoosesToReturnTheKey() {
        storyReader.chooseStoryOption("return_key")
        scope.idle()
    }

    @When("the reader pages back to the previous part")
    fun theReaderPagesBackToThePreviousPart() {
        storyReader.swipeToPreviousPart()
        scope.idle()
    }

    @Then("the story reader shows the {string} part of the adventure in German")
    fun theStoryReaderShowsThePart(part: String) {
        storyReader.assertStoryTextIsVisible(StoryFixtures.adventurePartGermanSentence(part))
    }
}
