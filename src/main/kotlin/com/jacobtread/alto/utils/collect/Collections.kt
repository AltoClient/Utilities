package com.jacobtread.alto.utils.collect

/**
 * addIf Conditionally adds an item to a collection. If the [test] is passed
 * for the item provided
 *
 * @param T The type of the collection items
 * @param value The value to add
 * @param test The condition that must pass
 * @receiver
 */
inline fun <T> MutableCollection<T>.addIf(value: T, test: (value: T) -> Boolean) {
    if (test(value)) this.add(value)
}

inline fun <T> Collection<T>.forEachExcluding(value: T, loop: (value: T) -> Unit) {
    for (t in this) {
        if (t != value) {
            loop(t)
        }
    }
}

/**
 * any Iterates over an iterator until it finds a value that matches
 * the provided [predicate]
 *
 * @param T The type of the iterator contents
 * @param predicate The condition to test for
 * @receiver
 * @return
 */
inline fun <T> Iterator<T>.any(predicate: (T) -> Boolean): Boolean {
    while (hasNext()) {
        val next = next()
        if (predicate(next)) return true
    }
    return false
}