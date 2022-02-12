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