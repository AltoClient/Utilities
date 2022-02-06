package com.jacobtread.alto.utils.resource

import com.jacobtread.alto.utils.Identifier
import com.jacobtread.alto.utils.resource.meta.MetadataSection
import com.jacobtread.alto.utils.resource.meta.MetadataSerializer
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream

interface ResourcePack {

    @Throws(IOException::class)
    fun getInputStream(identifier: Identifier): InputStream

    fun resourceExists(identifier: Identifier): Boolean

    val resourceDomains: Set<String>
    val packName: String

    @Throws(IOException::class)
    fun <T : MetadataSection?> getPackMetadata(serializer: MetadataSerializer, name: String): T

    @Throws(IOException::class)
    fun getPackImage(): BufferedImage?

}