@file:Suppress("UNUSED_PARAMETER")

package no.telenor.kt.env.parsers

import no.telenor.kt.env.Parser
import kotlin.reflect.KType

class StringParser : Parser {
	fun parseString(type: KType, name: String, value: String): String = value
}
