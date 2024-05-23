package input.comprehensible.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutinesModule::class]
)
class TestCoroutinesModule {
    @IoDispatcher
    @Singleton
    @Provides
    fun providesIoDispatcher(
        testDispatcher: TestDispatcher
    ): CoroutineDispatcher = testDispatcher

    @AppScope
    @Singleton
    @Provides
    fun providesAppScope(
        testDispatcher: TestDispatcher
    ): CoroutineScope = CoroutineScope(testDispatcher)
}
