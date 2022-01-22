package com.jacobtread.mck.utils

open class Identifier {

    val resourceDomain: String
    val resourcePath: String

    constructor(domain: String, path: String) {
        this.resourceDomain = domain
        this.resourcePath = path
    }

    constructor(identifier: String) {
        val parts = splitDomainString(identifier)
        this.resourceDomain = parts[0]
        this.resourcePath = parts[1]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Identifier) return false
        return resourceDomain == other.resourceDomain && resourcePath == other.resourcePath
    }

    override fun hashCode(): Int {
        var result = resourceDomain.hashCode()
        result = 31 * result + resourcePath.hashCode()
        return result
    }

    override fun toString(): String {
        return "${resourceDomain}:${resourcePath}"
    }

    companion object {
        @JvmStatic
        fun splitDomainString(value: String): Array<String> {
            val array = arrayOf("minecraft", value)
            val splitIndex = value.indexOf(':')
            if (splitIndex >= 0) {
                array[1] = value.substring(splitIndex + 1, value.length)
                if (splitIndex >= 1) {
                    array[0] = value.substring(0, splitIndex)
                }
            }
            return array
        }
    }

}