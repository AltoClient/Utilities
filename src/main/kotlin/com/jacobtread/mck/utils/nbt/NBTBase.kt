package com.jacobtread.mck.utils.nbt

import com.jacobtread.mck.utils.nbt.types.NBTCompound
import com.jacobtread.mck.utils.nbt.types.*
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

const val END: Byte = 0
const val BYTE: Byte = 1
const val SHORT: Byte = 2
const val INT: Byte = 3
const val LONG: Byte = 4
const val FLOAT: Byte = 5
const val DOUBLE: Byte = 6
const val BYTE_ARRAY: Byte = 7
const val STRING: Byte = 8
const val LIST: Byte = 9
const val COMPOUND: Byte = 10
const val INT_ARRAY: Byte = 11
const val PRIMITIVE: Byte = 99

abstract class NBTBase {

    abstract val id: Byte

    @Throws(IOException::class)
    abstract fun write(output: DataOutput)

    @Throws(IOException::class)
    abstract fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker)
    abstract fun copy(): NBTBase

    override fun equals(other: Any?): Boolean {
        if (other !is NBTBase) return false
        return other.id == id
    }

    override fun hashCode(): Int = id.toInt()
    open fun asString(): String = toString()
    open fun hasNoTags(): Boolean = false
    abstract override fun toString(): String

    companion object {
        val TYPES = arrayOf(
            "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE",
            "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"
        )

        fun createByType(id: Byte) = when (id) {
            END -> NBTEnd()
            BYTE -> NBTByte()
            SHORT -> NBTShort()
            INT -> NBTInt()
            LONG -> NBTLong()
            FLOAT -> NBTFloat()
            DOUBLE -> NBTDouble()
            BYTE_ARRAY -> NBTByteArray()
            STRING -> NBTString()
            LIST -> NBTList()
            COMPOUND -> NBTCompound()
            INT_ARRAY -> NBTIntArray()
            else -> throw RuntimeException("Unknown NBT tag type $id")
        }

    }
}