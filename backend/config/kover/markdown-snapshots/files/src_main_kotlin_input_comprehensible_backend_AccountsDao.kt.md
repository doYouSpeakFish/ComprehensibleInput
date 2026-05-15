# src/main/kotlin/input/comprehensible/backend/AccountsDao.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 96-100

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:96-100`

```kotlin
⚪   96 | 
🟢   97 |     fun resetPassword(email: String, passwordHash: String, code: String, now: Long): Boolean = transaction(database) {
🟡   98 |         val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
🟢   99 |         val accountId = account[AccountsTable.id]
🟢  100 |         val reset = PasswordResetTable.selectAll().where {
```
