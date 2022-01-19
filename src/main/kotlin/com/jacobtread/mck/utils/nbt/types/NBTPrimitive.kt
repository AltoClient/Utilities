package com.jacobtread.mck.utils.nbt.types

import com.jacobtread.mck.utils.nbt.NBTBase

abstract class NBTPrimitive : NBTBase() {

    abstract fun asLong(): Long
    abstract fun asInt(): Int
    abstract fun asShort(): Short
    abstract fun asByte(): Byte
    abstract fun asDouble(): Double
    abstract fun asFloat(): Float

}