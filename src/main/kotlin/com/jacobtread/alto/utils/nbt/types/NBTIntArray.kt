package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.INT_ARRAY
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTIntArray(private var data: IntArray = IntArray(0)) : NBTBase() {
    override val id: Byte = INT_ARRAY
    override fun copy(): NBTBase = NBTIntArray(data.copyOf())
    override fun toString(): String = "[${data.joinToString { "," }}]"
    override fun hashCode(): Int = super.hashCode() xor data.contentHashCode()
    fun asIntArray(): IntArray = data

    override fun write(output: DataOutput) {
        output.writeInt(data.size)
        for (datum in data) {
            output.writeInt(datum)
        }
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(192L)
        val size = input.readInt()
        data = IntArray(size)
        sizeTracker.read(32L * size)
        for (i in 0 until size) {
            data[i] = input.readInt()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTIntArray) return false
        if (other.id != id) return false
        return data.contentEquals(other.data)
    }
}