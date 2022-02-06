package com.jacobtread.alto.utils.resource.meta

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.jacobtread.alto.utils.json.expectBoolOrDefault
import com.jacobtread.alto.utils.json.expectObject
import com.jacobtread.alto.utils.json.expectString
import com.jacobtread.alto.utils.resource.data.Language
import java.lang.reflect.Type

class LanguageMetadataSection(val languages: HashSet<Language>) : MetadataSection {

    class Serializer : MetadataSectionSerializer<LanguageMetadataSection> {
        override val sectionName: String get() = "language"

        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LanguageMetadataSection {
            val root = json.asJsonObject
            val languages = HashSet<Language>()
            root.entrySet().forEach { (key, v) ->
                val value = v.expectObject("language")

                val region = value.expectString("region")
                val name = value.expectString("name")

                val isBidirectional = value.expectBoolOrDefault("bidirectional", false)

                if (region.isEmpty()) throw JsonParseException("Invalid language '$key'. Region cannot be empty")
                if (name.isEmpty()) throw JsonParseException("Invalid language '$key'. Name cannot be empty")

                val isDuplicate = !languages.add(Language(key, region, name, isBidirectional))
                if (isDuplicate) throw JsonParseException("Duplicate language '$key' already defined")
            }
            return LanguageMetadataSection(languages)
        }
    }
}