package input.comprehensible.storygen

import input.comprehensible.storygen.config.StoryGenerationConfig
import input.comprehensible.storygen.config.StoryVocabulary
import input.comprehensible.storygen.core.GeneratedStory
import input.comprehensible.storygen.core.StoryGenerator
import input.comprehensible.storygen.core.StoryModelPrompt
import input.comprehensible.storygen.provider.StoryModelChoice
import input.comprehensible.storygen.provider.StoryModelClient
import input.comprehensible.storygen.provider.StoryModelClientException
import input.comprehensible.storygen.provider.StoryModelSegment
import input.comprehensible.storygen.rate.RateLimiter
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

internal fun adventureScenario(block: AdventureTestDsl.() -> Unit) {
    AdventureTestDsl().apply(block)
}

internal class AdventureTestDsl {
    private val scriptedResponses = ArrayDeque<StoryModelSegment>()
    private val fakeStoryClient = object : StoryModelClient {
        override suspend fun requestSegment(prompt: StoryModelPrompt): StoryModelSegment {
            return scriptedResponses.removeFirstOrNull()
                ?: throw StoryModelClientException("No scripted story response available")
        }
    }
    private val fakeLimiter = object : RateLimiter {
        var limit = Int.MAX_VALUE
        var attemptsMade = 0
        override suspend fun acquire() {
            if (attemptsMade >= limit) {
                throw input.comprehensible.storygen.rate.RateLimitReachedException("Guide spoke too many times")
            }
            attemptsMade += 1
        }

        override val attempts: Int
            get() = attemptsMade
    }

    private var maxDepth: Int = 3
    private var maxRetries: Int = 2
    private var maxRequests: Int = 10
    private var inspirationCount: Int = 4
    private var result: GeneratedStory? = null
    private var failure: Throwable? = null

    fun givenTheGuideWillShare(vararg beats: AdventureBeat) {
        scriptedResponses.clear()
        scriptedResponses.addAll(beats.map { it.toSegment() })
    }

    fun givenTheGuideCanSpeakOnly(times: Int) {
        maxRequests = times
        fakeLimiter.limit = times
    }

    fun givenTheJourneyCanReachAtMost(depth: Int) {
        maxDepth = depth
    }

    fun whenAnAdventureIsRequested() {
        val config = StoryGenerationConfig(
            maxDepth = maxDepth,
            maxRetriesPerSegment = maxRetries,
            maxRequests = maxRequests,
            inspirationWordCount = inspirationCount,
            minDelayBetweenRequests = Duration.ZERO,
        )
        val generator = StoryGenerator(fakeStoryClient, fakeLimiter, Random(0))
        try {
            result = runBlocking { generator.generateStory(config) }
        } catch (error: Throwable) {
            failure = error
        }
    }

    fun thenTheAdventureHasEndings(expected: Int) {
        val story = ensureStory()
        val endings = countEndings(story.root)
        kotlin.test.assertEquals(expected, endings, "Unexpected number of endings")
    }

    fun thenTheAdventureMentionsExactly(words: Int) {
        val story = ensureStory()
        kotlin.test.assertEquals(words, story.inspirationWords.size, "Unexpected inspiration word count")
        kotlin.test.assertTrue(story.inspirationWords.all { StoryVocabulary.allInspirationWords().contains(it) })
    }

    fun thenTheAdventureFailsWithMessage(expected: String) {
        val error = failure ?: throw AssertionError("Expected failure but story was generated")
        kotlin.test.assertTrue(
            error.message?.contains(expected) == true,
            "Expected message to contain '$expected' but was '${error.message}'",
        )
    }

    fun thenTheGuideSpoke(times: Int) {
        kotlin.test.assertEquals(times, fakeLimiter.attempts)
    }

    private fun ensureStory(): GeneratedStory {
        return result ?: throw AssertionError("Story was not generated: ${failure?.message}")
    }

    private fun countEndings(node: input.comprehensible.storygen.core.StoryNode): Int {
        if (node.choices.isEmpty()) {
            return 1
        }
        return node.choices.sumOf { choice ->
            val next = choice.next ?: return@sumOf 0
            countEndings(next)
        }
    }
}

internal data class AdventureBeat(
    val title: String,
    val narrative: String,
    val ending: Boolean,
    val choices: List<Pair<String, String>>,
)

internal fun chapter(title: String, narrative: String, vararg options: Pair<String, String>): AdventureBeat =
    AdventureBeat(title, narrative, ending = false, choices = options.toList())

internal fun finale(title: String, narrative: String): AdventureBeat =
    AdventureBeat(title, narrative, ending = true, choices = emptyList())

internal fun turn(prompt: String, summary: String): Pair<String, String> = prompt to summary

private fun AdventureBeat.toSegment(): StoryModelSegment {
    return StoryModelSegment(
        title = title,
        narrative = narrative,
        isEnding = ending,
        choices = choices.map { (prompt, summary) -> StoryModelChoice(prompt, summary) },
    )
}
