# src/main/kotlin/input/comprehensible/backend/AccountService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-37

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:33-37`

```kotlin
⚪   33 |     ) {
🟢   34 |         val normalizedEmail = normalizeEmail(email) ?: return@transaction AccountResult(HttpStatusCode.BadRequest)
🟡   35 |         if (password.length < minimumPasswordLength) return@transaction AccountResult(HttpStatusCode.BadRequest)
🟢   36 |         val now = now()
🟢   37 |         runCatching {
```

## Lines 44-48

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:44-48`

```kotlin
🟢   44 |             }
🟢   45 |         }.onFailure { error ->
🟡   46 |             if (error !is ExposedSQLException) throw error
🟢   47 |         }
🟢   48 |         AccountResult(HttpStatusCode.OK)
```

## Lines 51-57

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:51-57`

```kotlin
🟢   51 |     fun signIn(email: String, password: String): SignInResult = transaction(database) {
🟢   52 |         val normalizedEmail = normalizeEmail(email)
🟡   53 |         val account = normalizedEmail?.let(::findAccountByEmail)
🟡   54 |         val passwordHash = account?.get(AccountsTable.passwordHash) ?: dummyPasswordHash
🟡   55 |         if (normalizedEmail == null || account == null || !BCrypt.checkpw(password, passwordHash)) {
🟢   56 |             return@transaction SignInResult(HttpStatusCode.Unauthorized)
⚪   57 |         }
```

## Lines 67-75

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:67-75`

```kotlin
⚪   67 |     }
⚪   68 | 
🔴   69 |     fun getMe(token: String): AccountPayload? = transaction(database) {
🔴   70 |         val session = findActiveSession(token) ?: return@transaction null
🔴   71 |         val account = AccountsTable.selectAll().where { AccountsTable.id eq session[SessionsTable.accountId] }.singleOrNull()
🔴   72 |             ?: return@transaction null
🔴   73 |         AccountPayload(account[AccountsTable.id], account[AccountsTable.email])
⚪   74 |     }
⚪   75 | 
```

## Lines 78-86

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:78-86`

```kotlin
🟢   78 |         transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
⚪   79 |     ) {
🟡   80 |         val session = findActiveSession(token) ?: return@transaction AccountResult(HttpStatusCode.Unauthorized)
🟡   81 |         val normalizedEmail = email?.let { normalizeEmail(it) ?: return@transaction AccountResult(HttpStatusCode.BadRequest) }
🟡   82 |         if (normalizedEmail != null) {
🟢   83 |             val existing = findAccountByEmail(normalizedEmail)
🟡   84 |             if (existing != null && existing[AccountsTable.id] != session[SessionsTable.accountId]) {
🟢   85 |                 return@transaction AccountResult(HttpStatusCode.Conflict)
⚪   86 |             }
```

## Lines 95-99

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:95-99`

```kotlin
⚪   95 | 
🟢   96 |     fun deleteMe(token: String): HttpStatusCode = transaction(database) {
🟡   97 |         val session = findActiveSession(token) ?: return@transaction HttpStatusCode.Unauthorized
🟢   98 |         SessionsTable.deleteWhere { SessionsTable.accountId eq session[SessionsTable.accountId] }
🟢   99 |         AccountsTable.deleteWhere { AccountsTable.id eq session[SessionsTable.accountId] }
```

## Lines 105-115

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:105-115`

```kotlin
🟢  105 |             it[revokedAt] = now()
🟢  106 |         }
🟡  107 |         if (updated > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  108 |     }
⚪  109 | 
🟢  110 |     fun findAccountBySessionToken(token: String): AccountPayload? = transaction(database) {
🟢  111 |         val session = findActiveSession(token) ?: return@transaction null
🟡  112 |         val account = AccountsTable.selectAll().where { AccountsTable.id eq session[SessionsTable.accountId] }.singleOrNull()
🔴  113 |             ?: return@transaction null
🟢  114 |         AccountPayload(account[AccountsTable.id], account[AccountsTable.email])
⚪  115 |     }
```

## Lines 124-128

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:124-128`

```kotlin
🟢  124 |         .singleOrNull()
⚪  125 |     private fun normalizeEmail(email: String): String? =
🟡  126 |         email.trim().lowercase().takeIf { it.isNotBlank() && it.contains('@') }
⚪  127 |     private fun generateToken(): String =
🟢  128 |         Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
```

## Lines 141-147

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:141-147`

```kotlin
⚪  141 | 
🟢  142 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  143 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  144 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  145 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
⚪  146 | 
🟢  147 | object AccountsTable : Table("account_user") {
```
