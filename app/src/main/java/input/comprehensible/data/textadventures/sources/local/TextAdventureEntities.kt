package input.comprehensible.data.textadventures.sources.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.DatabaseView
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
        )
    ],
    indices = [Index("adventureId")],
    primaryKeys = ["adventureId", "messageIndex"],
)
data class TextAdventureMessageEntity(
    val adventureId: String,
    val sender: TextAdventureMessageSender,
    val isEnding: Boolean,
    val createdAt: Long,
    val messageIndex: Int,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TextAdventureMessageEntity::class,
            parentColumns = ["adventureId", "messageIndex"],
            childColumns = ["adventureId", "messageIndex"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["adventureId", "messageIndex"])],
    primaryKeys = ["adventureId", "messageIndex", "paragraphIndex", "sentenceIndex", "language"],
)
data class TextAdventureSentenceEntity(
    val adventureId: String,
    val messageIndex: Int,
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
        COALESCE(latest.isEnding, 0) AS isComplete
    FROM TextAdventureEntity AS adventure
    LEFT JOIN TextAdventureMessageEntity AS latest
        ON latest.adventureId = adventure.id
        AND latest.messageIndex = (
            SELECT MAX(messageIndex)
            FROM TextAdventureMessageEntity
            WHERE adventureId = adventure.id
        )
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
        messages.messageIndex AS messageIndex,
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
        ON sentences.adventureId = messages.adventureId
        AND sentences.messageIndex = messages.messageIndex
    """
)
data class TextAdventureMessageSentenceView(
    val adventureId: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val messageIndex: Int,
    val sender: TextAdventureMessageSender,
    val isEnding: Boolean,
    val paragraphIndex: Int,
    val sentenceIndex: Int,
    val language: String,
    val text: String,
)
