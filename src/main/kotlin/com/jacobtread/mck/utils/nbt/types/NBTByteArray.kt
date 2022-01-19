package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.nbt.BYTE_ARRAY
import com.jacobtread.mck.utils.nbt.NBTBase
import com.jacobtread.mck.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTByteArray(private var data: ByteArray = ByteArray(0)) : NBTBase() {
    override val id: Byte = BYTE_ARRAY
    override fun copy(): NBTBase = NBTByteArray(data.copyOf())
    override fun toString(): String = "[${data.joinToString { "," }}]"
    override fun hashCode(): Int = super.hashCode() xor data.contentHashCode()

    override fun write(output: DataOutput) {
        output.writeInt(data.size)
        output.write(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(192L)
        val size = input.readInt()
        sizeTracker.read(8L * size)
        data = ByteArray(size)
        input.readFully(data)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTByteArray) return false
        if (other.id != id) return false
        return data.contentEquals(other.data)
    }

    fun asByteArray(): ByteArray = data
}