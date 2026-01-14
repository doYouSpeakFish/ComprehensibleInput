package input.comprehensible.di

import com.di.singleton.InjectedSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object CoroutinesModule {
    fun inject() {
        IoDispatcherProvider.inject { Dispatchers.IO }
        AppScopeProvider.inject { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    }
}

object IoDispatcherProvider : InjectedSingleton<CoroutineDispatcher>()
object AppScopeProvider : InjectedSingleton<CoroutineScope>()
