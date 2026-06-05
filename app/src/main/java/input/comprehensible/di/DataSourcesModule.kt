package input.comprehensible.di

import input.comprehensible.BuildConfig
import input.comprehensible.data.AppDb
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.account.sources.remote.DefaultAccountRemoteDataSource
import input.comprehensible.data.languages.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.DefaultStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource

object DataSourcesModule {
    fun inject() {
        StoriesInfoLocalDataSource.inject { AppDb.getInstance().getStoriesInfoDao() }
        StoriesLocalDataSource.inject { DefaultStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject { DefaultLanguageSettingsLocalDataSource() }
        AccountRemoteDataSource.inject {
            DefaultAccountRemoteDataSource(
                baseUrl = BuildConfig.BACKEND_BASE_URL,
                apiKey = BuildConfig.BACKEND_API_KEY,
            )
        }
        AccountLocalDataSource.inject { DefaultAccountLocalDataSource() }
    }
}
