package input.comprehensible.data.textadventures.sources.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender

@Entity
data class TextAdventureEntity(
    @PrimaryKey val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val isComplete: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity
data class TextAdventureMessageEntity(
    @PrimaryKey val id: String,
    val adventureId: String,
    val sender: TextAdventureMessageSender,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
    val createdAt: Long,
    val messageIndex: Int,
)
