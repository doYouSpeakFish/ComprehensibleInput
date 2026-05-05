package input.comprehensible.backend

import org.flywaydb.core.Flyway
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.name

class DatabaseMigrationTest {
    @Test
    fun `all SQL migrations apply sequentially on H2 in PostgreSQL mode`() {
        val migrationDirectory = "src/main/resources/db/migration"
        val migrationScripts = Files.list(java.nio.file.Path.of(migrationDirectory))
            .use { paths ->
                paths
                    .filter { path -> path.name.endsWith(".sql") }
                    .sorted()
                    .toList()
            }
        migrationScripts.forEachIndexed { index, _ ->
            val migrationCount = index + 1
            val databaseUrl = "jdbc:h2:mem:migration_test_$migrationCount;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
            val flyway = Flyway.configure()
                .dataSource(databaseUrl, "sa", "")
                .locations("filesystem:$migrationDirectory")
                .target("V$migrationCount")
                .load()

            val result = flyway.migrate()
            assertEquals(migrationCount, result.migrationsExecuted)
        }
    }
}
