package com.jacobtread.mck.utils.nbt

open class NBTSizeTracker(val max: Long) {

    var readCount = 0L

    open fun read(bits: Long) {
        this.readCount += bits / 8L
        if (readCount > max) throw RuntimeException("Tried to read too much from NBT tag tried to read $readCount when only allowed $max")
    }

    companion object {
        val INFINITE = object : NBTSizeTracker(0L) {
            override fun read(bits: Long) {
            }
        }
    }

}