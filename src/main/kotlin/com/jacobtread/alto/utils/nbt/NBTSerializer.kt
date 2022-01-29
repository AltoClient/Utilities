package com.jacobtread.alto.utils.nbt

interface NBTSerializer<V> {

    /**
     * Writes the serializable object to the provided
     * NBT compound tag and returns it
     *
     * @return The resulting NBT tag
     */
    fun toNBT(value: V): NBTBase

}