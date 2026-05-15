# src/main/kotlin/input/comprehensible/backend/AccountService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 26-30

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:26-30`

```kotlin
⚪   26 |     fun createAccount(email: String, password: String): AccountResult {
🟢   27 |         val normalizedEmail = normalizeEmail(email)
🟡   28 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) {
🟢   29 |             return AccountResult(HttpStatusCode.BadRequest)
⚪   30 |         }
```

## Lines 64-68

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:64-68`

```kotlin
⚪   64 |     fun signIn(email: String, password: String): SignInResult {
🟢   65 |         val normalizedEmail = normalizeEmail(email)
🟡   66 |         if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
🟢   67 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
🟢   68 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
```

## Lines 77-101

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:77-101`

```kotlin
⚪   77 |     }
⚪   78 | 
🟡   79 |     fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
🟢   80 |         AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
⚪   81 |     }
⚪   82 | 
⚪   83 |     fun updateMe(accountId: String, currentEmail: String, newEmail: String?, password: String?): AccountResult {
🟡   84 |         if (password.isNullOrBlank()) return AccountResult(HttpStatusCode.BadRequest)
🟡   85 |         val account = accountsDao.findAccountById(accountId) ?: return AccountResult(HttpStatusCode.Unauthorized)
🟡   86 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return AccountResult(HttpStatusCode.Unauthorized)
🟡   87 |         val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return AccountResult(HttpStatusCode.BadRequest)
🟡   88 |         if (!isValidEmail(normalizedEmail)) return AccountResult(HttpStatusCode.BadRequest)
🟢   89 |         val existing = accountsDao.findAccountByEmail(normalizedEmail)
🟡   90 |         if (existing != null && existing[AccountsTable.email] != currentEmail) return AccountResult(HttpStatusCode.Conflict)
🟢   91 |         accountsDao.updateEmail(accountId, normalizedEmail, now())
🟡   92 |         val updated = accountsDao.findAccountById(accountId) ?: return AccountResult(HttpStatusCode.Unauthorized)
🟢   93 |         return AccountResult(HttpStatusCode.OK, AccountPayload(updated[AccountsTable.id], updated[AccountsTable.email]))
⚪   94 |     }
⚪   95 | 
⚪   96 |     fun deleteMe(accountId: String, password: String?): HttpStatusCode {
🟡   97 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡   98 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡   99 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟢  100 |         accountsDao.deleteAccount(accountId)
🟢  101 |         return HttpStatusCode.NoContent
```

## Lines 103-111

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:103-111`

```kotlin
⚪  103 | 
⚪  104 |     fun signOutCurrent(token: String): HttpStatusCode =
🟡  105 |         if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  106 | 
⚪  107 |     fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
🟢  108 |         val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
🟡  109 |         val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
🟢  110 |         return AccountSessionPrincipal(
🟢  111 |             token = token,
```

## Lines 124-128

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:124-128`

```kotlin
⚪  124 |     fun requestPasswordReset(email: String): HttpStatusCode {
🟢  125 |         val normalizedEmail = normalizeEmail(email)
🟡  126 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  127 |         val account = accountsDao.findAccountByEmail(normalizedEmail)
🟢  128 |         if (account == null) {
```

## Lines 154-158

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:154-158`

```kotlin
⚪  154 |     fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
🟢  155 |         val normalizedEmail = normalizeEmail(email)
🟡  156 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
🟢  157 |         val updated = accountsDao.resetPassword(
🟢  158 |             email = normalizedEmail,
```

## Lines 165-169

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:165-169`

```kotlin
⚪  165 | 
🟢  166 |     private fun normalizeEmail(email: String): String = email.trim().lowercase()
🟡  167 |     private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
🟢  168 |     private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
⚪  169 |     private fun hashToken(token: String): String =
```

## Lines 180-186

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:180-186`

```kotlin
⚪  180 | 
🟢  181 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  182 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  183 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  184 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
⚪  185 | 
🟢  186 | object AccountsTable : Table("account_user") {
```
