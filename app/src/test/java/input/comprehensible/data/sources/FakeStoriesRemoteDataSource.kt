package input.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.data.stories.sources.stories.model.StoryData
import input.comprehensible.data.stories.sources.stories.model.StoryElementData
import input.comprehensible.data.stories.sources.stories.remote.AiStoryData
import input.comprehensible.data.stories.sources.stories.remote.StoriesRemoteDataSource
import input.comprehensible.data.stories.sources.stories.remote.StoriesRemoteDataSourceModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesRemoteDataSource @Inject constructor() : StoriesRemoteDataSource {
    private val germanAiStory = SampleStoriesData.aiGeneratedStory.let {
        StoryData(
            id = it.id,
            title = it.germanTitle,
            content = it.content.map { part ->
                when (part) {
                    is TestStoryPart.Image -> StoryElementData.ImageData(
                        contentDescription = part.contentDescription,
                        path = ""
                    )

                    is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                        sentences = part.germanSentences
                    )
                }
            }
        )
    }
    private val englishAiStory = SampleStoriesData.aiGeneratedStory.let {
        StoryData(
            id = it.id,
            title = it.englishTitle,
            content = it.content.map { part ->
                when (part) {
                    is TestStoryPart.Image -> StoryElementData.ImageData(
                        contentDescription = part.contentDescription,
                        path = ""
                    )

                    is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                        sentences = part.englishSentences
                    )
                }
            }
        )
    }
    private val spanishAiStory = SampleStoriesData.aiGeneratedStory.let {
        StoryData(
            id = it.id,
            title = it.spanishTitle,
            content = it.content.map { part ->
                when (part) {
                    is TestStoryPart.Image -> StoryElementData.ImageData(
                        contentDescription = part.contentDescription,
                        path = ""
                    )

                    is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                        sentences = part.spanishSentences
                    )
                }
            }
        )
    }
    private val aiStoriesByLanguage = mapOf(
        "de" to germanAiStory,
        "en" to englishAiStory,
        "es" to spanishAiStory,
    )

    override suspend fun generateAiStory(
        learningLanguage: String,
        translationLanguage: String
    ) = AiStoryData(
        content = aiStoriesByLanguage[learningLanguage]!!,
        translations = aiStoriesByLanguage[translationLanguage]!!,
    )
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StoriesRemoteDataSourceModule::class]
)
interface FakeStoriesRemoteDataSourceModule {
    @Binds
    @Singleton
    fun provideStoriesRemoteDataSource(
        fakeStoriesRemoteDataSource: FakeStoriesRemoteDataSource
    ): StoriesRemoteDataSource
}
