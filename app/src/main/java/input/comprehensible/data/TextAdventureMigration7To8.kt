package input.comprehensible.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TextAdventureMigration7To8 : Migration(
    FROM_VERSION,
    TO_VERSION,
) {
    companion object {
        private const val FROM_VERSION = 7
        private const val TO_VERSION = 8
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureMessageSentenceView`")
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureSummaryView`")

        db.execSQL("DROP INDEX IF EXISTS `index_TextAdventureSentenceEntity_adventureId_messageIndex`")
        db.execSQL("DROP TABLE IF EXISTS `TextAdventureSentenceEntity`")

        db.execSQL("DROP INDEX IF EXISTS `index_TextAdventureMessageEntity_adventureId`")
        db.execSQL("DROP TABLE IF EXISTS `TextAdventureMessageEntity`")

        // Adventures from the old anonymous API cannot be resumed via the v1 authenticated API,
        // so remove their metadata rows to avoid showing unloadable entries in the list.
        db.execSQL("DELETE FROM `TextAdventureEntity`")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TextAdventureMessageEntity` (
                `id` TEXT NOT NULL,
                `adventureId` TEXT NOT NULL,
                `parentId` TEXT,
                `sender` TEXT NOT NULL,
                `isEnding` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`adventureId`) REFERENCES `TextAdventureEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_TextAdventureMessageEntity_adventureId`
            ON `TextAdventureMessageEntity` (`adventureId`)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TextAdventureSentenceEntity` (
                `messageId` TEXT NOT NULL,
                `paragraphIndex` INTEGER NOT NULL,
                `sentenceIndex` INTEGER NOT NULL,
                `language` TEXT NOT NULL,
                `text` TEXT NOT NULL,
                PRIMARY KEY(`messageId`, `paragraphIndex`, `sentenceIndex`, `language`),
                FOREIGN KEY(`messageId`) REFERENCES `TextAdventureMessageEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_TextAdventureSentenceEntity_messageId`
            ON `TextAdventureSentenceEntity` (`messageId`)
            """.trimIndent()
        )

        db.execSQL(
            """
            |CREATE VIEW `TextAdventureSummaryView` AS SELECT
            |        adventure.id AS adventureId,
            |        adventure.title AS title,
            |        adventure.learningLanguage AS learningLanguage,
            |        adventure.translationLanguage AS translationLanguage,
            |        adventure.updatedAt AS updatedAt,
            |        COALESCE((
            |            SELECT m.isEnding
            |            FROM TextAdventureMessageEntity m
            |            WHERE m.adventureId = adventure.id
            |            AND m.id NOT IN (
            |                SELECT c.parentId
            |                FROM TextAdventureMessageEntity c
            |                WHERE c.adventureId = adventure.id
            |                AND c.parentId IS NOT NULL
            |            )
            |            LIMIT 1
            |        ), 0) AS isComplete
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
