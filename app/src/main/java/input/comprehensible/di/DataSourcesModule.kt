package input.comprehensible.di

import input.comprehensible.BuildConfig
import input.comprehensible.data.AppDb
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.account.sources.remote.DefaultAccountRemoteDataSource
import input.comprehensible.data.languagesettings.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languagesettings.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.DefaultStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.DefaultAdventureRemoteDataSource

object DataSourcesModule {
    fun inject() {
        StoriesInfoLocalDataSource.inject { AppDb.getInstance().getStoriesInfoDao() }
        UserLocalDataSource.inject { AppDb.getInstance().getUserDao() }
        AdventureLocalDataSource.inject { AppDb.getInstance().getAdventureDao() }
        AdventureRemoteDataSource.inject {
            DefaultAdventureRemoteDataSource(
                baseUrl = BuildConfig.BACKEND_BASE_URL,
                apiKey = BuildConfig.BACKEND_API_KEY,
            )
        }
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
