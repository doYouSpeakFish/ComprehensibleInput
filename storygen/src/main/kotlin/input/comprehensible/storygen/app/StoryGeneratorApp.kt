package input.comprehensible.storygen.app

import input.comprehensible.storygen.config.StoryGenerationConfig
import input.comprehensible.storygen.core.StoryGenerationException
import input.comprehensible.storygen.core.StoryGenerator
import input.comprehensible.storygen.provider.StoryModelClientFactory
import input.comprehensible.storygen.output.StoryFileWriter
import input.comprehensible.storygen.rate.SimpleRateLimiter
import input.comprehensible.storygen.util.LocalProperties
import java.nio.file.Path
import kotlin.random.Random
import kotlinx.coroutines.runBlocking

private const val GEMINI_MODEL = "gemini-2.5-pro"
private const val API_KEY_PROPERTY = "geminiApiKey"

fun main() = runBlocking {
    val apiKey = LocalProperties.readProperty(API_KEY_PROPERTY)
    val config = StoryGenerationConfig()
    val rateLimiter = SimpleRateLimiter(
        maxRequests = config.maxRequests,
        minDelay = config.minDelayBetweenRequests,
    )
    val storyClient = StoryModelClientFactory.googleGemini(apiKey = apiKey, modelId = GEMINI_MODEL)
    val generator = StoryGenerator(storyClient, rateLimiter, Random(System.currentTimeMillis()))
    try {
        val story = generator.generateStory(config)
        val outputDirectory = Path.of("storygen", "output")
        val writer = StoryFileWriter(outputDirectory)
        val file = writer.write(story)
        println("Story saved to ${file.toAbsolutePath()}")
    } catch (error: StoryGenerationException) {
        System.err.println("Failed to generate story: ${error.message}")
        throw error
    }
}
