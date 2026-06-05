package input.comprehensible.features.storylist

import input.comprehensible.features.AppScenarioHolder
import input.comprehensible.features.StoryFixtures
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the story list screen, delegating Compose interaction to [StoryListRobot].
 */
class StoryListStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val storyList get() = StoryListRobot(scope.composeRule)

    @Given("the story list is open")
    fun theStoryListIsOpen() {
        scope.goToStoryList()
        scope.idle()
    }

    @When("the reader opens the settings screen")
    fun theReaderOpensTheSettingsScreen() {
        storyList.openSettings()
        scope.idle()
    }

    @When("the reader selects the {word} story")
    fun theReaderSelectsTheStory(ordinal: String) {
        storyList.selectStory(StoryFixtures.story(ordinal))
        scope.idle()
    }

    @When("the reader selects the {word} story learning {word}")
    fun theReaderSelectsTheStoryLearning(ordinal: String, language: String) {
        storyList.selectStory(StoryFixtures.story(ordinal), learningLanguage = StoryFixtures.languageCode(language))
        scope.idle()
    }

    @When("the reader scrolls to and selects the {word} story")
    fun theReaderScrollsToAndSelectsTheStory(ordinal: String) {
        storyList.findStory(StoryFixtures.stories.lastIndex)
        storyList.selectStory(StoryFixtures.story(ordinal))
        scope.idle()
    }

    @Then("the {word} story is listed")
    fun theStoryIsListed(ordinal: String) {
        storyList.assertStoryTitleIsVisible(StoryFixtures.story(ordinal).germanTitle)
    }

    @Then("the {word} story is listed in {word}")
    fun theStoryIsListedIn(ordinal: String, language: String) {
        storyList.assertStoryTitleIsVisible(
            StoryFixtures.title(StoryFixtures.story(ordinal), StoryFixtures.languageCode(language)),
        )
    }

    @Then("the {word} story is not listed")
    fun theStoryIsNotListed(ordinal: String) {
        storyList.assertStoryIsNotVisible(StoryFixtures.story(ordinal))
    }

    @Then("the {word} story image is shown in the list")
    fun theStoryImageIsShownInTheList(ordinal: String) {
        storyList.assertStoryImageIsVisible(StoryFixtures.story(ordinal).id)
    }

    @Then("the start text adventure button is hidden")
    fun theStartTextAdventureButtonIsHidden() {
        storyList.assertStartTextAdventureIsHidden()
    }
}
