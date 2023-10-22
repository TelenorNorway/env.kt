package no.telenor.kt.env.parsers

import no.telenor.kt.env.Parser
import kotlin.reflect.KType

class BooleanParser : Parser {
	fun parseBoolean(type: KType, name: String, value: String): Boolean {
		val v = value.trim().lowercase()
		return values[v] ?: throw Throwable("Could not convert $name to a Boolean, value '$v' is not a boolean token!");
	}

	companion object {
		val values = mutableMapOf(
			// truthy
			"true" to true,
			"y" to true,
			"yes" to true,
			"on" to true,
			"enable" to true,
			"enabled" to true,
			"active" to true,
			"activated" to true,

			// falsy
			"false" to false,
			"n" to false,
			"no" to false,
			"off" to false,
			"disable" to false,
			"disabled" to false,
			"inactive" to false,
			"unactivated" to false,
		);
	}
}
