package input.comprehensible.di

import input.comprehensible.data.AppDb
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
    }
}
