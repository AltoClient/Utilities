package com.jacobtread.mck.utils.nbt

import com.jacobtread.mck.utils.nbt.types.NBTCompound
import com.jacobtread.mck.utils.nbt.types.NBTList

inline fun nbtCompoundOf(block: NBTCompound.() -> Unit): NBTCompound {
    val compound = NBTCompound()
    block(compound)
    return compound
}

inline fun nbtListOf(block: NBTList.() -> Unit): NBTList {
    val list = NBTList()
    block(list)
    return list
}
