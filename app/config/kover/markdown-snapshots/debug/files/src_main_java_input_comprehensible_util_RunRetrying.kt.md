# src/main/java/input/comprehensible/util/RunRetrying.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 6-19

Location: `src/main/java/input/comprehensible/util/RunRetrying.kt:6-19`

```kotlin
⚪    6 |  * [maxRetries] is the number of additional attempts after the first failure.
⚪    7 |  */
🔴    8 | inline fun <T, R> T.runRetrying(
⚪    9 |     maxRetries: Int,
🔴   10 |     onFailure: (retries: Int, e: Throwable) -> Unit = { _, _ -> },
⚪   11 |     block: T.() -> R,
⚪   12 | ): Result<R> {
🔴   13 |     var result = runCatching(block).onFailure { onFailure(0, it) }
🔴   14 |     for (i in 0 until maxRetries) {
🔴   15 |         if (result.isSuccess) break
🔴   16 |         result = runCatching(block).onFailure { onFailure(i + 1, it) }
⚪   17 |     }
🔴   18 |     return result
⚪   19 | }
```
