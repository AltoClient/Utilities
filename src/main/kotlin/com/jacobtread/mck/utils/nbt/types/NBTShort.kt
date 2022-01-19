package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.nbt.NBTBase
import com.jacobtread.mck.utils.nbt.NBTSizeTracker
import com.jacobtread.mck.utils.nbt.SHORT
import java.io.DataInput
import java.io.DataOutput

class NBTShort(private var data: Short = 0) : NBTPrimitive() {
    override val id: Byte = SHORT
    override fun asLong(): Long = data.toLong()
    override fun asInt(): Int = data.toInt()
    override fun asShort(): Short = data
    override fun asByte(): Byte = (asInt() and 255).toByte()
    override fun asDouble(): Double = data.toDouble()
    override fun asFloat(): Float = data.toFloat()
    override fun copy(): NBTBase = NBTShort(data)
    override fun toString(): String = "${data}s"
    override fun hashCode(): Int = super.hashCode() xor asInt()
    override fun write(output: DataOutput) {
       output.writeShort(data.toInt())
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(80L)
        data = input.readShort()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTShort) return false
        if (other.id != id) return false
        return data == other.data
    }
}