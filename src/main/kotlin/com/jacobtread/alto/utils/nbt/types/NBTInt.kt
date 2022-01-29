package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.INT
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTInt(private var data: Int = 0) : NBTPrimitive() {
    override val id: Byte = INT
    override fun asLong(): Long = data.toLong()
    override fun asInt(): Int = data
    override fun asShort(): Short = (data and 65535).toShort()
    override fun asByte(): Byte = (asInt() and 255).toByte()
    override fun asDouble(): Double = data.toDouble()
    override fun asFloat(): Float = data.toFloat()
    override fun copy(): NBTBase = NBTInt(data)
    override fun toString(): String = "$data"
    override fun hashCode(): Int = super.hashCode() xor data

    override fun write(output: DataOutput) {
       output.writeInt(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(96L)
        data = input.readInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTInt) return false
        if (other.id != id) return false
        return data == other.data
    }
}