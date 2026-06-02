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

## Lines 67-71

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:67-71`

```kotlin
⚪   67 |     fun signIn(email: String, password: String): SignInResult {
🟢   68 |         val normalizedEmail = normalizeEmail(email)
🟡   69 |         if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
🟢   70 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
🟢   71 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
```

## Lines 80-93

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:80-93`

```kotlin
⚪   80 |     }
⚪   81 | 
🟡   82 |     fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
🟢   83 |         AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
⚪   84 |     }
⚪   85 | 
⚪   86 |     fun updateMe(accountId: String, newEmail: String?, password: String?): HttpStatusCode {
🟡   87 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡   88 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡   89 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟡   90 |         val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return HttpStatusCode.BadRequest
🟡   91 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.BadRequest
🟢   92 |         val currentEmailCode = verificationCodeProvider()
🟢   93 |         val updateResult = accountsDao.requestEmailChange(
```

## Lines 148-154

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:148-154`

```kotlin
⚪  148 | 
⚪  149 |     fun deleteMe(accountId: String, password: String?): HttpStatusCode {
🟡  150 |         if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
🟡  151 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
🟡  152 |         if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
🟢  153 |         accountsDao.deleteAccount(accountId)
🟢  154 |         return HttpStatusCode.NoContent
```

## Lines 156-164

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:156-164`

```kotlin
⚪  156 | 
⚪  157 |     fun signOutCurrent(token: String): HttpStatusCode =
🟡  158 |         if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized
⚪  159 | 
⚪  160 |     fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
🟢  161 |         val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
🟡  162 |         val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
🟢  163 |         return AccountSessionPrincipal(
🟢  164 |             token = token,
```

## Lines 177-181

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:177-181`

```kotlin
⚪  177 |     fun requestPasswordReset(email: String): HttpStatusCode {
🟢  178 |         val normalizedEmail = normalizeEmail(email)
🟡  179 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  180 |         val account = accountsDao.findAccountByEmail(normalizedEmail)
🟢  181 |         if (account == null) {
```

## Lines 207-211

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:207-211`

```kotlin
⚪  207 |     fun requestNewEmailVerificationCode(email: String): HttpStatusCode {
🟢  208 |         val normalizedEmail = normalizeEmail(email)
🟡  209 |         if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
🟢  210 |         val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return HttpStatusCode.Accepted
🟢  211 |         if (account[AccountsTable.emailVerified]) return HttpStatusCode.Accepted
```

## Lines 227-231

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:227-231`

```kotlin
⚪  227 | 
⚪  228 |     fun requestNewEmailChangeCurrentCode(accountId: String): HttpStatusCode {
🟡  229 |         val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.BadRequest
🟢  230 |         val code = verificationCodeProvider()
🟢  231 |         val updated = accountsDao.updateCurrentEmailChangeCode(
```

## Lines 266-270

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:266-270`

```kotlin
⚪  266 |     fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
🟢  267 |         val normalizedEmail = normalizeEmail(email)
🟡  268 |         if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
🟢  269 |         val updated = accountsDao.resetPassword(
🟢  270 |             email = normalizedEmail,
```

## Lines 277-281

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:277-281`

```kotlin
⚪  277 | 
🟢  278 |     private fun normalizeEmail(email: String): String = email.trim().lowercase()
🟡  279 |     private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
🟢  280 |     private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
⚪  281 |     private fun hashToken(token: String): String =
```

## Lines 292-296

Location: `src/main/kotlin/input/comprehensible/backend/AccountService.kt:292-296`

```kotlin
⚪  292 | 
🟢  293 | data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
🔴  294 | @Serializable data class AccountPayload(val id: String, val email: String)
🟢  295 | data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
🔴  296 | @Serializable data class SignInPayload(val accessToken: String, val tokenType: String)
```
