package input.comprehensible.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.util.FeatureFlagModule
import input.comprehensible.util.FeatureFlagProvider
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FeatureFlagModule::class]
)
object TestFeatureFlagModule {
    @Provides
    @Singleton
    fun provideFeatureFlagProvider(): FeatureFlagProvider =
        object : FeatureFlagProvider {
            override val isAiStoriesEnabled: Boolean = true
        }
}
