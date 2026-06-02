package input.comprehensible.di

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
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.sources.remote.DefaultTextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource

object DataSourcesModule {
    fun inject() {
        StoriesInfoLocalDataSource.inject { AppDb.getInstance().getStoriesInfoDao() }
        StoriesLocalDataSource.inject { DefaultStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject { DefaultLanguageSettingsLocalDataSource() }
        TextAdventuresLocalDataSource.inject { AppDb.getInstance().getTextAdventuresDao() }
        TextAdventureRemoteDataSource.inject { DefaultTextAdventureRemoteDataSource() }
        AccountRemoteDataSource.inject { DefaultAccountRemoteDataSource() }
        AccountLocalDataSource.inject { DefaultAccountLocalDataSource() }
    }
}
