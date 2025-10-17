package input.comprehensible.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.analytics.AnalyticsLogger
import input.comprehensible.analytics.StubAnalyticsLogger
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AnalyticsModule::class],
)
object TestAnalyticsModule {

    @Provides
    @Singleton
    fun provideStubAnalyticsLogger(): StubAnalyticsLogger = StubAnalyticsLogger()

    @Provides
    @Singleton
    fun provideAnalyticsLogger(
        stubAnalyticsLogger: StubAnalyticsLogger,
    ): AnalyticsLogger = stubAnalyticsLogger
}
