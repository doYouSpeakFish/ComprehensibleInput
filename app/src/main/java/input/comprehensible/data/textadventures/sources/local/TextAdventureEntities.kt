package input.comprehensible.data.textadventures.sources.local

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender

@Entity
data class TextAdventureEntity(
    @PrimaryKey val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TextAdventureEntity::class,
            parentColumns = ["id"],
            childColumns = ["adventureId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TextAdventureMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("adventureId"), Index("parentId")],
)
data class TextAdventureMessageEntity(
    @PrimaryKey val id: String,
    val adventureId: String,
    val parentId: String?,
    val sender: TextAdventureMessageSender,
    val isEnding: Boolean,
    val createdAt: Long,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TextAdventureMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("messageId")],
    primaryKeys = ["messageId", "paragraphIndex", "sentenceIndex", "language"],
)
data class TextAdventureSentenceEntity(
    val messageId: String,
    val paragraphIndex: Int,
    val sentenceIndex: Int,
    val language: String,
    val text: String,
)

@DatabaseView(
    """
    SELECT
        adventure.id AS adventureId,
        adventure.title AS title,
        adventure.learningLanguage AS learningLanguage,
        adventure.translationLanguage AS translationLanguage,
        adventure.updatedAt AS updatedAt,
        CASE WHEN EXISTS (
            SELECT 1 FROM TextAdventureMessageEntity m
            WHERE m.adventureId = adventure.id AND m.isEnding = 1
        ) THEN 1 ELSE 0 END AS isComplete
    FROM TextAdventureEntity AS adventure
    """
)
data class TextAdventureSummaryView(
    val adventureId: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    val isComplete: Boolean,
)

@DatabaseView(
    """
    SELECT
        adventure.id AS adventureId,
        adventure.title AS title,
        adventure.learningLanguage AS learningLanguage,
        adventure.translationLanguage AS translationLanguage,
        messages.id AS messageId,
        messages.parentId AS parentId,
        messages.createdAt AS createdAt,
        messages.sender AS sender,
        messages.isEnding AS isEnding,
        sentences.paragraphIndex AS paragraphIndex,
        sentences.sentenceIndex AS sentenceIndex,
        sentences.language AS language,
        sentences.text AS text
    FROM TextAdventureEntity AS adventure
    INNER JOIN TextAdventureMessageEntity AS messages
        ON messages.adventureId = adventure.id
    INNER JOIN TextAdventureSentenceEntity AS sentences
        ON sentences.messageId = messages.id
    """
)
data class TextAdventureMessageSentenceView(
    val adventureId: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val messageId: String,
    val parentId: String?,
    val createdAt: Long,
    val sender: TextAdventureMessageSender,
    val isEnding: Boolean,
    val paragraphIndex: Int,
    val sentenceIndex: Int,
    val language: String,
    val text: String,
)
