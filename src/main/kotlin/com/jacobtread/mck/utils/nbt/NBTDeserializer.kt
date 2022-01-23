package com.jacobtread.mck.utils.nbt

interface NBTDeserializer<V> {

    /**
     * Reads the serialized object from
     * the provided NBT compound tag
     *
     * @param nbt The nbt tag to read from
     */
    fun fromNBT(nbt: NBTBase): V

}