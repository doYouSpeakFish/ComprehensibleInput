package input.comprehensible.features.textadventure

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions for the text adventures list feature. They delegate to the
 * [TextAdventuresListRobot] for UI interaction and to the [TextAdventureFeatureTestScope] (via
 * [TextAdventureScenarioHolder]) for scripting the account session and the fake backend.
 */
class TextAdventuresListStepDefinitions {
    private val scope get() = TextAdventureScenarioHolder.scope
    private val robot get() = TextAdventuresListRobot(scope.composeRule)

    @Given("I am signed out")
    fun iAmSignedOut() {
        scope.signOut()
    }

    @Given("I am signed in as {string}")
    fun iAmSignedInAs(email: String) {
        scope.signInAs(email)
    }

    @Given("the text adventures screen is open")
    fun theTextAdventuresScreenIsOpen() {
        scope.openScreen()
    }

    @Given("the adventures request will return no adventures")
    fun theAdventuresRequestWillReturnNoAdventures() {
        scope.returnAdventures()
    }

    @Given("the adventures request will return the {string} adventure")
    fun theAdventuresRequestWillReturnTheAdventure(title: String) {
        scope.returnAdventures(title)
    }

    @Given("the adventures request will return the {string} adventure with a cover image")
    fun theAdventuresRequestWillReturnTheAdventureWithACoverImage(title: String) {
        scope.returnAdventureWithImage(title)
    }

    @Given("adventures requests are delayed")
    fun adventuresRequestsAreDelayed() {
        scope.delayRequests()
    }

    @Given("the {string} adventure is cached for {string}")
    fun theAdventureIsCachedFor(title: String, email: String) {
        scope.cacheAdventure(title, email)
    }

    @Given("the adventures request will fail")
    fun theAdventuresRequestWillFail() {
        scope.failRefresh()
    }

    @Given("the adventures request will be rate limited")
    fun theAdventuresRequestWillBeRateLimited() {
        scope.rateLimitRefresh()
    }

    @Given("the delete adventure request will fail")
    fun theDeleteAdventureRequestWillFail() {
        scope.failDelete()
    }

    @Given("the restore adventure request will fail")
    fun theRestoreAdventureRequestWillFail() {
        scope.failRestore()
    }

    @When("I tap the text adventures sign in button")
    fun iTapTheSignInButton() {
        robot.tapSignIn()
        scope.idle()
    }

    @When("I swipe to delete the {string} adventure")
    fun iSwipeToDeleteTheAdventure(title: String) {
        robot.swipeToDeleteAdventure(title)
        scope.idle()
    }

    @When("I undo the deletion")
    fun iUndoTheDeletion() {
        robot.undoAdventureDeletion()
        // Two rounds so work scheduled by the restored row itself (not just the undo tap) also
        // completes before the next assertion.
        repeat(2) { scope.idle() }
    }

    @When("the undo message times out")
    fun theUndoMessageTimesOut() {
        scope.advanceTime(UNDO_SNACKBAR_TIMEOUT_MILLIS)
    }

    @When("I partially swipe the {string} adventure")
    fun iPartiallySwipeTheAdventure(title: String) {
        robot.swipeAdventureWithoutDeleting(title)
        scope.idle()
    }

    @When("I start a new adventure")
    fun iStartANewAdventure() {
        robot.startNewAdventure()
        scope.idle()
    }

    @When("I set the learning language to {word}")
    fun iSetTheLearningLanguageTo(language: String) {
        robot.setLearningLanguage(language)
        scope.idle()
    }

    @When("I set the translation language to {word}")
    fun iSetTheTranslationLanguageTo(language: String) {
        robot.setTranslationLanguage(language)
        scope.idle()
    }

    @Then("the text adventures sign in prompt is shown")
    fun theSignInPromptIsShown() {
        robot.assertSignInPromptIsShown()
    }

    @Then("the adventures list is hidden")
    fun theAdventuresListIsHidden() {
        robot.assertListIsHidden()
    }

    @Then("the account screen is shown")
    fun theAccountScreenIsShown() {
        robot.assertAccountScreenIsShown()
    }

    @Then("the empty adventures message is shown")
    fun theEmptyAdventuresMessageIsShown() {
        robot.assertEmptyMessageIsShown()
    }

    @Then("the {string} adventure is listed")
    fun theAdventureIsListed(title: String) {
        robot.assertAdventureIsListed(title)
    }

    @Then("the {string} adventure is not listed")
    fun theAdventureIsNotListed(title: String) {
        robot.assertAdventureIsNotListed(title)
    }

    @Then("the {string} adventure cover image is shown")
    fun theAdventureCoverImageIsShown(title: String) {
        robot.assertAdventureImageIsShown(title)
    }

    @Then("no cover image is shown for the {string} adventure")
    fun noCoverImageIsShownForTheAdventure(title: String) {
        robot.assertAdventureImageIsNotShown(title)
    }

    @Then("the adventures loading indicator is shown")
    fun theLoadingIndicatorIsShown() {
        robot.assertLoadingIndicatorIsShown()
    }

    @Then("the adventures error message is shown")
    fun theErrorMessageIsShown() {
        robot.assertErrorMessageIsShown()
    }

    @Then("the free early access notice is shown")
    fun theFreeEarlyAccessNoticeIsShown() {
        robot.assertEarlyAccessNoticeIsShown()
    }

    @Then("the system busy message is shown")
    fun theSystemBusyMessageIsShown() {
        robot.assertBusyMessageIsShown()
    }

    @Then("the text adventure chat is shown")
    fun theTextAdventureChatIsShown() {
        robot.assertChatIsShown()
    }

    @Then("the adventure deleted message is shown")
    fun theAdventureDeletedMessageIsShown() {
        robot.assertAdventureDeletedMessageIsShown()
    }

    @Then("the adventure deleted message is not shown")
    fun theAdventureDeletedMessageIsNotShown() {
        robot.assertAdventureDeletedMessageIsNotShown()
    }

    @Then("the text adventures title is shown")
    fun theTextAdventuresTitleIsShown() {
        robot.assertTitleIsShown()
    }

    @Then("the up button is shown")
    fun theUpButtonIsShown() {
        robot.assertUpButtonIsShown()
    }

    @Then("the language picker shows {word} as the learning language")
    fun theLanguagePickerShowsAsTheLearningLanguage(language: String) {
        robot.assertLearningLanguageIs(language)
    }

    @Then("the language picker shows {word} as the translation language")
    fun theLanguagePickerShowsAsTheTranslationLanguage(language: String) {
        robot.assertTranslationLanguageIs(language)
    }

    @Then("the adventure is started learning {word} with translations in {word}")
    fun theAdventureIsStartedLearningWithTranslationsIn(learning: String, translation: String) {
        scope.assertAdventureStartedWith(
            learningLanguage = languageCodeFor(learning),
            translationLanguage = languageCodeFor(translation),
        )
    }

    private fun languageCodeFor(languageName: String) = when (languageName) {
        "English" -> "en"
        "German" -> "de"
        "Spanish" -> "es"
        "French" -> "fr"
        "Portuguese" -> "pt"
        "Indonesian" -> "id"
        else -> error("Unknown language name: $languageName")
    }

    private companion object {
        // Comfortably beyond the undo snackbar's 10-second duration.
        const val UNDO_SNACKBAR_TIMEOUT_MILLIS = 11_000L
    }
}
