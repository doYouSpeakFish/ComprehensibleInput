package input.comprehensible

import android.app.Application
import input.comprehensible.data.AppDb
import input.comprehensible.data.languages.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.DefaultStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.di.AppScopeProvider
import input.comprehensible.di.IoDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        IoDispatcherProvider.inject { Dispatchers.IO }
        AppScopeProvider.inject { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

        val appDb = AppDb.getInstance(context = this)
        StoriesInfoLocalDataSource.inject { appDb.getStoriesInfoDao() }
        StoriesLocalDataSource.inject { DefaultStoriesLocalDataSource(context = this) }
        LanguageSettingsLocalDataSource.inject { DefaultLanguageSettingsLocalDataSource(context = this) }
    }
}
