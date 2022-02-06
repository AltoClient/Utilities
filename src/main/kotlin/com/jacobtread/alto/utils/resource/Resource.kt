package com.jacobtread.alto.utils.resource

import com.jacobtread.alto.utils.Identifier
import com.jacobtread.alto.utils.resource.meta.MetadataSection
import java.io.InputStream

interface Resource {

    val resourceLocation: Identifier
    val inputStream: InputStream
    val hasMetadata: Boolean

    fun <T : MetadataSection> getMetadata(name: String): T

    val resourcePackName: String
}