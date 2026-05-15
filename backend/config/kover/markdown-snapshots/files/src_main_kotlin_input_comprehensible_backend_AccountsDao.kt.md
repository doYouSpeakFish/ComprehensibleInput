# src/main/kotlin/input/comprehensible/backend/AccountsDao.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 95-99

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:95-99`

```kotlin
🟢   95 |         transaction(Connection.TRANSACTION_SERIALIZABLE, db = database) {
🟢   96 |             val existing = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
🟡   97 |             if (existing != null && existing[AccountsTable.id] != accountId) return@transaction EmailChangeRequestResult.AlreadyInUse
🟢   98 |             PendingEmailChangeTable.deleteWhere { PendingEmailChangeTable.accountId eq accountId }
🟢   99 |             PendingEmailChangeTable.insert {
```

## Lines 120-124

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:120-124`

```kotlin
🟢  120 |                 (PendingEmailChangeTable.currentEmailCode eq code)
🟢  121 |         }.singleOrNull() ?: return@transaction null
🟡  122 |         if (pending[PendingEmailChangeTable.currentEmailCodeExpiresAt] < now) return@transaction null
🟢  123 |         PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
🟢  124 |             it[PendingEmailChangeTable.newEmailCode] = newEmailCode
```

## Lines 137-141

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:137-141`

```kotlin
🟢  137 |                 (PendingEmailChangeTable.newEmailCode eq code)
🟢  138 |         }.singleOrNull() ?: return@transaction false
🟡  139 |         if (pending[PendingEmailChangeTable.newEmailCodeExpiresAt] < now) return@transaction false
🟢  140 |         AccountsTable.update({ AccountsTable.id eq accountId }) {
🟢  141 |             it[AccountsTable.email] = email
```

## Lines 157-161

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:157-161`

```kotlin
⚪  157 | 
🟢  158 |     fun resetPassword(email: String, passwordHash: String, code: String, now: Long): Boolean = transaction(database) {
🟡  159 |         val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
🟢  160 |         val accountId = account[AccountsTable.id]
🟢  161 |         val reset = PasswordResetTable.selectAll().where {
```
