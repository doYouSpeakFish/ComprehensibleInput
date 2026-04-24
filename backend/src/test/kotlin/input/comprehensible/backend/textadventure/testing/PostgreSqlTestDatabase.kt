package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.AdventureMessagesTable
import input.comprehensible.backend.textadventure.AdventureSentencesTable
import input.comprehensible.backend.textadventure.AdventuresTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object PostgreSqlTestDatabase {
    private const val jdbcUrl = "jdbc:h2:mem:text_adventure_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
    private var initialized = false

    fun connectAndInitialize(): Database {
        val database = Database.connect(
            url = jdbcUrl,
            user = "sa",
            password = "",
            driver = "org.h2.Driver",
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
