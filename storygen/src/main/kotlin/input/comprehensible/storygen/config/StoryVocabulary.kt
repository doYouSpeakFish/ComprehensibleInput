package input.comprehensible.storygen.config

import kotlin.random.Random

object StoryVocabulary {
    private val genres = listOf(
        "Science Fiction",
        "Fantasy",
        "Mystery",
        "Historical Adventure",
        "Mythic Quest",
        "Post-Apocalyptic Journey",
        "Underwater Expedition",
        "Space Opera",
        "Cozy Detective Tale",
        "Urban Legend",
    )

    private val vocabulary = listOf(
        "lantern",
        "compass",
        "whisper",
        "labyrinth",
        "starlight",
        "ember",
        "tidal",
        "quill",
        "harbor",
        "echo",
        "glyph",
        "ridge",
        "crystal",
        "horizon",
        "thunder",
        "meadow",
        "harvest",
        "aurora",
        "cavern",
        "shard",
        "glimmer",
        "ember",
        "moonlit",
        "willow",
        "solstice",
        "riverstone",
        "journeyman",
        "sapphire",
        "cathedral",
        "voyager",
    ).distinct()

    fun randomGenre(random: Random): String = genres.random(random)

    fun inspirationWords(random: Random, count: Int): List<String> {
        require(count <= vocabulary.size) {
            "Cannot sample $count inspiration words from a list of ${vocabulary.size}"
        }
        return vocabulary.shuffled(random).take(count)
    }

    fun allInspirationWords(): List<String> = vocabulary
}
