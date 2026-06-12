package input.comprehensible.data.textadventure.sources.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A [MessageEntity] together with its [SentenceEntity] rows, read in one query by Room.
 */
data class MessageWithSentences(
    @Embedded val message: MessageEntity,
    @Relation(parentColumn = "id", entityColumn = "messageId")
    val sentences: List<SentenceEntity>,
)
