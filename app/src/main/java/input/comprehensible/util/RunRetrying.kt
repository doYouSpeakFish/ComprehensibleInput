package input.comprehensible.util

/**
 * Runs [block] and retries it up to [maxRetries] times if it fails, returning the last [Result].
 *
 * [maxRetries] is the number of additional attempts after the first failure.
 */
inline fun <T, R> T.runRetrying(
    maxRetries: Int,
    onFailure: (retries: Int, e: Throwable) -> Unit = { _, _ -> },
    block: T.() -> R,
): Result<R> {
    var result = runCatching(block).onFailure { onFailure(0, it) }
    for (i in 0 until maxRetries) {
        if (result.isSuccess) break
        result = runCatching(block).onFailure { onFailure(i + 1, it) }
    }
    return result
}
