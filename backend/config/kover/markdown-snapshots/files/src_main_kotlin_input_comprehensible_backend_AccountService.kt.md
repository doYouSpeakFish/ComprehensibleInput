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

## Lines 64-68

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:64-68`

```kotlin
⚪   64 |     fun signIn(email: String, password: String): SignInResult {
🟢   65 |         val normalizedEmail = normalizeEmail(email)
🟡   66 |         if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
🟢   67 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
🟢   68 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
```

## Lines 77-90

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:77-90`

```kotlin
⚪   77 |     }
⚪   78 | 
🟡   79 |     fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
🟢   80 |         AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
⚪   81 |     }
⚪   82 | 
⚪   83 |     fun updateMe(accountId: String, newEmail: String?, password: String?): HttpStatusCode {
🟡   84 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡   85 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡   86 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟡   87 |         val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return HttpStatusCode.BadRequest
🟡   88 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.BadRequest
🟢   89 |         val currentEmailCode = verificationCodeProvider()
🟢   90 |         val updateResult = accountsDao.requestEmailChange(
```

## Lines 145-151

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:145-151`

```kotlin
⚪  145 | 
⚪  146 |     fun deleteMe(accountId: String, password: String?): HttpStatusCode {
🟡  147 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡  148 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡  149 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟢  150 |         accountsDao.deleteAccount(accountId)
🟢  151 |         return HttpStatusCode.NoContent
```

## Lines 153-161

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:153-161`

```kotlin
⚪  153 | 
⚪  154 |     fun signOutCurrent(token: String): HttpStatusCode =
🟡  155 |         if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  156 | 
⚪  157 |     fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
🟢  158 |         val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
🟡  159 |         val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
🟢  160 |         return AccountSessionPrincipal(
🟢  161 |             token = token,
```

## Lines 174-178

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:174-178`

```kotlin
⚪  174 |     fun requestPasswordReset(email: String): HttpStatusCode {
🟢  175 |         val normalizedEmail = normalizeEmail(email)
🟡  176 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  177 |         val account = accountsDao.findAccountByEmail(normalizedEmail)
🟢  178 |         if (account == null) {
```

## Lines 204-208

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:204-208`

```kotlin
⚪  204 |     fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
🟢  205 |         val normalizedEmail = normalizeEmail(email)
🟡  206 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
🟢  207 |         val updated = accountsDao.resetPassword(
🟢  208 |             email = normalizedEmail,
```

## Lines 215-219

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:215-219`

```kotlin
⚪  215 | 
🟢  216 |     private fun normalizeEmail(email: String): String = email.trim().lowercase()
🟡  217 |     private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
🟢  218 |     private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
⚪  219 |     private fun hashToken(token: String): String =
```

## Lines 230-234

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:230-234`

```kotlin
⚪  230 | 
🟢  231 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  232 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  233 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  234 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
```
