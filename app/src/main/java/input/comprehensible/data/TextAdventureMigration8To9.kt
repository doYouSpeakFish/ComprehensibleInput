package input.comprehensible.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TextAdventureMigration8To9 : Migration(
    FROM_VERSION,
    TO_VERSION,
) {
    companion object {
        private const val FROM_VERSION = 8
        private const val TO_VERSION = 9
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureMessageSentenceView`")
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureSummaryView`")

        db.execSQL("CREATE TABLE IF NOT EXISTS `UserEntity` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")

        // Adventures from version 8 have no userId and cannot be associated with a user.
        db.execSQL("DELETE FROM `TextAdventureMessageEntity`")
        db.execSQL("DELETE FROM `TextAdventureEntity`")

        db.execSQL("ALTER TABLE `TextAdventureEntity` RENAME TO `TextAdventureEntity_old`")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `TextAdventureEntity` " +
                "(`id` TEXT NOT NULL, `title` TEXT NOT NULL, `learningLanguage` TEXT NOT NULL, " +
                "`translationLanguage` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL, `userId` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `UserEntity`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TextAdventureEntity_userId` " +
                "ON `TextAdventureEntity` (`userId`)"
        )
        db.execSQL("DROP TABLE `TextAdventureEntity_old`")

        db.execSQL(
            """
            |CREATE VIEW `TextAdventureSummaryView` AS SELECT
            |        adventure.id AS adventureId,
            |        adventure.title AS title,
            |        adventure.learningLanguage AS learningLanguage,
            |        adventure.translationLanguage AS translationLanguage,
            |        adventure.updatedAt AS updatedAt,
            |        adventure.userId AS userId,
            |        CASE WHEN EXISTS (
            |            SELECT 1 FROM TextAdventureMessageEntity m
            |            WHERE m.adventureId = adventure.id AND m.isEnding = 1
            |        ) THEN 1 ELSE 0 END AS isComplete
            |    FROM TextAdventureEntity AS adventure
            """.trimMargin()
        )

        db.execSQL(
            """
            |CREATE VIEW `TextAdventureMessageSentenceView` AS SELECT
            |        adventure.id AS adventureId,
            |        adventure.title AS title,
            |        adventure.learningLanguage AS learningLanguage,
            |        adventure.translationLanguage AS translationLanguage,
            |        messages.id AS messageId,
            |        messages.parentId AS parentId,
            |        messages.createdAt AS createdAt,
            |        messages.sender AS sender,
            |        messages.isEnding AS isEnding,
            |        sentences.paragraphIndex AS paragraphIndex,
            |        sentences.sentenceIndex AS sentenceIndex,
            |        sentences.language AS language,
            |        sentences.text AS text
            |    FROM TextAdventureEntity AS adventure
            |    INNER JOIN TextAdventureMessageEntity AS messages
            |        ON messages.adventureId = adventure.id
            |    INNER JOIN TextAdventureSentenceEntity AS sentences
            |        ON sentences.messageId = messages.id
            """.trimMargin()
        )
    }
}
