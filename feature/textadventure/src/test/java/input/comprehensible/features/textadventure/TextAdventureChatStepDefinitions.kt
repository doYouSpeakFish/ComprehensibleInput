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

    @Given("the start adventure request will be rate limited")
    fun theStartAdventureRequestWillBeRateLimited() {
        scope.rateLimitStart()
    }

    @Given("the {string} adventure is cached with message {string}")
    fun theAdventureIsCachedWithMessage(title: String, message: String) {
        scope.cacheAdventureWithMessage(title, message)
    }

    @Given("the {string} adventure refreshes to {string}")
    fun theAdventureRefreshesTo(title: String, text: String) {
        scope.adventureRefreshesTo(title, text)
    }

    @Given("an adventure has started with {string}")
    fun anAdventureHasStartedWith(text: String) {
        scope.startReturns(text, "$text (translated)")
        list.startNewAdventure()
        scope.idle()
    }

    @Given("an adventure has started with a long opening passage")
    fun anAdventureHasStartedWithALongOpeningPassage() {
        scope.startReturnsLongPassage()
        list.startNewAdventure()
        scope.idle()
    }

    @Given("the user message request is delayed")
    fun theUserMessageRequestIsDelayed() {
        scope.delayUserMessage()
    }

    @Given("the user message request will fail")
    fun theUserMessageRequestWillFail() {
        scope.failUserMessage()
    }

    @Given("the user message request will be rate limited")
    fun theUserMessageRequestWillBeRateLimited() {
        scope.rateLimitUserMessage()
    }

    @Given("submitting {string} returns the translation {string}")
    fun submittingReturnsTheTranslation(text: String, translation: String) {
        scope.userMessageReturnsTranslation(text, translation)
    }

    @Given("the AI response request is delayed")
    fun theAiResponseRequestIsDelayed() {
        scope.delayAiMessage()
    }

    @Given("the AI response request will fail")
    fun theAiResponseRequestWillFail() {
        scope.failAiMessage()
    }

    @Given("the AI response request will be rate limited")
    fun theAiResponseRequestWillBeRateLimited() {
        scope.rateLimitAiMessage()
    }

    @Given("the AI responds with {string}")
    fun theAiRespondsWith(text: String) {
        scope.aiRespondsWith(text)
    }

    @Given("the AI responds with the ending {string}")
    fun theAiRespondsWithTheEnding(text: String) {
        scope.aiRespondsWith(text, isEnding = true)
    }

    @When("I send the message {string}")
    fun iSendTheMessage(text: String) {
        chat.sendMessage(text)
        scope.idle()
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

    @Then("the message error is shown")
    fun theMessageErrorIsShown() {
        chat.assertMessageErrorIsShown()
    }

    @Then("the chat shows a system busy message")
    fun theChatShowsASystemBusyMessage() {
        chat.assertBusyMessageIsShown()
    }

    @Then("the text adventure input is hidden")
    fun theTextAdventureInputIsHidden() {
        chat.assertInputIsHidden()
    }

    @Then("the text adventure input is disabled")
    fun theTextAdventureInputIsDisabled() {
        chat.assertInputIsDisabled()
    }
}
