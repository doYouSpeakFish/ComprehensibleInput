package input.comprehensible.backend.textadventure.testing

import input.comprehensible.backend.textadventure.InspirationWordSampler

class FakeInspirationWordSampler : InspirationWordSampler {
    private val queuedSamples = ArrayDeque<List<String>>()
    var defaultSample: List<String> = emptyList()

    fun enqueueSample(words: List<String>) {
        queuedSamples.add(words)
    }

    override fun sample(): List<String> = queuedSamples.removeFirstOrNull() ?: defaultSample
}
