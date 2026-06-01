# src/main/java/input/comprehensible/data/account/AccountRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 26-30

Location: `src/main/java/input/comprehensible/data/account/AccountRepository.kt:26-30`

```kotlin
⚪   26 |     suspend fun signOut(): Result<Unit> =
🟢   27 |         runCatching { localDataSource.clearSession() }
🟡   28 |             .onFailure { Timber.e(it, "Failed to sign out") }
⚪   29 | 
🟢   30 |     suspend fun getSessionToken(): String? = localDataSource.getSessionToken()
```
