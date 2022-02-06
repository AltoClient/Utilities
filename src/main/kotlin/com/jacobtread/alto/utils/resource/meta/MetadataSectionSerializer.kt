package com.jacobtread.alto.utils.resource.meta

import com.google.gson.JsonDeserializer

interface MetadataSectionSerializer<T : MetadataSection> : JsonDeserializer<T> {

    val sectionName: String

}