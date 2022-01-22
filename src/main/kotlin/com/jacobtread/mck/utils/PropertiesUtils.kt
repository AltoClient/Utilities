package com.jacobtread.mck.utils

import com.jacobtread.mck.logger.Logger
import java.util.*

fun Properties.boolean(key: String, default: Boolean): Boolean {
    val property = getProperty(key)?.lowercase()?.trim() ?: return default
    return when (property) {
        "true", "on" -> true
        "false", "off" -> false
        else -> {
            Logger.get().warn("Invalid value for $key: $property")
            default
        }
    }
}

fun Properties.float(key: String, default: Float): Float {
    val property = getProperty(key)?.lowercase()?.trim() ?: return default
    return try {
        property.toFloat()
    } catch (e: NumberFormatException) {
        Logger.get().warn("Invalid value for $key: $property")
        default
    }
}