package input.comprehensible.di

import input.comprehensible.util.FeatureFlags

object FeatureFlagsModule {
    fun inject() {
        FeatureFlags.inject { FeatureFlags.getDefault() }
    }
}
