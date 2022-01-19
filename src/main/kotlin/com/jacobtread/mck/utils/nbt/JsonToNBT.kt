package com.jacobtread.mck.utils.nbt

import com.google.gson.*
import com.jacobtread.mck.utils.nbt.types.NBTCompound
import com.jacobtread.mck.utils.nbt.types.*
import java.lang.reflect.Type

class NBTParseException(message: String) : RuntimeException(message)

object JsonToNBT {
    private val DOUBLE = Regex("[-+]?[0-9]*\\.?[0-9]+[d|D]")
    private val FLOAT = Regex("[-+]?[0-9]*\\.?[0-9]+[f|F]")
    private val BYTE = Regex("[-+]?[0-9]+[b|B]")
    private val LONG = Regex("[-+]?[0-9]+[l|L]")
    private val SHORT = Regex("[-+]?[0-9]+[s|S]")
    private val INTEGER = Regex("[-+]?[0-9]+")

    private val GSON = GsonBuilder()
        .registerTypeAdapter(NBTCompound::class.java, Deserializer())
        .create()

    fun getTagFromJson(data: String): NBTCompound {
        return try {
            GSON.fromJson(data, NBTCompound::class.java)
        } catch (e: Throwable) {
            throw NBTParseException(e.message ?: "Unknown reason")
        }
    }

    class Deserializer : JsonDeserializer<NBTCompound> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): NBTCompound {
            if (json !is JsonObject) throw NBTParseException("Expected JsonObject got ${json.javaClass.name} instead")
            return getNBTTag(json) as NBTCompound
        }

        fun dropSuffix(value: String, suffix: Char): String {
            return if (value.endsWith(suffix, ignoreCase = true)) {
                value.dropLast(1)
            } else {
                value
            }
        }

        fun getNBTTag(value: JsonElement): NBTBase {
            return when (value) {
                is JsonPrimitive -> {
                    when {
                        value.isBoolean -> NBTByte(if (value.asBoolean) 1 else 0)
                        value.isString -> {
                            val raw = value.asString
                            when {
                                DOUBLE.matches(raw) -> NBTDouble(dropSuffix(raw, 'd').toDouble())
                                FLOAT.matches(raw) -> NBTFloat(dropSuffix(raw, 'f').toFloat())
                                BYTE.matches(raw) -> NBTByte(dropSuffix(raw, 'b').toByte())
                                LONG.matches(raw) -> NBTLong(dropSuffix(raw, 'l').toLong())
                                SHORT.matches(raw) -> NBTShort(dropSuffix(raw, 's').toShort())
                                INTEGER.matches(raw) -> NBTInt(raw.toInt())
                                else -> NBTString(value.asString)
                            }
                        }
                        value.isJsonObject || value.isJsonArray -> getNBTTag(value)
                        value.isJsonArray -> {
                            val array = value.asJsonArray
                            val list = NBTList()
                            for (jsonElement in array) {
                                val tag = getNBTTag(jsonElement)
                                list.add(tag)
                            }
                            list
                        }
                        value.isNumber -> {
                            when (val num = value.asNumber) {
                                is Double -> NBTDouble(num)
                                is Float -> NBTFloat(num)
                                is Short -> NBTShort(num)
                                is Long -> NBTLong(num)
                                else -> NBTInt(num.toInt())
                            }
                        }
                        else -> throw NBTParseException("Unknown NBT tag type $value")
                    }
                }
                is JsonObject -> {
                    val compound = NBTCompound()
                    for ((key, v) in value.entrySet()) {
                        val tag = getNBTTag(v)
                        compound.setTag(key, tag)
                    }
                    compound
                }
                is JsonArray -> {
                    val list = NBTList()
                    for (jsonElement in value) {
                        val tag = getNBTTag(jsonElement)
                        list.add(tag)
                    }
                    list
                }
                else -> throw NBTParseException("Unknown NBT tag type $value")
            }
        }

    }

}