package input.comprehensible.features

import io.cucumber.java.en.Given

/**
 * Step definitions shared across app screens: seeding the story library the screens read, and the
 * data variations (missing images/translations, delays) that drive the edge-case scenarios.
 */
class AppCommonStepDefinitions {
    private val scope get() = AppScenarioHolder.scope

    @Given("the story library is available")
    fun theStoryLibraryIsAvailable() {
        scope.setLocalStories(StoryFixtures.stories)
    }

    @Given("the first two stories are available")
    fun theFirstTwoStoriesAreAvailable() {
        scope.setLocalStories(StoryFixtures.stories.take(2))
    }

    @Given("the choose your own adventure story is available")
    fun theChooseYourOwnAdventureStoryIsAvailable() {
        scope.setLocalStories(listOf(StoryFixtures.chooseYourOwnAdventureStory))
    }

    @Given("the {word} story has no image")
    fun theStoryHasNoImage(ordinal: String) {
        scope.removeImagesForStory(StoryFixtures.story(ordinal))
    }

    @Given("the {word} story has no English translation")
    fun theStoryHasNoEnglishTranslation(ordinal: String) {
        scope.hideTranslationForStory(languageCode = "en", story = StoryFixtures.story(ordinal))
    }

    @Given("the {word} story has no German learning content")
    fun theStoryHasNoGermanLearningContent(ordinal: String) {
        scope.hideStoryForLanguage(languageCode = "de", story = StoryFixtures.story(ordinal))
    }

    @Given("the {word} story has a mismatched English translation")
    fun theStoryHasAMismatchedEnglishTranslation(ordinal: String) {
        scope.mismatchTranslationForStory(languageCode = "en", story = StoryFixtures.story(ordinal))
    }

    @Given("story loads are delayed")
    fun storyLoadsAreDelayed() {
        scope.delayStoryLoads(IN_FLIGHT_DELAY_MILLIS)
    }

    private companion object {
        const val IN_FLIGHT_DELAY_MILLIS = 1_000L
    }
}
