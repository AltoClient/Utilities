package me.jacobtread.mck.utils.providers

interface TranslationProvider {

    fun translate(key: String): String

    fun format(key: String, vararg formatArgs: Any?): String

}