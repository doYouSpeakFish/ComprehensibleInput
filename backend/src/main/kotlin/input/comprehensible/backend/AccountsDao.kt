package input.comprehensible.backend

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.sql.Connection
import java.util.UUID

@Suppress("TooManyFunctions")
class AccountsDao(private val database: Database) {
    fun insertAccount(email: String, passwordHash: String, emailVerified: Boolean, now: Long): InsertAccountResult =
        transaction(Connection.TRANSACTION_SERIALIZABLE, db = database) {
            val existing = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
            if (existing != null) return@transaction InsertAccountResult.AlreadyExists(
                accountId = existing[AccountsTable.id],
                emailVerified = existing[AccountsTable.emailVerified],
            )

            val accountId = UUID.randomUUID().toString()
            AccountsTable.insert {
                it[id] = accountId
                it[this.email] = email
                it[this.passwordHash] = passwordHash
                it[this.emailVerified] = emailVerified
                it[createdAt] = now
                it[updatedAt] = now
            }
            InsertAccountResult.Inserted(accountId)
        }

    fun storeEmailVerificationCode(accountId: String, code: String, expiresAt: Long) = transaction(database) {
        EmailVerificationTable.deleteWhere { EmailVerificationTable.accountId eq accountId }
        EmailVerificationTable.insert {
            it[EmailVerificationTable.accountId] = accountId
            it[EmailVerificationTable.code] = code
            it[EmailVerificationTable.expiresAt] = expiresAt
        }
    }

    fun storePasswordResetCode(accountId: String, code: String, expiresAt: Long) = transaction(database) {
        PasswordResetTable.deleteWhere { PasswordResetTable.accountId eq accountId }
        PasswordResetTable.insert {
            it[PasswordResetTable.accountId] = accountId
            it[PasswordResetTable.code] = code
            it[PasswordResetTable.expiresAt] = expiresAt
        }
    }

    fun verifyEmailCode(email: String, code: String, now: Long): Boolean = transaction(database) {
        val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
        val accountId = account[AccountsTable.id]
        val verification = EmailVerificationTable.selectAll()
            .where { (EmailVerificationTable.accountId eq accountId) and (EmailVerificationTable.code eq code) }
            .singleOrNull() ?: return@transaction false
        if (verification[EmailVerificationTable.expiresAt] < now) return@transaction false
        AccountsTable.update({ AccountsTable.id eq accountId }) { it[AccountsTable.emailVerified] = true }
        EmailVerificationTable.deleteWhere { EmailVerificationTable.accountId eq accountId }
        true
    }

    fun findAccountByEmail(email: String): ResultRow? = transaction(database) {
        AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
    }

    fun findAccountById(accountId: String): ResultRow? = transaction(database) {
        AccountsTable.selectAll().where { AccountsTable.id eq accountId }.singleOrNull()
    }

    fun createSession(accountId: String, tokenHash: String, now: Long) = transaction(database) {
        SessionsTable.insert {
            it[id] = UUID.randomUUID().toString()
            it[this.accountId] = accountId
            it[this.tokenHash] = tokenHash
            it[createdAt] = now
        }
    }

    fun findSessionByTokenHash(tokenHash: String): ResultRow? = transaction(database) {
        SessionsTable.selectAll().where { SessionsTable.tokenHash eq tokenHash }.singleOrNull()
    }

    fun requestEmailChange(
        accountId: String,
        email: String,
        currentEmailCode: String,
        currentEmailCodeExpiresAt: Long,
        now: Long,
    ): EmailChangeRequestResult =
        transaction(Connection.TRANSACTION_SERIALIZABLE, db = database) {
            val existing = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull()
            if (existing != null && existing[AccountsTable.id] != accountId) return@transaction EmailChangeRequestResult.AlreadyInUse
            PendingEmailChangeTable.deleteWhere { PendingEmailChangeTable.accountId eq accountId }
            PendingEmailChangeTable.insert {
                it[PendingEmailChangeTable.accountId] = accountId
                it[PendingEmailChangeTable.email] = email
                it[PendingEmailChangeTable.currentEmailCode] = currentEmailCode
                it[PendingEmailChangeTable.currentEmailCodeExpiresAt] = currentEmailCodeExpiresAt
                it[PendingEmailChangeTable.newEmailCode] = ""
                it[PendingEmailChangeTable.newEmailCodeExpiresAt] = 0L
            }
            AccountsTable.update({ AccountsTable.id eq accountId }) { it[updatedAt] = now }
            EmailChangeRequestResult.Requested
        }

    fun verifyCurrentEmailChange(
        accountId: String,
        code: String,
        now: Long,
        newEmailCode: String,
        newEmailCodeExpiresAt: Long,
    ): PendingEmailVerification? = transaction(database) {
        val pending = PendingEmailChangeTable.selectAll().where {
            (PendingEmailChangeTable.accountId eq accountId) and
                (PendingEmailChangeTable.currentEmailCode eq code)
        }.singleOrNull() ?: return@transaction null
        if (pending[PendingEmailChangeTable.currentEmailCodeExpiresAt] < now) return@transaction null
        PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
            it[PendingEmailChangeTable.newEmailCode] = newEmailCode
            it[PendingEmailChangeTable.newEmailCodeExpiresAt] = newEmailCodeExpiresAt
        }
        PendingEmailVerification(
            email = pending[PendingEmailChangeTable.email],
            code = newEmailCode,
        )
    }

    fun verifyPendingEmailChange(accountId: String, email: String, code: String, now: Long): Boolean = transaction(database) {
        val pending = PendingEmailChangeTable.selectAll().where {
            (PendingEmailChangeTable.accountId eq accountId) and
                (PendingEmailChangeTable.email eq email) and
                (PendingEmailChangeTable.newEmailCode eq code)
        }.singleOrNull() ?: return@transaction false
        if (pending[PendingEmailChangeTable.newEmailCodeExpiresAt] < now) return@transaction false
        AccountsTable.update({ AccountsTable.id eq accountId }) {
            it[AccountsTable.email] = email
            it[updatedAt] = now
        }
        EmailVerificationTable.deleteWhere { EmailVerificationTable.accountId eq accountId }
        PendingEmailChangeTable.deleteWhere { PendingEmailChangeTable.accountId eq accountId }
        SessionsTable.deleteWhere { SessionsTable.accountId eq accountId }
        true
    }

    fun updateCurrentEmailChangeCode(accountId: String, code: String, expiresAt: Long): Boolean = transaction(database) {
        val updated = PendingEmailChangeTable.update({
            (PendingEmailChangeTable.accountId eq accountId) and
                (PendingEmailChangeTable.newEmailCode eq "")
        }) {
            it[currentEmailCode] = code
            it[currentEmailCodeExpiresAt] = expiresAt
        }
        updated > 0
    }

    fun updateNewEmailChangeCode(accountId: String, code: String, expiresAt: Long): String? = transaction(database) {
        val pending = PendingEmailChangeTable.selectAll()
            .where { PendingEmailChangeTable.accountId eq accountId }
            .singleOrNull() ?: return@transaction null
        if (pending[PendingEmailChangeTable.newEmailCode].isEmpty()) return@transaction null
        PendingEmailChangeTable.update({ PendingEmailChangeTable.accountId eq accountId }) {
            it[newEmailCode] = code
            it[newEmailCodeExpiresAt] = expiresAt
        }
        pending[PendingEmailChangeTable.email]
    }

    fun deleteAccount(accountId: String) = transaction(database) {
        AccountsTable.deleteWhere { AccountsTable.id eq accountId }
    }

    fun deleteSessionByTokenHash(tokenHash: String): Int = transaction(database) {
        SessionsTable.deleteWhere { SessionsTable.tokenHash eq tokenHash }
    }

    fun resetPassword(email: String, passwordHash: String, code: String, now: Long): Boolean = transaction(database) {
        val account = AccountsTable.selectAll().where { AccountsTable.email eq email }.singleOrNull() ?: return@transaction false
        val accountId = account[AccountsTable.id]
        val reset = PasswordResetTable.selectAll().where {
            (PasswordResetTable.accountId eq accountId) and (PasswordResetTable.code eq code)
        }.singleOrNull() ?: return@transaction false
        if (reset[PasswordResetTable.expiresAt] < now) {
            PasswordResetTable.deleteWhere { PasswordResetTable.accountId eq accountId }
            return@transaction false
        }
        AccountsTable.update({ AccountsTable.id eq accountId }) {
            it[AccountsTable.passwordHash] = passwordHash
            it[AccountsTable.emailVerified] = true
            it[updatedAt] = now
        }
        PasswordResetTable.deleteWhere { PasswordResetTable.accountId eq accountId }
        SessionsTable.deleteWhere { SessionsTable.accountId eq accountId }
        true
    }
}

data class PendingEmailVerification(val email: String, val code: String)

object PendingEmailChangeTable : Table("account_pending_email_change") {
    val accountId = varchar("account_id", 255).references(AccountsTable.id, onDelete = ReferenceOption.CASCADE)
    val email = varchar("email", 320).uniqueIndex()
    val currentEmailCode = varchar("current_email_code", 6)
    val currentEmailCodeExpiresAt = registerColumn("current_email_code_expires_at", LongColumnType())
    val newEmailCode = varchar("new_email_code", 6)
    val newEmailCodeExpiresAt = registerColumn("new_email_code_expires_at", LongColumnType())
    override val primaryKey = PrimaryKey(accountId)
}

enum class EmailChangeRequestResult { Requested, AlreadyInUse }

sealed interface InsertAccountResult {
    data class Inserted(val accountId: String) : InsertAccountResult
    data class AlreadyExists(val accountId: String, val emailVerified: Boolean) : InsertAccountResult
}

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
