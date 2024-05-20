package input.comprehensible.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Module that provides coroutines dependencies.
 */
@InstallIn(SingletonComponent::class)
@Module
class CoroutinesModule {
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

/**
 * Annotation for specifying that the IO dispatcher should be injected for a [CoroutineDispatcher]
 * provided using hilt.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher
