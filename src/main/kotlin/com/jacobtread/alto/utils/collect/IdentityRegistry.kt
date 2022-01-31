package com.jacobtread.alto.utils.collect

import com.google.common.collect.HashBiMap
import com.jacobtread.alto.logger.Logger
import com.jacobtread.alto.utils.Identifier
import java.util.*

/**
 * IdentityRegistry A Registry where all values have an associated [Identifier]
 * but also have an integer "id" which represents them this is to allow support
 * for the old Minecraft item/block "id" systems and keep the backward compatible
 * support for those
 *
 * @param V The type of value that will be stored in this registry
 * @constructor Create empty NamespacedRegistry
 */
open class IdentityRegistry<V> : Iterable<V> {

    companion object {
        val LOGGER: Logger = Logger.get()
    }

    private val objectIdMap = Object2IntIdentityMap<V>()
    private val underlyingMap = HashBiMap.create<Identifier, V>()
    private val registryInverse = underlyingMap.inverse()
    val keys: Set<Identifier> = Collections.unmodifiableSet(underlyingMap.keys)

    /**
     * put Creates a mapping for the provided [id] and [key] to
     * the provided [value]
     *
     * @param id The integer id representation
     * @param key The identifier / domain specific identifier key
     * @param value The value to map to
     * @return The value that was inserted
     */
    open fun put(id: Int, key: Identifier, value: V): V {
        objectIdMap[value] = id
        if (underlyingMap.containsKey(key)) {
            LOGGER.debug("Adding duplicate key '$key' to registry")
        }
        underlyingMap[key] = value
        return value
    }

    /**
     * containsKey Checks if this registry contains the
     * provided [Identifier]
     *
     * @param key The identifier to check for
     * @return Whether the identifier has been mapped to anything
     */
    fun containsKey(key: Identifier): Boolean {
        return underlyingMap.containsKey(key)
    }

    /**
     * get Returns the value mapped to the provided
     * [Identifier] [key] or null if there is no
     * mapping present
     *
     * @param key The identifier to search for
     * @return The mapped value or null if none are present
     */
    open operator fun get(key: Identifier): V? {
        return underlyingMap[key]
    }

    /**
     * getIdentifier Gets the mapped identifier for the
     * provided value if its present in the [registryInverse]
     * otherwise null is returned
     *
     * @param value The value to find the [Identifier] for
     * @return The mapped identifier or null if not present
     */
    fun getIdentifier(value: V?): Identifier? {
        return if (value == null) null else registryInverse[value]
    }

    /**
     * get Returns the value mapped to the provided
     * id [id] or null if there is no mapping present
     *
     * @param id The id to search for
     * @return The mapped value or null if none are present
     */
    open fun get(id: Int): V? {
        return objectIdMap.getByValue(id)
    }

    /**
     * getId Finds the id of the provided value or -1 if
     * there is no mapping for the provided value
     *
     * @param value
     * @return
     */
    fun getId(value: V): Int {
        return objectIdMap[value]
    }

    /**
     * iterator Returns an iterator for iterating
     * over the contents of this registry
     *
     * @return The registry iterator
     */
    override fun iterator(): Iterator<V> {
        return objectIdMap.iterator()
    }
}
