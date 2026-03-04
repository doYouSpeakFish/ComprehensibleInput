package input.comprehensible.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RunRetryingTest {

    @Test
    fun `zero retries and first attempt succeeds`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 0
        // WHEN the first attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 0,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            "ok"
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 1
        assertEquals(1, attempts)
        // AND no failures are recorded
        assertEquals(emptyList<Int>(), onFailureRetryCounts)
        assertEquals(emptyList<String?>(), onFailureThrowableMessages)
    }

    @Test
    fun `zero retries and first attempt fails`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 0
        // WHEN the first attempt fails
        val result = Unit.runRetrying(
            maxRetries = 0,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            error("boom 1")
        }

        // THEN the result is a failure
        assertTrue(result.isFailure)
        // AND the exception message is "boom 1"
        assertEquals("boom 1", result.exceptionOrNull()?.message)
        // AND the number of attempts is 1
        assertEquals(1, attempts)
        // AND retry number 0 is captured
        assertEquals(listOf(0), onFailureRetryCounts)
        // AND throwable message "boom 1" is captured
        assertEquals(listOf("boom 1"), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and first attempt succeeds`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN the first attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            "ok"
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 1
        assertEquals(1, attempts)
        // AND no failures are recorded
        assertEquals(emptyList<Int>(), onFailureRetryCounts)
        assertEquals(emptyList<String?>(), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and second attempt succeeds`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN the second attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            @Suppress("KotlinConstantConditions")
            if (attempts == 2) "ok" else error("boom $attempts")
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 2
        assertEquals(2, attempts)
        // AND retry number 0 is captured
        assertEquals(listOf(0), onFailureRetryCounts)
        // AND throwable message "boom 1" is captured
        assertEquals(listOf("boom 1"), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and third attempt succeeds`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN the third attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            @Suppress("KotlinConstantConditions")
            if (attempts == 3) "ok" else error("boom $attempts")
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 3
        assertEquals(3, attempts)
        // AND retry numbers 0 and 1 are captured
        assertEquals(listOf(0, 1), onFailureRetryCounts)
        // AND throwable messages "boom 1" and "boom 2" are captured
        assertEquals(listOf("boom 1", "boom 2"), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and last attempt succeeds`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN the last attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            @Suppress("KotlinConstantConditions")
            if (attempts == 4) "ok" else error("boom $attempts")
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 4
        assertEquals(4, attempts)
        // AND retry numbers 0, 1, and 2 are captured
        assertEquals(listOf(0, 1, 2), onFailureRetryCounts)
        // AND throwable messages "boom 1", "boom 2", and "boom 3" are captured
        assertEquals(listOf("boom 1", "boom 2", "boom 3"), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and all attempts fail`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN all attempts fail
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            error("boom $attempts")
        }

        // THEN the result is a failure
        assertTrue(result.isFailure)
        // AND the exception message is "boom 4"
        assertEquals("boom 4", result.exceptionOrNull()?.message)
        // AND the number of attempts is 4
        assertEquals(4, attempts)
        // AND retry numbers 0, 1, 2, and 3 are captured
        assertEquals(listOf(0, 1, 2, 3), onFailureRetryCounts)
        // AND throwable messages "boom 1", "boom 2", "boom 3", and "boom 4" are captured
        assertEquals(listOf("boom 1", "boom 2", "boom 3", "boom 4"), onFailureThrowableMessages)
    }

    @Test
    fun `three retries and all attempts succeed`() {
        val onFailureRetryCounts = mutableListOf<Int>()
        val onFailureThrowableMessages = mutableListOf<String?>()
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN every attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
            onFailure = { retries, e ->
                onFailureRetryCounts += retries
                onFailureThrowableMessages += e.message
            },
        ) {
            attempts++
            "ok"
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 1
        assertEquals(1, attempts)
        // AND no failures are recorded
        assertEquals(emptyList<Int>(), onFailureRetryCounts)
        assertEquals(emptyList<String?>(), onFailureThrowableMessages)
    }

    @Test
    fun `second attempt succeeds with default onFailure`() {
        var attempts = 0

        // GIVEN max retries is set to 3
        // WHEN every attempt succeeds
        val result = Unit.runRetrying(
            maxRetries = 3,
        ) {
            attempts++
            @Suppress("KotlinConstantConditions")
            if (attempts == 2) "ok" else error("boom $attempts")
        }

        // THEN the result is successful
        assertTrue(result.isSuccess)
        // AND the result is "ok"
        assertEquals("ok", result.getOrNull())
        // AND the number of attempts is 1
        assertEquals(2, attempts)
    }
}
