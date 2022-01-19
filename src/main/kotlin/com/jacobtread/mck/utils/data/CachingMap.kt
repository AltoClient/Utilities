package com.jacobtread.mck.utils.data

class CachingMap<K, V> : HashMap<K, V>() {

    var dirty = false
    var cachedContents = ArrayList<V>()

    override val values: MutableCollection<V>
        get() {
            if (dirty) {
                cachedContents.clear()
                cachedContents.addAll(super.values)
                dirty = false
            }
            return cachedContents
        }

    override fun clear() {
        super.clear()
        dirty = true
        cachedContents.clear()
    }

    override fun put(key: K, value: V): V? {
        dirty = true
        return super.put(key, value)
    }

    override fun remove(key: K): V? {
        val removed = super.remove(key)
        if (removed != null) dirty = true
        return removed
    }

}