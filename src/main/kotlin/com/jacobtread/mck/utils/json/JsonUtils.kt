package com.jacobtread.mck.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun JsonElement.expectObject(name: String): JsonObject {
    if (this is JsonObject) return this
    else throw InvalidJsonException(name, "Expected json object type")
}

fun JsonObject.expectString(key: String): String {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) throw InvalidJsonException(key, "Expected primitive string type")
    return element.asString
}

fun JsonObject.expectStringOrDefault(key: String, default: String): String {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) return default
    return element.asString
}

fun JsonObject.expectStringOrNull(key: String): String? {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) return null
    return element.asString
}

fun JsonObject.expectInt(key: String): Int {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) throw InvalidJsonException(key, "Expected primitive string type")
    return element.asInt
}

fun JsonObject.expectIntOrDefault(key: String, default: Int): Int {
    val element = this[key]
    if (element == null || element !is JsonPrimitive) return default
    return element.asInt
}

fun JsonObject.expectFloat(key: String): Float {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) throw InvalidJsonException(key, "Expected primitive string type")
    return element.asFloat
}

fun JsonObject.hasString(key: String): Boolean {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) return false
    return true
}

fun JsonObject.expectArrayOrNull(key: String): JsonArray? {
    val element = this[key] ?: return null
    if (!element.isJsonArray) return null
    return element.asJsonArray
}

fun JsonElement.asString(name: String): String {
    if (this !is JsonPrimitive || !isString) throw InvalidJsonException(name, "Expected $name to be a string")
    return asString
}

fun JsonElement.asFloat(name: String): Float {
    if (this !is JsonPrimitive || !isNumber) throw InvalidJsonException(name, "Expected $name to be a float")
    return asFloat
}

fun JsonObject.expectArray(key: String): JsonArray {
    val element = this[key]
    if (element == null || !element.isJsonArray) throw InvalidJsonException(key, "Expected json array type")
    return element.asJsonArray
}

fun JsonObject.expectObject(key: String): JsonObject {
    val element = this[key]
    if (element == null || !element.isJsonObject) throw InvalidJsonException(key, "Expected json object type")
    return element.asJsonObject
}

fun JsonObject.expectObjectOrNull(key: String): JsonObject? {
    val element = this[key]
    if (element == null || !element.isJsonObject) return null
    return element.asJsonObject
}

fun JsonObject.expectBoolOrDefault(key: String, default: Boolean): Boolean {
    val element = this[key]
    if (element == null || !element.isJsonPrimitive) return default
    element as JsonPrimitive
    if (!element.isBoolean) return default
    return element.asBoolean
}

class InvalidJsonException(val key: String, val reason: String) :
    RuntimeException("Cannot parse JSON key $key: $reason")