package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.LIST
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTList : NBTBase(), Iterable<NBTBase> {
    override val id: Byte = LIST
    private var list = ArrayList<NBTBase>()
    val isEmpty get() = list.isEmpty()
    val isNotEmpty get() = list.isNotEmpty()
    var tagType: Byte = 0

    override fun write(output: DataOutput) {
        tagType = if (list.isEmpty()) 0
        else list[0].id
        output.writeByte(tagType.toInt())
        output.writeInt(list.size)
        for (nbtBase in list) {
            nbtBase.write(output)
        }
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(296L)
        if (depth > 512) throw RuntimeException("Tried to read NBT tag with depth > 512")
        tagType = input.readByte()
        val size = input.readInt()
        if (tagType == 0.toByte() && size > 0) throw RuntimeException("Missing type on list NBT tag")
        sizeTracker.read(32L * size)
        list = ArrayList(size)
        for (i in 0 until size) {
            val nbt = createByType(tagType)
            nbt.read(input, depth + 1, sizeTracker)
            list.add(nbt)
        }
    }

    override fun copy(): NBTList {
        val tagList = NBTList()
        tagList.tagType = tagType
        for (nbtBase in list) {
            tagList.list.add(nbtBase.copy())
        }
        return tagList
    }

    fun add(nbt: NBTBase) {
        val zb = 0.toByte()
        if (nbt.id != zb) {
            if (tagType == zb) tagType = nbt.id
            else if (tagType != nbt.id) return
            list.add(nbt)
        }
    }

    fun set(index: Int, nbt: NBTBase) {
        val zb = 0.toByte()
        if (nbt.id != zb && index >= 0 && index < list.size) {
            if (tagType == zb) tagType = nbt.id
            else if (tagType != nbt.id) return
            list[index] = nbt
        }
    }

    fun removeTag(index: Int) {
        list.removeAt(index)
    }

    fun removeLast() {
        list.removeLast()
    }

    fun getCompoundTagAt(index: Int): NBTCompound {
        return if (index in 0 until list.size) {
            val tag = list[index]
            if (tag is NBTCompound) tag
            else NBTCompound()
        } else NBTCompound()
    }

    fun getDoubleAt(index: Int): Double {
        return if (index in 0 until list.size) {
            val tag = list[index]
            if (tag is NBTPrimitive) tag.asDouble()
            else 0.0
        } else 0.0
    }

    fun getFloatAt(index: Int): Float {
        return if (index in 0 until list.size) {
            val tag = list[index]
            if (tag is NBTPrimitive) tag.asFloat()
            else 0f
        } else 0f
    }

    fun getStringAt(index: Int): String {
        return if (index in 0 until list.size) {
            val tag = list[index]
            tag.asString()
        } else ""
    }

    fun get(index: Int): NBTBase = if (index in 0 until list.size) list[index] else NBTEnd()
    fun tagCount() = list.size
    override fun toString(): String = "[$list]"
    override fun hasNoTags(): Boolean = list.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (other !is NBTList) return false
        if (other.id != id) return false
        if (other.tagType != tagType) return false
        return other.list == list
    }

    override fun hashCode(): Int = super.hashCode() xor list.hashCode()

    override fun iterator(): Iterator<NBTBase> {
        return object : Iterator<NBTBase> {
            var i = -1
            override fun hasNext() = i < list.size - 1
            override fun next(): NBTBase {
                i++
                return list[i]
            }
        }
    }

    fun <V : NBTBase> iteratorTyped(): Iterator<V> {
        return object : Iterator<V> {
            var i = -1
            override fun hasNext() = i < list.size - 1
            override fun next(): V {
                i++
                @Suppress("UNCHECKED_CAST") return list[i] as V
            }
        }
    }

    fun iteratorString(): Iterator<String> {
        return object : Iterator<String> {
            var i = -1
            override fun hasNext() = i < list.size - 1
            override fun next(): String {
                i++
                return list[i].asString()
            }
        }
    }
}