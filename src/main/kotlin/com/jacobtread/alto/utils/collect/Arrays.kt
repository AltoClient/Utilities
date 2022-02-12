package com.jacobtread.alto.utils.collect

object Arrays {

    @JvmStatic
    fun <T> merge(a: Array<T>, b: T): Array<T> {
        return a.plus(b)
    }

    @JvmStatic
    fun <T : Any> appendStart(a: T, b: Array<T>): Array<Any?> {
        val array = arrayOfNulls<Any?>(b.size + 1)
        System.arraycopy(b, 0, array, 1, b.size)
        array[0] = a
        return array
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

inline fun <T> Array<T>.forEachExcluding(value: T, loop: (value: T) -> Unit) {
    for (t in this) {
        if (t != value) {
            loop(t)
        }
    }
}