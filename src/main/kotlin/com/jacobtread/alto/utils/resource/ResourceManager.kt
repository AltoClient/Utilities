package com.jacobtread.alto.utils.resource

import com.jacobtread.alto.utils.Identifier
import java.io.IOException

interface ResourceManager {

    val resourceDomains: Set<String>

    @Throws(IOException::class)
    fun getResource(identifier: Identifier): Resource

    @Throws(IOException::class)
    fun getAllResources(identifier: Identifier): List<Resource>

    fun reloadResources(resourcePacks: MutableList<ResourcePack>)

    fun registerReloadListener(listener: ResourceReloadListener)
}