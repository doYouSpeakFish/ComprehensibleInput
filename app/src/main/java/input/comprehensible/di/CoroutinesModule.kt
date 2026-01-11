package input.comprehensible.di

import input.comprehensible.util.InjectedSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

object IoDispatcherProvider : InjectedSingleton<CoroutineDispatcher>()
object AppScopeProvider : InjectedSingleton<CoroutineScope>()
