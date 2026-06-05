package input.comprehensible.features.story

import input.comprehensible.features.AppScenarioHolder
import input.comprehensible.features.StoryFixtures
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the story reader, delegating Compose interaction to [StoryReaderRobot]. Choices and paging
 * for the branching story live in [StoryChoicesStepDefinitions].
 */
class StoryReaderStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val storyReader get() = StoryReaderRobot(scope.composeRule)

    @Given("the {word} story is open in the reader")
    fun theStoryIsOpenInTheReader(ordinal: String) {
        scope.goToStoryReader(StoryFixtures.story(ordinal).id)
        scope.idle()
    }

    @Given("the choose your own adventure story is open in the reader")
    fun theChooseYourOwnAdventureStoryIsOpenInTheReader() {
        scope.goToStoryReader(StoryFixtures.chooseYourOwnAdventureStory.id)
        scope.idle()
    }

    @Then("the story reader shows the {word} story content in {word}")
    fun theStoryReaderShowsContent(ordinal: String, language: String) {
        storyReader.assertStoryTextIsVisible(
            StoryFixtures.firstParagraphSentences(StoryFixtures.story(ordinal), StoryFixtures.languageCode(language)),
        )
    }

    @Then("the story reader shows the {word} story title in {word}")
    fun theStoryReaderShowsTitle(ordinal: String, language: String) {
        storyReader.assertStoryTitleIsShown(
            StoryFixtures.title(StoryFixtures.story(ordinal), StoryFixtures.languageCode(language)),
        )
    }

    @Then("the {word} story image is shown in the reader")
    fun theStoryImageIsShownInTheReader(ordinal: String) {
        storyReader.assertImageIsShown(StoryFixtures.story(ordinal).images.first())
    }

    @When("the reader taps the first {word} sentence of the {word} story")
    fun theReaderTapsTheFirstSentence(language: String, ordinal: String) {
        val sentences = StoryFixtures.firstParagraphSentences(StoryFixtures.story(ordinal), StoryFixtures.languageCode(language))
        storyReader.tapOnSentence(sentences.first())
        scope.idle()
    }

    @When("the reader taps the {word} story title in {word}")
    fun theReaderTapsTheStoryTitle(ordinal: String, language: String) {
        storyReader.tapOnSentence(StoryFixtures.title(StoryFixtures.story(ordinal), StoryFixtures.languageCode(language)))
        scope.idle()
    }

    @Then("the story reader shows an error")
    fun theStoryReaderShowsAnError() {
        storyReader.assertErrorDialogIsShown()
    }

    @When("the reader dismisses the story error")
    fun theReaderDismissesTheStoryError() {
        storyReader.dismissErrorDialog()
        scope.idle()
    }

    @Then("the story reader shows a loading indicator")
    fun theStoryReaderShowsALoadingIndicator() {
        storyReader.assertLoadingIndicatorIsShown()
    }

    @When("the reader closes the story")
    fun theReaderClosesTheStory() {
        scope.navigateBack()
        scope.idle()
    }

    @When("the reader skips ahead in the {word} story")
    fun theReaderSkipsAheadInTheStory(ordinal: String) {
        storyReader.skipToSentence(StoryFixtures.savedPositionSentence(StoryFixtures.story(ordinal)))
        scope.idle()
    }

    @Then("the story reader shows the saved position in the {word} story")
    fun theStoryReaderShowsTheSavedPosition(ordinal: String) {
        storyReader.assertStoryTextIsVisible(StoryFixtures.savedPositionSentence(StoryFixtures.story(ordinal)))
    }
}
