# src/main/kotlin/input/comprehensible/backend/AccountsDao.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 84-92

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:84-92`

```kotlin
⚪   84 |     }
⚪   85 | 
🔴   86 |     fun updateEmail(accountId: String, email: String, now: Long) = transaction(database) {
🔴   87 |         AccountsTable.update({ AccountsTable.id eq accountId }) {
🔴   88 |             it[AccountsTable.email] = email
🔴   89 |             it[updatedAt] = now
🔴   90 |         }
⚪   91 |     }
⚪   92 | 
```

## Lines 100-104

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:100-104`

```kotlin
🟢  100 |         transaction(Connection.TRANSACTION_SERIALIZABLE, db = database) {
🟢  101 |             val existing = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
🟡  102 |             if (existing != null && existing[AccountsTable.id] != accountId) return@transaction EmailChangeRequestResult.AlreadyInUse
🟢  103 |             PendingEmailChangeTable.deleteWhere { PendingEmailChangeTable.accountId eq accountId }
🟢  104 |             PendingEmailChangeTable.insert {
```

## Lines 125-129

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:125-129`

```kotlin
🟢  125 |                 (PendingEmailChangeTable.currentEmailCode eq code)
🟢  126 |         }.singleOrNull() ?: return@transaction null
🟡  127 |         if (pending[PendingEmailChangeTable.currentEmailCodeExpiresAt] < now) return@transaction null
🟢  128 |         PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
🟢  129 |             it[PendingEmailChangeTable.newEmailCode] = newEmailCode
```

## Lines 142-146

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:142-146`

```kotlin
🟢  142 |                 (PendingEmailChangeTable.newEmailCode eq code)
🟢  143 |         }.singleOrNull() ?: return@transaction false
🟡  144 |         if (pending[PendingEmailChangeTable.newEmailCodeExpiresAt] < now) return@transaction false
🟢  145 |         AccountsTable.update({ AccountsTable.id eq accountId }) {
🟢  146 |             it[AccountsTable.email] = email
```

## Lines 162-166

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:162-166`

```kotlin
⚪  162 | 
🟢  163 |     fun resetPassword(email: String, passwordHash: String, code: String, now: Long): Boolean = transaction(database) {
🟡  164 |         val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
🟢  165 |         val accountId = account[AccountsTable.id]
🟢  166 |         val reset = PasswordResetTable.selectAll().where {
```
