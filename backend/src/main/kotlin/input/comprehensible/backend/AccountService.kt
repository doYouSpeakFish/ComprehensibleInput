package input.comprehensible.backend

import io.ktor.http.HttpStatusCode
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
) {
    fun createAccount(email: String, password: String): AccountResult {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail) || password.length < minimumPasswordLength) {
            return AccountResult(HttpStatusCode.BadRequest)
        }
        accountsDao.insertAccount(
            email = normalizedEmail,
            passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
            now = now(),
        )
        return AccountResult(HttpStatusCode.OK)
    }

    fun signIn(email: String, password: String): SignInResult {
        val normalizedEmail = normalizeEmail(email)
        if (!isValidEmail(normalizedEmail)) return SignInResult(HttpStatusCode.Unauthorized)
        val account = accountsDao.findAccountByEmail(normalizedEmail) ?: return SignInResult(HttpStatusCode.Unauthorized)
        if (!BCrypt.checkpw(password, account[AccountsTable.passwordHash])) return SignInResult(HttpStatusCode.Unauthorized)
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

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
    private fun isValidEmail(email: String): Boolean = email.isNotBlank() && email.contains('@')
    private fun generateToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also(random::nextBytes))
    private fun hashToken(token: String): String =
        Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.toByteArray()))

    private fun now(): Long = System.currentTimeMillis()

    private companion object {
        const val minimumPasswordLength = 12
        const val bearerTokenType = "Bearer"
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
    val createdAt = registerColumn("created_at", LongColumnType())
    val updatedAt = registerColumn("updated_at", LongColumnType())
    override val primaryKey = PrimaryKey(id)
}

object SessionsTable : Table("account_session") {
    val id = varchar("id", 255)
    val accountId = varchar("account_id", 255).references(AccountsTable.id, onDelete = ReferenceOption.CASCADE)
    val tokenHash = varchar("token_hash", 255).uniqueIndex()
    val createdAt = registerColumn("created_at", LongColumnType())
    override val primaryKey = PrimaryKey(id)
}
