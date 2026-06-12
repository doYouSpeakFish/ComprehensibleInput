package input.comprehensible.backend.openapi

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.email.EmailDataSource
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.PostgreSqlTestDatabase
import io.ktor.openapi.OpenApiDoc
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.getAllRoutes
import io.ktor.server.routing.routing
import io.ktor.server.routing.openapi.plus
import io.ktor.server.testing.testApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Renders the backend's OpenAPI contract from the live routing tree (request/response schemas are
 * produced by Ktor's OpenAPI compiler plugin from the kotlinx.serialization types) and asserts it
 * still matches the committed [SPEC_PATH].
 *
 * This is the source of the contract diffed in CI: the workflow runs oasdiff between the spec on
 * `main` and the spec on the PR branch and fails the build on a backwards-incompatible change, so
 * the API cannot break already-released app versions by accident. Because the assertion also fails
 * when the spec is merely out of date, the committed file is guaranteed to reflect the real routes.
 *
 * Regenerate after an intentional API change with:
 *   ./gradlew :backend:test -Popenapi.update
 */
class OpenApiSpecGenerationTest {

    @Test
    fun `committed OpenAPI spec matches the current routes`() {
        val generated = generateSpecYaml()
        val specFile = File(SPEC_PATH)
        File(GENERATED_COPY_PATH).apply {
            parentFile.mkdirs()
            writeText(generated)
        }

        if (System.getProperty("openapi.update") == "true") {
            specFile.parentFile.mkdirs()
            specFile.writeText(generated)
            return
        }

        if (!specFile.exists()) {
            throw AssertionError("Missing OpenAPI contract at $SPEC_PATH. Generate it with: $REGEN_COMMAND")
        }

        if (specFile.readText() != generated) {
            throw AssertionError(
                "The committed OpenAPI contract ($SPEC_PATH) is out of date with the routes.\n" +
                    "If this API change is intentional, regenerate and commit it with:\n" +
                    "  $REGEN_COMMAND\n" +
                    "The freshly generated spec was written to $GENERATED_COPY_PATH for comparison.",
            )
        }
    }

    private fun generateSpecYaml(): String {
        lateinit var spec: String
        testApplication {
            val database = connectDatabase(PostgreSqlTestDatabase.createConfig())
            val textAdventureService = TextAdventureGenerationService(
                structuredPromptExecutor = FakeTextAdventureStructuredPromptExecutor(),
                adventureRepository = DatabaseAdventureRepository(database = database),
            )
            val accountService = AccountService(
                database = database,
                emailDataSource = object : EmailDataSource {
                    override suspend fun sendEmail(to: String, subject: String, textBody: String) = Unit
                },
            )
            lateinit var root: RoutingNode
            application {
                configureRouting(
                    textAdventureService = textAdventureService,
                    appApiKey = "openapi-spec-generation",
                    accountService = accountService,
                )
                root = routing { }
            }
            // The application { } block above is deferred until the app starts, so force start-up
            // before reading the configured routing tree.
            startApplication()
            val doc = OpenApiDoc(info = OpenApiInfo(title = API_TITLE, version = API_VERSION)) + root.getAllRoutes()
            spec = SPEC_JSON.encodeToString(doc)
        }
        return spec.trimEnd() + "\n"
    }

    private companion object {
        // Stable, diff-friendly rendering: declaration order is deterministic, nulls are dropped to
        // reduce noise, and non-null defaults are kept so the spec stays self-contained for oasdiff.
        val SPEC_JSON = Json {
            prettyPrint = true
            explicitNulls = false
            encodeDefaults = true
        }

        const val SPEC_PATH = "openapi/openapi.json"
        const val GENERATED_COPY_PATH = "build/openapi/openapi.json"
        const val REGEN_COMMAND = "./gradlew :backend:test -Popenapi.update"
        const val API_TITLE = "3 Million Words backend API"
        const val API_VERSION = "1.0.0"
    }
}
