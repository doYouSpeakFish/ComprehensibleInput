package input.comprehensible.cucumber.steps

import input.comprehensible.cucumber.CucumberTestScopeSingleton
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.TestStory
import input.comprehensible.features.story.StoryReaderRobot
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class StoryReaderStepDefinitions {
    private val scope = CucumberTestScopeSingleton()
    private var stories: List<TestStory> = emptyList()

    @Given("a full story library is loaded for story reader")
    fun loadStories() = runBlocking(scope.dispatcher) {
        stories = SampleStoriesData.listOf100Stories
        scope.setLocalStories(stories)
    }

    @And("story loading is delayed by {int} milliseconds")
    fun delayLoading(delay: Int) = runBlocking(scope.dispatcher) {
        scope.delayStoryLoads(delay.toLong())
    }

    @And("the first story is missing English translation")
    fun hideEnglishTranslation() = runBlocking(scope.dispatcher) {
        scope.hideTranslationForStory("en", stories.first())
    }

    @When("the reader opens the first story reader page")
    fun openFirstStory() = runBlocking(scope.dispatcher) {
        scope.goToStoryReader(stories.first().id)
        scope.awaitIdle()
    }

    @And("the reader opens the first story reader page")
    fun andOpenFirstStory() = openFirstStory()

    @When("the reader taps the first German sentence in reader")
    fun tapFirstGermanSentence() = runBlocking(scope.dispatcher) {
        val sentence = stories.first().paragraphs.first().germanSentences.first()
        StoryReaderRobot(scope.composeRule).tapOnSentence(sentence)
        scope.awaitIdle()
    }

    @Then("the first story title is shown in reader")
    fun titleShown() {
        StoryReaderRobot(scope.composeRule).assertStoryTitleIsShown(stories.first().germanTitle)
    }

    @Then("the first story German text is shown in reader")
    fun germanTextShown() {
        StoryReaderRobot(scope.composeRule)
            .assertStoryTextIsVisible(stories.first().paragraphs.first().germanSentences)
    }

    @Then("the first story English sentence is shown in reader")
    fun englishShown() {
        val english = stories.first().paragraphs.first().englishSentences.first()
        StoryReaderRobot(scope.composeRule).assertStoryTextIsVisible(english)
    }

    @Then("the story loading indicator is shown")
    fun loadingShown() {
        StoryReaderRobot(scope.composeRule).assertLoadingIndicatorIsShown()
    }

    @Then("the story error dialog is shown")
    fun errorShown() {
        StoryReaderRobot(scope.composeRule).assertErrorDialogIsShown()
    }
}
