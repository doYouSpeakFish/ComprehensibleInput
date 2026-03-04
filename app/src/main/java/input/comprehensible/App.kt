package input.comprehensible

import android.app.Application
import input.comprehensible.di.ApplicationProvider
import input.comprehensible.di.CoroutinesModule
import input.comprehensible.di.DataSourcesModule
import input.comprehensible.di.FeatureFlagsModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }
}

fun Application.injectDependencies() {
    FeatureFlagsModule.inject()
    ApplicationProvider.inject { this }
    CoroutinesModule.inject()
    DataSourcesModule.inject()
}
