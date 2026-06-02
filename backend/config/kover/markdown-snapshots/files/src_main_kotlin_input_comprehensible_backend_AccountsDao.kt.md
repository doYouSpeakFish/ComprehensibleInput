# src/main/kotlin/input/comprehensible/backend/AccountsDao.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 98-102

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:98-102`

```kotlin
🟢   98 |         transaction(Connection.TRANSACTION_SERIALIZABLE, db = database) {
🟢   99 |             val existing = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
🟡  100 |             if (existing != null && existing[AccountsTable.id] != accountId) return@transaction EmailChangeRequestResult.AlreadyInUse
🟢  101 |             PendingEmailChangeTable.deleteWhere { PendingEmailChangeTable.accountId eq accountId }
🟢  102 |             PendingEmailChangeTable.insert {
```

## Lines 123-127

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:123-127`

```kotlin
🟢  123 |                 (PendingEmailChangeTable.currentEmailCode eq code)
🟢  124 |         }.singleOrNull() ?: return@transaction null
🟡  125 |         if (pending[PendingEmailChangeTable.currentEmailCodeExpiresAt] < now) return@transaction null
🟢  126 |         PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
🟢  127 |             it[PendingEmailChangeTable.newEmailCode] = newEmailCode
```

## Lines 140-144

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:140-144`

```kotlin
🟢  140 |                 (PendingEmailChangeTable.newEmailCode eq code)
🟢  141 |         }.singleOrNull() ?: return@transaction false
🟡  142 |         if (pending[PendingEmailChangeTable.newEmailCodeExpiresAt] < now) return@transaction false
🟢  143 |         AccountsTable.update({ AccountsTable.id eq accountId }) {
🟢  144 |             it[AccountsTable.email] = email
```

## Lines 166-170

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:166-170`

```kotlin
🟢  166 |             .where { PendingEmailChangeTable.accountId eq accountId }
🟢  167 |             .singleOrNull() ?: return@transaction null
🟡  168 |         if (pending[PendingEmailChangeTable.newEmailCode].isEmpty()) return@transaction null
🟢  169 |         PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
🟢  170 |             it[newEmailCode] = code
```

## Lines 183-187

Location: `src/main/kotlin/input/comprehensible/backend/AccountsDao.kt:183-187`

```kotlin
⚪  183 | 
🟢  184 |     fun resetPassword(email: String, passwordHash: String, code: String, now: Long): Boolean = transaction(database) {
🟡  185 |         val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
🟢  186 |         val accountId = account[AccountsTable.id]
🟢  187 |         val reset = PasswordResetTable.selectAll().where {
```
