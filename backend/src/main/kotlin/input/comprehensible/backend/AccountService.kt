package input.comprehensible.backend

import io.ktor.http.HttpStatusCode
import input.comprehensible.backend.email.CloudflareEmailDataSource
import input.comprehensible.backend.email.EmailDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.mindrot.jbcrypt.BCrypt
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

@Suppress("TooManyFunctions")
class AccountService(
    database: Database,
    private val random: SecureRandom = SecureRandom(),
    private val accountsDao: AccountsDao = AccountsDao(database),
    private val emailDataSource: EmailDataSource = CloudflareEmailDataSource.fromEnvironment(),
    private val verificationCodeProvider: () -> String = { random.nextInt(1_000_000).toString().padStart(6, '0') },
    private val currentTimeMillisProvider: () -> Long = System::currentTimeMillis,
) {
    fun createAccount(email: String, password: String): AccountResult {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) {
            return AccountResult(HttpStatusCode.BadRequest)
        }
        val verificationCode = verificationCodeProvider()
        val accountId = accountsDao.insertAccount(
            email = normalizedEmail,
            passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
            emailVerified = false,
            now = now(),
        )
        if (accountId == null) {
            runBlocking {
                emailDataSource.sendEmail(
                    to = normalizedEmail,
                    subject = "Comprehensible Input account already exists",
                    textBody = "A Comprehensible Input account already exists for this email address. " +
                        "If this was not you, you can safely ignore this message.",
                )
            }
            return AccountResult(HttpStatusCode.OK)
        }
        accountsDao.storeEmailVerificationCode(
            accountId = accountId,
            code = verificationCode,
            expiresAt = now() + verificationCodeTtlMs,
        )
        runBlocking {
            emailDataSource.sendEmail(
                to = normalizedEmail,
                subject = "Verify your Comprehensible Input email address",
                textBody = "Use this verification code to verify your Comprehensible Input email address: $verificationCode",
            )
        }
        return AccountResult(HttpStatusCode.OK)
    }

    fun signIn(email: String, password: String): SignInResult {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
        val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
        if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
        if (!account[AccountsTable.emailVerified]) return SignInResult(HttpStatusCode.Unauthorized)
        val token = generateToken()
        accountsDao.createSession(
            accountId = account[AccountsTable.id],
            tokenHash = hashToken(token),
            now = now(),
        )
        return SignInResult(HttpStatusCode.OK, SignInPayload(token, bearerTokenType))
    }

    fun getMe(accountId: String): AccountPayload? = accountsDao.findAccountById(accountId)?.let {
        AccountPayload(it[AccountsTable.id], it[AccountsTable.email])
    }

    fun updateMe(accountId: String, currentEmail: String, newEmail: String?, password: String?): AccountResult {
        if (password.isNullOrBlank()) return AccountResult(HttpStatusCode.BadRequest)
        val account = accountsDao.findAccountById(accountId) ?: return AccountResult(HttpStatusCode.Unauthorized)
        if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return AccountResult(HttpStatusCode.Unauthorized)
        val normalizedEmail = newEmail?.let(::normalizeEmail) ?: return AccountResult(HttpStatusCode.BadRequest)
        if (!isValidEmail(normalizedEmail)) return AccountResult(HttpStatusCode.BadRequest)
        val existing = accountsDao.findAccountByEmail(normalizedEmail)
        if (existing != null && existing[AccountsTable.email] != currentEmail) return AccountResult(HttpStatusCode.Conflict)
        accountsDao.updateEmail(accountId, normalizedEmail, now())
        val updated = accountsDao.findAccountById(accountId) ?: return AccountResult(HttpStatusCode.Unauthorized)
        return AccountResult(HttpStatusCode.OK, AccountPayload(updated[AccountsTable.id], updated[AccountsTable.email]))
    }

    fun deleteMe(accountId: String, password: String?): HttpStatusCode {
        if (password.isNullOrBlank()) return HttpStatusCode.BadRequest
        val account = accountsDao.findAccountById(accountId) ?: return HttpStatusCode.Unauthorized
        if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return HttpStatusCode.Unauthorized
        accountsDao.deleteAccount(accountId)
        return HttpStatusCode.NoContent
    }

    fun signOutCurrent(token: String): HttpStatusCode =
        if (accountsDao.deleteSessionByTokenHash(hashToken(token)) > 0) HttpStatusCode.NoContent else HttpStatusCode.Unauthorized

    fun findAccountBySessionToken(token: String): AccountSessionPrincipal? {
        val session = accountsDao.findSessionByTokenHash(hashToken(token)) ?: return null
        val account = accountsDao.findAccountById(session[SessionsTable.accountId]) ?: return null
        return AccountSessionPrincipal(
            token = token,
            accountId = account[AccountsTable.id],
            account = AccountPayload(account[AccountsTable.id], account[AccountsTable.email]),
        )
    }

    fun verifyEmail(email: String, code: String): HttpStatusCode {
        val normalizedEmail = normalizeEmail(email)
        val now = now()
        val verified = accountsDao.verifyEmailCode(normalizedEmail, code, now)
        return if (verified) HttpStatusCode.NoContent else HttpStatusCode.BadRequest
    }

    fun requestPasswordReset(email: String): HttpStatusCode {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail)) return HttpStatusCode.Accepted
        val account = accountsDao.findAccountByEmail(normalizedEmail)
        if (account == null) {
            runBlocking {
                emailDataSource.sendEmail(
                    to = normalizedEmail,
                    subject = "Comprehensible Input password reset request",
                    textBody = "A password reset was requested for this email, but you do not have a Comprehensible Input account.",
                )
            }
            return HttpStatusCode.Accepted
        }
        val code = verificationCodeProvider()
        accountsDao.storePasswordResetCode(
            accountId = account[AccountsTable.id],
            code = code,
            expiresAt = now() + verificationCodeTtlMs,
        )
        runBlocking {
            emailDataSource.sendEmail(
                to = normalizedEmail,
                subject = "Your Comprehensible Input password reset code",
                textBody = "Use this password reset code to reset your Comprehensible Input password: $code",
            )
        }
        return HttpStatusCode.Accepted
    }

    fun resetPassword(email: String, password: String, code: String): HttpStatusCode {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) return HttpStatusCode.BadRequest
        val updated = accountsDao.resetPassword(
            email = normalizedEmail,
            passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
            code = code,
            now = now(),
        )
        return if (updated) HttpStatusCode.NoContent else HttpStatusCode.BadRequest
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
    private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
    private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
    private fun hashToken(token: String): String =
        Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.toByteArray()))

    private fun now(): Long = currentTimeMillisProvider()

    private companion object {
        const val minimumPasswordLength = 12
        const val bearerTokenType = "Bearer"
        const val verificationCodeTtlMs = 15 * 60 * 1000L
    }
}

data class AccountResult(val status: HttpStatusCode, val payload: AccountPayload? = null)
@Serializable data class AccountPayload(val id: String, val email: String)
data class SignInResult(val status: HttpStatusCode, val payload: SignInPayload? = null)
@Serializable data class SignInPayload(val accessToken: String, val tokenType: String)

object AccountsTable : Table("account_user") {
    val id = varchar("id", 255)
    val email = varchar("email", 320).uniqueIndex()
    val passwordHash = varchar("password_hash", 1024)
    val emailVerified = bool("email_verified")
    val createdAt = registerColumn("created_at", LongColumnType())
    val updatedAt = registerColumn("updated_at", LongColumnType())
    override val primaryKey = PrimaryKey(id)
}

object EmailVerificationTable : Table("account_email_verification") {
    val accountId = varchar("account_id", 255).references(AccountsTable.id, onDelete = ReferenceOption.CASCADE)
    val code = varchar("code", 6)
    val expiresAt = registerColumn("expires_at", LongColumnType())
    override val primaryKey = PrimaryKey(accountId)
}

object SessionsTable : Table("account_session") {
    val id = varchar("id", 255)
    val accountId = varchar("account_id", 255).references(AccountsTable.id, onDelete = ReferenceOption.CASCADE)
    val tokenHash = varchar("token_hash", 255).uniqueIndex()
    val createdAt = registerColumn("created_at", LongColumnType())
    override val primaryKey = PrimaryKey(id)
}

object PasswordResetTable : Table("account_password_reset") {
    val accountId = varchar("account_id", 255).references(AccountsTable.id, onDelete = ReferenceOption.CASCADE)
    val code = varchar("code", 6)
    val expiresAt = registerColumn("expires_at", LongColumnType())
    override val primaryKey = PrimaryKey(accountId)
}
