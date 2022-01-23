package com.jacobtread.mck.utils.nbt

import com.jacobtread.mck.utils.nbt.types.NBTCompound

interface NBTMutableSerializable {

    /**
     * Reads the serialized object from
     * the provided NBT compound tag
     *
     * @param nbt The nbt tag to read from
     */
    fun readFromNBT(nbt: NBTCompound)

    /**
     * Writes the serializable object to the provided
     * NBT compound tag and returns it
     *
     * @param nbt The nbt tag to write to
     * @return The provided nbt tag
     */
    fun writeToNBT(nbt: NBTCompound)

}