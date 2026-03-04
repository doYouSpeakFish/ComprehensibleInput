package input.comprehensible.util

import com.ktin.InjectedSingleton
import input.comprehensible.BuildConfig

data class FeatureFlags(
    val aiTextAdventuresEnabled: Boolean
) {
    companion object : InjectedSingleton<FeatureFlags>() {
        fun getDefault() = FeatureFlags(
            aiTextAdventuresEnabled = BuildConfig.DEBUG,
        )
    }
}
