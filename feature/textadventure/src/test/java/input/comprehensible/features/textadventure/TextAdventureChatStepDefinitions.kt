package input.comprehensible.features.textadventure

import input.comprehensible.ui.textadventure.PLACEHOLDER_CYCLE_MILLIS
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions for the text adventure chat feature, delegating to the [TextAdventureChatRobot]
 * (and the list robot, to open an adventure) and to the [TextAdventureFeatureTestScope] for
 * scripting the fake backend.
 */
class TextAdventureChatStepDefinitions {
    private val scope get() = TextAdventureScenarioHolder.scope
    private val chat get() = TextAdventureChatRobot(scope.composeRule)
    private val list get() = TextAdventuresListRobot(scope.composeRule)

    @Given("the start adventure request is delayed")
    fun theStartAdventureRequestIsDelayed() {
        scope.delayRequests()
    }

    @Given("messages requests are delayed")
    fun messagesRequestsAreDelayed() {
        scope.delayRequests()
    }

    @Given("starting an adventure returns {string}")
    fun startingAnAdventureReturns(text: String) {
        scope.startReturns(text, "$text (translated)")
    }

    @Given("starting an adventure returns {string} translated as {string}")
    fun startingAnAdventureReturnsTranslatedAs(text: String, translation: String) {
        scope.startReturns(text, translation)
    }

    @Given("the start adventure request will fail")
    fun theStartAdventureRequestWillFail() {
        scope.failStart()
    }

    @Given("the {string} adventure is cached with message {string}")
    fun theAdventureIsCachedWithMessage(title: String, message: String) {
        scope.cacheAdventureWithMessage(title, message)
    }

    @When("I open the {string} adventure")
    fun iOpenTheAdventure(title: String) {
        list.openAdventure(title)
        scope.idle()
    }

    @When("I tap the retry button")
    fun iTapTheRetryButton() {
        // Delay the retried request so its in-flight (generating) state is observable.
        scope.delayRequests()
        chat.tapRetry()
        scope.idle()
    }

    @When("I tap {string} to translate it")
    fun iTapToTranslateIt(text: String) {
        chat.tapSentence(text)
        scope.idle()
    }

    @Then("a generating message placeholder is shown")
    fun aGeneratingMessagePlaceholderIsShown() {
        chat.assertPlaceholderIsShown()
    }

    @Then("the generating message placeholder is hidden")
    fun theGeneratingMessagePlaceholderIsHidden() {
        chat.assertPlaceholderIsHidden()
    }

    @Then("the generating message placeholder cycles through phrases")
    fun theGeneratingMessagePlaceholderCyclesThroughPhrases() {
        val first = chat.placeholderText()
        scope.advanceTime(PLACEHOLDER_CYCLE_MILLIS)
        val second = chat.placeholderText()
        check(first != second) { "Placeholder did not cycle through phrases: still '$first'" }
    }

    @Then("the text adventure shows {string}")
    fun theTextAdventureShows(text: String) {
        chat.assertShowsText(text)
    }

    @Then("the generation error message is shown")
    fun theGenerationErrorMessageIsShown() {
        chat.assertErrorIsShown()
    }

    @Then("the generation error message is hidden")
    fun theGenerationErrorMessageIsHidden() {
        chat.assertErrorIsHidden()
    }

    @Then("the retry button is shown")
    fun theRetryButtonIsShown() {
        chat.assertRetryIsShown()
    }
}
