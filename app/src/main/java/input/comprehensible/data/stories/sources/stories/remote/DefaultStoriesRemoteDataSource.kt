package input.comprehensible.data.stories.sources.stories.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig
import input.comprehensible.BuildConfig
import input.comprehensible.data.stories.sources.stories.model.StoryData
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.UUID

private val storyResponseSchema = Schema.obj(
    name = "story",
    description = "A story",
    Schema.str(
        name = "title",
        description = "The title of the story",
    ),
    Schema.arr(
        name = "content",
        description = "the content of the story",
        Schema.obj(
            name = "story_element",
            description = "An element of a story",
            Schema.enum(
                name = "type",
                description = "The type of story element",
                values = listOf("paragraph"),
            ),
            Schema.arr(
                name = "sentences",
                description = "the sentences in the paragraph",
                Schema.str(
                    name = "sentence",
                    description = "A sentence in the paragraph",
                ),
            ),
        ),
    ),
)

val parser = Json {
    ignoreUnknownKeys = true
}

/**
 * The default implementation of [StoriesRemoteDataSource].
 */
class DefaultStoriesRemoteDataSource : StoriesRemoteDataSource {
    private val storyGenerationModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                maxOutputTokens = 8192
                responseMimeType = "application/json"
                responseSchema = storyResponseSchema
            }
        )
    }

    override suspend fun generateAiStory(
        learningLanguage: String,
        translationLanguage: String
    ): AiStoryData {
        val id = UUID.randomUUID().toString()

        Timber.i("Generating story $id")
        val storyJson = generateStory(learningLanguage)
        val story = parser.decodeFromString<StoryData>(storyJson)
            .copy(id = id)

        Timber.i("Translating story $id")
        val translationJson = translateStory(storyJson, translationLanguage)
        val translation = parser.decodeFromString<StoryData>(translationJson)
            .copy(id = id)

        return AiStoryData(
            content = story,
            translations = translation
        )
    }

    private suspend fun generateStory(
        learningLanguage: String,
    ): String {
        val inspirationWords = a2Vocabulary
            .shuffled()
            .take(10)
            .joinToString(separator = ", ")
        Timber.d("Generating story with inspiration words: $inspirationWords")
        val prompt = """
            Write a story in the language $learningLanguage. The story should be written using 
            language that is appropriate for an A2 level speaker.
            
            Work as much of the following vocabulary into the story as possible:
            $inspirationWords
        """.trimIndent()
        val response = storyGenerationModel.generateContent(prompt = prompt)
        response.usageMetadata?.promptTokenCount?.let { tokenCount ->
            Timber.d("Story prompt token count: $tokenCount")
        }
        response.usageMetadata?.candidatesTokenCount?.let { tokenCount ->
            Timber.d("Story candidates token count: $tokenCount")
        }
        val story = requireNotNull(response.text) {
            "No story content received from the AI model."
        }
        Timber.d("Generated story: $story")
        return story
    }

    private suspend fun translateStory(
        story: String,
        translationLanguage: String,
    ): String {
        val response = storyGenerationModel.generateContent(
            prompt = """
            Translate the story in the following JSON into the language $translationLanguage.
            Retain the same structure and keys of the JSON. This translation is to assist language
            learners, so each sentence should be translated as literally as possible, without
            changing the meaning of the sentence.
            
            Be careful to ensure that the translation contains the same number of paragraphs and
            each paragraph contains the same number of sentences as the original story. Ensure
            quotation marks are properly escaped in the translation, to avoid this creating extra
            sentences.
            
            $story
        """.trimIndent()
        )
        response.usageMetadata?.promptTokenCount?.let { tokenCount ->
            Timber.d("Translation prompt token count: $tokenCount")
        }
        response.usageMetadata?.candidatesTokenCount?.let { tokenCount ->
            Timber.d("Translation candidates token count: $tokenCount")
        }
        val translation = requireNotNull(response.text) {
            "No translation received from the AI model."
        }
        Timber.d("Translated story: $translation")
        return translation
    }
}
