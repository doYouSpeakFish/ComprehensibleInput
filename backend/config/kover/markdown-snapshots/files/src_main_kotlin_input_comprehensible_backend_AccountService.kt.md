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

## Lines 79-83

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:79-83`

```kotlin
⚪   79 |     fun signIn(email: String, password: String): SignInResult {
🟢   80 |         val normalizedEmail = normalizeEmail(email)
🟡   81 |         if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
🟢   82 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
🟢   83 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
```

## Lines 92-105

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:92-105`

```kotlin
⚪   92 |     }
⚪   93 | 
🟡   94 |     fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
🟢   95 |         AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
⚪   96 |     }
⚪   97 | 
⚪   98 |     fun updateMe(accountId: String, newEmail: String?, password: String?): HttpStatusCode {
🟡   99 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡  100 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡  101 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟡  102 |         val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return HttpStatusCode.BadRequest
🟡  103 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.BadRequest
🟢  104 |         val currentEmailCode = verificationCodeProvider()
🟢  105 |         val updateResult = accountsDao.requestEmailChange(
```

## Lines 160-166

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:160-166`

```kotlin
⚪  160 | 
⚪  161 |     fun deleteMe(accountId: String, password: String?): HttpStatusCode {
🟡  162 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡  163 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡  164 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟢  165 |         accountsDao.deleteAccount(accountId)
🟢  166 |         return HttpStatusCode.NoContent
```

## Lines 168-176

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:168-176`

```kotlin
⚪  168 | 
⚪  169 |     fun signOutCurrent(token: String): HttpStatusCode =
🟡  170 |         if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  171 | 
⚪  172 |     fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
🟢  173 |         val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
🟡  174 |         val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
🟢  175 |         return AccountSessionPrincipal(
🟢  176 |             token = token,
```

## Lines 189-193

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:189-193`

```kotlin
⚪  189 |     fun requestPasswordReset(email: String): HttpStatusCode {
🟢  190 |         val normalizedEmail = normalizeEmail(email)
🟡  191 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  192 |         val account = accountsDao.findAccountByEmail(normalizedEmail)
🟢  193 |         if (account == null) {
```

## Lines 219-223

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:219-223`

```kotlin
⚪  219 |     fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
🟢  220 |         val normalizedEmail = normalizeEmail(email)
🟡  221 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
🟢  222 |         val updated = accountsDao.resetPassword(
🟢  223 |             email = normalizedEmail,
```

## Lines 230-234

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:230-234`

```kotlin
⚪  230 | 
🟢  231 |     private fun normalizeEmail(email: String): String = email.trim().lowercase()
🟡  232 |     private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
🟢  233 |     private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
⚪  234 |     private fun hashToken(token: String): String =
```

## Lines 245-249

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:245-249`

```kotlin
⚪  245 | 
🟢  246 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  247 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  248 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  249 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
```
