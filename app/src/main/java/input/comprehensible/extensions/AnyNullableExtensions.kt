package input.comprehensible.extensions

/**
 * Throws an [IllegalArgumentException] if the value is null. If [lazyMessage] is set, it will be
 * used as the message of the exception.
 */
fun <T : Any?> T?.orError(lazyMessage: (() -> String)): T =
    requireNotNull(value = this, lazyMessage = lazyMessage)

/**
 * Throws an [IllegalArgumentException] if the value is null.
 */
fun <T : Any?> T?.orError(): T = requireNotNull(value = this)
