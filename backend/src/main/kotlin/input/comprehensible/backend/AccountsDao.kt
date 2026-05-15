package input.comprehensible.backend

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

@Suppress("TooManyFunctions")
class AccountsDao(private val database: Database) {
    fun insertAccount(email: String, passwordHash: String, emailVerified: Boolean, now: Long): String? = transaction(database) {
        runCatching {
            val accountId = UUID.randomUUID().toString()
            AccountsTable.insert {
                it[id] = accountId
                it[this.email] = email
                it[this.passwordHash] = passwordHash
                it[this.emailVerified] = emailVerified
                it[createdAt] = now
                it[updatedAt] = now
            }
            accountId
        }.getOrNull()
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

    fun updateEmail(accountId: String, email: String, now: Long) = transaction(database) {
        AccountsTable.update({ AccountsTable.id eq accountId }) {
            it[AccountsTable.email] = email
            it[updatedAt] = now
        }
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
            it[updatedAt] = now
        }
        PasswordResetTable.deleteWhere { PasswordResetTable.accountId eq accountId }
        true
    }
}
