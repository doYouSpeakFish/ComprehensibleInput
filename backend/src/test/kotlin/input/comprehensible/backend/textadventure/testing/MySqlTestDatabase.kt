package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.AdventureMessagesTable
import input.comprehensible.backend.textadventure.AdventureSentencesTable
import input.comprehensible.backend.textadventure.AdventuresTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.MySQLContainer

object MySqlTestDatabase {
    private val mysqlContainer = MySQLContainer("mysql:8.4.6")
    private var initialized = false

    fun connectAndInitialize(): Database {
        if (!mysqlContainer.isRunning) {
            mysqlContainer.start()
        }

        val database = Database.connect(
            url = mysqlContainer.jdbcUrl,
            user = mysqlContainer.username,
            password = mysqlContainer.password,
            driver = "com.mysql.cj.jdbc.Driver",
        )

        if (!initialized) {
            transaction(database) {
                SchemaUtils.create(AdventuresTable, AdventureMessagesTable, AdventureSentencesTable)
            }
            initialized = true
        }

        return database
    }

    fun reset(database: Database) {
        transaction(database) {
            AdventureSentencesTable.deleteAll()
            AdventureMessagesTable.deleteAll()
            AdventuresTable.deleteAll()
        }
    }
}
