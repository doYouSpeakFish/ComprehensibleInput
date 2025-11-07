package input.comprehensible.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDbMigrationTest {

    @get:Rule
    private val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDb::class.java
    )

    @Test
    fun migrateAll() {
        // GIVEN a version 1 database
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS StoryEntity (
                        id TEXT NOT NULL,
                        storyPosition INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                """.trimIndent()
            )
            db.execSQL("INSERT INTO StoryEntity (id, storyPosition) VALUES ('story-1', 7)")
        }

        // WHEN migrating to the latest schema
        helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2).use { db ->
            db.query("SELECT id, partId, position FROM StoryEntity").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.count)
                assertEquals("story-1", cursor.getString(cursor.getColumnIndexOrThrow("id")))
                assertNull(cursor.getString(cursor.getColumnIndexOrThrow("partId")))
                assertEquals(7, cursor.getInt(cursor.getColumnIndexOrThrow("position")))
            }
        }

        // THEN the latest schema opens successfully in memory
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDb::class.java
        ).addMigrations(MIGRATION_1_2)
            .build()
            .apply {
                openHelper.writableDatabase
                close()
            }
    }

    private companion object {
        private const val TEST_DB = "migration-test"
    }
}
