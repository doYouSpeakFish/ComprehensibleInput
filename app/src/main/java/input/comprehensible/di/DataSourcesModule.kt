package input.comprehensible.di

import input.comprehensible.data.AppDb
import input.comprehensible.data.languages.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.DefaultStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.textadventure.sources.local.TextAdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.remote.DefaultTextAdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.TextAdventureRemoteDataSource

object DataSourcesModule {
    fun inject() {
        StoriesInfoLocalDataSource.inject { AppDb.getInstance().getStoriesInfoDao() }
        StoriesLocalDataSource.inject { DefaultStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject { DefaultLanguageSettingsLocalDataSource() }
        TextAdventureLocalDataSource.inject { AppDb.getInstance().getTextAdventureDao() }
        TextAdventureRemoteDataSource.inject { DefaultTextAdventureRemoteDataSource() }
    }
}
