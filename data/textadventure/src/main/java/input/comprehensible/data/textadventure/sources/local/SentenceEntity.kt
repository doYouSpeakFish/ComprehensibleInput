package input.comprehensible.data.textadventure.sources.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One sentence of a [MessageEntity], in both the learning language ([text]) and the translation
 * ([translation]). [paragraphIndex] and [sentenceIndex] preserve the message's paragraph structure
 * and reading order. Foreign-keyed to its message so it is removed with it.
 */
@Entity(
    tableName = "sentence",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("messageId")],
)
data class SentenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: String,
    val paragraphIndex: Int,
    val sentenceIndex: Int,
    val text: String,
    val translation: String,
)
