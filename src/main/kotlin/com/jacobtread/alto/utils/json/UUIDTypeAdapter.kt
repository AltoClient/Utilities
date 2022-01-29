package com.jacobtread.alto.utils.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*

class UUIDTypeAdapter : TypeAdapter<UUID>() {
    companion object {
        /**
         * fromUUID Converts the provided uuid into a string
         * by calling [UUID.toString] and removing any -
         *
         * @param uuid The UUID to convert to a string
         * @return The string representation of the UUID
         */
        fun fromUUID(uuid: UUID): String {
            return uuid.toString().replace("-", "")
        }

        /**
         * fromString Coverts the provided string to a UUID
         * and parses it using [UUID.fromString]. If the provided
         * string is represented without the - char they are automatically
         * added at the respective places
         *
         * Throws [IllegalArgumentException] if the provided [value] is not
         * a UUID
         *
         * @param value The value to convert to UUID
         * @return The resulting UUID
         */
        fun fromString(value: String): UUID {
            return UUID.fromString(
                value.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                    "$1-$2-$3-$4-$5"
                )
            )
        }

        /**
         * fromStringSafe A version of [fromString] that catches any
         * thrown exceptions and returns null instead
         *
         * @see fromString
         * @param value The value to covert to UUID
         * @return The resulting UUID or null on failure
         */
        fun fromStringSafe(value: String): UUID? {
            return try {
                fromString(value)
            } catch (e: Throwable) {
                null
            }
        }
    }

    override fun write(out: JsonWriter, value: UUID) {
        out.value(fromUUID(value))
    }

    override fun read(reader: JsonReader): UUID {
        return fromString(reader.nextString())
    }
}