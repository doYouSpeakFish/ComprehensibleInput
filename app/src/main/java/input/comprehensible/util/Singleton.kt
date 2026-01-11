package input.comprehensible.util

import java.util.concurrent.ConcurrentHashMap

/**
 * An abstract class that can be extended by the companion object of a class to turn it into
 * a singleton that can be retrieved using a normal no argument constructor.
 *
 * Example:
 * ```
 * class MySingleton(
 *     aDependency: Dependency,
 *     anotherDependency: AnotherDependency,
 * ) {
 *
 *     companion object : Singleton<MySingleton>() {
 *         override fun create() = MySingleton(
 *             aDependency = Dependency(),
 *             anotherDependency = AnotherDependency(),
 *         )
 *     }
 * }
 *
 * class DependsOnMySingleton(
 *     private val mySingleton: MySingleton = MySingleton() // This is a singleton instance
 * ) {
 *   ...
 * }
 * ```
 */
abstract class Singleton<T> {
    /**
     * Creates a new instance of the singleton.
     */
    protected abstract fun create(): T

    /**
     * Returns the singleton instance.
     */
    operator fun invoke(): T = SingletonStore.getOrPut(key = this, initializer = ::create)
}

/**
 * An abstract class that can be extended by the companion object of a class to turn it into
 * a singleton that can be retrieved using a normal no argument constructor, and that can be
 * injected by calling the [inject] method of the companion object, allowing fakes to be injected
 * for tests.
 *
 * Note: When used in tests, the [SingletonStore.clear] method must be called at the end of the
 * test to clear the singleton store ready for the next test to run with a fresh instance of the
 * singleton.
 *
 * Example:
 * ```
 * class MySingleton(
 *     aDependency: Dependency,
 *     anotherDependency: AnotherDependency,
 * ) {
 *
 *     companion object : InjectedSingleton<MySingleton>()
 * }
 *
 * class Main {
 *     fun main() {
 *         MySingleton.inject {
 *             MySingleton(
 *                 aDependency = Dependency(),
 *                 anotherDependency = AnotherDependency(),
 *             )
 *         }
 *     }
 * }
 *
 * class DependsOnMySingleton(
 *     private val mySingleton: MySingleton = MySingleton() // This is a singleton instance
 * ) {
 *   ...
 * }
 * ```
 */
abstract class InjectedSingleton<T> : Singleton<T>() {
    private lateinit var initializer: () -> T

    /**
     * Lazily injects a new instance of the singleton.
     */
    fun inject(initializer: () -> T) {
        this.initializer = initializer
    }

    override fun create() = initializer()
}

/**
 * Retains a reference to singletons created by extending the [Singleton] class or the
 * [InjectedSingleton] class. When used in tests, the [SingletonStore.clear] method must be called
 * at the end of the test to clear the singleton store ready for the next test to run with a fresh
 * instance of the singleton.
 */
object SingletonStore {
    private val singletons = ConcurrentHashMap<Singleton<*>, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrPut(key: Singleton<T>, initializer: () -> T): T {
        if (singletons.containsKey(key)) return singletons[key] as T
        synchronized(key) {
            return singletons.getOrPut(key, initializer) as T
        }
    }

    fun clear() = singletons.clear()
}
