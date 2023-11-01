package no.telenor.kt.env.parsers

import no.telenor.kt.env.MapEnv
import no.telenor.kt.env.Parser
import no.telenor.kt.env.parseValue
import kotlin.reflect.KType

class MapParser : Parser {
	fun parseMap(type: KType, name: String, value: String): Map<Any?, Any?> {
		val mapEnvAnnot: MapEnv? = type.annotations.find { it is MapEnv } as MapEnv?

		val eq = arrayOf(mapEnvAnnot?.eq ?: "=")
		val separator = mapEnvAnnot?.separator ?: ";"

		val keyType = type.arguments[0].type ?: throw Throwable("Could not detect map key type")
		val valueType = type.arguments[1].type ?: throw Throwable("Could not detect map value type")

		val keyValuePairs = value.split(separator)
		val outputs = mutableMapOf<Any?, Any?>()

		for (n in keyValuePairs.indices) {
			val arr = keyValuePairs[n].split(delimiters = eq, ignoreCase = false, limit = 2)
			@Suppress("USELESS_ELVIS") val key = arr[0] ?: continue
			@Suppress("USELESS_ELVIS") val str = arr[1] ?: ""
			outputs[parseValue(keyType, "$name[$n#key]", key)] = parseValue(valueType, "$name[$key:$n#value]", str)
		}

		return outputs
	}
}
