@file:Suppress("UNUSED_PARAMETER")

package no.telenor.kt.env.parsers

import no.telenor.kt.env.Parser
import kotlin.reflect.KType

class RegexParser : Parser {
	fun parseRegex(type: KType, name: String, value: String) = value.toRegex()
}
