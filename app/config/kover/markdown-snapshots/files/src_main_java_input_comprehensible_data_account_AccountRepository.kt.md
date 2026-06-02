# src/main/java/input/comprehensible/data/account/AccountRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 30-34

Location: `src/main/java/input/comprehensible/data/account/AccountRepository.kt:30-34`

```kotlin
⚪   30 |     suspend fun signOut(): Result<Unit> =
🟢   31 |         runCatching { localDataSource.clearSession() }
🟡   32 |             .onFailure { Timber.e(it, "Failed to sign out") }
⚪   33 | 
⚪   34 |     companion object : Singleton<AccountRepository>() {
```
