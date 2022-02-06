package com.jacobtread.alto.utils.resource.data

data class Language(
    val code: String,
    val region: String,
    val name: String,
    val isBidirectional: Boolean,
) : Comparable<Language> {

    override fun compareTo(other: Language): Int = code.compareTo(other.code)
    override fun toString(): String = "$name ($region)"
    override fun hashCode(): Int = code.hashCode()


    override fun equals(other: Any?): Boolean {
        return this === other || (other is Language && code == other.code)
    }
}