package com.jacobtread.mck.utils.exceptions

inline fun ignoreThrowable(action: () -> Unit) {
    try {
        action()
    } catch (_: Throwable) {
    }
}