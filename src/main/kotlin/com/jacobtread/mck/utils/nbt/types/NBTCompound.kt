package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.crash.Report
import com.jacobtread.mck.utils.crash.ReportedException
import com.jacobtread.mck.utils.nbt.*
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

class NBTCompound() : NBTBase() {
    override val id: Byte = COMPOUND

    constructor(init: NBTCompound.() -> Unit) : this() {
        init()
    }

    val tags = HashMap<String, NBTBase>()

    override fun write(output: DataOutput) {
        for ((key, tag) in tags) {
            writeTag(key, tag, output)
        }
        output.writeByte(END.toInt())
    }

    @Throws(IOException::class)
    fun writeTag(name: String, tag: NBTBase, output: DataOutput) {
        val id = tag.id.toInt()
        output.writeByte(id)
        if (id == 0) return
        output.writeUTF(name)
        tag.write(output)
    }

    fun setTag(key: String, value: NBTBase) = tags.put(key, value)
    fun setByte(key: String, value: Byte) = tags.put(key, NBTByte(value))
    fun setShort(key: String, value: Short) = tags.put(key, NBTShort(value))
    fun setInt(key: String, value: Int) = tags.put(key, NBTInt(value))
    fun setLong(key: String, value: Long) = tags.put(key, NBTLong(value))
    fun setFloat(key: String, value: Float) = tags.put(key, NBTFloat(value))
    fun setDouble(key: String, value: Double) = tags.put(key, NBTDouble(value))
    fun setString(key: String, value: String) = tags.put(key, NBTString(value))
    fun setByteArray(key: String, value: ByteArray) = tags.put(key, NBTByteArray(value))
    fun setIntArray(key: String, value: IntArray) = tags.put(key, NBTIntArray(value))
    fun setBoolean(key: String, value: Boolean) = tags.put(key, NBTByte(if (value) 1 else 0))
    fun getTagType(key: String) = tags[key]?.id ?: 0
    fun hasPrimitive(key: String) = hasKey(key, PRIMITIVE)
    fun hasCompound(key: String) = hasKey(key, COMPOUND)
    fun hasList(key: String) = hasKey(key, LIST)
    fun hasString(key: String) = hasKey(key, STRING)
    fun hasKey(key: String) = tags.containsKey(key)
    fun hasKey(key: String, type: Int): Boolean = hasKey(key, type.toByte())
    fun hasKey(key: String, type: Byte): Boolean {
        val id = getTagType(key)
        return if (id == type) true
        else type == PRIMITIVE && id in 1..6
    }

    fun getByte(key: String): Byte {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asByte() else 0
    }

    fun getShort(key: String): Short {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asShort() else 0
    }

    fun getInt(key: String): Int {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asInt() else 0
    }

    fun getLong(key: String): Long {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asLong() else 0L
    }

    fun getFloat(key: String): Float {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asFloat() else 0f
    }

    fun getDouble(key: String): Double {
        return if (hasKey(key, PRIMITIVE)) (tags[key] as NBTPrimitive).asDouble() else 0.0
    }

    fun getString(key: String): String = tags[key]?.asString() ?: ""

    fun getIntArray(key: String): IntArray {
        return if (hasKey(key, INT_ARRAY)) (tags[key] as NBTIntArray).asIntArray() else IntArray(0)
    }

    fun getByteArray(key: String): ByteArray {
        return if (hasKey(key, BYTE_ARRAY)) (tags[key] as NBTByteArray).asByteArray() else ByteArray(0)
    }

    fun getCompoundTag(key: String): NBTCompound {
        return if (hasKey(key, COMPOUND)) (tags[key] as NBTCompound) else NBTCompound()
    }

    fun getCompoundSafe(key: String): NBTCompound {
        if (!hasKey(key, COMPOUND)) tags[key] = NBTCompound()
        return tags[key] as NBTCompound
    }

    fun getTagList(key: String): NBTList {
        return if (hasKey(key, LIST)) {
            (tags[key] as NBTList)
        } else NBTList()
    }

    fun getTagListSafe(key: String): NBTList {
        if (!hasKey(key, LIST)) tags[key] = NBTList()
        return tags[key] as NBTList
    }

    fun getTagList(key: String, type: Byte): NBTList {
        return if (hasKey(key, LIST)) {
            val list = tags[key] as NBTList
            if (list.tagCount() > 0 && list.tagType != type) NBTList()
            else list
        } else NBTList()
    }

    fun getTag(key: String): NBTBase? = tags[key]

    fun getBoolean(key: String): Boolean = getByte(key) != 0.toByte()
    fun removeTag(key: String) = tags.remove(key)

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(384L)
        if (depth > 512) throw RuntimeException("Tried to read NBT tag with depth > 512")
        tags.clear()
        while (true) {
            val type = input.readByte()
            if (type == 0.toByte()) break
            val name = input.readUTF()
            sizeTracker.read(244L + (16 * name.length))
            try {
                val nbt = createByType(type)
                nbt.read(input, depth, sizeTracker)
                if (tags.put(name, nbt) != null) {
                    sizeTracker.read(288L)
                }
            } catch (e: IOException) {
                val report = Report.getOrMake("Loading NBT data", e)
                val category = report.section("NBT Tag")
                category.add("Tag name", name)
                category.add("Tag type", java.lang.Byte.valueOf(id))
                throw ReportedException(report)
            }
        }
    }

    override fun copy(): NBTCompound {
        val compound = NBTCompound()
        for ((key, tag) in tags) {
            compound.setTag(key, tag.copy())
        }
        return compound
    }

    fun merge(tag: NBTCompound) {
        for ((key, value) in tag.tags) {
            if (value is NBTCompound) {
                if (hasKey(key, COMPOUND)) {
                    val existing = getCompoundTag(key)
                    existing.merge(value)
                    continue
                }
            }
            setTag(key, value.copy())
        }
    }

    fun getKeySet() = tags.keys
    val isEmpty get() = tags.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (other !is NBTCompound) return false
        if (other.id != id) return false
        return tags.entries == other.tags.entries
    }

    override fun toString(): String = "{$tags}"


    override fun hasNoTags(): Boolean = tags.isEmpty()
    override fun hashCode(): Int = super.hashCode() xor tags.hashCode()

}