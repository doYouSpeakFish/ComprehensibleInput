package input.comprehensible.data.stories.sources.stories.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig
import input.comprehensible.BuildConfig
import kotlinx.serialization.json.Json
import timber.log.Timber

private val storyResponseSchema = Schema.obj(
    name = "story",
    description = "A story",
    Schema.str(
        name = "title",
        description = "The title of the story",
    ),
    Schema.str(
        name = "titleTranslation",
        description = "The title of the story",
    ),
    Schema.arr(
        name = "content",
        description = "the content of the story",
        Schema.obj(
            name = "paragraph",
            description = "A paragraph in a story",
            Schema.arr(
                name = "sentences",
                description = "the sentences in the paragraph",
                Schema.obj(
                    name = "sentence",
                    description = "A sentence in the paragraph",
                    Schema.str(
                        name = "text",
                        description = "The sentence in the learning language",
                    ),
                    Schema.str(
                        name = "translation",
                        description = "The sentence in the translation language",
                    ),
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
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                maxOutputTokens = 8192
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
        val storyJson = generateStory(learningLanguage, translationLanguage)
        return parser.decodeFromString<AiStoryData>(storyJson)
    }

    private suspend fun generateStory(
        learningLanguage: String,
        translationLanguage: String,
    ): String {
        val finalDraft = writeStory(learningLanguage)
        val prompt = """
            $finalDraft
            
            Write the above story into a JSON structure.
            
            After writing each sentence in each paragraph of the story, include an accompanying
            translation sentence in the language $translationLanguage. This will aid language
            learners in understanding the story, so make sure the translation is as literal as
            possible.
        """.trimIndent()
        Timber.d("Generating story")
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

    private suspend fun writeStory(learningLanguage: String): String {
        val inspirationWords = a2Vocabulary
            .shuffled()
            .take(10)
            .joinToString(separator = ", ")
        val plan = planStory(inspirationWords)
        val prompt = """
            Target language level: A1
            
            Inspiration words:
            $inspirationWords
            
            Plan:
            $plan
            
            Based on the above plan, write the final draft of this story in the language 
            $learningLanguage.
            
            Remember, it is extremely important that the story is accessible to A1 level speakers,
            so simplify the language where necessary and avoid using complex sentence structures.
            This is incredibly important. Make sure A1 level speakers can understand the story.
        """.trimIndent()
        Timber.d("Writing final draft of story")
        val response = planningModel.generateContent(prompt = prompt)
        response.usageMetadata?.promptTokenCount?.let { tokenCount ->
            Timber.d("Final draft prompt token count: $tokenCount")
        }
        response.usageMetadata?.candidatesTokenCount?.let { tokenCount ->
            Timber.d("Final draft candidates token count: $tokenCount")
        }
        val story = requireNotNull(response.text) {
            "No story content received from the AI model."
        }
        Timber.d("Generated final draft: $story")
        return story
    }

    private suspend fun planStory(inspirationWords: String): String {
        val inspirationNames = names
            .shuffled()
            .take(10)
            .joinToString(separator = ", ")
        val genre = genres.random()
        Timber.d("Planning $genre story with inspiration words: $inspirationWords")
        val prompt = """
            Plan a story.
            
            Genre: $genre
            
            Work as much of the following vocabulary into the story as possible:
            $inspirationWords
            
            Below are some names that can be used in the story. You don't need to use all the names,
            they are just for inspiration:
            $inspirationNames
            
            Show your chain of thought as you brainstorm ideas to make this story as interesting as 
            possible. Iterate as you develop the plan, being self critical and actively changing
            your mind and exploring ideas in order to flesh out important details, fill in blanks,
            correct aspects that don't make sense, and work towards writing the best story possible.
            Make sure to include a beginning, middle, and end, and to have a clear conflict and
            resolution.
            
            Don't worry too much about structuring this planning session. Just write down your
            thoughts as they come to you in a free form creative process.
            
            After planning, write an initial first draft of the story. The story should be roughly 
            1500 words long. It is very important that the language is accessible to an A1 level
            speaker. Remember, A1 level speakers are still learning the language, so the story
            should be simple and easy to understand. Don't pick a title until the end.
            
            After writing the first draft, give your thoughts on how the story could be improved and
            any plot holes or things that don't make sense that should be addressed. Most
            importantly, consider whether or not the story is accessible to an A1 level speaker.
            
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
        response.usageMetadata?.promptTokenCount?.let { tokenCount ->
            Timber.d("Planning prompt token count: $tokenCount")
        }
        response.usageMetadata?.candidatesTokenCount?.let { tokenCount ->
            Timber.d("Planning candidates token count: $tokenCount")
        }
        val plan = requireNotNull(response.text) {
            "No story plan received from the AI model."
        }
        Timber.d("Generated story plan: $plan")
        return plan
    }
}
