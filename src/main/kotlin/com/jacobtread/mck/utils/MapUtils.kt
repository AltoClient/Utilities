package com.jacobtread.mck.utils

import com.google.common.collect.HashBiMap

/**
 * biMap Converts the provided Map into a [HashBiMap]
 * for use like mapOf("something" to "something").biMap() for
 * a quick and easy reversible map
 *
 * @param K
 * @param V
 * @return
 */
fun <K, V> Map<K, V>.biMap(): HashBiMap<K, V> = HashBiMap.create(this)