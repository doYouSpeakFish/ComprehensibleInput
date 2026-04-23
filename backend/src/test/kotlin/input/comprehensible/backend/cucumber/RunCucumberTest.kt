package input.comprehensible.backend.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],
    glue = ["input.comprehensible.backend.cucumber"],
    plugin = ["pretty"],
)
class RunCucumberTest
