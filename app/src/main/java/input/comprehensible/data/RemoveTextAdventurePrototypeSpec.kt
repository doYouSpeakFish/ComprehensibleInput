package input.comprehensible.data

import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec

/**
 * Migration spec for version 7 -> 8. Installs reach version 7 through the restored prototype
 * migrations, then this drops the text adventure prototype's tables; its views are removed
 * automatically because version 8 declares none. The version 8 tables that back the rebuilt text
 * adventure feature (`user`, `adventure`, `message`, `sentence`) are added by Room automatically.
 */
@DeleteTable.Entries(
    DeleteTable(tableName = "TextAdventureEntity"),
    DeleteTable(tableName = "TextAdventureMessageEntity"),
    DeleteTable(tableName = "TextAdventureSentenceEntity"),
)
class RemoveTextAdventurePrototypeSpec : AutoMigrationSpec
