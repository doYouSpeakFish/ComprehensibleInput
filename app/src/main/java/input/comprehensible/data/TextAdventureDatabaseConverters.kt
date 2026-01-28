package input.comprehensible.data

import androidx.room.TypeConverter
import input.comprehensible.data.textadventure.model.TextAdventureRole
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class TextAdventureDatabaseConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val serializer = ListSerializer(String.serializer())
        return Json.encodeToString(serializer, value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val serializer = ListSerializer(String.serializer())
        return Json.decodeFromString(serializer, value)
    }

    @TypeConverter
    fun fromRole(value: TextAdventureRole): String = value.name

    @TypeConverter
    fun toRole(value: String): TextAdventureRole = TextAdventureRole.valueOf(value)
}
