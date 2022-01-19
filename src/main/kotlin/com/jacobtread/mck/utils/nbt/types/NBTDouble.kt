package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.nbt.DOUBLE
import com.jacobtread.mck.utils.nbt.NBTBase
import com.jacobtread.mck.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.floor

class NBTDouble(private var data: Double = 0.0) : NBTPrimitive() {
    override val id: Byte = DOUBLE
    override fun asLong(): Long = data.toLong()
    override fun asInt(): Int = floor(data).toInt()
    override fun asShort(): Short = (asInt() and 65535).toShort()
    override fun asByte(): Byte = (asInt() and 255).toByte()
    override fun asDouble(): Double = data
    override fun asFloat(): Float = data.toFloat()
    override fun copy(): NBTBase = NBTDouble(data)
    override fun toString(): String = "${data}d"
    override fun hashCode(): Int {
        val bits = data.toBits()
        return super.hashCode() xor (bits xor (bits ushr 32)).toInt()
    }

    override fun write(output: DataOutput) {
        output.writeDouble(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(128L)
        data = input.readDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTDouble) return false
        if (other.id != id) return false
        return data == other.data
    }
}