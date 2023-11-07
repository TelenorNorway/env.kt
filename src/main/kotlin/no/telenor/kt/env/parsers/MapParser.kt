package no.telenor.kt.env.parsers

import no.telenor.kt.env.Construct
import no.telenor.kt.env.MapEnv
import no.telenor.kt.env.Parser
import kotlin.reflect.KType

class MapParser : Parser {
	fun parseMap(type: KType, name: String, value: String): Map<Any?, Any?> {
		val mapEnvAnnot: MapEnv? = type.annotations.find { it is MapEnv } as? MapEnv

		val eq = mapEnvAnnot?.eq ?: "="
		val separator = mapEnvAnnot?.separator ?: ";"

		val eqSplit = if (mapEnvAnnot?.eqRegex == true) {
			createEqSplitRegex(Regex(eq))
		} else {
			createEqSplitStr(arrayOf(eq))
		}

		val separatorSplit = if (mapEnvAnnot?.separatorRegex == true) {
			createSeparatorSplitRegex(Regex(separator))
		} else {
			createSeparatorSplitStr(separator)
		}

		val keyType = type.arguments[0].type ?: throw Throwable("Could not detect map key type")
		val valueType = type.arguments[1].type ?: throw Throwable("Could not detect map value type")

		val keyValuePairs = separatorSplit(value)
		val outputs = mutableMapOf<Any?, Any?>()

		for (n in keyValuePairs.indices) {
			val arr = eqSplit(keyValuePairs[n])
			val key = arr.getOrNull(0) ?: continue
			val str = arr.getOrNull(1) ?: ""
			outputs[Construct.parseValue(keyType, "$name[$n#key]", key)] =
				Construct.parseValue(valueType, "$name[$key:$n#value]", str)
		}

		return outputs
	}
}

private fun createEqSplitRegex(regex: Regex): (String) -> List<String> = {
	it.split(regex, limit = 2)
}

private fun createEqSplitStr(delim: Array<String>): (String) -> List<String> = {
	it.split(delimiters = delim, ignoreCase = false, limit = 2)
}

private fun createSeparatorSplitRegex(regex: Regex): (String) -> List<String> = {
	it.split(regex)
}

private fun createSeparatorSplitStr(delim: String): (String) -> List<String> = {
	it.split(delim)
}
