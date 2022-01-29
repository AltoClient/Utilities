package com.jacobtread.alto.utils.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class EnumTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (!rawType.isEnum) return null
        val constants = HashMap<String, T>()
        for (constant in rawType.enumConstants) {
            constant ?: continue
            // IDE stupid this is literally T[]
            @Suppress("UNCHECKED_CAST")
            constants[getConstantName(constant)] = constant as T
        }
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T?) {
                if (value == null) out.nullValue()
                else out.value(getConstantName(value))
            }

            override fun read(input: JsonReader): T? {
                return if (input.peek() == JsonToken.NULL) {
                    input.nextNull()
                    null
                } else {
                    constants[input.nextString()]
                }
            }
        }
    }

    fun getConstantName(constant: Any): String {
        return if (constant is Enum<*>) {
            constant.name
        } else {
            constant.toString()
        }.lowercase()
    }
}