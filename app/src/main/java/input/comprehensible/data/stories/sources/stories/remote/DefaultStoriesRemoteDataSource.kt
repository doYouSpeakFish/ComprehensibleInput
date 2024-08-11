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
    private val planningModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                maxOutputTokens = 8192
                temperature = 2f
            }
        )
    }
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
        val plan = planStory(inspirationWords)
        val prompt = """
            Write a story in the language $learningLanguage. The story should be written using 
            language that is appropriate for an A2 level speaker.
            
            Use the following plan as a guide to structure your story.
            
            $plan
            
            Re-write the story from the above plan, adding description to flesh it out and to set
            the scene. Tweak the story where necessary to make it flow naturally.
        """.trimIndent()
        val response = storyGenerationModel.generateContent(prompt = prompt)
        val story = requireNotNull(response.text) {
            "No story content received from the AI model."
        }
        Timber.d("Generated story: $story")
        return story
    }

    private suspend fun planStory(
        inspirationWords: String,
    ): String {
        Timber.d("Planning story with inspiration words: $inspirationWords")
        val prompt = """
            Plan a story.
            
            Work as much of the following vocabulary into the story as possible:
            $inspirationWords
            
            Show your chain of thought as you brainstorm ideas to make this story as interesting as 
            possible. Iterate as you develop the plan, being self critical and actively changing
            your mind and exploring ideas in order to flesh out important details, fill in blanks,
            correct aspects that don't make sense, and work towards writing the best story possible.
            Make sure to include a beginning, middle, and end, and to have a clear conflict and
            resolution.
            
            Don't worry too much about structuring this planning session. Just write down your
            thoughts as they come to you in a free form creative process.
            
            After planning, write an initial first draft of the story. The story should be roughly 
            1500 words long and written in language suitable for an A2 level speaker. Don't pick a
            title until the end.
            
            After writing the first draft, give your thoughts on how the story could be improved and
            any plot holes or things that don't make sense that should be addressed.
            
            Rewrite the ending a few times to explore different options.
            Ask yourself the following questions:
            
            - Is the ending satisfying?
            - Does the ending actually resolve the conflict, or does the story feel unfinished?
            - Is the ending too predictable?
            - Is the ending too abrupt?
            
            The very last thing you should write is whether or not you are happy with the ending. If
            the answer is no, keep writing until you find the perfect conclusion to your story.
        """.trimIndent()
        val response = planningModel.generateContent(prompt = prompt)
        val plan = requireNotNull(response.text) {
            "No story plan received from the AI model."
        }
        Timber.d("Generated story plan: $plan")
        return plan
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
            
            $story
        """.trimIndent()
        )
        val translation = requireNotNull(response.text) {
            "No translation received from the AI model."
        }
        Timber.d("Translated story: $translation")
        return translation
    }
}
