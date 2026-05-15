# src/main/kotlin/input/comprehensible/backend/AccountService.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 23-27

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:23-27`

```kotlin
⚪   23 |     fun createAccount(email: String, password: String): AccountResult {
🟢   24 |         val normalizedEmail = normalizeEmail(email)
🟡   25 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) {
🟢   26 |             return AccountResult(HttpStatusCode.BadRequest)
⚪   27 |         }
```

## Lines 61-65

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:61-65`

```kotlin
⚪   61 |     fun signIn(email: String, password: String): SignInResult {
🟢   62 |         val normalizedEmail = normalizeEmail(email)
🟡   63 |         if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
🟢   64 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
🟢   65 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
```

## Lines 74-87

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:74-87`

```kotlin
⚪   74 |     }
⚪   75 | 
🟡   76 |     fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
🟢   77 |         AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
⚪   78 |     }
⚪   79 | 
⚪   80 |     fun updateMe(accountId: String, newEmail: String?, password: String?): HttpStatusCode {
🟡   81 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡   82 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡   83 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟡   84 |         val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return HttpStatusCode.BadRequest
🟡   85 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.BadRequest
🟢   86 |         val currentEmailCode = verificationCodeProvider()
🟢   87 |         val updateResult = accountsDao.requestEmailChange(
```

## Lines 142-148

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:142-148`

```kotlin
⚪  142 | 
⚪  143 |     fun deleteMe(accountId: String, password: String?): HttpStatusCode {
🟡  144 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡  145 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡  146 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟢  147 |         accountsDao.deleteAccount(accountId)
🟢  148 |         return HttpStatusCode.NoContent
```

## Lines 150-158

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:150-158`

```kotlin
⚪  150 | 
⚪  151 |     fun signOutCurrent(token: String): HttpStatusCode =
🟡  152 |         if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  153 | 
⚪  154 |     fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
🟢  155 |         val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
🟡  156 |         val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
🟢  157 |         return AccountSessionPrincipal(
🟢  158 |             token = token,
```

## Lines 171-175

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:171-175`

```kotlin
⚪  171 |     fun requestPasswordReset(email: String): HttpStatusCode {
🟢  172 |         val normalizedEmail = normalizeEmail(email)
🟡  173 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  174 |         val account = accountsDao.findAccountByEmail(normalizedEmail)
🟢  175 |         if (account == null) {
```

## Lines 201-205

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:201-205`

```kotlin
⚪  201 |     fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
🟢  202 |         val normalizedEmail = normalizeEmail(email)
🟡  203 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
🟢  204 |         val updated = accountsDao.resetPassword(
🟢  205 |             email = normalizedEmail,
```

## Lines 212-216

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:212-216`

```kotlin
⚪  212 | 
🟢  213 |     private fun normalizeEmail(email: String): String = email.trim().lowercase()
🟡  214 |     private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
🟢  215 |     private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
⚪  216 |     private fun hashToken(token: String): String =
```

## Lines 227-231

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:227-231`

```kotlin
⚪  227 | 
🟢  228 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  229 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  230 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  231 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
```
