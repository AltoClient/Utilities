package com.jacobtread.mck.utils.data

import com.mojang.authlib.GameProfile
import com.mojang.util.UUIDTypeAdapter

data class Session(val username: String, val uuid: String, val token: String, val type: Type) {

    fun getProfile(): GameProfile {
        return try {
            val uuid = UUIDTypeAdapter.fromString(uuid)
            GameProfile(uuid, username)
        } catch (e: IllegalArgumentException) {
            GameProfile(null, username)
        }
    }

    enum class Type {
        LEGACY, MOJANG;

        companion object {
            fun byName(name: String): Type = if (name == "legacy") LEGACY else MOJANG
        }
    }
}