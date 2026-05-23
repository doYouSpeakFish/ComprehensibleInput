package input.comprehensible.cucumber.steps

import input.comprehensible.cucumber.CucumberTestScopeSingleton
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.features.settings.SettingsRobot
import input.comprehensible.features.softwarelicences.SoftwareLicencesRobot
import input.comprehensible.features.storylist.StoryListRobot
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class SettingsStepDefinitions {
    private val scope = CucumberTestScopeSingleton()

    @Given("a reader is on the story list")
    fun aReaderIsOnTheStoryList() = runBlocking(scope.dispatcher) {
        scope.setLocalStories(SampleStoriesData.listOf100Stories)
        scope.goToStoryList()
        scope.awaitIdle()

        StoryListRobot(scope.composeRule).assertLearningLanguageIs("de")
    }

    @When("the reader opens settings")
    fun theReaderOpensSettings() = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).openSettings()
        scope.awaitIdle()
    }

    @When("the reader opens software licences")
    fun theReaderOpensSoftwareLicences() = runBlocking(scope.dispatcher) {
        SettingsRobot(scope.composeRule).openSoftwareLicences()
        scope.awaitIdle()
    }

    @When("the reader navigates back")
    fun theReaderNavigatesBack() = runBlocking(scope.dispatcher) {
        SettingsRobot(scope.composeRule).navigateBackToStories()
        scope.awaitIdle()
    }

    @Then("settings show the software licences option")
    fun settingsShowTheSoftwareLicencesOption() = runBlocking(scope.dispatcher) {
        SettingsRobot(scope.composeRule).apply {
            assertSettingsTitleIsVisible()
            assertSoftwareLicencesOptionIsVisible()
        }
    }

    @Then("software licences screen is shown")
    fun softwareLicencesScreenIsShown() = runBlocking(scope.dispatcher) {
        SoftwareLicencesRobot(scope.composeRule).assertSoftwareLicencesTitleIsVisible()
    }

    @Then("the story list is shown again")
    fun theStoryListIsShownAgain() = runBlocking(scope.dispatcher) {
        StoryListRobot(scope.composeRule).assertLearningLanguageIs("de")
    }
}
