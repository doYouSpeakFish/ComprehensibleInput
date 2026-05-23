package input.comprehensible.cucumber.steps

import input.comprehensible.cucumber.CucumberTestScopeSingleton
import input.comprehensible.features.softwarelicences.SoftwareLicencesRobot
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.runBlocking

class SoftwareLicencesStepDefinitions {
    private val scope = CucumberTestScopeSingleton()

    @Given("the software licences screen is open")
    fun softwareLicencesScreenIsOpen() = runBlocking(scope.dispatcher) {
        scope.goToSoftwareLicences()
        scope.awaitIdle()
    }

    @Then("the licence {string} is visible")
    fun licenceIsVisible(licence: String) = runBlocking(scope.dispatcher) {
        SoftwareLicencesRobot(scope.composeRule).apply {
            waitUntilLicencesHaveLoaded()
            assertLicenceIsVisible(licence)
        }
    }
}
