package input.comprehensible.data.textadventures.sources.local

import androidx.room.TypeConverter
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import org.json.JSONArray

class TextAdventureConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = JSONArray(value).toString()

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        val jsonArray = JSONArray(value)
        return List(jsonArray.length()) { index -> jsonArray.getString(index) }
    }

    @TypeConverter
    fun fromSender(value: TextAdventureMessageSender): String = value.name

    @TypeConverter
    fun toSender(value: String): TextAdventureMessageSender =
        TextAdventureMessageSender.valueOf(value)
}
