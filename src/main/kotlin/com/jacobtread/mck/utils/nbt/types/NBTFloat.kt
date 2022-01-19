package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.nbt.FLOAT
import com.jacobtread.mck.utils.nbt.NBTBase
import com.jacobtread.mck.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.floor

class NBTFloat(private var data: Float = 0f) : NBTPrimitive() {
    override val id: Byte = FLOAT
    override fun asLong(): Long = data.toLong()
    override fun asInt(): Int = floor(data).toInt()
    override fun asShort(): Short = (asInt() and 65535).toShort()
    override fun asByte(): Byte = (asInt() and 255).toByte()
    override fun asDouble(): Double = data.toDouble()
    override fun asFloat(): Float = data
    override fun copy(): NBTBase = NBTFloat(data)
    override fun toString(): String = "${data}f"
    override fun hashCode(): Int = super.hashCode() xor data.toBits()
    override fun write(output: DataOutput) {
        output.writeFloat(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(96L)
        data = input.readFloat()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTFloat) return false
        if (other.id != id) return false
        return data == other.data
    }
}