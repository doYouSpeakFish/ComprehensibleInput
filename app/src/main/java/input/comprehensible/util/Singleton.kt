package input.comprehensible.util

abstract class Singleton<T> {
    protected abstract fun create(): T

    operator fun invoke(): T = SingletonStore.getOrPut(key = this, initializer = ::create)
}

abstract class InjectedSingleton<T> : Singleton<T>() {
    private lateinit var initializer: () -> T

    fun inject(initializer: () -> T) {
        this.initializer = initializer
    }

    override fun create() = initializer()
}

object SingletonStore {
    private val singletons = mutableMapOf<Singleton<*>, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrPut(key: Singleton<T>, initializer: () -> T) =
        singletons.getOrPut(key, initializer) as T

    fun clear() {
        singletons.clear()
    }
}
