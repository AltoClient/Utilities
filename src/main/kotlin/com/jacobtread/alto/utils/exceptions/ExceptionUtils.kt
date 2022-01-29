package com.jacobtread.alto.utils.exceptions

inline fun ignoreThrowable(action: () -> Unit) {
    try {
        action()
    } catch (_: Throwable) {
    }
}