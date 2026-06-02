package input.comprehensible.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TextAdventureMigration7To8 : Migration(FROM_VERSION, TO_VERSION) {
    companion object {
        private const val FROM_VERSION = 7
        private const val TO_VERSION = 8
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `TextAdventureMessageEntity` ADD COLUMN `messageId` TEXT NOT NULL DEFAULT ''"
        )
    }
}
