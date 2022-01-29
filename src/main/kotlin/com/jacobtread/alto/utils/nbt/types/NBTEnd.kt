package com.jacobtread.alto.utils.nbt.types

import com.jacobtread.alto.utils.nbt.END
import com.jacobtread.alto.utils.nbt.NBTBase
import com.jacobtread.alto.utils.nbt.NBTSizeTracker
import java.io.DataInput
import java.io.DataOutput

class NBTEnd : NBTBase() {
    override val id: Byte = END
    override fun write(output: DataOutput) {}
    override fun read(input: DataInput, depth: Int, sizeTracker: NBTSizeTracker) {
        sizeTracker.read(64L)
    }
    override fun copy(): NBTBase = NBTEnd()
    override fun toString(): String = "END"
}