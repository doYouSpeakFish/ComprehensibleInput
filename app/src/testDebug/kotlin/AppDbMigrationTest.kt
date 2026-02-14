import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import input.comprehensible.data.AppDb
import input.comprehensible.data.TextAdventureMigration5To6
import input.comprehensible.data.TextAdventureMigration6To7
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDbMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDb::class.java,
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Start from the schema just before the text-adventure migrations.
        helper.createDatabase(TEST_DB, START_VERSION).apply {
            close()
        }

        helper.runMigrationsAndValidate(
            TEST_DB,
            END_VERSION,
            true,
            TextAdventureMigration5To6(),
            TextAdventureMigration6To7(),
        )
    }

    companion object {
        private const val TEST_DB = "migration-test"
        private const val START_VERSION = 5
        private const val END_VERSION = 7
    }
}
