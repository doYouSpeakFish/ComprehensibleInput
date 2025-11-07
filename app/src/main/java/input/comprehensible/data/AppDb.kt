package input.comprehensible.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import javax.inject.Singleton

@Database(entities = [StoryEntity::class], version = 2, exportSchema = true)
abstract class AppDb : RoomDatabase() {
    abstract fun getStoriesInfoDao(): StoriesInfoLocalDataSource
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS StoryEntity_new (
                    id TEXT NOT NULL,
                    partId TEXT DEFAULT NULL,
                    position INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(id)
                )
            """.trimIndent()
        )
        db.execSQL(
            """
                INSERT INTO StoryEntity_new (id, position)
                SELECT id, storyPosition FROM StoryEntity
            """.trimIndent()
        )
        db.execSQL("DROP TABLE StoryEntity")
        db.execSQL("ALTER TABLE StoryEntity_new RENAME TO StoryEntity")
    }
}

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDb =
        Room.databaseBuilder(
            context = context,
            klass = AppDb::class.java,
            name = "app-db"
        ).addMigrations(MIGRATION_1_2)
            .build()
}
