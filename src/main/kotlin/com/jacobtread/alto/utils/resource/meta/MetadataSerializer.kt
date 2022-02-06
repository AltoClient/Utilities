package com.jacobtread.alto.utils.resource.meta

import com.google.gson.JsonObject

interface MetadataSerializer {

    fun <T : MetadataSection?> parseMetadataSection(name: String?, json: JsonObject): T

}