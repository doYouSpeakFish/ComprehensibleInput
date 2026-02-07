package input.comprehensible.data

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn.Entries(
    DeleteColumn(tableName = "TextAdventureEntity", columnName = "isComplete"),
    DeleteColumn(tableName = "TextAdventureMessageEntity", columnName = "sentences"),
    DeleteColumn(tableName = "TextAdventureMessageEntity", columnName = "translatedSentences"),
)
class TextAdventureMigration4To5 : AutoMigrationSpec
