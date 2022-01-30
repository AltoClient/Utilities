package com.jacobtread.alto.utils.collect

import com.jacobtread.alto.utils.Identifier

/**
 * IdentityRegistryWithDefault An implementation of [IdentityRegistry] with a
 * default identifier and value that are used if the lookup inside [get] fails. If
 * the [get] function fails to find an entry the default value at [defaultValue]
 * will be returned instead.
 *
 * [defaultValue] is initialized when [put] is called with the [defaultIdentifier] as
 * the identifier the [defaultValue] will be then assigned to the provided value.
 * Because of this its recommended that you call [ensureDefaultNonNull] before calling
 * any [get] functions if you expect [defaultValue] to not be null
 *
 * @param V The type of value that will be stored in this registry
 * @property defaultIdentifier The default identifier to use if all else fails
 * @constructor Create empty IdentityRegistryWithDefault
 */
open class IdentityRegistryWithDefault<V>(private val defaultIdentifier: Identifier) : IdentityRegistry<V>() {

    private var defaultValue: V? = null

    fun ensureDefaultNonNull() {
        if (defaultValue == null) throw NullPointerException("Default value of identity registry cannot be null")
    }

    override fun put(id: Int, key: Identifier, value: V) {
        if (key == defaultIdentifier) {
            defaultValue = value
        }
        super.put(id, key, value)
    }

    override fun get(id: Int): V? {
        val value = super.get(id)
        return value ?: defaultValue
    }

    override fun get(key: Identifier): V? {
        val value = super.get(key)
        return value ?: defaultValue
    }

}