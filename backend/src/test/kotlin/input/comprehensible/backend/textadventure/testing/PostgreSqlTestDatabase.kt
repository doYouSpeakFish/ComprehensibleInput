package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.DatabaseConnectionConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object PostgreSqlTestDatabase {
    private const val user = "sa"
    private const val password = ""
    private const val appRole = "comprehensible_test_app"
    private const val appRolePassword = "comprehensible_test_password"

    fun createConfig(): DatabaseConnectionConfig = DatabaseConnectionConfig(
        databaseUrl = "jdbc:h2:mem:text_adventure_test_${System.nanoTime()};MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        databaseUser = user,
        databasePassword = password,
        migrationDatabaseUser = user,
        migrationDatabasePassword = password,
        appRole = appRole,
        appRolePassword = appRolePassword,
    )

    fun reset(database: Database) {
        transaction(database) {
            exec("DROP ALL OBJECTS")
        }
    }
}
