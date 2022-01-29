package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.BYTE
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTByte(private var data: Byte = 0) : NBTPrimitive() {

    override val id: Byte = BYTE
    override fun asLong(): Long = data.toLong()
    override fun asInt(): Int = data.toInt()
    override fun asShort(): Short = data.toShort()
    override fun asByte(): Byte = data
    override fun asDouble(): Double = data.toDouble()
    override fun asFloat(): Float = data.toFloat()
    override fun copy(): NBTBase = NBTByte(data)
    override fun toString(): String = "${data}b"
    override fun hashCode(): Int = super.hashCode() xor asInt()

    override fun write(output: DataOutput) {
       output.writeByte(data.toInt())
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(72L)
        data = input.readByte()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTByte) return false
        if (other.id != id) return false
        return data == other.data
    }

}