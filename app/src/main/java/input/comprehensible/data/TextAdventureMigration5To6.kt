package input.comprehensible.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TextAdventureMigration5To6 : Migration(
    FROM_VERSION,
    TO_VERSION,
) {
    companion object {
        private const val FROM_VERSION = 5
        private const val TO_VERSION = 6
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TextAdventureSentenceEntity_new` (
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
            INSERT INTO `TextAdventureSentenceEntity_new` (
                `messageId`,
                `paragraphIndex`,
                `sentenceIndex`,
                `language`,
                `text`
            )
            SELECT
                paragraphs.`messageId`,
                paragraphs.`paragraphIndex`,
                sentences.`sentenceIndex`,
                sentences.`language`,
                sentences.`text`
            FROM `TextAdventureSentenceEntity` AS sentences
            INNER JOIN `TextAdventureParagraphEntity` AS paragraphs
                ON paragraphs.`id` = sentences.`paragraphId`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `TextAdventureSentenceEntity`")
        db.execSQL("DROP TABLE `TextAdventureParagraphEntity`")
        db.execSQL("ALTER TABLE `TextAdventureSentenceEntity_new` RENAME TO `TextAdventureSentenceEntity`")
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_TextAdventureSentenceEntity_messageId`
            ON `TextAdventureSentenceEntity` (`messageId`)
            """.trimIndent()
        )
    }
}
