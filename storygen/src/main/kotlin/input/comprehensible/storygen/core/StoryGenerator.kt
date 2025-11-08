package input.comprehensible.storygen.core

import input.comprehensible.storygen.config.StoryGenerationConfig
import input.comprehensible.storygen.config.StoryVocabulary
import input.comprehensible.storygen.provider.StoryModelClient
import input.comprehensible.storygen.provider.StoryModelClientException
import input.comprehensible.storygen.provider.StoryModelSegment
import input.comprehensible.storygen.rate.RateLimitReachedException
import input.comprehensible.storygen.rate.RateLimiter
import kotlin.random.Random

class StoryGenerator(
    private val storyClient: StoryModelClient,
    private val rateLimiter: RateLimiter,
    private val random: Random = Random.Default,
) {

    suspend fun generateStory(config: StoryGenerationConfig = StoryGenerationConfig()): GeneratedStory {
        val genre = StoryVocabulary.randomGenre(random)
        val inspirationWords = StoryVocabulary.inspirationWords(random, config.inspirationWordCount)
        val context = StoryGenerationContext(config, genre, inspirationWords)
        val root = generateNode(context, emptyList(), depth = 0)
        return GeneratedStory(
            genre = genre,
            inspirationWords = inspirationWords,
            root = root,
        )
    }

    private suspend fun generateNode(
        context: StoryGenerationContext,
        path: List<BranchStep>,
        depth: Int,
    ): StoryNode {
        val segment = requestSegmentWithValidation(context, path, depth)
        val nextChoices = if (segment.isEnding) {
            emptyList()
        } else {
            segment.choices.map { choice ->
                val nextPath = path + BranchStep(choice.prompt, choice.summary)
                val nextNode = generateNode(context, nextPath, depth + 1)
                StoryChoice(
                    prompt = choice.prompt,
                    summary = choice.summary,
                    next = nextNode,
                )
            }
        }
        return StoryNode(
            title = segment.title,
            narrative = segment.narrative,
            choices = nextChoices,
        )
    }

    private suspend fun requestSegmentWithValidation(
        context: StoryGenerationContext,
        path: List<BranchStep>,
        depth: Int,
    ): StoryModelSegment {
        var attempt = 0
        var lastError: Throwable? = null
        while (attempt < context.config.maxRetriesPerSegment) {
            attempt += 1
            try {
                rateLimiter.acquire()
            } catch (limitError: RateLimitReachedException) {
                val detail = limitError.message?.let { ": $it" } ?: ""
                throw StoryGenerationException("Rate limit reached$detail", limitError)
            }
            val prompt = StoryModelPrompt(
                genre = context.genre,
                inspirationWords = context.inspiration,
                pathSummary = path,
                depth = depth,
                maxDepth = context.config.maxDepth,
            )
            val segment = try {
                storyClient.requestSegment(prompt)
            } catch (error: StoryModelClientException) {
                lastError = error
                continue
            }
            val validationError = validateSegment(segment, depth, context.config.maxDepth)
            if (validationError == null) {
                return segment
            } else {
                lastError = StoryGenerationException(validationError)
            }
        }
        val message = buildString {
            append("Story provider failed to deliver a valid segment after ${context.config.maxRetriesPerSegment} attempts")
            lastError?.message?.let { append(": $it") }
        }
        throw StoryGenerationException(message, lastError)
    }

    private fun validateSegment(segment: StoryModelSegment, depth: Int, maxDepth: Int): String? {
        if (segment.title.isBlank()) {
            return "Story segment is missing a title"
        }
        if (segment.narrative.isBlank()) {
            return "Story segment is missing narrative text"
        }
        val isAtMaxDepth = depth >= maxDepth
        if (isAtMaxDepth && !segment.isEnding) {
            return "Maximum depth reached but the story provider still offered choices"
        }
        if (segment.isEnding && segment.choices.isNotEmpty()) {
            return "Ending segments must not include choices"
        }
        if (!segment.isEnding && segment.choices.isEmpty()) {
            return "Non-ending segments must include at least one choice"
        }
        segment.choices.forEach { choice ->
            if (choice.prompt.isBlank()) {
                return "A choice prompt was empty"
            }
            if (choice.summary.isBlank()) {
                return "A choice summary was empty"
            }
        }
        return null
    }

    private data class StoryGenerationContext(
        val config: StoryGenerationConfig,
        val genre: String,
        val inspiration: List<String>,
    )
}

class StoryGenerationException(message: String, cause: Throwable? = null) : Exception(message, cause)
