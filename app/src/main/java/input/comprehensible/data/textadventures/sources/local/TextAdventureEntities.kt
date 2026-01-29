package input.comprehensible.data.textadventures.sources.local

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
        )
    ],
    indices = [Index("adventureId")],
)
data class TextAdventureMessageEntity(
    @PrimaryKey val id: String,
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
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE,
        ),
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
