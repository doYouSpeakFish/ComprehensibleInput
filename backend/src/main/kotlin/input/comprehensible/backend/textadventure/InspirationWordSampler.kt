package input.comprehensible.backend.textadventure

import kotlin.random.Random

/**
 * Supplies a small random sample of words drawn from a large, curated word list.
 *
 * The sample is handed to the language model purely as inspiration so that generated
 * adventures vary more from one another. The model is free to ignore the words entirely.
 */
fun interface InspirationWordSampler {
    fun sample(): List<String>
}

/**
 * Default sampler backed by a bundled word list resource.
 *
 * The bundled list is the EFF "large" word list, which is deliberately curated to
 * exclude offensive or uncomfortable words.
 */
class DefaultInspirationWordSampler(
    private val words: List<String> = loadWords(),
    private val sampleSize: Int = DEFAULT_SAMPLE_SIZE,
    private val random: Random = Random.Default,
) : InspirationWordSampler {
    override fun sample(): List<String> {
        if (words.isEmpty()) return emptyList()
        val count = sampleSize.coerceIn(0, words.size)
        return words.shuffled(random).take(count)
    }

    companion object {
        const val DEFAULT_SAMPLE_SIZE = 100
        private const val WORDS_RESOURCE = "/textadventure/inspiration-words.txt"

        private fun loadWords(): List<String> =
            DefaultInspirationWordSampler::class.java.getResourceAsStream(WORDS_RESOURCE)
                ?.bufferedReader()
                ?.useLines { lines -> lines.map(String::trim).filter(String::isNotEmpty).toList() }
                ?: error("Inspiration word list resource not found: $WORDS_RESOURCE")
    }
}
