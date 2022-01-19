package com.jacobtread.mck.utils.providers

interface ScoreProvider {

    fun canProvide(): Boolean

    fun provide(name: String, objective: String): String

}