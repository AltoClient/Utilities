package com.jacobtread.mck.utils.nbt

import com.jacobtread.mck.utils.nbt.types.NBTCompound

interface NBTDeserializable<V> {

    /**
     * Reads the serialized object from
     * the provided NBT compound tag
     *
     * @param nbt The nbt tag to read from
     */
    fun readFromNBT(nbt: NBTCompound): V

}