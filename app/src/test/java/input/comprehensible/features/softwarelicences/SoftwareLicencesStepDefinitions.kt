package input.comprehensible.features.softwarelicences

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

/**
 * Drives the software licences screen, delegating Compose interaction to [SoftwareLicencesRobot].
 */
class SoftwareLicencesStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val softwareLicences get() = SoftwareLicencesRobot(scope.composeRule)

    @Given("the software licences screen is open")
    fun theSoftwareLicencesScreenIsOpen() {
        scope.goToSoftwareLicences()
        scope.idle()
        softwareLicences.waitUntilLicencesHaveLoaded()
    }

    @Then("the software licences title is shown")
    fun theSoftwareLicencesTitleIsShown() {
        softwareLicences.assertSoftwareLicencesTitleIsVisible()
    }

    @Then("the {string} licence is shown")
    fun theLicenceIsShown(licence: String) {
        softwareLicences.assertLicenceIsVisible(licence)
    }
}
