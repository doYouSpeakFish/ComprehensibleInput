# src/main/java/input/comprehensible/data/account/AccountRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 30-40

Location: `src/main/java/input/comprehensible/data/account/AccountRepository.kt:30-40`

```kotlin
⚪   30 | 
🟢   31 |     suspend fun signOut(): Result<Unit> = runCatching {
🟡   32 |         val token = localDataSource.session.first()?.token
🟡   33 |         if (token != null) {
🟢   34 |             runCatching { remoteDataSource.signOut(token) }
🟡   35 |                 .onFailure { Timber.e(it, "Failed to revoke server session") }
⚪   36 |         }
🟢   37 |         localDataSource.clearSession()
🟡   38 |     }.onFailure { Timber.e(it, "Failed to sign out") }
⚪   39 | 
⚪   40 |     companion object : Singleton<AccountRepository>() {
```
