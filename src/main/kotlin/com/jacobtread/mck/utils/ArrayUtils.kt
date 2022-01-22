package com.jacobtread.mck.utils

object ArrayUtils {

    @JvmStatic
    fun <T> merge(a: Array<T>, b: T): Array<T> {
        return a.plus(b)
    }

    @JvmStatic
    fun mergeAt(a: Array<Any>, b: Any, i: Int): Array<Any> {
        val list = a.toMutableList()
        list.add(i, b)
        return list.toTypedArray()
    }

    @JvmStatic
    fun mergeInt(a: IntArray, b: Int): IntArray {
        return a.plus(b)
    }

}