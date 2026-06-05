import android.app.Application
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import input.comprehensible.data.AppDb
import input.comprehensible.data.dropTextAdventureMigrations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = Application::class)
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
            val builder = Room.databaseBuilder(context, AppDb::class.java, TEST_DB)
            dropTextAdventureMigrations().forEach { builder.addMigrations(it) }
            builder.build()
        },
        autoMigrationSpecs = emptyList(),
    )

    @Test
    fun migratesFromTextAdventureSchema() {
        // GIVEN a current install still carrying the text adventure prototype tables, with a row.
        recreateDatabaseFile()
        helper.createDatabase(TEXT_ADVENTURE_VERSION).apply {
            execSQL(
                """
                INSERT INTO `TextAdventureEntity`
                    (`id`, `title`, `learningLanguage`, `translationLanguage`, `createdAt`, `updatedAt`)
                VALUES ('adventure-1', 'Forest Trails', 'de', 'en', 0, 0)
                """.trimIndent()
            )
            close()
        }

        // WHEN it upgrades, THEN it reaches version 8 with every text adventure object dropped.
        helper.runMigrationsAndValidate(
            CURRENT_VERSION,
            dropTextAdventureMigrations().toList(),
        ).close()
    }

    @Test
    fun migratesFromOldestSchema() {
        // GIVEN an install at the earliest schema version, from before the prototype existed.
        recreateDatabaseFile()
        helper.createDatabase(EARLIEST_VERSION).close()

        // WHEN it upgrades, THEN every migration up to version 8 applies cleanly.
        helper.runMigrationsAndValidate(
            CURRENT_VERSION,
            dropTextAdventureMigrations().toList(),
        ).close()
    }

    private fun recreateDatabaseFile() {
        databaseFile.parentFile?.mkdirs()
        if (databaseFile.exists()) {
            check(databaseFile.delete()) { "Unable to delete ${databaseFile.absolutePath}" }
        }
    }

    companion object {
        private const val TEST_DB = "migration-test"
        private const val EARLIEST_VERSION = 1
        private const val TEXT_ADVENTURE_VERSION = 7
        private const val CURRENT_VERSION = 8
    }
}
