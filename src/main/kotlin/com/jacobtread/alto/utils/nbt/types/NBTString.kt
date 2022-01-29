package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import com.jacobtread.alto.utils.nbt.STRING
import java.io.DataInput
import java.io.DataOutput
import java.util.*

class NBTString(private var data: String = "") : NBTBase() {
    override val id: Byte = STRING
    override fun copy(): NBTBase = NBTString(data)
    override fun toString(): String = "\"" + data.replace("\"", "\\\"") + "\""
    override fun asString(): String = data
    override fun hasNoTags(): Boolean = data.isEmpty()
    override fun hashCode(): Int = super.hashCode() xor data.hashCode()
    override fun write(output: DataOutput) {
        output.writeUTF(data)
    }

    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(288L)
        data = input.readUTF()
        sizeTracker.read(16L * data.length)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NBTString) return false
        if (other.id != id) return false
        return Objects.equals(data, other.data)
    }
}