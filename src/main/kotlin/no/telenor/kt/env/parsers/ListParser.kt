package no.telenor.kt.env.parsers

import no.telenor.kt.env.ListEnv
import no.telenor.kt.env.Parser
import no.telenor.kt.env.parseValue
import kotlin.reflect.KType

class ListParser : Parser {
	fun parseList(type: KType, name: String, value: String): List<Any?> {
		val arrayEnvAnnot: ListEnv? = type.annotations.find { it is ListEnv } as ListEnv?

		val separator = arrayEnvAnnot?.separator ?: ","
		// val regex = arrayEnvAnnot?.regex ?: false

		val itemType = type.arguments[0].type ?: throw Throwable("Could not detect array item type")
		// val items = if (regex) value.split(Regex(separator)) else value.split(separator)
		val items = value.split(separator)
		val outputs = mutableListOf<Any?>()

		for (n in items.indices) outputs.add(parseValue(itemType, "$name#$n", items[n]))

		return outputs
	}
}
