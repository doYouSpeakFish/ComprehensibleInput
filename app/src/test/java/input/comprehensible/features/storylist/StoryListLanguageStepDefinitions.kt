package input.comprehensible.features.storylist

import input.comprehensible.features.AppScenarioHolder
import input.comprehensible.features.StoryFixtures
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the learning/translation language pickers on the story list, delegating to [StoryListRobot].
 */
class StoryListLanguageStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val storyList get() = StoryListRobot(scope.composeRule)

    @When("the reader sets the learning language to {word}")
    fun theReaderSetsTheLearningLanguageTo(language: String) {
        storyList.setLearningLanguage(StoryFixtures.languageCode(language))
        scope.idle()
    }

    @When("the reader sets the translation language to {word}")
    fun theReaderSetsTheTranslationLanguageTo(language: String) {
        storyList.setTranslationLanguage(StoryFixtures.languageCode(language))
        scope.idle()
    }

    @Then("the learning language is {word}")
    fun theLearningLanguageIs(language: String) {
        storyList.assertLearningLanguageIs(StoryFixtures.languageCode(language))
    }

    @Then("the translation language is {word}")
    fun theTranslationLanguageIs(language: String) {
        storyList.assertTranslationLanguageIs(StoryFixtures.languageCode(language))
    }
}
