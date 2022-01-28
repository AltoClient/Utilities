package com.jacobtread.mck.utils.collect

import java.util.*

/**
 * Object2IntIdentityMap A simple map which maps [V] values to
 * integer values this map is reversible and the int value can
 * be obtained using the provided [V].
 *
 * Keys are stored in an [IdentityHashMap] and mapped to the provided
 * value. This means that the keys are compared using their identity
 * rather than .equals
 *
 * @param V The value type
 * @constructor Create empty Object2IntIdentityMap
 */
class Object2IntIdentityMap<V> : MutableMap<V, Int>, Iterable<V> {

    private val identityMap = IdentityHashMap<V, Int>(512)
    private val intValues = ArrayList<V?>()

    /**
     * put Adds a key value mapping to the identity map
     * and adds values to fill in the spaces in [intValues]
     * and sets the [value] in values to be [key] to allow
     * for reversed lookups
     *
     * @param key
     * @param value
     */
    override fun put(key: V, value: Int): Int? {
        identityMap[key] = value
        while (intValues.size < value) intValues.add(null)
        intValues[value] = key
        return null
    }


    /**
     * get Return a value from a key [V] or -1
     * if it is not present in the identity map
     *
     * @param key The key to search for
     * @return The value present or null
     */
    override fun get(key: V): Int {
        return identityMap[key] ?: -1
    }

    /**
     * getByValue Returns the key that is mapped to the provided
     * integer [value] or null if it was not present in the values
     * list
     *
     * @param value The value to find the key for
     * @returnThe mapped key or null if not found
     */
    fun getByValue(value: Int): V? {
        return if (value in intValues.indices) intValues[value] else null
    }

    override fun iterator(): Iterator<V> {
        return ObjectIterator()
    }

    override val values: MutableCollection<Int> get() = identityMap.values
    override val entries: MutableSet<MutableMap.MutableEntry<V, Int>> get() = identityMap.entries
    override val keys: MutableSet<V> get() = identityMap.keys
    override val size: Int get() = identityMap.size

    override fun containsKey(key: V): Boolean = identityMap.containsKey(key)
    override fun containsValue(value: Int): Boolean = identityMap.containsValue(value)
    override fun isEmpty(): Boolean = identityMap.isEmpty()


    /**
     * ObjectIterator A Simple iterator that iterates over the
     * [intValues] skipping any values that are null
     *
     * @constructor Create empty ObjectIterator
     */
    inner class ObjectIterator : AbstractIterator<V>() {
        private var i = 0
        override fun computeNext() {
            while (i < intValues.size) {
                val value = intValues[i]
                if (value != null) {
                    setNext(value)
                    i++
                    return
                }
                i++
            }
            done()
        }
    }

    override fun clear() {
        intValues.clear()
        identityMap.clear()
    }

    override fun putAll(from: Map<out V, Int>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun remove(key: V): Int? {
        val value = identityMap.remove(key)
        if (value != null) {
            intValues[value] = null
        }
        return value
    }
}