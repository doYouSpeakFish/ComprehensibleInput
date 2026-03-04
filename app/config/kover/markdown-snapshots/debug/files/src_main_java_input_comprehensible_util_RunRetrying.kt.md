# src/main/java/input/comprehensible/util/RunRetrying.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 11-18

Location: `src/main/java/input/comprehensible/util/RunRetrying.kt:11-18`

```kotlin
⚪   11 |     block: T.() -> R,
⚪   12 | ): Result<R> {
🟡   13 |     var result = runCatching(block).onFailure { onFailure(0, it) }
🟡   14 |     for (retryCount in 1..maxRetries) {
🟡   15 |         if (result.isSuccess) break
🟡   16 |         result = runCatching(block).onFailure { onFailure(retryCount, it) }
⚪   17 |     }
🟢   18 |     return result
```
