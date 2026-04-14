package input.comprehensible.backend

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {

    @Test
    fun `health endpoint returns ok`() = testApplication {
        // GIVEN a running server with backend routing configured.
        application {
            configureRouting()
        }

        // WHEN the user requests the health endpoint.
        val response = client.get("/health")

        // THEN the service reports healthy status and body.
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ok", response.bodyAsText())
    }
}
