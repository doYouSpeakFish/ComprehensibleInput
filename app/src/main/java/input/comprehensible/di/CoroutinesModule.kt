package input.comprehensible.di

import com.ktin.InjectedSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object CoroutinesModule {
    fun inject() {
        IoDispatcher.inject { Dispatchers.IO }
        AppScope.inject { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    }
}

object IoDispatcher : InjectedSingleton<CoroutineDispatcher>()
object AppScope : InjectedSingleton<CoroutineScope>()
