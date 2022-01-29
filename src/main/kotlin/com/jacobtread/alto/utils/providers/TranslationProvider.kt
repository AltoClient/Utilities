package com.jacobtread.alto.utils.providers

interface TranslationProvider {

    fun translate(key: String): String

    fun format(key: String, vararg formatArgs: Any?): String

}