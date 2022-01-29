package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.LONG
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTLong(private var data: Long = 0) : NBTPrimitive() {
    override val id: Byte = LONG
    override fun asLong(): Long = data
    override fun asInt(): Int = data.toInt()
    override fun asShort(): Short = (data and 65535L).toShort()
    override fun asByte(): Byte = (data and 255L).toByte()
    override fun asDouble(): Double = data.toDouble()
    override fun asFloat(): Float = data.toFloat()
    override fun copy(): NBTBase = NBTLong(data)
    override fun toString(): String = "${data}L"
    override fun hashCode(): Int = super.hashCode() xor (data xor (data ushr 32)).toInt()
    override fun write(output: DataOutput) {
        output.writeLong(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(128L)
        data = input.readLong()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTLong) return false
        if (other.id != id) return false
        return data == other.data
    }
}