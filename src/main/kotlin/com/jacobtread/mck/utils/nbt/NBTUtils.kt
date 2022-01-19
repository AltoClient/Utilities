package com.jacobtread.mck.utils.nbt

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.jacobtread.mck.utils.nbt.types.NBTCompound
import com.jacobtread.mck.utils.nbt.types.NBTList
import java.util.*

object NBTUtils {

    fun readGameProfile(compound: NBTCompound): GameProfile? {
        val name = compound.getString("Name")
        val id = compound.getString("Id")
        if (name.isEmpty() && id.isEmpty()) return null
        val uuid = try {
            UUID.fromString(id)
        } catch (e: Throwable) {
            null
        }
        val profile = GameProfile(uuid, name)
        if (compound.hasKey("Properties", 10)) {
            val properties = compound.getCompoundTag("Properties")
            for (s in properties.getKeySet()) {
                val list = properties.getTagList(s, 10)
                for (i in 0 until list.tagCount()) {
                    val tag = list.getCompoundTagAt(i)
                    val value = tag.getString("Value")
                    if (tag.hasKey("Signature", 8)) {
                        val signature = tag.getString("Signature")
                        profile.properties.put(s, Property(s, value, signature))
                    } else {
                        profile.properties.put(s, Property(s, value))
                    }
                }
            }
        }
        return profile
    }

    fun writeGameProfile(compound: NBTCompound, profile: GameProfile): NBTCompound {
        val name = profile.name
        if (!name.isNullOrEmpty()) {
            compound.setString("Name", name)
        }
        val id = profile.id
        if (id != null) {
            compound.setString("Id", id.toString())
        }
        val properties = profile.properties
        if (!properties.isEmpty) {
            val props = NBTCompound()
            for (key in properties.keys()) {
                val list = NBTList()
                for (property in properties.get(key)) {
                    val prop = NBTCompound()
                    prop.setString("Value", property.value)
                    if (property.hasSignature()) {
                        prop.setString("Signature", property.signature)
                    }
                    list.add(prop)
                }
                props.setTag(key, list)
            }
            compound.setTag("Properties", props)
        }
        return compound
    }
}