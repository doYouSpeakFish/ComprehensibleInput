package input.comprehensible.backend

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class AccountsDao(private val database: Database) {
    fun insertAccount(email: String, passwordHash: String, now: Long): Boolean = transaction(database) {
        runCatching {
            AccountsTable.insert {
                it[id] = UUID.randomUUID().toString()
                it[this.email] = email
                it[this.passwordHash] = passwordHash
                it[createdAt] = now
                it[updatedAt] = now
            }
        }.isSuccess
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
}
