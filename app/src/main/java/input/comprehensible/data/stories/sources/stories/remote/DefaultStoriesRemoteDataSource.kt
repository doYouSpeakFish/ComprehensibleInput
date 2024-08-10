package input.comprehensible.data.stories.sources.stories.remote

import com.google.ai.client.generativeai.GenerativeModel
import input.comprehensible.BuildConfig
import input.comprehensible.data.stories.sources.stories.model.StoryData
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.UUID

/**
 * The default implementation of [StoriesRemoteDataSource].
 */
class DefaultStoriesRemoteDataSource : StoriesRemoteDataSource {
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
        )
    }

    override suspend fun generateAiStory(
        learningLanguage: String,
        translationLanguage: String
    ): AiStoryData? {
        val storyJson = generateStory(learningLanguage)
            .onFailure { Timber.e(it, "Failed to generate story") }
            .getOrNull() ?: return null
        val translationJson = translateStory(storyJson, translationLanguage)
            .onFailure { Timber.e(it, "Failed to translate story") }
            .getOrNull() ?: return null

        val id = UUID.randomUUID().toString()
        val story = Json.decodeFromString<StoryData>(storyJson)
            .copy(id = id)
        val translation = Json.decodeFromString<StoryData>(translationJson)
            .copy(id = id)

        return AiStoryData(
            content = story,
            translations = translation
        )
    }

    private suspend fun generateStory(
        learningLanguage: String,
    ): Result<String?> = runCatching {
        model.generateContent(
            prompt = """
            Write a story in the language $learningLanguage. The story should be written using
            language that is appropriate for an A2 level speaker.
            Output the story in the following JSON format:
            {
                "title": "Story Title",
                "content": [
                    {
                        "type": "paragraph",
                        "sentences": [
                            "Sentence 1",
                            "Sentence 2",
                            "Sentence 3",
                            "Sentence 4",
                            "Sentence 5"
                        ]
                    }
                ]
            }
        """.trimIndent(),
        ).text?.let {
            Timber.d("Generated story: $it")
            it.firstJsonObjectOrNull()
        }
    }

    private suspend fun translateStory(
        story: String,
        translationLanguage: String,
    ): Result<String?> = runCatching {
        model.generateContent(
            prompt = """
            Translate the story in the following JSON into the language $translationLanguage.
            Retain the same structure and keys of the JSON.
            
            $story
        """.trimIndent()
        ).text?.let {
            Timber.d("Generated translation: $it")
            it.firstJsonObjectOrNull()
        }
    }
}

private fun String.firstJsonObjectOrNull(): String? {
    var startIndex = -1
    var endIndex = -1
    var numberOfOpenBrackets = 0
    for (i in indices) {
        when (this[i]) {
            '{' -> {
                if (numberOfOpenBrackets == 0) {
                    startIndex = i
                }
                numberOfOpenBrackets++
            }

            '}' -> {
                numberOfOpenBrackets--
                if (numberOfOpenBrackets == 0) {
                    endIndex = i
                    break
                }
            }
        }
    }

    if (startIndex == -1 || endIndex == -1) {
        return null
    }
    return substring(startIndex, endIndex + 1)
}
