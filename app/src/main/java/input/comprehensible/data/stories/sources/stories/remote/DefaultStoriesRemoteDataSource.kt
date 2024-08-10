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
    ): AiStoryData {
        val id = UUID.randomUUID().toString()

        Timber.i("Generating story")
        val storyJson = generateStory(learningLanguage)
        val story = Json.decodeFromString<StoryData>(storyJson)
            .copy(id = id)

        Timber.i("Translating story")
        val translationJson = translateStory(storyJson, translationLanguage)
        val translation = Json.decodeFromString<StoryData>(translationJson)
            .copy(id = id)

        return AiStoryData(
            content = story,
            translations = translation
        )
    }

    private suspend fun generateStory(
        learningLanguage: String,
    ): String {
        val response = model.generateContent(
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
        )
        val story = requireNotNull(response.text) {
            "No story content received from the AI model."
        }
        Timber.d("Generated story: $story")
        return story.firstJsonObject()
    }

    private suspend fun translateStory(
        story: String,
        translationLanguage: String,
    ): String {
        val response = model.generateContent(
            prompt = """
            Translate the story in the following JSON into the language $translationLanguage.
            Retain the same structure and keys of the JSON.
            
            $story
        """.trimIndent()
        )
        val translation = requireNotNull(response.text) {
            "No translation received from the AI model."
        }
        Timber.d("Translated story: $translation")
        return translation.firstJsonObject()
    }
}

private fun String.firstJsonObject(): String {
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
        error("No JSON object found in string: $this")
    }
    return substring(startIndex, endIndex + 1)
}
