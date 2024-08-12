package input.comprehensible.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.BuildConfig
import javax.inject.Singleton

/**
 * A provider for feature flags.
 */
interface FeatureFlagProvider {
    val isAiStoriesEnabled: Boolean
}

/**
 * A default implementation of [FeatureFlagProvider].
 */
data class DefaultFeatureFlagProvider(
    override val isAiStoriesEnabled: Boolean = BuildConfig.DEBUG,
) : FeatureFlagProvider

/**
 * A Dagger module for providing feature flags related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object FeatureFlagModule {
    @Provides
    @Singleton
    fun provideFeatureFlagProvider(): FeatureFlagProvider = DefaultFeatureFlagProvider()
}
