package input.comprehensible.cucumber.steps

import input.comprehensible.cucumber.CucumberComprehensibleInputTestScope
import input.comprehensible.cucumber.CucumberTestScopeSingleton
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.TestStory
import input.comprehensible.features.story.StoryReaderRobot
import input.comprehensible.features.storylist.StoryListRobot
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class StoryListStepDefinitions {
    private val scope = CucumberTestScopeSingleton()
    private var stories: List<TestStory> = emptyList()

    @Given("a full story library is loaded")
    fun fullStoryLibraryLoaded() = runBlocking(scope.dispatcher) {
        stories = SampleStoriesData.listOf100Stories
        scope.setLocalStories(stories)
    }

    @Given("a full story library is loaded with text adventures disabled")
    fun fullStoryLibraryLoadedWithoutTextAdventures() = runBlocking(scope.dispatcher) {
        scope.close()
        CucumberTestScopeSingleton.inject {
            CucumberComprehensibleInputTestScope(
                composeRule = scope.composeRule,
                testScope = scope.testScope,
                dispatcher = scope.dispatcher,
                darkTheme = false,
                aiTextAdventuresEnabled = false,
            )
        }
        stories = SampleStoriesData.listOf100Stories
        CucumberTestScopeSingleton().setLocalStories(stories)
    }

    @And("the reader is on the story list screen")
    fun readerOnStoryList() = runBlocking(scope.dispatcher) {
        scope.goToStoryList()
        scope.awaitIdle()
    }

    @And("the first story has no image")
    fun firstStoryHasNoImage() = runBlocking(scope.dispatcher) {
        scope.removeImagesForStory(stories.first())
    }

    @When("the reader opens the story list screen")
    fun openStoryListScreen() = runBlocking(scope.dispatcher) {
        scope.goToStoryList()
        scope.awaitIdle()
    }

    @When("the reader opens the first story")
    fun openFirstStory() = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).selectStory(stories.first())
        scope.awaitIdle()
    }

    @When("the learning language is set to {string}")
    fun setLearningLanguage(language: String) = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).setLearningLanguage(language)
        scope.awaitIdle()
    }

    @And("the reader opens the first story in language {string}")
    fun openFirstStoryInLanguage(language: String) = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).selectStory(stories.first(), learningLanguage = language)
        scope.awaitIdle()
    }


    @When("the translation language is set to {string}")
    fun setTranslationLanguage(language: String) = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).setTranslationLanguage(language)
        scope.awaitIdle()
    }

    @Then("the learning language shown is {string}")
    fun learningLanguageShown(language: String) {
        StoryListRobot(scope.composeRule).assertLearningLanguageIs(language)
    }

    @Then("the translation language shown is {string}")
    fun translationLanguageShown(language: String) {
        StoryListRobot(scope.composeRule).assertTranslationLanguageIs(language)
    }

    @Then("the first story text is shown")
    fun firstStoryTextShown() = runBlocking(scope.dispatcher) {
        StoryReaderRobot(scope.composeRule)
            .assertStoryTextIsVisible(stories.first().paragraphs.first().germanSentences)
    }

    @Then("the first story image is visible")
    fun firstStoryImageVisible() {
        StoryListRobot(scope.composeRule).assertStoryImageIsVisible(stories.first().id)
    }

    @Then("the first story is not visible in the list")
    fun firstStoryNotVisible() {
        StoryListRobot(scope.composeRule).assertStoryIsNotVisible(stories.first())
    }

    @Then("the text adventure call to action is hidden")
    fun textAdventureHidden() {
        StoryListRobot(CucumberTestScopeSingleton().composeRule).assertStartTextAdventureIsHidden()
    }

    @Then("the first story text is shown in language {string}")
    fun firstStoryTextShownInLanguage(language: String) {
        val paragraph = stories.first().paragraphs.first()
        val expected = when (language) {
            "es" -> paragraph.spanishSentences
            "en" -> paragraph.englishSentences
            else -> paragraph.germanSentences
        }
        StoryReaderRobot(scope.composeRule).assertStoryTextIsVisible(expected)
    }
}
