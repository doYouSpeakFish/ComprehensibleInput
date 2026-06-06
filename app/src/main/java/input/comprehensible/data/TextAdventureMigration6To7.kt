package input.comprehensible.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TextAdventureMigration6To7 : Migration(
    FROM_VERSION,
    TO_VERSION,
) {
    companion object {
        private const val FROM_VERSION = 6
        private const val TO_VERSION = 7
    }

    @Suppress("LongMethod")
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TextAdventureMessageEntity_new` (
                `adventureId` TEXT NOT NULL,
                `sender` TEXT NOT NULL,
                `isEnding` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `messageIndex` INTEGER NOT NULL,
                PRIMARY KEY(`adventureId`, `messageIndex`),
                FOREIGN KEY(`adventureId`) REFERENCES `TextAdventureEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `TextAdventureMessageEntity_new` (
                `adventureId`,
                `sender`,
                `isEnding`,
                `createdAt`,
                `messageIndex`
            )
            SELECT
                `adventureId`,
                `sender`,
                `isEnding`,
                `createdAt`,
                `messageIndex`
            FROM `TextAdventureMessageEntity`
            """.trimIndent()
        )
        db.execSQL("DROP INDEX IF EXISTS `index_TextAdventureMessageEntity_adventureId`")
        db.execSQL(
            """
            ALTER TABLE `TextAdventureMessageEntity` RENAME TO `TextAdventureMessageEntity_old`
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE `TextAdventureMessageEntity_new` RENAME TO `TextAdventureMessageEntity`")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TextAdventureSentenceEntity_new` (
                `adventureId` TEXT NOT NULL,
                `messageIndex` INTEGER NOT NULL,
                `paragraphIndex` INTEGER NOT NULL,
                `sentenceIndex` INTEGER NOT NULL,
                `language` TEXT NOT NULL,
                `text` TEXT NOT NULL,
                PRIMARY KEY(`adventureId`, `messageIndex`, `paragraphIndex`, `sentenceIndex`, `language`),
                FOREIGN KEY(`adventureId`, `messageIndex`) REFERENCES `TextAdventureMessageEntity`(`adventureId`, `messageIndex`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `TextAdventureSentenceEntity_new` (
                `adventureId`,
                `messageIndex`,
                `paragraphIndex`,
                `sentenceIndex`,
                `language`,
                `text`
            )
            SELECT
                messages.`adventureId`,
                messages.`messageIndex`,
                sentences.`paragraphIndex`,
                sentences.`sentenceIndex`,
                sentences.`language`,
                sentences.`text`
            FROM `TextAdventureSentenceEntity` AS sentences
            INNER JOIN `TextAdventureMessageEntity_old` AS messages
                ON messages.`id` = sentences.`messageId`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `TextAdventureSentenceEntity`")
        db.execSQL("DROP TABLE `TextAdventureMessageEntity_old`")
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_TextAdventureMessageEntity_adventureId`
            ON `TextAdventureMessageEntity` (`adventureId`)
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE `TextAdventureSentenceEntity_new` RENAME TO `TextAdventureSentenceEntity`")
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_TextAdventureSentenceEntity_adventureId_messageIndex`
            ON `TextAdventureSentenceEntity` (`adventureId`, `messageIndex`)
            """.trimIndent()
        )
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureSummaryView`")
        db.execSQL("DROP VIEW IF EXISTS `TextAdventureMessageSentenceView`")
        db.execSQL(
            """
            |CREATE VIEW `TextAdventureSummaryView` AS SELECT
            |        adventure.id AS adventureId,
            |        adventure.title AS title,
            |        adventure.learningLanguage AS learningLanguage,
            |        adventure.translationLanguage AS translationLanguage,
            |        adventure.updatedAt AS updatedAt,
            |        COALESCE(latest.isEnding, 0) AS isComplete
            |    FROM TextAdventureEntity AS adventure
            |    LEFT JOIN TextAdventureMessageEntity AS latest
            |        ON latest.adventureId = adventure.id
            |        AND latest.messageIndex = (
            |            SELECT MAX(messageIndex)
            |            FROM TextAdventureMessageEntity
            |            WHERE adventureId = adventure.id
            |        )
            """.trimMargin()
        )
        db.execSQL(
            """
            |CREATE VIEW `TextAdventureMessageSentenceView` AS SELECT
            |        adventure.id AS adventureId,
            |        adventure.title AS title,
            |        adventure.learningLanguage AS learningLanguage,
            |        adventure.translationLanguage AS translationLanguage,
            |        messages.messageIndex AS messageIndex,
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
            |        ON sentences.adventureId = messages.adventureId
            |        AND sentences.messageIndex = messages.messageIndex
            """.trimMargin()
        )
    }
}
