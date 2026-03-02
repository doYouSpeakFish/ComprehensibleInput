import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import input.comprehensible.data.AppDb
import input.comprehensible.data.TextAdventureMigration4To5
import input.comprehensible.data.TextAdventureMigration5To6
import input.comprehensible.data.TextAdventureMigration6To7
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDbMigrationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val databaseFile = context.getDatabasePath(TEST_DB)

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = databaseFile,
        driver = AndroidSQLiteDriver(),
        databaseClass = AppDb::class,
        databaseFactory = {
            Room.databaseBuilder(context, AppDb::class.java, TEST_DB)
                .addMigrations(
                    TextAdventureMigration5To6(),
                    TextAdventureMigration6To7(),
                )
                .build()
        },
        autoMigrationSpecs = listOf(TextAdventureMigration4To5()),
    )

    @Test
    fun migrateAll() {
        // GIVEN a database file at the earliest supported schema version.
        databaseFile.parentFile?.mkdirs()
        if (databaseFile.exists()) {
            check(databaseFile.delete()) { "Unable to delete ${databaseFile.absolutePath}" }
        }
        helper.createDatabase(START_VERSION).close()

        Room
            .databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                AppDb::class.java,
            TEST_DB,
        )
            .addMigrations(
                TextAdventureMigration5To6(),
                TextAdventureMigration6To7(),
            )
            .build()
            .apply { openHelper.writableDatabase.close() }
    }

    companion object {
        private const val TEST_DB = "migration-test"
        private const val START_VERSION = 1
    }
}
